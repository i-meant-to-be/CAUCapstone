package com.caucapstone.app.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.chaquo.python.PyException
import com.chaquo.python.Python
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _py = Python.getInstance()
    private val _output = mutableStateOf("(empty)")
    private val _navControllerState = mutableStateOf(0)
    val output: State<String> = _output
    val navControllerState = _navControllerState

    fun init() {
        val sys = this._py.getModule("sys")
        val io = this._py.getModule("io")
        val console = this._py.getModule("test")
        val textOutputStream = io.callAttr("StringIO")
        sys.put("stdout", textOutputStream)

        try {
            console.callAttrThrows("function")
            _output.value = textOutputStream.callAttr("getvalue").toString()
        } catch (e: PyException) {
            _output.value = e.message.toString()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }

    fun setNavControllerState(state: Int) {
        _navControllerState.value = state
    }
}