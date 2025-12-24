package com.cloudgarden.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NameRequest {

    @NotBlank(message = "Name is required")
    private String name;
    
}
