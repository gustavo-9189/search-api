package com.search_api.infrastructure.adapter.in.rest;

import com.search_api.domain.model.SearchCount;
import com.search_api.domain.port.in.CountSearchUseCase;
import com.search_api.domain.port.in.CreateSearchUseCase;
import com.search_api.infrastructure.adapter.in.rest.dto.CountSearchResponse;
import com.search_api.infrastructure.adapter.in.rest.dto.CreateSearchResponse;
import com.search_api.infrastructure.adapter.in.rest.dto.SearchRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Search", description = "Operaciones de búsqueda de disponibilidad hotelera")
public class SearchController {

    private final CreateSearchUseCase createSearchUseCase;
    private final CountSearchUseCase countSearchUseCase;

    public SearchController(CreateSearchUseCase createSearchUseCase,
                            CountSearchUseCase countSearchUseCase) {
        this.createSearchUseCase = createSearchUseCase;
        this.countSearchUseCase = countSearchUseCase;
    }

    @Operation(
            summary = "Crear una búsqueda",
            description = "Registra una búsqueda de disponibilidad hotelera. Devuelve el searchId de inmediato; la persistencia es asíncrona vía Kafka.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Búsqueda aceptada",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CreateSearchResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Solicitud inválida",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @PostMapping("/search")
    public ResponseEntity<CreateSearchResponse> search(@Valid @RequestBody SearchRequest request) {
        String searchId = createSearchUseCase.create(
                request.hotelId(),
                request.checkIn(),
                request.checkOut(),
                request.ages()
        );
        return ResponseEntity.ok(new CreateSearchResponse(searchId));
    }

    @Operation(
            summary = "Contar búsquedas",
            description = "Devuelve cuántas veces se realizó la misma búsqueda (mismo hotelId, checkIn, checkOut y ages en el mismo orden).",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Resultado del conteo",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = CountSearchResponse.class))),
                    @ApiResponse(responseCode = "404", description = "searchId no encontrado",
                            content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                    schema = @Schema(implementation = ProblemDetail.class)))
            }
    )
    @GetMapping("/count")
    public ResponseEntity<CountSearchResponse> count(
            @Parameter(description = "El searchId devuelto por POST /search", required = true)
            @RequestParam String searchId) {
        SearchCount result = countSearchUseCase.count(searchId);
        CountSearchResponse response = new CountSearchResponse(
                result.searchId(),
                new CountSearchResponse.SearchData(
                        result.search().hotelId(),
                        result.search().checkIn(),
                        result.search().checkOut(),
                        result.search().ages()
                ),
                result.count()
        );
        return ResponseEntity.ok(response);
    }
}