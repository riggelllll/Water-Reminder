package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.koniukhov.waterreminder.R
import com.koniukhov.waterreminder.data.user.Gender
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.RemindIntervalDialogFragmentBinding
import com.koniukhov.waterreminder.databinding.SettingsFragmentBinding
import com.koniukhov.waterreminder.databinding.WaterLimitDialogFragmentBinding
import com.koniukhov.waterreminder.databinding.WeightDialogFragmentBinding
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_BED_HOUR
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_MINUTE
import com.koniukhov.waterreminder.utilities.Constants.DEFAULT_WAKE_UP_HOUR
import com.koniukhov.waterreminder.utilities.Constants.REMINDER_INTERVAL_FROM
import com.koniukhov.waterreminder.utilities.Constants.REMINDER_INTERVAL_STEP
import com.koniukhov.waterreminder.utilities.Constants.REMINDER_INTERVAL_TO
import com.koniukhov.waterreminder.utilities.Constants.WATER_LIMIT_FROM
import com.koniukhov.waterreminder.utilities.Constants.WATER_LIMIT_STEP
import com.koniukhov.waterreminder.utilities.Constants.WATER_LIMIT_TO
import com.koniukhov.waterreminder.utilities.Constants.WEIGHT_MAX
import com.koniukhov.waterreminder.utilities.Constants.WEIGHT_MIN
import com.koniukhov.waterreminder.utilities.Constants.WEIGHT_STEP
import com.koniukhov.waterreminder.utilities.WaterHelper
import com.koniukhov.waterreminder.viewmodels.MainViewModel
import kotlinx.coroutines.launch

private const val WAKE_UP_TIME_TAG = "WakeUpTimeDialog"
private const val BED_TIME_TAG = "BedTimeDialog"
class SettingsFragment : Fragment() {

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    private var wakeUpHour: Int = DEFAULT_WAKE_UP_HOUR
    private var wakeUpMinute: Int = DEFAULT_MINUTE
    private var bedTimeHour: Int = DEFAULT_BED_HOUR
    private var bedTimeMinute: Int = DEFAULT_MINUTE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = sharedViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateSettings()
    }

    private fun updateSettings() {
        lifecycleScope.launch {
            sharedViewModel.userFlow.collect {
                binding.isReminderSwitch.isChecked =
                    it.isRemind
                binding.waterLimit.text =
                    getString(R.string.water_limit_value, it.waterLimitPerDay.toString())
                binding.remindInterval.text =
                    getString(R.string.remind_interval_value, it.reminderInterval.toString())
                binding.genderText.text =
                    if (it.gender == Gender.MALE) getString(R.string.male_text) else getString(R.string.female_text)
                binding.weightText.text = getString(R.string.weight_value, it.weight.toString())
                binding.wakeUpText.text = it.wakeUpTime.toString()
                binding.bedTimeText.text = it.bedTime.toString()
                wakeUpHour = it.wakeUpTime.hour
                wakeUpMinute = it.wakeUpTime.minute
                bedTimeHour = it.bedTime.hour
                bedTimeMinute = it.bedTime.minute
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showRemindIntervalDialog(){
        val binding = RemindIntervalDialogFragmentBinding.inflate(layoutInflater)
        binding.slider.valueFrom = REMINDER_INTERVAL_FROM
        binding.slider.valueTo = REMINDER_INTERVAL_TO
        binding.slider.stepSize = REMINDER_INTERVAL_STEP
        binding.slider.value = sharedViewModel.userPreferences.reminderInterval.toFloat()

        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.change_reminder_interval_title))
            .setPositiveButton(getString(R.string.dialog_save_btn)){ dialog, _ ->
                sharedViewModel.changeReminderInterval(binding.slider.value.toInt())
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

    fun showWaterLimitDialog(){
        val binding = WaterLimitDialogFragmentBinding.inflate(layoutInflater)
        binding.slider.valueFrom = WATER_LIMIT_FROM
        binding.slider.valueTo = WATER_LIMIT_TO
        binding.slider.stepSize = WATER_LIMIT_STEP
        binding.slider.value = sharedViewModel.userPreferences.waterLimitPerDay.toFloat()

        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.change_water_limit_title))
            .setPositiveButton(getString(R.string.dialog_save_btn)){ dialog, _ ->
                sharedViewModel.changeWaterLimit(binding.slider.value.toInt())
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showChangeGenderDialog(){
        val genders = arrayOf(getString(R.string.male_text), getString(R.string.female_text))
        var selectedIndex = if (sharedViewModel.userPreferences.gender == Gender.MALE) {
            Gender.MALE.gender
        } else {
            Gender.FEMALE.gender
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.change_gender_title))
            .setSingleChoiceItems(genders, selectedIndex) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton(getString(R.string.dialog_save_btn)){ dialog, _ ->
                if (selectedIndex == Gender.MALE.gender){
                    sharedViewModel.changeGender(Gender.MALE)
                    sharedViewModel.changeWaterLimit(
                        WaterHelper.calculateWaterAmount(
                            Gender.MALE,
                            sharedViewModel.userPreferences.weight
                        )
                    )
                }else{
                    sharedViewModel.changeGender(Gender.FEMALE)
                    sharedViewModel.changeWaterLimit(
                        WaterHelper.calculateWaterAmount(
                            Gender.FEMALE,
                            sharedViewModel.userPreferences.weight
                        )
                    )
                }
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showWeightDialog(){
        val binding = WeightDialogFragmentBinding.inflate(layoutInflater)
        binding.slider.valueFrom = WEIGHT_MIN.toFloat()
        binding.slider.valueTo = WEIGHT_MAX.toFloat()
        binding.slider.stepSize = WEIGHT_STEP.toFloat()
        binding.slider.value = sharedViewModel.userPreferences.weight.toFloat()

        MaterialAlertDialogBuilder(requireContext())
            .setView(binding.root)
            .setTitle(getString(R.string.change_weight_title))
            .setPositiveButton(getString(R.string.dialog_save_btn)){ dialog, _ ->
                val weight = binding.slider.value.toInt()
                val waterLimit =
                    WaterHelper.calculateWaterAmount(sharedViewModel.userPreferences.gender, weight)
                sharedViewModel.changeWeight(weight)
                sharedViewModel.changeWaterLimit(waterLimit)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.dialog_cancel_btn)){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    fun showWakeUpTimeDialog(){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(wakeUpHour)
                .setMinute(wakeUpMinute)
                .setTitleText(getString(R.string.change_wake_up_time_title))
                .build()
        picker.addOnPositiveButtonClickListener {
            sharedViewModel.changeWakeUpTime(picker.hour, picker.minute)
        }

        picker.show(childFragmentManager, WAKE_UP_TIME_TAG)
    }

    fun showBedTimeDialog(){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(bedTimeHour)
                .setMinute(bedTimeMinute)
                .setTitleText(getString(R.string.change_bed_time_title))
                .build()
        picker.addOnPositiveButtonClickListener {
            sharedViewModel.changeBedTime(picker.hour, picker.minute)
        }

        picker.show(childFragmentManager, BED_TIME_TAG)
    }
}