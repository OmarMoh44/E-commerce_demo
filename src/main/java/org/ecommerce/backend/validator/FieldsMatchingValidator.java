package org.ecommerce.backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;

public class FieldsMatchingValidator implements ConstraintValidator<FieldsMatching, Object> {
    private String firstField;
    private String secondField;

    @Override
    public void initialize(FieldsMatching constraintAnnotation) {
        this.firstField = constraintAnnotation.firstField();
        this.secondField = constraintAnnotation.secondField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {        
        try {
            Field field1 = value.getClass().getDeclaredField(firstField);
            Field field2 = value.getClass().getDeclaredField(secondField);

            field1.setAccessible(true); // bypass private access
            field2.setAccessible(true);   // bypass private access

            Object value1 = field1.get(value);
            Object value2 = field2.get(value);
            
            if (value1 == null || value2 == null) {
                return true; // Null values are considered valid
            }
            return value1.equals(value2); // Check if values are equal
        } catch (Exception e) {
            return false; // Invalid if reflection fails
        }
    }
}
