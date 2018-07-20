package asmble.cli

import asmble.ast.Script
import asmble.compile.jvm.javaIdent
import asmble.run.jvm.Module
import asmble.run.jvm.ScriptContext
import java.io.File
import java.util.*

abstract class ScriptCommand<T> : Command<T>() {

    fun scriptArgs(bld: Command.ArgsBuilder) = ScriptArgs(
        inFiles = bld.args(
            name = "inFile",
            opt = "in",
            desc = "Files to add to classpath. Can be wasm, wast, or class file. " +
                "Named wasm/wast modules here are automatically registered unless -noreg is set.",
            default = emptyList()
        ),
        registrations = bld.args(
            name = "registration",
            opt = "reg",
            desc = "Register class name to a module name. Format: modulename=classname.",
            default = emptyList()
        ).map {
            it.split('=').also { require(it.size == 2) { "Invalid modulename=classname pair" } }.let { it[0] to it[1] }
        },
        disableAutoRegister = bld.flag(
            opt = "noreg",
            desc = "If set, this will not auto-register modules with names.",
            lowPriority = true
        ),
        specTestRegister = bld.flag(
            opt = "testharness",
            desc = "If set, registers the spec test harness as 'spectest'.",
            lowPriority = true
        ),
        defaultMaxMemPages = bld.arg(
            name = "defaultMaxMemPages",
            opt = "defmaxmempages",
            desc = "The maximum number of memory pages when a module doesn't say.",
            default = "5",
            lowPriority = true
        ).toInt()
    )

    fun prepareContext(args: ScriptArgs): ScriptContext {
        var context = ScriptContext(
            packageName = "asmble.temp" + UUID.randomUUID().toString().replace("-", ""),
            defaultMaxMemPages = args.defaultMaxMemPages
        )
        // Compile everything
        context = args.inFiles.foldIndexed(context) { index, ctx, inFile ->
            try {
                when (inFile.substringAfterLast('.')) {
                    // if input file is class file
                    "class" -> ctx.classLoader.addClass(File(inFile).readBytes()).let { ctx }
                    // if input file is wasm file
                    else -> Translate.inToAst(inFile, inFile.substringAfterLast('.')).let { inAst ->
                        val (mod, name) = (inAst.commands.singleOrNull() as? Script.Cmd.Module) ?:
                            error("Input file must only contain a single module")
                        val className = name?.javaIdent?.capitalize() ?:
                            "Temp" + UUID.randomUUID().toString().replace("-", "")
                        ctx.withCompiledModule(mod, className, name).let { ctx ->
                            if (name == null && index != args.inFiles.size - 1)
                                logger.warn { "File '$inFile' not last and has no name so will be unused" }
                            if (name == null || args.disableAutoRegister) ctx
                            else ctx.runCommand(Script.Cmd.Register(name, null))
                        }
                    }
                }
            } catch (e: Exception) { throw Exception("Failed loading $inFile - ${e.message}", e) }
        }
        // Do registrations
        context = args.registrations.fold(context) { ctx, (moduleName, className) ->
            ctx.withModuleRegistered(moduleName,
                Module.Native(Class.forName(className, true, ctx.classLoader).newInstance()))
        }
        if (args.specTestRegister) context = context.withHarnessRegistered()  // проверить что не так с "Cannot find compatible import for spectest::print"
        return context
    }

    /**
     * Common arguments for 'invoke' and 'run' ScriptCommands.
     *
     * @param inFiles Files to add to classpath. Can be wasm, wast, or class file
     * @param registrations Register class name to a module name
     * @param disableAutoRegister If set, this will not auto-register modules with names
     * @param specTestRegister If true, registers the spec test harness as 'spectest'
     * @param defaultMaxMemPages The maximum number of memory pages when a module doesn't say
     */
    data class ScriptArgs(
        val inFiles: List<String>,
        val registrations: List<Pair<String, String>>,
        val disableAutoRegister: Boolean,
        val specTestRegister: Boolean,
        val defaultMaxMemPages: Int
    )
}