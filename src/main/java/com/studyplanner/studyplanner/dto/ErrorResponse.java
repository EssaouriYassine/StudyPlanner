package com.studyplanner.studyplanner.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Map<String, String> errors,
        LocalDateTime timestamp
) {
}
