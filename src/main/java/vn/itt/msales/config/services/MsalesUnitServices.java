package vn.itt.msales.config.services;

import java.util.List;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;

/**
 *
 * @author ChinhNQ
 */
public interface MsalesUnitServices {

    public boolean checkName(String field, String name, int companyId);
    public MsalesResults<Unit> searchUnit(String textSearch, int companyId, MsalesPageRequest page);

    public List<Unit> getListUnit(int companyId);
}
