package com.hallen.bustamante

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hallen.bustamante.databinding.ActivityMainBinding
import com.hallen.bustamante.databinding.SerachViewBinding
import com.hallen.bustamante.service.AndroidScheduler
import com.hallen.bustamante.ui.productos.ProductosViewModel
import com.hallen.bustamante.ui.tcps.ProveedorViewModel
import com.hallen.bustamante.utils.Permissions
import com.hallen.bustamante.utils.SearchUseCase
import dagger.hilt.android.AndroidEntryPoint

private const val JOB_ID = 123
private const val MIN_JOB_INTERVAL: Long = 30 * 60 * 1000
private const val MAX_JOB_INTERVAL: Long = 45 * 60 * 1000

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val productosViewModel: ProductosViewModel by viewModels()
    private val proveedorViewModel: ProveedorViewModel by viewModels()
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val updateFilter = IntentFilter("jobservice.to.activity.update")

    private val updateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, int: Intent?) {
            when (int?.getStringExtra("clave")) {
                "productos" -> productosViewModel.getProductos()
                "proveedores" -> proveedorViewModel.getProveedores()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(updateReceiver, updateFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(updateReceiver)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        configNavBar()
        Permissions(this).askStoragePermissions()
        setupService()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                val message = getString(R.string.permission_canceled)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun configNavBar() {
        setSupportActionBar(binding.appBarMain.toolbar)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val destinations = setOf(R.id.nav_productos, R.id.nav_mypimes, R.id.nav_tcps)
        appBarConfiguration = AppBarConfiguration(destinations, binding.drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun setupService() {
        val jobSheduler: JobScheduler =
            getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val componentName = ComponentName(this, AndroidScheduler::class.java)
        val builder = JobInfo.Builder(JOB_ID, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(MIN_JOB_INTERVAL, MAX_JOB_INTERVAL)
        } else builder.setPeriodic(MAX_JOB_INTERVAL)

        val info = builder.build()
        jobSheduler.schedule(info)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)

        val menuItem = menu.findItem(R.id.action_search)
        val inflater = LayoutInflater.from(this)
        val searchViewBinding = SerachViewBinding.inflate(inflater, null, false)
        menuItem.actionView = searchViewBinding.root

        val searchView = searchViewBinding.serachView

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        val searchUseCase = SearchUseCase(navController, productosViewModel, proveedorViewModel)
        searchUseCase.searchViewClick(searchView)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
