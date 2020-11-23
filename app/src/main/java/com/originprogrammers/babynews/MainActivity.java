package com.originprogrammers.babynews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aphidmobile.flip.FlipViewController;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.squareup.picasso.Picasso;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.originprogrammers.babynews.Controller.DataRepository;
import com.originprogrammers.babynews.Model.Listeners.PageRefreshListener;
import com.originprogrammers.babynews.Model.Listeners.ResultListener;
import com.originprogrammers.babynews.Model.models.NewsDetails;
import com.originprogrammers.babynews.dialogs.AboutDialogFragment;
import com.originprogrammers.babynews.dialogs.RateusDialogFragment;
import com.originprogrammers.babynews.dialogs.SupportDialogFragment;
import com.originprogrammers.babynews.utils.ConnectivityChangeReceiver;
import com.originprogrammers.babynews.utils.Prefs;

public class MainActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    PlayerView pv;
    FlipViewController.ViewFlipListener flipListener;
    MediaSource playerMediaSource;
    FlipViewController flipView;
    ArrayList<FlipViewModel> flipdata;
    private static View currentView = null;
    private boolean isVideoEnabled = false;
    public NewsViewAdapter mNewsViewAdapter;
    public int newsPaginationCount = 0;
    boolean isOptionsLayoutShowing = false;
    public LoadingActivity loadingActivity;

    private FlipViewModel forVideoModel;
    private UnifiedNativeAd nativeAd;
    private boolean isAdShowing = false;
    private int AD_POS = 15;
    private SlidingRootNav slidingRootNav;
    private ListView listView;
    private ConnectivityChangeReceiver connectivityChangeReceiver;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Prefs.initPrefs(this);
        //You can also use FlipViewController.VERTICAL
        currentView = null;
        FlipViewController.currentView = null;

        flipView = null;
        flipView = new FlipViewController(MainActivity.this, FlipViewController.VERTICAL);
        listView = findViewById(R.id.list);
        connectivityChangeReceiver = new ConnectivityChangeReceiver();
        registerNetworkBroadcastForNoughat();
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });
        String params = getParams("trending", "25", String.valueOf(newsPaginationCount), "en");
        Log.d("params->", params);
        Intent loadingIntent = new Intent(MainActivity.this, LoadingActivity.class);
        loadingIntent.putExtra("isLoading", true);
        loadingIntent.putExtra("isFromMainActivity", true);
        startActivity(loadingIntent);
        DataRepository.getNewsList(params, new ResultListener() {
            @Override
            public void getResult(Object object, boolean isSuccess) {
                if(isSuccess) {
                    LoadingActivity.stopLoadingAndFinish();
                    List<NewsDetails> newsDetails = (List<NewsDetails>) object;
                    if (newsDetails != null) {
                        Log.d("MainAct:NewsList--> ", "Size = " + newsDetails.size());
                        flipdata = new ArrayList<FlipViewModel>();
                        for (int i = 0; i < newsDetails.size(); i++) {
                            flipdata.add(new FlipViewModel("image",
                                    0,
                                    null,
                                    newsDetails.get(i).getTitle(),
                                    newsDetails.get(i).getSummarization(),
                                    newsDetails.get(i).getImageUrl(),
                                    newsDetails.get(i).getSource(),
                                    newsDetails.get(i).getPublishedDate(),
                                    newsDetails.get(i).getUrl()));
                            //Halt and add a empty view for showing ad
                            if(i == AD_POS) { //pos = 15
                                FlipViewModel emptyView = new FlipViewModel();
                                emptyView.setSourceType("advert");
                                emptyView.setImgId(0);
                                emptyView.setVideoUri(null);
                                emptyView.setText("");
                                emptyView.setNews("");
                                emptyView.setImageUrl(null);
                                flipdata.add(emptyView);
                            }

                        }
                        //We're creating a NewsViewAdapter instance, by passing in the current context and the
                        //values to display after each flip
                        mNewsViewAdapter = new NewsViewAdapter(MainActivity.this, flipdata, new PageRefreshListener() {
                            @Override
                            public void onRefreshPage() {
                                newsPaginationCount = newsPaginationCount + 25;
                                String params = getParams("trending", "25", String.valueOf(newsPaginationCount), "en");
                                Log.d("params->", params);
                                Log.d("onRefreshPage->", "Pagerefreshed");
                                DataRepository.getNewsList(params, new ResultListener() {
                                    @Override
                                    public void getResult(Object object, boolean isSuccess) {
                                        if(isSuccess) {
                                            ArrayList<FlipViewModel> moreNewsList = new ArrayList<>();
                                            List<NewsDetails> newsDetails = (List<NewsDetails>) object;
                                            if(newsDetails != null) {
                                                for (int i = 0; i < newsDetails.size(); i++) {
                                                    moreNewsList.add(new FlipViewModel("image",
                                                            0,
                                                            null,
                                                            newsDetails.get(i).getTitle(),
                                                            newsDetails.get(i).getSummarization(),
                                                            newsDetails.get(i).getImageUrl(),
                                                            newsDetails.get(i).getSource(),
                                                            newsDetails.get(i).getPublishedDate(),
                                                            newsDetails.get(i).getUrl()));

                                                    //Halt and add a empty view for showing ad
                                                    if(i == AD_POS) { //pos = 15
                                                        FlipViewModel emptyView = new FlipViewModel();
                                                        emptyView.setSourceType("advert");
                                                        emptyView.setImgId(0);
                                                        emptyView.setVideoUri(null);
                                                        emptyView.setText("");
                                                        emptyView.setNews("");
                                                        emptyView.setImageUrl(null);
                                                        moreNewsList.add(emptyView);
                                                    }

                                                }
                                                Log.d("listSizeNow->", "Size=" + moreNewsList.size());
                                                mNewsViewAdapter.updateList(moreNewsList);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                        flipView.setAdapter(mNewsViewAdapter);
//                        flipView.setAnimationBitmapFormat(new Bitmap.Config());
                        setContentView(flipView);
                        setSideMenu();
                    }
                }

            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String getParams(String endPoint, String limit, String pageSkip, String languageCode) {
        StringBuilder params = new StringBuilder();
        params.append(endPoint);
        params.append("?limit=" + limit);
        params.append("&skip=" + pageSkip);
        params.append("&langs=" + languageCode);
        return params.toString();
    }

    public class NewsViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private ArrayList<FlipViewModel> newsArrayList;
        private PageRefreshListener pageRefreshListener;
        private View mainLayout;
        private boolean isOptionsShowing = false;

        public NewsViewAdapter(Context currentContext, ArrayList<FlipViewModel> allNews, PageRefreshListener pageRefreshListener) {
            inflater = LayoutInflater.from(currentContext);
            newsArrayList = allNews;
            this.pageRefreshListener = pageRefreshListener;
        }



        @Override
        public int getCount() {
            return newsArrayList.size();
        }

        public void updateList(ArrayList<FlipViewModel> updatedList) {
//            this.newsArrayList = updatedList;
            newsArrayList.addAll(updatedList);
            Log.d("LstSizeAfterUpdate", "--> Size:" + newsArrayList.size());
            notifyDataSetChanged();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ClickableViewAccessibility")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = convertView;
            mainLayout = convertView;
            final FlipViewModel f = newsArrayList.get(position);
            if (convertView == null) {
                layout = inflater.inflate(R.layout.activity_main, null);
            }

            TextView tvHead = layout.findViewById(R.id.head_note);
            TextView tvNews = layout.findViewById(R.id.note);
            ImageView iv = layout.findViewById(R.id.iv_image);
            final LinearLayout llMenu = layout.findViewById(R.id.ll_menu);
            RelativeLayout rlShare = layout.findViewById(R.id.rl_share);
            RelativeLayout rlRead = layout.findViewById(R.id.rl_details);
            TextView tvPublished = layout.findViewById(R.id.tv_pub_date);
            TextView tvSource = layout.findViewById(R.id.tv_source);

            LinearLayout llMain = layout.findViewById(R.id.ll_main);
            LinearLayout llAdvert = layout.findViewById(R.id.ll_advert);


            llAdvert.setVisibility(View.GONE);
            llMain.setVisibility(View.VISIBLE);

            pv = layout.findViewById(R.id.video_view);
//            releasePlayer();
            Log.d("SourceType-->", f.getSourceType() + ", pos->" + position);
            if (f.getSourceType().equals("image")) {
                pv.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);
                if (f.getImgId() != 0) {
                    iv.setImageResource(f.getImgId());
                } else if(f.getImageUrl() != null) {
                    Picasso.get().load(f.getImageUrl()).into(iv);
                }
                tvHead.setText(f.getText());
                tvNews.setText(f.getNews());
                rlRead.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri.parse(f.getNewsUrl()));
                        startActivity(webIntent);
                    }
                });
                rlShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Newsmob - checkout the below trending news");
                        shareIntent.putExtra(Intent.EXTRA_TEXT,  f.getNewsUrl());
                        startActivity(Intent.createChooser(shareIntent, "Share"));
                    }
                });

                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
                    Date d = sdf.parse(f.getPublishedOn());
                    tvPublished.setText("Published on " + new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                tvSource.setText("Source: " + f.getNewsSource());
                tvSource.setMovementMethod(LinkMovementMethod.getInstance());
                tvSource.setHighlightColor(Color.BLUE);
                isVideoEnabled = false;
            } else if (f.getSourceType().equals("video")) {
                if (f.getVideoUri() != null) {
                    pv.setVisibility(View.VISIBLE);
                    iv.setVisibility(View.GONE);
                    isVideoEnabled = true;
//                releasePlayer();
                    /*stopPlayerAndRelease();
                    initializePlayerAdditionalSettings(f.getVideoUri());*/
                    /*forVideoModel = new FlipViewModel(f.getSourceType(),
                            0,
                            f.getVideoUri(),
                            f.getText());*/
//                    initializePlayer(f.getVideoUri());
//                    tv.setText(f.getText());
//                nonGlobalPlayerInit(pv, f.getVideoUri());

                }
            } else if(f.getSourceType().equals("advert")) {
                refreshAd();
            }

            //ad is loading at 16 so, show it.
            if(position == (newsArrayList.size() + 1) - 10) {
                llAdvert.setVisibility(View.VISIBLE);
                llMain.setVisibility(View.GONE);
            }

            if(position == newsArrayList.size() - 4) {
                pageRefreshListener.onRefreshPage();
            }

            llMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(slidingRootNav.isMenuClosed()) {
                        slidingRootNav.openMenu();
                    } else {
                        slidingRootNav.closeMenu();
                    }
                }
            });

            return layout;
        }
    }

    private void setSideMenu() {
        SlidingRootNavBuilder builder =
                new SlidingRootNavBuilder(this)
                .withMenuOpened(false)
                .withMenuLocked(false)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.menu_left_drawer);
        slidingRootNav = builder.inject();

        final ArrayList<String> listArray = new ArrayList<>();
        listArray.add("About");
        listArray.add("Support");
        listArray.add("Rate us");
        listArray.add("Exit");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.menu_item, listArray) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = null;
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if(convertView == null) {
                    v = inflater.inflate(R.layout.menu_item, parent, false);
                } else {
                    v = convertView;
                }

                ImageView iv = v.findViewById(R.id.iv_image);
                TextView tv = v.findViewById(R.id.tv);
                tv.setText(listArray.get(position));
                if(position == 0) {
                    iv.setImageDrawable(getDrawable(R.drawable.ic_about));
                } else if (position == 1) {
                    iv.setImageDrawable(getDrawable(R.drawable.ic_support));
                } else if (position == 2) {
                    iv.setImageDrawable(getDrawable(R.drawable.ic_rate));
                } else if (position == 3) {
                    iv.setImageDrawable(getDrawable(R.drawable.ic_exit));
                }

                return v;

            }
        };
        ListView lv = findViewById(R.id.list);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(position == 0) {
                    new AboutDialogFragment().show(getSupportFragmentManager(), "AboutDialogFragment");
                } else if (position == 1) {
                    new SupportDialogFragment().show(getSupportFragmentManager(), "SupportDialogFragment");
                } else if (position == 2) {
                    new RateusDialogFragment().show(getSupportFragmentManager(), "RateusDialogFragment");
                } else if (position == 3) {
                    finish();
                }
            }
        });
    }

    //Google advertisement loadout
    private void refreshAd() {
        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.native_advanced_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if(nativeAd == null)
                    nativeAd = unifiedNativeAd;
                CardView cardView = findViewById(R.id.ad_container);
                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad_layout, null);
                populateNativeAd(unifiedNativeAd, adView);
                cardView.removeAllViews();
                cardView.addView(adView);
//                isAdShowing = true;
            }
        });

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Toast.makeText(MainActivity.this, "Ad failed to load", Toast.LENGTH_SHORT).show();
                Log.d("AdLoader::", "adFailedToLoad-->" + loadAdError.toString());
                super.onAdFailedToLoad(loadAdError);
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAd(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_heading));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setBodyView(adView.findViewById(R.id.ad_body_text));
        adView.setStarRatingView(adView.findViewById(R.id.ad_rating));
        adView.setMediaView((MediaView) adView.findViewById(R.id.media_view));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));

        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        if(nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView)adView.getBodyView()).setText(nativeAd.getBody());
            adView.getBodyView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if(nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            adView.getCallToActionView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);

    }

    private void registerNetworkBroadcastForNoughat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(connectivityChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(connectivityChangeReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private void initializePlayer(String url) {
        player = new SimpleExoPlayer.Builder(this).build();
        pv.setPlayer(player);
        Uri uri = Uri.parse(url);
        MediaSource mediaSource = buildMediaSource(uri);
        player.setPlayWhenReady(playWhenReady);
        player.seekTo(currentWindow, playbackPosition);
        player.prepare(mediaSource, false, false);
    }

    private void initializePlayerAdditionalSettings(String url) {
        Log.d("initialize", "Url-> " + url);

        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(this);
            trackSelector.setParameters(
                    trackSelector.buildUponParameters()
                            .setMaxVideoSize(640, 480)
                            .setMaxVideoBitrate(64)
                            .setMaxAudioBitrate(64));
            LoadControl loadControl = new DefaultLoadControl.Builder()
                    .setAllocator(new DefaultAllocator(true, 16))
                    .setBufferDurationsMs(VideoPlayerConfig.MIN_BUFFER_DURATION,
                            VideoPlayerConfig.MAX_BUFFER_DURATION,
                            VideoPlayerConfig.MIN_PLAYBACK_START_BUFFER,
                            VideoPlayerConfig.MIN_PLAYBACK_RESUME_BUFFER).createDefaultLoadControl();
            player = new SimpleExoPlayer.Builder(this)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build();
            pv.setPlayer(player);
            Uri uri = Uri.parse(url);
            MediaSource playerMediaSource = buildMediaSource(uri);
            player.setPlayWhenReady(playWhenReady);
            Log.d("playWhenReady", "playWhenReady-> " + playWhenReady);
            Log.d("currentWindow", "currentWindow-> " + currentWindow);
            Log.d("playbackPosition", "playbackPosition-> " + playbackPosition);
            player.seekTo(currentWindow, playbackPosition);
            player.prepare(playerMediaSource, false, false);
        }
    }


    public class VideoPlayerConfig {
        //Minimum Video you want to buffer while Playing
        public static final int MIN_BUFFER_DURATION = 2000;
        //Max Video you want to buffer during PlayBack
        public static final int MAX_BUFFER_DURATION = 5000;
        //Min Video you want to buffer before start Playing it
        public static final int MIN_PLAYBACK_START_BUFFER = 1500;
        //Min video You want to buffer when user resumes video
        public static final int MIN_PLAYBACK_RESUME_BUFFER = 2000;
    }

    private void pausePlayer() {
        if (player != null) {
            player.setPlayWhenReady(false);
            playWhenReady = true;
        }
    }

    private MediaSource buildMediaSource(Uri uri) {
        DataSource.Factory dataSourceFactory =
                new DefaultDataSourceFactory(this, "exoplayer-flipviewtest");
        return new ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.stop(true);
            player.release();
            player = null;
        }
    }

    private void stopPlayerAndRelease() {
        if (player != null) {
            playbackPosition = 0;
            currentWindow = 0;
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    @Override
    protected void onDestroy() {
        if(nativeAd != null)
        nativeAd.destroy();
        unregisterNetworkChanges();
        super.onDestroy();
    }
}
