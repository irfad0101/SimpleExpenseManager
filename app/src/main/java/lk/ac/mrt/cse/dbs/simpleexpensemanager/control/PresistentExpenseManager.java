package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.DBHandler;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PresistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PresistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Irfad Hussain on 12/3/2015.
 */
public class PresistentExpenseManager extends ExpenseManager {

    private Context context;

    public PresistentExpenseManager(Context context){
        this.context = context;
        setup();
    }

    @Override
    public void setup() {
        DBHandler dbHandler = DBHandler.getInstance(context);
        PresistentAccountDAO presistentAccountDAO = new PresistentAccountDAO(dbHandler);
        setAccountsDAO(presistentAccountDAO);
        PresistentTransactionDAO presistentTransactionDAO = new PresistentTransactionDAO(dbHandler);
        setTransactionsDAO(presistentTransactionDAO);
        getAccountsDAO().addAccount(new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0));
        getAccountsDAO().addAccount(new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0));
    }
}
