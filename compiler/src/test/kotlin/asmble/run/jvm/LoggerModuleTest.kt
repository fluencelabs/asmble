package asmble.run.jvm

import asmble.TestBase
import org.junit.Test
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.test.assertEquals

class LoggerModuleTest : TestBase() {

    @Test
    fun writeAndFlushTest() {
        val stream = StringWriter()
        val logger = LoggerModule(1, PrintWriter(stream))

        logger.flush() // checks that no raise error

        val testString = "test String for log to stdout"
        for (byte: Byte in testString.toByteArray()) {
            logger.write(byte.toInt())
        }

        logger.flush()
        val loggedString = stream.toString()
        assertEquals(testString, loggedString)
    }

    @Test
    fun writeAndFlushMoreThanLoggerBufferTest() {
        val stream = StringWriter()
        // logger buffer has 65Kb size
        val logger = LoggerModule(1, PrintWriter(stream))

        val testString = longString(65_000 * 2) // twice as much as logger buffer
        for (byte: Byte in testString.toByteArray()) {
            logger.write(byte.toInt())
        }

        logger.flush()
        val loggedString = stream.toString()
        assertEquals(testString, loggedString)
    }

    private fun longString(size: Int): String {
        val stringBuffer = StringBuffer()
        for (idx: Int in (1 until size)) {
            stringBuffer.append((idx % Byte.MAX_VALUE).toChar())
        }
        return stringBuffer.toString()
    }
}
