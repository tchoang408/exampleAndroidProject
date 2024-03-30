package musicplayer.cs371m.musicplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import musicplayer.cs371m.musicplayer.databinding.SettingsFragmentBinding

class SettingsFragment : Fragment() {
    // https://developer.android.com/topic/libraries/view-binding#fragments
    private var _binding: SettingsFragmentBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    private fun initMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Clear menu because we don't want settings icon
                menu.clear()
            }
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Do nothing
                return false
            }
        }, viewLifecycleOwner)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        // XXX Write me findNavController().popBackStack() exits
       // val navController = findNavController()
       // navController.popBackStack()
        /*
        binding.LoopSwitch.setOnClickListener(){
            viewModel.loop = binding.LoopSwitch.isChecked
        }
        */

        binding.LoopSwitch.isChecked = viewModel.loop
        binding.settingBut.setOnClickListener {
            viewModel.loop = binding.LoopSwitch.isChecked
            findNavController().popBackStack()
        }
        binding.settingCancel.setOnClickListener {
            findNavController().popBackStack()
        }

        val millisec = 600L
        viewLifecycleOwner.lifecycleScope.launch {
            updateSongCounts(millisec)
        }

    }
    private suspend fun updateSongCounts(misc: Long) {
        // This only runs while the display is active
        while (viewLifecycleOwner.lifecycleScope.coroutineContext.isActive) {
            binding.tvSongCounts.text = viewModel.songsPlayed.toString()
            delay(misc)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
