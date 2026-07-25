package com.apexgym.ai.domain;

import com.apexgym.booking.persistence.GymClass;
import com.apexgym.profile.dto.UserProfile;
import com.apexgym.tracking.persistence.ClassBooking;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AiPromptProvider {

    // =========================================================================
    // Dynamic Role-Based Chat Prompts
    // =========================================================================

    /**
     * Primary entry point to get dynamic chat system prompt based on user role.
     */
    public String getDynamicChatSystemPrompt(String role, UserProfile profile, List<ClassBooking> upcomingBookings, List<GymClass> availableClasses) {
        String normalizedRole = (role != null) ? role.toUpperCase() : "GUEST";

        return switch (normalizedRole) {
            case "ADMIN" -> getAdminChatSystemPrompt();
            case "TRAINER" -> getTrainerChatSystemPrompt();
            case "USER" -> getUserChatSystemPrompt(profile, upcomingBookings, availableClasses);
            default -> getGuestChatSystemPrompt(availableClasses);
        };
    }

    /**
     * Overload for backward compatibility (defaults to USER role).
     */
    public String getDynamicChatSystemPrompt(UserProfile profile, List<ClassBooking> upcomingBookings, List<GymClass> availableClasses) {
        return getDynamicChatSystemPrompt(profile.role(), profile, upcomingBookings, availableClasses);
    }

    private String getUserChatSystemPrompt(UserProfile profile, List<ClassBooking> upcomingBookings, List<GymClass> availableClasses) {
        String name = (profile != null && profile.name() != null) ? profile.name() : "Member";
        String goals = (profile != null && profile.goals() != null) ? profile.goals() : "General Fitness";
        String level = (profile != null && profile.level() != null) ? profile.level() : "Intermediate";
        String availability = (profile != null && profile.availability() != null) ? profile.availability() : "Flexible";

        String bookingsFormatted = (upcomingBookings == null || upcomingBookings.isEmpty())
                ? "No upcoming bookings."
                : upcomingBookings.stream()
                .map(b -> "- " + b.getGymClass().getName() + " at " + b.getGymClass().getClassDate() + " with " + b.getGymClass().getInstructorName())
                .collect(Collectors.joining("\n"));

        String scheduleFormatted = getScheduleFormatted(availableClasses);

        return """
                You are ApexAI, an expert personal trainer and fitness assistant at APEX GYM.
                You can answer questions, book classes for users, and cancel existing bookings.
                Your goal is to assist members with personal fitness advice, schedule guidance, and motivation.
                
                ### MEMBER CONTEXT:
                - Name: %s
                - Goals: %s
                - Fitness Level: %s
                - Availability: %s
                
                ### UPCOMING BOOKINGS:
                %s
                
                ### UPCOMING GYM SCHEDULE (NEXT 30 DAYS):
                %s
                
                ### CONVERSATION INSTRUCTIONS:
                1. Use the provided MEMBER CONTEXT and UPCOMING SCHEDULE to give tailored, accurate recommendations.
                2. If recommending classes, reference real upcoming classes from the schedule above.
                3. Keep responses encouraging, concise, and structured with markdown bullet points when helpful.
                4. Never invent classes not listed in the schedule context.
                5. If the user explicitly asks to book a class, call the 'book_class' tool using the exact class ID from the schedule.
                6. To cancel a booking, call 'cancel_booking' using the booking ID from UPCOMING BOOKINGS.
                7. Do not fake or claim a class is booked or cancelled without calling the tool.
                """.formatted(
                name,
                goals,
                level,
                availability,
                bookingsFormatted,
                scheduleFormatted
        );
    }

    private String getGuestChatSystemPrompt(List<GymClass> availableClasses) {
        String scheduleFormatted = getScheduleFormatted(availableClasses);

        return """
                You are ApexAI, the public virtual concierge at APEX GYM.
                Your goal is to welcome visitors, answer questions about facilities, explain membership plans, and showcase upcoming classes.
                
                ### UPCOMING GYM SCHEDULE (NEXT 30 DAYS):
                %s
                
                ### CONVERSATION INSTRUCTIONS:
                1. Answer questions about APEX GYM facilities, opening hours, class offerings, and membership tiers.
                2. Maintain a warm, encouraging, and welcoming tone.
                3. If a guest wants to book a class, encourage them to sign up or log in to their APEX GYM account.
                4. Do NOT attempt to perform bookings or cancellations for guest users.
                """.formatted(scheduleFormatted);
    }

    private String getTrainerChatSystemPrompt() {
        return """
                You are ApexAI Trainer Co-Pilot, an expert fitness science assistant for APEX GYM coaches.
                Your goal is to assist trainers with workout program design, periodization models, exercise cues, and macro calculations.
                
                ### CONVERSATION INSTRUCTIONS:
                1. Provide evidence-based exercise science advice, injury regressions, progressions, and biomechanics cues.
                2. Structure responses clearly with actionable programming guidelines.
                3. Maintain a professional, technical, and collaborative tone tailored for fitness professionals.
                """;
    }

    private String getAdminChatSystemPrompt() {
        return """
                You are ApexAI Admin Assistant, an operational AI assistant for APEX GYM management staff.
                Your goal is to assist administrative staff with operational insights, schedule organization, member retention strategies, and communication templates.
                
                ### CONVERSATION INSTRUCTIONS:
                1. Help gym management draft communication templates, policy guidelines, and operational plans.
                2. Keep answers structured, executive, and direct.
                3. Maintain a formal, efficient, and business-focused tone.
                """;
    }

    // =========================================================================
    // Generator & Recommendation Prompts
    // =========================================================================

    public String getClassRecommendationSystemPrompt(List<GymClass> availableClasses) {
        String scheduleFormatted = getScheduleFormatted(availableClasses);
        return """
                You are a professional fitness advisor at APEX GYM.
                Analyze the user profile and recommend 5 gym classes.
                
                ### UPCOMING GYM SCHEDULE (NEXT 30 DAYS):
                %s
                
                ### CONVERSATION INSTRUCTIONS:
                1. Use the provided UPCOMING GYM SCHEDULE to give tailored, accurate recommendations.
                2. If recommending classes, reference real upcoming classes from the schedule above.
                
                Respond ONLY with a JSON array, no markdown, no extra text.
                Format:
                [
                  {
                    "className": "HIIT Bootcamp",
                    "reasoning": "Matches your high-intensity goals",
                    "benefits": ["Burns calories", "Builds endurance"],
                    "matchPercentage": 95
                  }
                ]
                """.formatted(scheduleFormatted);
    }

    public String getClassRecommendationUserPrompt(String goals, String level, List<String> history, String availability) {
        return """
                User Profile:
                - Goals: %s
                - Fitness Level: %s
                - Past Classes: %s
                - Preferred Time: %s
                
                Available Classes at APEX GYM:
                1. HIIT Bootcamp - High intensity interval training (60min)
                2. Yoga Flow - Flexibility and mindfulness (75min)
                3. Strength Training - Muscle building and conditioning (90min)
                4. Cycling Endurance - Cardio and stamina (45min)
                5. Boxing Fundamentals - Combat sports and conditioning (60min)
                6. Pilates Core - Core strength and stability (60min)
                7. CrossFit - Functional fitness (75min)
                8. Zumba Dance - Cardio through dance (50min)
                
                Recommend the best 5 classes.
                """.formatted(goals, level, history, availability);
    }

    public String getWorkoutPlanPrompt(String dayName, String goals, int experienceYears, List<String> equipment) {
        return """
                Generate a workout for %s ONLY.
                Goal: %s | Experience: %d yrs | Equipments: %s
                
                Format as JSON:
                {"day": "%s", "focus": "", "exercises": [{"name": "", "sets": 0, "reps": 0}], "duration": "", "rest": ""}
                
                Respond ONLY with raw JSON.
                """.formatted(dayName, goals, experienceYears, equipment, dayName);
    }

    public String getNutritionPlanPrompt(String goals, double weight, int age, String activityLevel, List<String> restrictions) {
        return """
                Create a nutrition plan for:
                Goal: %s | Weight: %.1f kg | Age: %d | Activity: %s | Restrictions: %s
                
                Response must follow this exact JSON structure:
                {"dailyCalorieTarget":0,"macroSplit":{"protein":0,"carbs":0,"fats":0},"mealTimingRecommendations":{},"sampleMeals":[{"meal":"","food":""}],"supplementSuggestions":[]}
                
                Respond ONLY with raw JSON.
                """.formatted(goals, weight, age, activityLevel, restrictions);
    }

    public String getGeneralRecommendationPrompt(List<String> history, String goals, String level, String availability) {
        return """
                Act as a professional fitness coordinator. Based on the user data provided below, recommend 5 fitness classes.
                
                ### User Data:
                - Past classes: %s
                - Goals: %s
                - Fitness level: %s
                - Available times: %s
                
                ### Instructions:
                Return the recommendations strictly as a JSON object containing a list named "recommendations".\s
                Each object in the list must follow this schema:
                {
                  "name": "Class Name",
                  "reasoning": "A concise explanation of why this fits the user's goals, level, and schedule."
                }
                
                Do not include any introductory text, markdown formatting (like ```json), or follow-up remarks. Return only the raw JSON.
                """.formatted(history, goals, level, availability);
    }

    // =========================================================================
    // Helpers & Agent Tools
    // =========================================================================

    private static @NonNull String getScheduleFormatted(List<GymClass> availableClasses) {
        if (availableClasses == null || availableClasses.isEmpty()) {
            return "No upcoming classes currently scheduled.";
        }
        return availableClasses.stream()
                .limit(10)
                .map(c -> "- ID: " + c.getId() + " | " + c.getName() + " (" + c.getCategory() + ") on " + c.getClassDate() + " with " + c.getInstructorName())
                .collect(Collectors.joining("\n"));
    }

    /**
     * Role-aware tool retriever. Only signed-in USER roles get booking function calls.
     */
    public List<Map<String, Object>> getAgentTools(String role) {
        if (role != null && role.equalsIgnoreCase("USER")) {
            return getAgentTools();
        }
        return List.of(); // Return empty tool definitions for GUEST, TRAINER, and ADMIN
    }

    public List<Map<String, Object>> getAgentTools() {
        return List.of(
                Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", "book_class",
                                "description", "Book a gym class for the user by providing the class ID.",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "classId", Map.of(
                                                        "type", "integer",
                                                        "description", "The ID of the gym class to book."
                                                )
                                        ),
                                        "required", List.of("classId")
                                )
                        )
                ),
                Map.of(
                        "type", "function",
                        "function", Map.of(
                                "name", "cancel_booking",
                                "description", "Cancel an existing gym class booking for the user using the booking ID or class ID.",
                                "parameters", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "bookingId", Map.of(
                                                        "type", "integer",
                                                        "description", "The ID of the booking or class to cancel."
                                                )
                                        ),
                                        "required", List.of("bookingId")
                                )
                        )
                )
        );
    }
}