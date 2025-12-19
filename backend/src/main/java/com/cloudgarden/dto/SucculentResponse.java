package com.cloudgarden.dto;

import com.cloudgarden.model.Succulent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SucculentResponse {

    private Long id;
    private String name;
    private String type;
    private Integer waterLevel;
    private String status;

    public static SucculentResponse fromEntity(Succulent succulent) {
        return new SucculentResponse(
            succulent.getId(),
            succulent.getName(),
            succulent.getType(),
            succulent.getWaterLevel(),
            succulent.getStatus().name()
        );
    }
}
