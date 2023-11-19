package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.HomeFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import androidx.lifecycle.lifecycleScope
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.drinkware.DrinkWareIcons
import com.koniukhov.waterreminder.dialogs.CustomWaterVolumeDialogFragment
import com.koniukhov.waterreminder.dialogs.DrinkWareDialogFragment
import com.koniukhov.waterreminder.utilities.getPercentageOfWaterDrunk
import com.koniukhov.waterreminder.utilities.getStringAmountOfRemainingWater
import com.koniukhov.waterreminder.utilities.getStringPercentageOfWaterDrunk
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.launch
import java.util.stream.Collectors

class HomeFragment : Fragment() {

    private var _binding: HomeFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navigateToStarterFragment()
        sharedViewModel.waterAmount.observe(viewLifecycleOwner) {
            val circleProgress = binding.circleProgress

            lifecycleScope.launch(Dispatchers.Main) {
                val waterLimit = sharedViewModel.userFlow.first().waterLimitPerDay
                circleProgress.setCenterText(getStringPercentageOfWaterDrunk(it, waterLimit, requireContext()))
                circleProgress.setBottomText(getStringAmountOfRemainingWater(it, waterLimit, requireContext()))
                circleProgress.setProgress(getPercentageOfWaterDrunk(it, waterLimit))
            }
        }
        lifecycleScope.launch {
            sharedViewModel.allDrinkWare.collect{

                val drinkWare = it.stream().filter{v -> v.isActive == 1}.collect(Collectors.toList())
                if (drinkWare.isNotEmpty()){
                    binding.drinkWareBtn.setImageResource(DrinkWareIcons.icons[drinkWare.first().iconName]!!)
                }
            }
        }
    }

    fun showCustomWaterVolumeDialog(){
        CustomWaterVolumeDialogFragment().show(childFragmentManager, CustomWaterVolumeDialogFragment.TAG)
    }

    fun showDrinkWareDialog(){
        DrinkWareDialogFragment().show(childFragmentManager, DrinkWareDialogFragment.TAG)
    }

    fun saveWater(){
        sharedViewModel.addWaterByCurrentDrinkWare()
    }

    private fun navigateToStarterFragment(){

        val userPreferences = UserDataStore(requireContext().dataStore)

        lifecycleScope.launch(Dispatchers.Main) {
           if (userPreferences.userPreferencesFlow.first().isFirstOpening){
               findNavController().navigate(R.id.action_homeFragment_to_starterFragment)
           }else{
               binding.root.visibility = View.VISIBLE
           }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}