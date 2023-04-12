package com.example.tp3.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.tp3.MainActivity
import com.example.tp3.R
import com.example.tp3.databinding.FragmentMapBinding

class MapFragment : Fragment() {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val fragmentBinding = FragmentMapBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Click listener top app bar
        binding.topAppBar.setNavigationOnClickListener() {
            openDrawer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Ouvre le drawer
     * */
    private fun openDrawer() {
        (activity as MainActivity?)?.openDrawer()
    }
}