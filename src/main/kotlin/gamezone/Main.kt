package gamezone

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    println("========================================================")
    println(" INICIALIZANDO SISTEMA GAMEZONE (10 PUESTOS DISPONIBLES)")
    println("========================================================\n")

    val gestor = GestorPuestos(capacidadTotal = 10)

    // 1. Entradas con datos de prueba sugeridos[cite: 1]
    println("--- [FASE 1] REGISTRO DE ENTRADAS DE CONSOLAS ---")

    val c1 = ConsolaClasica("CC12CD", "PlayStation 5", "2026-09-08 10:00", TipoUsuario.SOCIO)
    val c2 = ConsolaClasica("CC99ZA", "Xbox Series X", "2026-09-08 10:05", TipoUsuario.INFANTIL)
    val c3 = ConsolaModerna("CM22TO", "Nintendo Switch", "2026-09-08 10:10", TipoUsuario.INFANTIL)
    val c4 = ConsolaVR("VR44RG", "Meta Quest 3", "2026-09-08 10:15", TipoUsuario.EDUCACIONAL, tieneAccesoriosPremium = true)
    val c5 = ConsolaVR("VR77RG", "HTC Vive Pro", "2026-09-08 10:20", TipoUsuario.INFANTIL, tieneAccesoriosPremium = false)

    gestor.registrarEntrada(c1)
    gestor.registrarEntrada(c2)
    gestor.registrarEntrada(c3)
    gestor.registrarEntrada(c4)
    gestor.registrarEntrada(c5)

    // Prueba de error de código inválido (R6)[cite: 1]
    println("\n--- [PRUEBA DE ERROR] Intentando registrar código inválido '123ABC' ---")
    val cError = ConsolaClasica("123ABC", "PlayStation 4", "2026-09-08 10:25", TipoUsuario.INFANTIL)
    gestor.registrarEntrada(cError)

    // 2. Salidas y cálculo de cobros[cite: 1]
    println("\n--- [FASE 2] REGISTRO DE SALIDAS Y CÁLCULO DE TARIFAS ---")

    gestor.registrarSalida("CC12CD", 75)
    gestor.registrarSalida("CC99ZA", 180)
    gestor.registrarSalida("CM22TO", 18)
    gestor.registrarSalida("VR44RG", 120)
    gestor.registrarSalida("VR77RG", 45)

    // Prueba de error de consola inexistente (R6)[cite: 1]
    println("--- [PRUEBA DE ERROR] Intentando salida de consola no registrada 'XX00XX' ---")
    gestor.registrarSalida("XX00XX", 60)

    // 3. Consultas de negocio (R4)[cite: 1]
    println("--- [FASE 3] CONSULTAS DE NEGOCIO ---")
    println("• Puestos disponibles actualmente: ${gestor.contarPuestosDisponibles()}")
    println("• Consolas atendidas de clientes Socio: ${gestor.obtenerConsolasClientesSocio().map { it.codigo }}")
    println("• Ingreso promedio por consola atendida: $${String.format("%.2f", gestor.obtenerIngresoPromedio())}")
    println("• Códigos de consolas finalizadas: ${gestor.obtenerCodigosConsolasFinalizadas()}")

    val masTiempo = gestor.obtenerConsolaMasTiempoUso()
    if (masTiempo != null) {
        println("• Consola con mayor tiempo de uso: ${masTiempo.consola.codigo} (${masTiempo.minutosUso} minutos)")
    }

    // 4. Reporte de cierre de turno (R4)[cite: 1]
    gestor.generarReporteCierre()
}