package com.search_api.infrastructure.adapter.in.rest.validation;

import com.search_api.infrastructure.adapter.in.rest.dto.SearchRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SearchDatesValidatorTest {

    private static final LocalDate TODAY     = LocalDate.now();
    private static final List<Integer> AGES  = List.of(30);

    private final SearchDatesValidator validator = new SearchDatesValidator();
    private final ConstraintValidatorContext ctx  = mock(ConstraintValidatorContext.class);

    @Test
    void isValid_shouldReturnTrue_whenCheckInIsNull() {
        SearchRequest request = new SearchRequest("hotel1", null, TODAY.plusDays(1), AGES);

        assertThat(validator.isValid(request, ctx)).isTrue();
    }

    @Test
    void isValid_shouldReturnTrue_whenCheckOutIsNull() {
        SearchRequest request = new SearchRequest("hotel1", TODAY, null, AGES);

        assertThat(validator.isValid(request, ctx)).isTrue();
    }

    @Test
    void isValid_shouldReturnTrue_whenCheckOutIsAfterCheckIn() {
        SearchRequest request = new SearchRequest("hotel1", TODAY, TODAY.plusDays(1), AGES);

        assertThat(validator.isValid(request, ctx)).isTrue();
    }

    @Test
    void isValid_shouldReturnFalse_whenCheckOutEqualsCheckIn() {
        SearchRequest request = new SearchRequest("hotel1", TODAY, TODAY, AGES);

        assertThat(validator.isValid(request, ctx)).isFalse();
    }

    @Test
    void isValid_shouldReturnFalse_whenCheckOutIsBeforeCheckIn() {
        SearchRequest request = new SearchRequest("hotel1", TODAY.plusDays(5), TODAY, AGES);

        assertThat(validator.isValid(request, ctx)).isFalse();
    }
}