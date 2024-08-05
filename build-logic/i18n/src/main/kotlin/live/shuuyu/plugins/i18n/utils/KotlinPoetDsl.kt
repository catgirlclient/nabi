package live.shuuyu.plugins.i18n.utils

import com.squareup.kotlinpoet.*

@DslMarker
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.TYPE)
internal annotation class KotlinPoetDsl

internal typealias FileSpecBuilder = (@KotlinPoetDsl FileSpec.Builder).() -> (Unit)
internal typealias TypeSpecBuilder = (@KotlinPoetDsl TypeSpec.Builder).() -> (Unit)
internal typealias AnnotationSpecBuilder = (@KotlinPoetDsl AnnotationSpec.Builder).() -> (Unit)
internal typealias FunSpecBuilder = (@KotlinPoetDsl FunSpec.Builder).() -> Unit
internal typealias PropertySpecBuilder = (@KotlinPoetDsl PropertySpec.Builder).() -> (Unit)
internal typealias ParameterSpecBuilder = (@KotlinPoetDsl ParameterSpec.Builder).() -> (Unit)
internal typealias CodeBlockBuilder = (@KotlinPoetDsl CodeBlock.Builder).() -> (Unit)

internal fun TypeSpec.Builder.addFunction(name: String, builder: FunSpecBuilder) =
    addFunction(FunSpec.builder(name).apply(builder).build())

// TODO: Fix later lol
/*
internal fun TypeSpec.Builder.addProperty(name: String, type: String, builder: PropertySpecBuilder) =
    addProperty(PropertySpec.builder(name, type))
 */