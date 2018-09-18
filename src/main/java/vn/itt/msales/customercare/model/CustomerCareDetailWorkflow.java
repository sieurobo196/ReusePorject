/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.customercare.model;

/**
 *
 * @author cshiflett
 */
public class CustomerCareDetailWorkflow {
    private String title;
    
    private String posName;
    
    private String posOwner;
    
    private String posAddress;
    
    private String posTel;
    
    private String toolName;
    
    private int toolCount;
    
    private String compatitorName; // ten doi thu canh tranh
    
    private String compatitorValue;
    
    //private String

    /**
     * @return the posName
     */
    public String getPosName() {
        return posName;
    }

    /**
     * @param posName the posName to set
     */
    public void setPosName(String posName) {
        this.posName = posName;
    }

    /**
     * @return the posOwner
     */
    public String getPosOwner() {
        return posOwner;
    }

    /**
     * @param posOwner the posOwner to set
     */
    public void setPosOwner(String posOwner) {
        this.posOwner = posOwner;
    }

    /**
     * @return the posAddress
     */
    public String getPosAddress() {
        return posAddress;
    }

    /**
     * @param posAddress the posAddress to set
     */
    public void setPosAddress(String posAddress) {
        this.posAddress = posAddress;
    }

    /**
     * @return the posTel
     */
    public String getPosTel() {
        return posTel;
    }

    /**
     * @param posTel the posTel to set
     */
    public void setPosTel(String posTel) {
        this.posTel = posTel;
    }

    /**
     * @return the toolName
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * @param toolName the toolName to set
     */
    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    /**
     * @return the toolCount
     */
    public int getToolCount() {
        return toolCount;
    }

    /**
     * @param toolCount the toolCount to set
     */
    public void setToolCount(int toolCount) {
        this.toolCount = toolCount;
    }

    /**
     * @return the compatitorName
     */
    public String getCompatitorName() {
        return compatitorName;
    }

    /**
     * @param compatitorName the otherName to set
     */
    public void setCompatitorName(String compatitorName) {
        this.compatitorName = compatitorName;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the compatitorValue
     */
    public String getCompatitorValue() {
        return compatitorValue;
    }

    /**
     * @param compatitorValue the compatitorValue to set
     */
    public void setCompatitorValue(String compatitorValue) {
        this.compatitorValue = compatitorValue;
    }
}
