package com.apt_mgmt_sys.APIServer.models.transactions;

import com.apt_mgmt_sys.APIServer.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue Long id;
    private Date targetDate;
    @CreatedBy
    private Date created;
    @LastModifiedBy
    private Date lastModified;

    private String title;
    private String remark;
    private Double amount;
    private Boolean enabled;
    private Boolean isNotificationPayment;

    @OneToOne
    private User creator;

    @OneToOne
    private Balance balance;

    public Transaction(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+100);
            this.targetDate = sdf.parse(String.format("%s/%s/%s",
                    cal.get(Calendar.DATE),
                    cal.get(Calendar.MONTH+1),
                    cal.get(Calendar.YEAR)));

        } catch (Exception e){
            e.printStackTrace();
            this.targetDate = null;
        }
        this.title = "";
        this.remark = "";
        this.creator = null;
        this.balance = null;
        this.amount = 0.0;
        this.enabled = true;
        this.isNotificationPayment = false;

    }

    public Transaction(Balance balance, User creator, Double amount,  String title, String remark, Date targetDate, Boolean isNotificationPayment){
        this.balance = balance;
        this.creator = creator;
        this.title = title;
        this.remark = remark;
        this.targetDate = targetDate;
        this.amount = amount;
        this.enabled = true;
        this.isNotificationPayment = isNotificationPayment;
    }

    public Double getAmount(){
        return this.amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Balance getBalance() {
        return balance;
    }

    public void setBalance(Balance balance) {
        this.balance = balance;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getIsNotificationPayment() {
        return isNotificationPayment;
    }

    public void setIsNotificationPayment(Boolean notificationPayment) {
        isNotificationPayment = notificationPayment;
    }

    @Override
    public String toString(){
        ObjectMapper om = new ObjectMapper();
        om.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return om.writeValueAsString(this);
        } catch (Exception e){
            e.printStackTrace();
            return "Transaction[ERROR]";
        }
    }
}
