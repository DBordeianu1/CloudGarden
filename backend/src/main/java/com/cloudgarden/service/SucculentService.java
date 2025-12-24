package com.cloudgarden.service;

import com.cloudgarden.dto.SucculentRequest;
import com.cloudgarden.dto.NameRequest;
import com.cloudgarden.dto.TypeRequest;
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
            succulent.setResponseTimeMS(3500);
        } else {
            succulent.setStatus(Succulent.Status.HEALTHY);
            succulent.setWaterLevel(100);
            succulent.setResponseTimeMS(50);
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
                continue; //a zombie's plant state always stays at 0
            }
            int currentWaterLevel = succulent.getWaterLevel();
            int newWaterLevel = Math.max(0, currentWaterLevel - WATER_DECAY_RATE);

            succulent.setWaterLevel(newWaterLevel);

            if (newWaterLevel == 0) {
                succulent.setStatus(Succulent.Status.DEAD);
                succulent.setResponseTimeMS(120);
                log.warn("Succulent {} has died", succulent.getName());
            } else if (newWaterLevel < WILTING_THRESHOLD) {
                succulent.setStatus(Succulent.Status.WILTING);
                succulent.setResponseTimeMS(850);
                log.warn("Succulent {} is wilting ({}% water)", succulent.getName(), newWaterLevel);
            } else {
                succulent.setStatus(Succulent.Status.HEALTHY);
                succulent.setResponseTimeMS(50);
            }

            succulentRepository.save(succulent);
        }

        log.debug("Water level update completed");
    }

    @Transactional(readOnly = true)
    public void simulateResponseTime(int milliseconds){
        try{
            Thread.sleep(milliseconds);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }

    @Transactional(readOnly = true)
    public int calculateMedian(List<SucculentResponse> succulents){
        if (succulents.isEmpty()){
            return 0;
        }
        List<Integer> responseTimes=succulents.stream()
                                        .map(SucculentResponse::getResponseTimeMS)
                                        .sorted().collect(Collectors.toList());
        int size=responseTimes.size();
        return responseTimes.get((size-1)/2);
    }

    @Transactional
    public SucculentResponse updateName(Long id, NameRequest request){
        Succulent succulent = succulentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Succulent not found with id: " + id));
        if (request.getName().trim().isEmpty()){
            log.info("Succulent name update failed for id {}. Name cannot be empty", id);
        }
        else if(request.getName().trim().equals(succulent.getName())){
            log.info("Succulent name update skipped for id {}. New name: {}, is identical to current name", 
            id, succulent.getName());
        }
        else{
            succulent.setName(request.getName().trim());
            log.info("Succulent name updated successfully");
        }
        Succulent updated = succulentRepository.save(succulent);
        return SucculentResponse.fromEntity(updated);
    }

    @Transactional
    public SucculentResponse updateType(Long id, TypeRequest request){
        Succulent succulent = succulentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Succulent not found with id: " + id));
        if (request.getType().trim().isEmpty()){
            log.info("Succulent type update failed for id {}. Type cannot be empty", id);
        }
        else if(request.getType().trim().equals(succulent.getType())){
            log.info("Succulent type update skipped for id {}. New type: {}, is identical to current type", 
            id, succulent.getType());
        }
        else{
            succulent.setType(request.getType().trim());
            log.info("Succulent type updated successfully");
        }
        Succulent updated = succulentRepository.save(succulent);
        return SucculentResponse.fromEntity(updated);
    }

    @Transactional
    public SucculentResponse updateSucculent(Long id, SucculentRequest request){
        Succulent succulent = succulentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Succulent not found with id: " + id));
        if (request.getType().trim().isEmpty() && request.getName().trim().isEmpty()){
            log.info("Succulent update failed for id {}. Name and type cannot be empty", id);
        }
        else if(request.getType().trim().isEmpty() && !request.getName().trim().isEmpty()){
            log.info("Succulent update failed for id {}. Type cannot be empty", id);
        }
        else if(!request.getType().trim().isEmpty() && request.getName().trim().isEmpty()){
            log.info("Succulent update failed for id {}. Name cannot be empty", id);
        }
        else if(request.getType().trim().equals(succulent.getType()) && request.getName().trim().equals(succulent.getName())){
            log.info("Succulent update skipped for id {}. New name: {}, is identical to current name and new type: {}, is identical to current type", 
            id, succulent.getName(), succulent.getType());
        }
        else{
            succulent.setName(request.getName().trim());
            succulent.setType(request.getType().trim());
            log.info("Succulent updated successfully");
        }
        Succulent updated = succulentRepository.save(succulent);
        return SucculentResponse.fromEntity(updated);
    }
}
