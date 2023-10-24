package com.koniukhov.waterreminder.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.koniukhov.waterreminder.data.user.UserDataStore
import com.koniukhov.waterreminder.data.user.dataStore
import com.koniukhov.waterreminder.databinding.RemindIntervalDialogFragmentBinding
import com.koniukhov.waterreminder.viewmodels.MainViewModel

class ReminderIntervalDialogFragment : DialogFragment() {
    companion object {
        const val TAG = "ReminderIntervalDialog"
    }

    private var _binding: RemindIntervalDialogFragmentBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: MainViewModel by activityViewModels{
        MainViewModel.MainViewModelFactory(UserDataStore(requireContext().dataStore), requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = RemindIntervalDialogFragmentBinding.inflate(inflater, container, false)
        this.dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.slider.value = sharedViewModel.userPreferences.reminderInterval.toFloat()

        binding.cancelBtn.setOnClickListener{
            dismiss()
        }

        binding.saveBtn.setOnClickListener{
            sharedViewModel.changeReminderInterval(binding.slider.value.toInt())
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}