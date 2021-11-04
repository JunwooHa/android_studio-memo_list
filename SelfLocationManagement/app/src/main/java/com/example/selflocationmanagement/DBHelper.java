package com.example.selflocationmanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.selflocationmanagement.DB.Memo_Model;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private Context context;
    private MemoViewModel memoViewModel;


    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public List<Memo_Model> getData() {
        StringBuffer sb = new StringBuffer();
        SQLiteDatabase db = getReadableDatabase();
        List<Memo_Model> memo = new ArrayList<Memo_Model>();

        return memo;
    }
}
