package com.hallen.bustamante

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hallen.bustamante.databinding.SplashScreenLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val SPLAY_DELAY_MILLIS = 2000L // 2 seconds

class SplashScreen : AppCompatActivity() {
    private lateinit var binding: SplashScreenLayoutBinding

    private val splashCoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashScreenLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Glide.with(this).load(R.drawable.logo_anim).into(binding.imageView)

        splashCoroutineScope.launch {
            delay(SPLAY_DELAY_MILLIS)
            startActivity(Intent(this@SplashScreen, MainActivity::class.java))
            this@SplashScreen.finish()
        }
    }

    override fun onDestroy() {
        // Cancelamos la corutina en caso de que la actividad sea destruida antes de los 2 segundos
        splashCoroutineScope.cancel()
        super.onDestroy()
    }
}
