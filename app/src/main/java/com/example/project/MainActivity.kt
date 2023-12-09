package com.example.project

//import android.widget.Toolbar
import MyDatabaseHelper
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.project.auth.LoginActivity
import com.example.project.databinding.ActivityMainBinding
import com.example.project.helpers.add_trip.AddTripFragment
import com.example.project.helpers.search.SearchBottomSheetFragment
import com.example.project.helpers.search.SearchBottomSheetViewModel
import com.example.project.models.Note
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.auth


class MainActivity : AppCompatActivity() {
    private val auth = com.google.firebase.Firebase.auth
    private lateinit var navController:NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawer_layout: DrawerLayout
    private lateinit var fab: FloatingActionButton
    private lateinit var searchBottomSheetModel: SearchBottomSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //drawer_layout = findViewById(R.id.drawer_layout)
        searchBottomSheetModel = ViewModelProvider(this).get(SearchBottomSheetViewModel::class.java)
        searchBottomSheetModel.turnOnNavigate()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkUserSignedIn()

        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_navigation)

        fab = binding.appBarMain.fab
        fab.setOnClickListener { view ->
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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // if destination.id not in an array of ids then show fab
           val idArray = arrayOf(R.id.nav_trip, R.id.nav_profile, R.id.nav_location)

            if(!idArray.contains(destination.id))
                fab.show()
            else fab.hide()



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


        val dbHelper = MyDatabaseHelper(this)
        val newNote = Note(id = 1, description = "Sample Note","")
        val noteId = dbHelper.insertNote(newNote)
        Log.v("MainActivity", "Note id: $noteId")
        val notes = dbHelper.getAllNotes()
        val updatedNote = Note(id = 1, description = "Updated Note", "")
        val rowsAffected = dbHelper.updateNote(updatedNote)
        Log.v("MainActivity", "Updated note affected: $rowsAffected")
        val deletedNoteId = dbHelper.deleteNoteById(1)
        Log.v("MainActivity", "Deleted note id: $deletedNoteId")
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
    fun navigateToDestination(destinationId: Int, locationId: Int) {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            val bundle = Bundle().apply { putInt("locationId", locationId) }
            navController.navigate(destinationId, bundle, navOptions)
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
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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
                R.id.addTravelogue -> {
                    navigateToDestination(R.id.nav_gallery)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        signOut()
    }
}