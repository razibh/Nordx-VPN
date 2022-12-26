package com.inapp.vpn.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.inapp.vpn.Activities.MainActivity;
import com.inapp.vpn.R;
import com.inapp.vpn.Activities.UnlockAllActivity;
import com.inapp.vpn.AdapterWrappers.ServerListAdapterVip;
import com.inapp.vpn.Config;
import com.facebook.ads.*;
import com.inapp.vpn.Utils.Constants;
import com.inapp.vpn.model.Countries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentVip extends Fragment {

    private RecyclerView recyclerView;
    private ServerListAdapterVip adapter;
    private static RewardedAd rewardedAd;
    private RewardedAd mRewardedAd;
    private RelativeLayout animationHolder;
    private static final String TAG = "CHECKADS";
    private RelativeLayout mPurchaseLayout;
    private ImageButton mUnblockButton;
    private static RewardedVideoAd rewardedVideoAd;
    private static SharedPreferences sharedPreferences;
    static Countries countryy;
    static View btView;
    public static Context context;
    public static boolean viewSet = false;
    static View view;
    public static boolean fbAdIsLoading = true;
    public static boolean googleAdIsLoading = true;
    public static boolean googleAdResune = false;
    public static boolean fbAdResume = false;
    public static ProgressDialog progressdialog;
    private static BottomSheetDialog btDialog;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdSettings.addTestDevice("c4894289-bd58-4ec5-b608-192469edce5a");
        AdSettings.addTestDevice("4cbd7f01-b2fb-4d12-ac35-f399d9f30351");
        AdSettings.addTestDevice("ad883e4f-8d84-4631-afdb-12104e62f4b8");
        AdSettings.addTestDevice("6b5e1429-599a-4c17-adc0-c1758563d3ec");
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_one_indratech, container, false);


        progressdialog = new ProgressDialog(context);
        progressdialog.setMessage("Please wait just a moment !!");
        progressdialog.setCancelable(false);

        recyclerView = view.findViewById(R.id.region_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        animationHolder = view.findViewById(R.id.animation_layout);
        sharedPreferences = getContext().getSharedPreferences("userRewarded",Context.MODE_PRIVATE);
        btView = LayoutInflater.from(context)
                .inflate(R.layout.layout_bottom_sheet_indratech,(ConstraintLayout)view.findViewById(R.id.bsContainer));

        btDialog = new BottomSheetDialog(
                context,R.style.BottomSheetDialogTheme
        );

        btDialog.setContentView(btView);

        mPurchaseLayout = view.findViewById(R.id.purchase_layout);
        mUnblockButton = view.findViewById(R.id.vip_unblock);

        mPurchaseLayout.setVisibility(View.GONE);



        adapter = new ServerListAdapterVip(getActivity());
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadServers();
    }

    private void loadServers() {
        ArrayList<Countries> servers = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(Constants.PREMIUM_SERVERS);

            for (int i=0; i < jsonArray.length();i++){
                JSONObject object = (JSONObject) jsonArray.get(i);
                servers.add(new Countries(object.getString("serverName"),
                        object.getString("flag_url"),
                        object.getString("ovpnConfiguration"),
                        object.getString("vpnUserName"),
                        object.getString("vpnPassword")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter.setData(servers);
        animationHolder.setVisibility(View.GONE);
    }



    public static void unblockServer()
    {

        btView.findViewById(R.id.but_subs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, UnlockAllActivity.class));
                btDialog.dismiss();
            }
        });

        btDialog.show();
    }

    public static void onItemClick(Countries country)
    {
        countryy = country;

        if (Config.vip_subscription || Config.all_subscription) {
            Intent intent=new Intent(context, MainActivity.class);
            intent.putExtra("c",country);
            intent.putExtra("type",MainActivity.type);
            intent.putExtra("indratech_fast_27640849_ad_banner",MainActivity.indratech_fast_27640849_ad_banner_id);
            intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
            intent.putExtra("indratech_fast_27640849_fb_native",MainActivity.indratech_fast_27640849_fb_native_id);
            intent.putExtra("indratech_fast_27640849_fb_interstitial",MainActivity.indratech_fast_27640849_fb_interstitial_id);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
        else
        {
            unblockServer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        googleAdIsLoading = true;
        fbAdIsLoading = true;
        googleAdResune = false;
        fbAdResume = false;
    }


}