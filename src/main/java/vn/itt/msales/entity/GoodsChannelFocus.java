/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author ChinhNQ
 */
@Entity
@Table(name = "goods_channel_focus")
public class GoodsChannelFocus implements Serializable {
    private static final long serialVersionUID = 1601034479799385837L;

    @Column(name = "ID", unique = true, nullable = false)
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SEQ_ID")
    @SequenceGenerator(name = "SEQ_ID", sequenceName = "SEQ_GOODS_CHANNEL_FOCUS_ID")
    private Integer id;

    @Transient
    private Integer goodsId;

    @Transient
    private Integer channelId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GOODS_ID", nullable = false)
    private Goods goodss;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CHANNEL_ID", nullable = false)
    private Channel channels;

    @NotNull
    @Column(name = "CREATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "CREATED_USER")
    private int createdUser;

    @Column(name = "UPDATED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Column(name = "UPDATED_USER")
    private int updatedUser;

    @Column(name = "DELETED_AT")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;

    @Column(name = "DELETED_USER")
    private int deletedUser;

    public GoodsChannelFocus() {
    }

    public GoodsChannelFocus(Integer id, Integer goodsId, Integer channelId, Date createdAt, int createdUser, Date updatedAt, int updatedUser, Date deletedAt, int deletedUser) {
        this.id = id;
        this.goodsId = goodsId;
        this.channelId = channelId;
        this.createdAt = createdAt;
        this.createdUser = createdUser;
        this.updatedAt = updatedAt;
        this.updatedUser = updatedUser;
        this.deletedAt = deletedAt;
        this.deletedUser = deletedUser;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    public Integer getGoodsId() {
        return goodsId;
    }

    @JsonSetter
    public void setGoodsId(Integer goodsId) {
        this.goodsId = goodsId;
        Goods goods = new Goods();
        goods.setId(goodsId);
        this.goodss = goods;
    }

    @JsonIgnore
    public Integer getChannelId() {
        return channelId;
    }

    @JsonSetter
    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
        Channel channel = new Channel();
        channel.setId(channelId);
        this.channels = channel;
    }

    public Goods getGoodss() {
        return goodss;
    }

    public void setGoodss(Goods goodss) {
        this.goodss = goodss;
    }

    public Channel getChannels() {
        return channels;
    }

    public void setChannels(Channel channels) {
        this.channels = channels;
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

}
