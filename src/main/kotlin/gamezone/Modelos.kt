package gamezone

/**
 * Modelos de dominio y jerarquía de consolas (R1, R2, R3)[cite: 1].
 */
enum class TipoUsuario(val descripcion: String) {
    INFANTIL("Infantil"),
    SOCIO("Socio (20% dcto en tiempo)"),
    EDUCACIONAL("Educacional (50% dcto sobre monto con IVA)")
}

sealed class Consola(
    val codigo: String,
    val marcaModelo: String,
    val fechaIngreso: String,
    val tipoUsuario: TipoUsuario
) {
    abstract val tipoNombre: String
    abstract val tarifaBaseHora: Double
    abstract fun calcularCostoBase(minutosUso: Int): Double
}

class ConsolaClasica(
    codigo: String,
    marcaModelo: String,
    fechaIngreso: String,
    tipoUsuario: TipoUsuario
) : Consola(codigo, marcaModelo, fechaIngreso, tipoUsuario) {

    override val tipoNombre: String = "ConsolaClasica"
    override val tarifaBaseHora: Double = 800.0

    override fun calcularCostoBase(minutosUso: Int): Double {
        val horas = minutosUso / 60.0
        var costo = horas * tarifaBaseHora
        if (tipoUsuario == TipoUsuario.SOCIO) {
            costo *= 0.80 // 20% descuento en tiempo para socios[cite: 1]
        }
        return costo
    }
}

class ConsolaModerna(
    codigo: String,
    marcaModelo: String,
    fechaIngreso: String,
    tipoUsuario: TipoUsuario
) : Consola(codigo, marcaModelo, fechaIngreso, tipoUsuario) {

    override val tipoNombre: String = "ConsolaModerna"
    override val tarifaBaseHora: Double = 1500.0

    override fun calcularCostoBase(minutosUso: Int): Double {
        if (minutosUso < 20) {
            return 0.0 // Gratuito si es menor a 20 minutos[cite: 1]
        }
        val horas = minutosUso / 60.0
        return horas * tarifaBaseHora
    }
}

class ConsolaVR(
    codigo: String,
    marcaModelo: String,
    fechaIngreso: String,
    tipoUsuario: TipoUsuario,
    val tieneAccesoriosPremium: Boolean
) : Consola(codigo, marcaModelo, fechaIngreso, tipoUsuario) {

    override val tipoNombre: String = "ConsolaVR"
    override val tarifaBaseHora: Double = 3000.0

    override fun calcularCostoBase(minutosUso: Int): Double {
        val horas = minutosUso / 60.0
        var costo = horas * tarifaBaseHora
        if (tieneAccesoriosPremium) {
            costo *= 1.30 // 30% recargo accesorios premium[cite: 1]
        }
        return costo
    }
}

sealed class EstadoPuesto {
    object Libre : EstadoPuesto() {
        override fun toString(): String = "Libre"
    }
    data class EnJuego(val consola: Consola) : EstadoPuesto() {
        override fun toString(): String = "EnJuego (${consola.codigo})"
    }
    data class EnProceso(val motivo: String) : EstadoPuesto() {
        override fun toString(): String = "EnProceso ($motivo)"
    }
    data class EnReparacion(val motivo: String) : EstadoPuesto() {
        override fun toString(): String = "EnReparación ($motivo)"
    }
}

data class Puesto(
    val numero: Int,
    var estado: EstadoPuesto = EstadoPuesto.Libre
)

data class Ticket(
    val numeroTicket: Int,
    val consola: Consola,
    val minutosUso: Int,
    val costoBase: Double,
    val iva: Double,
    val descuentoEducacional: Double,
    val montoFinal: Double
)