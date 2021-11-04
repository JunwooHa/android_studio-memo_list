package com.example.selflocationmanagement;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.selflocationmanagement.Recycler.RecyclerViewHolder;

import java.util.ArrayList;

public class StatsAdapter extends BaseAdapter {

    private ArrayList<Cov> arr_cov = null;
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;

    String[] covInfo = null;

    StatsAdapter(Context context, ArrayList<Cov> arr_cov) {
        this.arr_cov = arr_cov;
        mContext = context;

    }


    @Override
    public int getCount() {
        return arr_cov.size();
    }

    @Override
    public Object getItem(int position) {
        return arr_cov.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if (convertView == null)                                                                    // 리스트의 내용물이 될 layout을 inflate
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);

        }

        TextView list_pos = (TextView) convertView.findViewById(R.id.list_pos);
        TextView list_type = (TextView) convertView.findViewById(R.id.list_type);
        TextView list_date = (TextView) convertView.findViewById(R.id.list_date);
        TextView list_addr = (TextView) convertView.findViewById(R.id.list_addr);

        Cov cov = arr_cov.get(position);

        covInfo = cov.getCov();

        list_pos.setText(covInfo[0]);                                                               //상호명
        list_type.setText(covInfo[1]);                                                              //종류
        list_date.setText(covInfo[2]);                                                              //날짜
        list_addr.setText(covInfo[3]);                                                              //주소

        if (covInfo[0].isEmpty()) {
            list_pos.setText("상호명 미확인");
        }
        if (covInfo[1].isEmpty()) {
            list_type.setText("종류 미확인");
        }
        if (covInfo[2].isEmpty()) {
            list_date.setText("날짜 미확인");
        }
        if (covInfo[3].isEmpty()) {
            list_addr.setText("주소 미확인");
        }


        return convertView;


    }
}
