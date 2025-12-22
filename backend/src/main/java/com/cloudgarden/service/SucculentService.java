package com.cloudgarden.service;

import com.cloudgarden.dto.SucculentRequest;
import com.cloudgarden.dto.SucculentResponse;
import com.cloudgarden.model.Succulent;
import com.cloudgarden.repository.SucculentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SucculentService {

    private final SucculentRepository succulentRepository;

    private static final int WATER_DECAY_RATE = 5;
    private static final int WILTING_THRESHOLD = 20;

    @Transactional
    public SucculentResponse plantSucculent(SucculentRequest request) {
        log.info("Planting new succulent: {}", request.getName());

        Succulent succulent = new Succulent();
        succulent.setName(request.getName());
        succulent.setType(request.getType());
        succulent.setStatus(Succulent.Status.HEALTHY);
        succulent.setWaterLevel(100);

        Succulent saved = succulentRepository.save(succulent);
        log.info("Succulent planted successfully with ID: {}", saved.getId());

        return SucculentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<SucculentResponse> getAllSucculents() {
        return succulentRepository.findAll().stream()
            .map(SucculentResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SucculentResponse getSucculentById(Long id) {
        Succulent succulent = succulentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Succulent not found with id: " + id));
        return SucculentResponse.fromEntity(succulent);
    }

    @Transactional
    public SucculentResponse waterSucculent(Long id) {
        log.info("Watering succulent with ID: {}", id);

        Succulent succulent = succulentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Succulent not found with id: " + id));

        if (succulent.getStatus() == Succulent.Status.DEAD || succulent.getStatus() == Succulent.Status.ZOMBIE) {
            //throw new RuntimeException("Cannot water a dead succulent");
            //succulent can now be watered but will become a zombie
            succulent.setStatus(Succulent.Status.ZOMBIE);
            //water level is already at 0 and remains at 0
        } else {
            succulent.setStatus(Succulent.Status.HEALTHY);
            succulent.setWaterLevel(100);
        }

        Succulent updated = succulentRepository.save(succulent);
        log.info("Succulent watered successfully. Water level restored to 100");

        return SucculentResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteSucculent(Long id) {
        log.info("Removing succulent with ID: {}", id);

        if (!succulentRepository.existsById(id)) {
            throw new RuntimeException("Succulent not found with id: " + id);
        }

        succulentRepository.deleteById(id);
        log.info("Succulent removed successfully");
    }

    @Transactional
    public void updateSucculentWaterLevels() {
        log.debug("Updating water levels for all succulents");

        List<Succulent> aliveSucculents = succulentRepository
            .findByStatusNot(Succulent.Status.DEAD);

        for (Succulent succulent : aliveSucculents) {
            if (succulent.getStatus()==Succulent.Status.ZOMBIE){
                continue; //a zombie's plant state always remains at 0
            }
            int currentWaterLevel = succulent.getWaterLevel();
            int newWaterLevel = Math.max(0, currentWaterLevel - WATER_DECAY_RATE);

            succulent.setWaterLevel(newWaterLevel);

            if (newWaterLevel == 0) {
                succulent.setStatus(Succulent.Status.DEAD);
                log.warn("Succulent {} has died", succulent.getName());
            } else if (newWaterLevel < WILTING_THRESHOLD) {
                succulent.setStatus(Succulent.Status.WILTING);
                log.warn("Succulent {} is wilting ({}% water)", succulent.getName(), newWaterLevel);
            } else {
                succulent.setStatus(Succulent.Status.HEALTHY);
            }

            succulentRepository.save(succulent);
        }

        log.debug("Water level update completed");
    }
}
