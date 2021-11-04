package com.example.selflocationmanagement;

import androidx.annotation.NonNull;

import java.io.Serializable;


public class CovInfo implements Serializable {
    private String total;                                                                           // 총 확진자
    private String cured;                                                                           // 총 완치자
    private String inhospital;                                                                      // 입원 중
    private String death;                                                                           // 총 사망자
    private String cur_inspect;                                                                     // 검사중
    private String self_isol;                                                                       // 자가 격리자

    public CovInfo(){};                                                                             // 생성자 함수

    public CovInfo(@NonNull String total, @NonNull String cured, @NonNull String inhospital, @NonNull String death, @NonNull String cur_inspect, @NonNull String self_isol){

        setCovInfo(total, cured, inhospital, death, cur_inspect, self_isol);
    }

    public void setCovInfo(String total, String cured, String inhospital, String death, String cur_inspect, String self_isol){          // setter 메서드

        this.total = total;
        this.cured = cured;
        this.inhospital = inhospital;
        this.death = death;
        this.cur_inspect = cur_inspect;
        this.self_isol = self_isol;

    }

    public String getTotal() {
        return total;
    }

    public String getCured() {
        return cured;
    }

    public String getInhospital() {
        return inhospital;
    }

    public String getDeath() {
        return death;
    }

    public String getCur_inspect() {
        return cur_inspect;
    }

    public String getSelf_isol() {
        return self_isol;
    }
}
