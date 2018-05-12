package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import com.apt_mgmt_sys.APIServer.repositories.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CustomerProfileService {
    @Autowired
    CustomerProfileRepository customerProfileRepository;
    public List<CustomerProfile> getAll(){
        List<CustomerProfile> Out = new ArrayList<>();
        customerProfileRepository.findAll().forEach(x -> Out.add(x));
        return Out;
    }

    public CustomerProfile getById(Long id){
        return customerProfileRepository.findOne(id);
    }

    public CustomerProfile getByCitizenId(String citizenId){
        return customerProfileRepository.getCustomerProfileByCitizenId(citizenId);
    }

    public CustomerProfile create(Map<String, Object> json){
        CustomerProfile profile = new CustomerProfile();
        Utilities.updateBlock("firstname", json, String.class, x -> profile.setFirstname(x)).required();
        Utilities.updateBlock("lastname", json, String.class, x -> profile.setLastname(x)).required();
        Utilities.updateBlock("email", json, String.class, x -> profile.setEmail(x));
        Utilities.updateBlock("phoneNumber", json, String.class, x -> profile.setPhoneNumber(x));
        Utilities.updateBlock("citizenId", json, String.class, x -> profile.setCitizenId(x)).required();
        Utilities.updateBlock("address", json, String.class, x -> profile.setAddress(x));
        Utilities.updateBlock("remark", json, String.class, x -> profile.setRemark(x));
        profile.setEnabled(true);
        return customerProfileRepository.save(profile);
    }

    public CustomerProfile create(String firstname, String lastname, String email,
                                  String phoneNumber, String citizenId, String address){
        CustomerProfile profile = new CustomerProfile(firstname, lastname, email, phoneNumber, citizenId, address);
        return customerProfileRepository.save(profile);
    }

    public CustomerProfile updateById(Long id, Map<String, Object> json){
        CustomerProfile profile = getById(id);
        if (profile == null) throw new IllegalArgumentException("Unable to find CustomerProfile for id : " + id);
        Utilities.updateBlock("firstname", json, String.class, x -> profile.setFirstname(x));
        Utilities.updateBlock("lastname", json, String.class, x -> profile.setLastname(x));
        Utilities.updateBlock("email", json, String.class, x -> profile.setEmail(x));
        Utilities.updateBlock("phoneNumber", json, String.class, x -> profile.setPhoneNumber(x));
        Utilities.updateBlock("citizenId", json, String.class, x -> profile.setCitizenId(x));
        Utilities.updateBlock("address", json, String.class, x -> profile.setAddress(x));
        Utilities.updateBlock("remark", json, String.class, x -> profile.setRemark(x));
        return customerProfileRepository.save(profile);
    }

    public CustomerProfile removeById(Long id){
        CustomerProfile profile = getById(id);
        if (profile == null) throw new IllegalArgumentException("Unable to find CustomerProfile for id : " + id);
        profile.setEnabled(false);
        return customerProfileRepository.save(profile);

    }

    public static List<CustomerProfile> makeTestData(CustomerProfileService service){
        List<CustomerProfile> cps = new ArrayList<>();
        for(int i = 0; i < 14; i++){
            Map<String, Object> h = new HashMap<>();
            h.put("firstname", "Bruce_"+i);
            h.put("lastname", "Wayne_"+i);
            h.put("phoneNumber", "089990"+i);
            h.put("email", "iambatman_"+i+"@gmail.com");
            h.put("citizenId", "3331300000"+i);
            h.put("address", i + " Kappa Street");
            h.put("remark", "remark " + i);
            cps.add(service.create(h));
        }
        return cps;
    }
}
