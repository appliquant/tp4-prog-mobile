package com.example.tp3.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp3.MainActivity
import com.example.tp3.R
import com.example.tp3.adapter.MessageAdapter
import com.example.tp3.databinding.FragmentListBinding
import com.example.tp3.db.MessageApplication
import com.example.tp3.viewmodel.MessageViewModel
import com.example.tp3.viewmodel.MessageViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ListFragment : Fragment() {
    // ============================================================================
    // Propriétés
    // ============================================================================
    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val messageViewModel: MessageViewModel by activityViewModels {
        MessageViewModelFactory(
            (activity?.application as MessageApplication).database.messageDao()
        )
    }

//    /**
//     * Base de donnée Firestore
//     */
//    private var fireStoreDb = Firebase.firestore

    // ============================================================================
    // Fonctions de cycle de vie
    // ============================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentListBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup recycler view
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MessageAdapter()
        recyclerView.adapter = adapter

        // Remplir le recycler view
        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.getAllMessages().collect {
                adapter.submitList(it)
            }
        }

        Log.d("ListFragment", "onViewCreated: ${messageViewModel.currentUser.value}")
        // Click listener top app bar
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                // Menu configuration
                R.id.menu_top_app_bar_list_config -> {
                    navigateToConfigFragment()
                    true
                }

                // Menu supression
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

    // ============================================================================
    // Fonctions
    // ============================================================================

    /**
     * Navigation vers le fragment de configuration
     */
    private fun navigateToConfigFragment() {
        binding.root.findNavController().navigate(R.id.configurationFragment)
    }

    /**
     * Supprimer tous les messages de la base de donnée
     */
    private fun deleteMessages() {
        viewLifecycleOwner.lifecycleScope.launch {
            messageViewModel.deleteAllMessages()
        }

        // Message de confirmation
        activity?.let {
            Snackbar.make(
                it.findViewById(android.R.id.content),
                getString(R.string.fragment_list_delete), Snackbar.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Ouvre le drawer
     * */
    private fun openDrawer() {
        (activity as MainActivity?)?.openDrawer()
    }
}