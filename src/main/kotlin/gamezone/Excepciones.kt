package gamezone


/**
 * Excepciones del dominio GameZone para manejo seguro de errores (R6)[cite: 1].
 */
sealed class GameZoneException(message: String) : Exception(message)

class CodigoInvalidoException(codigo: String) :
    GameZoneException("El código de consola '$codigo' no cumple con el formato válido (Ejemplo: CC12CD).")

class SistemaLlenoException :
    GameZoneException("No hay puestos libres disponibles en el sistema GameZone (capacidad máxima alcanzada).")

class ConsolaNoEncontradaException(codigo: String) :
    GameZoneException("No se encontró ninguna consola activa en los puestos con el código '$codigo'.")

class TarifaInvalidaException(monto: Double) :
    GameZoneException("El cálculo arrojó un monto de tarifa inválido ($$monto).")