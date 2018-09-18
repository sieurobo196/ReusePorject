package vn.itt.msales.customercare.validator;

import java.util.ArrayList;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import vn.itt.msales.customercare.model.WebPlanForm;


public class PlanValidation implements Validator{
	@Override
	//Only validate for UserForm and its sub-class.
	public boolean supports(Class<?> clazz) {
		return WebPlanForm.class.equals(clazz);		
	}

	@Override
	public void validate(Object target, Errors errors) {
		WebPlanForm o = (WebPlanForm) target;
		
		String tenKeHoach=o.getMcpName();
		if (tenKeHoach ==null  || tenKeHoach.length()==0){
				errors.rejectValue("mcpName", "errTenKeHoach", "errCodeTenKeHoach");
		}
		if (o.getImplementId()==0){
			errors.rejectValue("implementId", "errNhanVienThucHien","errCodeNhanVienThucHien");
		}
                /*
		String ghiChu=o.getGhiChu();
		if (ghiChu!=null  && ghiChu.length()>500){
			errors.rejectValue("ghiChu", "errGhiChu", "errCodeGhiChu");
		}
                */
		 ArrayList<String> listDiemBHSelect= (ArrayList<String>) o.getListDiemBHSelect();
		if (listDiemBHSelect == null || listDiemBHSelect.size()==0)  {
			errors.rejectValue("ListDiemBHSelect", "errListDiemBHSelect","errCodeListDiemBHSelect");
		}
	}
}
