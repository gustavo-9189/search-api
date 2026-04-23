package com.search_api.infrastructure.exception;

import com.search_api.domain.exception.SearchNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    @RestController
    static class ThrowingController {
        @GetMapping("/test/not-found")
        void notFound() { throw new SearchNotFoundException("abc-123"); }

        @GetMapping("/test/runtime")
        void runtime() { throw new RuntimeException("Conexión rechazada por Kafka"); }
    }

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(handler)
                .build();
    }

    @Test
    void handleValidationException_shouldReturn400() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new FieldError("searchRequest", "hotelId", "no debe estar en blanco")
        ));

        ProblemDetail result = handler.handleValidationException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getDetail()).contains("hotelId", "no debe estar en blanco");
    }

    @Test
    void handleValidationException_shouldConcatenateMultipleErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(List.of(
                new FieldError("searchRequest", "hotelId", "no debe estar en blanco"),
                new FieldError("searchRequest", "ages", "no debe estar vacío")
        ));

        ProblemDetail result = handler.handleValidationException(ex);

        assertThat(result.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(result.getDetail()).contains("hotelId").contains("ages");
    }

    @Test
    void handleSearchNotFound_shouldReturn404WithDetail() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No se encontró una búsqueda con id: abc-123"));
    }

    @Test
    void handleUnexpected_shouldReturn500WithGenericMessage() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("Ocurrió un error inesperado. Por favor, intente nuevamente más tarde."));
    }

    @Test
    void handleUnexpected_shouldNotExposeInternalDetails() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.trace").doesNotExist())
                .andExpect(jsonPath("$.exception").doesNotExist())
                .andExpect(jsonPath("$.message").doesNotExist());
    }
}