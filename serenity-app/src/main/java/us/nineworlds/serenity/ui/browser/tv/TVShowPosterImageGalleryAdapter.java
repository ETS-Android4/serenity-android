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

package us.nineworlds.serenity.ui.browser.tv;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import us.nineworlds.serenity.R;
import us.nineworlds.serenity.core.model.SeriesContentInfo;

public class TVShowPosterImageGalleryAdapter extends TVShowRecyclerAdapter {

  private RecyclerView recyclerView;

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.poster_tvshow_indicator_view, parent, false);
    return new TVShowPosterViewHolder(view);
  }

  @Override
  public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    super.onAttachedToRecyclerView(recyclerView);
    this.recyclerView = recyclerView;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    SeriesContentInfo pi = tvShowList.get(position);
    TVShowViewHolder tvShowViewHolder = (TVShowViewHolder) holder;

    Context context = tvShowViewHolder.getContext();
    int width = context.getResources().getDimensionPixelSize(R.dimen.tvshow_poster_image_width);
    int height = context.getResources().getDimensionPixelSize(R.dimen.tvshow_poster_image_height);

    tvShowViewHolder.reset();
    tvShowViewHolder.createImage(pi, width, height, recyclerView);
    tvShowViewHolder.toggleWatchedIndicator(pi);
    tvShowViewHolder.getItemView().setOnClickListener((view -> onItemViewClick(view, position)));
    tvShowViewHolder.getItemView().setOnFocusChangeListener((view, focus) -> onItemViewFocusChanged(focus, view, position));
    tvShowViewHolder.getItemView().setOnLongClickListener((view) -> onItemViewLongClick(view, position));
    tvShowViewHolder.getItemView().setOnKeyListener(this);
  }

}
