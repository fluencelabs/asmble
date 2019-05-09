package asmble.compile.jvm;

import java.nio.ByteBuffer;

public class MemoryInit {
    public static MemoryBuffer init(int capacity) {
        return new MemoryByteBuffer(ByteBuffer.allocateDirect(capacity));
    }
}
