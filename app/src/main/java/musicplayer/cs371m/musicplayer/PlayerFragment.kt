package musicplayer.cs371m.musicplayer
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import musicplayer.cs371m.musicplayer.MainActivity.Companion.setBackgroundDrawable
import musicplayer.cs371m.musicplayer.databinding.PlayerFragmentBinding
import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicBoolean


class PlayerFragment : Fragment() {
    // When this is true, the displayTime coroutine should not modify the seek bar
    val userModifyingSeekBar = AtomicBoolean(false)
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var adapter: RVDiffAdapter

    private var _binding: PlayerFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var countOnce = true
    private var songClick = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRecyclerViewDividers(rv: RecyclerView) {
        // Let's have dividers between list items
        val dividerItemDecoration = DividerItemDecoration(
            rv.context, LinearLayoutManager.VERTICAL
        )
        rv.addItemDecoration(dividerItemDecoration)
    }

    // Please put all display updates in this function
    // The exception is that
    //   displayTime updates the seek bar, time passed & time remaining
    private fun updateDisplay() {
        // If settings is active, we are in the background and do
        // not have a binding.  Return early.
        if (_binding == null) {
            return
        }
        //XXX Write me. Make sure all player UI elements are up to date
        // That includes all buttons, textViews, icons & the seek bar

        if(viewModel.loop){
            binding.loopIndicator.setBackgroundColor(Color.RED)
            viewModel.player.isLooping = true
        }
        else{
            binding.loopIndicator.setBackgroundColor(Color.WHITE)
            viewModel.player.isLooping = false

        }

        if(viewModel.isPlaying){
            setBackgroundDrawable(binding.playerPlayPauseButton,R.drawable.ic_pause_black_24dp)
        }
        else{
            setBackgroundDrawable(binding.playerPlayPauseButton,R.drawable.ic_play_arrow_black_24dp)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Make the RVDiffAdapter and set it up

        //XXX Write me. Setup adapter.
        adapter = RVDiffAdapter(viewModel, this::adapterOnClick)

        binding.playerRV.adapter = adapter

        binding.playerRV.layoutManager = LinearLayoutManager(binding.playerRV.context)

        // MyComment: add decoration line to list
        initRecyclerViewDividers(binding.playerRV)
        val list = viewModel.getCopyOfSongInfo()
        adapter.submitList(list)
        //XXX Write me. Write callbacks for buttons

        binding.playerPlayPauseButton.setOnClickListener(){
            Log.d("Pause", "is click")
            val btn = it as ImageButton

            // Corner case when play, increment soudns counts
            if(countOnce) {
                viewModel.songsPlayed++
                countOnce = false
            }

            // Initial state
            if(btn.tag == R.drawable.ic_play_arrow_black_24dp){
                setBackgroundDrawable(btn,R.drawable.ic_pause_black_24dp)
                viewModel.player.start()
                viewModel.isPlaying = true
                if(songClick){
                    viewModel.songsPlayed++
                    songClick = false
                }
            }
            else if(btn.tag == R.drawable.ic_pause_black_24dp){
                setBackgroundDrawable(btn,R.drawable.ic_play_arrow_black_24dp)
                viewModel.player.pause()
                viewModel.isPlaying = false
            }
        }

        viewModel.player.setOnCompletionListener(){
            viewModel.nextSong()
        }

        binding.playerSkipForwardButton.setOnClickListener(){
            viewModel.nextSong()

        }

        binding.playerSkipBackButton.setOnClickListener(){
            viewModel.prevSong()
        }

        binding.playerPermuteButton.setOnClickListener {
            adapter.submitList(viewModel.shuffleAndReturnCopyOfSongInfo())
        }

        //XXX Write me. binding.playerSeekBar.setOnSeekBarChangeListener
        binding.playerSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                // here, you react to the value being set in seekBar
                //seekBar.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
                userModifyingSeekBar.set(true)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // you can probably leave this empty
                seekBar.progress = seekBar.progress
                viewModel.player.seekTo(seekBar.progress)
                userModifyingSeekBar.set(false)
            }
        })
        updateDisplay()

        // Don't change this code.  We are launching a coroutine (user-level thread) that will
        // execute concurrently with our code, but will update the displayed time
        val millisec = 100L
        viewLifecycleOwner.lifecycleScope.launch {
            displayTime(millisec)
        }
        updateNextSongNowPlaying(500L)

    }

    // The suspend modifier marks this as a function called from a coroutine
    // Note, this whole function is somewhat reminiscent of the Timer class
    // from Fling and Peck.  We use an independent thread to manage one small
    // piece of our GUI.  This coroutine should not modify any data accessed
    // by the main thread (it can read property values)
    private suspend fun displayTime(misc: Long) {
        // This only runs while the display is active
        while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
            val currentPosition = viewModel.player.currentPosition
            val maxTime = viewModel.player.duration
            // Update the seek bar (if the user isn't updating it)
            // and update the passed and remaining time
            //XXX Write me

            // TODO: passed and remaining time
            var time = convertTime(maxTime - currentPosition)
            binding.playerSeekBar.max   = maxTime
            binding.playerTimeRemainingText.text  =  time

            // TODO: Update the seekbar of the current position and text view
            var cuurtime = convertTime(currentPosition)
            binding.playerTimePassedText.text  = cuurtime

            if(!userModifyingSeekBar.get())
                binding.playerSeekBar.setProgress(currentPosition,true)

            // Leave this code as is.  it inserts a delay so that this thread does
            // not consume too much CPU
            delay(misc)
        }
    }
    private fun adapterOnClick(songIndex: Int) {
        Log.d("Debug", songIndex.toString())

        if(binding.playerPlayPauseButton.tag == R.drawable.ic_play_arrow_black_24dp){
            viewModel.playCurrentSong(songIndex)
            songClick = true
        }
        else if(binding.playerPlayPauseButton.tag == R.drawable.ic_pause_black_24dp){
            setBackgroundDrawable(binding.playerPlayPauseButton,R.drawable.ic_pause_black_24dp)
            viewModel.playCurrentSong(songIndex)
            viewModel.player.start()
            viewModel.songsPlayed++
            viewModel.isPlaying = true
        }

    }

    // This method converts time in milliseconds to minutes-second formatted string
    // with two digit minutes and two digit sections, e.g., 01:30
    private fun convertTime(milliseconds: Int): String {
        //XXX Write me
        var MinSecFormat = SimpleDateFormat("mm:ss")
        return MinSecFormat.format(milliseconds).toString()
    }

    // XXX Write me, handle player dynamics and currently playing/next song
    fun updateNextSongNowPlaying(time: Long){
            viewLifecycleOwner.lifecycleScope.launch {
                while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
                    binding.playerCurrentSongText.text = viewModel.getCurrentSongName()
                    binding.playerNextSongText.text = viewModel.getNextSongName()
                    delay(time)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}