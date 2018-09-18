package vn.itt.msales.config.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.itt.msales.entity.Unit;
import vn.itt.msales.entity.param.ParameterList;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.services.DataService;
import vn.itt.msales.services.DataServiceImpl;

/**
 *
 * @author ChinhNQ
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MsalesUnitServicesImpl extends DataServiceImpl implements MsalesUnitServices {

    @Autowired
    private DataService dataService;

    @Override
    public boolean checkName(String field, String name, int companyId) {
        String hql = "FROM Unit where "+ field + " = '" + name + "' and companys.id = " + companyId +" and deletedUser = 0";
        List<Unit> listUnit = dataService.executeSelectHQL(Unit.class, hql, false, 0, 0);
        return (listUnit.size() > 0);
    }

  @Override
  public MsalesResults<Unit> searchUnit(String textSearch, int companyId, MsalesPageRequest page) {
    MsalesResults<Unit> listUnit = new MsalesResults<>();
    String hql = "FROM Unit WHERE  deletedUser=0 AND companys.id = " + companyId + " AND code LiKE CONCAT('%','"+textSearch+"' ,'%')  AND name LiKE CONCAT('%','"+textSearch+"' ,'%') order by order";
    List<Unit> listUnitAll = dataService.executeSelectHQL(Unit.class, hql, false, page.getPageNo(), page.getRecordsInPage());
    listUnit.setContentList(listUnitAll);
    listUnit.setCount((long)listUnitAll.size());
    return listUnit; 
  }

    @Override
    public List<Unit> getListUnit(int companyId) {
        ParameterList param = new ParameterList();
        param.add("companys.id", companyId);
        param.setOrder("order");

        List<Unit> listUnit = dataService.getListOption(Unit.class, param);

        return listUnit;
    }


}
