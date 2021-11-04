package com.example.selflocationmanagement.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Entity 클래스 배열 ,버전 정수 값, 스키마를 폴더에 내보낼 지에 대한 매개변수 값
@Database(entities = {Memo_Model.class}, version = 1, exportSchema = false)
public abstract class MemoRoomDatabase extends RoomDatabase {

    public abstract MemoDao memoDao();                                                              // DAO(Data Access Object, DB에 접근하는 클래스)
    public static final int NUMBER_OF_THREADS = 4;                                                  // 작업 쓰레드 수

    public static volatile MemoRoomDatabase INSTANCE;                                               // 싱글톤 인스턴스(해당 클래스를 중복 선언해도 객체는 하나만 생성된다)
                                                                                                    // 여기서 volatile은 해당 변수를 Cache가 아닌, 항상 Main Memory에서 Read/Write를 하겠다는 의미,
                                                                                                    // 자신의 값을 스스로 제거할 수 있음을 나타낸다.
                                                                                                    // 싱글톤 표현 시, volatile을 사용하는 이유는 해당 변수가 사용되는 쓰레드가 다를 수 있으며,
                                                                                                    // 각기 다른 쓰레드에서 동일한 값에 접근하기 위해서 이다.
                                                                                                    // 안드로이드는 퍼포먼스를 위해 Main Thread에서 데이터를 조작할 수 없도록 제어한다.
                                                                                                    // 그러므로 변수의 값 수정에 있어 Main Thread의 감시 하에서 수행하므로, 정확한 값을 가진다.
    
    public static final ExecutorService databaseWriterExecutor
            = Executors.newFixedThreadPool(NUMBER_OF_THREADS);                                      // 쓰레드 풀 생성
                                                                                                    // Main Thread는 오직 UI표현을 위해서만 사용되므로, Room 쿼리 처리를 위해 쓰레드 풀을 생성
    

                                                                                                    // 깔끔한 DAO 기능 RoomDatabase의 유무를 확인 후, 없으면 memo 데이터베이스와 연동과 동시에 생성하며
                                                                                                    // Database 생성은 한 번만 하되, 여러 부분에서 사용할 수 있게 roomDB를 return 한다.
    public static MemoRoomDatabase getDatabase(final Context context)
    {
        if(INSTANCE == null) {
            synchronized (MemoRoomDatabase.class) {
                if (INSTANCE == null) {                                                             // 미리 생성된 DB가 없으면 생성한다
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MemoRoomDatabase.class, "memo")
                            .addCallback(setInitialRoomDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;
    }

                                                                                                    // INSTANCE 생성 시 CALLBACK 메소드를 추가하여 초기 데이터를 추가하는 것이 가능하다.
                                                                                                    // 현재 추가된 모든 데이터를 지우고, 새로운 데이터를 초기 데이터로 추가한 메서드
    private static final RoomDatabase.Callback setInitialRoomDatabaseCallback =
        new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db)
        {
            super.onCreate(db);

            databaseWriterExecutor.execute(()->{
                MemoDao memoDao = INSTANCE.memoDao();
                memoDao.deleteAll();

                Memo_Model memo = new Memo_Model("초기데이터 1", "위치 없는 초기데이터입니다.\n DB 첫 생성 시, 해당 메모도 생성됩니다");
                memoDao.insert(memo);

                memo = new Memo_Model("초기데이터 2", "위치가 있는 초기데이터입니다.\n DB 첫 생성 시, 해당 메모도 생성됩니다", 128.1076213, 35.1799817);
                memoDao.insert(memo);

            });
        }

    };

}
