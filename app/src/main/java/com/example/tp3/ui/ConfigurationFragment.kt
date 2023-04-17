package com.example.tp3.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.tp3.R
import com.example.tp3.databinding.FragmentConfigurationBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ConfigurationFragment : Fragment() {
    // ============================================================================
    // Propriétés
    // ============================================================================
    private var _binding: FragmentConfigurationBinding? = null
    private val binding get() = _binding!!

    // ============================================================================
    // Fonctions cycle de vie
    // ============================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setup binding
        val fragmentBinding = FragmentConfigurationBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Mettre les données dans les champs
        viewLifecycleOwner.lifecycleScope.launch {
            populateFields()
        }

        // Click listener top app bar
        binding.topAppBar.setNavigationOnClickListener {
            navigateBack()
        }

        // Click listener bouton
        binding.btnSave.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                saveUser()
            }
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
     * Mettre les données dans les champs
     */
    private suspend fun populateFields() {
        viewLifecycleOwner.lifecycleScope.launch {

            // Récupérer les données
            val firstname = context?.dataStore?.data?.firstOrNull()
                ?.get(stringPreferencesKey(CONFIGURATION_DATE_STORE_KEY_FIRSTNAME))
                ?: DEFAULT_FIRSTNAME
            val lastname = context?.dataStore?.data?.firstOrNull()
                ?.get(stringPreferencesKey(CONFIGURATION_DATE_STORE_KEY_LASTNAME))
                ?: DEFAULT_LASTNAME

            // Populer les champs
            binding.txtInpFirstname.setText(firstname)
            binding.txtInpLastname.setText(lastname)
        }
    }

    /**
     * Sauvegarder les données de l'utilisateur (nom et prénom)
     */
    private suspend fun saveUser() {
        val firstname = binding.txtInpFirstname.text.toString()
        val lastname = binding.txtInpLastname.text.toString()

        // Valider les données
        if (firstname.isEmpty() || lastname.isEmpty()) {
            activity?.let {
                Snackbar.make(
                    it.findViewById(android.R.id.content),
                    getString(R.string.fragment_configuration_error), Snackbar.LENGTH_LONG
                ).show()
            }
            return
        }


        // Sauvegarder les données
        context?.dataStore?.edit { preferences ->
            preferences[stringPreferencesKey(CONFIGURATION_DATE_STORE_KEY_FIRSTNAME)] = firstname
            preferences[stringPreferencesKey(CONFIGURATION_DATE_STORE_KEY_LASTNAME)] = lastname
        }


        // Retour à la liste
        navigateBack()
    }

    /**
     * Retour à la liste
     */
    private fun navigateBack() {
        findNavController().navigateUp()
    }

    companion object {
        const val CONFIGURATION_DATE_STORE_KEY = "COM.EXAMPLE.TP3@CONFIGURATION_DATE_STORE_KEY"
        const val CONFIGURATION_DATE_STORE_KEY_FIRSTNAME =
            "COM.EXAMPLE.TP3@CONFIGURATION_DATE_STORE_KEY@FIRSTNAME"
        const val CONFIGURATION_DATE_STORE_KEY_LASTNAME =
            "COM.EXAMPLE.TP3.CONFIGURATION_DATE_STORE_KEY@LASTNAME"

        const val DEFAULT_FIRSTNAME = "Garneau"
        const val DEFAULT_LASTNAME = "Cégep"

        // Datastore singleton
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = CONFIGURATION_DATE_STORE_KEY)

    }
}