package com.example.project

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.example.project.databinding.ActivityMainBinding
import com.example.project.auth.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private val auth = Firebase.auth
    private lateinit var navController:NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSignedIn()

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            showContextMenu(view)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)



        // logout when clicking logout button
        navView.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            signOut()
            true
        }

        // reset navController graph when navigating
        navController.addOnDestinationChangedListener { _, _, _ ->
            checkUserSignedIn()
            navController.graph = navController.navInflater.inflate(R.navigation.mobile_navigation)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            navigateToDestination(menuItem.itemId)
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    public fun signOut() {
        auth.signOut()
        startActivity(Intent(this, LoginActivity::class.java))
    }

    public fun checkUserSignedIn() {
        val currentUser = auth.currentUser
        Log.v("MainActivity", "Current user: $currentUser")
        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    public fun navigateToDestination(destinationId: Int) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .build()
            navController.navigate(destinationId, null, navOptions)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun showContextMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.fab_context_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.addTripPlan -> {
                    navigateToDestination(R.id.nav_slideshow)
                    true
                }
                R.id.addTravelogue -> {
                    navigateToDestination(R.id.nav_gallery)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }
}