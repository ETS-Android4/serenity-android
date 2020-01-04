package us.nineworlds.serenity.ui.adapters

import androidx.recyclerview.widget.DiffUtil
import us.nineworlds.serenity.core.model.ContentInfo

class VideoContentInfoAdapter : AsyncListDifferDelegationAdapter<ContentInfo>(ContentInfoListDiffer()) {

  init {
    delegatesManager.addDelegate(MoviePosterAdapterDelegate())
    delegatesManager.addDelegate(TVSeriesAdapterDelegate())
  }

  class ContentInfoListDiffer : DiffUtil.ItemCallback<ContentInfo>() {
    override fun areItemsTheSame(oldItem: ContentInfo, newItem: ContentInfo): Boolean = oldItem.id() == newItem.id()

    override fun areContentsTheSame(oldItem: ContentInfo, newItem: ContentInfo): Boolean = oldItem.id() == newItem.id()
  }

  fun getItemAtPosition(position: Int) : ContentInfo = differ.currentList[position]

}