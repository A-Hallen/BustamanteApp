package com.example.bustamante

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
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bustamante.databinding.ActivityMainBinding
import com.example.bustamante.service.AndroidScheduler
import com.example.bustamante.ui.productos.ProductosViewModel
import com.example.bustamante.ui.tcps.ProveedorViewModel
import com.example.bustamante.utils.Permissions
import dagger.hilt.android.AndroidEntryPoint


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
        askPermissions()
        setupService()
    }

    private fun askPermissions() {
        Permissions(this).askStoragePermissions()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "Sin el permiso de escritura no puedo guardar tus datos, reinicia la aplicacion y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            // Permiso concedido, puedes continuar con tu lógica
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
        val builder = JobInfo.Builder(123, componentName)
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
            .setPersisted(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            builder.setPeriodic(30 * 60 * 1000, 45 * 60 * 1000)
        } else builder.setPeriodic(30 * 60 * 1000)

        val info = builder.build()
        jobSheduler.schedule(info)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}