package gamezone

/**
 * Calculadora de orden secuencial estricto para cobros (R3)[cite: 1].
 */
object CalculadoraTarifas {

    fun calcularTarifa(consola: Consola, minutosUso: Int, numeroTicket: Int): Ticket {
        // 1. Costo base por tiempo de uso[cite: 1]
        val costoBase = consola.calcularCostoBase(minutosUso)

        // 2. IVA del 19%[cite: 1]
        val iva = costoBase * 0.19
        val montoConIva = costoBase + iva

        // 3. Descuento Educacional del 50% sobre el monto con IVA[cite: 1]
        val descuentoEducacional = if (consola.tipoUsuario == TipoUsuario.EDUCACIONAL) {
            montoConIva * 0.50
        } else {
            0.0
        }

        val montoFinal = montoConIva - descuentoEducacional

        // Excepción válida: ConsolaModerna con menos de 20 min[cite: 1]
        val esConsolaModernaGratis = consola is ConsolaModerna && minutosUso < 20

        if (montoFinal < 0 || (montoFinal == 0.0 && !esConsolaModernaGratis)) {
            throw TarifaInvalidaException(montoFinal)
        }

        return Ticket(
            numeroTicket = numeroTicket,
            consola = consola,
            minutosUso = minutosUso,
            costoBase = costoBase,
            iva = iva,
            descuentoEducacional = descuentoEducacional,
            montoFinal = montoFinal
        )
    }
}