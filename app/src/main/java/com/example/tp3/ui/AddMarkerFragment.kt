package com.example.tp3.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.tp3.R
import com.example.tp3.databinding.FragmentAddMarkerBinding
import com.example.tp3.db.Message
import com.example.tp3.db.MessageApplication
import com.example.tp3.viewmodel.MessageViewModel
import com.example.tp3.viewmodel.MessageViewModel.Companion.GlobalDataStore
import com.example.tp3.viewmodel.MessageViewModelFactory
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.security.SecureRandom

class AddMarkerFragment : Fragment() {

    // ============================================================================
    // Propriétés
    // ============================================================================
    private var _binding: FragmentAddMarkerBinding? = null
    private val binding get() = _binding!!
    private val messageViewModel: MessageViewModel by activityViewModels {
        MessageViewModelFactory(
            (activity?.application as MessageApplication).database.messageDao()
        )
    }

    // ============================================================================
    // Fonctions de cycle de vie
    // ============================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentAddMarkerBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Click listener sur bouton ajout
        binding.btnAddMarker.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                addMessage()
            }
        }

        // Setup top bar
        binding.topAppBar.setNavigationOnClickListener {
            goBack()
        }
    }

    // ============================================================================
    // Fonctions
    // ============================================================================
    /**
     * Ajouter un nouveau message
     */
    private suspend fun addMessage() {
        val message = binding.txtInpName.text.toString()

        // Valider le message
        if (message.isEmpty()) {
            activity?.let {
                Snackbar.make(
                    it.findViewById(android.R.id.content),
                    getString(R.string.fragment_add_marker_error), Snackbar.LENGTH_LONG
                ).show()
            }
            return
        }

        // Récupérer les coordonnées
        val latitude = arguments?.getFloat(ARG_LATITUDE) ?: 0.0f
        val longitude = arguments?.getFloat(ARG_LONGITUDE) ?: 0.0f
        val firstname = context?.GlobalDataStore?.data?.firstOrNull()
            ?.get(stringPreferencesKey(MessageViewModel.CONFIGURATION_DATA_STORE_KEY_FIRSTNAME))
            ?: MessageViewModel.DEFAULT_FIRSTNAME
        val lastname = context?.GlobalDataStore?.data?.firstOrNull()
            ?.get(stringPreferencesKey(MessageViewModel.CONFIGURATION_DATA_STORE_KEY_LASTNAME))
            ?: MessageViewModel.DEFAULT_LASTNAME
        val picture = "https://robohash.org/$firstname/$lastname"
        val random = SecureRandom()
        val id = random.nextLong()

        val messageObj = Message(
            id = id,
            firstname = firstname,
            lastname = lastname,
            message = message,
            picture = picture,
            latitude = latitude.toDouble(),
            longitude = longitude.toDouble()
        )

        // Ajouter le message
        viewLifecycleOwner.lifecycleScope.launch {
              messageViewModel.insertMessageFirestore(messageObj)
        }

        // Message de confirmation
        activity?.let {
            Snackbar.make(
                it.findViewById(android.R.id.content),
                getString(R.string.fragment_add_marker_success), Snackbar.LENGTH_LONG
            ).show()
        }

        // Retourner à la carte
        goBack()
    }

    /**
     * Retourner à la carte
     */
    private fun goBack() {
        view?.findNavController()?.navigate(R.id.action_addMarkerFragment_to_mapFragment)
    }

    companion object {
        // Arguments
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"
    }
}