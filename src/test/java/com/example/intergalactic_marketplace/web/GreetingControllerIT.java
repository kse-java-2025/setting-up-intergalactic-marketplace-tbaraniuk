package com.example.intergalactic_marketplace.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static com.example.intergalactic_marketplace.web.exception.GreetingNotFoundException.GREETING_NOT_FOUND_MESSAGE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "greeting.greetings.bob.name=Bob",
        "greeting.greetings.bob.message=Hello Bob!"
})
@AutoConfigureMockMvc
@DisplayName("GreetingController Integration Tests")
@Tag("greeting")
public class GreetingControllerIT extends AbstractIT {
    private static final String GREETING_VALID_NAME = "bob";
    private static final String GREETING_VALID_MESSAGE = "Hello Bob!";

    private static final String GREETING_INVALID_NAME = "invalid-name";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @SneakyThrows
    void testGreeting() {
        mockMvc.perform(get("/api/v1/greetings")
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(result -> result.getResponse().getContentAsString().contains("Hello"));
    }

    @Test
    @SneakyThrows
    void testGreetingWithValidName() {
        mockMvc.perform(get("/api/v1/greetings/{name}", GREETING_VALID_NAME)
            .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(GREETING_VALID_MESSAGE));
    }

    @Test
    @SneakyThrows
    void testGreetingWithInvalidName() {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(NOT_FOUND, String.format(GREETING_NOT_FOUND_MESSAGE, GREETING_INVALID_NAME));
        problemDetail.setType(URI.create("greeting-not-found"));
        problemDetail.setTitle("Greeting Not Found");

        mockMvc.perform(get("/api/v1/greetings/{name}", GREETING_INVALID_NAME)
                .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound())
                .andExpect(content().json(objectMapper.writeValueAsString(problemDetail)));
    }
}
