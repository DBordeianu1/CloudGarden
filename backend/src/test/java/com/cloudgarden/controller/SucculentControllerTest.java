package com.cloudgarden.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cloudgarden.service.SucculentService;
import com.cloudgarden.dto.SucculentResponse;
import com.cloudgarden.dto.SucculentRequest;
import com.cloudgarden.dto.NameRequest;
import com.cloudgarden.dto.TypeRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(SucculentController.class)
class SucculentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SucculentService succulentService;
    
    @Test
    void shouldReturnEmptyListWhenNoSucculents() throws Exception{
        when(succulentService.getAllSucculents()).thenReturn(Collections.emptyList());
        when(succulentService.calculateMedian(null)).thenReturn(0);

        mockMvc.perform(get("/api/plants"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldNotReturnEmptyListWhenSucculents() throws Exception{
        SucculentResponse succulent1=new SucculentResponse(1L , "Snoopy", "Echeveria", 100, "HEALTHY", 50);
        SucculentResponse succulent2=new SucculentResponse(2L , "Woodstock", "Jade Plant", 100, "HEALTHY", 50);

        List<SucculentResponse> succulents=Arrays.asList(succulent1, succulent2);

        when(succulentService.getAllSucculents()).thenReturn(succulents);
        when(succulentService.calculateMedian(succulents)).thenReturn(50);

        mockMvc.perform(get("/api/plants"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$").isArray())
               .andExpect(jsonPath("$").isNotEmpty())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[0].name").value("Snoopy"))
               .andExpect(jsonPath("$[0].type").value("Echeveria"))
               .andExpect(jsonPath("$[0].waterLevel").value(100))
               .andExpect(jsonPath("$[0].status").value("HEALTHY"))
               .andExpect(jsonPath("$[0].responseTimeMS").value(50))
               .andExpect(jsonPath("$[1].id").value(2L))
               .andExpect(jsonPath("$[1].name").value("Woodstock"))
               .andExpect(jsonPath("$[1].type").value("Jade Plant"))
               .andExpect(jsonPath("$[1].waterLevel").value(100))
               .andExpect(jsonPath("$[1].status").value("HEALTHY"))
               .andExpect(jsonPath("$[1].responseTimeMS").value(50));
    }   

    @Test
    void shouldPlantSucculent() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Charlie Brown", "Aloe", 100, "HEALTHY", 50);

        when(succulentService.plantSucculent(any(SucculentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/plants")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"name\":\"Charlie Brown\",\"type\":\"Aloe\"}"))
               .andExpect(status().isCreated())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.name").value("Charlie Brown"))
               .andExpect(jsonPath("$.type").value("Aloe"))
               .andExpect(jsonPath("$.waterLevel").value(100))
               .andExpect(jsonPath("$.status").value("HEALTHY"));
    }

    @Test
    void shouldWaterSucculent() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Snoopy", "Echeveria", 100, "HEALTHY", 50);

        when(succulentService.waterSucculent(1L)).thenReturn(response);
        when(succulentService.getSucculentById(1L)).thenReturn(response);

        mockMvc.perform(post("/api/plants/1/water"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.name").value("Snoopy"))
               .andExpect(jsonPath("$.waterLevel").value(100));
    }

    @Test
    void shouldChangeName() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Linus", "Echeveria", 100, "HEALTHY", 50);

        when(succulentService.updateName(eq(1L), any(NameRequest.class))).thenReturn(response);
        when(succulentService.getSucculentById(1L)).thenReturn(response);

        mockMvc.perform(put("/api/plants/1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Linus\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.name").value("Linus"));
    }

    @Test
    void shouldChangeType() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Snoopy", "Cactus", 100, "HEALTHY", 50);

        when(succulentService.updateType(eq(1L), any(TypeRequest.class))).thenReturn(response);
        when(succulentService.getSucculentById(1L)).thenReturn(response);

        mockMvc.perform(put("/api/plants/1/type")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"type\":\"Cactus\"}"))
               .andExpect(status().isOk())
               .andExpect(content().contentType("application/json"))
               .andExpect(jsonPath("$.type").value("Cactus"));
    }

    @Test
    void shouldChangeNameAndType() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Lucy", "Jade Plant", 100, "HEALTHY", 50);

        when(succulentService.updateSucculent(eq(1L), any(SucculentRequest.class))).thenReturn(response);
        when(succulentService.getSucculentById(1L)).thenReturn(response);

        mockMvc.perform(put("/api/plants/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Lucy\",\"type\":\"Jade Plant\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.name").value("Lucy"))
                .andExpect(jsonPath("$.type").value("Jade Plant"));
    }

    @Test
    void shouldDeleteSucculentWhenExisting() throws Exception {
        SucculentResponse response = new SucculentResponse(1L, "Snoopy", "Echeveria", 100, "HEALTHY", 50);

        when(succulentService.getSucculentById(1L)).thenReturn(response);

        mockMvc.perform(delete("/api/plants/1"))
               .andExpect(status().isNoContent());
    }
}
