package com.example.tp3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tp3.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Drawer
        val navigationView = binding.navigationView

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_drawer_map -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.mapFragment)
                    closeDrawer()
                    true
                }

                R.id.menu_drawer_list -> {
                    findNavController(R.id.nav_host_fragment).navigate(R.id.listFragment)
                    closeDrawer()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Ferme le drawer
     */
    private fun closeDrawer() {
        binding.drawerLayout.closeDrawers()
    }

    /**
     * Ouvre le drawer
     * (appelé depuis les fragments)
     */
    fun openDrawer() {
        binding.drawerLayout.openDrawer(binding.navigationView, true)
    }
}