package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Irfad Hussain on 12/4/2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    /* This class handles all the database operations*/

    public static final String DB_NAME = "ExpenseManager.db";
    /* column names and table name for accounts table which holds data about accounts */
    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String ACCOUNTS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String ACCOUNTS_COLUMN_BANK = "bank";
    public static final String ACCOUNTS_COLUMN_HOLDER = "holder";
    public static final String ACCOUNTS_COLUMN_BALANCE = "balance";
    /* column names and table name for transacts table which holds data about transactions */
    public static final String TRANSACTIONS_TABLE_NAME = "transacts";
    public static final String TRANSACTIONS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String TRANSACTIONS_COLUMN_TYPE = "type";
    public static final String TRANSACTIONS_COLUMN_AMOUNT = "amount";
    public static final String TRANSACTIONS_COLUMN_DATE = "transacDate";

    private static DBHandler dbHandler = null;

    private DBHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

    // singleton pattern
    public static DBHandler getInstance(Context context){
        if (dbHandler == null){
            synchronized (DBHandler.class){
                if (dbHandler==null){
                    dbHandler = new DBHandler(context);
                }
            }
        }
        return dbHandler;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // create tables
        db.execSQL("create table "+ACCOUNTS_TABLE_NAME+"("+ACCOUNTS_COLUMN_ACCOUNTNO+" text primary key,"+ACCOUNTS_COLUMN_BANK+" text not null,"+ACCOUNTS_COLUMN_HOLDER+" text not null,"+ACCOUNTS_COLUMN_BALANCE+" real not null);");
        db.execSQL("create table "+TRANSACTIONS_TABLE_NAME+"("+TRANSACTIONS_COLUMN_ACCOUNTNO+" text,"+TRANSACTIONS_COLUMN_TYPE+" integer not null,"+TRANSACTIONS_COLUMN_AMOUNT+" real not null,"+TRANSACTIONS_COLUMN_DATE+" text not null,foreign key("+TRANSACTIONS_COLUMN_ACCOUNTNO+") references "+ACCOUNTS_TABLE_NAME+"("+ACCOUNTS_COLUMN_ACCOUNTNO+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<String> getAccountNumList(){
        // returns all the account numbers as a list
        List<String> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("select "+ACCOUNTS_COLUMN_ACCOUNTNO+" from "+ACCOUNTS_TABLE_NAME+";",null);  // query
        results.moveToFirst();
        while (!results.isAfterLast()){
            // add each account numnber to list
            accounts.add(results.getString(results.getColumnIndex(ACCOUNTS_COLUMN_ACCOUNTNO)));
            results.moveToNext();
        }
        db.close();
        return accounts;
    }

    public List<Account> getAccounts(){
        // return all accounts as a list of account objects
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("select * from "+ACCOUNTS_TABLE_NAME+";",null);  // query
        results.moveToFirst();
        while (!results.isAfterLast()){
            // get data about each account from results and create an account object using them and add the object to list
            String accNo = results.getString(results.getColumnIndex(ACCOUNTS_COLUMN_ACCOUNTNO));
            String bank = results.getString(results.getColumnIndex(ACCOUNTS_COLUMN_BANK));
            String holder = results.getString(results.getColumnIndex(ACCOUNTS_COLUMN_HOLDER));
            double amount = results.getDouble(results.getColumnIndex(ACCOUNTS_COLUMN_BALANCE));
            accounts.add(new Account(accNo,bank,holder,amount));
            results.moveToNext();
        }
        db.close();
        return accounts;
    }

    public Account getAccount(String accountNO){
        // find a specific account, specified by account number
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + ACCOUNTS_TABLE_NAME + " where " + ACCOUNTS_COLUMN_ACCOUNTNO + "= '" + accountNO + "';", null);  // query
        result.moveToFirst();
        if (result.getCount()==0){ // if no account found return null.
            db.close();
            return null;
        }
        // if an account found create an account object using retrieved data and return it
        String accNo = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_ACCOUNTNO));
        String bank = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_BANK));
        String holder = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_HOLDER));
        double amount = result.getDouble(result.getColumnIndex(ACCOUNTS_COLUMN_BALANCE));
        db.close();
        return new Account(accNo,bank,holder,amount);
    }

    public void addAccount(Account account){
        // add given account information to database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contents = new ContentValues();
        contents.put(ACCOUNTS_COLUMN_ACCOUNTNO, account.getAccountNo());
        contents.put(ACCOUNTS_COLUMN_BANK,account.getBankName());
        contents.put(ACCOUNTS_COLUMN_HOLDER,account.getAccountHolderName());
        contents.put(ACCOUNTS_COLUMN_BALANCE,account.getBalance());
        db.insert(ACCOUNTS_TABLE_NAME,null,contents);
        db.close();
    }

    public boolean removeAccount(String accountNo){
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDel = db.delete(ACCOUNTS_TABLE_NAME,ACCOUNTS_COLUMN_ACCOUNTNO+" = ? ",new String[]{accountNo});
        db.close();
        if (rowsDel==0)     // no acocunt deleted
            return false;
        return true;
    }

    public void updateAccountBalance(Account account){
        // update the balance of a given account
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_COLUMN_BALANCE, account.getBalance());
        db.update(ACCOUNTS_TABLE_NAME,values,ACCOUNTS_COLUMN_ACCOUNTNO+" = ?",new String[]{account.getAccountNo()});
        db.close();
    }

    public void addTransaction(Transaction transaction){
        // add transactoin details to database
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TRANSACTIONS_COLUMN_ACCOUNTNO,transaction.getAccountNo());
        // ExpenseType is stored as an integer. 1 for income and -1 for expense
        values.put(TRANSACTIONS_COLUMN_TYPE,(transaction.getExpenseType()== ExpenseType.INCOME) ? 1 : -1);
        values.put(TRANSACTIONS_COLUMN_AMOUNT,transaction.getAmount());
        // date is stores as a formatted String.
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String date = sdf.format(transaction.getDate());
        values.put(TRANSACTIONS_COLUMN_DATE,date);
        db.insert(TRANSACTIONS_TABLE_NAME, null, values);
        db.close();
    }

    public List<Transaction> getAllTransactions(){
        // get all transaction and return them as a list
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("select * from " + TRANSACTIONS_TABLE_NAME + ";", null);
        results.moveToFirst();
        while (!results.isAfterLast()){
            // add each transaction detail to list
            String accoutNo = results.getString(results.getColumnIndex(TRANSACTIONS_COLUMN_ACCOUNTNO));
            ExpenseType type = (results.getInt(results.getColumnIndex(TRANSACTIONS_COLUMN_TYPE))==1)? ExpenseType.INCOME:ExpenseType.EXPENSE;
            double amount = results.getDouble(results.getColumnIndex(TRANSACTIONS_COLUMN_AMOUNT));
            // create a date object from formatted String that retrieved from database
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date = null;
            try {
                date = sdf.parse(results.getString(results.getColumnIndex(TRANSACTIONS_COLUMN_DATE)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            transactions.add(new Transaction(date,accoutNo,type,amount));
            results.moveToNext();
        }
        db.close();
        return transactions;
    }

    public List<Transaction> getPaginatedTransactions(int limit){
        // returns a list of most recent transactoins. number of transaction that is required is given as limit
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select count(*) from "+TRANSACTIONS_TABLE_NAME+";",null); // get number of transactions stored in database
        c.moveToFirst();
        int rowCount = c.getInt(0);
        if(rowCount<=limit){    // if number of transactions stored is less than limit return all transactions.
            db.close();
            return getAllTransactions();
        }else{     // if num of transaction exceeds the limit retrieve last limit number of transcactions and return them as a list
            List<Transaction> transactions = new ArrayList<>();
            c = db.rawQuery("select * from " + TRANSACTIONS_TABLE_NAME + " limit 10 offset "+Integer.toString(rowCount-10)+";", null);
            c.moveToFirst();
            while (!c.isAfterLast()){
                String accoutNo = c.getString(c.getColumnIndex(TRANSACTIONS_COLUMN_ACCOUNTNO));
                ExpenseType type = (c.getInt(c.getColumnIndex(TRANSACTIONS_COLUMN_TYPE))==1)? ExpenseType.INCOME:ExpenseType.EXPENSE;
                double amount = c.getDouble(c.getColumnIndex(TRANSACTIONS_COLUMN_AMOUNT));
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date date = null;
                try {
                    date = sdf.parse(c.getString(c.getColumnIndex(TRANSACTIONS_COLUMN_DATE)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                transactions.add(new Transaction(date,accoutNo,type,amount));
                c.moveToNext();
            }
            db.close();
            return  transactions;
        }
    }

}
