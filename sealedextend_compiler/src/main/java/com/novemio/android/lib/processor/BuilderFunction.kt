package com.novemio.android.lib.processor

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.TypeElement

/**
 * Created by novemio on 22/11/2019.
 */
class BuilderFunction(
    val typeElement: TypeElement,
    val classSimpleName: String,
    val packageLocation: String
) {
    init {
        val clazzName = ClassName(packageLocation, typeElement.javaClass.simpleName)
    }
}


fun String.toUperFirstCase() = "${this[0].toUpperCase()}${this.subSequence(1, this.length)}"
