package com.hallen.bustamante.service

import android.app.job.JobParameters
import android.app.job.JobService
import com.hallen.bustamante.domain.ProductUseCase
import com.hallen.bustamante.domain.ProveedorUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AndroidScheduler : JobService() {

    @Inject
    lateinit var productUseCase: ProductUseCase

    @Inject
    lateinit var proveedorUseCase: ProveedorUseCase

    private var myAsyncTask: JobCoroutineTask? = null
    private var jobParameters: JobParameters? = null

    override fun onStartJob(p0: JobParameters?): Boolean {
        jobParameters = p0
        startJob()
        return true
    }

    private fun startJob() {
        myAsyncTask = JobCoroutineTask(
            proveedorUseCase, productUseCase, applicationContext
        ) {
            jobFinished(jobParameters, it)
        }
        myAsyncTask?.start()
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        if (myAsyncTask?.isAlive == true) {
            myAsyncTask?.interrupt()
        }
        return true
    }
}
