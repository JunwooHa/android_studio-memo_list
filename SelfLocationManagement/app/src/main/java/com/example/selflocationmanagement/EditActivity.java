package com.example.selflocationmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.database.sqlite.SQLiteDatabase;

import com.example.selflocationmanagement.DB.MemoRoomDatabase;
import com.example.selflocationmanagement.R;

import com.example.selflocationmanagement.MemoActivity;
import com.example.selflocationmanagement.Recycler.RecyclerAdapter;
import com.example.selflocationmanagement.DB.Memo_Model;
import com.example.selflocationmanagement.DB.MemoDao;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;

import java.lang.reflect.Array;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import static android.app.WallpaperManager.getInstance;
import static java.nio.file.Files.move;
import static java.util.Collections.reverseOrder;

public class EditActivity extends AppCompatActivity {

    LiveData<List<Memo_Model>> MemoAll,SearchMemo,DateMemo; //이름별 정렬,검색,날짜 정렬을 위한 배열
    private EditText editText;
    private MemoViewModel memoViewModel;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memosort);

        editText = findViewById(R.id.editview);
        ImageButton button = (ImageButton) findViewById(R.id.buttonsearch1);
        ImageButton button2 = (ImageButton) findViewById(R.id.buttonsort);
        FloatingActionButton button3 = (FloatingActionButton) findViewById(R.id.addMemo);




        RecyclerView recyclerView = findViewById(R.id.recyclerView); // 리사이클러뷰에 db 내용을 뿌리기
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        manager.setReverseLayout(true);
//        manager.setStackFromEnd(true);
//        RecyclerAdapter adapter = new RecyclerAdapter((List<Memo_Model>) MemoAll);
//
        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(adapter);

        memoViewModel = new ViewModelProvider.AndroidViewModelFactory(EditActivity.this // db 내용 가져오기
                .getApplication())
                .create(MemoViewModel.class);

        memoViewModel.getAllMemo_list().observe(this, memo_list -> {            // select * 시 listener
            recyclerView.setAdapter(new RecyclerAdapter(memo_list));
        });

        MemoAll = new LiveData<List<Memo_Model>>() { //초기화 선언하기
        };
        MemoAll = memoViewModel.getMemo_sort(); //이름별 정렬

        SearchMemo = new LiveData<List<Memo_Model>>() {

        };

        DateMemo = new LiveData<List<Memo_Model>>() {
        };
        DateMemo = memoViewModel.Date_sort(); //날짜별 정렬

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING); // 값을 입력할 키보드가 나올 때 화면이 밀리지 않게 하기 위함


        Intent intent = getIntent(); // 받아온 것

        if (intent != null) {
            if (intent.hasExtra("title")) {
                String title = intent.getStringExtra("title");
                String contents = intent.getStringExtra("contents");

                Memo_Model model = new Memo_Model(title, contents);

                Log.i("memo_model", title + contents);

                memoViewModel.insert(model);
            }
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = editText.getText().toString(); // edittext의 값을 String 형태로 바꾸어 db의 값과 제목을 비교해 리사이클러뷰에 나타내기
                SearchMemo = memoViewModel.searchMemo(s);

                if(s != null)
                {
                    SearchMemo.observe(EditActivity.this,memo -> {
                        recyclerView.setAdapter(new RecyclerAdapter(memo));
                    });

                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final PopupMenu p = new PopupMenu(getApplicationContext(), view);
                getMenuInflater().inflate(R.menu.popup, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.action_menu1: // 이름별 정렬
                              MemoAll.observe(EditActivity.this,memo -> {
                                 recyclerView.setAdapter(new RecyclerAdapter(memo));
                               });
                            break;
                            case R.id.action_menu2: // 날짜별 정렬
                                DateMemo.observe(EditActivity.this,memo -> {
                                    recyclerView.setAdapter(new RecyclerAdapter(memo));
                                });
                            break;
                        }
                        return false;
                    }
                });
                p.show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // 추가 창으로 이동
               Intent intent1 = new Intent(EditActivity.this,MemoActivity.class);
                startActivity(intent1);
            }
        });


    }

    public void getTime() {

    }

//    public class JsoupAsync extends AsyncTask<Void, Void, Void> {
//        // AsyncTask를 상속                    <매개1, 매개2, 매개3>
//        // 매개변수1 : 백그라운드 작업을 위한 doInBackground()함수의 매개변수 타입과 동일
//        // 매개변수2 : doInBackground() 함수 수행에 의해 발생한 데이터를 publishProgress()함수를 이용해 전달하는데,
//        //            이 때 전달할 데이터 타입
//        // 매개변수3 : onPostExecute()함수의 매개변수 타입과 동일하게 지정 doInBackground()함수의 반환형이며,
//        //            반환된 데이터가 onPostExecute()함수의 매개변수로 전달됨
//        //implement로 다중 상속 가능
//
//        @Override
//        protected void onPreExecute() {
//            // AsyncTask의 작업을 시작하기 전에 호출(가장 먼저 한 번 호출) 설정 작업 메인인지 애매
//
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//            // doInBackground() : 스레드에 의해 처리될 내용을 담기 위한 함수 [본문,계산 코드,정렬]
//
//            return null;
//        }
//        @Override //main thread 죽기 전에 발생 UI를 바꾸는건 이 역할
//        protected void onPostExecute (Void aVoid){
//            // AsyncTask의 모든 작업이 완료된 후 가장 마지막에 한 번 호출
//            // 주소 -> 좌표로 변환하는 메소드
//        }
//    }


//    public class JsoupAsynck extends AsyncTask<Void, Void, Void> {
//        // AsyncTask를 상속                    <매개1, 매개2, 매개3>
//        // 매개변수1 : 백그라운드 작업을 위한 doInBackground()함수의 매개변수 타입과 동일
//        // 매개변수2 : doInBackground() 함수 수행에 의해 발생한 데이터를 publishProgress()함수를 이용해 전달하는데,
//        //            이 때 전달할 데이터 타입
//        // 매개변수3 : onPostExecute()함수의 매개변수 타입과 동일하게 지정 doInBackground()함수의 반환형이며,
//        //            반환된 데이터가 onPostExecute()함수의 매개변수로 전달됨
//        //implement로 다중 상속 가능
//
//        @Override
//        protected void onPreExecute() {
//            // AsyncTask의 작업을 시작하기 전에 호출(가장 먼저 한 번 호출) 설정 작업 메인인지 애매
//
//        }
//        @Override
//        protected Void doInBackground(Void... params) {
//            // doInBackground() : 스레드에 의해 처리될 내용을 담기 위한 함수 [본문,계산 코드,정렬]
////            if (editText != null) {
////                String s = editText.getText().toString();
////                if (s == SearchMemo) {
////                    textView.setText(s);
////                }
////                else
////                    textView.setText("결과 없음");
////            }
////            else
////                textView.setText("입력값 없음");
//            return null;
//        }
//        @Override //main thread 죽기 전에 발생 UI를 바꾸는건 이 역할
//        protected void onPostExecute (Void aVoid){
//            // AsyncTask의 모든 작업이 완료된 후 가장 마지막에 한 번 호출
//            // 주소 -> 좌표로 변환하는 메소드
//        }
//    }

    public void sideBar_BtnClick_edit(View view)
    {
        switch (view.getId()){
            case R.id.list_btn: //뒤로가기 버튼
                onBackPressed();
                break;
            case R.id.iv_option:
                break;

        }
    }
}