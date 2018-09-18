package vn.itt.msales.entity.searchObject;

public class SearchPOS {

    private Integer locationId;
    
    private String searchText;
    
    private String[] posList;
    
    private String[] posListSelected;
    
    private String beginDate;

    public SearchPOS() {
    }

    public SearchPOS(Integer locationId, String searchText) {
        this.locationId = locationId;
        this.searchText = searchText;
    }

    public SearchPOS(String[] posList) {
        this.posList = posList;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    /**
     * @return the posList
     */
    public String[] getPosList() {
        return posList;
    }

    /**
     * @param posList the posList to set
     */
    public void setPosList(String[] posList) {
        this.posList = posList;
    }

    /**
     * @return the posListSelected
     */
    public String[] getPosListSelected() {
        return posListSelected;
    }

    /**
     * @param posListSelected the posListSelected to set
     */
    public void setPosListSelected(String[] posListSelected) {
        this.posListSelected = posListSelected;
    }

	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}
    
}
