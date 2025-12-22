package com.cloudgarden.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//import java.time.LocalDateTime;

@Entity
@Table(name = "succulents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Succulent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Integer waterLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = Status.HEALTHY;
        }
        if (waterLevel == null) {
            waterLevel = 100;
        }
    }

    public enum Status {
        HEALTHY,
        WILTING,
        DEAD,
        ZOMBIE
    }
}
