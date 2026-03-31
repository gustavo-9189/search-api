package com.search_api.infrastructure.adapter.in.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.search_api.infrastructure.adapter.in.rest.validation.ValidSearchDates;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

@ValidSearchDates
public record SearchRequest(
        @NotBlank(message = "El hotelId es obligatorio")
        String hotelId,

        @NotNull(message = "El checkIn es obligatorio")
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy", example = "01/01/2026")
        LocalDate checkIn,

        @NotNull(message = "El checkOut es obligatorio")
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(type = "string", pattern = "dd/MM/yyyy", example = "10/01/2026")
        LocalDate checkOut,

        @NotNull(message = "El campo ages es obligatorio")
        @NotEmpty(message = "El campo ages no puede estar vacío")
        @ArraySchema(schema = @Schema(type = "integer", example = "10"),
                     arraySchema = @Schema(example = "[10,18,20,50,51]"))
        List<@NotNull Integer> ages
) {
    public SearchRequest {
        if (ages != null) ages = List.copyOf(ages);
    }
}