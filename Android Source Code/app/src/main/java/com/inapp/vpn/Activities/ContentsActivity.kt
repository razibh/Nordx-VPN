package com.inapp.vpn.Activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.facebook.ads.*
import com.facebook.ads.AdError
import com.facebook.ads.AdView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import com.inapp.vpn.Config
import com.inapp.vpn.R
import com.inapp.vpn.Utils.Constants
import com.inapp.vpn.speed.Speed
import com.inapp.vpn.ui.toolside
import com.onesignal.OneSignal
import es.dmoral.toasty.Toasty
import pl.droidsonroids.gif.GifImageView


abstract class ContentsActivity : toolside() {

    private var mLastRxBytes: Long = 0
    private var mLastTxBytes: Long = 0
    private var mLastTime: Long = 0
    private var mSpeed: Speed? = null

    var lottieAnimationView: LottieAnimationView? = null
    var vpnToastCheck = true
    var handlerTraffic: Handler? = null
    private val adCount = 0

    private var mInterstitialAdMob: com.google.android.gms.ads.interstitial.InterstitialAd? = null
    private var loadingAd: Boolean? = false
    var frameLayout: RelativeLayout? = null
    var nativeAdLayout: NativeAdLayout? = null

    @JvmField


    var progressBarValue = 0
    var handler = Handler(Looper.getMainLooper())
    private val customHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L
    var timeInMilliseconds = 0L
    var timeSwapBuff = 0L
    var updatedTime = 0L


    var tvIpAddress: TextView? = null
    var textDownloading: TextView? = null
    var textUploading: TextView? = null
    var tvConnectionStatus: TextView? = null
    var ivConnectionStatusImage: ImageView? = null
    var ivVpnDetail: ImageView? = null
    var timerTextView: TextView? = null
    var connectBtnTextView: ImageView? = null
    var connectionStateTextView: TextView? = null
    var rcvFree: RecyclerView? = null
    var footer: RelativeLayout? = null
    var gifImageView1: GifImageView? = null
    var gifImageView2: GifImageView? = null
    lateinit var sharedPreferences :SharedPreferences

    @JvmField
    var imgFlag: ImageView? = null

    @JvmField
    var flagName: TextView? = null


    var facebookAdView: AdView? = null
    private var nativeAd: NativeAd? = null

    @JvmField
    var mInterstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd? = null

    @JvmField
    var facebookInterstitialAd: InterstitialAd? = null

    private var STATUS: String? = "DISCONNECTED"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        textDownloading = findViewById(R.id.downloading)

        textUploading = findViewById(R.id.uploading)

        tvConnectionStatus = findViewById(R.id.connection_status)

        ivConnectionStatusImage = findViewById(R.id.connection_status_image)

        ivVpnDetail = findViewById(R.id.vpn_details)

        timerTextView = findViewById(R.id.tv_timer)

        connectBtnTextView = findViewById(R.id.connect_btn)

        connectionStateTextView = findViewById(R.id.connection_state)

        imgFlag = findViewById(R.id.flag_image)

        rcvFree = findViewById(R.id.rcv_free)


        flagName = findViewById(R.id.flag_name)

        footer = findViewById(R.id.footer)

        frameLayout = findViewById(R.id.fl_adplaceholder)

        nativeAdLayout = findViewById(R.id.native_ad_container)

        gifImageView1 = findViewById(R.id.gifImageView1)
        gifImageView2 = findViewById(R.id.gifImageView2)

        connectBtnTextView?.setOnClickListener {
            btnConnectDisconnect()
        }


        val img = findViewById<View>(R.id.rate_us) as ImageView
        img.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(this.resources.getString(R.string.share_link))
            startActivity(intent)
        }

        findViewById<View>(R.id.term_link).setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(this.resources.getString(R.string.term_link))
            startActivity(intent)
        }


        findViewById<View>(R.id.play_link).setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Your Application name")
                var shareMessage = "\n Download this app now and get many server \n\n"
                shareMessage =
                    """
         ${shareMessage}${this.resources.getString(R.string.share_link)}
        
        """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "choose one"))
            } catch (e: Exception) {

            }
        }



        findViewById<View>(R.id.ic_crown).setOnClickListener {
            if(Constants.FREE_SERVERS != "" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                showMessage("Loading servers. Please try again", "")
        }

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init()
        tvIpAddress = findViewById<TextView>(R.id.tv_ip_address)
        showIP()


        lottieAnimationView = findViewById(R.id.animation_view)

        ivVpnDetail?.setOnClickListener {
            if(Constants.FREE_SERVERS != "server" && Constants.PREMIUM_SERVERS != "")
                showServerList()
            else
                showMessage("Loading servers. Please try again", "")
        }


    }




    private fun showIP() {
        val queue = Volley.newRequestQueue(this)
        val urlip = "https://checkip.amazonaws.com/"

        val stringRequest =
                StringRequest(Request.Method.GET, urlip, { response -> tvIpAddress?.setText(response) })
                { e ->
                    run {
                        tvIpAddress?.setText(getString(R.string.app_name))
                    }
                }
        queue.add(stringRequest)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onPause() {
        super.onPause()

    }

    override fun onDestroy() {
        if (nativeAd != null) {
            nativeAd!!.destroy()
        }
        super.onDestroy()
    }

    private fun showOrHideAppendLayout() {
        if (footer!!.visibility == View.VISIBLE) {
            ivVpnDetail!!.setImageResource(R.drawable.ic_drop_down)
            footer!!.visibility = View.GONE
        } else {
            ivVpnDetail!!.setImageResource(R.drawable.ic_up)
            footer!!.visibility = View.VISIBLE
        }
    }

    private val mUIHandler = Handler(Looper.getMainLooper())
    val mUIUpdateRunnable: Runnable = object : Runnable {
        override fun run() {

            checkRemainingTraffic()
            mUIHandler.postDelayed(this, 10000)
        }
    }

    private fun btnConnectDisconnect() {
        if (STATUS != "DISCONNECTED") {
            disconnectAlert()
        } else {
            if (!Utility.isOnline(applicationContext)) {
                showMessage("No Internet Connection", "error")
            } else {
                checkSelectedCountry()
            }
        }
    }

    protected abstract fun checkRemainingTraffic()

    protected fun updateUI(status:String) {

        when (status) {
            "CONNECTED" -> {
                STATUS = "CONNECTED"
                textDownloading!!.visibility = View.VISIBLE
                textUploading!!.visibility = View.VISIBLE
                gifImageView1!!.setBackgroundResource(R.drawable.gif)
                gifImageView2!!.setBackgroundResource(R.drawable.gif)
                connectBtnTextView!!.isEnabled = true
                connectionStateTextView!!.setText(R.string.connected)
                timerTextView!!.visibility = View.GONE
                hideConnectProgress()
                showIP()

                Glide.with(this).load(R.drawable.gif).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.gif).into(gifImageView2!!)

                connectBtnTextView!!.visibility = View.VISIBLE
                tvConnectionStatus!!.text = "Selected"
                lottieAnimationView!!.visibility = View.GONE
                Toasty.success(this@ContentsActivity, "Server Connected", Toast.LENGTH_SHORT).show()
            }
            "AUTH" -> {
                STATUS = "AUTHENTICATION"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.auth)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "WAIT" -> {
                STATUS = "WAITING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.wait)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "RECONNECTING" -> {
                STATUS = "RECONNECTING"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.recon)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "LOAD" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.connecting)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "ASSIGN_IP" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.assign_ip)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "GET_CONFIG" -> {
                STATUS = "LOAD"
                connectBtnTextView!!.visibility = View.VISIBLE
                lottieAnimationView!!.visibility = View.VISIBLE

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectionStateTextView!!.setText(R.string.config)
                connectBtnTextView!!.isEnabled = true
                timerTextView!!.visibility = View.GONE
            }
            "USERPAUSE" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_power)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.paused)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
            }
            "NONETWORK" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
                showIP()

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_power)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.nonetwork)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
            }
            "DISCONNECTED" -> {
                STATUS = "DISCONNECTED"
                tvConnectionStatus!!.text = "Not Selected"
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
                timerTextView!!.visibility = View.INVISIBLE
                hideConnectProgress()
                showIP()

                Glide.with(this).load(R.drawable.static_img).into(gifImageView1!!)
                Glide.with(this).load(R.drawable.static_img).into(gifImageView2!!)

                connectBtnTextView!!.setImageResource(R.drawable.ic_power)
                tvConnectionStatus!!.text = "Not Selected"
                connectionStateTextView!!.setText(R.string.disconnected)
                ivConnectionStatusImage!!.setImageResource(R.drawable.ic_dot)
            }
        }
    }

    protected fun hideConnectProgress() {
        connectionStateTextView!!.visibility = View.VISIBLE
    }

    protected fun disconnectAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Do you want to disconnect?")
        builder.setPositiveButton(
                "Disconnect"
        ) { _, _ ->
            disconnectFromVpn()
            STATUS = "DISCONNECTED"

            textDownloading!!.text = "0.0 kB/s"
            textUploading!!.text = "0.0 kB/s"

            showMessage("Server Disconnected", "success")
        }
        builder.setNegativeButton(
                "Cancel"
        ) { _, _ ->
            showMessage("VPN Remains Connected", "success")
        }
        builder.show()
    }

    private fun populateUnifiedNativeAdView(
            nativeAd:com.google.android.gms.ads.nativead.NativeAd,
            adView:NativeAdView
    ) {
        val mediaView: MediaView = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView!!.bodyView.visibility = View.INVISIBLE
        } else {
            adView!!.bodyView.visibility = View.VISIBLE
            (adView!!.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView!!.callToActionView.visibility = View.INVISIBLE
        } else {
            adView!!.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView!!.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd!!.icon.drawable
            )
            adView!!.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView!!.priceView.visibility = View.INVISIBLE
        } else {
            adView!!.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView!!.storeView.visibility = View.INVISIBLE
        } else {
            adView!!.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView!!.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView!!.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView!!.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView!!.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
    }

    private fun refreshAd() {
        val adLoader = AdLoader.Builder(this, MainActivity.indratech_fast_27640849_aad_native_id)
                .forNativeAd { nativeAd ->
                    frameLayout!!.visibility = View.VISIBLE
                    val adView = layoutInflater
                            .inflate(R.layout.ad_unified_indratech, null) as NativeAdView
                    if (canShowAd) {
                        populateUnifiedNativeAdView(nativeAd, adView)
                        frameLayout!!.removeAllViews()
                        frameLayout!!.addView(adView)
                    }
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                    }
                })
                .withNativeAdOptions(
                        NativeAdOptions.Builder()
                                .build()
                )
                .build()

        adLoader.loadAd(
                AdRequest.Builder()
                        .build()
        )
    }

    private fun showServerList() {
        startActivity(Intent(this, Servers::class.java))
    }

    fun updateSubscription() {
        if (canShowAd) {

            Log.v("UPDATESUBS", "onStart----: ")
            if (MainActivity.type == "ad") {
                refreshAd()
            } else {
                val nativeAd: NativeAd = NativeAd(this, MainActivity.indratech_fast_27640849_fb_native_id)
                val nativeAdListener: NativeAdListener = object : NativeAdListener {
                    override fun onMediaDownloaded(ad: Ad) {}
                    override fun onError(ad: Ad, adError: AdError) {
                        Log.w("AdLoader", "" + MainActivity.indratech_fast_27640849_fb_native_id)
                        Log.w("AdLoader", "onAdFailedToLoad" + adError.errorMessage)
                    }

                    override fun onAdLoaded(ad: Ad) {
                        if (nativeAd == null || nativeAd !== ad) {
                            return
                        }
                        nativeAd.unregisterView()
                        if (!Config.vip_subscription && !Config.all_subscription) {
                            nativeAdLayout!!.visibility = View.VISIBLE
                        }
                        val inflater = LayoutInflater.from(this@ContentsActivity)
                        val adView = inflater.inflate(
                                R.layout.native_banner_ad_layout_indratech,
                                nativeAdLayout,
                                false
                        ) as LinearLayout
                        nativeAdLayout!!.addView(adView)
                        val adChoicesContainer: LinearLayout =
                                nativeAdLayout!!.findViewById<LinearLayout>(R.id.ad_choices_container)
                        val adOptionsView = AdOptionsView(this@ContentsActivity, nativeAd, nativeAdLayout)
                        adChoicesContainer.removeAllViews()
                        adChoicesContainer.addView(adOptionsView, 0)
                        val nativeAdIcon: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_icon)
                        val nativeAdTitle = adView.findViewById<TextView>(R.id.native_ad_title)
                        val nativeAdMedia: com.facebook.ads.MediaView =
                                adView.findViewById(R.id.native_ad_media)
                        val nativeAdSocialContext =
                                adView.findViewById<TextView>(R.id.native_ad_social_context)
                        val nativeAdBody = adView.findViewById<TextView>(R.id.native_ad_body)
                        val sponsoredLabel =
                                adView.findViewById<TextView>(R.id.native_ad_sponsored_label)
                        val nativeAdCallToAction =
                                adView.findViewById<Button>(R.id.native_ad_call_to_action)
                        nativeAdTitle.text = nativeAd.advertiserName
                        nativeAdBody.text = nativeAd.adBodyText
                        nativeAdSocialContext.text = nativeAd.adSocialContext
                        nativeAdCallToAction.visibility =
                                if (nativeAd.hasCallToAction()) View.VISIBLE else View.INVISIBLE
                        nativeAdCallToAction.text = nativeAd.adCallToAction
                        sponsoredLabel.text = nativeAd.sponsoredTranslation
                        val clickableViews: MutableList<View> = ArrayList()
                        clickableViews.add(nativeAdTitle)
                        clickableViews.add(nativeAdCallToAction)
                        nativeAd.registerViewForInteraction(
                                adView, nativeAdMedia, nativeAdIcon, clickableViews
                        )
                    }

                    override fun onAdClicked(ad: Ad) {}
                    override fun onLoggingImpression(ad: Ad) {}
                }
                nativeAd.loadAd(
                        nativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener)
                                .build()
                )
            }
        }
    }

    private fun loadAds(id: Int) {

        if (canShowAd) {
            if (MainActivity.type == "ad") {
                if(loadingAd == false) {

                    val adRequest = AdRequest.Builder().build()
                    loadingAd = true

                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
                            MainActivity.admob_interstitial_id,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                                       mInterstitialAdMob = interstitialAd
                                    Log.i("INTERSTITIAL", "onAdLoaded")
                                    loadingAd = false

                                    if (mInterstitialAdMob != null) {

                                        mInterstitialAdMob!!.show(this@ContentsActivity)

                                        mInterstitialAdMob!!.setFullScreenContentCallback(object :
                                                FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                  Log.d("TAG", "The ad was dismissed.")
                                                proceedToNextView(id)
                                            }

                                            fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                                 Log.d("TAG", "The ad failed to show.")
                                                proceedToNextView(id)
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                  mInterstitialAdMob = null
                                                Log.d("TAG", "The ad was shown.")
                                            }
                                        })
                                    } else {
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                        proceedToNextView(id)
                                    }
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                                    Log.i("INTERSTITIAL", loadAdError.message)
                                    proceedToNextView(id)
                                    loadingAd = false
                                    mInterstitialAdMob = null
                                }
                            })
                }
            } else {
                AudienceNetworkAds.initialize(this@ContentsActivity)
                val interstitialAdListener: InterstitialAdListener =
                        object : InterstitialAdListener {
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                proceedToNextView(id)
                            }

                            override fun onError(ad: Ad, adError: com.facebook.ads.AdError) {
                                Log.d(
                                        TAG,
                                        "An error occurred when loading ad ${adError.errorMessage}"
                                )
                                proceedToNextView(id)
                            }

                            override fun onAdLoaded(ad: Ad) {
                                facebookInterstitialAd!!.show()
                            }

                            override fun onAdClicked(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                        }

                facebookInterstitialAd = InterstitialAd(
                        this@ContentsActivity,
                        MainActivity.indratech_fast_27640849_fb_interstitial_id
                )
                facebookInterstitialAd!!.loadAd(
                        facebookInterstitialAd!!
                                .buildLoadAdConfig()
                                .withAdListener(interstitialAdListener)
                                .build()
                )
            }
        } else {
            proceedToNextView(id)
        }
    }

    private val canShowAd: Boolean
        get() = MainActivity.indratech_fast_27640849_all_ads_on_off &&
                !Config.ads_subscription &&
                !Config.all_subscription &&
                !Config.vip_subscription

    private fun proceedToNextView(id: Int) {
        when (id) {

        }

    }

    companion object {
        protected val TAG = MainActivity::class.java.simpleName
    }

    protected fun showMessage(msg: String?, type:String) {

        if(type == "success") {
            Toasty.success(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        } else if (type == "error") {
            Toasty.error(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        } else {
            Toasty.normal(
                    this@ContentsActivity,
                    msg + "",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    open fun updateConnectionStatus(
            duration: String?,
            lastPacketReceive: String?,
            byteIn: String,
            byteOut: String
    ) {
        val byteinKb = byteIn.split("-").toTypedArray()[1]
        val byteoutKb = byteOut.split("-").toTypedArray()[1]

        textDownloading!!.text = byteinKb
        textUploading!!.text = byteoutKb
        timerTextView!!.text = duration
    }

    fun showInterstitialAndConnect() {

        if (MainActivity.indratech_fast_27640849_all_ads_on_off && !Config.ads_subscription && !Config.all_subscription && !Config.vip_subscription) {
            if (MainActivity.type == "ad") {
                if (loadingAd == false) {

                    loadingAd = true
                    val adRequest = AdRequest.Builder().build()

                    com.google.android.gms.ads.interstitial.InterstitialAd.load(this@ContentsActivity,
                            MainActivity.admob_interstitial_id,
                            adRequest,
                            object : InterstitialAdLoadCallback() {
                                override fun onAdLoaded(interstitialAd: com.google.android.gms.ads.interstitial.InterstitialAd) {
                                     mInterstitialAdMob = interstitialAd
                                    Log.i("INTERSTITIAL", "onAdLoaded")
                                    loadingAd = false

                                    if (mInterstitialAdMob != null) {

                                        mInterstitialAdMob!!.show(this@ContentsActivity)

                                        mInterstitialAdMob!!.setFullScreenContentCallback(object :
                                                FullScreenContentCallback() {
                                            override fun onAdDismissedFullScreenContent() {
                                                 Log.d("TAG", "The ad was dismissed.")
                                                prepareVpn()
                                            }

                                            fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                                 Log.d("TAG", "The ad failed to show.")
                                                prepareVpn()
                                            }

                                            override fun onAdShowedFullScreenContent() {
                                                    mInterstitialAdMob = null
                                                Log.d("TAG", "The ad was shown.")
                                            }
                                        })
                                    } else {
                                        Log.d("TAG", "The interstitial ad wasn't ready yet.")
                                        prepareVpn()
                                    }
                                }

                                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                                    Log.i("INTERSTITIAL", loadAdError.message)
                                    loadingAd = false
                                    mInterstitialAdMob = null
                                }
                            })

                }

            } else if (MainActivity.type == "fb") {
                AudienceNetworkAds.initialize(this@ContentsActivity)

                val interstitialAdListener: InterstitialAdListener =
                        object : InterstitialAdListener {
                            override fun onInterstitialDisplayed(ad: Ad) {}
                            override fun onInterstitialDismissed(ad: Ad) {
                                prepareVpn()
                            }

                            override fun onError(
                                    ad: Ad,
                                    adError: com.facebook.ads.AdError
                            ) {
                                Log.v("CHECKADS", adError.errorMessage)
                                prepareVpn()
                            }

                            override fun onAdLoaded(ad: Ad) {
                                facebookInterstitialAd!!.show()
                                Log.v("CHECKADS", "loaded")
                            }

                            override fun onAdClicked(ad: Ad) {}
                            override fun onLoggingImpression(ad: Ad) {}
                        }

                facebookInterstitialAd = InterstitialAd(
                        this@ContentsActivity,
                        MainActivity.indratech_fast_27640849_fb_interstitial_id
                )
                facebookInterstitialAd!!.loadAd(
                        facebookInterstitialAd!!.buildLoadAdConfig()
                                .withAdListener(interstitialAdListener).build()
                )

            } else {
                prepareVpn()
            }
        } else {
            prepareVpn()
        }
    }

    protected abstract fun checkSelectedCountry()
    protected abstract fun prepareVpn()
    protected abstract fun disconnectFromVpn()
}