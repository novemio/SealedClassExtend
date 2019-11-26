package com.novemio.android.sealedclasssextend.example.main

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.lifecycle.MutableLiveData
import com.novemio.android.sealedclasssextend.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : Activity() {

    private var state = MutableLiveData<MainState>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        observe()
        init()
    }

    private fun observe() {


        state.observeForever {

            val returnNestop:String = it.run {
                isError { "Bravo" } ?:isSuccess { "Cao" }!!
            }
            println(returnNestop)

            it.isSuccess {
                tvTitle.text="MIlan"
            }
        }
    }

    private fun init() {

        state.postValue(MainState.Success)
        Timer().schedule(5000) {
            state.postValue(MainState.Error("Error"))
        }
    }


}
