package com.inapp.vpn.Activities;

import android.content.Intent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inapp.vpn.R;
import com.inapp.vpn.Utils.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import top.oneconnectapi.app.api.OneConnect;

public class SplashScreen extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    OneConnect oneConnect = new OneConnect();
                    oneConnect.initialize(SplashScreen.this, getString(R.string.oneconnect_key));
                    try {
                        Constants.FREE_SERVERS = oneConnect.fetch(true);
                        Constants.PREMIUM_SERVERS = oneConnect.fetch(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference typeRef = database.getReference("type");
        DatabaseReference indratech_fast_27640849_admob_id = database.getReference("indratech_fast_27640849_admob_id");
        DatabaseReference indratech_fast_27640849_ad_banner = database.getReference("indratech_fast_27640849_ad_banner");
        DatabaseReference indratech_fast_27640849_aad_native = database.getReference("indratech_fast_27640849_aad_native");
        DatabaseReference indratech_fast_27640849_fb_native = database.getReference("indratech_fast_27640849_fb_native");
        DatabaseReference indratech_fast_27640849_fb_interstitial = database.getReference("indratech_fast_27640849_fb_interstitial");
        DatabaseReference indratech_fast_27640849_ad_interstitial = database.getReference("indratech_fast_27640849_ad_interstitial");
        DatabaseReference indratech_fast_27640849_all_ads_on_off = database.getReference("indratech_fast_27640849_all_ads_on_off");
        DatabaseReference copyright_indratech_official_dont_change_the_value = database.getReference("copyright_indratech_official_dont_change_the_value");

        String TAG = "Firebase";

        indratech_fast_27640849_all_ads_on_off.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);

                MainActivity.indratech_fast_27640849_all_ads_on_off = false;

                assert value != null;
                if(value.equalsIgnoreCase("on")) {
                    MainActivity.indratech_fast_27640849_all_ads_on_off = true;
                }

                Log.d(TAG,"indratech_fast_27640849_all_ads_on_off "+value);
                Log.d(TAG,"indratech_fast_27640849_all_ads_on_off "+MainActivity.indratech_fast_27640849_all_ads_on_off);
            }

            @Override
            public void onCancelled(@NotNull DatabaseError error) {
                MainActivity.indratech_fast_27640849_all_ads_on_off = false;
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        typeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.type = value;
                Log.d(TAG,"Type"+value);
                Log.d(TAG,"Type"+MainActivity.type);

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        indratech_fast_27640849_aad_native.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_fast_27640849_aad_native_id = value;
                Log.d(TAG,"Native"+value);
                Log.d(TAG,"Native"+MainActivity.indratech_fast_27640849_aad_native_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        indratech_fast_27640849_admob_id.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_fast_27640849_admob_id = value;
                Log.d(TAG,"Admob ID"+value);
                Log.d(TAG,"Admob ID"+MainActivity.indratech_fast_27640849_admob_id);
                try {
                    ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(),PackageManager.GET_META_DATA);
                    Bundle bundle = applicationInfo.metaData;
                    applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID",MainActivity.indratech_fast_27640849_admob_id);
                    String apiKey = bundle.getString("com.google.android.gms.ads.APPLICATION_ID");
                    Log.d(TAG,"The saved id is "+MainActivity.indratech_fast_27640849_admob_id);
                    Log.d(TAG,"The saved id is "+apiKey);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        indratech_fast_27640849_ad_banner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_fast_27640849_ad_banner_id = value;
                Log.d(TAG,"Admob Banner"+value);
                Log.d(TAG,"Admob Banner"+MainActivity.indratech_fast_27640849_ad_banner_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
       
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

     
        indratech_fast_27640849_ad_interstitial.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.admob_interstitial_id = value;
                Log.d(TAG,"Admob interstitial"+value);
                Log.d(TAG,"Admob interstitial"+MainActivity.admob_interstitial_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
            
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

     
        indratech_fast_27640849_fb_native.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_fast_27640849_fb_native_id = value;
                Log.d(TAG,"indratech_fast_27640849_fb_native"+value);
                Log.d(TAG,"indratech_fast_27640849_fb_native"+MainActivity.indratech_fast_27640849_fb_native_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
              
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

   
        indratech_fast_27640849_fb_interstitial.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.indratech_fast_27640849_fb_interstitial_id = value;
                Log.d(TAG,"indratech_fast_27640849_fb_interstitial"+value);
                Log.d(TAG,"indratech_fast_27640849_fb_interstitial"+MainActivity.indratech_fast_27640849_fb_interstitial_id);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                  Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



        copyright_indratech_official_dont_change_the_value.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.copyright_indratech_official_dont_change_the_value = value;
                Log.d(TAG,"copyright_indratech_official_dont_change_the_value"+value);
                Log.d(TAG,"copyright_indratech_official_dont_change_the_value"+MainActivity.copyright_indratech_official_dont_change_the_value);

            }

            @Override
            public void onCancelled(DatabaseError error) {
              
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_indratech);
        coordinatorLayout = findViewById(R.id.cordi);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!Utility.isOnline(getApplicationContext())) {


            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Check internet connection", Snackbar.LENGTH_LONG);
            snackbar.show();


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                    finish();
                }
            },1000);

        }

    }
}
