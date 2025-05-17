package com.example.tsj_app.ui.services

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tsj_app.AddMeterActivity
import com.example.tsj_app.CheckMeterReadingActivity
import com.example.tsj_app.CheckRequestActivity
import com.example.tsj_app.CreateNewRequestActivity
import com.example.tsj_app.ProfileActivity
import com.example.tsj_app.databinding.FragmentServicesBinding

class ServicesFragment : Fragment() {

    private var _binding: FragmentServicesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val servicesViewModel =
            ViewModelProvider(this).get(ServicesViewModel::class.java)

        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as? ProfileActivity)?.act_bar()

        val createRequestButton = binding.createRequestButton
        val checkRequestButton = binding.checkRequestButton
        val addMeterReadingButton = binding.addMeterReadingButton
        val checkMeterReadingButton = binding.checkMeterReadingButton

        createRequestButton.setOnClickListener{
            val intent = Intent(requireContext(), CreateNewRequestActivity::class.java)
            startActivity(intent)
        }

        checkRequestButton.setOnClickListener {
            val intent = Intent(requireContext(), CheckRequestActivity::class.java)
            startActivity(intent)
        }

        addMeterReadingButton.setOnClickListener {
            val intent = Intent(requireContext(), AddMeterActivity::class.java)
            startActivity(intent)
        }

        checkMeterReadingButton.setOnClickListener {
            val intent = Intent(requireContext(), CheckMeterReadingActivity::class.java)
            startActivity(intent)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}