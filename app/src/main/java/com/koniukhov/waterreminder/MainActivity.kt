package com.koniukhov.waterreminder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.koniukhov.waterreminder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermission()
        setNavController()
        bindBottomBarToNavController()
        hideBottomBarInStarterFragment()
    }

    private fun setNavController(){
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun bindBottomBarToNavController(){
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private fun hideBottomBarInStarterFragment(){
        navController.addOnDestinationChangedListener{ _, destination, _ ->
            if (destination.id == R.id.starterFragment){
                binding.bottomNavigation.visibility = View.GONE
            }else{
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    private fun checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val permission = Manifest.permission.POST_NOTIFICATIONS

            if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED){
                if (!shouldShowRequestPermissionRationale(permission)){
                    val requestNotificationPermission =
                        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                            if (isGranted){
                                Toast.makeText(applicationContext, getString(R.string.permission_granted), Toast.LENGTH_SHORT).show()
                            }else{
                                Toast.makeText(applicationContext, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
                            }
                        }
                    requestNotificationPermission.launch(permission)
                }
            }


        }
    }
}