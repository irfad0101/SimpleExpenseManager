package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Irfad Hussain on 12/3/2015.
 */
public class PresistentTransactionDAO implements TransactionDAO {

    private DBHandler dbHandler;

    public PresistentTransactionDAO(DBHandler dbHandler){
        this.dbHandler = dbHandler;
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        dbHandler.addTransaction(transaction);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return dbHandler.getAllTransactions();
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        return dbHandler.getPaginatedTransactions(limit);
    }
}
