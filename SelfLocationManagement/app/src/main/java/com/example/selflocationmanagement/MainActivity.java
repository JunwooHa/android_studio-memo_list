package com.example.selflocationmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.selflocationmanagement.ActionBar.CustomActionBar;
import com.example.selflocationmanagement.DB.Memo_Model;
import com.example.selflocationmanagement.Recycler.RecyclerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// 지도 인터페이스
public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback {


    private MapView mapView;                                                                        // 맵뷰(지도 객체를 보여주는 기능을 수행한다)
    private static NaverMap naverMap;                                                               // 네이버 맵
    private FusedLocationSource locationSource;                                                     // 위치를 받아오기 위한 객체
    private static final int PERMISSIONS_REQUEST_CODE = 100;                                        // 퍼미션 코드

    
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};     // 필요한 퍼미션들의 배열

    public static Context context_main;                                                             // 메인 액티비티의 context 변수(정적)
    User user = new User();                                                                         // 마커 리스트, 위치 리스트가 포함된 클래스 변수 선언
    public CovInfo covInfo;                                                                         // 코로나 확진자 수 통계(총 확진자 수, 입원 중 ... 등)를 가지는 클래스 변수 선언
    private MemoViewModel memoViewModel;                                                            // DB에 접근할 때 사용하는 MemoViewModel 클래스 변수
    public Handler mHandler = null;                                                                 // 맵뷰를 갱신할 때 사용할 핸들러 변수
    public Dialog dialog_memo;                                                                      // 메모 클릭 시, 생성되는 다이얼로그

    public View marker_root_view;
    public ImageView img_marker;

    // 웹 크롤링(스크래핑) 관련
    private String htmlURL = "https://www.jinju.go.kr/05190/05641.web";                             // 스크래핑 할 웹 사이트 주소(진주시청)
    private ArrayList<String> addr_s = new ArrayList<String>();                                          // 주소(코로나 확진자)를 저장하는 변수

    private long mLastClickTime = 0;                                                                // 중복 클릭 제한 타이머 변수(최종 클릭 시간)
    public int clicked_marker_number = 0;                                                           // 마커의 id를 파악하기 위한 변수
    public Double tmp_lat = 0.0;                                                                    // 임시 위도 저장 변수
    public Double tmp_lon = 0.0;                                                                    // 임시 경도 저장 변수



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context_main = this;                                                                        // context 객체 초기화(context_main)

        mapView = (MapView) findViewById(R.id.map);                                                 // 맵뷰에 네이버 지도 저장
        mapView.onCreate(savedInstanceState);
        locationSource = new FusedLocationSource(this, PERMISSIONS_REQUEST_CODE);            // 현재 내 위치 설정
        mapView.getMapAsync(this);


        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);                  //  RecyclerView에 Adapter 및 LayoutManager 등록
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        setCustomMarkerView();                                                                      // 커스텀 마커에 필요한 뷰 id 연결 및 레이아웃 선언 메소드

        memoViewModel = new ViewModelProvider.AndroidViewModelFactory(MainActivity.this             // MVVM패턴 Model, View, View_Model 패턴(순서대로 MemoViewModel -> MemoRepository -> memoDao -> DB 로 연결)
                .getApplication())
                .create(MemoViewModel.class);

        memoViewModel.getAllMemo_list().observe(this, memo_list -> {                          // DB에서 전체 메모 가져오기 and 옵저버 설정하여 가져올 경우, 
            recyclerView.setAdapter(new RecyclerAdapter(memo_list));                                        // 자동으로 recyclerView 갱신
            setMeMarker(memo_list);                                                                         // 가져온 메모에 해당하는 마커 생성
        });


//----------------------------------- 스크래핑 and 갱신 반복 코드(10분 마다 반복) --------------------------------------------------------------
        mHandler = new Handler();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        JsoupAsync jsoupAsync = new JsoupAsync();                                   // JsoupAsync 라이브러리를 이용한 스크래핑
                        jsoupAsync.execute();
                        mHandler.postDelayed(this, 10*60*1000);                         // 10분 마다 핸들러를 이용해 메인스레드를 딜레이 호출(UI는 메인스레드로만 갱신할 수 있다.)
                    }
                });
            }
        });
        t.start();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    
    @Override
    public void onBackPressed() {                                                                   // 메인 액티비티에서 뒤로가기 시 어플 종료 코드
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);                   // 종료 질문 다이얼로그 생성
        builder.setTitle("어플리케이션 종료")
                .setMessage("어플리케이션을 종료하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("종료", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int pid = android.os.Process.myPid();                                               // 해당 어플리케이션의 pid(process id)를 저장
                        android.os.Process.killProcess(pid);                                                // 프로세스 kill
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();                                                                    // 취소할 경우, 질문 다이얼로그 취소
                    }
                })
                .show();
    }




    public class JsoupAsync extends AsyncTask<Void, Void, Void> {                                   // jsoup : 자바 HTML파서(오픈소스 프로젝트, MIT) - 웹 스크래핑에 사용
                                                                                                    // AsyncTask를 상속                    <매개1, 매개2, 매개3>
                                                                                                    // 매개변수1 : 백그라운드 작업을 위한 doInBackground()함수의 매개변수 타입과 동일
                                                                                                    // 매개변수2 : doInBackground() 함수 수행에 의해 발생한 데이터를 publishProgress()함수를 이용해 전달하는데,
                                                                                                    //            이 때 전달할 데이터 타입
                                                                                                    // 매개변수3 : onPostExecute()함수의 매개변수 타입과 동일하게 지정 doInBackground()함수의 반환형이며,
                                                                                                    //            반환된 데이터가 onPostExecute()함수의 매개변수로 전달됨
        @Override
        protected void onPreExecute(){                                                              // AsyncTask의 작업을 시작하기 전에 호출(가장 먼저 한 번 호출)
            super.onPreExecute();
            removeMarker();                                                                                 // removeMarker() : 지도 상의 모든 마커 제거
        }

        @Override
        protected Void doInBackground(Void... params) {                                             // doInBackground() : 백그라운드 스레드에 의해 처리될 내용을 담기 위한 함수

            try{

                setSSL();                                                                           // setSSL()은 SSLHandshakeException 에러를 해결하기 위해
                                                                                                    // 유효성 체크 비활성화한다.

                addr_s = new ArrayList<String>();                                                   // 주소 배열 초기화
                Cov tmp_cov;                                                                        // 임시 Cov 클래스 저장 공간 선언

                Document doc = Jsoup.connect(htmlURL).get();                                        // 해당 URL의 페이지와 연결
                Elements tr_s = doc.select("tbody.tdtac tr");                               // 해당 태그에 해당하는 내용 가져오기


                for(Element tr : tr_s)                                                              // 태그 내용 하나씩 String 포맷화
                {
                    tmp_cov = new Cov();                                                            // Cov 동적 객체 초기화
                    String tmp_address = tr.select("td").get(3).text();

                    if(tmp_address.isEmpty())
                    {
                        tmp_address = "진주시 " + tr.select("td").get(2).text();
                    }else{
                        tmp_address = tr.select("td").get(3).text() + " " + tr.select("td").get(2).text();
                    }

//                    Log.i("MainActivity", "tmp_address : " + tmp_address);

                    tmp_cov.setCov(
                            tr.select("td").get(2).text(),                                  // 확진자 방문 장소 이름
                            tr.select("td").get(1).text(),                                  // 확진자 방문 장소 종류
                            tr.select("td").get(4).text(),                                  // 확진자 방문 일시
                            tmp_address,                                                            // 확진자 방문 장소 주소
                            tr.select("td").get(6).text()                                   // 비고 내용
                    );


                    if(tr.select("td").get(1).text().equals("시외버스")){                           // 시외버스일 경우, 예외처리
                        tmp_address = "시외버스";
                    }
                    String address = getSearchItem(tmp_address);                                    // getSearchItem(임시주소) : "진주시 + 장소명"이라는 임시주소를 가지고 네이버에 검색하여
                                                                                                    //                          확실한 도로명 주소 및 번지주소를 파악한다.

                    Log.i("MainActivity","현재 처리할 주소 입니다 : " + address + "\n");

                    user.covList.add(tmp_cov);                                                      // 확진자 정보를 user.covList에 저장
                    addr_s.add(address);                                                            // addr_s (한글 주소를 관리)에 추가
                    Thread.sleep(100);                                                         // api에 초당 10회 호출 제한이 있어, 시간 제한
                }

                covInfo = new CovInfo(doc.select("span.num2").text(),                       // 확진자 통계 스크래핑(covInfo에 저장)
                        doc.select("span.num1").eq(0).text(),
                        doc.select("span.num1").eq(1).text(),
                        doc.select("span.num1").eq(2).text(),
                        doc.select("span.num1.ls3").eq(0).text(),
                        doc.select("span.num11").text());

            }catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NullPointerException e){
                e.printStackTrace();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {                                                  // AsyncTask의 모든 작업이 완료된 후 가장 마지막에 한 번 호출
            getCoordinate();                                                                        // 주소 -> 좌표로 변환하는 메소드
        }
    }




                                                                                                    // setSSL()은 웹 스크래핑 중, SSLHandshakeException 에러를 해결하기 위해 유효성 체크 비활성화한다.
    public static void setSSL() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub

                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        // TODO Auto-generated method stub
                    }
                }
        };
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new SecureRandom());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }





    public String getCurrentAddress(double latitude, double longitude) {                            // 지오코더... GPS를 주소로 변환(GPS -> 주소)
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioException) { //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }


    public void getCoordinate()                                                                     // 지오코더 (주소 -> GPS)
    {

        Geocoder geocoder = new Geocoder(this);                                              // 지오코더
        user.coMarkerPositions = new Vector<LatLng>();                                              // 마커 생성 전 위치 파악


        for(String pos:addr_s)
        {
            user.set_overlap_check(false);                                                          // 마커 중복 체크 변수 초기화

            try{
                
                Log.i("MainActivity", "GeoCoder 변경 전 주소 : " + pos);
                List<Address> list = geocoder.getFromLocationName(pos, 10);                // 변환된 결과값을 담는 리스트
                                                            // 지역 주소       결과 개수

                Address addr = list.get(0);                                                         
                Double lat = addr.getLatitude();
                Double lon = addr.getLongitude();
                LatLng tmp_pos = sliceDouble(lat, lon);                                             // sliceDouble : 위도, 경도 소수점 4자리 수에서 끊는 메서드


                for(LatLng markerPosition:user.coMarkerPositions)
                {

                    if(markerPosition == null){continue;}
                    if(tmp_pos.equals(markerPosition))
                    {
                        user.set_overlap_check(true);                                               // 중복 좌표가 있을 경우, 중복 변수 체크
                        System.out.println("---- !! 중복 주소 발견 ----\n");
                        System.out.println("--- 중복된 좌표 : " + markerPosition + "----\n\n");

                    }

                }

                if(user.get_overlap_check() == false)                                               // 현재 등록하려는 좌표가 중복이 아니면, 해당 좌표를 리스트에 저장
                {
                    user.coMarkerPositions.add(tmp_pos);
                    Log.i("MainActivity", " 변환된 좌표 출력  :  " + addr.getLatitude() + "     " + addr.getLongitude());
                }

            }catch (IOException e)
            {
                e.printStackTrace();
                Log.i("geoerr", "입출력 오류 - 서버에서 주소변환 시 발생");
            }
        }

       setMarker();                                                                                 // 검증된 좌표 배열을 활용하여 마커 생성
    }



    


    LatLng sliceDouble(Double lat, Double lon){

        lat =  Double.parseDouble(String.format(Locale.KOREAN, "%.4f", lat));
        lon =  Double.parseDouble(String.format(Locale.KOREAN, "%.4f", lon));

        return new LatLng(lat, lon);
    }

    
    
    
    

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {                                            // 네이버 지도 관련 설정
//        Log.d( TAG, "onMapReady");
        this.naverMap = naverMap;
        naverMap.setLocationSource(locationSource);
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);

        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setCompassEnabled(true);                                                         // 나침반
        uiSettings.setScaleBarEnabled(true);                                                        // 거리
        uiSettings.setZoomControlEnabled(true);                                                     // 줌
        uiSettings.setLocationButtonEnabled(true);                                                  // 내 위치

        naverMap.addOnLocationChangeListener(location ->                                            // 위치 변경 시 : posBoundCheck 실행(코로나 반경에 들어갔는지 체크)
                posBoundCheck(location.getLatitude(), location.getLongitude())
        );

        naverMap.addOnCameraIdleListener(() -> {                                                    // 위치 변경이 끝나고 idle 상태가 되면, 해당 위치에서의 범위 체크 및 범위 내 마커만 보여주기
                markerUpdate(naverMap);
        });

        naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener(){                           // 네이버맵 클릭 시 리스너 설정
            @Override
            public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {

                if(SystemClock.elapsedRealtime() - mLastClickTime < 1000){                          // 중복 클릭 방지(클릭 한 후 1초 이내에 입력받은 클릭 모두 무시
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();                                     // Time 갱신

                
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);    // 메모 생성 확인 다이얼로그
                builder.setTitle("새 위치 메모 생성")
                        .setMessage("메모를 생성하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(MainActivity.this, MemoActivity.class);
                                String addr = getCurrentAddress(latLng.latitude, latLng.longitude);
                                intent.putExtra("lat", latLng.latitude);                            // 경도
                                intent.putExtra("lon", latLng.longitude);                           // 위도
                                intent.putExtra("addr", addr);                                      // 주소
                                startActivity(intent);                                                    // memoActivity로의 이동
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                AlertDialog dialog = builder.create();                                              // 알림창 객체 생성
                dialog.show();                                                                      // 알림창 표현

            }
        });
    }






    void markerUpdate(@NonNull NaverMap naverMap){                                                  // 위치의 중심점을 기준으로 범위 내의 마커만 표시하는 코드

        Double lat = Double.valueOf(naverMap.getCameraPosition().target.latitude);
        Double lon = Double.valueOf(naverMap.getCameraPosition().target.longitude);

        // CircleOverlay circle = new CircleOverlay();
        // circle.setCenter(lng);
        // circle.setRadius(3000);
        // LatLngBounds cirBound = circle.getBounds();
        // 위의 코드는 범위 설정 후 범위 내의 마커만 표시(부하가 심할 때 사용)
        // 아래코드는 네이버 지도, 카메라 범위 내의 마커만 표시


        LatLngBounds curBound = naverMap.getContentBounds();                                        // 카메라 범위
        offMarker();                                                                                // 마커 지도 상에서 제거(코로나, 메모 포함)

        for(int i=0; i<user.coMarkerList.size();i++)
        {
            if(curBound.contains(user.coMarkerPositions.get(i)))
            {
                user.coMarkerList.get(i).setMap(naverMap);                                          // 마커가 현재 보는 범위 내에 있으면 표현
                user.circleList.get(i).setMap(naverMap);
            }

        }

        for(int i=0; i<user.meMarkerList.size();i++)
        {
            if(curBound.contains(user.meMarkerPositions.get(i)))
            {
                user.meMarkerList.get(i).setMap(naverMap);                                          // 마커가 현재 보는 범위 내에 있으면 표현
            }

        }

    }




    private void posBoundCheck(double lati, double longti)
    {

        LatLngBounds cirBound;                                                                      // 위치 범위 내 접근 체크
        for(CircleOverlay circle : user.circleList)
        {
            cirBound = circle.getBounds();
            user.set_bound_check(cirBound.contains(new LatLng(lati, longti)));                      // 좌표가 원 오버레이의 영역에 포함되는 지 여부를 확인

            if (user.get_bound_check() == true)
            {
                createNotification();                                                               // 확진자 지점 반경 안에 접근 시 알림 생성
            }
        }
        user.set_bound_check(false);                                                                // 범위 체크가 끝나면 중복 부울 값 초기화
    }






    private void setMarker()                                                                        // 마커 설정
    {

        Marker marker;
        CircleOverlay circle;

        for(LatLng markerPosition:user.coMarkerPositions)
        {
            marker = new Marker();                                                                  // 마커 오버레이(지점 표시)
            circle = new CircleOverlay();                                                           // 원 오버레이(범위 표시)

            marker.setPosition(markerPosition);                                                     // 마커 위치 설정
            marker.setIconPerspectiveEnabled(true);                                                 // 마커의 원근감 표시
            marker.setWidth(100);
            marker.setHeight(130);
            marker.setIcon(OverlayImage.fromResource(R.drawable.cov_marker));
            marker.setIconTintColor(Color.parseColor("#EB0000"));                          // 마커 색
            user.coMarkerList.add(marker);                                                          // 마커 배열에 해당 마커를 삽입

            circle.setCenter(markerPosition);                                                       // 원 위치 설정
            circle.setRadius(100);                                                                  // 원 범위 설정
            circle.setColor(Color.parseColor("#B3FF4646"));                                // 원 오버레이에는 setAlpha()로 투명도를 조정하는 기능이 없기에 setColor()로 투명도가 있는 rgb 값을 지정한다.
            user.circleList.add(circle);                                                            // 원 배열에 해당 원 오버레이를 삽입

            marker.setMap(naverMap);                                                                // 네이버 맵에 각각 배치
            circle.setMap(naverMap);
        }
    }






    public void offMarker()
    {
        for(Marker marker:user.coMarkerList)                                                        // 네이버 맵에서 마커와 원 오버레이 제거
        {
            marker.setMap(null);
        }
        for(CircleOverlay circle:user.circleList)
        {
            circle.setMap(null);
        }
        for(Marker marker:user.meMarkerList)
        {
            marker.setMap(null);
        }
    }




    private void removeMarker()                                                                     // 마커 초기화
    {
        offMarker();
        user.coMarkerList = new ArrayList<Marker>();                                                // markerList 및 circlesList 초기화
        user.circleList = new ArrayList<CircleOverlay>();
    }




    private void createNotification()
    {
        Intent dummyIntent = new Intent();                                                          // autoCancel을 활성화하기 위해서는 Intent가 필요, 따라서 dummyIntent를 생성
        PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, dummyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mBuilder =                                                       // 접근 시 푸시 알림관련 코드
                new NotificationCompat.Builder(this, "default")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("경고")
                        .setContentText("현재 확진자 경로에 위치해있습니다.")
//                        .setLargeIcon(BitmapFactory.decodeResource())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setContentIntent(pIntent)
                        .setOnlyAlertOnce(true)                                                     // 동일 아이디의 알림을 처음 받았을때만 알린다.
                        .setAutoCancel(true);                                                       // 푸시 알림창을 사용자가 터치하였을 때, 자동으로 사라짐

        /*setSmallIcon : 작은 아이콘 (필수)
        setContentTitle : 제목 (필수)
        setContentText : 내용 (필수)
        setColor : 알림내 앱 이름 색
        setWhen : 받은 시간 커스텀 ( 기본 시스템에서 제공합니다 )
        setShowWhen : 알림 수신 시간 ( default 값은 true, false시 숨길 수 있습니다 )
        setOnlyAlertOnce : 알림 1회 수신 ( 동일 아이디의 알림을 처음 받았을때만 알린다, 상태바에 알림이 잔존하면 무음 )
        setContentTitle : 제목
        setContentText : 내용
        setFullScreenIntent : 긴급 알림 ( 자세한 설명은 아래에서 설명합니다 )
        setTimeoutAfter : 알림 자동 사라지기 ( 지정한 시간 후 수신된 알림이 사라집니다 )
        setContentIntent : 알림 클릭시 이벤트 ( 지정하지 않으면 클릭했을때 아무 반응이 없고 setAutoCancel 또한 작동하지 않는다 )
        setLargeIcon : 큰 아이콘 ( mipmap 에 있는 아이콘이 아닌 drawable 폴더에 있는 아이콘을 사용해야 합니다. )
        setAutoCancel : 알림 클릭시 삭제 여부 ( true = 클릭시 삭제 , false = 클릭시 미삭제 )
        setPriority : 알림의 중요도를 설정 ( 중요도에 따라 head up 알림으로 설정할 수 있는데 자세한 내용은 밑에서 설명하겠습니다. )
        setVisibility : 잠금 화면내 알림 노출 여부
        Notification.VISIBILITY_PRIVATE : 알림의 기본 정보만 노출 (제목, 타이틀 등등)
        Notification.VISIBILITY_PUBLIC : 알림의 모든 정보 노출
        Notification.VISIBILITY_SECRET : 알림의 모든 정보 비노출
        */

        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mNotificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }
        mNotificationManager.notify(0, mBuilder.build());
    }





    public void onBack(View view) {                                                                 // onbackpressed.xml 뒤로가기
        onBackPressed();
        finish();
    }





    public void actionBar_BtnClick(View view)                                                       // 액션바 메뉴버튼
    {
        switch (view.getId()){
            case R.id.iv_menu:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
                if(!drawer.isDrawerOpen(Gravity.LEFT))
                    drawer.openDrawer(Gravity.LEFT);
                if(drawer.isDrawerOpen(Gravity.LEFT))
                    drawer.closeDrawer(Gravity.LEFT);
                break;
            case R.id.iv_option:
                
                break;
        }
    }




    public void sideBar_BtnClick(View view)                                                         // 커스텀 액션바의 메뉴 옵션(네이버 맵, 통계 화면, 메모 리스트)
    {
        switch (view.getId()){
            case R.id.map_btn:
                finish();
                overridePendingTransition(0, 0);
                Intent intent1 = getIntent();
                startActivity(intent1);
                overridePendingTransition(0, 0);
                break;
            case R.id.stats_btn:
                if(user.covList.isEmpty()){                                                         // 아직 스크래핑 중 이라면, 대기 요청 알림
                    Toast.makeText(context_main, "조금만 기다려주세요", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent2 = new Intent(MainActivity.this, StatsActivity.class);
                    startActivity(intent2);
                }
                break;
            case R.id.list_btn:
                if(user.covList.isEmpty()){                                                         // 아직 스크래핑 중 이라면, 대기 요청 알림
                    Toast.makeText(context_main, "조금만 기다려주세요", Toast.LENGTH_SHORT).show();
                } else{
                    Intent intent3 = new Intent(MainActivity.this, EditActivity.class);
                    startActivity(intent3);
                }
                break;
        }
    }





    public void setMeMarker(List<Memo_Model> memo_list){                                            // 메모용 마커 설정
        Marker marker;
        LatLng markerposition;


        for(Memo_Model memo : memo_list)
        {
            Log.i("MainActivity", "메모마커 설정 준비 " + memo.title);
            Log.i("MainActivity", "메모마커 위치 준비 " + memo.lat + " " + memo.lon);

            if(memo.lat.equals(0.0) || memo.lon.equals(0.0))
            {
                continue;
            }

            markerposition = new LatLng(memo.lat, memo.lon);

//            if(memo.uri != null)
//            {
//                Uri uri = Uri.parse(memo.uri);                                                      // String 타입 uri를 Uri 타입으로 변환
//                Glide.with(this).load(uri).into(img_marker);                                 // uri에서 이미지 불러와서 설정
//            }

            marker = new Marker();                                                                  // 마커 오버레이(지점 표시)
            marker.setPosition(markerposition);                                                     // 마커 위치 설정
            marker.setIconPerspectiveEnabled(true);                                                 // 마커의 원근감 표시
            marker.setWidth(100);
            marker.setHeight(130);
            marker.setIcon(OverlayImage.fromBitmap(createDrawableFromView(this, marker_root_view)));    // memo_marker_img라는 레이아웃.xml 파일을 비트맵으로 변환 -> 비트맵을 오버레이 이미지로 변환 -> 적용

                                                                                                    //  MarkerOptions markerOptions = new MarkerOptions();( 구글맵용 옵션 )//  markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, marker_root_view)));
            
            marker.setOnClickListener(overlay -> {                                                  // 마커별로 따로 클릭 리스너를 주는 것이 정석

                Marker ovl_marker = (Marker) overlay;
                LatLng tmp_pos = new LatLng(0.0,0.0);
                clicked_marker_number = 0;
                tmp_lat = 0.0;
                tmp_lon = 0.0;

                for(int i=0; i<user.meMarkerList.size(); i++)
                {

                    if(user.meMarkerList.get(i).equals(ovl_marker))
                    {
                        tmp_pos = user.meMarkerPositions.get(i);
                        clicked_marker_number = i;
                        break;
                    }
                }

                tmp_lat = tmp_pos.latitude;
                tmp_lon = tmp_pos.longitude;
                
                
                dialog_memo = new Dialog(MainActivity.this);                                 // 메모 마커 클릭 시, 상세 표시 다이얼로그 생성
                dialog_memo.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog_memo.setContentView(R.layout.marker_dialog);


                LiveData<List<Memo_Model>> listData = memoViewModel.select(tmp_lat, tmp_lon);       // 다이얼로그에 정보 추가
                listData.observe(this, memoInfoList -> {                                      // select * 시 listener
                    for(Memo_Model memoInfo : memoInfoList){
                        TextView tempTitle = dialog_memo.findViewById(R.id.dialog_title);
                        tempTitle.setText(memoInfo.title);

                        TextView tempContent= dialog_memo.findViewById(R.id.dialog_content);
                        tempContent.setText(memoInfo.contents);
                    }
                });

                dialog_memo.show();
                return true;
            });
            // 마커 longclick을 사용한 삭제 기능을 만드려 하였으나, longclicklistener가 존재하지 않음

            user.meMarkerPositions.add(markerposition);
            user.meMarkerList.add(marker);                                                          // 마커 배열에 해당 마커를 삽입
            marker.setMap(naverMap);                                                                // 네이버 맵에 각각 배치
            Log.i("MainActivity", "메모마커 설정 완료" + markerposition.toString());
        }
    }

    public void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(this).inflate(R.layout.memo_marker_img, null);  // 메모 커스텀 마커 메소드
        img_marker = (ImageView) marker_root_view.findViewById(R.id.img_marker);
    }

    public Bitmap createDrawableFromView(Context context, View view){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public String getSearchItem(String name)                                                        // 네이버지도에 명칭을 검색하여 주소를 가져오는 메소드
    {
        String clientID = "jsv4xhadKcrUTHEUrBd5";                                                   // 애플리케이션 클라이언트 아이디 값
        String clientSecret = "3Gzju3B9in";                                                         // 애플리케이션 클라이언트 시크릿값
        int display = 1;                                                                            // 가장 근접한 값 하나만을 가져오기

        
        try{
            String tmp_name = name;

            if(tmp_name.equals("진주시 두드림 한식뷔페"))                                               // 네이버 지도에 제대로 검색 안되는 이름으로 진주시청에 올라가있는 경우, 수기로 검색 가능한 명으로 입력해준다(대부분은 검색 잘 됨)
            {
                tmp_name = "진주시 두드림";
            }else if(tmp_name.equals("진주시 가좌동 경상국립대학교 교육문화센터 2층 교직원 식당")){
                tmp_name = "진주시 가좌동 경상국립대학교 교육문화센터";
            } else if(tmp_name.equals("진주시 상대동 잠잠우즈베키스탄레스토랑, 실크로드")){
                tmp_name = "진주시 제주회초밥";                                                        // 실제 회초밥집은 아니고 찾아볼수 집이라 주소만 동일하게 등록
            } else if(tmp_name.equals("시외버스")){
                tmp_name = "진주시외버스터미널";
            } else if(tmp_name.equals("진주시 ")){
                tmp_name = "";
            }

            Log.i("MainActivity.getSearchItem", tmp_name);

            String text = URLEncoder.encode(tmp_name, "UTF-8");  // UTF-8 형식으로 검색 키워드를 인코딩 해야 한다.

            String apiURL = "https://openapi.naver.com/v1/search/local?query=" + text + "&display=" + display + "&start=1";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            int responseCode = conn.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else{
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;

            while((line = br.readLine()) != null){
                sb.append(line + "\n");
            }

            br.close();
            conn.disconnect();

            System.out.println(sb);
            String data = sb.toString();
            String[] array;
            array = data.split("\"");
            String[] title = new String[display];
            String[] link = new String[display];
            String[] category = new String[display];
            String[] description = new String[display];
            String[] telephone = new String[display];
            String[] address = new String[display];
            String[] mapx = new String[display];
            String[] mapy = new String[display];
            int k = 0;
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals("title"))
                    title[k] = array[i + 2];
                if (array[i].equals("link"))
                    link[k] = array[i + 2];
                if (array[i].equals("category"))
                    category[k] = array[i + 2];
                if (array[i].equals("description"))
                    description[k] = array[i + 2];
                if (array[i].equals("telephone"))
                    telephone[k] = array[i + 2];
                if (array[i].equals("address"))
                    address[k] = array[i + 2];
                if (array[i].equals("mapx"))
                    mapx[k] = array[i + 2];
                if (array[i].equals("mapy")) {
                    mapy[k] = array[i + 2];
                    k++;
                }
            }
            System.out.println(sb);
            System.out.println("----------------------------");
            Log.i("주소 불러드립니다.", address[0]);

            return address[0];


        }catch (Exception e){
            e.printStackTrace();
            Log.i("MainActivity", "스크래핑으로 받은 주소가 없습니다 \" 진주시 \" 로 대체합니다.\n 주소 : " + name);
            return "";
        }
    }
    
    
    
    

    public void setDel_btn_click(View view) {                                                       // 다이얼로그의 메모 삭제 버튼 메소드
        
                Toast.makeText(context_main, "삭제클릭", Toast.LENGTH_LONG).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("메모 삭제")
                        .setMessage("메모를 삭제하시겠습니까?")
                        .setCancelable(true)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog_memo.cancel();                                               // 메모의 내용을 보여주는 다이얼로그 끄기
                                user.meMarkerList.get(clicked_marker_number).setMap(null);          // 지도에서 삭제

                                user.meMarkerList.remove(clicked_marker_number);                    // ArrayList 마커 제거
                                user.meMarkerPositions.remove(clicked_marker_number);               // ArrayList 위치 제거
                                System.out.println(clicked_marker_number + " 마커 지도 상 삭제 ");


                                Thread t = new Thread(new Runnable() {                              // DB 접속은 시간 소요가 많은 작업이기에 Main Thread에서는 허가되지 않음, 외부 쓰레드에서 사용
                                    @Override
                                    public void run() {
                                        memoViewModel.delete(tmp_lat, tmp_lon);                     // db row 제거
                                        finish();
                                        overridePendingTransition(0, 0);
                                        Intent intent1 = getIntent();
                                        startActivity(intent1);
                                        overridePendingTransition(0, 0);
                                                                                                    // 네이버 맵에서 마커가 제거되는 속도가 느리길래, 삭제 시 마다 activity를 refresh하는 코드를 생성
                                    }
                                });
                                t.start();
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);                   // 퍼미션 검사의 메소드

        if(requestCode == PERMISSIONS_REQUEST_CODE){                                                // 만약 요청한 퍼미션이 100번, 즉 현재 위치 검사와 관련된 퍼미션 코드이면, 수행
            if(grantResults.length > 0                                                                  // 만약 요청한 퍼미션에 "확인" 즉, 허가를 받은 경우 수행
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);                              // 네이버맵의 위치모드를 Follow 모드로 즉시 변경
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        Intent intent1 = getIntent();
        startActivity(intent1);
        overridePendingTransition(0, 0);
    }
}