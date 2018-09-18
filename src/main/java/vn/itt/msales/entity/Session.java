/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import vn.itt.msales.common.json.validator.MsalesValidator;

/**
 *
 * @author vtm036
 */
@Entity
@Table(name = "`session`")
@NamedQueries({
    @NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s"),
    @NamedQuery(name = "Session.findById", query = "SELECT s FROM Session s WHERE s.id = :id"),
    @NamedQuery(name = "Session.findByUserId", query = "SELECT s FROM Session s WHERE s.userId = :userId"),
    @NamedQuery(name = "Session.findByToken", query = "SELECT s FROM Session s WHERE s.token = :token"),
    @NamedQuery(name = "Session.findByLastAccessedTime", query = "SELECT s FROM Session s WHERE s.lastAccessedTime = :lastAccessedTime"),
    @NamedQuery(name = "Session.findByCreatedAt", query = "SELECT s FROM Session s WHERE s.createdAt = :createdAt"),
    @NamedQuery(name = "Session.findByCreatedUser", query = "SELECT s FROM Session s WHERE s.createdUser = :createdUser"),
    @NamedQuery(name = "Session.findByUpdatedAt", query = "SELECT s FROM Session s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Session.findByUpdatedUser", query = "SELECT s FROM Session s WHERE s.updatedUser = :updatedUser"),
    @NamedQuery(name = "Session.findByDeletedAt", query = "SELECT s FROM Session s WHERE s.deletedAt = :deletedAt"),
    @NamedQuery(name = "Session.findByDeletedUser", query = "SELECT s FROM Session s WHERE s.deletedUser = :deletedUser"),
    @NamedQuery(name = "Session.updateLastAccessedTime", query = "UPDATE  Session s SET s.lastAccessedTime = :lastAccessedTime WHERE s.token = :token")
})
public class Session implements Serializable {
    private static final long serialVersionUID = -3419362133448243177L;

    @Id
    @Column(name = "ID", unique = true, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_SESSION_ID")
    private Integer id;

    @Column(name = "USER_ID", nullable = false)
    private int userId;

    @Size(min = 1, max = 256)
    @Column(nullable = false, length = 256)
    private String token;

    @Column(name = "LAST_ACCESSED_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessedTime;

    @Column(name = "CREATED_AT", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "CREATED_USER", nullable = false)
    private int createdUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_AT", length = 19)
    @JsonIgnore
    private Date updatedAt;

    @Column(name = "UPDATED_USER", nullable = false)
    @NotNull(message = MsalesValidator.UPDATED_USER_NULL)
    private Integer updatedUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DELETED_AT", length = 19)
    @JsonIgnore
    private Date deletedAt;

    @Column(name = "DELETED_USER", nullable = false)
    @NotNull(message = MsalesValidator.DELETED_USER_NULL)
    private Integer deletedUser;

    @Column(name = "USER_ROLES_ID", nullable = false)
    private int userRolerId;

    public Session() {
    }

    public Session(Integer id) {
        this.id = id;
    }

    public Session(Integer id, int userId, String token, Date lastAccessedTime, Date createdAt, int createdUser, Date updatedAt, int updatedUser, Date deletedAt, int deletedUser) {
        this.id = id;
        this.userId = userId;
        this.token = token;
        this.lastAccessedTime = lastAccessedTime;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public int getUserRolerId() {
        return userRolerId;
    }

    public void setUserRolerId(int userRolerId) {
        this.userRolerId = userRolerId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Date lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(int createdUser) {
        this.createdUser = createdUser;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(int updatedUser) {
        this.updatedUser = updatedUser;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getDeletedUser() {
        return deletedUser;
    }

    public void setDeletedUser(int deletedUser) {
        this.deletedUser = deletedUser;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "vn.itt.msales.entity.Session[ id=" + id + " ]";
    }

}
