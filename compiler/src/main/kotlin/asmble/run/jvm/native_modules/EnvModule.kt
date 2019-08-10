package asmble.run.jvm.native_modules

/**
 * Module used for gas and EIC metering.
 */
open class EnvModule(private val gasLimit: Long) {

    private var overallSpentGas: Long = 0

    // executed instruction counter
    private var EIC: Long = 0

    /**
     * [Wasm function]
     * Adds spent gas to overall spent gas and checks limit exceeding.
     */
    fun gas(spentGas: Int) {
        if(overallSpentGas + spentGas > gasLimit) {
            // TODO : check for overflow, throw an exception
        }

        overallSpentGas += spentGas;
    }

    /**
     * [Wasm function]
     * Adds EIC to overall executed instruction counter.
     */
    fun eic(currentEIC: Int) {
        EIC += currentEIC;
    }

    /**
     * Sets spent gas and EIC value to 0. Used from WasmVm to clear gas value before metering.
     * It should be impossible to call this function from a Wasm module.
     */
    fun clearState() {
        overallSpentGas = 0;
        EIC = 0;
    }

    /**
     * Returns spent gas. Used from WasmVm to determine spent gas after each invocation.
     */
    fun getSpentGas(): Long {
        return overallSpentGas;
    }

    /**
     * Returns current EIC. Used from WasmVm to determine count of executed instruction after each invocation.
     */
    fun getEIC(): Long {
        return EIC;
    }
}
