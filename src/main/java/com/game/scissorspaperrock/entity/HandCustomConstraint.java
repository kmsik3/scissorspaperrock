package com.game.scissorspaperrock.entity;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = HandCustomValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandCustomConstraint {
    String message() default "Invalid Hand pick";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

