package ec.compumax.pedidos.Otros;

public class UtilBD {

    //nombre base datos
    public static final String NOMBRE_BD = "pedidosPlusBD";

    //tabla usuarios
    public static final String TABLA_USERS = "usuarios";
    public static final String CAMPO_LOGIN = "login";
    public static final String CAMPO_PSWD = "pswd";
    public static final String CAMPO_NAME = "name";
    public static final String CAMPO_RUC = "ruc";
    public static final String CAMPO_RAZONSOCIAL = "razonsocial";
    public static final String CAMPO_DIRECCION = "direccion";
    public static final String CAMPO_REFERENCIA = "referencia";
    public static final String CAMPO_CREDITO = "credito";

    public static final String CREAR_TABLA_USUARIOS="CREATE TABLE IF NOT EXISTS "+TABLA_USERS+"("+CAMPO_LOGIN+" TEXT PRIMARY KEY, " +
            ""+CAMPO_PSWD+" TEXT, "+CAMPO_NAME+" TEXT, "+CAMPO_RUC+" TEXT, "+CAMPO_RAZONSOCIAL+" TEXT, "+CAMPO_DIRECCION+" TEXT, "+CAMPO_REFERENCIA+" TEXT, "+CAMPO_CREDITO+" INTEGER)";

    //tabla pedidos
    public static final String TABLA_PEDIDOS = "pedidos";
    public static final String CAMPO_IDPEDIDO = "idpedido";
    //public static final String CAMPO_FECHA = "fecha";
    public static final String CAMPO_IDLOCAL = "idlocal";
    public static final String CAMPO_TSIVA = "tsiva";
    public static final String CAMPO_TIVA = "tiva";
    //public static final String CAMPO_IVA = "iva";
    public static final String CAMPO_TOTAL = "total";
    public static final String CAMPO_FINALIZADO = "finalizado";
    public static final String CAMPO_BORRADO = "borrado";

    public static final String CREAR_TABLA_PEDIDOS="CREATE TABLE IF NOT EXISTS "+TABLA_PEDIDOS+"("+CAMPO_IDPEDIDO+" INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ""+CAMPO_IDLOCAL+" TEXT, "+CAMPO_TSIVA+" DECIMAL(10,2), "+CAMPO_TIVA+" DECIMAL(10,2), "+CAMPO_TOTAL+" DECIMAL(10,2), "+CAMPO_LOGIN+" TEXT, "+CAMPO_FINALIZADO+" INTEGER, "+CAMPO_BORRADO+" INTEGER)";

    //TABLA MPEDIDOS

    public static final String TABLA_MPEDIDOS = "mpedidos";
    public static final String CAMPO_IDMPEDIDO = "idmpedido";
    public static final String CAMPO_IDPRODUCTO = "idproducto";
    public static final String CAMPO_CANTIDAD = "cantidad";
    public static final String CAMPO_PRECIO = "precio";
    public static final String CAMPO_IVA = "iva";
    public static final String CAMPO_PTOTAL = "ptotal";
    public static final String CAMPO_OBSERVACION = "observacion";
    public static final String CAMPO_NPRODUCTO = "nproducto";
    public static final String CAMPO_DETALLE = "detalle";

    public static final String CREAR_TABLA_MPEDIDOS="CREATE TABLE IF NOT EXISTS "+TABLA_MPEDIDOS+"("+CAMPO_IDMPEDIDO+" INTEGER PRIMARY KEY AUTOINCREMENT, "+CAMPO_IDPEDIDO+" INTEGER," +
            ""+CAMPO_IDPRODUCTO+" TEXT, "+CAMPO_CANTIDAD+" INTEGER, "+CAMPO_PRECIO+" DECIMAL(10,2), "+CAMPO_IVA+" INTEGER, "+CAMPO_PTOTAL+" DECIMAL(10,2), "+CAMPO_NPRODUCTO+" TEXT, "+CAMPO_OBSERVACION+" TEXT, "+CAMPO_DETALLE+" TEXT)";

}
