package asmble.run.jvm.native_modules.wasi

import asmble.ast.Script
import asmble.compile.jvm.MemoryBuffer
import asmble.run.jvm.ScriptAssertionError
import java.io.File
import java.io.PrintWriter

/**
 * Module used for support WASI, contains the realization of several WASI imports
 * for operating with file system. Should be registered as 'wasi_unstable'.
 */
open class WASIModule(private val mem: MemoryBuffer, private val preopenedDirNames: Array<String>) {
/*
 + (import "wasi_unstable" "fd_prestat_get" (func $__wasi_fd_prestat_get (type 2)))
 + (import "wasi_unstable" "fd_prestat_dir_name" (func $__wasi_fd_prestat_dir_name (type 1)))
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

    private val writer = PrintWriter(System.out, true);

    // this error code will be returned by all non-implemented methods
    private val wasiErrorCode: Int = __WASI_EACCES

    private val stdin: Int = 0
    private val stdout: Int = 1
    private val stderr: Int = 2

    private var nextPreopenedDirId = 0;
    private val preopenedDirs = HashMap<Int, String>()

    // mapping between file descriptors and opened file
    private val openFiles = HashMap<Int, File>()

    /**
     * [Wasm function]
     * Gets metadata about a preopened file descriptor.
     *
     * @param fd the preopened file descriptor to query
     * @param buf_ptr where the metadata will be written
     * @return WASI code of result
     */
    fun fd_prestat_get(
            fd: Int,
            buf_ptr: Int
    ): Int {
        writer.println("fd_prestat_get: $fd, $buf_ptr, returning __WASI_EBADF");
        writer.flush();

        if (nextPreopenedDirId >= preopenedDirNames.size) {
            return __WASI_EBADF;
        }

        // dir type
        mem.put(buf_ptr, 0);

        // dir length
        mem.putInt(buf_ptr + 1, preopenedDirNames[nextPreopenedDirId].length);
        preopenedDirs[fd] = preopenedDirNames[nextPreopenedDirId];

        ++nextPreopenedDirId;

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Gets state of preopened file descriptors.
     *
     * @param fd the preopened file descriptor to query
     * @param path where the metadata will be written
     * @param path_len the length of metadata
     * @return WASI code of result
     */
    fun fd_prestat_dir_name(
            fd: Int,
            path: Int,
            path_len: Int
    ): Int {
        writer.println("fd_prestat_dir_name: $fd, $path, $path_len");
        writer.flush();

        val memView = mem.duplicate();
        memView.position(path);

        if(preopenedDirs.containsKey(fd) ) {
            writer.println("fd_prestat_dir_name: return string ${preopenedDirs[fd]}");
            writer.flush();
            memView.put(preopenedDirs[fd]!!.toByteArray(), 0, path_len);
        } else {
            // TODO: handle error
            memView.put(path, 0);
        }

        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Gets metadata of a file descriptor.
     *
     * @param fd the file descriptor whose metadata will be accessed
     * @param buf the location where the metadata will be written
     * @return WASI code of result
     */
    fun fd_fdstat_get(
            fd: Int,
            path: Int
    ): Int {
        writer.println("fd_fdstat_get: $fd, $path");
        writer.flush();

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Opens file located at the given path.
     *
     * @param dirfd the descriptor corresponding to the directory that the file is in
     * @param dirflags flags specifying how the path will be resolved
     * @param path_offset the path of the file or directory to be open
     * @param path_len the lenght of the path string
     * @param o_flags how the file will be open
     * @param fs_rights_base the rights of the created file descriptor
     * @param fs_rightsinheriting the rights of file descriptors derived from the created file descriptor
     * @param fs_flags the flags of the file descriptor
     * @param fd the new file descriptor
     * @return WASI code of result
     */
    fun path_open(
            dirfd: __wasi_fd_t,
            dirflags: __wasi_lookupflags_t,
            path_offset: Int,
            path_len: Int,
            o_flags: __wasi_oflags_t,
            fs_rights_base: __wasi_rights_t,
            fs_rightsinheriting: __wasi_rights_t,
            fs_flags: __wasi_fdflags_t,
            fd: __wasi_fd_t
    ): Int {
        //(type (;10;) (func (param i32 i32 i32 i32 i32 i64 i64 i32 i32) (result i32)))
        writer.println("path_open: $dirfd, $dirflags, $path_offset, $path_len, $o_flags, $fs_rights_base, $fs_rightsinheriting, $fs_flags, $fd");
        writer.flush();

        if( (dirflags and __WASI_LOOKUP_SYMLINK_FOLLOW) != 0) {
            // TODO
            return __WASI_EACCES;
        }


        val path = readUTF8String(mem, path_offset, path_len);
        writer.println("path_open: $path");
        writer.flush();

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Reads data from file descriptor.
     *
     * @param fd a file descriptor from which data will be read
     * @param iovs vectors where data will be stored
     * @param iovs_len the length of data in `iovs`
     * @param nread number of bytes read
     */
    fun fd_read(
            fd: __wasi_fd_t,
            iovs: Int,
            iovs_len: Int,
            nread: Int
    ): Int {

        writer.println("fd_read: $fd, $iovs, $iovs_len, $nread");
        writer.flush();

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Writes data to the file descriptor.
     *
     * @param fd a file descriptor (opened with writing) to write to
     * @param iovs a list of vectors to read data from
     * @param iovs_len the length of data in `iovs`
     * @param nwritten the number of bytes written
     */
    fun fd_write(
            fd: __wasi_fd_t,
            iovs: Int,
            iovs_len: Int,
            nwritten: Int
    ): Int {

        writer.println("fd_write: $fd, $iovs, $iovs_len, $nwritten");
        writer.flush();

        val data_ptr = mem.getInt(iovs);
        val data_len = mem.getInt(iovs + 4);

        val str = readUTF8String(mem, data_ptr, data_len);

        writer.println("fd_write: $str");
        writer.flush();

        mem.putInt(nwritten, data_len);
        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Close an open file descriptor
     *
     * @param fd a file descriptor mapping to an open file to close
     */
    fun fd_close(
            fd: __wasi_fd_t
    ): Int {
        writer.println("fd_close: $fd");
        writer.flush();

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Returns a command-line arguments size.
     *
     * @param argc the number of arguments
     * @param argv_buf_size the size of the argument string data
     */
    fun args_sizes_get(
            argc: Int,
            argv_buf_size: Int
    ): Int {
        writer.println("args_sizes_get: $argc, $argv_buf_size");
        writer.flush();

        mem.putInt(argc, 0);
        mem.putInt(argv_buf_size, 0);

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     *
     * @param argv a pointer to a buffer to write the argument pointers
     * @param argv_buf a pointer to a buffer to write the argument string data
     */
    fun args_get(
            argv: Int,
            argv_buf: Int
    ): Int {
        writer.println("args_get: $argv, $argv_buf");
        writer.flush();

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Returns a command-line arguments size.
     *
     * @param environ_count the number of environment variables
     * @param environ_buf_size the size of the environment variable string data
     */
    fun environ_sizes_get(
            environ_count: Int,
            environ_buf_size: Int
    ): Int {
        writer.println("environ_sizes_get: $environ_count, $environ_buf_size");
        writer.flush();

        mem.putInt(environ_count, 0);
        mem.putInt(environ_buf_size, 0);

        return __WASI_ESUCCESS;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     */
    fun environ_get(
            environ: Int,
            environ_buf: Int
    ): Int {
        writer.println("environ_get: $environ, $environ_buf");
        writer.flush();

        return wasiErrorCode;
    }

    /**
     * [Wasm function]
     * Reads a command-line arguments.
     *
     * @param code the exit code
     */
    fun proc_exit(code: Int){
        writer.println("proc_exit: $code");
        writer.flush();

        throw ScriptAssertionError(Script.Cmd.Assertion.Trap(Script.Cmd.Action.Get("", ""), "trap"), "proc_exit")
    }

}
