package com.novemio.android.lib.processor

import com.novemio.android.lib.sealedclassextend.SealedExtension
import com.squareup.kotlinpoet.*
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

class SealedProcessor : AbstractProcessor() {


    private var filer: Filer? = null
    private lateinit var messager: Messager
    private var elements: Elements? = null
    private var namespaceAnnotation: AnnotationSpec? = null
    private var rootBodyAnnotation: AnnotationSpec? = null
    private var rootEnvelopeAnnotation: AnnotationSpec? = null

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
    }

    override fun process(set: Set<TypeElement>, roundEnvironment: RoundEnvironment): Boolean {
        for (element in roundEnvironment.getElementsAnnotatedWith(SealedExtension::class.java)) {

            checkAnnotationFitsItsKind(element)

            val typeElement = element as TypeElement

            // Annotated class info
            val classSimpleName = typeElement.simpleName.toString()
            val packageLocation = elements!!.getPackageOf(typeElement).qualifiedName.toString()

            // Annotated class type
            val rootSlassName = ClassName(packageLocation, classSimpleName)





            try {

                val list = mutableListOf<BuilderFunction>()

                element.getEnclosedElements()?.forEach { child ->
                    if (child.kind == ElementKind.CLASS) {
                        val type = child as TypeElement

                        // Annotated class info
                        val simpleName = child.simpleName.toString()
                        list.add(
                            BuilderFunction(
                                type,
                                simpleName,
                                packageLocation
                            )
                        )
                    }
                    messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "SEALED_STATE:-------------------------------------${child.simpleName}\n"
                    )


                }

                createExtensonFunction(rootSlassName, packageLocation, list)

            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }

        return true
    }

    @Throws(IOException::class)
    private fun createExtensonFunction(
        rootClassName: ClassName,
        packageName: String,
        list: List<BuilderFunction>
    ) {
        val fileName = "Extension${rootClassName.simpleName}"
        val fileSpec = FileSpec.builder(packageName, fileName)
        list.forEach {

            val childName = "${rootClassName.simpleName}.${it.classSimpleName}"
            val onSuccessType = LambdaTypeName.get(
                parameters = *arrayOf(TypeVariableName(name = childName)),
                returnType = TypeVariableName.Companion.invoke("R")
            )
//            fileSpec.addImport(rootClassName.canonicalName)
            fileSpec.addFunction(
                FunSpec.builder("is${it.classSimpleName.toUperFirstCase()}")
                    .addTypeVariable(TypeVariableName.Companion.invoke("R"))
                    .addParameter("block", onSuccessType)
                    .receiver(rootClassName)
                    .returns(TypeVariableName.Companion.invoke("R?"))
                    .addStatement("return if (this is ${childName}) block.invoke(this) else null")
                    .build()
            )
        }


        fileSpec.build().writeTo(filer!!)

    }

    private fun checkAnnotationFitsItsKind(element: Element) {
        if (element.kind != ElementKind.CLASS) {
            messager.printMessage(Diagnostic.Kind.ERROR, "Annotation applied on wrong element")
            return
        }
    }

    //
    override fun getSupportedAnnotationTypes(): Set<String> {

        val annotations = LinkedHashSet<String>()
        annotations.add(SealedExtension::class.java.canonicalName)
        return annotations
    }
}
