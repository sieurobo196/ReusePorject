/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common.json;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import vn.itt.msales.common.json.validator.MsalesValidationError;

/**
 * @author ChinhNQ
 * @version
 * @since 1 Jun 2015 18:02:17
 * msales#com.itt.msales.utils.converter.json.MsalesRestErrorHandler.java
 *
 *Data transfer objects which contains the information returned to the user of our REST API
 */

public class MsalesRestErrorHandler {
    
    /* The message source is used to fetch localized error message for validation errors. */
    private MessageSource mMessageSource;
    
    /**
     * Inject the MessageSource bean by using constructor injection
     */
    @Autowired
    public MsalesRestErrorHandler(MessageSource messageSource) {
        this.mMessageSource = messageSource;
    }
    
    
    /**
     * This method returns {@link MsalesValidationError} object and takes 
     * a {@link MethodArgumentNotValidException} object as a method parameter.
     * 
     * Annotate the method with the @ExceptionHandler annotation and ensure that 
     * the method is called when the MethodArgumentNotValidException is thrown.
     * 
     * Annotate the method with the @MsaleResponseStatus annotation and ensure 
     * that the HTTP status code 400 (bad request) is returned.
     * 
     * Annotate the method with the @ResponseBody annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public MsalesValidationError processValidationError(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        // Get a list of FieldError objects and process them.
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        return processFieldErrors(fieldErrors);
        
    }
    
    /**
     * Process the field errors one field error at the time.
     * Return the created ValidationErrorDTO object after each field error has been processed.
     */
    private MsalesValidationError processFieldErrors(List<FieldError> fieldErrors) {
        MsalesValidationError dto = new MsalesValidationError();
 
        for (FieldError fieldError: fieldErrors) {
            String localizedErrorMessage = resolveLocalizedErrorMessage(fieldError);
            //Add a new field error by calling the addFieldError() method of the ValidationErrorDTO class. 
            //Pass the name of the field and the resolved error message as method parameters.
            dto.addFieldError(fieldError.getField(), localizedErrorMessage);
        }
 
        return dto;
    }
    
    /**
     * Try to resolve a localized error message by calling the getMessage() method of the MessageSource interface, 
     * and pass the processed field error and the current locale as method parameters.
     * Return the resolved error message. If the error message is not found from the properties file,
     * return the most accurate field error code.
     */
    private String resolveLocalizedErrorMessage(FieldError fieldError) {
        Locale currentLocale =  LocaleContextHolder.getLocale();
        String localizedErrorMessage = mMessageSource.getMessage(fieldError, currentLocale);
 
        //If the message was not found, return the most accurate field error code instead.
        //we can remove this check if we prefer to get the default error message.
        if (localizedErrorMessage.equals(fieldError.getDefaultMessage())) {
            String[] fieldErrorCodes = fieldError.getCodes();
            localizedErrorMessage = fieldErrorCodes[0];
        }
 
        return localizedErrorMessage;
    }
}
