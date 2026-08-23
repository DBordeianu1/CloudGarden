package com.cloudgarden.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Drives real requests through the MVC infrastructure to prove WebConfig is picked up as
 * a WebMvcConfigurer and that its registration reaches the CORS processor.
 *
 * A dedicated probe controller is used instead of SucculentController because that one
 * carries its own @CrossOrigin(origins = "*"), which Spring combines with the global
 * configuration: against it these assertions would still pass with WebConfig deleted.
 */
@WebMvcTest(controllers = WebConfigCorsIntegrationTest.CorsProbeController.class)
@Import(WebConfigCorsIntegrationTest.CorsProbeController.class) //nested classes are not component-scanned
class WebConfigCorsIntegrationTest {

    private static final String ORIGIN = "https://cloudgarden.example.com";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAcceptPreflightForApiPath() throws Exception {
        mockMvc.perform(options("/api/probe")
                        .header(HttpHeaders.ORIGIN, ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    @Test
    void shouldAdvertiseEveryConfiguredMethodOnPreflight() throws Exception {
        mockMvc.perform(options("/api/probe")
                        .header(HttpHeaders.ORIGIN, ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "DELETE"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS,
                        "GET,POST,PUT,DELETE,OPTIONS"));
    }

    @Test
    void shouldEchoRequestedHeadersOnPreflight() throws Exception {
        //a JSON body is what makes the frontend's POST and PUT calls preflighted at all
        mockMvc.perform(options("/api/probe")
                        .header(HttpHeaders.ORIGIN, ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PUT")
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type"));
    }

    @Test
    void shouldRejectPreflightForMethodOutsideTheConfiguredSet() throws Exception {
        //the probe controller does handle PATCH, so a rejection here can only come from
        //the allowedMethods list rather than from routing
        mockMvc.perform(options("/api/probe")
                        .header(HttpHeaders.ORIGIN, ORIGIN)
                        .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "PATCH"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @Test
    void shouldTagActualCrossOriginResponseForApiPath() throws Exception {
        mockMvc.perform(get("/api/probe").header(HttpHeaders.ORIGIN, ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*"));
    }

    @Test
    void shouldLeaveNonApiPathsWithoutCorsHeaders() throws Exception {
        //the mapping is "/api/**": anything served outside it, such as the h2 console,
        //must stay same-origin only
        mockMvc.perform(get("/probe").header(HttpHeaders.ORIGIN, ORIGIN))
                .andExpect(status().isOk())
                .andExpect(header().doesNotExist(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    @RestController
    static class CorsProbeController {

        @GetMapping("/api/probe")
        String apiGet() {
            return "ok";
        }

        @PostMapping("/api/probe")
        String apiPost() {
            return "ok";
        }

        @PutMapping("/api/probe")
        String apiPut() {
            return "ok";
        }

        @DeleteMapping("/api/probe")
        String apiDelete() {
            return "ok";
        }

        @PatchMapping("/api/probe")
        String apiPatch() {
            return "ok";
        }

        @GetMapping("/probe")
        String outsideApi() {
            return "ok";
        }
    }
}
