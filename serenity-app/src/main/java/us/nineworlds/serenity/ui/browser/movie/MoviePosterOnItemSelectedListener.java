/**
 * The MIT License (MIT)
 * Copyright (c) 2012 David Carver
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package us.nineworlds.serenity.ui.browser.movie;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import us.nineworlds.serenity.SerenityApplication;
import us.nineworlds.serenity.core.imageloader.SerenityBackgroundLoaderListener;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.ui.browser.tv.episodes.EpisodePosterOnItemSelectedListener;
import us.nineworlds.serenity.ui.util.ImageInfographicUtils;
import us.nineworlds.serenity.ui.views.SerenityPosterImageView;
import us.nineworlds.serenity.widgets.SerenityAdapterView;
import us.nineworlds.serenity.widgets.SerenityAdapterView.OnItemSelectedListener;

import us.nineworlds.serenity.R;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * When a poster is selected, update the information displayed in the browser.
 * 
 * @author dcarver
 * 
 */
public class MoviePosterOnItemSelectedListener implements
		OnItemSelectedListener {

	private static final String CRLF = "\r\n";
	private View bgLayout;
	private Activity context;
	private ImageLoader imageLoader;
	private View previous;
	private ImageSize bgImageSize = new ImageSize(1280, 720);

	/**
	 * 
	 */
	public MoviePosterOnItemSelectedListener(View bgv, Activity activity) {
		bgLayout = bgv;
		context = activity;
		imageLoader = SerenityApplication.getImageLoader();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android
	 * .widget.AdapterView, android.view.View, int, long)
	 */
	public void onItemSelected(SerenityAdapterView<?> av, View v, int position,
			long id) {

		if (previous != null) {
			previous.setPadding(0, 0, 0, 0);
		}

		previous = v;

		v.setPadding(5, 5, 5, 5);

		createMovieDetail((SerenityPosterImageView) v);
		createMovieMetaData((SerenityPosterImageView) v);
		createInfographicDetails((SerenityPosterImageView) v);
		changeBackgroundImage(v);

	}

	private void createMovieDetail(SerenityPosterImageView v) {
		TextView summary = (TextView) context.findViewById(R.id.movieSummary);
		summary.setText(v.getPosterInfo().getPlotSummary());

		TextView title = (TextView) context
				.findViewById(R.id.movieBrowserPosterTitle);
		title.setText(v.getPosterInfo().getTitle());
	}

	private void createMovieMetaData(SerenityPosterImageView v) {
		SerenityPosterImageView mpiv = (SerenityPosterImageView) v;
		VideoContentInfo mi = mpiv.getPosterInfo();
		TextView ty = (TextView) context.findViewById(R.id.video_year);
		TextView tg = (TextView) context.findViewById(R.id.video_genre);
		TextView tw = (TextView) context.findViewById(R.id.video_writers);
		TextView td = (TextView) context.findViewById(R.id.video_directors);

		ty.setText(context.getString(R.string.unknown));
		tg.setText(context.getString(R.string.unknown));
		tw.setText(context.getString(R.string.unknown));
		td.setText(context.getString(R.string.unknown));

		if (mi.getYear() != null) {
			ty.setText(mi.getYear());
		}

		if (mi.getGenres() != null) {
			StringBuilder details = new StringBuilder();
			for (String genre : mi.getGenres()) {
				details.append(genre);
				details.append(CRLF);
			}
			tg.setText(details.toString());
		}

		if (mi.getWriters() != null) {
			StringBuilder details = new StringBuilder();
			for (String writers : mi.getWriters()) {
				details.append(writers);
				details.append(CRLF);
			}
			tw.setText(details.toString());
		}

		if (mi.getDirectors() != null) {
			StringBuilder details = new StringBuilder();
			for (String directors : mi.getDirectors()) {
				details.append(directors);
				details.append(CRLF);
			}
			td.setText(details.toString());
		}

	}

	/**
	 * Change the background image of the activity.
	 * 
	 * @param v
	 */
	private void changeBackgroundImage(View v) {
		SerenityPosterImageView mpiv = (SerenityPosterImageView) v;
		VideoContentInfo mi = mpiv.getPosterInfo();

		if (mi.getBackgroundURL() == null) {
			return;
		}

		imageLoader.loadImage(mi.getBackgroundURL(), bgImageSize,
				new SerenityBackgroundLoaderListener(bgLayout,
						R.drawable.movies));
	}

	/**
	 * Create the images representing info such as sound, ratings, etc based on
	 * the currently selected movie poster.
	 * 
	 * @param position
	 */
	private void createInfographicDetails(SerenityPosterImageView v) {
		LinearLayout infographicsView = (LinearLayout) context
				.findViewById(R.id.movieInfoGraphicLayout);
		infographicsView.removeAllViews();
		VideoContentInfo mpi = v.getPosterInfo();

		ImageView viewed = new ImageView(context);
		viewed.setScaleType(ScaleType.FIT_XY);
		LinearLayout.LayoutParams viewedlp = new LinearLayout.LayoutParams(80,
				58);
		viewedlp.setMargins(10, 0, 5, 5);
		viewed.setLayoutParams(viewedlp);
		viewed.setId(EpisodePosterOnItemSelectedListener.WATCHED_VIEW_ID);

		if (mpi.getViewCount() > 0) {
			viewed.setImageResource(R.drawable.watched_small);
		} else {
			viewed.setImageResource(R.drawable.unwatched_small);
		}
		infographicsView.addView(viewed);

		ImageInfographicUtils imageUtilsWide = new ImageInfographicUtils(154,
				58);
		ImageInfographicUtils imageUtilsNormal = new ImageInfographicUtils(100,
				58);

		ImageView acv = imageUtilsWide.createAudioCodecImage(
				mpi.getAudioCodec(), context);
		if (acv != null) {
			infographicsView.addView(acv);
		}

		ImageView achannelsv = imageUtilsWide.createAudioChannlesImage(
				mpi.getAudioChannels(), v.getContext());
		if (achannelsv != null) {
			infographicsView.addView(achannelsv);
		}

		ImageView resv = imageUtilsWide.createVideoResolutionImage(
				mpi.getVideoResolution(), context);
		if (resv != null) {
			infographicsView.addView(resv);
		}

		ImageView aspectv = imageUtilsNormal.createAspectRatioImage(
				mpi.getAspectRatio(), context);
		if (aspectv != null) {
			infographicsView.addView(aspectv);
		}

		ImageView crv = imageUtilsWide.createContentRatingImage(
				mpi.getContentRating(), context);
		infographicsView.addView(crv);

		infographicsView.refreshDrawableState();
	}

	public void onNothingSelected(SerenityAdapterView<?> av) {
		if (previous != null) {
			previous.setPadding(0, 0, 0, 0);
			previous.refreshDrawableState();
		}

	}

}