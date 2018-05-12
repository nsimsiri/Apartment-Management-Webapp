package com.apt_mgmt_sys.APIServer.services.transactions;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.PageRequest;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.models.User;
import com.apt_mgmt_sys.APIServer.repositories.TransactionRepository;

import com.apt_mgmt_sys.APIServer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BalanceService balanceService;


    private static String LN = TransactionService.class.getSimpleName();
    public static String DEFAULT_ORDER_FIELD = "targetDate";
    public static Integer DEFAULT_ORDER_DIRECTION = 1;

    public List<Transaction> getAll() {
        return Utilities.iterableToList(transactionRepository.findAll());
    }

    public List<Transaction> getAll(PageRequest pageRequest){
        Page<Transaction> page = transactionRepository.findAll(pageRequest);
        if (page == null) throw new IllegalArgumentException("Unable to request for page with PageRequest: " + pageRequest);
        return page.getContent();
    }

    public Transaction getById(Long id){
        return transactionRepository.findOne(id);
    }

    public Transaction create(Balance balance, User user, Double amount, String title, String remark, Date targetDate, Boolean isNotificationPayment){
        if (user == null) throw new IllegalArgumentException(String.format("[%s]: null user", LN));
        Map<String, Object> json = new HashMap<>();
        json.put("user", user);
        json.put("balance", balance);
        json.put("title", title);
        json.put("amount", amount);
        json.put("remark", remark);
        json.put("targetDate", targetDate.getTime());
        json.put("isNotificationPayment", isNotificationPayment);
        return create(json);
    }

    public Transaction create(Map<String, Object> json){
        final Transaction transaction = new Transaction();
        Utilities.updateBlock("user", json, User.class, x -> transaction.setCreator(x));
        Utilities.updateBlock("balance", json, Balance.class, x -> transaction.setBalance(x));
        Utilities.updateBlock("title", json, String.class, x -> transaction.setTitle(x));
        Utilities.updateBlock("remark", json, String.class, x -> transaction.setRemark(x));
        Utilities.updateBlock("amount", json, Number.class, x -> transaction.setAmount(x.doubleValue()));
        Utilities.updateBlock("targetDate", json, Number.class, x -> transaction.setTargetDate(new Date(x.longValue())));
        Utilities.updateBlock("isNotificationPayment", json, Boolean.class, x -> transaction.setIsNotificationPayment(x));
        if(!Utilities.validateDateAfterToday(transaction.getTargetDate())) throw new IllegalArgumentException("Please set Target Date to be at least today");
        if (transaction.getCreator() == null){
            Number userId = (Number)Utilities.getFieldFromJSON(json, "userId", Number.class);
            if (userId== null) throw new IllegalArgumentException(String.format("[%s]: null User ID in json %s", LN, json));
            User creator = userService.getUserById(userId.longValue());
        if (creator == null) throw new IllegalArgumentException(String.format("[%s]: cannot get user with id: %s", LN, userId.longValue()));
            transaction.setCreator(creator);
        }
        if (transaction.getBalance() == null){
            Number balanceId = (Number) Utilities.getFieldFromJSON(json, "balanceId", Number.class);
            if (balanceId == null) throw new IllegalArgumentException(String.format("[%s]: null Balannc ID in json : %s", LN, json));
            Balance balance = balanceService.getById(balanceId.longValue());
            if (balance == null) throw new IllegalArgumentException(String.format("[%s]: unable to find balance with id : %s", LN, json));
            transaction.setBalance(balance);
        }

        final Transaction savedTransaction = save(transaction);
        if (savedTransaction .getEnabled()){
            Balance balance = savedTransaction .getBalance();
            balanceService.updateCachedAmountPerTransaction(balance, savedTransaction.getAmount());
            balanceService.updateLatestTransactionId(balance, savedTransaction);

        }

        return savedTransaction ;
    }

    public Transaction updateฺById(Long id, Map<String, Object> json){
        Transaction transaction = getById(id);
        if (transaction == null) throw new IllegalArgumentException(String.format("[%s]: cannot find transaction with id : %s", LN, id));
        Utilities.updateBlock("user", json, User.class, x -> transaction.setCreator(x));
        Utilities.updateBlock("balance", json, Balance.class, x -> transaction.setBalance(x));
        Utilities.updateBlock("title", json, String.class, x -> transaction.setTitle(x));
        Utilities.updateBlock("remark", json, String.class, x -> transaction.setRemark(x));
        Utilities.updateBlock("amount", json, Number.class, x ->  transaction.setAmount(x.doubleValue()));
        Utilities.updateBlock("targetDate", json, Number.class, x -> transaction.setTargetDate(new Date(x.longValue())));
        Utilities.updateBlock("isNotificationPayment", json, Boolean.class, x -> transaction.setIsNotificationPayment(x));

        return save(transaction);
    }

    public Transaction setEnabledById(Long id, Boolean enabled){
        Transaction transaction = getById(id);
        if (transaction == null) throw new IllegalArgumentException("can't find transaction with id " + id);
        transaction.setEnabled(enabled);
        Transaction savedTransaction = save(transaction);;
        if (!enabled){
            List<Transaction> transactions =
                    transactionRepository.findByBalanceAndEnabled(
                            transaction.getBalance(),
                            true,
                            new PageRequest(0, 100, new Sort(Sort.Direction.DESC, "created")));
//            transactions.stream().forEach(x -> System.out.println(x.getCreated()));
            balanceService.updateCachedAmountPerTransaction(transaction.getBalance(), -1.0 * transaction.getAmount());
            balanceService.updateLatestTransactionId(transaction.getBalance(), transactions.size() > 0 ? transactions.get(0) : null);

        }
        return savedTransaction;
    }

    public List<Transaction> listByBalance(Balance balance, Integer offset, Integer limit){
        return transactionRepository.findByBalanceOrderByTargetDateDesc(balance, new PageRequest(offset, limit));
    }

    public Transaction removeById(Long id){
        return null;
    }

    public Transaction save(Transaction transaction){
        return transactionRepository.save(transaction);
    }

    public static List<Transaction> makeTestData(TransactionService service, UserService userService){
        List<Transaction> trans=  new ArrayList<>();
        try {
            Map<String, Object> json = new HashMap<>();
            List<User> users = userService.getUsers();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            json.put("creator", users.get(0));
            json.put("title", "Contract payment number 1");
            json.put("remark", "no remark Keepo");
            json.put("targetDate", sdf.parse("27/11/2017").getTime());
            Transaction t1 = service.create(json);
            trans.add(t1);

        } catch (Exception e){
            e.printStackTrace();
        } finally {
            return trans;
        }
    }

    public static void makeTestData(TransactionService tservice, BalanceService bservice, UserService uservice){
        try {
            List<User> users = uservice.getUsers();
            List<Balance> balances = bservice.getAll();
            User userAdmin = users.get(0);
            Balance testBalance = balances.get(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            for(int i = 0; i < 30; i++){
                tservice.create(testBalance, userAdmin, (double)1000+39*i, "Test " + i, "no remarks " + i,
                        sdf.parse(String.format("%s/10/2018",(5+i)%30)), true);
            }
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }


}