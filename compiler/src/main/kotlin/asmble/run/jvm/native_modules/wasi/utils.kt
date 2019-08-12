package asmble.run.jvm.native_modules.wasi

import asmble.compile.jvm.MemoryBuffer
import java.io.PrintWriter
import java.nio.charset.Charset
import kotlin.ByteArray

fun readUTF8String(mem: MemoryBuffer, offset: Int, length: Int): String {
    // TODO: too many copying
    val duplicatedMem = mem.duplicate();
    duplicatedMem.position(offset);

    val arr = ByteArray(length);
    duplicatedMem.get(arr, 0, length);

    return String(arr, 0, length, Charset.forName("UTF-8"));
}