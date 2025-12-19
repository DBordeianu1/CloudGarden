package com.cloudgarden.controller;

import com.cloudgarden.dto.SucculentRequest;
import com.cloudgarden.dto.SucculentResponse;
import com.cloudgarden.service.SucculentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SucculentController {

    private final SucculentService succulentService;

    @GetMapping
    public ResponseEntity<List<SucculentResponse>> getAllPlants() {
        List<SucculentResponse> plants = succulentService.getAllSucculents();
        return ResponseEntity.ok(plants);
    }

    @PostMapping
    public ResponseEntity<SucculentResponse> plantNew(
            @Valid @RequestBody SucculentRequest request) {
        SucculentResponse response = succulentService.plantSucculent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/water")
    public ResponseEntity<SucculentResponse> waterPlant(@PathVariable Long id) {
        SucculentResponse response = succulentService.waterSucculent(id);
        return ResponseEntity.ok(response);
    }
}
