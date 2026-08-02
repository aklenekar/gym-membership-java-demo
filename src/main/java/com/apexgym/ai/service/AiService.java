package com.apexgym.ai.service;

import com.apexgym.ai.domain.AiPromptProvider;
import com.apexgym.ai.dto.*;
import com.apexgym.ai.dto.openrouter.OpenRouterMessage;
import com.apexgym.ai.dto.openrouter.OpenRouterResponse;
import com.apexgym.ai.infrastructure.strategy.AiStrategy;
import com.apexgym.booking.dto.ClassAttendance;
import com.apexgym.booking.persistence.BookingStatus;
import com.apexgym.booking.persistence.ClassBookingRepository;
import com.apexgym.booking.persistence.GymClass;
import com.apexgym.booking.service.GymClassService;
import com.apexgym.profile.dto.UserProfile;
import com.apexgym.profile.service.ProfileService;
import com.apexgym.tracking.persistence.ClassBooking;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiStrategy aiStrategy;
    private final ProfileService profileService;
    private final GymClassService gymClassService;
    private final ClassBookingRepository classBookingRepository;
    private final ChatHistoryService chatHistoryService;
    private final AgentToolExecutor toolExecutor;
    private final AiPromptProvider aiPromptProvider;

    public List<FitnessClass> getRecommendations(String email) throws Exception {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendations(
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.goals(),
                userProfile.level(),
                userProfile.availability()
        );
    }

    public List<ClassRecommendationDTO> getRecommendedClasses(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendedClasses(
                userProfile.goals(),
                userProfile.level(),
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.availability()
        );
    }

    public Flux<ClassRecommendationDTO> getRecommendedClassesStreamResponse(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendedClassesStream(
                userProfile.goals(),
                userProfile.level(),
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.availability()
        );
    }

    public List<WorkoutDayDto> generateWorkoutPlan(String email, WorkoutPlanRequest workoutPlanRequest) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        return aiStrategy.generateWorkoutPlan(userProfile.goals(), workoutPlanRequest.daysPerWeek(), workoutPlanRequest.experienceYears(), workoutPlanRequest.availableEquipment());
    }

    public String getNutritionPlan(String email, NutritionRequest nutritionRequest) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        return aiStrategy.getNutritionPlan(userProfile.goals(), nutritionRequest.weight(), nutritionRequest.age(), userProfile.level(), nutritionRequest.dietaryRestrictions());
    }

    public Flux<String> chatResponse(ChatRequest request) {
        String systemPrompt = "You are a helpful fitness assistant for ApexGym.";
        return aiStrategy.chatResponse(systemPrompt, request.message());
    }

    public Flux<String> chatResponse(String email, ChatRequest request) {
        String systemPrompt;
        List<GymClass> availableClasses = gymClassService.findUpcomingClasses(LocalDateTime.now());
        if (!email.equalsIgnoreCase("anonymousUser")) {
            UserProfile profile = profileService.getCurrentUser(email);
            List<ClassBooking> upcomingBookings = classBookingRepository
                    .findByUserIdAndStatusAndGymClass_ClassDateAfterOrderByGymClass_ClassDate(profile.id(), BookingStatus.BOOKED, LocalDateTime.now());
            systemPrompt = aiPromptProvider.getDynamicChatSystemPrompt(profile, upcomingBookings, availableClasses);
            chatHistoryService.saveUserMessage(email, request.message());
        } else {
            systemPrompt = aiPromptProvider.getDynamicChatSystemPrompt(null, null, Collections.emptyList(), availableClasses);
        }

        List<ChatMessageDTO> history = chatHistoryService.getRecentHistory(email, 10);

        // Accumulators for streaming response
        StringBuilder textResponseAccumulator = new StringBuilder();
        StringBuilder functionNameAccumulator = new StringBuilder();
        StringBuilder functionArgsAccumulator = new StringBuilder();

        return aiStrategy.chatResponseWithHistory(
                systemPrompt, history, request.message(), aiPromptProvider.getAgentTools()
        ).flatMap(openRouterResponse -> {
            if (openRouterResponse.choices() == null || openRouterResponse.choices().isEmpty()) {
                return Flux.empty();
            }

            OpenRouterMessage delta = openRouterResponse.choices().getFirst().delta();
            if (delta == null) return Flux.empty();

            // 1. Regular text chunk streaming
            if (delta.content() != null && !delta.content().isEmpty()) {
                textResponseAccumulator.append(delta.content());
                return Flux.just(delta.content());
            }

            // 2. Tool Call fragment stream accumulator
            if (delta.tool_calls() != null && !delta.tool_calls().isEmpty()) {
                var toolCall = delta.tool_calls().getFirst();
                if (toolCall.function() != null) {
                    if (toolCall.function().name() != null) {
                        functionNameAccumulator.append(toolCall.function().name());
                    }
                    if (toolCall.function().arguments() != null) {
                        functionArgsAccumulator.append(toolCall.function().arguments());
                    }
                }
            }

            return Flux.empty();
        }).concatWith(Flux.defer(() -> {
            // Executed once the initial LLM stream finishes
            String functionName = functionNameAccumulator.toString();
            String functionArgs = functionArgsAccumulator.toString();

            if (!functionName.isBlank()) {
                // Execute booking tool
                String result = toolExecutor.executeTool(email, functionName, functionArgs);
                String confirmationMessage = "\n\n" + result;

                textResponseAccumulator.append(confirmationMessage);
                return Flux.just(confirmationMessage);
            }

            return Flux.empty();
        })).doOnComplete(() -> {
            // Save complete assistant message to chat history
            if (!textResponseAccumulator.isEmpty() && !email.equalsIgnoreCase("anonymousUser")) {
                chatHistoryService.saveAssistantMessage(email, textResponseAccumulator.toString());
            }
        });
    }
}
