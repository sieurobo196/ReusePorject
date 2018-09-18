package vn.itt.msales.customercare.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.itt.msales.entity.POS;


public class POSValidation implements Validator{
	@Override
	//Only validate for UserForm and its sub-class.
	public boolean supports(Class<?> clazz) {
		return POS.class.equals(clazz);		
	}

	@Override
	public void validate(Object target, Errors errors) {
		POS pos = (POS) target;
		
		if (pos.getName() ==null  || pos.getName().isEmpty()){
                    errors.rejectValue("name", "errTenKeHoach", "errCodeTenKeHoach");
		}
	}
}
