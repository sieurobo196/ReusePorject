package vn.itt.msales.config.model;

import java.util.List;
import vn.itt.msales.entity.Unit;

/**
 *
 * @author nguyenquocchinh
 */
public class WebUnits {
  private String textSearch;
  private List<Unit> listUnits;

  public WebUnits() {
  }

  public WebUnits(String textSearch, List<Unit> listUnits) {
    this.textSearch = textSearch;
    this.listUnits = listUnits;
  }

  public String getTextSearch() {
    return textSearch;
  }

  public void setTextSearch(String textSearch) {
    this.textSearch = textSearch;
  }

  public List<Unit> getListUnits() {
    return listUnits;
  }

  public void setListUnits(List<Unit> listUnits) {
    this.listUnits = listUnits;
  }

  
}
