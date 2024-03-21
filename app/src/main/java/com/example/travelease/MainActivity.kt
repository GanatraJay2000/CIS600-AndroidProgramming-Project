package com.example.travelease

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import com.example.travelease.databinding.ActivityMainBinding
import com.example.travelease.helper.dialogs.addTrip.AddTripFragment
import com.example.travelease.helper.dialogs.search.SearchBottomSheetFragment
import com.example.travelease.helper.dialogs.search.SearchBottomSheetViewModel
import com.example.travelease.ui.auth.LoginActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private val auth = com.google.firebase.Firebase.auth
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkUserSignedIn()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        //drawer_layout = findViewById(R.id.drawer_layout)
        searchBottomSheetModel = ViewModelProvider(this).get(SearchBottomSheetViewModel::class.java)
        searchBottomSheetModel.turnOnNavigate()

        fab = binding.appBarMain.fab
        fab.setOnClickListener { view ->
            showContextMenu(view)
        }

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // logout when clicking logout button
        navView.menu.findItem(R.id.nav_logout).setOnMenuItemClickListener {
            signOut()
            true
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val idArray = arrayOf(R.id.nav_trip, R.id.nav_profile, R.id.nav_location)
            if (!idArray.contains(destination.id)) fab.show()
            else fab.hide()
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.app_bar_search -> {
                val searchBottomSheetFragment = SearchBottomSheetFragment()
                searchBottomSheetFragment.show(supportFragmentManager, searchBottomSheetFragment.tag)
                return true
            }
            R.id.profile -> {
                navigateToDestination(R.id.nav_profile)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun navigateToDestination(destinationId: Int) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            navController.navigate(destinationId, null, navOptions)
        }
    }

    private fun showContextMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.fab_context_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.addTripPlan -> {
                    val addTripFragment = AddTripFragment()
                    addTripFragment.show(supportFragmentManager, addTripFragment.tag)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
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
}