package com.apexgym.ai.service;

import com.apexgym.booking.service.ClassBookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentToolExecutor {

    private final ClassBookingService classBookingService;
    private final ObjectMapper objectMapper;

    public String executeTool(String userEmail, String functionName, String argumentsJson) {
        log.info("Executing tool '{}' for user: {}", functionName, userEmail);
        try {
            if ("book_class".equals(functionName)) {
                Map<?, ?> args = objectMapper.readValue(argumentsJson, Map.class);
                Long classId = Long.valueOf(args.get("classId").toString());

                classBookingService.bookClass(userEmail, classId);
                return "SUCCESS: Class ID " + classId + " has been successfully booked for " + userEmail;
            }

            if ("cancel_booking".equals(functionName)) {
                Map<?, ?> args = objectMapper.readValue(argumentsJson, Map.class);
                Long bookingId = Long.valueOf(args.get("bookingId").toString());

                // Execute domain cancel logic
                classBookingService.cancelBooking(userEmail, bookingId);
                return "SUCCESS: Booking ID " + bookingId + " has been successfully canceled.";
            }
        } catch (Exception e) {
            log.error("Failed to execute tool call", e);
            return "ERROR: Operation failed - " + e.getMessage();
        }
        return "ERROR: Unknown function call: " + functionName;
    }
}