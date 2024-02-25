package org.example.entidadfinancieraquind.Constantes;

public class FinancieraConstantes {
    // Tipos de cuenta
    public static final String CUENTA_CORRIENTE = "Cuenta Corriente";
    public static final String CUENTA_AHORROS = "Cuenta de Ahorros";

    // Mensajes de error comunes
    public static final String ERROR_CLIENTE_ASOCIADO_PRODUCTO = "El cliente asociado al producto no existe.";
    public static final String ERROR_CARACTERES_NOMBRE = "El nombre debe tener al menos 2 caracteres.";
    public static final String ERROR_CARACTERES_APELLIDO = "El apellido debe tener al menos 2 caracteres.";
    public static final String ERROR_FORMATO_EMAIL= "El correo electrónico no tiene un formato válido.";
    public static final String ERROR_AL_CREAR_EL_PRODUCTO = "Error al crear el producto: El cliente asociado no existe.";
    public static final String ERROR_AHORROS_CORRIENTE = "El tipo de producto debe ser 'Cuenta Corriente' o 'Cuenta de Ahorros'.";
    public static final String ERROR_SALDO_AHORRO_CERO= "El saldo de una cuenta de ahorros no puede ser menor de 0.";
    public static final String ERROR_CANCELAR_CUENTA= "No se puede cancelar la cuenta porque el saldo no es igual a $0.";
    public static final String ERROR_TIPO_TRANSACCION_INVALIDA= "Tipo de transacción inválido.";
    public static final String ERROR_CUENTA_DESTINO_NO_EXISTE= "La cuenta de destino no existe en el sistema.";
    public static final String ERROR_CUENTA_ORIGEN_NO_EXISTE= "La cuenta de origen no existe en el sistema.";
    public static final String ERROR_SALDO_INSUFICIENTE_RETIRO= "Saldo insuficiente para realizar el retiro.";
    public static final String ERROR_SALDO_INSUFICIENTE_TRANSFERENCIA= "Saldo insuficiente para realizar la transferncia.";




    public static final String CLIENTE_ELIMINADO = "Cliente eliminado";
    public static final String CLIENTE_MAYOR_EDAD = "El cliente debe ser mayor de 18 años para crear una cuenta.";
    public static final String CLIENTE_NO_ENCONTRADO_CON_ID = "No se encontró el cliente con el ID proporcionado.";
    public static final String PRODUCTO_NO_ENCONTRADO_CON_ID = "No se encontró el producto con el ID especificado.";
    public static final String TRANSACCION_NO_ENCONTRADO_CON_ID = "No se encontró el producto con el ID especificado.";
    public static final String CLIENTE_VINCULO_PRODUCTO = "No se puede eliminar el cliente porque tiene productos vinculados.";

    //Estados
    public static final String CANCELADA = "Cancelada";
    public static final String ACTIVA = "Activa";
    public static final String INACTIVA = "Inactiva";

    //Numeros

    public static final String N_CUENTA_AHORROS = "53";
    public static final String N_CUENTA_CORRIENTE = "33";

    //Transacciones
    public static final String CONSIGNACION = "Consignación";
    public static final String RETIRO = "Retiro";
    public static final String TRANSFERENCIA = "Transferencia";

}
