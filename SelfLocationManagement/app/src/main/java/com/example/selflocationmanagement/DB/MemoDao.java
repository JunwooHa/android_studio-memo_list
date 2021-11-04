package com.example.selflocationmanagement.DB;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.selflocationmanagement.DB.Memo_Model;

import java.util.List;

// DAO(Data Access Object) : DB에 직접 접근하는 클래스이다.
// SQLite와 Entity간 transaction을 수행한다.(CRUD제공)  CRUD : Create Read Update Delete
@Dao
public interface MemoDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)                                                 // onConflict는 Primay key가 겹치는 것이 있을 때를 의미하며, ingnore시 새 id를 생성한다.
    void insert(Memo_Model memo);                                                                   // insert(메모) : 메모를 DB에 추가 하는 메서드



    @Query("SELECT * FROM memo")
    LiveData<List<Memo_Model>> getAll();                                                            // getAll() : DB에 있는 모든 메모를 return 한다.(LiveData 형태로)
                                                                                                    //***** LiveData는 데이터의 상태를 감시하며,

    @Query("Select * FROM memo ORDER BY title asc") // db 안의 있는 모든 메모들을 제목 별로 정렬하는 코드
    LiveData<List<Memo_Model>> Name_Sort();

    @Query("SELECT * FROM memo WHERE title = :title") //db 안의 제목으로 검색하는 코드
    LiveData<List<Memo_Model>> search(String title);

    @Query("Select * FROM memo ORDER BY today desc") //db 내용의 날짜를 기준으로 정렬하는 코드
    LiveData<List<Memo_Model>> Date_Sort();
    // 데이터가 변경되면 연결된 Listener에 알림을 보내 UI가 변경될 수 있는 기능을 제공한다.

                                                                                                    //LiveData는 관찰 가능한 데이터 홀더 클래스로, 수명주기를 인식한다는 특징이 있다.
                                                                                                 //select된 Memo_Model의 객체들을 List로 묶은다음, 수명주기 인식을 위해 LiveData로 한번 더 묶어 관리한다.

    @Query("SELECT * FROM memo where lat = :lat AND lon = :lon")
    LiveData<List<Memo_Model>> getMarkerInfo(Double lat, Double lon);                               // getMarkerInfo(lat,lon) : 위도와 경도가 매개변수의 값과 같은 메모만 return 한다(LiveData 형태로)



    @Query("DELETE FROM memo WHERE lat= :lat AND lon = :lon")                                       // delete(lat, lon) : 위도와 경도가 매개변수의 값과 같은 메모만 삭제한다.
    void delete(Double lat, Double lon);

    @Query("DELETE FROM memo")
    void deleteAll();                                                                               // deleteAll() : DB에서 모든 메모 삭제


}
