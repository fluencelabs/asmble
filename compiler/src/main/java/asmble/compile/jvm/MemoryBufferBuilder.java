package asmble.compile.jvm;

/**
 * Interface to initialize MemoryBuffer
 */
public interface MemoryBufferBuilder {
    MemoryBuffer build(int capacity);
}
