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

package us.nineworlds.serenity.ui.browser.movie;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.model.VideoContentInfo;
import us.nineworlds.serenity.ui.adapters.AbstractPosterImageGalleryAdapter;
import us.nineworlds.serenity.ui.listeners.AbstractVideoOnItemSelectedListener;
import us.nineworlds.serenity.ui.util.ImageInfographicUtils;

/**
 * When a poster is selected, update the information displayed in the browser.
 *
 * @author dcarver
 */
public class MoviePosterOnItemSelectedListener extends AbstractVideoOnItemSelectedListener {

  private AbstractPosterImageGalleryAdapter adapter;

  public MoviePosterOnItemSelectedListener(AbstractPosterImageGalleryAdapter adapter) {
    super();
    this.adapter = adapter;
  }

  @Override protected void createVideoDetail(ImageView v) {
    Activity context = getActivity(v.getContext());
    View cardView = context.findViewById(R.id.video_details_container);
    if (cardView != null) {
      cardView.setVisibility(View.VISIBLE);
    }

    ImageView posterImage = context.findViewById(R.id.video_poster);
    posterImage.setVisibility(View.VISIBLE);
    posterImage.setScaleType(ScaleType.FIT_XY);
    posterImage.setMaxWidth(posterImage.getWidth());
    posterImage.setMaxHeight(posterImage.getHeight());

    Glide.with(context).load(videoInfo.getImageURL()).fitCenter().into(posterImage);

    TextView summary = context.findViewById(R.id.movieSummary);
    summary.setText(videoInfo.getSummary());

    TextView title = context.findViewById(R.id.movieBrowserPosterTitle);
    title.setText(videoInfo.getTitle());

    int width = context.getResources().getDimensionPixelSize(R.dimen.info_graphic_width);
    int height = context.getResources().getDimensionPixelSize(R.dimen.info_graphic_height);

    ImageInfographicUtils imageUtilsNormal = new ImageInfographicUtils(width, height);

    ImageView crv = imageUtilsNormal.createContentRatingImage(videoInfo.getContentRating(), context);
    Drawable drawable = crv.getDrawable();

    BitmapDrawable bmd = (BitmapDrawable) drawable;

    Bitmap bitmap = bmd.getBitmap();

    Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, width, height, false);

    title.setCompoundDrawablesWithIntrinsicBounds(null, null,
        new BitmapDrawable(v.getContext().getResources(), bitmapResized), null);
  }

  @Override protected void createVideoMetaData(ImageView v) {
    super.createVideoMetaData(v);
    Activity context = getActivity(v.getContext());
    TextView subt = context.findViewById(R.id.subtitleFilter);
    if (subt != null) {
      subt.setVisibility(View.GONE);
    }

    Spinner subtitleSpinner = context.findViewById(R.id.videoSubtitle);
    if (subtitleSpinner != null) {
      subtitleSpinner.setVisibility(View.GONE);
    }
  }

  @Override public void fetchSubtitle(VideoContentInfo mpi) {
    super.fetchSubtitle(mpi);
  }

  @Override public void onItemSelected(View view, int i) {
    Activity context = getActivity(view.getContext());
    if (context.isDestroyed()) {
      return;
    }

    if (i > adapter.getItemCount()) {
      return;
    }

    position = i;
    if (position < 0) {
      position = 0;
    }
    //		fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);

    videoInfo = (VideoContentInfo) adapter.getItem(position);
    if (videoInfo == null) {
      return;
    }

    changeBackgroundImage(context);

    ImageView posterImageView = view.findViewById(R.id.posterImageView);
    currentView = posterImageView;

    createVideoDetail(posterImageView);
    createVideoMetaData(posterImageView);
    createInfographicDetails(posterImageView);
  }
}
