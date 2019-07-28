package asmble.run.jvm

/**
 * Module used for gas metering.
 */
open class MeteringModule(val gasLimit: Long) {

    private var overallSpentGas: Long = 0

    /**
     * [Wasm function]
     * Adds spent gas to overall spent gas and checks limit exceeding
     */
    fun gas(spentGas: Int) {
        if(overallSpentGas + spentGas > gasLimit) {
            // TODO : check for overflow, add exception
        }

        overallSpentGas += spentGas;
    }

    fun getSpentGas(): Long {
        return overallSpentGas;
    }

}
