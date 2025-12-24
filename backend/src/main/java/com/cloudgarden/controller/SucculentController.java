package com.cloudgarden.controller;

import com.cloudgarden.dto.SucculentRequest;
import com.cloudgarden.dto.NameRequest;
import com.cloudgarden.dto.TypeRequest;
import com.cloudgarden.dto.SucculentResponse;
import com.cloudgarden.service.SucculentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/plants")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SucculentController {

    private final SucculentService succulentService;

    @GetMapping
    public ResponseEntity<List<SucculentResponse>> getAllPlants() {
        List<SucculentResponse> plants = succulentService.getAllSucculents();
        int responseTime=succulentService.calculateMedian(plants);
        succulentService.simulateResponseTime(responseTime);
        return ResponseEntity.ok(plants);
    }

    @PostMapping
    public ResponseEntity<SucculentResponse> plantNew(
            @Valid @RequestBody SucculentRequest request) {
        SucculentResponse response = succulentService.plantSucculent(request);
        succulentService.simulateResponseTime(50); //a new succulent's state is healthy by default, so this is its corresponding response time
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/water")
    public ResponseEntity<SucculentResponse> waterPlant(@PathVariable Long id) {
        SucculentResponse response = succulentService.waterSucculent(id);
        int responseTime=succulentService.getSucculentById(id).getResponseTimeMS();
        succulentService.simulateResponseTime(responseTime);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<SucculentResponse> changeName(
            @PathVariable Long id, @Valid @RequestBody NameRequest request) {
        SucculentResponse response = succulentService.updateName(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/type")
    public ResponseEntity<SucculentResponse> changeType(
            @PathVariable Long id, @Valid @RequestBody TypeRequest request) {
        SucculentResponse response = succulentService.updateType(id, request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucculentResponse> changeSucculent(
            @PathVariable Long id, @Valid @RequestBody SucculentRequest request) {
        SucculentResponse response = succulentService.updateSucculent(id, request);
        return ResponseEntity.ok(response);
    }
    
}
