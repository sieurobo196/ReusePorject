/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.model;

import java.util.ArrayList;
import java.util.List;
import vn.itt.msales.entity.MCPDetails;
import vn.itt.msales.entity.MCPSalesDetails;

/**
 *
 * @author PhucPD
 */
public class WebPlanForm {
    private int mcpId;
    
    private int mcpDetailId;
    
    private String mcpName;
    
    // Mã tĩnh/thành.
    private int provinceId;
    
    // Mã quận/huyện.
    private int districId;
    
    // Mã phường/xã.
    private int wardId;
    
    // Người lập.
    private int establistId;
    
    // Người thực hiện.
    private int implementId;
    
    // Thứ.
    private int dayOfWeek;
    
    private String beginDate;
    
    // Tên/mã/địa chỉ ĐBH.
    private String nameCodeAddressPos;
    
    private List<String> listDiemBH;
    
    private List<String> listDiemBHSelect;
    
    private List<Integer> tempPos;
    
    private List<MCPDetails> keHoachChiTiets = new ArrayList<MCPDetails>(0);
    
    private MCPSalesDetails salesDetails;
    
    private boolean override;
    
    public WebPlanForm() {
    }
    
    /**
     * @return the mcpId
     */
    public int getMcpId() {
        return mcpId;
    }

    /**
     * @param mcpId the mcpId to set
     */
    public void setMcpId(int mcpId) {
        this.mcpId = mcpId;
    }

    /**
     * @return the provinceId
     */
    public int getProvinceId() {
        return provinceId;
    }

    /**
     * @param provinceId the provinceId to set
     */
    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    /**
     * @return the districId
     */
    public int getDistricId() {
        return districId;
    }

    /**
     * @param districId the districId to set
     */
    public void setDistricId(int districId) {
        this.districId = districId;
    }

    /**
     * @return the wardId
     */
    public int getWardId() {
        return wardId;
    }

    /**
     * @param wardId the wardId to set
     */
    public void setWardId(int wardId) {
        this.wardId = wardId;
    }

    /**
     * @return the establistId
     */
    public int getEstablistId() {
        return establistId;
    }

    /**
     * @param establistId the establistId to set
     */
    public void setEstablistId(int establistId) {
        this.establistId = establistId;
    }

    /**
     * @return the implementId
     */
    public int getImplementId() {
        return implementId;
    }

    /**
     * @param implementId the implementId to set
     */
    public void setImplementId(int implementId) {
        this.implementId = implementId;
    }

    /**
     * @return the dayOfWeek
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the mcpName
     */
    public String getMcpName() {
        return mcpName;
    }

    /**
     * @param mcpName the mcpName to set
     */
    public void setMcpName(String mcpName) {
        this.mcpName = mcpName;
    }

    /**
     * @return the nameCodeAddressPos
     */
    public String getNameCodeAddressPos() {
        return nameCodeAddressPos;
    }

    /**
     * @param nameCodeAddressPos the nameCodeAddressPos to set
     */
    public void setNameCodeAddressPos(String nameCodeAddressPos) {
        this.nameCodeAddressPos = nameCodeAddressPos;
    }

    /**
     * @return the listDiemBH
     */
    public List<String> getListDiemBH() {
        return listDiemBH;
    }

    /**
     * @param listDiemBH the listDiemBH to set
     */
    public void setListDiemBH(List<String> listDiemBH) {
        this.listDiemBH = listDiemBH;
    }

    /**
     * @return the listDiemBHSelect
     */
    public List<String> getListDiemBHSelect() {
        return listDiemBHSelect;
    }

    /**
     * @param listDiemBHSelect the listDiemBHSelect to set
     */
    public void setListDiemBHSelect(List<String> listDiemBHSelect) {
        this.listDiemBHSelect = listDiemBHSelect;
    }

    /**
     * @return the tempPos
     */
    public List<Integer> getTempPos() {
        if (tempPos != null) {
            return tempPos;
        }
        tempPos = new ArrayList<Integer>();
        for (MCPDetails ct : keHoachChiTiets) {
            //if (ct.isTamThoi()) {
                tempPos.add(ct.getPosId());
            //}
        }
        return tempPos;
    }

    /**
     * @param tempPos the tempPos to set
     */
    public void setTempPos(List<Integer> tempPos) {
        this.tempPos = tempPos;
    }

    /**
     * @return the keHoachChiTiets
     */
    public List<MCPDetails> getKeHoachChiTiets() {
        return keHoachChiTiets;
    }

    /**
     * @param keHoachChiTiets the keHoachChiTiets to set
     */
    public void setKeHoachChiTiets(List<MCPDetails> keHoachChiTiets) {
        this.keHoachChiTiets = keHoachChiTiets;
    }

    /**
     * @return the beginDate
     */
    public String getBeginDate() {
        return beginDate;
    }

    /**
     * @param beginDate the beginDate to set
     */
    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    /**
     * @return the salesDetails
     */
    public MCPSalesDetails getSalesDetails() {
        return salesDetails;
    }

    /**
     * @param salesDetails the salesDetails to set
     */
    public void setSalesDetails(MCPSalesDetails salesDetails) {
        this.salesDetails = salesDetails;
    }

    /**
     * @return the mcpDetailId
     */
    public int getMcpDetailId() {
        return mcpDetailId;
    }

    /**
     * @param mcpDetailId the mcpDetailId to set
     */
    public void setMcpDetailId(int mcpDetailId) {
        this.mcpDetailId = mcpDetailId;
    }

    /**
     * @return the override
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * @param override the override to set
     */
    public void setOverride(boolean override) {
        this.override = override;
    }
}
