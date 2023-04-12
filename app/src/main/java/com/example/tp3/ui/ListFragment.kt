package com.example.tp3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tp3.MainActivity
import com.example.tp3.R
import com.example.tp3.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentBinding = FragmentListBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Click listener top app bar
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_top_app_bar_list_config -> {
                    navigateToConfigFragment()
                    true
                }

                R.id.menu_top_app_bar_list_delete -> {
                    deleteMessages()
                    true
                }
                else -> true
            }

        }

        // Click listener top app bar sur le bouton `menu` pour ouvir le drawer
        binding.topAppBar.setNavigationOnClickListener {
            openDrawer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Navigation vers le fragment de configuration
     */
    private fun navigateToConfigFragment() {
        binding.root.findNavController().navigate(R.id.configurationFragment)
    }

    /**
     * Supprimer les méssages de la base de donnée
     */
    private fun deleteMessages() {

    }

    /**
     * Ouvre le drawer
     * */
    private fun openDrawer() {
        (activity as MainActivity?)?.openDrawer()
    }
}