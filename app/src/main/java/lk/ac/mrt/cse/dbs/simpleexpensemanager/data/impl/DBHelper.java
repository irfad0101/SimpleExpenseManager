package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Irfad Hussain on 12/4/2015.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "ExpenseManager.db";

    public static final String ACCOUNTS_TABLE_NAME = "accounts";
    public static final String ACCOUNTS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String ACCOUNTS_COLUMN_BANK = "bank";
    public static final String ACCPUNTS_COLUMN_HOLDER = "holder";
    public static final String ACCPUNTS_COLUMN_BALANCE = "balance";

    public static final String TRANSACTIONS_TABLE_NAME = "transactions";
    public static final String TRANSACTIONS_COLUMN_ACCOUNTNO = "accountNo";
    public static final String TRANSACTIONS_COLUMN_TYPE = "type";
    public static final String TRANSACTIONS_COLUMN_AMOUNT = "amount";
    public static final String TRANSACTIONS_COLUMN_YEAR = "year";
    public static final String TRANSACTIONS_COLUMN_MONTH = "month";
    public static final String TRANSACTIONS_COLUMN_DAY = "day";


    public DBHelper(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
