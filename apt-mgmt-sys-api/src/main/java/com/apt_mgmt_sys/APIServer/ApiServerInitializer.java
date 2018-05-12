package com.apt_mgmt_sys.APIServer;

import com.apt_mgmt_sys.APIServer.models.*;
import com.apt_mgmt_sys.APIServer.models.transaction_creation_policies.BaseTCP;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.security.Role;
import com.apt_mgmt_sys.APIServer.services.*;
import com.apt_mgmt_sys.APIServer.services.transaction_creation_policies.BaseTCPEntityService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;
import com.apt_mgmt_sys.APIServer.services.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ApiServerInitializer implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    TaskService taskService;
    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;
    @Autowired
    EmployeeProfileService employeeProfileService;
    @Autowired
    CustomerProfileService customerProfileService;
    @Autowired
    RoomService roomService;
    @Autowired
    ContractService contractService;
    @Autowired
    BalanceService balanceService;
    @Autowired
    TransactionService transactionService;
    @Autowired
    BaseTCPEntityService tcpService;
    @Autowired
    FileEntityService fileService;
    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event){
        System.out.println("===== APPLICATION INITIALIZED ====");

        List<User> users = new ArrayList<>();
        try {
            users.add(userService.create("admin@ams.com","1234"));
            employeeProfileService.createWithJson(users.get(0), employeeProfileService.buildWithJson(
                    "Natcha", "Simsiri", "nat@gmail.com", "123 Kappa street", "08099999", "331338513513841", 999.0
            ));
            users.add(userService.create("user_a@ams.com", "1234"));
            employeeProfileService.createWithJson(users.get(1), employeeProfileService.buildWithJson(
                    "usera", "bbbb", "nat123@gmail.com", "123 Kappa street", "123151", "123123123", 992429.0
            ));
            users.add(userService.create("user_b@ams.com", "1234"));
            employeeProfileService.createWithJson(users.get(2), employeeProfileService.buildWithJson(
                    "userab", "bbbb", "nat123@gmail.com", "123 Kappa street", "3414131", "1351343151", 14412.0
            ));
            for(int i = 0; i < 20; i++){
                users.add(userService.create("user_" + (i+3) + "@ams.com", "1234"));
                employeeProfileService.createWithJson(users.get(i+3), employeeProfileService.buildWithJson(
                        "user_"+(i+3), "bbbb", (i+3)+"@gmail.com", (i+3)+" Kappa street", "123151", "123123123", 992429.0
                ));
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }
        User adminUser = users.get(0);
        Authority auth1 = authorityService.create(adminUser, Role.ADMIN);
        Authority auth2 = authorityService.create(adminUser, Role.EMPLOYEE);
        Authority auth3 = authorityService.create(users.get(1), Role.EMPLOYEE);
        Authority auth4 = authorityService.create(users.get(2), Role.EMPLOYEE);
        for(int i = 3; i < users.size(); i++){
            authorityService.create(users.get(i), Role.EMPLOYEE);
        }

        System.out.println(adminUser);
//        List<Authority> auths = authorityService.getAuthoritiesbyUser(adminUser);

        CustomerProfileService.makeTestData(customerProfileService);
        RoomService.initializeData(roomService);
        List<Contract> contracts = new ArrayList<>();
        try { contracts = ContractService.makeTestData(contractService, roomService, customerProfileService); }
        catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        contracts.forEach(x -> System.out.println(x));
        try {
            BalanceService.makeTestData(balanceService, transactionService, userService);
            TransactionService.makeTestData(transactionService, balanceService, userService);
        } catch (Exception e){
            e.printStackTrace();
        }

        BaseTCPEntityService.makeTestData(tcpService);

        FileEntityService.makeTestData(fileService);
    }


}
