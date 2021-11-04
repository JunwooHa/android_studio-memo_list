package com.example.selflocationmanagement.Recycler;

import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.selflocationmanagement.DB.Memo_Model;
import com.example.selflocationmanagement.MainActivity;
import com.example.selflocationmanagement.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {                                   // 뷰 홀더 클래스이다(기능 : 뷰에 들어가는 구성요소들을 선언, 초기화를 담당한다)

    ImageView card_img;
    TextView card_title;
    TextView card_contents;
    TextView card_date;

    RecyclerViewHolder(View itemView){
        super(itemView);

        card_img = itemView.findViewById(R.id.card_img);
        card_title = itemView.findViewById(R.id.card_txt);
        card_contents = itemView.findViewById(R.id.card_grade);
        card_date = itemView.findViewById(R.id.date_txt);
    }

    void bind(Memo_Model memo)
    {
        Log.i("RecyclerViewHolder", "RecyclerView에 DB반영 준비 : " + memo.title + " "+ memo.contents);

        if(memo.uri != null)                                                                        // uri가 null이 아니면 이미지를 로딩한다.
        {
            Uri uri = Uri.parse(memo.uri);

            Glide.with(((MainActivity)MainActivity.context_main)).load(uri).into(card_img);
            card_title.setText(memo.title);                                                             // 매개변수로 받은 메모의 title 필드의 값을 입력한다
            card_contents.setText(memo.contents);                                                       // 매개변수로 받은 메모의 contents 필드의 값을 입력한다.
            card_date.setText(memo.today);
        } else{                                                                                     // uri가 null이면 로딩하지 않고,
            card_title.setText(memo.title);                                                             // 매개변수로 받은 메모의 title 필드의 값을 입력한다
            card_contents.setText(memo.contents);
            card_date.setText(memo.today);
        }


    }

}
