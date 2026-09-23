package gamezone

/**
 * Validador de formato Regex para códigos de consola (R3)[cite: 1].
 */
object Validador {
    private val REGEX_CODIGO_CONSOLA = Regex("^[A-Z]{2}[0-9]{2}[A-Z]{2}$")

    fun validarCodigoConsola(codigo: String) {
        if (!REGEX_CODIGO_CONSOLA.matches(codigo)) {
            throw CodigoInvalidoException(codigo)
        }
    }
}