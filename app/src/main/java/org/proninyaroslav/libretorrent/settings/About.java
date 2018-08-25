package org.proninyaroslav.libretorrent.settings;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.proninyaroslav.libretorrent.BuildConfig;
import org.proninyaroslav.libretorrent.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import osmandroid.project_basics.Task;


public class About extends AppCompatActivity implements BillingProcessor.IBillingHandler{


    @Bind(R.id.sponser_image)
    ImageView mSponsorImage;
    @Bind(R.id.sponsor_title)
    TextView mSponsorTitle;
    @Bind(R.id.sponsor_text)
    TextView mSponsorText;
    String mPkgName;


    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    public static final String APP_LINK = "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
    Bundle params;
    private static final String DONATE = "donate" /*"android.test.purchased"*/;

    private BillingProcessor bp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initialize();
        params = new Bundle();

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(remoteConfigSettings);
        retreiveCong();


        bp = new BillingProcessor(this, getString(R.string.base64), About.this);
        bp.initialize(); // binds
    }



    private void retreiveCong() {

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo( ).getConfigSettings( ).isDeveloperModeEnabled( )) {
            cacheExpiration = 0;
        }




        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mFirebaseRemoteConfig.activateFetched ( );

                String title = mFirebaseRemoteConfig.getString(getString(R.string.sponser_title));
                String text = mFirebaseRemoteConfig.getString(getString(R.string.sponser_text));
                String image = mFirebaseRemoteConfig.getString(getString(R.string.sponser_image));
                String pkg = mFirebaseRemoteConfig.getString(getString(R.string.sponser_pkg_name));


                mSponsorText.setText(text);
                mSponsorTitle.setText(title);
                mPkgName = pkg;
                try {
                    Glide.with(About.this).load(image).into(mSponsorImage);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }





    public boolean MyStartActivity(Intent aIntent, Context c) {
        try {
            c.startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }
    @OnClick(R.id.moreAppsByMe)
    public void moreApps() {
        Task.MoreApps(this,"Arsal");

    }

    @OnClick(R.id.invite)
    public void invite() {

        Task.ShareApp(this,BuildConfig.APPLICATION_ID,getString(R.string.share_app_title),getString(R.string.share_app_msg));
    }

    @OnClick(R.id.followOnFb)
    public void appFb() {

        Task.FollowOnFb(this,"1558467514403713","https://www.facebook.com/pg/arsalanengr");

    }

    @OnClick(R.id.followOnTwitter)
    public void appTwitter() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=2917866169")));
    }

    @OnClick(R.id.followOnInsta)
    public void appInsta()  {
        String scheme = "http://instagram.com/_u/arsalanengr";
        String nomPackageInfo = "com.instagram.android";
        try {
            getPackageManager().getPackageInfo(nomPackageInfo, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Intent intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
        startActivity(intentAiguilleur);

    }



    @OnClick(R.id.followyoutube)
    public void appYoutube() {
        Intent intent=null;
        String youTubeURL = "https://www.youtube.com/channel/UCclw5bgq7erW1pgrqwpXoWg";
        try {
            intent =new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse(youTubeURL));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(youTubeURL));
            startActivity(intent);
        }
    }

    @OnClick(R.id.ratebrowsesimply)
    public void rate(){
        Task.RateApp(this, BuildConfig.APPLICATION_ID);

    }

    @OnClick(R.id.contact)
    public void contact(){
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact.arsalanengr@gmail.com"});
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.contact_partnership) + " - " + getString(R.string.app_name));
            startActivity(Intent.createChooser(intent, getString(R.string.send_email)));
            intent.setType("text/plain");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.no_email_app, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.coolprodownload)
    public void coolpro(){

        Task.RateApp(this, mPkgName);
    }


    @OnClick(R.id.donate)
    public void donate() {
        try {
            boolean isOneTimePurchaseSupported = bp.isOneTimePurchaseSupported();
            if(isOneTimePurchaseSupported) {
                // launch payment flow
                bp.purchase(this, DONATE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onDestroy() {
        if (bp != null) {
            bp.release();
        }
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {

        bp.consumePurchase(DONATE);

    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int errorCode, @Nullable Throwable error) {

    }

    @Override
    public void onBillingInitialized() {

    }

    private synchronized void initialize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            getWindow().setStatusBarColor(Color.BLACK);
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }


}
