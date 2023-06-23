package com.game.scissorspaperrock.entity;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.List;

public class HandCustomValidator implements
        ConstraintValidator<HandCustomConstraint, String> {
    @Override
    public void initialize(HandCustomConstraint constraintAnnotation) {

    }

    @Override
    public boolean isValid(String handShape, ConstraintValidatorContext cxt) {
        List<String> handEnumCheck = new ArrayList<>();
        handEnumCheck.add("SCISSORS");
        handEnumCheck.add("PAPER");
        handEnumCheck.add("ROCK");
        if (handEnumCheck.stream().noneMatch(handShape::equalsIgnoreCase))
            return false;
        return true;
    }
}
