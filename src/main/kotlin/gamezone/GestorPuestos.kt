package gamezone


import kotlinx.coroutines.delay

/**
 * Gestor asíncrono de puestos con simulación de sensores mediante Corrutinas (R4, R5, R6)[cite: 1].
 */
class GestorPuestos(val capacidadTotal: Int = 10) {

    val puestos: List<Puesto> = List(capacidadTotal) { id -> Puesto(numero = id + 1) }
    private val historialTickets = mutableListOf<Ticket>()
    private var contadorTickets = 1

    suspend fun registrarEntrada(consola: Consola) {
        try {
            Validador.validarCodigoConsola(consola.codigo)

            val puestoLibre = puestos.firstOrNull { it.estado is EstadoPuesto.Libre }
                ?: throw SistemaLlenoException()

            puestoLibre.estado = EstadoPuesto.EnProceso("Registrando entrada del sensor...")
            println("⏳ [Puesto #${puestoLibre.numero}] Registrando entrada de consola ${consola.codigo}... (Esperando 3s)")
            delay(3000L) // Simula comunicación con el sensor (3 segundos)[cite: 1]

            puestoLibre.estado = EstadoPuesto.EnJuego(consola)
            println("✅ [Puesto #${puestoLibre.numero}] Entrada confirmada para consola ${consola.codigo} (${consola.marcaModelo}).")

        } catch (e: GameZoneException) {
            println("⚠️ [ERROR DE REGISTRO] ${e.message}")
        }
    }

    suspend fun registrarSalida(codigoConsola: String, minutosUso: Int) {
        try {
            val puesto = puestos.firstOrNull {
                val estado = it.estado
                estado is EstadoPuesto.EnJuego && estado.consola.codigo == codigoConsola
            } ?: throw ConsolaNoEncontradaException(codigoConsola)

            val consolaActiva = (puesto.estado as EstadoPuesto.EnJuego).consola

            puesto.estado = EstadoPuesto.EnProceso("Calculando tarifa con sensor...")
            println("\n⏳ [Puesto #${puesto.numero}] Procesando salida de consola $codigoConsola... (Esperando 6.5s)")
            delay(6500L) // Simula procesamiento de salida (6.5 segundos)[cite: 1]

            val ticket = CalculadoraTarifas.calcularTarifa(consolaActiva, minutosUso, contadorTickets++)
            historialTickets.add(ticket)

            puesto.estado = EstadoPuesto.Libre
            println("✅ [Puesto #${puesto.numero}] Salida finalizada. Puesto liberado.")
            imprimirTicket(ticket)

        } catch (e: GameZoneException) {
            println("⚠️ [ERROR DE SALIDA] ${e.message}")
        }
    }

    private fun imprimirTicket(ticket: Ticket) {
        println("==========================================")
        println("          TICKET DE COBRO # ${ticket.numeroTicket}")
        println("==========================================")
        println("Consola:          ${ticket.consola.tipoNombre} (${ticket.consola.codigo})")
        println("Modelo:           ${ticket.consola.marcaModelo}")
        println("Tipo Usuario:     ${ticket.consola.tipoUsuario.descripcion}")
        if (ticket.consola is ConsolaVR) {
            println("Accesorios VR:    ${if (ticket.consola.tieneAccesoriosPremium) "Premium (+30%)" else "Estándar"}")
        }
        println("Tiempo de Uso:    ${ticket.minutosUso} minutos")
        println("------------------------------------------")
        println("Costo Base:       $${String.format("%.2f", ticket.costoBase)}")
        println("IVA (19%):        $${String.format("%.2f", ticket.iva)}")
        println("Dcto Educacional: -$${String.format("%.2f", ticket.descuentoEducacional)}")
        println("------------------------------------------")
        println("TOTAL A PAGAR:    $${String.format("%.2f", ticket.montoFinal)}")
        println("==========================================\n")
    }

    // Consultas de negocio (R4)[cite: 1]
    fun contarPuestosDisponibles(): Int = puestos.count { it.estado is EstadoPuesto.Libre }

    fun obtenerConsolasClientesSocio(): List<Consola> =
        historialTickets.map { it.consola }.filter { it.tipoUsuario == TipoUsuario.SOCIO }

    fun obtenerIngresoPromedio(): Double =
        if (historialTickets.isEmpty()) 0.0 else historialTickets.sumOf { it.montoFinal } / historialTickets.size

    fun obtenerCodigosConsolasFinalizadas(): List<String> =
        historialTickets.map { it.consola.codigo }

    fun obtenerConsolaMasTiempoUso(): Ticket? =
        historialTickets.maxByOrNull { it.minutosUso }

    fun generarReporteCierre() {
        println("\n========================================================")
        println("         REPORTE DE CIERRE DE TURNO - GAMEZONE")
        println("========================================================")
        println("RESUMEN DE CONSOLAS ATENDIDAS:")
        if (historialTickets.isEmpty()) {
            println("No se atendieron consolas durante el turno.")
        } else {
            historialTickets.forEach { t ->
                println(" Ticket #${t.numeroTicket} | Tipo: ${t.consola.tipoNombre} | Código: ${t.consola.codigo} | Tiempo: ${t.minutosUso} min | Cobrado: $${String.format("%.2f", t.montoFinal)}")
            }
        }

        val totalRecaudado = historialTickets.sumOf { it.montoFinal }
        val tipoMasIngresos = historialTickets.groupBy { it.consola.tipoNombre }
            .maxByOrNull { entry -> entry.value.sumOf { it.montoFinal } }?.key ?: "N/A"

        println("--------------------------------------------------------")
        println(" Total Recaudado:                 $${String.format("%.2f", totalRecaudado)}")
        println(" Cantidad de Consolas Atendidas: ${historialTickets.size}")
        println(" Ingreso Promedio por Consola:   $${String.format("%.2f", obtenerIngresoPromedio())}")
        println(" Tipo con Mayor Recaudación:      $tipoMasIngresos")
        println(" Puestos Disponibles al Cierre:   ${contarPuestosDisponibles()}")
        println("========================================================\n")
    }
}