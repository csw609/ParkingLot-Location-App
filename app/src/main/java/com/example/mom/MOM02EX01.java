package com.example.mom;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Rating;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MOM02EX01 extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerClickListener, View.OnTouchListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleMap mMap;
    private static String IP_ADDRESS = "211.217.28.193";
    //private static String IP_ADDRESS = "192.168.43.240";
    private static String TAG = "phptest";
    private ArrayList<ParkingData> mArrayList= new ArrayList<>();
    public boolean taskChecker = true;
    private String mJsonString;
    public int currentIndex;
    Button reviewList, beneBtn, myInfoBtn;
    public int buttonChecker = 0;
    timeThread time;
    int fare;
    //
    String user_name;
    String user_car;
    String user_disability_type;
    String user_disability_grade;
    //-----------------GPS
    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    //private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private Activity mActivity;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocation;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;

    LocationRequest locationRequest;
    //

    {
        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
    }

    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GPS
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mActivity = this;


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        //
        Intent intent = getIntent();
        user_name = intent.getStringExtra("name");
        user_disability_type = intent.getStringExtra("type");
        user_disability_grade = intent.getStringExtra("grade");
        user_car = intent.getStringExtra("car");
        taskChecker = intent.getBooleanExtra("task",true);

        Window win = getWindow();


        if(taskChecker) {
            GetData task = new GetData();
            task.execute("http://" + IP_ADDRESS + "/getjson_parking.php", "");
        }
        else{
            mArrayList = (ArrayList<ParkingData>) intent.getSerializableExtra("array");
        }
        win.setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.smallmenu2, null);

        LinearLayout.LayoutParams paramlinear = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        win.addContentView(linear, paramlinear);
        //win.addContentView(linear2, paramlinear);
        //-------------
        LinearLayout upperLinear = findViewById(R.id.upper);
        LinearLayout lowerLinear = findViewById(R.id.lower);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = 0;
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
        upperLinear.setLayoutParams(param);
        lowerLinear.setLayoutParams(param);
        //--------------
        setReviewList();
        beneBtnClick();
        myInfoBtnClick();

    }
    //-----------------------
    @Override
    public void onResume() {

        super.onResume();

        if (mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }


        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;

                checkPermissions();
            }
        }
    }


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
            mRequestingLocationUpdates = true;

            mGoogleMap.setMyLocationEnabled(true);

        }

    }



    private void stopLocationUpdates() {

        Log.d(TAG,"stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = false;
    }
    //-----------------------
    /*
    public void setReviewWrite() {
        reviewWrite = (Button) findViewById(R.id.reviewWrite);

        final EditText writeET = new EditText(MOM02EX01.this);

        reviewWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder writedlg = new AlertDialog.Builder(MOM02EX01.this);
                writedlg.setTitle("후기를 입력하세요.");
                writedlg.setView(writeET);
                writedlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                writedlg.show();
            }
        });
    }
*/

    public void beneBtnClick() {
        beneBtn = (Button) findViewById(R.id.beneBtn);

        beneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MOM02EX01.this, Benefit.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                startActivity(intent);
            }
        });
    }

    public void myInfoBtnClick() {
        myInfoBtn = (Button) findViewById(R.id.myInfoBtn);

        myInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MOM02EX01.this, MyInfo.class);
                intent.putExtra("name",user_name);
                intent.putExtra("type",user_disability_type);
                intent.putExtra("grade",user_disability_grade);
                intent.putExtra("car",user_car);
                intent.putExtra("array",mArrayList);
                startActivity(intent);
            }
        });
    }

    public void setReviewList() {
        reviewList = (Button) findViewById(R.id.reviewList);

        reviewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MOM02EX01.this, ReviewList.class);
                intent.putExtra("Index",currentIndex);
                intent.putExtra("rate",mArrayList.get(currentIndex).getParking_averageRating());
                startActivity(intent);
            }
        });

    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mGoogleMap = googleMap;

        LatLng KwangWoonSt = new LatLng(37.623854, 127.061963);
        LatLng KwangWoonSt2 = new LatLng(37.622687, 127.060051);


//        Marker make;
//        MarkerOptions marker =new MarkerOptions();
//        MarkerOptions marker2 =new MarkerOptions();
//        int a = 1;
//        int b = 2;
//        marker.position(KwangWoonSt);
//        //marker.title("1");
//        make = mMap.addMarker(marker);
//        make.setTag(a);
//
//        marker2.position(KwangWoonSt2);
//       // marker2.title("2");
//        make = mMap.addMarker(marker2);
//        make.setTag(b);


        mMap.moveCamera(CameraUpdateFactory.newLatLng(KwangWoonSt));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//        Button remove = findViewById(R.id.remove_lay);
//        remove.setOnClickListener(this);
        Button PkStart = findViewById(R.id.PkStart);

        PkStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                if(buttonChecker == 0) {
                    time = new timeThread();
                    time.start();
                    buttonChecker = 1;
                }
                else {
                    Button PkStart = findViewById(R.id.PkStart);
                    long timeGet;
                    String t = time.sEll;
                    timeGet = time.ell;
                    time.keep = false;
                    //디버깅
                    //timeGet += 1000*3600;
                    CalculateFare(timeGet);
                    buttonChecker = 0;
                    AlertDialog.Builder feedlg = new AlertDialog.Builder(MOM02EX01.this);
                    feedlg.setTitle("예상 요금입니다.");
                    feedlg.setMessage(Integer.toString(fare) + "원");
                    feedlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    feedlg.show();
                }

            }
        });

        RelativeLayout rel_map = findViewById(R.id.Rel_map);
        rel_map.setOnTouchListener(this);
        mMap.setOnMarkerClickListener(this);
        //----GPS
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener(){

            @Override
            public boolean onMyLocationButtonClick() {

                Log.d( TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                MakeMarker(mMap);
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                Log.d( TAG, "onMapClick :");
            }
        });

        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {

                if (mMoveMapByUser == true && mRequestingLocationUpdates){

                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }

                mMoveMapByUser = true;

            }
        });


        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {

            @Override
            public void onCameraMove() {


            }
        });
    }
    @Override
    public void onLocationChanged(Location location) {

        currentPosition
                = new LatLng( location.getLatitude(), location.getLongitude());


        Log.d(TAG, "onLocationChanged : ");

        String markerTitle = getCurrentAddress(currentPosition);
        String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                + " 경도:" + String.valueOf(location.getLongitude());

        //현재 위치에 마커 생성하고 이동
        setCurrentLocation(location, markerTitle, markerSnippet);

        mCurrentLocation = location;
    }


    @Override
    protected void onStart() {

        if(mGoogleApiClient != null && mGoogleApiClient.isConnected() == false){

            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {

        if (mRequestingLocationUpdates) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }

        if ( mGoogleApiClient.isConnected()) {

            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }
    @Override
    public void onConnected(Bundle connectionHint) {


        if ( mRequestingLocationUpdates == false ) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {

                    ActivityCompat.requestPermissions(mActivity,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                } else {

                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }

            }else{

                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }





    @Override
    public void onConnectionSuspended(int cause) {

        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {

        mMoveMapByUser = false;


        //if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(currentLatLng);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);


        //currentMarker = mGoogleMap.addMarker(markerOptions);


        if ( mMoveMapByAPI ) {

            Log.d( TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude() ) ;
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }


    public void setDefaultLocation() {

        mMoveMapByUser = false;


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        //if (currentMarker != null) currentMarker.remove();

//        MarkerOptions markerOptions = new MarkerOptions();
//        markerOptions.position(DEFAULT_LOCATION);
//        markerOptions.title(markerTitle);
//        markerOptions.snippet(markerSnippet);
//        markerOptions.draggable(true);
//        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //currentMarker = mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");

        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {


            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");

            if ( mGoogleApiClient.isConnected() == false) {

                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {

            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (permissionAccepted) {


                if ( mGoogleApiClient.isConnected() == false) {

                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }



            } else {

                checkPermissions();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MOM02EX01.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(mActivity,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });

        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MOM02EX01.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                askPermissionOnceAgain = true;

                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + mActivity.getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mActivity.startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        builder.create().show();
    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MOM02EX01.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if ( mGoogleApiClient.isConnected() == false ) {

                            Log.d( TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }


    public void MakeMarker(GoogleMap mMap){
        int NumOfData;
        NumOfData  = mArrayList.size();
        Marker makeTag;
        for(int i = 0; i < NumOfData;i++ )
        {
            LatLng location = new LatLng(Double.parseDouble(mArrayList.get(i).getParking_x()),Double.parseDouble(mArrayList.get(i).getParking_y()));
            MarkerOptions marker =new MarkerOptions();

            marker.position(location);

            //marker.title(String.valueOf(i));

            makeTag = mMap.addMarker(marker);
            makeTag.setTag(Integer.parseInt(mArrayList.get(i).getParking_id()));
        }


    }
    public int CalculateFare(long time){
        fare = 0;
        time = time / 1000; //밀리세컨드에서 minute으로 변환
        time = time / 60;

        int unitTime = Integer.parseInt(mArrayList.get(currentIndex).getParking_unitTime());
        int unitFare = Integer.parseInt(mArrayList.get(currentIndex).getParking_unitFare());
        int maxFare = Integer.parseInt(mArrayList.get(currentIndex).getParking_maxFare());
        //기본 요금 계산
        fare += Integer.parseInt(mArrayList.get(currentIndex).getParking_basicFare());
        time = time - Integer.parseInt(mArrayList.get(currentIndex).getParking_basicTime());
        if(time < 0){
            fare = fare / 5;
            return fare;
        }
        //단위 요금 계산
        fare += (time / unitTime + 1 ) * unitFare;

        //할인율 적용하기

        if (fare > maxFare){
            fare = maxFare;
        }
        //서울시는 전부 80퍼센트 이므로 0.2를 곱함 이후 전국으로 확대시 변경핗요
        fare = fare / 5;

        return fare;
    }

    public void onClick(View view) {
//        LinearLayout upperLinear = findViewById(R.id.upper);
//        LinearLayout lowerLinear = findViewById(R.id.lower);
//        int width = LinearLayout.LayoutParams.MATCH_PARENT;
//
//        if(upperLinear.getHeight() != 0) {
//            int height = 0;
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
//            upperLinear.setLayoutParams(param);
//            lowerLinear.setLayoutParams(param);
//        }
//        else{
//            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 97, getResources().getDisplayMetrics());
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
//            upperLinear.setLayoutParams(param);
//            lowerLinear.setLayoutParams(param);
//        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        int index = (Integer)marker.getTag();
        currentIndex = index;
        index = index - 1;
        LinearLayout upperLinear = findViewById(R.id.upper);
        LinearLayout lowerLinear = findViewById(R.id.lower);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;

        final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 97, getResources().getDisplayMetrics());
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
        upperLinear.setLayoutParams(param);
        lowerLinear.setLayoutParams(param);
        Log.i(this.getClass().getName(), Integer.toString(currentIndex));

        RatingBar ratingBar = findViewById(R.id.RatingBar);
        //데이터 받아오기
        ratingBar.setRating(Float.parseFloat(mArrayList.get(index).getParking_averageRating()));
        Log.i(this.getClass().getName(), Float.toString(Float.parseFloat(mArrayList.get(index).getParking_averageRating())));

        TextView parkingNum = findViewById(R.id.pkNum);
        TextView price = findViewById(R.id.price);
        TextView priceUnit = findViewById(R.id.priceLetter);

        parkingNum.setText(mArrayList.get(index).getParking_number() + "개");
        price.setText(mArrayList.get(index).getParking_unitFare() + "원");
        priceUnit.setText(mArrayList.get(index).getParking_unitTime() + "분 당  ");
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        LinearLayout upperLinear = findViewById(R.id.upper);
        LinearLayout lowerLinear = findViewById(R.id.lower);
        int width = LinearLayout.LayoutParams.MATCH_PARENT;

        //if(upperLinear.getHeight() != 0) {
            int height = 0;
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
            upperLinear.setLayoutParams(param);
            lowerLinear.setLayoutParams(param);
        //}
//        else{
//            final int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 97, getResources().getDisplayMetrics());
//            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, height);
//            upperLinear.setLayoutParams(param);
//            lowerLinear.setLayoutParams(param);
//        }
        return false;
    }


    private class GetData extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MOM02EX01.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            //mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null) {

                //    mTextViewResult.setText(errorString);
            } else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = params[1];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }

        }
    }

    private void showResult() {

        String TAG_JSON = "Parking Location";

        String TAG_NAME = "parking_name";
        String TAG_ID = "parking_id";
        String TAG_X = "parking_x";
        String TAG_Y = "parking_y";
        String TAG_BASICFARE="parking_basicFare";
        String TAG_BASICTIME="parking_basicTime";
        String TAG_UNITFARE="parking_unitFare";
        String TAG_UNITTIME="parking_unitTime";
        String TAG_MAXFARE="parking_maxFare";
        String TAG_AVERAGERATING="parking_averageRating";
        String TAG_NUMBER="parking_number";






        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject item = jsonArray.getJSONObject(i);

                String parking_id = item.getString(TAG_ID);
                String parking_name = item.getString(TAG_NAME);
                String parking_x = item.getString(TAG_X);
                String parking_y = item.getString(TAG_Y);
                String parking_basicFare = item.getString(TAG_BASICFARE);
                String parking_basicTime = item.getString(TAG_BASICTIME);
                String parking_unitFare = item.getString(TAG_UNITFARE);
                String parking_unitTime = item.getString(TAG_UNITTIME);
                String parking_maxFare = item.getString(TAG_MAXFARE);
                String parking_averageRating = item.getString(TAG_AVERAGERATING);
                String parking_number = item.getString(TAG_NUMBER);


                ParkingData parkingData = new ParkingData();

                parkingData.setParking_id(parking_id);
                parkingData.setParking_name(parking_name);
                parkingData.setParking_x(parking_x);
                parkingData.setParking_y(parking_y);
                parkingData.setParking_basicFare(parking_basicFare);
                parkingData.setParking_basicTime(parking_basicTime);
                parkingData.setParking_unitFare(parking_unitFare);
                parkingData.setParking_unitTime(parking_unitTime);
                parkingData.setParking_maxFare(parking_maxFare);
                parkingData.setParking_averageRating(parking_averageRating);
                parkingData.setParking_number(parking_number);

                Log.i(this.getClass().getName(),parking_x);
                Log.i(this.getClass().getName(),parking_y);

                Log.i(this.getClass().getName(),"---------------------------------------------");



                mArrayList.add(parkingData);

                Log.i(this.getClass().getName(), Integer.toString(mArrayList.size()));
                Log.i(this.getClass().getName(),"---------------------------------------------");
                //mAdapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {

            Log.d(TAG, "showResult : ", e);
        }

    }
    public class timeThread extends Thread{
        int i = 0;
        public boolean keep = true;
        public long mBaseTime; //시간 측정 베이스 타임
        public long now = SystemClock.elapsedRealtime();
        public long ell = now - mBaseTime;
        public String sEll;
        @Override
        public void run() {
            Button PkStart = findViewById(R.id.PkStart);
            mBaseTime = SystemClock.elapsedRealtime();
            sEll = String.format("%02d:%02d", ell / 1000 / 60, (ell / 1000) % 60);
            PkStart.setText(sEll + "\n주차종료");

            while(keep){
                i++;
                now = SystemClock.elapsedRealtime();
                ell = now - mBaseTime;
                sEll = String.format("%02d:%02d", ell / 1000 / 60, (ell / 1000) % 60);
                PkStart.setText(sEll + "\n주차종료");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            PkStart.setText("주차시작");
        }
    }
}
