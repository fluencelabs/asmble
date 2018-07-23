package asmble.compile.jvm

import asmble.ast.Node
import asmble.util.Logger

/**
 * Jvm context of execution a function.
 *
 * @param cls Class execution context
 * @param node Ast of this function
 * @param insns A list of instructions
 * @param memIsLocalVar If true then function use only local variables and don't load
 *                       and store from memory.
 */
data class FuncContext(
    val cls: ClsContext,
    val node: Node.Func,
    val insns: List<Insn>,
    val memIsLocalVar: Boolean = false
) : Logger by cls.logger {
    fun actualLocalIndex(givenIndex: Int) = node.actualLocalIndex(givenIndex)
}