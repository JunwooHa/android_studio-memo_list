package com.example.selflocationmanagement;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.selflocationmanagement.DB.MemoDao;
import com.example.selflocationmanagement.DB.MemoRoomDatabase;
import com.example.selflocationmanagement.DB.Memo_Model;

import java.io.Serializable;
import java.util.DoubleSummaryStatistics;
import java.util.List;


                                                                                                    // Repository 클래스는 Room Database의 일부가 아닌 권장 사항이다.
                                                                                                    // 하지만 Repository 클래스에서만 데이터를 보관하고 조작하면 ViewModel에서 LiveData를 관리하는 과정이 간편해진다.
                                                                                                    // Dao로부터 데이터를 받아오고, Network나 text file을 연결하여 다른 사람의 데이터를 받아올 수 있는 일종의 Helper
public class MemoRepository {

    private MemoDao memoDao;
    private LiveData<List<Memo_Model>> allMemo,sortMemo;

    public MemoRepository(Application application)
    {
        MemoRoomDatabase db = MemoRoomDatabase.getDatabase(application);
        memoDao = db.memoDao();                                                                     // dao 연동
        allMemo = memoDao.getAll();// 모든 메모 가져오기
        sortMemo = memoDao.Name_Sort();
    }

    public LiveData<List<Memo_Model>> getAllMemo() {
        return allMemo;
    }
    public LiveData<List<Memo_Model>> getPartMarkInfo(Double lat, Double lon) {return memoDao.getMarkerInfo(lat, lon);}
    public LiveData<List<Memo_Model>> select(Double lat, Double lon){return memoDao.getMarkerInfo(lat, lon);}       //select 추가

    public void delete(Double lat, Double lon){memoDao.delete(lat, lon);}
    public void insert(Memo_Model memo){
        MemoRoomDatabase.databaseWriterExecutor.execute(()->{
            memoDao.insert(memo);
        });
    }

    public LiveData<List<Memo_Model>> Name_sort() {return sortMemo;}
    public LiveData<List<Memo_Model>> Title_search(String sd) {return memoDao.search(sd);}
    public LiveData<List<Memo_Model>> Date_sort() {return memoDao.Date_Sort();}
}
