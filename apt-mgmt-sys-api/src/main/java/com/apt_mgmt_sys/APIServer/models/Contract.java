package com.apt_mgmt_sys.APIServer.models;

import com.apt_mgmt_sys.APIServer.constants.ContractType;
import com.apt_mgmt_sys.APIServer.interfaces.ITransactionService;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Contract {
    @Id
    @GeneratedValue Long id;

    @OneToOne
    private CustomerProfile customerProfile;
    @OneToOne
    private Room bookedRoom;

    private Date startDate;
    private Date endDate;
    private Boolean enabled;
    private ContractType contractType;
    private Boolean active;
    private String remark;

    public Contract(){
        this.customerProfile = null;
        this.bookedRoom = null;
        this.startDate = null;
        this.endDate = null;
        this.enabled = false;
        this.active = false;
        this.contractType = null;
        this.remark = "";
    }

    public Contract(CustomerProfile customerProfile, Room room, Date startDate, Date endDate, ContractType contractType){
        this.customerProfile = customerProfile;
        this.bookedRoom = room;
        this.startDate =  startDate;
        this.endDate = endDate;
        this.contractType = contractType;
        this.active = false;
        this.remark = "";

    }

    public Contract(CustomerProfile customerProfile, Room room, Date startDate, Date endDate, ContractType contractType, String remark){
        this.customerProfile = customerProfile;
        this.bookedRoom = room;
        this.startDate =  startDate;
        this.endDate = endDate;
        this.contractType = contractType;
        this.active = false;
        this.remark = "";
        this.remark = remark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerProfile getCustomerProfile() {
        return customerProfile;
    }

    public void setCustomerProfile(CustomerProfile customerProfile) {
        this.customerProfile = customerProfile;
    }

    public Room getBookedRoom() {
        return bookedRoom;
    }

    public void setBookedRoom(Room bookedRoom) {
        this.bookedRoom = bookedRoom;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public ContractType getContractType() {
        return contractType;
    }

    public void setContractType(ContractType contractType) {
        this.contractType = contractType;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return String.format("CONTRACT[roomNumber: %s, userProfile: %s %s, start: %s, end: %s, active: %s",
                    bookedRoom.getRoomNumber(), customerProfile.getFirstname(), customerProfile.getLastname(),
                    sdf.format(startDate), sdf.format(endDate), active);
        } catch (Exception e){
            return "[Broken contract " + this.getId() + "]";
        }

    }
}
