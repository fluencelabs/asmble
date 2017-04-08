package asmble.ast

import kotlin.reflect.KClass

sealed class Node {
    data class Module(
        val types: List<Type.Func> = emptyList(),
        val imports: List<Import> = emptyList(),
        val tables: List<Type.Table> = emptyList(),
        val memories: List<Type.Memory> = emptyList(),
        val globals: List<Global> = emptyList(),
        val exports: List<Export> = emptyList(),
        val startFuncIndex: Int? = null,
        val elems: List<Elem> = emptyList(),
        val funcs: List<Func> = emptyList(),
        val data: List<Data> = emptyList(),
        val customSections: List<CustomSection> = emptyList()
    ) : Node()

    enum class ExternalKind {
        FUNCTION, TABLE, MEMORY, GLOBAL
    }

    enum class ElemType {
        ANYFUNC
    }

    sealed class Type : Node() {

        sealed class Value : Type() {
            object I32 : Value()
            object I64 : Value()
            object F32 : Value()
            object F64 : Value()
        }

        data class Func(
            val params: List<Value>,
            val ret: Value?
        ) : Type()

        data class Global(
            val contentType: Value,
            val mutable: Boolean
        ) : Type()

        data class Table(
            val elemType: ElemType,
            val limits: ResizableLimits
        ) : Type()

        data class Memory(
            val limits: ResizableLimits
        ) : Type()
    }

    data class ResizableLimits(
        val initial: Int,
        val maximum: Int?
    )

    data class Import(
        val module: String,
        val field: String,
        val kind: Kind
    ) : Node() {
        sealed class Kind {
            data class Func(val typeIndex: Int) : Kind()
            data class Table(val type: Type.Table) : Kind()
            data class Memory(val type: Type.Memory) : Kind()
            data class Global(val type: Type.Global) : Kind()
        }
    }

    data class Global(
        val type: Type.Global,
        val init: List<Instr>
    ) : Node()

    data class Export(
        val field: String,
        val kind: ExternalKind,
        val index: Int
    ) : Node()

    data class Elem(
        val index: Int,
        val offset: List<Instr>,
        val funcIndices: List<Int>
    ) : Node()

    data class Func(
        val type: Type.Func,
        val locals: List<Type.Value>,
        val instructions: List<Instr>
    ) : Node()

    data class Data(
        val index: Int,
        val offset: List<Instr>,
        val data: ByteArray
    ) : Node()

    data class CustomSection(
        // The order of custom sections amongst themselves is based on how they
        // appear in the module node.
        val beforeSectionId: Int,
        val name: String,
        val payload: ByteArray
    ) : Node()

    sealed class Instr : Node() {

        fun op() = InstrOp.classToOpMap[this::class] ?: throw Exception("No op found for ${this::class}")

        // Interfaces to help extraction
        interface Args {
            interface None : Args
            interface Type : Args { val type: Node.Type.Value? }
            interface RelativeDepth : Args { val relativeDepth: Int }
            interface Table : Args { val targetTable: List<Int>; val default: Int }
            interface Index : Args { val index: Int }
            interface Reserved : Args { val reserved: Boolean }
            interface ReservedIndex : Index, Reserved
            interface AlignOffset : Args { val align: Int; val offset: Long }
            interface Const<out T : Number> : Args { val value: T }
        }

        // Control flow
        object Unreachable : Instr(), Args.None
        object Nop : Instr(), Args.None
        data class Block(override val type: Type.Value?) : Instr(), Args.Type
        data class Loop(override val type: Type.Value?) : Instr(), Args.Type
        data class If(override val type: Type.Value?) : Instr(), Args.Type
        object Else : Instr(), Args.None
        object End : Instr(), Args.None
        data class Br(override val relativeDepth: Int) : Instr(), Args.RelativeDepth
        data class BrIf(override val relativeDepth: Int) : Instr(), Args.RelativeDepth
        data class BrTable(
            override val targetTable: List<Int>,
            override val default: Int
        ) : Instr(), Args.Table
        object Return : Instr()

        // Call operators
        data class Call(override val index: Int) : Instr(), Args.Index
        data class CallIndirect(
            override val index: Int,
            override val reserved: Boolean
        ) : Instr(), Args.ReservedIndex

        // Parametric operators
        object Drop : Instr(), Args.None
        object Select : Instr(), Args.None

        // Variable access
        data class GetLocal(override val index: Int) : Instr(), Args.Index
        data class SetLocal(override val index: Int) : Instr(), Args.Index
        data class TeeLocal(override val index: Int) : Instr(), Args.Index
        data class GetGlobal(override val index: Int) : Instr(), Args.Index
        data class SetGlobal(override val index: Int) : Instr(), Args.Index

        // Memory operators
        data class I32Load(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class F32Load(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class F64Load(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Load8S(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Load8U(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Load16S(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Load16U(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load8S(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load8U(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load16S(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load16U(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load32S(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Load32U(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Store(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Store(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class F32Store(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class F64Store(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Store8(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I32Store16(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Store8(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Store16(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class I64Store32(override val align: Int, override val offset: Long) : Instr(), Args.AlignOffset
        data class CurrentMemory(override val reserved: Boolean) : Instr(), Args.Reserved
        data class GrowMemory(override val reserved: Boolean) : Instr(), Args.Reserved

        // Constants
        data class I32Const(override val value: Int) : Instr(), Args.Const<Int>
        data class I64Const(override val value: Long) : Instr(), Args.Const<Long>
        data class F32Const(override val value: Float) : Instr(), Args.Const<Float>
        data class F64Const(override val value: Double) : Instr(), Args.Const<Double>

        // Comparison operators
        object I32Eqz : Instr(), Args.None
        object I32Eq : Instr(), Args.None
        object I32Ne : Instr(), Args.None
        object I32LtS : Instr(), Args.None
        object I32LtU : Instr(), Args.None
        object I32GtS : Instr(), Args.None
        object I32GtU : Instr(), Args.None
        object I32LeS : Instr(), Args.None
        object I32LeU : Instr(), Args.None
        object I32GeS : Instr(), Args.None
        object I32GeU : Instr(), Args.None
        object I64Eqz : Instr(), Args.None
        object I64Eq : Instr(), Args.None
        object I64Ne : Instr(), Args.None
        object I64LtS : Instr(), Args.None
        object I64LtU : Instr(), Args.None
        object I64GtS : Instr(), Args.None
        object I64GtU : Instr(), Args.None
        object I64LeS : Instr(), Args.None
        object I64LeU : Instr(), Args.None
        object I64GeS : Instr(), Args.None
        object I64GeU : Instr(), Args.None
        object F32Eq : Instr(), Args.None
        object F32Ne : Instr(), Args.None
        object F32Lt : Instr(), Args.None
        object F32Gt : Instr(), Args.None
        object F32Le : Instr(), Args.None
        object F32Ge : Instr(), Args.None
        object F64Eq : Instr(), Args.None
        object F64Ne : Instr(), Args.None
        object F64Lt : Instr(), Args.None
        object F64Gt : Instr(), Args.None
        object F64Le : Instr(), Args.None
        object F64Ge : Instr(), Args.None

        // Numeric operators
        object I32Clz : Instr(), Args.None
        object I32Ctz : Instr(), Args.None
        object I32Popcnt : Instr(), Args.None
        object I32Add : Instr(), Args.None
        object I32Sub : Instr(), Args.None
        object I32Mul : Instr(), Args.None
        object I32DivS : Instr(), Args.None
        object I32DivU : Instr(), Args.None
        object I32RemS : Instr(), Args.None
        object I32RemU : Instr(), Args.None
        object I32And : Instr(), Args.None
        object I32Or : Instr(), Args.None
        object I32Xor : Instr(), Args.None
        object I32Shl : Instr(), Args.None
        object I32ShrS : Instr(), Args.None
        object I32ShrU : Instr(), Args.None
        object I32Rotl : Instr(), Args.None
        object I32Rotr : Instr(), Args.None
        object I64Clz : Instr(), Args.None
        object I64Ctz : Instr(), Args.None
        object I64Popcnt : Instr(), Args.None
        object I64Add : Instr(), Args.None
        object I64Sub : Instr(), Args.None
        object I64Mul : Instr(), Args.None
        object I64DivS : Instr(), Args.None
        object I64DivU : Instr(), Args.None
        object I64RemS : Instr(), Args.None
        object I64RemU : Instr(), Args.None
        object I64And : Instr(), Args.None
        object I64Or : Instr(), Args.None
        object I64Xor : Instr(), Args.None
        object I64Shl : Instr(), Args.None
        object I64ShrS : Instr(), Args.None
        object I64ShrU : Instr(), Args.None
        object I64Rotl : Instr(), Args.None
        object I64Rotr : Instr(), Args.None
        object F32Abs : Instr(), Args.None
        object F32Neg : Instr(), Args.None
        object F32Ceil : Instr(), Args.None
        object F32Floor : Instr(), Args.None
        object F32Trunc : Instr(), Args.None
        object F32Nearest : Instr(), Args.None
        object F32Sqrt : Instr(), Args.None
        object F32Add : Instr(), Args.None
        object F32Sub : Instr(), Args.None
        object F32Mul : Instr(), Args.None
        object F32Div : Instr(), Args.None
        object F32Min : Instr(), Args.None
        object F32Max : Instr(), Args.None
        object F32CopySign : Instr(), Args.None
        object F64Abs : Instr(), Args.None
        object F64Neg : Instr(), Args.None
        object F64Ceil : Instr(), Args.None
        object F64Floor : Instr(), Args.None
        object F64Trunc : Instr(), Args.None
        object F64Nearest : Instr(), Args.None
        object F64Sqrt : Instr(), Args.None
        object F64Add : Instr(), Args.None
        object F64Sub : Instr(), Args.None
        object F64Mul : Instr(), Args.None
        object F64Div : Instr(), Args.None
        object F64Min : Instr(), Args.None
        object F64Max : Instr(), Args.None
        object F64CopySign : Instr(), Args.None

        // Conversions
        object I32WrapI64 : Instr(), Args.None
        object I32TruncSF32 : Instr(), Args.None
        object I32TruncUF32 : Instr(), Args.None
        object I32TruncSF64 : Instr(), Args.None
        object I32TruncUF64 : Instr(), Args.None
        object I64ExtendSI32 : Instr(), Args.None
        object I64ExtendUI32 : Instr(), Args.None
        object I64TruncSF32 : Instr(), Args.None
        object I64TruncUF32 : Instr(), Args.None
        object I64TruncSF64 : Instr(), Args.None
        object I64TruncUF64 : Instr(), Args.None
        object F32ConvertSI32 : Instr(), Args.None
        object F32ConvertUI32 : Instr(), Args.None
        object F32ConvertSI64 : Instr(), Args.None
        object F32ConvertUI64 : Instr(), Args.None
        object F32DemoteF64 : Instr(), Args.None
        object F64ConvertSI32 : Instr(), Args.None
        object F64ConvertUI32 : Instr(), Args.None
        object F64ConvertSI64 : Instr(), Args.None
        object F64ConvertUI64 : Instr(), Args.None
        object F64PromoteF32 : Instr(), Args.None

        // Reinterpretations
        object I32ReinterpretF32 : Instr(), Args.None
        object I64ReinterpretF64 : Instr(), Args.None
        object F32ReinterpretI32 : Instr(), Args.None
        object F64ReinterpretI64 : Instr(), Args.None
    }

    sealed class InstrOp<out A : Instr.Args> {

        abstract val name: String
        val opcode get() = strToOpcodeMap[name] ?: error("No known opcode for $name")

        @Suppress("UNCHECKED_CAST")
        fun argsOf(i: Instr): A = i as A

        sealed class ControlFlowOp<out A : Instr.Args> : InstrOp<A>() {
            data class NoArg(override val name: String, val create: Instr) : ControlFlowOp<Instr.Args.None>()
            data class TypeArg(override val name: String, val create: (Type.Value?) -> Instr) : ControlFlowOp<Instr.Args.Type>()
            data class DepthArg(override val name: String, val create: (Int) -> Instr) : ControlFlowOp<Instr.Args.RelativeDepth>()
            data class TableArg(override val name: String, val create: (List<Int>, Int) -> Instr) : ControlFlowOp<Instr.Args.Table>()
        }

        sealed class CallOp<out A : Instr.Args> : InstrOp<A>() {
            data class IndexArg(override val name: String, val create: (Int) -> Instr) : CallOp<Instr.Args.Index>()
            data class IndexReservedArg(override val name: String, val create: (Int, Boolean) -> Instr) : CallOp<Instr.Args.ReservedIndex>()
        }

        sealed class ParamOp : InstrOp<Instr.Args.None>() {
            data class NoArg(override val name: String, val create: Instr) : ParamOp()
        }

        sealed class VarOp : InstrOp<Instr.Args.Index>() {
            data class IndexArg(override val name: String, val create: (Int) -> Instr) : VarOp()
        }

        sealed class MemOp<out A : Instr.Args> : InstrOp<A>() {
            data class AlignOffsetArg(override val name: String, val create: (Int, Long) -> Instr) : MemOp<Instr.Args.AlignOffset>()
            data class ReservedArg(override val name: String, val create: (Boolean) -> Instr) : MemOp<Instr.Args.Reserved>()
        }

        sealed class ConstOp<out T : Number> : InstrOp<Instr.Args.Const<T>>() {
            data class IntArg(override val name: String, val create: (Int) -> Instr) : ConstOp<Int>()
            data class LongArg(override val name: String, val create: (Long) -> Instr) : ConstOp<Long>()
            data class FloatArg(override val name: String, val create: (Float) -> Instr) : ConstOp<Float>()
            data class DoubleArg(override val name: String, val create: (Double) -> Instr) : ConstOp<Double>()
        }

        sealed class CompareOp : InstrOp<Instr.Args.None>() {
            data class NoArg(override val name: String, val create: Instr) : CompareOp()
        }

        sealed class NumOp : InstrOp<Instr.Args.None>() {
            data class NoArg(override val name: String, val create: Instr) : NumOp()
        }

        sealed class ConvertOp : InstrOp<Instr.Args.None>() {
            data class NoArg(override val name: String, val create: Instr) : ConvertOp()
        }

        sealed class ReinterpretOp : InstrOp<Instr.Args.None>() {
            data class NoArg(override val name: String, val create: Instr) : ReinterpretOp()
        }

        companion object {
            // TODO: why can't I set a val in init?
            var strToOpMap = emptyMap<String, InstrOp<*>>(); private set
            var classToOpMap = emptyMap<KClass<out Instr>, InstrOp<*>>(); private set
            var strToOpcodeMap = emptyMap<String, Short>(); private set

            init {
                // Can't use reification here because inline funcs not allowed in nested context :-(
                fun <T> opMapEntry(
                    name: String,
                    opcode: Short,
                    newOp: (String, T) -> InstrOp<*>,
                    create: T, clazz: KClass<out Instr>
                ) {
                    require(!strToOpMap.contains(name) && !classToOpMap.contains(clazz)) {
                        "Name '$name', class '$clazz', or op '$opcode' already exists"
                    }
                    val op = newOp(name, create)
                    strToOpMap += name to op
                    classToOpMap += clazz to op
                    strToOpcodeMap += name to opcode
                }

                opMapEntry("unreachable", 0x00, ::ControlFlowOpNoArg, Instr.Unreachable, Instr.Unreachable::class)
                opMapEntry("nop", 0x01, ::ControlFlowOpNoArg, Instr.Nop, Instr.Nop::class)
                opMapEntry("block", 0x02, ::ControlFlowOpTypeArg, Instr::Block, Instr.Block::class)
                opMapEntry("loop", 0x03, ::ControlFlowOpTypeArg, Instr::Loop, Instr.Loop::class)
                opMapEntry("if", 0x04, ::ControlFlowOpTypeArg, Instr::If, Instr.If::class)
                opMapEntry("else", 0x05, ::ControlFlowOpNoArg, Instr.Else, Instr.Else::class)
                opMapEntry("end", 0x0b, ::ControlFlowOpNoArg, Instr.End, Instr.End::class)
                opMapEntry("br", 0x0c, ::ControlFlowOpDepthArg, Instr::Br, Instr.Br::class)
                opMapEntry("br_if", 0x0d, ::ControlFlowOpDepthArg, Instr::BrIf, Instr.BrIf::class)
                opMapEntry("br_table", 0x0e, ::ControlFlowOpTableArg, Instr::BrTable, Instr.BrTable::class)
                opMapEntry("return", 0x0f, ::ControlFlowOpNoArg, Instr.Return, Instr.Return::class)

                opMapEntry("call", 0x10, ::CallOpIndexArg, Instr::Call, Instr.Call::class)
                opMapEntry("call_indirect", 0x11, ::CallOpIndexReservedArg, Instr::CallIndirect, Instr.CallIndirect::class)

                opMapEntry("drop", 0x1a, ParamOp::NoArg, Instr.Drop, Instr.Drop::class)
                opMapEntry("select", 0x1b, ParamOp::NoArg, Instr.Select, Instr.Select::class)

                opMapEntry("get_local", 0x20, VarOp::IndexArg, Instr::GetLocal, Instr.GetLocal::class)
                opMapEntry("set_local", 0x21, VarOp::IndexArg, Instr::SetLocal, Instr.SetLocal::class)
                opMapEntry("tee_local", 0x22, VarOp::IndexArg, Instr::TeeLocal, Instr.TeeLocal::class)
                opMapEntry("get_global", 0x23, VarOp::IndexArg, Instr::GetGlobal, Instr.GetGlobal::class)
                opMapEntry("set_global", 0x24, VarOp::IndexArg, Instr::SetGlobal, Instr.SetGlobal::class)

                opMapEntry("i32.load", 0x28, ::MemOpAlignOffsetArg, Instr::I32Load, Instr.I32Load::class)
                opMapEntry("i64.load", 0x29, ::MemOpAlignOffsetArg, Instr::I64Load, Instr.I64Load::class)
                opMapEntry("f32.load", 0x2a, ::MemOpAlignOffsetArg, Instr::F32Load, Instr.F32Load::class)
                opMapEntry("f64.load", 0x2b, ::MemOpAlignOffsetArg, Instr::F64Load, Instr.F64Load::class)
                opMapEntry("i32.load8_s", 0x2c, ::MemOpAlignOffsetArg, Instr::I32Load8S, Instr.I32Load8S::class)
                opMapEntry("i32.load8_u", 0x2d, ::MemOpAlignOffsetArg, Instr::I32Load8U, Instr.I32Load8U::class)
                opMapEntry("i32.load16_s", 0x2e, ::MemOpAlignOffsetArg, Instr::I32Load16S, Instr.I32Load16S::class)
                opMapEntry("i32.load16_u", 0x2f, ::MemOpAlignOffsetArg, Instr::I32Load16U, Instr.I32Load16U::class)
                opMapEntry("i64.load8_s", 0x30, ::MemOpAlignOffsetArg, Instr::I64Load8S, Instr.I64Load8S::class)
                opMapEntry("i64.load8_u", 0x31, ::MemOpAlignOffsetArg, Instr::I64Load8U, Instr.I64Load8U::class)
                opMapEntry("i64.load16_s", 0x32, ::MemOpAlignOffsetArg, Instr::I64Load16S, Instr.I64Load16S::class)
                opMapEntry("i64.load16_u", 0x33, ::MemOpAlignOffsetArg, Instr::I64Load16U, Instr.I64Load16U::class)
                opMapEntry("i64.load32_s", 0x34, ::MemOpAlignOffsetArg, Instr::I64Load32S, Instr.I64Load32S::class)
                opMapEntry("i64.load32_u", 0x35, ::MemOpAlignOffsetArg, Instr::I64Load32U, Instr.I64Load32U::class)
                opMapEntry("i32.store", 0x36, ::MemOpAlignOffsetArg, Instr::I32Store, Instr.I32Store::class)
                opMapEntry("i64.store", 0x37, ::MemOpAlignOffsetArg, Instr::I64Store, Instr.I64Store::class)
                opMapEntry("f32.store", 0x38, ::MemOpAlignOffsetArg, Instr::F32Store, Instr.F32Store::class)
                opMapEntry("f64.store", 0x39, ::MemOpAlignOffsetArg, Instr::F64Store, Instr.F64Store::class)
                opMapEntry("i32.store8", 0x3a, ::MemOpAlignOffsetArg, Instr::I32Store8, Instr.I32Store8::class)
                opMapEntry("i32.store16", 0x3b, ::MemOpAlignOffsetArg, Instr::I32Store16, Instr.I32Store16::class)
                opMapEntry("i64.store8", 0x3c, ::MemOpAlignOffsetArg, Instr::I64Store8, Instr.I64Store8::class)
                opMapEntry("i64.store16", 0x3d, ::MemOpAlignOffsetArg, Instr::I64Store16, Instr.I64Store16::class)
                opMapEntry("i64.store32", 0x3e, ::MemOpAlignOffsetArg, Instr::I64Store32, Instr.I64Store32::class)
                opMapEntry("current_memory", 0x3f, ::MemOpReservedArg, Instr::CurrentMemory, Instr.CurrentMemory::class)
                opMapEntry("grow_memory", 0x40, ::MemOpReservedArg, Instr::GrowMemory, Instr.GrowMemory::class)

                opMapEntry("i32.const", 0x41, ::ConstOpIntArg, Instr::I32Const, Instr.I32Const::class)
                opMapEntry("i64.const", 0x42, ::ConstOpLongArg, Instr::I64Const, Instr.I64Const::class)
                opMapEntry("f32.const", 0x43, ::ConstOpFloatArg, Instr::F32Const, Instr.F32Const::class)
                opMapEntry("f64.const", 0x44, ::ConstOpDoubleArg, Instr::F64Const, Instr.F64Const::class)

                opMapEntry("i32.eqz", 0x45, CompareOp::NoArg, Instr.I32Eqz, Instr.I32Eqz::class)
                opMapEntry("i32.eq", 0x46, CompareOp::NoArg, Instr.I32Eq, Instr.I32Eq::class)
                opMapEntry("i32.ne", 0x47, CompareOp::NoArg, Instr.I32Ne, Instr.I32Ne::class)
                opMapEntry("i32.lt_s", 0x48, CompareOp::NoArg, Instr.I32LtS, Instr.I32LtS::class)
                opMapEntry("i32.lt_u", 0x49, CompareOp::NoArg, Instr.I32LtU, Instr.I32LtU::class)
                opMapEntry("i32.gt_s", 0x4a, CompareOp::NoArg, Instr.I32GtS, Instr.I32GtS::class)
                opMapEntry("i32.gt_u", 0x4b, CompareOp::NoArg, Instr.I32GtU, Instr.I32GtU::class)
                opMapEntry("i32.le_s", 0x4c, CompareOp::NoArg, Instr.I32LeS, Instr.I32LeS::class)
                opMapEntry("i32.le_u", 0x4d, CompareOp::NoArg, Instr.I32LeU, Instr.I32LeU::class)
                opMapEntry("i32.ge_s", 0x4e, CompareOp::NoArg, Instr.I32GeS, Instr.I32GeS::class)
                opMapEntry("i32.ge_u", 0x4f, CompareOp::NoArg, Instr.I32GeU, Instr.I32GeU::class)
                opMapEntry("i64.eqz", 0x50, CompareOp::NoArg, Instr.I64Eqz, Instr.I64Eqz::class)
                opMapEntry("i64.eq", 0x51, CompareOp::NoArg, Instr.I64Eq, Instr.I64Eq::class)
                opMapEntry("i64.ne", 0x52, CompareOp::NoArg, Instr.I64Ne, Instr.I64Ne::class)
                opMapEntry("i64.lt_s", 0x53, CompareOp::NoArg, Instr.I64LtS, Instr.I64LtS::class)
                opMapEntry("i64.lt_u", 0x54, CompareOp::NoArg, Instr.I64LtU, Instr.I64LtU::class)
                opMapEntry("i64.gt_s", 0x55, CompareOp::NoArg, Instr.I64GtS, Instr.I64GtS::class)
                opMapEntry("i64.gt_u", 0x56, CompareOp::NoArg, Instr.I64GtU, Instr.I64GtU::class)
                opMapEntry("i64.le_s", 0x57, CompareOp::NoArg, Instr.I64LeS, Instr.I64LeS::class)
                opMapEntry("i64.le_u", 0x58, CompareOp::NoArg, Instr.I64LeU, Instr.I64LeU::class)
                opMapEntry("i64.ge_s", 0x59, CompareOp::NoArg, Instr.I64GeS, Instr.I64GeS::class)
                opMapEntry("i64.ge_u", 0x5a, CompareOp::NoArg, Instr.I64GeU, Instr.I64GeU::class)
                opMapEntry("f32.eq", 0x5b, CompareOp::NoArg, Instr.F32Eq, Instr.F32Eq::class)
                opMapEntry("f32.ne", 0x5c, CompareOp::NoArg, Instr.F32Ne, Instr.F32Ne::class)
                opMapEntry("f32.lt", 0x5d, CompareOp::NoArg, Instr.F32Lt, Instr.F32Lt::class)
                opMapEntry("f32.gt", 0x5e, CompareOp::NoArg, Instr.F32Gt, Instr.F32Gt::class)
                opMapEntry("f32.le", 0x5f, CompareOp::NoArg, Instr.F32Le, Instr.F32Le::class)
                opMapEntry("f32.ge", 0x60, CompareOp::NoArg, Instr.F32Ge, Instr.F32Ge::class)
                opMapEntry("f64.eq", 0x61, CompareOp::NoArg, Instr.F64Eq, Instr.F64Eq::class)
                opMapEntry("f64.ne", 0x62, CompareOp::NoArg, Instr.F64Ne, Instr.F64Ne::class)
                opMapEntry("f64.lt", 0x63, CompareOp::NoArg, Instr.F64Lt, Instr.F64Lt::class)
                opMapEntry("f64.gt", 0x64, CompareOp::NoArg, Instr.F64Gt, Instr.F64Gt::class)
                opMapEntry("f64.le", 0x65, CompareOp::NoArg, Instr.F64Le, Instr.F64Le::class)
                opMapEntry("f64.ge", 0x66, CompareOp::NoArg, Instr.F64Ge, Instr.F64Ge::class)

                opMapEntry("i32.clz", 0x67, NumOp::NoArg, Instr.I32Clz, Instr.I32Clz::class)
                opMapEntry("i32.ctz", 0x68, NumOp::NoArg, Instr.I32Ctz, Instr.I32Ctz::class)
                opMapEntry("i32.popcnt", 0x69, NumOp::NoArg, Instr.I32Popcnt, Instr.I32Popcnt::class)
                opMapEntry("i32.add", 0x6a, NumOp::NoArg, Instr.I32Add, Instr.I32Add::class)
                opMapEntry("i32.sub", 0x6b, NumOp::NoArg, Instr.I32Sub, Instr.I32Sub::class)
                opMapEntry("i32.mul", 0x6c, NumOp::NoArg, Instr.I32Mul, Instr.I32Mul::class)
                opMapEntry("i32.div_s", 0x6d, NumOp::NoArg, Instr.I32DivS, Instr.I32DivS::class)
                opMapEntry("i32.div_u", 0x6e, NumOp::NoArg, Instr.I32DivU, Instr.I32DivU::class)
                opMapEntry("i32.rem_s", 0x6f, NumOp::NoArg, Instr.I32RemS, Instr.I32RemS::class)
                opMapEntry("i32.rem_u", 0x70, NumOp::NoArg, Instr.I32RemU, Instr.I32RemU::class)
                opMapEntry("i32.and", 0x71, NumOp::NoArg, Instr.I32And, Instr.I32And::class)
                opMapEntry("i32.or", 0x72, NumOp::NoArg, Instr.I32Or, Instr.I32Or::class)
                opMapEntry("i32.xor", 0x73, NumOp::NoArg, Instr.I32Xor, Instr.I32Xor::class)
                opMapEntry("i32.shl", 0x74, NumOp::NoArg, Instr.I32Shl, Instr.I32Shl::class)
                opMapEntry("i32.shr_s", 0x75, NumOp::NoArg, Instr.I32ShrS, Instr.I32ShrS::class)
                opMapEntry("i32.shr_u", 0x76, NumOp::NoArg, Instr.I32ShrU, Instr.I32ShrU::class)
                opMapEntry("i32.rotl", 0x77, NumOp::NoArg, Instr.I32Rotl, Instr.I32Rotl::class)
                opMapEntry("i32.rotr", 0x78, NumOp::NoArg, Instr.I32Rotr, Instr.I32Rotr::class)
                opMapEntry("i64.clz", 0x79, NumOp::NoArg, Instr.I64Clz, Instr.I64Clz::class)
                opMapEntry("i64.ctz", 0x7a, NumOp::NoArg, Instr.I64Ctz, Instr.I64Ctz::class)
                opMapEntry("i64.popcnt", 0x7b, NumOp::NoArg, Instr.I64Popcnt, Instr.I64Popcnt::class)
                opMapEntry("i64.add", 0x7c, NumOp::NoArg, Instr.I64Add, Instr.I64Add::class)
                opMapEntry("i64.sub", 0x7d, NumOp::NoArg, Instr.I64Sub, Instr.I64Sub::class)
                opMapEntry("i64.mul", 0x7e, NumOp::NoArg, Instr.I64Mul, Instr.I64Mul::class)
                opMapEntry("i64.div_s", 0x7f, NumOp::NoArg, Instr.I64DivS, Instr.I64DivS::class)
                opMapEntry("i64.div_u", 0x80, NumOp::NoArg, Instr.I64DivU, Instr.I64DivU::class)
                opMapEntry("i64.rem_s", 0x81, NumOp::NoArg, Instr.I64RemS, Instr.I64RemS::class)
                opMapEntry("i64.rem_u", 0x82, NumOp::NoArg, Instr.I64RemU, Instr.I64RemU::class)
                opMapEntry("i64.and", 0x83, NumOp::NoArg, Instr.I64And, Instr.I64And::class)
                opMapEntry("i64.or", 0x84, NumOp::NoArg, Instr.I64Or, Instr.I64Or::class)
                opMapEntry("i64.xor", 0x85, NumOp::NoArg, Instr.I64Xor, Instr.I64Xor::class)
                opMapEntry("i64.shl", 0x86, NumOp::NoArg, Instr.I64Shl, Instr.I64Shl::class)
                opMapEntry("i64.shr_s", 0x87, NumOp::NoArg, Instr.I64ShrS, Instr.I64ShrS::class)
                opMapEntry("i64.shr_u", 0x88, NumOp::NoArg, Instr.I64ShrU, Instr.I64ShrU::class)
                opMapEntry("i64.rotl", 0x89, NumOp::NoArg, Instr.I64Rotl, Instr.I64Rotl::class)
                opMapEntry("i64.rotr", 0x8a, NumOp::NoArg, Instr.I64Rotr, Instr.I64Rotr::class)
                opMapEntry("f32.abs", 0x8b, NumOp::NoArg, Instr.F32Abs, Instr.F32Abs::class)
                opMapEntry("f32.neg", 0x8c, NumOp::NoArg, Instr.F32Neg, Instr.F32Neg::class)
                opMapEntry("f32.ceil", 0x8d, NumOp::NoArg, Instr.F32Ceil, Instr.F32Ceil::class)
                opMapEntry("f32.floor", 0x8e, NumOp::NoArg, Instr.F32Floor, Instr.F32Floor::class)
                opMapEntry("f32.trunc", 0x8f, NumOp::NoArg, Instr.F32Trunc, Instr.F32Trunc::class)
                opMapEntry("f32.nearest", 0x90, NumOp::NoArg, Instr.F32Nearest, Instr.F32Nearest::class)
                opMapEntry("f32.sqrt", 0x91, NumOp::NoArg, Instr.F32Sqrt, Instr.F32Sqrt::class)
                opMapEntry("f32.add", 0x92, NumOp::NoArg, Instr.F32Add, Instr.F32Add::class)
                opMapEntry("f32.sub", 0x93, NumOp::NoArg, Instr.F32Sub, Instr.F32Sub::class)
                opMapEntry("f32.mul", 0x94, NumOp::NoArg, Instr.F32Mul, Instr.F32Mul::class)
                opMapEntry("f32.div", 0x95, NumOp::NoArg, Instr.F32Div, Instr.F32Div::class)
                opMapEntry("f32.min", 0x96, NumOp::NoArg, Instr.F32Min, Instr.F32Min::class)
                opMapEntry("f32.max", 0x97, NumOp::NoArg, Instr.F32Max, Instr.F32Max::class)
                opMapEntry("f32.copysign", 0x98, NumOp::NoArg, Instr.F32CopySign, Instr.F32CopySign::class)
                opMapEntry("f64.abs", 0x99, NumOp::NoArg, Instr.F64Abs, Instr.F64Abs::class)
                opMapEntry("f64.neg", 0x9a, NumOp::NoArg, Instr.F64Neg, Instr.F64Neg::class)
                opMapEntry("f64.ceil", 0x9b, NumOp::NoArg, Instr.F64Ceil, Instr.F64Ceil::class)
                opMapEntry("f64.floor", 0x9c, NumOp::NoArg, Instr.F64Floor, Instr.F64Floor::class)
                opMapEntry("f64.trunc", 0x9d, NumOp::NoArg, Instr.F64Trunc, Instr.F64Trunc::class)
                opMapEntry("f64.nearest", 0x9e, NumOp::NoArg, Instr.F64Nearest, Instr.F64Nearest::class)
                opMapEntry("f64.sqrt", 0x9f, NumOp::NoArg, Instr.F64Sqrt, Instr.F64Sqrt::class)
                opMapEntry("f64.add", 0xa0, NumOp::NoArg, Instr.F64Add, Instr.F64Add::class)
                opMapEntry("f64.sub", 0xa1, NumOp::NoArg, Instr.F64Sub, Instr.F64Sub::class)
                opMapEntry("f64.mul", 0xa2, NumOp::NoArg, Instr.F64Mul, Instr.F64Mul::class)
                opMapEntry("f64.div", 0xa3, NumOp::NoArg, Instr.F64Div, Instr.F64Div::class)
                opMapEntry("f64.min", 0xa4, NumOp::NoArg, Instr.F64Min, Instr.F64Min::class)
                opMapEntry("f64.max", 0xa5, NumOp::NoArg, Instr.F64Max, Instr.F64Max::class)
                opMapEntry("f64.copysign", 0xa6, NumOp::NoArg, Instr.F64CopySign, Instr.F64CopySign::class)

                opMapEntry("i32.wrap/i64", 0xa7, ConvertOp::NoArg, Instr.I32WrapI64, Instr.I32WrapI64::class)
                opMapEntry("i32.trunc_s/f32", 0xa8, ConvertOp::NoArg, Instr.I32TruncSF32, Instr.I32TruncSF32::class)
                opMapEntry("i32.trunc_u/f32", 0xa9, ConvertOp::NoArg, Instr.I32TruncUF32, Instr.I32TruncUF32::class)
                opMapEntry("i32.trunc_s/f64", 0xaa, ConvertOp::NoArg, Instr.I32TruncSF64, Instr.I32TruncSF64::class)
                opMapEntry("i32.trunc_u/f64", 0xab, ConvertOp::NoArg, Instr.I32TruncUF64, Instr.I32TruncUF64::class)
                opMapEntry("i64.extend_s/i32", 0xac, ConvertOp::NoArg, Instr.I64ExtendSI32, Instr.I64ExtendSI32::class)
                opMapEntry("i64.extend_u/i32", 0xad, ConvertOp::NoArg, Instr.I64ExtendUI32, Instr.I64ExtendUI32::class)
                opMapEntry("i64.trunc_s/f32", 0xae, ConvertOp::NoArg, Instr.I64TruncSF32, Instr.I64TruncSF32::class)
                opMapEntry("i64.trunc_u/f32", 0xaf, ConvertOp::NoArg, Instr.I64TruncUF32, Instr.I64TruncUF32::class)
                opMapEntry("i64.trunc_s/f64", 0xb0, ConvertOp::NoArg, Instr.I64TruncSF64, Instr.I64TruncSF64::class)
                opMapEntry("i64.trunc_u/f64", 0xb1, ConvertOp::NoArg, Instr.I64TruncUF64, Instr.I64TruncUF64::class)
                opMapEntry("f32.convert_s/i32", 0xb2, ConvertOp::NoArg, Instr.F32ConvertSI32, Instr.F32ConvertSI32::class)
                opMapEntry("f32.convert_u/i32", 0xb3, ConvertOp::NoArg, Instr.F32ConvertUI32, Instr.F32ConvertUI32::class)
                opMapEntry("f32.convert_s/i64", 0xb4, ConvertOp::NoArg, Instr.F32ConvertSI64, Instr.F32ConvertSI64::class)
                opMapEntry("f32.convert_u/i64", 0xb5, ConvertOp::NoArg, Instr.F32ConvertUI64, Instr.F32ConvertUI64::class)
                opMapEntry("f32.demote/f64", 0xb6, ConvertOp::NoArg, Instr.F32DemoteF64, Instr.F32DemoteF64::class)
                opMapEntry("f64.convert_s/i32", 0xb7, ConvertOp::NoArg, Instr.F64ConvertSI32, Instr.F64ConvertSI32::class)
                opMapEntry("f64.convert_u/i32", 0xb8, ConvertOp::NoArg, Instr.F64ConvertUI32, Instr.F64ConvertUI32::class)
                opMapEntry("f64.convert_s/i64", 0xb9, ConvertOp::NoArg, Instr.F64ConvertSI64, Instr.F64ConvertSI64::class)
                opMapEntry("f64.convert_u/i64", 0xba, ConvertOp::NoArg, Instr.F64ConvertUI64, Instr.F64ConvertUI64::class)
                opMapEntry("f64.promote/f32", 0xbb, ConvertOp::NoArg, Instr.F64PromoteF32, Instr.F64PromoteF32::class)

                opMapEntry("i32.reinterpret/f32", 0xbc, ReinterpretOp::NoArg, Instr.I32ReinterpretF32, Instr.I32ReinterpretF32::class)
                opMapEntry("i64.reinterpret/f64", 0xbd, ReinterpretOp::NoArg, Instr.I64ReinterpretF64, Instr.I64ReinterpretF64::class)
                opMapEntry("f32.reinterpret/i32", 0xbe, ReinterpretOp::NoArg, Instr.F32ReinterpretI32, Instr.F32ReinterpretI32::class)
                opMapEntry("f64.reinterpret/i64", 0xbf, ReinterpretOp::NoArg, Instr.F64ReinterpretI64, Instr.F64ReinterpretI64::class)
            }
        }
    }
}

// TODO: UG! https://youtrack.jetbrains.com/issue/KT-15952
typealias ControlFlowOpNoArg = Node.InstrOp.ControlFlowOp.NoArg
typealias ControlFlowOpTypeArg = Node.InstrOp.ControlFlowOp.TypeArg
typealias ControlFlowOpDepthArg = Node.InstrOp.ControlFlowOp.DepthArg
typealias ControlFlowOpTableArg = Node.InstrOp.ControlFlowOp.TableArg
typealias CallOpIndexArg = Node.InstrOp.CallOp.IndexArg
typealias CallOpIndexReservedArg = Node.InstrOp.CallOp.IndexReservedArg
typealias MemOpAlignOffsetArg = Node.InstrOp.MemOp.AlignOffsetArg
typealias MemOpReservedArg = Node.InstrOp.MemOp.ReservedArg
typealias ConstOpIntArg = Node.InstrOp.ConstOp.IntArg
typealias ConstOpLongArg = Node.InstrOp.ConstOp.LongArg
typealias ConstOpFloatArg = Node.InstrOp.ConstOp.FloatArg
typealias ConstOpDoubleArg = Node.InstrOp.ConstOp.DoubleArg