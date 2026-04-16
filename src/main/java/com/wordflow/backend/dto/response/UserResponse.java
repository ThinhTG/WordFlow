package com.wordflow.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String email;
    private String level;
    private long totalLearnedWords;
    private int streak;
}