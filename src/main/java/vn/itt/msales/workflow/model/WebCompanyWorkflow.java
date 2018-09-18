package vn.itt.msales.workflow.model;

import java.util.LinkedHashMap;

/**
 *
 * @author ChinhNQ
 */
public class WebCompanyWorkflow {

    private String bgColor;
    private String textColor;
    private String buttonBgColor;
    private String buttonBgColorOver;
    private String topBarBgColor;

    private LinkedHashMap<String, Object> pages;

    public WebCompanyWorkflow() {
    }

    public WebCompanyWorkflow(String bgColor, String textColor, String buttonBgColor, String buttonBgColorOver, String topBarBgColor, LinkedHashMap<String, Object> pages) {
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.buttonBgColor = buttonBgColor;
        this.buttonBgColorOver = buttonBgColorOver;
        this.topBarBgColor = topBarBgColor;
        this.pages = pages;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getButtonBgColor() {
        return buttonBgColor;
    }

    public void setButtonBgColor(String buttonBgColor) {
        this.buttonBgColor = buttonBgColor;
    }

    public String getButtonBgColorOver() {
        return buttonBgColorOver;
    }

    public void setButtonBgColorOver(String buttonBgColorOver) {
        this.buttonBgColorOver = buttonBgColorOver;
    }

    public String getTopBarBgColor() {
        return topBarBgColor;
    }

    public void setTopBarBgColor(String topBarBgColor) {
        this.topBarBgColor = topBarBgColor;
    }

    public LinkedHashMap<String, Object> getPages() {
        return pages;
    }

    public void setPages(LinkedHashMap<String, Object> pages) {
        this.pages = pages;
    }

}
