package asmble.compile.jvm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The abstraction that describes work with the memory of the virtual machine.
 */
public abstract class MemoryBuffer {

    /**
     * The default implementation of MemoryBuffer that based on java.nio.DirectByteBuffer
     */
    public static MemoryBuffer init(int capacity) {
        return new MemoryByteBuffer(ByteBuffer.allocateDirect(capacity));
    }

    public abstract int capacity();
    public abstract int limit();
    public abstract MemoryBuffer clear();
    public abstract MemoryBuffer limit(int newLimit);
    public abstract MemoryBuffer position(int newPosition);
    public abstract MemoryBuffer order(ByteOrder order);
    public abstract MemoryBuffer duplicate();
    public abstract MemoryBuffer put(byte[] arr, int offset, int length);
    public abstract MemoryBuffer put(byte[] arr);
    public abstract MemoryBuffer put(int index, byte b);
    public abstract MemoryBuffer putInt(int index, int n);
    public abstract MemoryBuffer putLong(int index, long n);
    public abstract MemoryBuffer putDouble(int index, double n);
    public abstract MemoryBuffer putShort(int index, short n);
    public abstract MemoryBuffer putFloat(int index, float n);
    public abstract byte get(int index);
    public abstract int getInt(int index);
    public abstract long getLong(int index);
    public abstract short getShort(int index);
    public abstract float getFloat(int index);
    public abstract double getDouble(int index);
    public abstract byte get();
    public abstract void get(byte[] arr, int offset, int length);
    public abstract MemoryBuffer get(byte[] arr);
}
