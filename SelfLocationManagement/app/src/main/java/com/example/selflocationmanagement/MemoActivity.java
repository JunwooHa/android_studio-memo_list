package com.example.selflocationmanagement;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.selflocationmanagement.ActionBar.CustomActionBar;
import com.example.selflocationmanagement.DB.Memo_Model;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoActivity extends AppCompatActivity {
    private EditText mTitleEditText;
    private EditText mContentEditText;
    private TextView memo_Pos;
    private Button back_Btn;
    private MemoViewModel memoViewModel;
    private ImageView imgView;
    private ImageButton img_btn;

    private static final int REQUEST_CODE = 200;                                                      // 프로필 사진 요청 코드
    private long mMemoId = -1;
    private Double lat, lon;
    private String uri = null;
    public String today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()); // 현재 시간
    // 이미지 경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

        mTitleEditText = findViewById(R.id.memo_title);                                             // 메모 제목
        mContentEditText = findViewById(R.id.memo_content);                                         // 메모 내용
        memo_Pos = findViewById(R.id.memo_pos);                                                     // 주소 표현 텍스트뷰
        back_Btn = findViewById(R.id.back_Btn);                                                     // 뒤로 가기 버튼
        imgView = findViewById(R.id.img);
        img_btn = findViewById(R.id.img_btn);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        img_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("MemoActivity", " 이미지 버튼 클릭 중");

                Intent intent = new Intent();
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });


        memoViewModel = new ViewModelProvider.AndroidViewModelFactory(MemoActivity.this             // DB 접근 메소드
                .getApplication())
                .create(MemoViewModel.class);
        

        Intent intent = getIntent();
        if (intent != null) {
            if(intent.hasExtra("lat"))                                                         // 좌표 생성 메모일 경우
            {
                lat = intent.getDoubleExtra("lat",0);                               // defaultValue는 만약 데이터값이 넘어오지 않았을 경우 채우는 값
                lon = intent.getDoubleExtra("lon",0);                               // 좌표(위도, 경도) 변수
                String addr = intent.getStringExtra("addr");

                memo_Pos.setText(": " + addr);

            }else{                                                                                  // 메모수정 일 경우

                mMemoId = intent.getLongExtra("id", -1);
                String title = intent.getStringExtra("title");
                String content = intent.getStringExtra("contents");

                mTitleEditText.setText(title);
                mContentEditText.setText(content);
            }                                                                                       // 좌표X 새 메모일 경우, 내용 채움x
        }
        back_Btn.setOnClickListener(v -> { onBackPressed(); });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

                                                                                                    // 메모 생성 코드
                                                                                                    // 뒤로가기를 눌렀을 때
                                                                                                    // back button press
    @Override
    public void onBackPressed() {
        String title = mTitleEditText.getText().toString();
        String contents = mContentEditText.getText().toString();
        String gps = memo_Pos.getText().toString();

        if(title.equals("") && contents.equals("")) {}                                              // 제목과 내용이 없다면 저장하지 말것!!
        else if(title.equals("") && !contents.equals("")){                                          // 제목이 없지만 내용은 있을 경우,
            insert_memo(title, "제목없음", lat, lon, uri,today);
        }
        else if(gps.equals("")) {
            insert_memo(title,contents,0.0,0.0,uri,today);
        }
        else{                                                                                     // 제목과 내용이 둘 다 있을 경우,
            insert_memo(title, contents, lat, lon, uri,today);
        }

        super.onBackPressed();                                                                      // 오버라이딩 되기 전의 뒤로가기 기능 수행
    }
    
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){           // 이미지 가져오는 메소드
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE){
            if(resultCode == RESULT_OK){                                                                // 불러오기 성공이라면
                try{
                    Uri tmp_uri = data.getData();
                    uri = tmp_uri.toString();
                    Glide.with(getApplicationContext()).load(uri).into(imgView);                            // 이미지 사진에 넣기

                }catch (Exception e){
                    e.printStackTrace();
                }

            }else if(resultCode == RESULT_CANCELED){

            }
        }
    }



    void insert_memo(String title, String contents, Double lat, Double lon, String uri, String today){

        Memo_Model tmp_model = new Memo_Model(title, contents, lat, lon, uri,today);
        memoViewModel.insert(tmp_model);
    }
}
