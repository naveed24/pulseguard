package com.pulseguard.pulseguard.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MonitorRequest {

    @NotBlank(message = "Monitor name is required")
    @Size(max = 100, message = "Monitor name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "URL is required")
    @Size(max = 2048, message = "URL cannot exceed 2048 characters")
    private String url;

    @Min(value = 100, message = "Expected status code must be at least 100")
    @Max(value = 599, message = "Expected status code cannot exceed 599")
    private Integer expectedStatusCode;

    @Min(value = 10, message = "Check interval must be at least 10 seconds")
    @Max(value = 86400, message = "Check interval cannot exceed 86400 seconds")
    private Integer checkIntervalSeconds;

    private Boolean active;
}
