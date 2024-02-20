package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.adapters.DrinkWareAdapter
import com.koniukhov.waterreminder.data.drinkware.DrinkWareIcons
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.CustomWaterVolumeDialogFragmentBinding
import com.koniukhov.waterreminder.databinding.DrinkWareDialogFragmentBinding
import com.koniukhov.waterreminder.databinding.HomeFragmentBinding
import com.koniukhov.waterreminder.utilities.Constants.HOUR_MAX_VALUE
import com.koniukhov.waterreminder.utilities.Constants.HOUR_MIN_VALUE
import com.koniukhov.waterreminder.utilities.Constants.MINUTE_MAX_VALUE
import com.koniukhov.waterreminder.utilities.getPercentageOfWaterDrunk
import com.koniukhov.waterreminder.utilities.getStringAmountOfRemainingWater
import com.koniukhov.waterreminder.utilities.getStringPercentageOfWaterDrunk
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
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
        updateCircleProgress()
        updateDrinkWareIcon()
    }

    private fun updateDrinkWareIcon() {
        lifecycleScope.launch {
            sharedViewModel.allDrinkWare.collect {
                val drinkWare =
                    it.stream().filter { v -> v.isActive == 1 }.collect(Collectors.toList())
                if (drinkWare.isNotEmpty()) {
                    binding.drinkWareBtn.setImageResource(DrinkWareIcons.icons[drinkWare.first().iconName]!!)
                }
            }
        }
    }

    private fun updateCircleProgress() {
        sharedViewModel.waterAmount.observe(viewLifecycleOwner) {
            val circleProgress = binding.circleProgress

            lifecycleScope.launch(Dispatchers.Main) {
                val waterLimit = sharedViewModel.userFlow.first().waterLimitPerDay
                circleProgress.setCenterText(
                    getStringPercentageOfWaterDrunk(
                        it,
                        waterLimit,
                        requireContext()
                    )
                )
                circleProgress.setBottomText(
                    getStringAmountOfRemainingWater(
                        it,
                        waterLimit,
                        requireContext()
                    )
                )
                circleProgress.setProgress(getPercentageOfWaterDrunk(it, waterLimit))
            }
        }
    }

    fun showCustomWaterVolumeDialog(){
        val binding = CustomWaterVolumeDialogFragmentBinding.inflate(layoutInflater)
        val time: LocalTime = LocalTime.now()

        binding.hourOfDrinking.minValue = HOUR_MIN_VALUE
        binding.hourOfDrinking.maxValue = HOUR_MAX_VALUE
        binding.hourOfDrinking.value = time.hour

        binding.minuteOfDrinking.minValue = HOUR_MIN_VALUE
        binding.minuteOfDrinking.maxValue = MINUTE_MAX_VALUE
        binding.minuteOfDrinking.value = time.minute

        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.add_custom_water_amount))
            .setPositiveButton(getString(R.string.dialog_save_btn)){ dialog, _ ->
                val time = LocalTime.of(binding.hourOfDrinking.value, binding.minuteOfDrinking.value)
                val waterAmountText = binding.waterAmount.text.toString()
                val waterAmount = when{
                    waterAmountText.isEmpty() -> 0
                    else -> waterAmountText.toInt()
                }
                val date = LocalDate.now()
                val iconName = resources.getResourceEntryName(R.drawable.ic_drink_ware_custom)

                if (waterAmount > 0){
                    sharedViewModel.addWater(time, date, waterAmount, iconName)
                }else{
                    Toast.makeText(requireContext(), R.string.toast_enter_amount_of_water, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    fun showDrinkWareDialog(){
        val binding = DrinkWareDialogFragmentBinding.inflate(layoutInflater)
        val adapter = DrinkWareAdapter(requireContext()){
            sharedViewModel.updateDrinkWare(it)
        }
        binding.drinkWareRecycler.adapter = adapter
        lifecycleScope.launch(Dispatchers.IO) {
            sharedViewModel.allDrinkWare.collect {
                adapter.submitList(it)
            }
        }
        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.switch_cup))
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
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