package com.example.selflocationmanagement;

import java.io.Serializable;

public class Cov implements Serializable {

    private String name;                                                                            // 코로나 확진자 방문 장소명
    private String type;                                                                            // 코로나 확진자 방문 장소 유형
    private String date;                                                                            // 코로나 확진자 방문 일시
    private String address;                                                                         // 코로나 확진자 방문 장소 주소
    private String note;                                                                            // 코로나 확진자 방문에 대한 비고내용

    public String[] getCov()
    {
        return new String[]{name, type, date, address, note};
    }

    public void setCov(String name, String type, String date, String address, String note){         // setter
        this.name = name;
        this.type = type;
        this.date = date;
        this.address = address;
        this.note = note;
    }
}
