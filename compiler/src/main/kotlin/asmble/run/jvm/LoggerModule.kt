package asmble.run.jvm

import asmble.compile.jvm.Mem
import java.io.PrintWriter
import java.nio.ByteBuffer

/**
 * Module with possibility to write bytes to any 'writer'. This module actually
 * used for logging from the Wasm code outside to 'embedder' (host environment).
 */
open class LoggerModule(pagesOfMemory: Int, val writer: PrintWriter) {

    private val memory =
            ByteBuffer.allocate(pagesOfMemory * Mem.PAGE_SIZE) as ByteBuffer

    /**
     * [Wasm function]
     * Writes one byte to the logger memory buffer. If there is no place to write
     * one byte into the buffer then flush all data from the buffer to [PrintWriter]
     * and after that try to put the byte again.
     */
    fun write(byte: Int) {
        val isFull = memory.position() >= memory.limit()
        if (isFull) {
            flush()
        }
        memory.put(byte.toByte())
    }


    /**
     * [Wasm function]
     * Reads all bytes from the logger memory buffer, convert its to UTF-8
     * string and writes to stdout.
     * Cleans the logger memory buffer.
     */
    fun flush() {
        val message = String(memory.array(), 0, memory.position())
        writer.print(message)
        writer.flush()
        memory.clear()
    }

}
