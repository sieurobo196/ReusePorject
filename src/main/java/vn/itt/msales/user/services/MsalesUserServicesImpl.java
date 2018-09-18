package vn.itt.msales.user.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import vn.itt.msales.common.OptionItem;
import vn.itt.msales.database.dbrouting.DatabaseType;
import vn.itt.msales.entity.Company;
import vn.itt.msales.entity.Equipment;
import vn.itt.msales.entity.MsalesCompany;
import vn.itt.msales.entity.User;
import vn.itt.msales.entity.UserGoodsCategory;
import vn.itt.msales.entity.UserRoleChannel;
import vn.itt.msales.entity.param.MsalesParameter;
import vn.itt.msales.entity.request.MsalesPageRequest;
import vn.itt.msales.entity.response.MsalesResults;
import vn.itt.msales.entity.searchObject.SearchObject;
import vn.itt.msales.entity.searchObject.SearchUser;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class MsalesUserServicesImpl implements MsalesUserServices {

    @Autowired
    private DataService dataService;

    public DataService getDataService() {
        return dataService;
    }

    public void setDataService(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public int count() {
        return dataService.executeHQL("SELECT COUNT(DISTINCT User) AS VAL");
    }

    @Override
    public MsalesResults<HashMap> searchUser(List<OptionItem> userList, SearchUser searchUser, int companyId, MsalesPageRequest pageRequest) {
        MsalesResults<HashMap> ret = new MsalesResults<>();
        ret.setCount(0L);
        ret.setContentList(new ArrayList<HashMap>());

        if (!userList.isEmpty()) {
            String hqlList = "SELECT DISTINCT(User.id) AS id,User.username AS username, CONCAT(User.lastName , ' ' , User.firstName) AS name,User.tel AS tel,"
                    + " UserRoleChannel.userRoles as userRoles,User.locations AS locations, User.isActive AS isActive,"
                    + " User.statuss AS statuss,User.userCode AS userCode";
            String hqlCount = "SELECT COUNT(DISTINCT User.id)";
            String hql = " FROM UserRoleChannel as UserRoleChannel"
                    + " RIGHT JOIN UserRoleChannel.users AS User"
                    + " JOIN UserRoleChannel.channels AS Channel"
                    + " JOIN User.locations AS Location"
                    + " WHERE UserRoleChannel.deletedUser = 0"
                    + " AND User.deletedUser = 0"
                    + " AND User.companys.id = :companyId"
                    + " AND User.id IN (:ids)";
            List<Integer> ids = new ArrayList<>();
            for (OptionItem optionItem : userList) {
                ids.add(optionItem.getId());
            }

            List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
            parameters.add(MsalesParameter.create("ids", ids, 1));

            if (searchUser.getActiveId() != null && searchUser.getActiveId() != -1) {
                hql += " AND User.isActive = :isActive";
                parameters.add(MsalesParameter.create("isActive", searchUser.getActiveId()));
            }
            if (searchUser.getStatusId() != null && searchUser.getStatusId() != 0) {
                hql += " AND User.statuss.id = :statusId";
                parameters.add(MsalesParameter.create("statusId", searchUser.getStatusId()));
            }
            if (searchUser.getUserRoleId() != null && searchUser.getUserRoleId() != 0) {
                hql += " AND UserRoleChannel.userRoles.id = :userRoleId";
                parameters.add(MsalesParameter.create("userRoleId", searchUser.getUserRoleId()));
            }
            if (searchUser.getSearchText() != null && !searchUser.getSearchText().trim().isEmpty()) {
                hql += " AND (CONCAT(User.lastName , ' ' , User.firstName) LIKE :searchText OR User.userCode LIKE :searchText)";
                parameters.add(MsalesParameter.create("searchText", "%" + searchUser.getSearchText() + "%"));
            }
            hql += " ORDER BY User.lastName,User.firstName,User.userCode";
            List<HashMap> list = dataService.executeSelectHQL(hqlList + hql, parameters, true, pageRequest.getPageNo(), pageRequest.getRecordsInPage());
            List<Long> count = dataService.executeSelectHQL(hqlCount + hql, parameters, false, 0, 0);
            ret.setContentList(list);
            ret.setCount(count.get(0));
        }

        return ret;
    }

    @Override
    public boolean checkUsernameExists(String name) {
        String hql = "FROM User"
                + " WHERE username = '" + name + "'";
        List<User> list = dataService.executeSelectHQL(User.class, hql, false, 1, 1);
        return list == null || list.isEmpty();
    }

    @Override
    public int updateUserRoleChannel(User user, List<UserRoleChannel> userRoleChannels) {
        String hql = "UPDATE UserRoleChannel"
                + " SET deletedUser = '" + user.getUpdatedUser() + "'"
                + " WHERE users.id = '" + user.getId() + "'"
                + " AND id NOT IN (";
        for (UserRoleChannel userRoleChannel : userRoleChannels) {
            hql += userRoleChannel.getId() + ",";
        }
        hql += "'')";
        return dataService.executeHQL(hql);
    }

    @Override
    public int updateUserGoodsCategory(User user, List<UserGoodsCategory> userGoodsCategorys) {
        String hql = "UPDATE UserGoodsCategory"
                + " SET statuss.id = 17,"
                + " deletedUser = '" + user.getUpdatedUser() + "'"//sua lai sau
                + " WHERE users.id = '" + user.getId() + "'"
                + " AND id NOT IN (";
        for (UserGoodsCategory userGoodsCategory : userGoodsCategorys) {
            hql += userGoodsCategory.getId() + ",";
        }
        hql += "'')";
        return dataService.executeHQL(hql);
    }

    @Override
    public boolean checkMaxSaleUser(int companyId, Integer userId, Integer equipmentMax, DataService dataService) {
        String hql = "SELECT DISTINCT users.id"
                + " FROM UserRoleChannel"
                + " WHERE deletedUser = 0"
                + " AND users.isActive = 1"//dang hoat dong
                + " AND users.statuss.id = 6"//dang lam viec
                + " AND users.deletedUser = 0"
                + " AND userRoles.id IN (4,5,6)"
                + " AND users.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        List<Integer> list = dataService.executeSelectHQL(hql, parameters, false, 0, 0);
        if (userId != null) {
            for (int id : list) {
                if (userId == id) {
                    return false;
                }
            }
        }

        return list.size() >= equipmentMax;
    }

    @Override
    public List<OptionItem> getListBranch() {
        List<OptionItem> ret = new ArrayList<>();
        OptionItem optionItem = new OptionItem(0, "-- Ngành nghề --");
        ret.add(optionItem);
        for (DatabaseType databaseType : DatabaseType.values()) {
            String name = "";
            if (databaseType.getBranch() > 0) {
                switch (databaseType.getBranch()) {
                    case 1:
                        name = "Viễn thông";
                        break;
                    case 2:
                        name = "Tài chính";
                        break;
                    case 3:
                        name = "Bảo hiểm";
                        break;
                    case 4:
                        name = "Dược";
                        break;
                    case 5:
                        name = "Tiêu dùng";
                        break;
                }
                optionItem = new OptionItem(databaseType.getBranch(), name);
                ret.add(optionItem);
            }
        }
        return ret;
    }

    @Override
    public List<Company> searchCompany(SearchObject searchObject, DataService dataService) {
        List<Company> list = new ArrayList<>();
        Integer branch = searchObject.getBranch();
        if (branch != null && branch != 0) {
            dataService.setBranch(branch);
            List<Company> temp = getCompanies(searchObject.getSearchText(), searchObject.getPackageService(), dataService);
            for (Company company : temp) {
                company.setBranch(branch);
            }
            list.addAll(temp);
        } else {
            for (DatabaseType databaseType : DatabaseType.values()) {
                if (databaseType.getBranch() != 0) {
                    dataService.setBranch(databaseType.getBranch());
                    List<Company> temp = getCompanies(searchObject.getSearchText(), searchObject.getPackageService(), dataService);
                    for (Company company : temp) {
                        company.setBranch(databaseType.getBranch());
                    }
                    list.addAll(temp);
                }
            }
        }
        return list;
    }

    private List<Company> getCompanies(String name, Integer packageService, DataService dataService) {
        String hql = "FROM Company"
                + " WHERE deletedUser = 0";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (name != null && !name.trim().isEmpty()) {
            hql += " AND (name LIKE :name OR code LIKE :name)";
            parameters.add(MsalesParameter.create("name", "%" + name + "%"));
        }
        if (packageService != null && packageService != 0) {
            hql += " AND packageService = :packageService";
            parameters.add(MsalesParameter.create("packageService", packageService));
        }

        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    private List<Equipment> getEquipments(String searchText, DataService dataService) {
        String hql = "FROM Equipment"
                + " WHERE deletedUser = 0";
        List<MsalesParameter> parameters = new ArrayList<>();
        if (searchText != null && !searchText.trim().isEmpty()) {
            hql += " AND (imei LIKE :searchText"
                    + " OR subscriberId LIKE :searchText"
                    + " OR users.username LIKE :searchText"
                    + " OR companys.name LIKE :searchText"
                    + " OR CONCAT(users.lastName,' ', users.firstName) LIKE :searchText)";
            parameters.add(MsalesParameter.create("searchText", "%" + searchText + "%"));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }

    @Override
    public List<OptionItem> getListPackage(boolean full) {
        List<OptionItem> ret = new ArrayList<>();

        OptionItem optionItem = new OptionItem(1, "Normal");
        ret.add(optionItem);
        optionItem = new OptionItem(2, "Professional");
        ret.add(optionItem);
        optionItem = new OptionItem(3, "Premium");
        ret.add(optionItem);

        if (full) {
            optionItem = new OptionItem(0, "-- Gói dịch vụ --");
            ret.add(0, optionItem);
        }
        return ret;
    }

    @Override
    public MsalesCompany getMsalesCompany(String code, int branch, DataService dataService) {
        String hql = "FROM MsalesCompany"
                + " WHERE deletedUser = 0"
                + " AND code = :code"
                + " AND branch = :branch";
        List<MsalesParameter> parameters = MsalesParameter.createList("code", code);
        parameters.add(MsalesParameter.create("branch", branch));
        List<MsalesCompany> list = dataService.executeSelectHQL(hql, parameters, false, 1, 1);
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public boolean deleteCompany(Company company, DataService dataService) {
//        String hqlMCP = "DELETE MCP  WHERE implementEmployees.companys.id = :id";
//        String hqlMCPDetails = "DELETE MCPDetails  WHERE implementEmployees.companys.id = :id";
//        String hqlUserGoodsCategory = "DELETE UserGoodsCategory WHERE users.companys.id = :id";        
//        String hqlUser = "DELETE User where companys.id = :id";
//        String hqlUserRoute = "DELETE UserRoute WHERE createdUser IN (:userIds)";
//        
//        String hqlSalesStock = "DELETE SalesStock WHERE poss.channels.companys.id = :id OR salemanUsers.companys.id = :id OR channels.companys.id = :id";        
//        String hqlChannelType = "DELETE ChannelType  WHERE channels.companys.id = :id";
//        String hqlPOSImg = "DELETE POSImg  WHERE poss.channels.companys.id = :id";
//        String hqlCustomerCareDetails = "DELETE CustomerCareDetails  WHERE poss.channels.companys.id = :id";
//        String hqlCustomerCareInfo = "DELETE CustomerCareInfomation  WHERE poss.channels.companys.id = :id";        
//        String hqlPOS = "DELETE POS  WHERE channels.companys.id = :id";
//        
//        String hqlGoodsChannelFocus = "DELETE GoodsChannelFocus  WHERE channels.id = :id";
//        String hqlUserRoleChannel = "DELETE UserRoleChannel WHERE channels.companys.id = :id";
//        String hqlChannel = "DELETE Channel  WHERE companys.id = :id";
//        String hqlSalesTransDetails = "DELETE SalesTransDetails WHERE salesTranss.companys.id = :id";
//        String hqlSalesTrans = "DELETE SalesTrans WHERE companys.id = :id";
//        String hqlSalesStockGoods = "DELETE SalesStockGoods WHERE goodss.goodsCategorys.companys.id = :id";
//        String hqlGoods = "DELETE Gooods  WHERE goodsCategorys.companys.id =:id";
//        String hqlGoodsCategory = "DELETE GoodsCategory  WHERE companys.id = :id";
//        String hqlPromotionAccumulationRetailer = "DELETE PromotionAccumulationRetailer WHERE promotions.companys.id = :id";
//        String hqlPromotionAwardOther = "DELETE PromotionAwardOther WHERE promotions.companys.id = :id";
//        String hqlPromotionChannel ="DELETE PromotionChannel WHERE promotions.companys.id = :id";
//        String hqlPromotionConditionRef = "DELETE PromotionCoditionRef WHERE promotions.companys.id = :id";
//        String hqlPromotionGoodsRef = "DELETE PromotionGoodsRef WHERE promotions.companys.id = :id";
//        String hqlPromotionTransRetailer ="DELETE PromotionTransRetailer WHERE promotions.companys.id = :id";
//        String hqlPromotion = "DELETE Promotion  WHERE companys.id = :id";
//        String hqlSalesOrderDetails = "DELETE SalesOrderDetails WHERE orders.companys.id = :id";
//        String hqlSalesOrder = "DELETE SalesOrder WHERE companys.id = :id";
//        String hqlGoodsUnit = "DELETE GoodsUnit  WHERE units.companys.id = :id";
//        String hqlUnit = "DELETE Unit WHERE companys.id = :id";        
//        String hqlCompanyConfigKPI = "DELETE CompanyConfigKPI  WHERE companys.id = :id";
//        String hqlCompanyConfigDetail = "DELETE CompanyConfigDetail  WHERE companyConfigs.companys.id = :id";
//        String hqlCompanyConfig = "DELETE CompanyConfig  WHERE companys.id = :id";
//        String hqlWorkflowDetails = "DELETE WorkflowDetails WHERE workflows.companys.id = :id";
//        String hqlWorkflow = "DELETE Workflow WHERE companys.id = :id";
//        String hqlVersion = "DELETE Version WHERE companys.id = :id";
//        String hqlEquipent = "DELETE Equipment  WHERE companys.id = :id";
//        String hqlCompanyConstrant = "DELETE CompanyConstrant  WHERE companys.id = :id";
//        String hqlCompanyHoliday = "DELETE CompanyHoliday  WHERE companys.id = :id";
//        String hqlCompany = "DELETE Company  WHERE id = :id";
//        
//        List<MsalesParameter> parameters = MsalesParameter.createList("id",id);
//        
//        Session datasesSession = dataService.openSession();
//        Transaction transaction = datasesSession.beginTransaction();
//        try{
//            datasesSession.createQuery(hqlMCP).setParameter("id", id).executeUpdate();
//            datasesSession.createQuery(hqlMCPDetails).setParameter("id", id).executeUpdate();
//            
//            transaction.commit();
//            return true;
//        }
//        catch(Exception ex){
//            transaction.rollback();
//            return false;
//        }     

        company.setDeletedUser(1);
        company.setDeletedAt(new Date());
        dataService.updateRow(company);
        dataService.setBranch(DatabaseType.DATABASE_TYPE_COMPANY.getBranch());
        MsalesCompany msalesCompany = getMsalesCompany(company.getCode(), company.getBranch(), dataService);
        if (msalesCompany != null) {
            msalesCompany.setDeletedUser(-1);//admin
            msalesCompany.setDeletedAt(new Date());
            dataService.updateRow(msalesCompany);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Equipment> searchEquipment(SearchObject searchObject, DataService dataService) {
        List<Equipment> list = new ArrayList<>();
        Integer branch = searchObject.getBranch();

        if (branch != null && branch != 0) {
            dataService.setBranch(branch);
            List<Equipment> temp = getEquipments(searchObject.getSearchText(), dataService);
            for (Equipment equipment : temp) {
                equipment.setBranch(branch);
            }
            list.addAll(temp);
        } else {
            for (DatabaseType databaseType : DatabaseType.values()) {
                if (databaseType.getBranch() != 0) {
                    dataService.setBranch(databaseType.getBranch());
                    List<Equipment> temp = getEquipments(searchObject.getSearchText(), dataService);
                    for (Equipment equipment : temp) {
                        equipment.setBranch(databaseType.getBranch());
                    }
                    list.addAll(temp);
                }
            }
        }
        return list;
    }

    @Override
    public boolean deleteEquipment(Equipment equipment, DataService dataService) {
        if (equipment != null) {
            equipment.setDeletedUser(-1);//admin
            equipment.setDeletedAt(new Date());
            dataService.updateRow(equipment);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Equipment> getListEquipmentByCompanyId(int companyId,String searchText, DataService dataService) {
        String hql = "FROM Equipment AS e"
                + " WHERE e.deletedUser = 0"
                + " AND e.companys.id = :companyId";
        List<MsalesParameter> parameters = MsalesParameter.createList("companyId", companyId);
        if (searchText != null && !searchText.trim().isEmpty()) {
            hql += " AND (imei LIKE :searchText"
                    + " OR subscriberId LIKE :searchText"
                    + " OR users.username LIKE :searchText"
                    + " OR CONCAT(users.lastName,' ', users.firstName) LIKE :searchText)";
            parameters.add(MsalesParameter.create("searchText", "%" + searchText + "%"));
        }
        return dataService.executeSelectHQL(hql, parameters, false, 0, 0);
    }
}
