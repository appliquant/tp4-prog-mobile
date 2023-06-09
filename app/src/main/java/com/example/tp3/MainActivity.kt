package com.example.tp3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.tp3.databinding.ActivityMainBinding
import com.example.tp3.notifications.SmsNotificationService

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Permissions
        requestPermissions(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.RECEIVE_SMS,
                android.Manifest.permission.POST_NOTIFICATIONS
            ),
            1200112
        )

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

        // Channel notifications
        val desc = getString(R.string.notification_channel_description)

        val channel = NotificationChannel(
            SmsNotificationService.SMS_CHANNEL_ID,
            desc,
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = desc

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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