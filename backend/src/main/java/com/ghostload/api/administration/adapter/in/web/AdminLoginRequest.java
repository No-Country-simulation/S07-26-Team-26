package com.ghostload.api.administration.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminLoginRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 128) String password) {
}
