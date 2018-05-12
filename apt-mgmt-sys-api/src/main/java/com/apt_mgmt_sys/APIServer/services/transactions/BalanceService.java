package com.apt_mgmt_sys.APIServer.services.transactions;

import com.apt_mgmt_sys.APIServer.Utilities;
import com.apt_mgmt_sys.APIServer.constants.TransactionState;
import com.apt_mgmt_sys.APIServer.models.transactions.Balance;
import com.apt_mgmt_sys.APIServer.models.transactions.Transaction;
import com.apt_mgmt_sys.APIServer.repositories.BalanceRepository;
import com.apt_mgmt_sys.APIServer.services.UserService;
import jdk.nashorn.internal.runtime.options.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BalanceService {
    @Autowired
    private BalanceRepository balanceRepository;


    public List<Balance> getAll(){
        return Utilities.iterableToList(balanceRepository.findAll(), Balance.class);
    }

    public Balance getById(Long id){
        return balanceRepository.findOne(id);
    }

    public Balance create(String name, String remark){
        Map<String, Object> json = new HashMap<>();
        json.put("name", name);
        json.put("remark", remark);
        return create(json);
    }

    public Balance create(Map<String, Object> json){
        Balance balance = new Balance();
        Utilities.updateBlock("name", json, String.class, x -> balance.setName(x)).required();
        Utilities.updateBlock("remark", json, String.class, x -> balance.setRemark(x));

        return save(balance);
    }

    public Balance updateById(Long id, Map<String, Object> json){
        Balance balance = getById(id);
        if (balance == null) throw new IllegalArgumentException("unable to find balance id : " + id);
        Utilities.updateBlock("name", json, String.class, x -> balance.setName(x));
        Utilities.updateBlock("remark", json, String.class, x -> balance.setRemark(x));
        Utilities.updateBlock("state", json, String.class, x -> balance.setState(TransactionState.valueOf(x)));
        Utilities.updateBlock("latestTransactionId", json, Number.class, x -> balance.setLatestTransactionId(x.longValue()));
        return save(balance);

    }

    public Balance setEnabled(Long id, boolean enabled){
        Balance balance = getById(id);
        if (balance == null) throw new IllegalArgumentException("unable to find balance id : " + id);
        return setEnabled(balance, enabled);
    }

    public Balance setEnabled(Balance balance, boolean enabled){
        balance.setEnabled(enabled);
        return save(balance);
    }
    public void removeById(Long id){
        balanceRepository.delete(id);
    }

    public Balance save(Balance balance){
        return this.balanceRepository.save(balance);
    }

    public Balance updateCachedAmount(Balance balance, List<Transaction> transactions) {
        if (balance == null) throw new IllegalArgumentException("Balance is null when updating CachedAmount");
        Optional<Double> amountOption = transactions
                .stream()
                .filter(
                    x -> x.getEnabled() &&
                        x.getBalance().getId().equals(balance.getId())
                )
                .map(x -> { System.out.printf("amount: %s enabled: %s balanceId: %s\n", x.getAmount(), x.getEnabled(), x.getBalance().getId()); return x.getAmount() == null ? 0.0 : x.getAmount(); })
                .reduce((x, y) -> x + y);


        balance.setCachedAmount(amountOption.isPresent() ? amountOption.get() : 0.0);
        return save(balance);
    }

    public Balance updateCachedAmountPerTransaction(Balance balance, Double amount){
        if (balance == null) throw new IllegalArgumentException("Balance is null when updating CachedAmount");
        if (amount != null){
            balance.setCachedAmount(balance.getCachedAmount() + amount);
        }
        return save(balance);
    }

    public Balance updateLatestTransactionId(Balance balance, Transaction transaction){
        if (balance == null) throw new IllegalArgumentException("Balance is null when updating CachedAmount");
        if (transaction == null) throw new IllegalArgumentException("Transaction is null when updating CachedAmount");
        if (transaction.getId() != null){
            balance.setLatestTransactionId(transaction.getId());
        }
        return save(balance);
    }

    public static List<Balance> makeTestData(BalanceService balanceService, TransactionService transactionService, UserService userService) throws Exception{
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        List<Balance> balances = new ArrayList<>();
//        Balance balance = balanceService.create("Balance A", "dummy balane");
//        List<Transaction> b1_tvec = new ArrayList<>();
//        List<User> users = userService.getUsers();
//        Map<String, Object> json = new HashMap<>();
//        json.put("balanceId", balance.getId());
//        json.put("userId", users.get(0).getId());
//        json.put("targetDate", sdf.parse("11/09/2017").getTime());
//        json.put("title", "transaction-A");
//        json.put("remark", "no remarks this time");
//        json.put("amount", 322.00);
//        Transaction t1 = transactionService.create(json);
//
//        b1_tvec.add(t1);
//        balances.add(balance);
//        System.out.println(balance);
//        System.out.println(b1_tvec);
//        return balances;
        return null;
    }
}
