package live.shuuyu.plugins.i18n.utils

import com.squareup.kotlinpoet.*
import kotlin.reflect.KClass

internal typealias Builder<T> = T.() -> (Unit)

internal typealias CodeBlockBuilder = Builder<CodeBlock.Builder>
internal typealias FileSpecBuilder = Builder<FileSpec.Builder>
internal typealias FunSpecBuilder = Builder<FunSpec.Builder>
internal typealias PropertySpecBuilder = Builder<PropertySpec.Builder>
internal typealias TypeSpecBuilder = Builder<TypeSpec.Builder>

/**
 * Creates a [CodeBlock] with the supplied parameter.
 *
 * @param builder Modifies the contents of the [CodeBlock]
 *
 * @see CodeBlock.Builder
 *
 * @since 0.0.1
 */
internal fun createCodeBlock(
    builder: CodeBlockBuilder
): CodeBlock = CodeBlock.builder().apply(builder).build()

/**
 * Creates a [FunSpec] with the supplied parameters.
 *
 * @param name The name of the function.
 * @param builder Modifies the contents of the function.
 *
 * @see FunSpec.Builder
 *
 * @since 0.0.1
 */
internal fun createFunction(
    name: String,
    builder: FunSpecBuilder
): FunSpec = FunSpec.builder(name).apply(builder).build()

/**
 * Creates a [FileSpec] with the supplied parameters.
 *
 * @param packageName The package the file is located at.
 * @param className The name of the file.
 * @param builder Modifies the contents of the file.
 *
 * @see FileSpec.Builder
 *
 * @since 0.0.1
 */
internal fun createFile(
    packageName: String,
    className: String,
    builder: FileSpecBuilder
): FileSpec = FileSpec.builder(packageName, className).apply(builder).build()

/**
 * Creates a [FileSpec] with the supplied parameters.
 *
 * @param className See [ClassName].
 * @param builder Modifies the contents of the file.
 *
 * @see FileSpec.Builder
 *
 * @since 0.0.1
 */
internal fun createFile(
    className: ClassName,
    builder: FileSpecBuilder
): FileSpec = FileSpec.builder(className).apply(builder).build()

/**
 * Creates a [FileSpec] with the supplied parameters.
 *
 * @param memberName See [MemberName].
 * @param builder Modifies the contents of the file.
 *
 * @see FileSpec
 *
 * @since 0.0.1
 */
internal fun createFile(
    memberName: MemberName,
    builder: FileSpecBuilder
): FileSpec = FileSpec.builder(memberName).apply(builder).build()

/**
 * Creates a [PropertySpec] with the supplied parameters.
 *
 * @param name The name of the property.
 * @param clazz The type of which is returned.
 * @param builder Modifies the contents of the property.
 *
 * @see PropertySpec.Builder
 *
 * @since 0.0.1
 */
internal fun createProperty(
    name: String,
    clazz: KClass<*>,
    builder: PropertySpecBuilder
): PropertySpec = PropertySpec.builder(name, clazz).apply(builder).build()

/**
 * Creates a Kotlin [Object].
 *
 * @param name The name of the object.
 * @param builder Modifies the contents of the object.
 *
 * @see TypeSpec.Builder
 *
 * @since 0.0.1
 */
internal fun createObject(
    name: String,
    builder: TypeSpecBuilder
): TypeSpec = TypeSpec.objectBuilder(name).apply(builder).build()