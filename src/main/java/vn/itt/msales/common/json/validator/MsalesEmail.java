/*
 * Copyright (C) 2015 The mSales Project
 *
 */
package vn.itt.msales.common.json.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.hibernate.validator.internal.constraintvalidators.EmailValidator;

/**
 *
 * @author ChinhNQ
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidator.class)
public @interface MsalesEmail {

    String message() default "{user.email.invalid}";

    Class<?>[] group() default {};

    Class<? extends Payload>[] payload() default {};
}
