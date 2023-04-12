package com.example.tp3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tp3.databinding.FragmentConfigurationBinding

class ConfigurationFragment : Fragment() {

    var _binding: FragmentConfigurationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Setup binding
        val fragmentBinding = FragmentConfigurationBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Click listener top app bar
        binding.topAppBar.setNavigationOnClickListener() {
            navigateBack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Retour à la liste
     */
    private fun navigateBack() {
        findNavController().navigateUp()
    }
}