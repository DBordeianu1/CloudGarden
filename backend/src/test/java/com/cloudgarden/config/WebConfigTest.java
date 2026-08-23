package com.cloudgarden.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Covers what WebConfig actually registers on the CorsRegistry. The registry starts
 * every mapping from CorsConfiguration.applyPermitDefaultValues(), so the tests below
 * pin down both what WebConfig sets and what it leaves at the permissive default.
 */
class WebConfigTest {

    private static final String API_MAPPING = "/api/**";

    private Map<String, CorsConfiguration> corsConfigurations;

    /**
     * CorsRegistry only exposes its accumulated configuration to Spring's own
     * infrastructure, so a subclass is the only way to read it back in a unit test.
     */
    private static class ExposedCorsRegistry extends CorsRegistry {
        @Override
        public Map<String, CorsConfiguration> getCorsConfigurations() {
            return super.getCorsConfigurations();
        }
    }

    @BeforeEach
    void registerMappings() {
        ExposedCorsRegistry registry = new ExposedCorsRegistry();

        new WebConfig().addCorsMappings(registry);

        corsConfigurations = registry.getCorsConfigurations();
    }

    @Test
    void shouldRegisterExactlyOneMappingScopedToTheApi() {
        //the h2 console and any future non-/api endpoint must stay outside the CORS
        //configuration, so the mapping set is asserted exactly, not just for containment
        assertThat(corsConfigurations).containsOnlyKeys(API_MAPPING);
    }

    @Test
    void shouldAllowAnyOrigin() {
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getAllowedOrigins()).containsExactly("*");
        assertThat(config.checkOrigin("https://cloudgarden.example.com")).isEqualTo("*");
    }

    @Test
    void shouldNotAllowCredentials() {
        //allowedOrigins("*") together with allowCredentials(true) is an illegal
        //combination that Spring rejects at request time, not at startup
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getAllowCredentials()).isNull();
    }

    @Test
    void shouldAllowTheMethodsTheApiExposes() {
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getAllowedMethods())
                .containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS");
    }

    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST", "PUT", "DELETE", "OPTIONS"})
    void shouldAcceptEveryMethodTheControllerHandles(String method) {
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.checkHttpMethod(HttpMethod.valueOf(method))).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {"PATCH", "HEAD", "TRACE"})
    void shouldRejectMethodsOutsideTheConfiguredSet(String method) {
        //explicitly listing the methods narrows the permit-default set (GET, HEAD, POST),
        //so HEAD is dropped as a side effect: worth knowing before a client relies on it
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.checkHttpMethod(HttpMethod.valueOf(method))).isNull();
    }

    @Test
    void shouldAllowAnyRequestHeader() {
        //the frontend sends Content-Type: application/json on POST and PUT, which makes
        //those requests preflighted, so the header has to survive checkHeaders
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getAllowedHeaders()).containsExactly("*");
        assertThat(config.checkHeaders(List.of("Content-Type", "X-Requested-With")))
                .containsExactly("Content-Type", "X-Requested-With");
    }

    @Test
    void shouldKeepTheDefaultPreflightCacheDuration() {
        //WebConfig never calls maxAge(), so the registry default applies
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getMaxAge()).isEqualTo(1800L);
    }

    @Test
    void shouldNotExposeAnyResponseHeaders() {
        CorsConfiguration config = corsConfigurations.get(API_MAPPING);

        assertThat(config.getExposedHeaders()).isNullOrEmpty();
    }
}
