package asmble.run.jvm.native_modules

/**
 * Used to tack the state of the environment module.
 */
data class EnvState(
        var spentGas: Long = 0,
        // executed instruction counter
        var EIC: Long = 0
)

/**
 * Module used for gas and EIC metering.
 */
open class EnvModule(private val gasLimit: Long) {

    private var state = EnvState();

    /**
     * [Wasm function]
     * Adds spent gas to overall spent gas and checks limit exceeding.
     */
    fun gas(spentGas: Int) {
        if(state.spentGas + spentGas > gasLimit) {
            // TODO : check for overflow, throw an exception
        }

        state.spentGas += spentGas;
    }

    /**
     * [Wasm function]
     * Adds EIC to overall executed instruction counter.
     */
    fun eic(EIC: Int) {
        state.EIC += EIC;
    }

    /**
     * Sets spent gas and EIC value to 0. Used from WasmVm to clear gas value before metering.
     * It should be impossible to call this function from a Wasm module.
     */
    fun clearState() {
        state.spentGas = 0;
        state.EIC = 0;
    }

    /**
     * Returns environment module state.
     * Used from WasmVm to determine spent gas and executed instruction counter after each invocation.
     */
    fun getState(): EnvState {
        return state;
    }

}
