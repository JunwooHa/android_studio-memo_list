package com.example.selflocationmanagement;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

public class User implements Serializable {
    public Vector<LatLng> coMarkerPositions;                                                        // 코로나 마커 좌표 배열
    public Vector<LatLng> meMarkerPositions;                                                        // 메모 마커 좌표 배열

    public ArrayList<Cov> covList;                                                                  // 코로나 정보 배열
    public ArrayList<Marker> coMarkerList;                                                          // 코로나 마커 배열
    public ArrayList<Marker> meMarkerList;                                                          // 메모 마커 배열
    public ArrayList<CircleOverlay> circleList;                                                     // 원(범위) 오버레이 배열

    private boolean pos_bound_check;                                                                // 범위 진입 판단 변수
    private boolean marker_overlap_check;                                                           // 마커 좌표 중복 판단 변수


    User(){

        coMarkerPositions = new Vector<LatLng>();
        meMarkerPositions = new Vector<LatLng>();
        covList = new ArrayList<Cov>();
        coMarkerList = new ArrayList<Marker>();
        meMarkerList = new ArrayList<Marker>();
        circleList = new ArrayList<CircleOverlay>();

    }


    public boolean get_bound_check() {
        return pos_bound_check;
    }

    public boolean get_overlap_check() {
        return marker_overlap_check;
    }

    public void set_bound_check(boolean pos_bound_check) {
        this.pos_bound_check = pos_bound_check;
    }

    public void set_overlap_check(boolean marker_overlap_check) {
        this.marker_overlap_check = marker_overlap_check;
    }

}
