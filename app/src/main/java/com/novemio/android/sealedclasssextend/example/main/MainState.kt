package com.novemio.android.sealedclasssextend.example.main

import com.novemio.android.lib.sealedclassextend.SealedExtension

/**
 * Created by novemio on 26/11/2019.
 */
@SealedExtension
sealed class MainState {

    object Success : MainState()
    data class Error(val msg: String) : MainState()
}
