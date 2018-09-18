package vn.itt.msales.customercare.model;

import java.util.Date;
import java.util.List;
import java.util.Set;
import vn.itt.msales.entity.POS;
import vn.itt.msales.entity.User;

/**
 *
 * @author ChinhNQ
 */
public class MsalesPlanImport {

    private String userName;
    private String posCode;
    private Date dateCare;
    private String mcpName;
    private User user;
    private List<POS> posList;
    
    private List<String> posDuplicateList;
    
    private Set<String> posCodeRepeatList;
    
    private List<String> posCodeDuplicate;

    public MsalesPlanImport() {
    }

    public MsalesPlanImport(String employeeCode, String posCode, Date dateCare, String mcpName) {
        this.userName = employeeCode;
        this.posCode = posCode;
        this.dateCare = dateCare;
        this.mcpName = mcpName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }

    public Date getDateCare() {
        return dateCare;
    }

    public void setDateCare(Date dateCare) {
        this.dateCare = dateCare;
    }

    public String getMcpName() {
        return mcpName;
    }

    public void setMcpName(String mcpName) {
        this.mcpName = mcpName;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<POS> getPosList() {
        return posList;
    }

    public void setPosList(List<POS> posList) {
        this.posList = posList;
    }

    public List<String> getPosDuplicateList() {
        return posDuplicateList;
    }

    public void setPosDuplicateList(List<String> posDuplicateList) {
        this.posDuplicateList = posDuplicateList;
    }

    public Set<String> getPosCodeRepeatList() {
        return posCodeRepeatList;
    }

    public void setPosCodeRepeatList(Set<String> posCodeRepeatList) {
        this.posCodeRepeatList = posCodeRepeatList;
    }

    public List<String> getPosCodeDuplicate() {
        return posCodeDuplicate;
    }

    public void setPosCodeDuplicate(List<String> posCodeDuplicate) {
        this.posCodeDuplicate = posCodeDuplicate;
    }
}
