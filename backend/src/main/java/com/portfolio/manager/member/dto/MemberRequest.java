package com.portfolio.manager.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRequest(
    @NotBlank(message = "name is required")
    @Size(max = 150, message = "name must have at most 150 characters")
    String name,

    @NotBlank(message = "assignment is required")
    @Size(max = 80, message = "assignment must have at most 80 characters")
    String assignment
) {
}
