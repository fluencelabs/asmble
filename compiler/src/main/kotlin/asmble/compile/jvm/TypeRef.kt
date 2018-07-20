package asmble.compile.jvm

import org.objectweb.asm.Type

/**
 * A Java field or method type. This class can be used to make it easier to
 * manipulate type and method descriptors.
 *
 * @param asm Wrapped [org.objectweb.asm.Type] from asm library
 */
data class TypeRef(val asm: Type) {

    /** The internal name of the class corresponding to this object or array type. */
    val asmName: String get() = asm.internalName

    /** The descriptor corresponding to this Java type. */
    val asmDesc: String get() = asm.descriptor

    /** Size of this type in stack, either 1 or 2 only allowed, where 1 = 2^32` bits */
    val stackSize: Int get() = if (asm == Type.DOUBLE_TYPE || asm == Type.LONG_TYPE) 2 else 1

    fun asMethodRetDesc(vararg args: TypeRef) = Type.getMethodDescriptor(asm, *args.map { it.asm }.toTypedArray())

    fun equivalentTo(other: TypeRef) = this == other || this == Unknown || other == Unknown

    object UnknownType

    companion object {
        val Unknown = UnknownType::class.ref
    }
}