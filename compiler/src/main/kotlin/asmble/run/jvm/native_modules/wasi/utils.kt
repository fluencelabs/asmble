package asmble.run.jvm.native_modules.wasi

import asmble.compile.jvm.MemoryBuffer
import java.nio.charset.Charset
import kotlin.ByteArray

fun readUTF8String(mem: MemoryBuffer, offset: Int, length: Int): String {
    // TODO: too mane copying
    var arr = ByteArray(length);
    mem.get(arr, offset, length);
    return String(arr, offset, length, Charset.forName("UTF-8"));
}