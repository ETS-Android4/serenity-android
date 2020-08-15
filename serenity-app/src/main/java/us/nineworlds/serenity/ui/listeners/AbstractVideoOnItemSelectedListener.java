/**
 * The MIT License (MIT)
 * Copyright (c) 2013 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.listeners;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import com.birbit.android.jobqueue.JobManager;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import us.nineworlds.serenity.GlideApp;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.common.rest.SerenityClient;
import us.nineworlds.serenity.core.imageloader.BackgroundBitmapDisplayer;
import us.nineworlds.serenity.core.logger.Logger;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.injection.BaseInjector;
import us.nineworlds.serenity.ui.util.ImageInfographicUtils;

import javax.inject.Inject;

/**
 * Abstract class for handling video selection information. This can either be a
 * movie or a tv episode. This is primarily used in a detail view browsing
 * scenario.
 *
 * @author dcarver
 */
public abstract class AbstractVideoOnItemSelectedListener extends BaseInjector {

    @Inject
    protected SerenityClient serenityClient;
    @Inject
    protected SharedPreferences preferences;

    @Inject
    JobManager jobManager;
    @Inject
    Logger logger;

    public static final int WATCHED_VIEW_ID = 1000;
    protected View currentView;
    protected int position;
    protected VideoContentInfo videoInfo;

    protected abstract void createVideoDetail(ImageView v);

    protected void createVideoMetaData(ImageView v) {
        //        fetchSubtitle(videoInfo);
    }

    /**
     * Create the images representing info such as sound, ratings, etc based on
     * the currently selected movie poster.
     */
    protected void createInfographicDetails(ImageView v) {
        Activity context = getActivity(v.getContext());
        LinearLayout infographicsView = context.findViewById(R.id.movieInfoGraphicLayout);
        infographicsView.removeAllViews();

        int normalHeight = context.getResources().getDimensionPixelSize(R.dimen.video_infogrpahic_height);
        int normalWidth = context.getResources().getDimensionPixelSize(R.dimen.video_infographic_width);
        int audioWidth = context.getResources().getDimensionPixelSize(R.dimen.audio_infographic_width);
        int audioHeight = context.getResources().getDimensionPixelSize(R.dimen.audio_inographic_height);

        ImageInfographicUtils imageUtilsNormal = new ImageInfographicUtils(normalWidth, normalHeight);
        ImageInfographicUtils imageUtilsAudioChannel = new ImageInfographicUtils(audioWidth, audioHeight);

        TextView durationView = imageUtilsNormal.createDurationView(videoInfo.getDuration(), context);
        if (durationView != null) {
            infographicsView.addView(durationView);
        }

        ImageView resv = imageUtilsNormal.createVideoCodec(videoInfo.getVideoCodec(), v.getContext());
        if (resv != null) {
            infographicsView.addView(resv);
        }

        ImageView resolution = imageUtilsNormal.createVideoResolutionImage(videoInfo.getVideoResolution(), v.getContext());
        if (resolution != null) {
            infographicsView.addView(resolution);
        }

        ImageView aspectv = imageUtilsNormal.createAspectRatioImage(videoInfo.getAspectRatio(), context);
        if (aspectv != null) {
            infographicsView.addView(aspectv);
        }

        ImageView acv = imageUtilsNormal.createAudioCodecImage(videoInfo.getAudioCodec(), context);
        if (acv != null) {
            infographicsView.addView(acv);
        }

        ImageView achannelsv =
                imageUtilsAudioChannel.createAudioChannlesImage(videoInfo.getAudioChannels(), v.getContext());
        if (achannelsv != null) {
            infographicsView.addView(achannelsv);
        }

        if (videoInfo.getRating() != 0) {
            RatingBar ratingBar = new RatingBar(context, null, android.R.attr.ratingBarStyleIndicator);
            ratingBar.setMax(4);
            ratingBar.setIsIndicator(true);
            ratingBar.setStepSize(0.1f);
            ratingBar.setNumStars(4);
            ratingBar.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = 15;
            ratingBar.setLayoutParams(params);

            double rating = videoInfo.getRating();
            ratingBar.setRating((float) (rating / 2.5));
            infographicsView.addView(ratingBar);
        }

        ImageView studiov =
                imageUtilsNormal.createStudioImage(videoInfo.getStudio(), context, videoInfo.getMediaTagIdentifier());
        if (studiov != null) {
            infographicsView.addView(studiov);
        }
    }

    public void fetchSubtitle(VideoContentInfo mpi) {
        //        jobManager.addJobInBackground(new SubtitleJob("/library/metadata/" + mpi.id()));
    }

    //    @Subscribe(threadMode = ThreadMode.MAIN)
    //    public void onSubtitleEvent(SubtitleEvent event) {
    //        if (event.getVideoContentInfo() != null) {
    //            new SubtitleResponseListener(event.getVideoContentInfo(), context).onResponse(event.getMediaContainer());
    //        }
    //    }

    public void changeBackgroundImage(final Activity context) {

        if (videoInfo.getBackgroundURL() == null) {
            return;
        }

        final View fanArt = context.findViewById(R.id.fanArt);
        logger.debug("Background ImageUrl: " + videoInfo.getBackgroundURL());
        String transcodingURL = serenityClient.createImageURL(videoInfo.getBackgroundURL(), 1280, 720);
        logger.debug("Generated Image Url: " + transcodingURL);

        SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>(1280, 720) {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                context.runOnUiThread(new BackgroundBitmapDisplayer(resource, R.drawable.movies, fanArt));
            }
        };

        GlideApp.with(context).asBitmap().load(transcodingURL).into(target);
    }

    public abstract void onItemSelected(View view, int i);
}
