package vn.itt.msales.user.services;

import java.util.HashMap;
import java.util.List;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.entity.searchObject.SearchUser;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public interface MsalesUserServices {

    public int count();

    public MsalesResults<HashMap> searchUser(List<OptionItem> userList,SearchUser searchUser, int companyId, MsalesPageRequest pageRequest);

    public boolean checkUsernameExists(String name);

    public int updateUserRoleChannel(User user, List<UserRoleChannel> userRoleChannels);

    public int updateUserGoodsCategory(User user, List<UserGoodsCategory> userGoodsCategorys);
    
    public boolean checkMaxSaleUser(int companyId,Integer userId, Integer equipmentMax,DataService dataService);
    
    public List<OptionItem> getListBranch();
    
    public List<Company> searchCompany(SearchObject searchObject,DataService dataService);
    
    public List<OptionItem> getListPackage(boolean full);
    
    public MsalesCompany getMsalesCompany(String code,int branch,DataService dataService);
    
    public boolean deleteCompany(Company company,DataService dataService);
    
    public List<Equipment> searchEquipment(SearchObject searchObject,DataService dataService);
    
    public boolean deleteEquipment(Equipment equipment,DataService dataService);
    
    public List<Equipment> getListEquipmentByCompanyId(int companyId,String searchText,DataService dataService);
}
