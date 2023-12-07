package com.example.project.helpers

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.project.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance() = SearchBottomSheetFragment()
    }

    private lateinit var viewModel: SearchBottomSheetViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.closeButton).setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog as BottomSheetDialog?
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as ViewGroup?
        val bottomSheetBehavior = bottomSheet?.let { BottomSheetBehavior.from(it) }
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            bottomSheetBehavior.peekHeight = 0 // Optional: Remove the 'peek' height
        }


        // Optionally, set the height to full screen
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

    }

}