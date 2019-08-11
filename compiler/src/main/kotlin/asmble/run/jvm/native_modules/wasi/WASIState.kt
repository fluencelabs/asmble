package asmble.run.jvm.native_modules.wasi

import java.io.File

/**
 * Module used for support WASI, contains the realization of several WASI imports
 * for operating with file system. Should be registered as 'wasi_unstable'.
 */
data class WASIState (
    // mapping between file descriptors and opened file
    val openFiles: HashMap<Int, File> = HashMap<Int, File>()
)
