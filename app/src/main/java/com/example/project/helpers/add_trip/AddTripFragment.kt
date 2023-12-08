package com.example.project.helpers.add_trip

import android.app.DatePickerDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project.R
import com.example.project.databinding.FragmentAddTripBinding
import com.example.project.databinding.FragmentLocationBinding
import com.example.project.helpers.search.SearchBottomSheetFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AddTripFragment  : BottomSheetDialogFragment() {
    private var _binding: FragmentAddTripBinding? = null
    private val binding get() = _binding!!

    private var startDateCalendar = Calendar.getInstance()
    private var endDateCalendar = Calendar.getInstance()

    companion object {
        fun newInstance() = AddTripFragment()
    }

    private lateinit var viewModel: AddTripViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTripBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddTripViewModel::class.java)

        binding.closeButton.setOnClickListener { dismiss() }

    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet =
            dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup?
        val bottomSheetBehavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = 0
        }

        // Optionally, set the height to full screen
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        val startDateEditText = binding.startDateEditText
        val endDateEditText = binding.endDateEditText

        startDateEditText.setOnClickListener {
            showDateRangePicker()
            }

        // End Date Picker
        endDateEditText.setOnClickListener {
            showDateRangePicker()
            }

        binding.startPlanningButton.setOnClickListener {
            val destination = binding.whereToEditText.text.toString()
            val startDate = binding.startDateEditText.text.toString()
            val endDate = binding.endDateEditText.text.toString()

            if (destination.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                viewModel.setTripDetails(destination, startDate, endDate)
                // TODO: Navigate away or show confirmation
            } else {
                // TODO: Show error, fields are not filled in
            }
        }
    }


    private fun showDateRangePicker() {
        // Prepare the current selected dates as the initial selection for the picker
        val currentStartDateStr = binding.startDateEditText.text.toString()
        val currentEndDateStr = binding.endDateEditText.text.toString()
        val formatter = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

        // Prepare the current selected dates as the initial selection for the picker
        val selection = if (currentStartDateStr.isNotEmpty() && currentEndDateStr.isNotEmpty()) {
            val startMillis = java.time.LocalDate.parse(currentStartDateStr, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val endMillis = java.time.LocalDate.parse(currentEndDateStr, formatter).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            if (startMillis != null && endMillis != null) {
                androidx.core.util.Pair(startMillis, endMillis) // Correct Pair type from AndroidX
            } else {
                null
            }
        } else {
            null
        }

        // Create the date range picker builder
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Select dates")
            .apply {
                // Set the initial selection if dates are already selected
                selection?.let { setSelection(it) }
            }
            .build()

        // Add the event listener for when the dates are selected
        dateRangePicker.addOnPositiveButtonClickListener { datePair ->
            val startDate = datePair.first
            val endDate = datePair.second

            if (startDate != null && endDate != null) {
                // Format and display the selected dates
                val formattedStartDate = Instant.ofEpochMilli(startDate).atZone(ZoneOffset.UTC).toLocalDate().format(formatter)
                val formattedEndDate = Instant.ofEpochMilli(endDate).atZone(ZoneOffset.UTC).toLocalDate().format(formatter)

                // Set the formatted dates to your text views or edit texts
                binding.startDateEditText.setText(formattedStartDate)
                binding.endDateEditText.setText(formattedEndDate)
            }
        }

        // Show the date range picker
        dateRangePicker.show(parentFragmentManager, dateRangePicker.toString())
    }

}