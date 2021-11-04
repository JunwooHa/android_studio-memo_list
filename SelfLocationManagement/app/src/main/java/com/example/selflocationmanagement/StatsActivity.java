package com.example.selflocationmanagement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.selflocationmanagement.ActionBar.CustomActionBar;
import com.example.selflocationmanagement.Recycler.RecyclerAdapter;

import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {

    private TextView stats_total;
    private TextView stats_cured;
    private TextView stats_inhospital;
    private TextView stats_death;
    private TextView stats_cur_inspect;
    private TextView stats_self_isol;

    private ListView listView;



    private CovInfo covInfo = null;
    private ArrayList<Cov> arr_cov = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stats);


        stats_total = findViewById(R.id.stats_total);
        stats_cured = findViewById(R.id.stats_cured);
        stats_inhospital = findViewById(R.id.stats_inhospital);
        stats_death = findViewById(R.id.stats_death);
        stats_cur_inspect = findViewById(R.id.stats_cur_inspect);
        stats_self_isol = findViewById(R.id.stats_self_isol);
        listView = findViewById(R.id.listView);



        covInfo = ((MainActivity)MainActivity.context_main).covInfo;
        arr_cov = ((MainActivity)MainActivity.context_main).user.covList;


        stats_total.setText(covInfo.getTotal());
        stats_cured.setText(covInfo.getCured());
        stats_inhospital.setText(covInfo.getInhospital());
        stats_death.setText(covInfo.getDeath());
        stats_cur_inspect.setText(covInfo.getCur_inspect());
        stats_self_isol.setText(covInfo.getSelf_isol());

        String[] cov;

        for(int i=0; i<arr_cov.size();i++)
        {
            cov = arr_cov.get(i).getCov();
            System.out.println(cov[0] + " " + cov[1] + " " + cov[2] + " " + cov[3] + " " + cov[4]);
        }

        listView.setAdapter((ListAdapter) new StatsAdapter(this, arr_cov));                  // 해당 recyclerview에 어댑터 적용

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CustomActionBar ca = new CustomActionBar(this, getActionBar());
        ca.setActionBar();

        return true;
    }

    public void onBack(View view) {
        onBackPressed();
        finish();
    }

    public void sideBar_BtnClick(View view)
    {
        switch (view.getId()){
            case R.id.stats_btn:
                onBackPressed();
                break;
            case R.id.iv_option:
                break;

        }
    }
}
