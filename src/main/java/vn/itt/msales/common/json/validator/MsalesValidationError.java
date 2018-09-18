/*
 * Copyright (C) 2015 The mSales Project
 * 
 */
package vn.itt.msales.common.json.validator;

import java.util.ArrayList;
import java.util.List;
import vn.itt.msales.common.json.MsalesFieldError;

/**
 * @author ChinhNQ
 * @version
 * @since 1 Jun 2015 18:07:22
 * msales#com.itt.msales.utils.converter.json.MsalesValidationError.java
 * 
 * It contains a list of FieldErrorDTO objects and a method which is used to add new field errors to the list.
 * The following listing provides an example Json document which is send back to the user of our API when the validation fails
 * {
 *    "fieldErrors":[
 *        {
 *            "field":"text",
 *            "message":"error message"
 *        }
 *    ]
  * }
 */
public class MsalesValidationError {
    
    private List<MsalesFieldError> fieldErrors = new ArrayList<MsalesFieldError>();
    
    public void addFieldError(String path, String message) {
        MsalesFieldError error = new MsalesFieldError(path, message);
        fieldErrors.add(error);
    }

}
