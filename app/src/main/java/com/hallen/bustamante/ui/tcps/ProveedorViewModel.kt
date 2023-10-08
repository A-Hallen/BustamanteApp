package com.hallen.bustamante.ui.tcps

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hallen.bustamante.data.model.Proveedor
import com.hallen.bustamante.domain.ProveedorUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProveedorViewModel @Inject constructor(
    private val proveedorUseCase: ProveedorUseCase
) : ViewModel() {
    val tcpList = MutableLiveData<List<Proveedor>>()
    val mypimeList = MutableLiveData<List<Proveedor>>()

//    fun getProveedoresFromApi() {
//        CoroutineScope(Dispatchers.IO).launch {
//            val proveedores = proveedorUseCase.getAllProveedorFromApi()
//            Logger.i("Proveedores: $proveedores")
//            val (tcps, mypime) = proveedores.partition { it.tipo == "TCP" }
//            tcpList.postValue(tcps)
//            mypimeList.postValue(mypime)
//        }
//    }

    fun getProveedores() {
        CoroutineScope(Dispatchers.IO).launch {
            val proveedores = proveedorUseCase.getAllProveedores()
            val (tcps, mypimes) = proveedores.partition { it.tipo == "TCP" }
            tcpList.postValue(tcps)
            mypimeList.postValue(mypimes)
        }
    }

}