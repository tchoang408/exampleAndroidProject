package musicplayer.cs371m.musicplayer

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import musicplayer.cs371m.musicplayer.databinding.SongRowBinding

// Pass in a function called clickListener that takes a view and a songName
// as parameters.  Call clickListener when the row is clicked.
class RVDiffAdapter(private val viewModel: MainViewModel,
                    private val clickListener: (songIndex : Int)->Unit)
    // https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
    // Slick adapter that provides submitList, so you don't worry about how to update
    // the list, you just submit a new one when you want to change the list and the
    // Diff class computes the smallest set of changes that need to happen.
    // NB: Both the old and new lists must both be in memory at the same time.
    // So you can copy the old list, change it into a new list, then submit the new list.
    : ListAdapter<SongInfo,
        RVDiffAdapter.ViewHolder>(Diff())
{
    companion object {
        val TAG = "RVDiffAdapter"
        var prev: View? = null
    }
    // ViewHolder pattern holds row binding
    inner class ViewHolder(val songRowBinding : SongRowBinding)
        : RecyclerView.ViewHolder(songRowBinding.root) {
        init {
            //XXX Write me.
            // Only set onclick listener when we create the holder
            //TODO: copy and paste from RVDiffAdapter example
            itemView.setOnClickListener {
                prev?.setBackgroundColor(Color.WHITE)
                prev = it
                it.setBackgroundColor(Color.YELLOW)
                clickListener(absoluteAdapterPosition)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //XXX Write me.
        // TODO: copy and paste might be correct
        val rowBinding = SongRowBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(rowBinding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //XXX Write me.
        //TODO: DONE ?
        val rowBinding = holder.songRowBinding
        val item = getItem(position)
        rowBinding.tvSongName.textSize = 20f
        rowBinding.tvSongName.setTextColor(Color.BLACK)
        rowBinding.tvTime.textSize = 20f
        rowBinding.tvSongName.text = item.name
        rowBinding.tvTime.text = item.time
        rowBinding.tvSongName.tag = item.uniqueId
    }

    class Diff : DiffUtil.ItemCallback<SongInfo>() {
        // Item identity
        override fun areItemsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
        // Item contents are the same, but the object might have changed
        override fun areContentsTheSame(oldItem: SongInfo, newItem: SongInfo): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.rawId == newItem.rawId
                    && oldItem.time == newItem.time
        }
    }
}

