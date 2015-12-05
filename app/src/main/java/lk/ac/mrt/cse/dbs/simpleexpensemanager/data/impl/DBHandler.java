package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

/**
 * Created by Irfad Hussain on 12/4/2015.
 */
public class DBHandler extends SQLiteOpenHelper {

    public static final String DB_NAME = "ExpenseManager.db";

    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String ACCOUNTS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String ACCOUNTS_COLUMN_BANK = "bank";
    public static final String ACCOUNTS_COLUMN_HOLDER = "holder";
    public static final String ACCOUNTS_COLUMN_BALANCE = "balance";

    public static final String TRANSACTIONS_TABLE_NAME = "transacts";
    public static final String TRANSACTIONS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String TRANSACTIONS_COLUMN_TYPE = "type";
    public static final String TRANSACTIONS_COLUMN_AMOUNT = "amount";
    public static final String TRANSACTIONS_COLUMN_DATE = "transacDate";

    private static DBHandler dbHandler = null;

    private DBHandler(Context context) {
        super(context, DB_NAME, null, 1);
    }

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
        db.execSQL("create table "+ACCOUNTS_TABLE_NAME+"("+ACCOUNTS_COLUMN_ACCOUNTNO+" text primary key,"+ACCOUNTS_COLUMN_BANK+" text not null,"+ACCOUNTS_COLUMN_HOLDER+" text not null,"+ACCOUNTS_COLUMN_BALANCE+" real not null);");
        db.execSQL("create table "+TRANSACTIONS_TABLE_NAME+"("+TRANSACTIONS_COLUMN_ACCOUNTNO+" text,"+TRANSACTIONS_COLUMN_TYPE+" integer not null,"+TRANSACTIONS_COLUMN_AMOUNT+" real not null,"+TRANSACTIONS_COLUMN_DATE+" text not null,foreign key("+TRANSACTIONS_COLUMN_ACCOUNTNO+") references "+ACCOUNTS_TABLE_NAME+"("+ACCOUNTS_COLUMN_ACCOUNTNO+"));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<String> getAccountNumList(){
        List<String> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("select "+ACCOUNTS_COLUMN_ACCOUNTNO+" from "+ACCOUNTS_TABLE_NAME+";",null);
        results.moveToFirst();
        while (!results.isAfterLast()){
            accounts.add(results.getString(results.getColumnIndex(ACCOUNTS_COLUMN_ACCOUNTNO)));
            results.moveToNext();
        }
        db.close();
        return accounts;
    }

    public List<Account> getAccounts(){
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor results = db.rawQuery("select * from "+ACCOUNTS_TABLE_NAME+";",null);
        results.moveToFirst();
        while (!results.isAfterLast()){
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("select * from " + ACCOUNTS_TABLE_NAME + " where " + ACCOUNTS_COLUMN_ACCOUNTNO + "=" + accountNO + ";", null);
        result.moveToFirst();
        if (result.getCount()==0){
            db.close();
            return null;
        }
        String accNo = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_ACCOUNTNO));
        String bank = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_BANK));
        String holder = result.getString(result.getColumnIndex(ACCOUNTS_COLUMN_HOLDER));
        double amount = result.getDouble(result.getColumnIndex(ACCOUNTS_COLUMN_BALANCE));
        db.close();
        return new Account(accNo,bank,holder,amount);
    }

    public void addAccount(Account account){
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
        if (rowsDel==0)
            return false;
        return true;
    }

    public void updateAccountBalance(Account account){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNTS_COLUMN_BALANCE,account.getBalance());
        db.update(ACCOUNTS_TABLE_NAME,values,ACCOUNTS_COLUMN_ACCOUNTNO+" = ?",new String[]{account.getAccountNo()});
        db.close();
    }

}
