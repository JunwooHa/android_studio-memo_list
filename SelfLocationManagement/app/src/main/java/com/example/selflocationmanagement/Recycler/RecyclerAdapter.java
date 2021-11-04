package com.example.selflocationmanagement.Recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.selflocationmanagement.DB.Memo_Model;
import com.example.selflocationmanagement.R;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {

    private List<Memo_Model> memo_list;                                                             // 리사이클러 뷰에 등록할 DB에서 가져온 메모 리스트를 담을 공간

    public RecyclerAdapter(List<Memo_Model> memo_list){                                 // 해당 클래스 생성 시, memo_list 초기화
        this.memo_list = memo_list;
    }
    
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {                  // 뷰 홀더가 생성되면 수행해야 하는 메서드

        Context context = parent.getContext();                                                                  // 리사이클러뷰가 존재하는 액티비티의 Context를 context변수에 저장
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   // 리사이클러뷰의 LayoutInflater
        View view = inflater.inflate(R.layout.recyclerview, parent, false);                          // R.layout.recyclerview.xml을 view변수에 저장
        RecyclerViewHolder viewHolder = new RecyclerViewHolder(view);                                           // 리사이클러 뷰에 위의 xml파일을 설정

        return viewHolder;                                                                                      // 설정이 완료된 뷰 홀더를 반환
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder viewHolder, int position) {                     // 뷰 홀더가 연동되면 수행하는 메서드(position 값이 상승하면 하나씩 메모를 등록한다, 일종의 for문)
        viewHolder.bind(memo_list.get(position));                                                   // memo_list의 position변수의 값과 일치하는 메모를 연동한다
    }

    @Override
    public int getItemCount() {                                                                     // 위 메소드의 position 값의 최대치. 즉, 연동할 아이템의 개수를 파악한다.
        return memo_list.size();
    }

}
