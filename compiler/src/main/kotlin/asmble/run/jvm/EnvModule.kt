package asmble.run.jvm

/**
 * Module used for metering.
 */
open class EnvModule(val gasLimit: Long) {

    private var overallSpentGas: Long = 0

    /**
     * [Wasm function]
     * Adds spent gas to overall spent gas and checks limit exceeding.
     */
    fun gas(spentGas: Int) {
        if(overallSpentGas + spentGas > gasLimit) {
            // TODO : check for overflow, add exception
        }

        overallSpentGas += spentGas;
    }

    /**
     * Sets spent gas value. Used from WasmVm to clear gas value before metering.
     * It should be impossible to call this function from a wasm module.
     */
    fun setSpentGas(value: Long) {
        overallSpentGas = value;
    }

    /**
     * Returns spent gas. Used from WasmVm to determine spent gas after each invocation.
     */
    fun getSpentGas(): Long {
        return overallSpentGas;
    }

}
