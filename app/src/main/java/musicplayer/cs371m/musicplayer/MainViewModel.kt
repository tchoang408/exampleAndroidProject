package musicplayer.cs371m.musicplayer

//import android.R
import android.app.Application
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel


class MainViewModel(application: Application) : AndroidViewModel(application) {
    // A repository can be a local database or the network
    //  or a combination of both
    private val repository = Repository()
    private var songResources = repository.fetchData()

    // Public properties, mostly accessed by PlayerFragment, but useful elsewhere
    var context = application.applicationContext
    // This variable controls what song is playing
    var currentIndex = 0
    // It is convenient to have the player never be null, so proactively
    // create it
    var player: MediaPlayer = MediaPlayer.create(
        application.applicationContext,
        getCurrentSongResourceId()
    )
    // Should I loop the current song?
    var loop = false
    // How many songs have played?
    var songsPlayed = 0
    // Is the player playing?
    var isPlaying = false

    var prevIndex = 0
    // Creating a mutable list also creates a copy
    fun getCopyOfSongInfo(): MutableList<SongInfo> {
        return songResources.toMutableList()
    }

    fun shuffleAndReturnCopyOfSongInfo(): MutableList<SongInfo> {
        // XXX Write me
        //TODO: this is wrong. We need to shuffle the songs

        var songList = songResources
        var newList = songList.shuffled()

        // After find the unque id of current in new list
        val id = songResources[currentIndex].uniqueId
        for(i in 0..newList.size){
            if(id == newList[i].uniqueId){
                // update the current index to the new list
                currentIndex = i
                break
            }
        }
        songResources = newList

        return getCopyOfSongInfo()
    }

    fun getCurrentSongName() : String {
        // XXX Write me
        return songResources.get(currentIndex).name
    }
    // Private function
    private fun nextIndex() : Int {
        // XXX Write me
        //TODO: This might not be correct
        if(currentIndex == 13){
            return 0
        }
        else{
            return currentIndex + 1
        }
    }
    fun nextSong() {
        // XXX Write me
        // TODO: This might not be correct
        prevIndex =currentIndex
        if(currentIndex == 13){
            currentIndex = 0
        }
        else{
            currentIndex += 1
        }
        player.reset()
        player.release()

        player = MediaPlayer.create(
            context.applicationContext,
            getCurrentSongResourceId()
        )
        // Set on click listen again since we create a new player
        player.setOnCompletionListener(){
            nextSong()
        }
        songsPlayed++
        player.start()


    }
    fun getNextSongName() : String {
        // XXX Write me
        return songResources[nextIndex()].name
    }

    fun prevSong() {
        // XXX Write me
        prevIndex =currentIndex
        if(currentIndex == 0){
            currentIndex = 13
        }
        else {
            currentIndex -= 1
        }

        player.reset()
        player.release()

        player = MediaPlayer.create(
            context.applicationContext,
            getCurrentSongResourceId()
        )
        // Set on click listen again since we create a new player
        player.setOnCompletionListener(){
            nextSong()
        }
        songsPlayed++
        player.start()
    }

    fun getCurrentSongResourceId(): Int {
        // XXX Write me
        // TODO: This might not be correct
        return songResources[currentIndex].rawId

    }

    fun playCurrentSong(_songIndex: Int){
        prevIndex = currentIndex
        currentIndex = _songIndex
        player.reset()
        player.release()

        player = MediaPlayer.create(
            context.applicationContext,
            getCurrentSongResourceId()
        )
        // Set on click listen again since we create a new player
        player.setOnCompletionListener(){
            nextSong()
        }
    }
}