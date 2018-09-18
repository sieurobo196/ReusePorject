/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.report.model;

import java.util.Date;

/**
 *
 * @author vtm_2
 */
public class ReportKPI {
        
	private Integer userId;
        private String nameUser;
	private Float percentRevenue;
	private Float percentRoute;
	private Long targetRevenue;
	private Long revenue;
	private Integer targetPosCare;
	private Integer posCare;
	private Integer posSales;
	private String startDateCare;
	private String endDateCare;
	private String totalHour;
        private String supervisorComment;
        private String comment;
        private long pointNV;

    public ReportKPI() {
    }

    public ReportKPI(Integer userId, String nameUser, Float percentRevenue, Float percentRoute, Long targetRevenue, Long revenue, Integer targetPosCare, Integer posCare, Integer posSales, String startDateCare, String endDateCare, String totalHour, String supervisorComment, String comment) {
        this.userId = userId;
        this.nameUser = nameUser;
        this.percentRevenue = percentRevenue;
        this.percentRoute = percentRoute;
        this.targetRevenue = targetRevenue;
        this.revenue = revenue;
        this.targetPosCare = targetPosCare;
        this.posCare = posCare;
        this.posSales = posSales;
        this.startDateCare = startDateCare;
        this.endDateCare = endDateCare;
        this.totalHour = totalHour;
        this.supervisorComment = supervisorComment;
        this.comment = comment;
    }

    public long getPointNV() {
        return pointNV;
    }

    public void setPointNV(long pointNV) {
        this.pointNV = pointNV;
    }

  

    public String getNameUser() {
        return nameUser;
    }

    public void setNameUser(String nameUser) {
        this.nameUser = nameUser;
    }
    
    
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

   

    public Float getPercentRevenue() {
        return percentRevenue;
    }

    public void setPercentRevenue(Float percentRevenue) {
        this.percentRevenue = percentRevenue;
    }

    public Float getPercentRoute() {
        return percentRoute;
    }

    public void setPercentRoute(Float percentRoute) {
        this.percentRoute = percentRoute;
    }

    public Long getTargetRevenue() {
        return targetRevenue;
    }

    public void setTargetRevenue(Long targetRevenue) {
        this.targetRevenue = targetRevenue;
    }

    public Long getRevenue() {
        return revenue;
    }

    public void setRevenue(Long revenue) {
        this.revenue = revenue;
    }

    

    public Integer getTargetPosCare() {
        return targetPosCare;
    }

    public void setTargetPosCare(Integer targetPosCare) {
        this.targetPosCare = targetPosCare;
    }

    public Integer getPosCare() {
        return posCare;
    }

    public void setPosCare(Integer posCare) {
        this.posCare = posCare;
    }

    public Integer getPosSales() {
        return posSales;
    }

    public void setPosSales(Integer posSales) {
        this.posSales = posSales;
    }

   
    public String getEndDateCare() {
        return endDateCare;
    }

    public void setEndDateCare(String endDateCare) {
        this.endDateCare = endDateCare;
    }

    public String getStartDateCare() {
        return startDateCare;
    }

    public void setStartDateCare(String startDateCare) {
        this.startDateCare = startDateCare;
    }

    public String getTotalHour() {
        return totalHour;
    }

    public void setTotalHour(String totalHour) {
        this.totalHour = totalHour;
    }

    public String getSupervisorComment() {
        return supervisorComment;
    }

    public void setSupervisorComment(String supervisorComment) {
        this.supervisorComment = supervisorComment;
    }

    

   

  
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
        	
}
