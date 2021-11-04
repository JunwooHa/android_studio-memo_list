package com.example.selflocationmanagement.DB;

import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

// 어노테이션 Entity가 붙을 때, 해당 클래스는 Room에서 사용하는 Entity가 되며,
// 각 변수 들은 Entity의 Column이 된다.
@Entity(tableName = "memo")
public class Memo_Model{                                                                            // 해당 클래스는 DB의 레코드 스키마라고 생각하면 된다.(DB에 넣을 한 줄의 형태)

    // 어노테이션 PrimaryKey를 사용할 경우, 자동으로 id를 생성할 수 있다.
    @PrimaryKey(autoGenerate = true)
    private int id;                                                                                 // 메모를 구별할 Primary key
    public String title;                                                                            // 메모 제목
    public String contents;                                                                         // 메모 내용
    public Double lat = 0.0;                                                                        // 메모 위치(위도)
    public Double lon = 0.0;                                                                        // 메모 위치(경도)
    public String uri = null;
    public String today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()); // 현재 시간

    // Column 이름을 변경하고 싶다면 @ColumnInfo Annotation을 사용하여 이름을 변경가능
    // 반드시 값을 채워주고 싶은 Column의 경우 @NonNull Annotation을 사용


    public Memo_Model(){ }

    public Memo_Model(@NonNull String title, String conetents)                                      // 제목과 내용만 있는 메모
    {
        this.title = title;
        this.contents = conetents;
    }

    public Memo_Model(@NonNull String title, String conetents, Double lat, Double lon)              // 제목과 내용, 위치(위,경)만 있는 메모
    {
        this.title = title;
        this.contents = conetents;
        this.lat = lat;
        this.lon = lon;
    }
    
    public Memo_Model(@NonNull String title, String conetents, String uri)                      // 제목과 내용, 사진경로만 있는 메모
    {
        this.title = title;
        this.contents = conetents;
        this.uri = uri;
    }

    public Memo_Model(@NonNull String title, String conetents, Double lat, Double lon, String uri, String today) // 제목과 내용, 위치(위,경), 사진경로가 있는 메모
    {
        this.title = title;
        this.contents = conetents;
        this.lat = lat;
        this.lon = lon;
        this.uri = uri;
        this.today = today;
    }



    public int getId() {
        return id;
    }
    public void setId(int id) { }

}
