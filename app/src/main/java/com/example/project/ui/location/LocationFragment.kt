package com.example.project.ui.location

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.project.R
import com.example.project.databinding.FragmentHomeBinding
import com.example.project.databinding.FragmentLocationBinding
import com.example.project.models.dummyLocations

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    companion object {
        fun newInstance() = LocationFragment()
    }

    private lateinit var viewModel: LocationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val locationId = arguments?.getInt("locationId") ?: 1
        viewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        viewModel.setLocationId(locationId)


        binding.locId.text = dummyLocations.find {
            it.id == locationId
        }?.title.toString()

    }

}