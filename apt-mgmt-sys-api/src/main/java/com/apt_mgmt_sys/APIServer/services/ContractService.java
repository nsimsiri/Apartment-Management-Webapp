package com.apt_mgmt_sys.APIServer.services;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.ContractType;
import com.apt_mgmt_sys.APIServer.constants.PageRequest;
import com.apt_mgmt_sys.APIServer.constants.TransactionType;
import com.apt_mgmt_sys.APIServer.interfaces.IBalanceEntityEdge;
import com.apt_mgmt_sys.APIServer.models.Contract;
import com.apt_mgmt_sys.APIServer.models.CustomerProfile;
import com.apt_mgmt_sys.APIServer.models.Room;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;

import com.apt_mgmt_sys.APIServer.repositories.ContractRepository;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceEntityEdgeService;
import com.apt_mgmt_sys.APIServer.services.transactions.BalanceService;

import com.apt_mgmt_sys.APIServer.spec.ContractSpecifications;
import javafx.util.Pair;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;

import javax.naming.OperationNotSupportedException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private BalanceService balanceService;
    @Autowired
    private BalanceEntityEdgeService balanceEntityEdgeService;
    @Autowired
    private CustomerProfileService profileService;

    public List<Contract> getAll(){
        List<Contract> contracts = new ArrayList<>();
        contractRepository.findAll().forEach( x -> contracts.add(x));
        return contracts;
    }

    public List<Contract> query(Map<String, Object> json){
        Number from = Utilities.getFieldFromJSON(json, "from", Number.class);
        Number to = Utilities.getFieldFromJSON(json, "to", Number.class);
        Number profileId = Utilities.getFieldFromJSON(json, "profileId", Number.class);
        String roomNumber = Utilities.getFieldFromJSON(json, "roomNumber", String.class);
        Room room = Utilities.getFieldFromJSON(json, "room", Room.class);
        String customerName = Utilities.getFieldFromJSON(json, "customerName", String.class);

        Number offset = Utilities.getFieldFromJSON(json, "offset", Number.class);
        Number limit = Utilities.getFieldFromJSON(json, "limit", Number.class);
        String sortField = Utilities.getFieldFromJSON(json, "sortField", String.class);
        Number sortDirection = Utilities.getFieldFromJSON(json, "sortDirection", Number.class);

        Date fromDate = from != null ? new Date(from.longValue()) : Utilities.MIN_DATE();
        Date toDate = from != null ? new Date(to.longValue()) : Utilities.MAX_DATE();
        int dateCheck = Utilities.compareDate(fromDate, toDate);
        if (dateCheck != -1) throw new IllegalArgumentException(String.format("from(Date) must be before to(Date) from=%s to=%s", fromDate, toDate));
        Specifications specs = Specifications.where(ContractSpecifications.contractWithinDateRange(fromDate, toDate));
        if (profileId!=null) {
            CustomerProfile profile = profileService.getById(profileId.longValue());
            if (profile == null) return new ArrayList<>();
            specs = specs.and(ContractSpecifications.findByCustomerProfile(profile));
        } else if (customerName!=null&&customerName.length()>0){
            specs = specs.and(ContractSpecifications.findByNameCustomerProfileFirstOrLastName(customerName));
        }
        if (room!=null){
            specs = specs.and(ContractSpecifications.findByBookedRoom(room));
        } else if(roomNumber!=null && roomNumber.length()>0){
            specs = specs.and(ContractSpecifications.findByBookedRoomName(roomNumber));
        }

        Pageable pageRequest = PageRequest.buildWithOrder(offset!=null ? offset.intValue() : 0,
                                                          limit!=null ? limit.intValue() : Integer.MAX_VALUE,
                                                          sortField,
                                                          sortDirection!=null ? sortDirection.intValue() : -1);

        Page<Contract> page = contractRepository.findAll(specs, pageRequest);
        if (page == null) throw new IllegalArgumentException("No page");

        return page.getContent();

    }

    public List<Contract> getByCustomerProfile(CustomerProfile profile, PageRequest pageRequest){
        return contractRepository.getContractsByCustomerProfile(profile, pageRequest);
    }

    public Contract getById(Long id){
        return contractRepository.findOne(id);
    }


    public static boolean contractsWithinDatesFilter(Date reqStart, Date conStart, Date reqEnd, Date conEnd){
        int ss = Utilities.compareDate(reqStart, conStart); //
        int se = Utilities.compareDate(reqStart, conEnd);
        int es = Utilities.compareDate(reqEnd, conStart);
        int ee = Utilities.compareDate(reqEnd, conEnd);
        boolean a = (ss == 1  || ss == 0) && se == -1; // exclusive => (reqStart > curStart || reqStart == curStart) && reqStart > e
        boolean b = es == 1 && (ee == -1 || ee == 0); //exclusive
        boolean c = (ss == -1 || ss == 0) && (ee == 1 || ee == 0);
        return a||b||c;
    }

    // get Contracts during a time for this room.
    public List<Contract> getContractsForRoomWithinDateRange(Room room, Date startDate, Date endDate){
        int cDate = Utilities.compareDate(startDate, endDate);
        if (cDate == 1 || cDate == 0) throw new IllegalArgumentException("start date must be before end date");
        Map<String, Object> jsonQuery = new TreeMap<>();
        jsonQuery.put("room", room);
        jsonQuery.put("from", startDate.getTime());
        jsonQuery.put("to", endDate.getTime());
        List<Contract> contracts = query(jsonQuery);
        return contracts;
    }

    public List<Contract> getContractsWithinDate(Date startDate, Date endDate){
        int cDate = Utilities.compareDate(startDate, endDate);
        if (cDate == 1 || cDate == 0) throw new IllegalArgumentException("start date must be before end date");
        Map<String, Object> queryJson = new HashMap<>();
        queryJson.put("from", startDate.getTime());
        queryJson.put("to", endDate.getTime());
        List<Contract> contracts = query(queryJson);
        return contracts;
    }

    public Contract create(Map<String, Object> json){
        // (1) CREATE CONTRACT;
        final Contract contract = new Contract();

        Utilities.updateBlock("customerProfile", json, CustomerProfile.class, (CustomerProfile cp) -> {
            if (cp == null) throw new IllegalArgumentException ("[ContractService] Unable to parse CustomerProfile from json");
            contract.setCustomerProfile(cp);
        }).required();

        Utilities.updateBlock("bookedRoom", json, Room.class, (Room r) -> {
           contract.setBookedRoom(r);
        }).required();

        Utilities.updateBlock("contractType", json, Object.class, ct -> {
            ContractType contractType = null;
            if (ct instanceof ContractType){
                contractType = (ContractType)ct;
            } else if (ct instanceof String){
                contractType = ContractType.valueOf((String)ct);
            }
            contract.setContractType(contractType);
        }).required();

        Utilities.updateBlock("startDate", json, Number.class, startDate -> {
                contract.setStartDate(new Date(startDate.longValue()));
        }).required();
        Utilities.updateBlock("endDate", json, Number.class, endDate -> {
            contract.setEndDate(new Date(endDate.longValue()));
        }).required();
        Utilities.updateBlock("active", json, Boolean.class, active -> contract.setActive(active));
        Utilities.updateBlock("remark", json, String.class, remark -> contract.setRemark(remark));

        if (!Utilities.validateDateAfterToday(contract.getStartDate()))  throw new IllegalArgumentException("Please set Start to be at least today");
        if (!Utilities.validateDateAfterToday(contract.getEndDate()))  throw new IllegalArgumentException("Please set End Date to be at least today");

        List<Contract> contractsForRoomWithinDates = getContractsForRoomWithinDateRange(contract.getBookedRoom(), contract.getStartDate(), contract.getEndDate());
        if (contractsForRoomWithinDates.size() > 0)
            throw new IllegalArgumentException("found " + contractsForRoomWithinDates.size() + " contracts with overlapping dates with ids : " +
            Arrays.toString(contractsForRoomWithinDates.stream().mapToLong(x -> x.getId()).toArray()));

        contract.setEnabled(true);
        contract.setActive(false);
        Contract savedContract = contractRepository.save(contract);
        // (2) CREATE BALANCE
        CustomerProfile customer = savedContract.getCustomerProfile();
        Balance balance = balanceService.create(String.format("%s %s Balance", customer.getFirstname(), customer.getLastname()), "Automatically created balance");
        // (3) CREATE BALANCE-CONTRACT EDGE
        Map<String, Object> balanceContractJson = Utilities.buildMap(
                new Pair[]{
                        new Pair("balance", balance),
                        new Pair("entity", savedContract),
                }
        );
        balanceEntityEdgeService.create(balanceContractJson, TransactionType.CONTRACT);
        return savedContract;
    }

    public Contract updateById(Long id, Map<String, Object> json){
        Contract contract = getById(id);
        if (contract == null) throw new IllegalArgumentException("[ContractService] unable to find contract with id : " + id);
        Utilities.updateBlock("customerProfile", json, CustomerProfile.class, (CustomerProfile cp) -> {
            if (cp == null) throw new IllegalArgumentException ("[ContractService] Unable to parse CustomerProfile from json");
            contract.setCustomerProfile(cp);
        });

        Utilities.updateBlock("bookedRoom", json, Room.class, (Room r) -> {
            if (r == null) throw new IllegalArgumentException("[ContractService] Unable to parse Room from json");
            contract.setBookedRoom(r);
        });

        Utilities.updateBlock("contractType", json, String.class, ct -> contract.setContractType(ContractType.valueOf(ct)));
        Utilities.updateBlock("startDate", json, Number.class, startDate -> contract.setStartDate(new Date(startDate.longValue())));
        Utilities.updateBlock("endDate", json, Number.class, endDate -> contract.setEndDate(new Date(endDate.longValue())));
        Utilities.updateBlock("active", json, Boolean.class, active -> contract.setActive(active));
        Utilities.updateBlock("remark", json, String.class, remark -> contract.setRemark(remark));

        List<Contract> contractsForRoomWithinDates =
                getContractsForRoomWithinDateRange(contract.getBookedRoom(), contract.getStartDate(), contract.getEndDate())
                .stream().filter(x -> x.getId()!=id).collect(Collectors.toList());

        if (contractsForRoomWithinDates.size() > 0)
            throw new IllegalArgumentException("found " + contractsForRoomWithinDates.size() +"contracts with overlapping dates with ids : " +
                    Arrays.toString(contractsForRoomWithinDates.stream().mapToLong(x -> x.getId()).toArray()));

        if (!Utilities.validateDateAfterToday(contract.getStartDate()))  throw new IllegalArgumentException("Please set Start to be at least today");
        if (!Utilities.validateDateAfterToday(contract.getEndDate()))  throw new IllegalArgumentException("Please set End Date to be at least today");

        contract.setEnabled(true);
        return contractRepository.save(contract);
    }

    public boolean deleteById(Long id) throws OperationNotSupportedException{
        throw new OperationNotSupportedException("operation not needed");
//        contractRepository.delete(id);
//        return true;
    }

    public Contract setEnable(Long id, boolean enable){
        Contract contract = getById(id);
        if (contract == null) return null;
        IBalanceEntityEdge<?> balanceEntityEdge = balanceEntityEdgeService.getByEntity(contract, TransactionType.CONTRACT);
        Balance balance = balanceEntityEdge.getBalance();
        balanceService.setEnabled(balance, enable);
        contract.setEnabled(enable);
        return contractRepository.save(contract);
    }


    public static List<Contract> makeTestData(ContractService contractService, RoomService roomService, CustomerProfileService customerProfileService) throws Exception{
        List<Contract> contracts = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        List<Room> rooms = roomService.getAll();
        List<CustomerProfile> profiles = customerProfileService.getAll();
        Map<String, Object> json = new HashMap<>();
        Date now = new Date();
        Calendar curDate = Calendar.getInstance();
        curDate.setTime(curDate.getTime());
        json.put("bookedRoom", rooms.get(0));
        json.put("customerProfile", profiles.get(0));
        json.put("contractType", ContractType.SHORT_TERM);
        json.put("startDate",sdf.parse("10/10/2018").getTime());
        json.put("endDate", sdf.parse("12/10/2018").getTime());
        json.put("remark", "remark 123");
        Contract c1 = contractService.create(json);
        json.put("bookedRoom", rooms.get(0));
        json.put("customerProfile", profiles.get(1));
        json.put("contractType", ContractType.LONG_TERM);
        json.put("startDate",sdf.parse("13/10/2018").getTime());
        json.put("endDate", sdf.parse("15/10/2018").getTime());
        json.put("remark", "");
        Contract c2 = contractService.create(json);
        json.put("bookedRoom", rooms.get(0));
        json.put("customerProfile", profiles.get(1));
        json.put("contractType", ContractType.LONG_TERM);
        json.put("startDate", now.getTime());
        json.put("endDate", sdf.parse(String.format("%s/%s/%s", curDate.get(Calendar.DATE)+5,
                curDate.get(Calendar.MONTH)+1, curDate.get(Calendar.YEAR))).getTime());
        json.put("remark", "");
        Contract c33 = contractService.create(json);
        try {
            json.put("bookedRoom", rooms.get(0));
            json.put("customerProfile", profiles.get(3));
            json.put("contractType", ContractType.SHORT_TERM);
            json.put("startDate",sdf.parse("10/10/2018").getTime());
            json.put("endDate", sdf.parse("15/10/2018").getTime());
            Contract c3 = contractService.create(json);
            contracts.add(c3);
            System.out.println(Utilities.FAIL_STR("[TEST FAILED] can add " + c3));
        } catch (Exception e){
            System.out.println(Utilities.SUCCESS_STR("[TEST PASSED 1/3] EXCEPTION nicely got caught !: " + e.getMessage()));

        }
        try {
            json.put("bookedRoom", rooms.get(0));
            json.put("customerProfile", profiles.get(4));
            json.put("contractType", ContractType.SHORT_TERM);
            json.put("startDate",sdf.parse("13/10/2018").getTime());
            json.put("endDate", sdf.parse("20/10/2018").getTime());
            json.put("remark", "remark 123");
            Contract c3 = contractService.create(json);
            contracts.add(c3);
            System.out.println(Utilities.FAIL_STR("[TEST FAILED] can add " + c3));
        } catch (Exception e){
            System.out.println(Utilities.SUCCESS_STR("[TEST PASSED 2/3] EXCEPTION nicely got caught !: " + e.getMessage()));

        }

        try {
            json.put("bookedRoom", rooms.get(0));
            json.put("customerProfile", profiles.get(4));
            json.put("contractType", ContractType.SHORT_TERM);
            json.put("startDate",sdf.parse("9/10/2018").getTime());
            json.put("endDate", sdf.parse("20/10/2018").getTime());
            json.put("remark", "remark 123");
            Contract c3 = contractService.create(json);
            contracts.add(c3);
            System.out.println(Utilities.FAIL_STR("[TEST FAILED] can add " + c3));
        } catch (Exception e){
            System.out.println(Utilities.SUCCESS_STR("[TEST PASSED 3/3] EXCEPTION nicely got caught !: " + e.getMessage()));

        }
        contracts.add(c1);
        contracts.add(c2);
        return contracts;
    }


}
