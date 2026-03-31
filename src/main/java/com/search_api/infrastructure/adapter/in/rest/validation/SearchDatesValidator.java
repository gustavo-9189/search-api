package com.search_api.infrastructure.adapter.in.rest.validation;

import com.search_api.infrastructure.adapter.in.rest.dto.SearchRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SearchDatesValidator implements ConstraintValidator<ValidSearchDates, SearchRequest> {

    @Override
    public boolean isValid(SearchRequest request, ConstraintValidatorContext context) {
        if (request.checkIn() == null || request.checkOut() == null) {
            return true; // manejado por @NotNull en los campos
        }
        return request.checkOut().isAfter(request.checkIn());
    }

}
