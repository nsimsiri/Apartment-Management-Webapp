package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.models.EmployeeProfile;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.repositories.EmployeeProfileRepository;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEmployeeEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class EmployeeProfileService {
    @Autowired
    private EmployeeProfileRepository employeeProfileRepository;

    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;

    @Autowired
    private BalanceService balanceService;

    public List<EmployeeProfile> getAll(){
        return Utilities.iterableToList(employeeProfileRepository.findAll(), EmployeeProfile.class);
    }

    public EmployeeProfile getById(Long id){
        return employeeProfileRepository.findOne(id);
    }

    public EmployeeProfile getByUser(User user){
        return employeeProfileRepository.getByUser(user);
    }

    public EmployeeProfile createWithJson(User user, Map<String, Object> json){
        final EmployeeProfile profile = new EmployeeProfile();
        Utilities.updateBlock("firstname", json, String.class, (s)->profile.setFirstname(s));
        Utilities.updateBlock("lastname", json, String.class, (s)->profile.setLastname(s));
        Utilities.updateBlock("email", json, String.class, (s)->profile.setEmail(s));
        Utilities.updateBlock("address", json, String.class, (s)->profile.setAddress(s));
        Utilities.updateBlock("phoneNumber", json, String.class, (s)->profile.setPhoneNumber(s));
        Utilities.updateBlock("citizenId", json, String.class, (s)->profile.setCitizenId(s));
        Utilities.updateBlock("salary", json, Number.class, (s)->profile.setSalary(s.doubleValue()));
        profile.setValidEmployee(false);
        profile.setUser(user);
        EmployeeProfile savedProfile = employeeProfileRepository.save(profile);
        Balance balance = balanceService.create(String.format("%s %s Balance", savedProfile.getFirstname(), savedProfile.getLastname()), "Automatically created balance");
        balanceEntityEdgeService.create(Utilities.buildMap(
                new Pair[]{
                        new Pair("balance", balance),
                        new Pair("entity", savedProfile)
                }
        ), TransactionType.EMPLOYEE_PROFILE);
        return savedProfile;

    }

    public EmployeeProfile updateUserById(Long id, Map<String, Object> updateSchema){
        EmployeeProfile profile = getById(id);
        if (profile == null) return null;

        Utilities.updateBlock("firstname", updateSchema, String.class, (s)->profile.setFirstname(s));
        Utilities.updateBlock("lastname", updateSchema, String.class, (s)->profile.setLastname(s));
        Utilities.updateBlock("email", updateSchema, String.class, (s)->profile.setEmail(s));
        Utilities.updateBlock("address", updateSchema, String.class, (s)->profile.setAddress(s));
        Utilities.updateBlock("phoneNumber", updateSchema, String.class, (s)->profile.setPhoneNumber(s));
        Utilities.updateBlock("citizenId", updateSchema, String.class, (s)->profile.setCitizenId(s));
        Utilities.updateBlock("salary", updateSchema, Double.class, (s)->profile.setSalary(s));
        Utilities.updateBlock("enabled", updateSchema, Boolean.class, (s)->profile.setEnabled(s));
        Utilities.updateBlock("validEmployee", updateSchema, Boolean.class, (s)->profile.setValidEmployee(s));

        return employeeProfileRepository.save(profile);
    }

    public Map<String, Object> buildWithJson(String firstname, String lastname, String email, String address,
                                             String phoneNumber, String citizenId, Double salary){
        Map<String, Object> m = new HashMap<>();
        m.put("firstname", firstname);
        m.put("lastname", lastname);
        m.put("email", email);
        m.put("address", address);
        m.put("phoneNumber", phoneNumber);
        m.put("citizenId", citizenId);
        m.put("salary", salary);
        return m;
    }

    public EmployeeProfile remove(EmployeeProfile profile){
        if (profile == null) return null;
        profile.setEnabled(false);
        return employeeProfileRepository.save(profile);
    }

    public boolean deleteById(Long id){
        employeeProfileRepository.delete(id);
        return getById(id) == null;
    }
}
