package asmble.run.jvm.native_modules.wasi

import asmble.ast.Script
import asmble.compile.jvm.MemoryBuffer
import asmble.run.jvm.ScriptAssertionError
import java.io.File

/**
 * Module used for support WASI, contains the realization of several WASI imports
 * for operating with file system. Should be registered as 'wasi_unstable'.
 */
open class WASIModule(private val mem: MemoryBuffer) {
/*
  (import "wasi_unstable" "fd_prestat_get" (func $__wasi_fd_prestat_get (type 2)))
  (import "wasi_unstable" "fd_prestat_dir_name" (func $__wasi_fd_prestat_dir_name (type 1)))
 + (import "wasi_unstable" "environ_sizes_get" (func $__wasi_environ_sizes_get (type 2)))
 + (import "wasi_unstable" "environ_get" (func $__wasi_environ_get (type 2)))
 + (import "wasi_unstable" "args_sizes_get" (func $__wasi_args_sizes_get (type 2)))
 + (import "wasi_unstable" "args_get" (func $__wasi_args_get (type 2)))
  (import "wasi_unstable" "fd_close" (func $fd_close (type 3)))
  (import "wasi_unstable" "fd_write" (func $fd_write (type 9)))
  (import "wasi_unstable" "fd_read" (func $fd_read (type 9)))
  (import "wasi_unstable" "path_open" (func $path_open (type 10)))
 + (import "wasi_unstable" "proc_exit" (func $__wasi_proc_exit (type 0)))
  (import "wasi_unstable" "fd_fdstat_get" (func $__wasi_fd_fdstat_get (type 2)))
 */

    // this error code will be returned by all non-implemented methods
    private val wasiErrorCode: Int = __WASI_EACCES

    private val stdin: Int = 0
    private val stdout: Int = 1
    private val stderr: Int = 2

    // mapping between file descriptors and opened file
    private val openFiles = HashMap<Int, File>()

    /**
     * [Wasm function]
     * Returns a command-line arguments size.
     */
    fun args_sizes_get(_param1: Int, _param2: Int): Int {
        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     */
    fun args_get(_param1: Int, _param2: Int): Int {
        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Returns a command-line arguments size.
     */
    fun environ_sizes_get(_param1: Int, _param2: Int): Int {
        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     */
    fun environ_get(_param1: Int, _param2: Int): Int {
        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     */
    fun proc_exit(_param1: Int){
        throw ScriptAssertionError(Script.Cmd.Assertion.Trap(Script.Cmd.Action.Get("", ""), "trap"), "proc_exit")
    }

}
