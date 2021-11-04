package com.example.selflocationmanagement;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.selflocationmanagement.DB.Memo_Model;

import java.io.Serializable;
import java.util.List;

public class MemoViewModel extends AndroidViewModel{
    private MemoRepository repository;
    LiveData<List<Memo_Model>> memo_list;

    public MemoViewModel(Application application)
    {
        super(application);
        repository = new MemoRepository(application);
        memo_list = repository.getAllMemo();                                                        // 초기화
    }

    LiveData<List<Memo_Model>> getAllMemo_list() {
        return memo_list;
    }
    LiveData<List<Memo_Model>> getPartMarkInfo(Double lat, Double lon) {return repository.getPartMarkInfo(lat, lon);}
    void delete(Double lat, Double lon){repository.delete(lat, lon);}

    LiveData<List<Memo_Model>> select(Double lat, Double lon){return repository.select(lat, lon);}  // select 부분추가
    public void insert(Memo_Model memo){
        repository.insert(memo);
    }

    LiveData<List<Memo_Model>> getMemo_sort() {return repository.Name_sort();}
    LiveData<List<Memo_Model>> searchMemo(String sd) {return repository.Title_search(sd);}
    LiveData<List<Memo_Model>> Date_sort() {return repository.Date_sort();}

}
