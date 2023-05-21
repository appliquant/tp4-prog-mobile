package com.example.tp3.ui

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.tp3.R
import com.example.tp3.databinding.FragmentAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthFragment : Fragment() {
    // ============================================================================
    // Propriétés
    // ============================================================================
    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    // ============================================================================
    // Fonctions de cycle de vie
    // ============================================================================
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentAuthBinding.inflate(inflater, container, false)
        _binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup binding
        binding.lifecycleOwner = viewLifecycleOwner

        // Setup authentification
        auth = Firebase.auth

        // Vérifier si l'utilisateur est authentifié
        val currentUser = auth.currentUser
        if (currentUser != null) {
            navigateToMessages()
        }

        // Clique sur le bouton de connexion
        binding.btnLogin.setOnClickListener {
            validations()
        }


    }


    // ============================================================================
    // Fonctions
    // ============================================================================
    /**
     * Valider les champs du formulaire
     */
    private fun validations() {

        // Valider email
        val email = binding.txtInpEmail.text.toString()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.txtInpEmail.error = getString(R.string.email_required)
            return
        }

        // Valider mot de passe
        val password = binding.txtInpPassword.text.toString()
        if (password.isEmpty() || password.length < 6) {
            binding.txtInpPassword.error = getString(R.string.password_required)
            return
        }

        // Authentifier utilisateur
        authentification(email, password)

    }

    /**
     * Authentifier utilisateur
     */
    private fun authentification(email: String, password: String) {
        // Authentifier l'utilisateur
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    if (user != null) {
                        navigateToMessages()
                    }

                } else {
                    // Message d'erreur
                    Toast.makeText(
                        context,
                        getString(R.string.fragment_auth_failed),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }

    /**
     * Naviguer vers la liste des messages
     */
    private fun navigateToMessages() {
        binding.root.findNavController().navigate(R.id.listFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}