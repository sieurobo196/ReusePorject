/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vtm036
 */
@Entity
@Table(name = "company_config_kpi", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ID"})})
@NamedQueries({
    @NamedQuery(name = "CompanyConfigKpi.findAll", query = "SELECT c FROM CompanyConfigKpi c"),
    @NamedQuery(name = "CompanyConfigKpi.findById", query = "SELECT c FROM CompanyConfigKpi c WHERE c.id = :id"),
    @NamedQuery(name = "CompanyConfigKpi.findByCompanyId", query = "SELECT c FROM CompanyConfigKpi c WHERE c.companyId = :companyId"),
    @NamedQuery(name = "CompanyConfigKpi.findByPeriod1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.period1 = :period1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPeriodPoint1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.periodPoint1 = :periodPoint1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPeriod2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.period2 = :period2"),
    @NamedQuery(name = "CompanyConfigKpi.findByPeriodPoint2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.periodPoint2 = :periodPoint2"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentPos1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentPos1 = :percentPos1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentPosPoint1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentPosPoint1 = :percentPosPoint1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentPos2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentPos2 = :percentPos2"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentPosPoint2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentPosPoint2 = :percentPosPoint2"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentTarget1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentTarget1 = :percentTarget1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentTargetPoint1", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentTargetPoint1 = :percentTargetPoint1"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentTarget2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentTarget2 = :percentTarget2"),
    @NamedQuery(name = "CompanyConfigKpi.findByPercentTargetPoint2", query = "SELECT c FROM CompanyConfigKpi c WHERE c.percentTargetPoint2 = :percentTargetPoint2"),
    @NamedQuery(name = "CompanyConfigKpi.findByMaxPoint", query = "SELECT c FROM CompanyConfigKpi c WHERE c.maxPoint = :maxPoint"),
    @NamedQuery(name = "CompanyConfigKpi.findByMaxCorlor", query = "SELECT c FROM CompanyConfigKpi c WHERE c.maxCorlor = :maxCorlor"),
    @NamedQuery(name = "CompanyConfigKpi.findByMediumPoint", query = "SELECT c FROM CompanyConfigKpi c WHERE c.mediumPoint = :mediumPoint"),
    @NamedQuery(name = "CompanyConfigKpi.findByMediumCorlor", query = "SELECT c FROM CompanyConfigKpi c WHERE c.mediumCorlor = :mediumCorlor"),
    @NamedQuery(name = "CompanyConfigKpi.findByMinPoint", query = "SELECT c FROM CompanyConfigKpi c WHERE c.minPoint = :minPoint"),
    @NamedQuery(name = "CompanyConfigKpi.findByMinCorlor", query = "SELECT c FROM CompanyConfigKpi c WHERE c.minCorlor = :minCorlor"),
    @NamedQuery(name = "CompanyConfigKpi.findByCreatedAt", query = "SELECT c FROM CompanyConfigKpi c WHERE c.createdAt = :createdAt"),
    @NamedQuery(name = "CompanyConfigKpi.findByCreatedUser", query = "SELECT c FROM CompanyConfigKpi c WHERE c.createdUser = :createdUser"),
    @NamedQuery(name = "CompanyConfigKpi.findByUpdatedAt", query = "SELECT c FROM CompanyConfigKpi c WHERE c.updatedAt = :updatedAt"),
    @NamedQuery(name = "CompanyConfigKpi.findByUpdatedUser", query = "SELECT c FROM CompanyConfigKpi c WHERE c.updatedUser = :updatedUser"),
    @NamedQuery(name = "CompanyConfigKpi.findByDeletedAt", query = "SELECT c FROM CompanyConfigKpi c WHERE c.deletedAt = :deletedAt"),
    @NamedQuery(name = "CompanyConfigKpi.findByDeletedUser", query = "SELECT c FROM CompanyConfigKpi c WHERE c.deletedUser = :deletedUser")})
public class CompanyConfigKpi implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPANY_ID", nullable = false)
    private Integer companyId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERIOD_1", nullable = false)
    private Integer period1 = 4;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERIOD_POINT_1", nullable = false)
    private Integer periodPoint1 = 100;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERIOD_2", nullable = false)
    private Integer period2 = 6;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERIOD_POINT_2", nullable = false)
    private Integer periodPoint2 = 50;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_POS_1", nullable = false)
    private Integer percentPos1 = 50;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_POS_POINT_1", nullable = false)
    private Integer percentPosPoint1 = 100;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_POS_2", nullable = false)
    private Integer percentPos2 = 75;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_POS_POINT_2", nullable = false)
    private Integer percentPosPoint2 = 50;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_TARGET_1", nullable = false)
    private Integer percentTarget1 = 30;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_TARGET_POINT_1", nullable = false)
    private Integer percentTargetPoint1 = 100;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_TARGET_2", nullable = false)
    private Integer percentTarget2 = 50;
    @Basic(optional = false)
    @NotNull
    @Column(name = "PERCENT_TARGET_POINT_2", nullable = false)
    private Integer percentTargetPoint2 = 50;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MAX_POINT", nullable = false)
    private Integer maxPoint = 200;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "MAX_CORLOR", nullable = false, length = 9)
    private String maxCorlor = "ff0000";
    @Basic(optional = false)
    @NotNull
    @Column(name = "MEDIUM_POINT", nullable = false)
    private Integer mediumPoint = 100;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "MEDIUM_CORLOR", nullable = false, length = 9)
    private String mediumCorlor = "ffff33";
    @Basic(optional = false)
    @NotNull
    @Column(name = "MIN_POINT", nullable = false)
    private Integer minPoint = 100;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 9)
    @Column(name = "MIN_CORLOR", nullable = false, length = 9)
    private String minCorlor = "008000";
    @Basic(optional = false)
    @NotNull
    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CREATED_USER", nullable = false)
    private Integer createdUser;
    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "UPDATED_USER", nullable = false)
    private Integer updatedUser;
    @Column(name = "DELETED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
    @Basic(optional = false)
    @NotNull
    @Column(name = "DELETED_USER", nullable = false)
    private Integer deletedUser;

    public CompanyConfigKpi() {
    }

    public CompanyConfigKpi(Integer id) {
        this.id = id;
    }

    public CompanyConfigKpi(Integer id, Integer companyId, Integer period1, Integer periodPoint1, Integer period2, Integer periodPoint2, Integer percentPos1, Integer percentPosPoint1, Integer percentPos2, Integer percentPosPoint2, Integer percentTarget1, Integer percentTargetPoint1, Integer percentTarget2, Integer percentTargetPoint2, Integer maxPoint, String maxCorlor, Integer mediumPoint, String mediumCorlor, Integer minPoint, String minCorlor, Date createdAt, Integer createdUser, Integer updatedUser, Integer deletedUser) {
        this.id = id;
        this.companyId = companyId;
        this.period1 = period1;
        this.periodPoint1 = periodPoint1;
        this.period2 = period2;
        this.periodPoint2 = periodPoint2;
        this.percentPos1 = percentPos1;
        this.percentPosPoint1 = percentPosPoint1;
        this.percentPos2 = percentPos2;
        this.percentPosPoint2 = percentPosPoint2;
        this.percentTarget1 = percentTarget1;
        this.percentTargetPoint1 = percentTargetPoint1;
        this.percentTarget2 = percentTarget2;
        this.percentTargetPoint2 = percentTargetPoint2;
        this.maxPoint = maxPoint;
        this.maxCorlor = maxCorlor;
        this.mediumPoint = mediumPoint;
        this.mediumCorlor = mediumCorlor;
        this.minPoint = minPoint;
        this.minCorlor = minCorlor;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedUser = updatedUser;
        this.deletedUser = deletedUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getPeriod1() {
        return period1;
    }

    public void setPeriod1(Integer period1) {
        this.period1 = period1;
    }

    public Integer getPeriodPoint1() {
        return periodPoint1;
    }

    public void setPeriodPoint1(Integer periodPoint1) {
        this.periodPoint1 = periodPoint1;
    }

    public Integer getPeriod2() {
        return period2;
    }

    public void setPeriod2(Integer period2) {
        this.period2 = period2;
    }

    public Integer getPeriodPoint2() {
        return periodPoint2;
    }

    public void setPeriodPoint2(Integer periodPoint2) {
        this.periodPoint2 = periodPoint2;
    }

    public Integer getPercentPos1() {
        return percentPos1;
    }

    public void setPercentPos1(Integer percentPos1) {
        this.percentPos1 = percentPos1;
    }

    public Integer getPercentPosPoint1() {
        return percentPosPoint1;
    }

    public void setPercentPosPoint1(Integer percentPosPoint1) {
        this.percentPosPoint1 = percentPosPoint1;
    }

    public Integer getPercentPos2() {
        return percentPos2;
    }

    public void setPercentPos2(Integer percentPos2) {
        this.percentPos2 = percentPos2;
    }

    public Integer getPercentPosPoint2() {
        return percentPosPoint2;
    }

    public void setPercentPosPoint2(Integer percentPosPoint2) {
        this.percentPosPoint2 = percentPosPoint2;
    }

    public Integer getPercentTarget1() {
        return percentTarget1;
    }

    public void setPercentTarget1(Integer percentTarget1) {
        this.percentTarget1 = percentTarget1;
    }

    public Integer getPercentTargetPoint1() {
        return percentTargetPoint1;
    }

    public void setPercentTargetPoint1(Integer percentTargetPoint1) {
        this.percentTargetPoint1 = percentTargetPoint1;
    }

    public Integer getPercentTarget2() {
        return percentTarget2;
    }

    public void setPercentTarget2(Integer percentTarget2) {
        this.percentTarget2 = percentTarget2;
    }

    public Integer getPercentTargetPoint2() {
        return percentTargetPoint2;
    }

    public void setPercentTargetPoint2(Integer percentTargetPoint2) {
        this.percentTargetPoint2 = percentTargetPoint2;
    }

    public Integer getMaxPoint() {
        return maxPoint;
    }

    public void setMaxPoint(Integer maxPoint) {
        this.maxPoint = maxPoint;
    }

    public String getMaxCorlor() {
        return maxCorlor;
    }

    public void setMaxCorlor(String maxCorlor) {
        this.maxCorlor = maxCorlor;
    }

    public Integer getMediumPoint() {
        return mediumPoint;
    }

    public void setMediumPoint(Integer mediumPoint) {
        this.mediumPoint = mediumPoint;
    }

    public String getMediumCorlor() {
        return mediumCorlor;
    }

    public void setMediumCorlor(String mediumCorlor) {
        this.mediumCorlor = mediumCorlor;
    }

    public Integer getMinPoint() {
        return minPoint;
    }

    public void setMinPoint(int minPoint) {
        this.minPoint = minPoint;
    }

    public String getMinCorlor() {
        return minCorlor;
    }

    public void setMinCorlor(String minCorlor) {
        this.minCorlor = minCorlor;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(Integer createdUser) {
        this.createdUser = createdUser;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(Integer updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getDeletedUser() {
        return deletedUser;
    }

    public void setDeletedUser(Integer deletedUser) {
        this.deletedUser = deletedUser;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CompanyConfigKpi)) {
            return false;
        }
        CompanyConfigKpi other = (CompanyConfigKpi) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.itt.msales.entity.CompanyConfigKpi[ id=" + id + " ]";
    }

}
