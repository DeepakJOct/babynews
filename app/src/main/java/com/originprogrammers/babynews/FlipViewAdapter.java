package com.originprogrammers.babynews;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.util.ArrayList;

public class FlipViewAdapter extends BaseAdapter {

    private final ArrayList<FlipViewModel> flipViewList;
    private Context context;
    private SimpleExoPlayer player;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    public FlipViewAdapter(Context context, ArrayList<FlipViewModel> flipViewList) {
        Log.d("FlipViewAdapter->" , "Called");
        this.flipViewList = flipViewList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return flipViewList.size();
    }

    @Override
    public FlipViewModel getItem(int pos) {
        return flipViewList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("FlipViewAdapter->" , "GetViewCalled");
        ViewHolder vh;

        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.activity_main, parent, false);

            vh = new ViewHolder();
            vh.imageView = convertView.findViewById(R.id.iv_image);
            vh.pv = convertView.findViewById(R.id.video_view);
            vh.tvText = convertView.findViewById(R.id.note);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        FlipViewModel f = getItem(position);
//        Log.d("ModelData->", "" + f.getSourceType() + ", text: " + f.getText() + ", Position: " + position);

        if(f != null) {
            Log.d("ModelData->", "" + f.getSourceType() + ", text: " + f.getText() + ", Position: " + position);
            Log.d("SourceType-->", f.getSourceType() + ", pos->" + position);
            if(f.getSourceType().equals("image") && f.getImgId() != 0) {
                vh.pv.setVisibility(View.GONE);
                vh.imageView.setVisibility(View.VISIBLE);
                vh.imageView.setImageResource(f.getImgId());
                vh.tvText.setText(f.getText());
                if(player!= null && player.isLoading() || player != null && player.isPlaying()) {
                    player.stop();
                    player = null;
                }
            } else if(f.getSourceType().equals("video") && f.getVideoUri() != null) {
                if (f.getVideoUri() != null) {
                    vh.imageView.setVisibility(View.GONE);
                    vh.pv.setVisibility(View.VISIBLE);
//                releasePlayer();
                    stopPlayerAndRelease();
                    initializePlayerAdditionalSettings(f.getVideoUri(), vh);
                    vh.tvText.setText(f.getText());
//                nonGlobalPlayerInit(pv, f.getVideoUri());

                }
            } else {
                stopPlayerAndRelease();
            }

            /*if(vh.pv.getVisibility() == View.VISIBLE && vh.pv.getVideoSurfaceView().getVisibility() == View.VISIBLE) {
                stopPlayerAndRelease();
                initializePlayerAdditionalSettings(f.getVideoUri(), vh);
            }*/
        }


        return convertView;
    }

    private void initializePlayerAdditionalSettings(String url, ViewHolder viewHolder) {
        Log.d("initialize", "Url-> " + url);

        if (player == null) {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(context);
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
            player = new SimpleExoPlayer.Builder(context)
                    .setTrackSelector(trackSelector)
                    .setLoadControl(loadControl)
                    .build();
            viewHolder.pv.setPlayer(player);
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
                new DefaultDataSourceFactory(context, "exoplayer-flipviewtest");
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


    class ViewHolder {
        /**
         * The Tv ttem.
         */
        TextView tvText;
        /**
         * The Imv cat.
         */
        ImageView imageView;
        /**
         * The Ll spinner.
         */
        PlayerView pv;
    }
}
