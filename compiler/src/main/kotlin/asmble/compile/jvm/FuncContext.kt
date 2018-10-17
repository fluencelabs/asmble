package asmble.compile.jvm

import asmble.ast.Node
import asmble.util.Logger

/**
 * Jvm context of the function execution.
 *
 * @param cls class execution context
 * @param node Ast of this function
 * @param insns instructions list
 * @param memIsLocalVar true if function uses only local variables and doesn't load
 *                       or store to/from memory.
 */
data class FuncContext(
    val cls: ClsContext,
    val node: Node.Func,
    val insns: List<Insn>,
    val memIsLocalVar: Boolean = false
) : Logger by cls.logger {
    fun actualLocalIndex(givenIndex: Int) = node.actualLocalIndex(givenIndex)
}