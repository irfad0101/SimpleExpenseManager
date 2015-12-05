package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Irfad Hussain on 12/3/2015.
 */
public class PresistentAccountDAO implements AccountDAO {

    private DBHandler dbHandler;

    public PresistentAccountDAO(DBHandler dbHandler){
        this.dbHandler = dbHandler;
    }

    @Override
    public List<String> getAccountNumbersList() {
        return dbHandler.getAccountNumList();
    }

    @Override
    public List<Account> getAccountsList() {
        return dbHandler.getAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Account account = dbHandler.getAccount(accountNo);
        if (account==null){ // get account method returns null if no matching account found
            String msg = "The accountNo" + accountNo + "does not exist!";
            throw new InvalidAccountException(msg);
        }
        return account;
    }

    @Override
    public void addAccount(Account account) {
        dbHandler.addAccount(account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!dbHandler.removeAccount(accountNo)){
            String msg = "Account number "+ accountNo + "does not exist!";
            throw new InvalidAccountException(msg);
        }
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        Account account = getAccount(accountNo);
        switch (expenseType){
            case EXPENSE:
                account.setBalance(account.getBalance()-amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance()+amount);
                break;
        }
        dbHandler.updateAccountBalance(account);
    }
}
