package com.search_api.infrastructure.adapter.in.rest.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SearchDatesValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSearchDates {
    String message() default "El checkOut debe ser posterior al checkIn";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
