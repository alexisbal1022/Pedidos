package ec.compumax.pedidos.Otros;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DBHelperPedidos extends SQLiteOpenHelper {

    public DBHelperPedidos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL(UtilBD.CREAR_TABLA_USUARIOS);
        db.execSQL(UtilBD.CREAR_TABLA_MPEDIDOS);
        db.execSQL(UtilBD.CREAR_TABLA_PEDIDOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS "+UtilBD.TABLA_USERS);
        db.execSQL("DROP TABLE IF EXISTS "+UtilBD.TABLA_MPEDIDOS);
        db.execSQL("DROP TABLE IF EXISTS "+UtilBD.TABLA_PEDIDOS);

    }

    public boolean insertUsers(String login, String pswd, String name, String ruc, String razon, String direccion, String referencia, int credito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(UtilBD.CAMPO_LOGIN, login);
        contentValues.put(UtilBD.CAMPO_PSWD, pswd);
        contentValues.put(UtilBD.CAMPO_NAME, name);
        contentValues.put(UtilBD.CAMPO_RUC, ruc);
        contentValues.put(UtilBD.CAMPO_RAZONSOCIAL, razon);
        contentValues.put(UtilBD.CAMPO_DIRECCION, direccion);
        contentValues.put(UtilBD.CAMPO_REFERENCIA, referencia);
        contentValues.put(UtilBD.CAMPO_CREDITO, credito);
        db.insert(UtilBD.TABLA_USERS, null, contentValues);
        return true;
    }

    public boolean verificaUsuario(String login){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from usuarios where login='"+login+"'", null );
        res.moveToFirst();
        if(res.getCount()!=0){
            Log.e("usuario", "si existen datos");
            return true;
        }else {
            Log.e("usuario", "no existen datos");
            return false;
        }
    }

    public String insertaPedido(String login, String idlocal) {

        String idpedido, cedula, nombre, dir, telf, tiden;
        Cursor res, resCliente, resPedido;
        ContentValues contentValues = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();


        res =  db.rawQuery( "select idpedido, finalizado from pedidos where finalizado='0' and login='"+login+"' and idlocal='"+idlocal+"' order by idpedido desc", null );
        res.moveToFirst();

        if(res.getCount()!=0){

            Log.e("consegreso","val: "+res.getString(res.getColumnIndex(UtilBD.CAMPO_IDPEDIDO)));
            Log.e("consestadao","val: "+res.getString(res.getColumnIndex(UtilBD.CAMPO_FINALIZADO)));

            idpedido=res.getString(res.getColumnIndex(UtilBD.CAMPO_IDPEDIDO));

            //contentValues.put(UtilBD.CAMPO_IDLOCAL, idlocal);
            //db.update(UtilBD.TABLA_PEDIDOS,contentValues,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{idpedido});

            return idpedido;
        }else {

            contentValues.put(UtilBD.CAMPO_IDLOCAL, idlocal);
            contentValues.put(UtilBD.CAMPO_FINALIZADO, 0);
            contentValues.put(UtilBD.CAMPO_BORRADO, 0);
            contentValues.put(UtilBD.CAMPO_LOGIN, login);

            db.insert(UtilBD.TABLA_PEDIDOS,null, contentValues);

            resPedido = db.rawQuery( "select MAX(idpedido) as idpedido from pedidos", null );
            resPedido.moveToFirst();
            idpedido = resPedido.getString(resPedido.getColumnIndex(UtilBD.CAMPO_IDPEDIDO));

            return idpedido;
        }

    }

    public boolean insertaMPedido(String pedido, String producto, int cantidad, Double precio, int iva, String observacion, String nproducto, String detalle) {
        Cursor res, resSiva, resIva;
        SQLiteDatabase db = this.getWritableDatabase();
        String idmpedido;
        ContentValues datosMPedido = new ContentValues();
        ContentValues datosSiniva = new ContentValues();
        ContentValues datosTotal = new ContentValues();
        Double siniva, civa, totalpedido;

        res =  db.rawQuery( "select idmpedido, cantidad, ptotal from mpedidos where idpedido='"+pedido+"' and idproducto='"+producto+"'", null );
        res.moveToFirst();
        if(res.getCount()!=0){

            Double ttot=0.00, ncant=0.00, ntot=0.00;
            int tcant=0;
            //ncant = Double.parseDouble(cant);
            //ntot = Double.parseDouble(tot);
            ntot = cantidad*precio;

            idmpedido = res.getString(res.getColumnIndex(UtilBD.CAMPO_IDMPEDIDO));
            tcant = res.getInt(res.getColumnIndex(UtilBD.CAMPO_CANTIDAD))+cantidad;
            ttot = res.getDouble(res.getColumnIndex(UtilBD.CAMPO_PTOTAL))+ntot;

            datosMPedido.put(UtilBD.CAMPO_CANTIDAD, tcant);
            datosMPedido.put(UtilBD.CAMPO_PTOTAL, ttot);

            db.update(UtilBD.TABLA_MPEDIDOS,datosMPedido,UtilBD.CAMPO_IDMPEDIDO+"=?", new String[]{idmpedido});

            //CALCULA TOTAL SIN IVA
            resSiva = db.rawQuery( "SELECT SUM(ptotal) as siniva from mpedidos where idpedido='"+pedido+"' and iva='0'", null );
            resSiva.moveToFirst();


            if(resSiva.getCount()==0){
                Log.e("SIN IVA","es nulo");
                siniva=0.00;

                Log.e("NUEVO SIN IVA","val: "+siniva);
            }else{
                siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));
                Log.e("SIN IVA","val: "+siniva);
            }

            datosSiniva.put(UtilBD.CAMPO_TSIVA, siniva);
            db.update(UtilBD.TABLA_PEDIDOS,datosSiniva,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

            //CALCULA TOTAL CON IVA
            resIva = db.rawQuery( "SELECT SUM(ptotal) as civa from mpedidos where idpedido='"+pedido+"' and iva='1'", null );
            resIva.moveToFirst();
            civa = resIva.getDouble(resIva.getColumnIndex("civa"));
            totalpedido = siniva+civa;

            datosTotal.put(UtilBD.CAMPO_TIVA, civa);
            datosTotal.put(UtilBD.CAMPO_TOTAL, totalpedido);
            db.update(UtilBD.TABLA_PEDIDOS,datosTotal,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

            db.close();

            return true;

        }else {

            Double ptotal = cantidad*precio;

            datosMPedido.put(UtilBD.CAMPO_IDPEDIDO, pedido);
            datosMPedido.put(UtilBD.CAMPO_IDPRODUCTO, producto);
            datosMPedido.put(UtilBD.CAMPO_CANTIDAD, cantidad);
            datosMPedido.put(UtilBD.CAMPO_PRECIO, precio);
            datosMPedido.put(UtilBD.CAMPO_IVA, iva);
            datosMPedido.put(UtilBD.CAMPO_PTOTAL, ptotal);
            datosMPedido.put(UtilBD.CAMPO_OBSERVACION, observacion);
            datosMPedido.put(UtilBD.CAMPO_NPRODUCTO, nproducto);
            datosMPedido.put(UtilBD.CAMPO_DETALLE, detalle);

            db.insert(UtilBD.TABLA_MPEDIDOS,null, datosMPedido);

            //CALCULA TOTAL SIN IVA
            resSiva = db.rawQuery( "SELECT SUM(ptotal) as siniva from mpedidos where idpedido='"+pedido+"' and iva='0'", null );
            resSiva.moveToFirst();
            //siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));

            if(resSiva.getCount()==0){
                Log.e("SIN IVA","es nulo");
                siniva=0.00;

                Log.e("NUEVO SIN IVA","val: "+siniva);
            }else{
                siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));
                Log.e("SIN IVA","val: "+siniva);
            }

            datosSiniva.put(UtilBD.CAMPO_TSIVA, siniva);
            db.update(UtilBD.TABLA_PEDIDOS,datosSiniva,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

            //CALCULA TOTAL CON IVA
            resIva = db.rawQuery( "SELECT SUM(ptotal) as civa from mpedidos where idpedido='"+pedido+"' and iva='1'", null );
            resIva.moveToFirst();

            if(resIva.getCount()==0){
                Log.e("SIN IVA","es nulo");
                civa=0.00;

                Log.e("NUEVO SIN IVA","val: "+civa);
            }else{
                civa = resIva.getDouble(resIva.getColumnIndex("civa"));
                Log.e("SIN IVA","val: "+civa);
            }

            //civa = resIva.getDouble(resIva.getColumnIndex("civa"));

            totalpedido = siniva+civa;

            datosTotal.put(UtilBD.CAMPO_TIVA, civa);
            datosTotal.put(UtilBD.CAMPO_TOTAL, totalpedido);
            db.update(UtilBD.TABLA_PEDIDOS,datosTotal,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});
            db.close();

            return true;

        }

    }

    public Cursor mostrarFactura(String pedido) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT idlocal, tsiva, tiva, total, login from pedidos where idpedido='"+pedido+"'", null);

        res.moveToFirst();

        return res;
    }

    public Cursor mostrarMfactura(String pedido) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT idmpedido, idpedido, idproducto, nproducto, cantidad, precio, ptotal, detalle, iva, observacion " +
                "from mpedidos where idpedido='"+pedido+"'", null);

        res.moveToFirst();

        return res;
    }

    public boolean eliminaProductoTodo(String mpedido, String pedido){
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor resSiva, resIva;
        ContentValues datosSiniva = new ContentValues();
        ContentValues datosTotal = new ContentValues();
        Double siniva, civa, totalpedido;

        db.delete(UtilBD.TABLA_MPEDIDOS,UtilBD.CAMPO_IDMPEDIDO+"=?",new String[]{mpedido});

        //CALCULA TOTAL SIN IVA
        resSiva = db.rawQuery( "SELECT SUM(ptotal) as siniva from mpedidos where idpedido='"+pedido+"' and iva='0'", null );
        resSiva.moveToFirst();
        //siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));

        if(resSiva.getCount()==0){
            Log.e("SIN IVA","es nulo");
            siniva=0.00;

            Log.e("NUEVO SIN IVA","val: "+siniva);
        }else{
            siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));
            Log.e("SIN IVA","val: "+siniva);
        }

        datosSiniva.put(UtilBD.CAMPO_TSIVA, siniva);
        db.update(UtilBD.TABLA_PEDIDOS,datosSiniva,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

        //CALCULA TOTAL CON IVA
        resIva = db.rawQuery( "SELECT SUM(ptotal) as civa from mpedidos where idpedido='"+pedido+"' and iva='1'", null );
        resIva.moveToFirst();

        if(resIva.getCount()==0){
            Log.e("SIN IVA","es nulo");
            civa=0.00;

            Log.e("NUEVO SIN IVA","val: "+civa);
        }else{
            civa = resIva.getDouble(resIva.getColumnIndex("civa"));
            Log.e("SIN IVA","val: "+civa);
        }

        totalpedido = siniva+civa;

        datosTotal.put(UtilBD.CAMPO_TIVA, civa);
        datosTotal.put(UtilBD.CAMPO_TOTAL, totalpedido);
        db.update(UtilBD.TABLA_PEDIDOS,datosTotal,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});


        return true;
    }

    public boolean eliminaProducto(String mpedido, String pedido){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        Cursor resSiva, resIva;
        int cant;
        double ptotal, precio;
        ContentValues datosElimina = new ContentValues();
        ContentValues datosSiniva = new ContentValues();
        ContentValues datosTotal = new ContentValues();
        Double siniva, civa, totalpedido;

        res =  db.rawQuery( "select cantidad, ptotal, precio from mpedidos where idmpedido='"+mpedido+"'", null );
        res.moveToFirst();
        if(res.getCount()!=0){
            cant = res.getInt(res.getColumnIndex("cantidad"));
            ptotal = res.getDouble(res.getColumnIndex("ptotal"));
            precio = res.getDouble(res.getColumnIndex("precio"));
            if(cant>1) {
                datosElimina.put(UtilBD.CAMPO_CANTIDAD, cant-1);
                datosElimina.put(UtilBD.CAMPO_PTOTAL, ptotal-precio);
                db.update(UtilBD.TABLA_MPEDIDOS,datosElimina,UtilBD.CAMPO_IDMPEDIDO+"=?", new String[]{mpedido});
            }else {
                db.delete(UtilBD.TABLA_MPEDIDOS,UtilBD.CAMPO_IDMPEDIDO+"=?",new String[]{mpedido});
            }
        }


        //CALCULA TOTAL SIN IVA
        resSiva = db.rawQuery( "SELECT SUM(ptotal) as siniva from mpedidos where idpedido='"+pedido+"' and iva='0'", null );
        resSiva.moveToFirst();
        //siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));

        if(resSiva.getCount()==0){
            Log.e("SIN IVA","es nulo");
            siniva=0.00;

            Log.e("NUEVO SIN IVA","val: "+siniva);
        }else{
            siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));
            Log.e("SIN IVA","val: "+siniva);
        }

        datosSiniva.put(UtilBD.CAMPO_TSIVA, siniva);
        db.update(UtilBD.TABLA_PEDIDOS,datosSiniva,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

        //CALCULA TOTAL CON IVA
        resIva = db.rawQuery( "SELECT SUM(ptotal) as civa from mpedidos where idpedido='"+pedido+"' and iva='1'", null );
        resIva.moveToFirst();

        if(resIva.getCount()==0){
            Log.e("SIN IVA","es nulo");
            civa=0.00;

            Log.e("NUEVO SIN IVA","val: "+civa);
        }else{
            civa = resIva.getDouble(resIva.getColumnIndex("civa"));
            Log.e("SIN IVA","val: "+civa);
        }

        totalpedido = siniva+civa;

        datosTotal.put(UtilBD.CAMPO_TIVA, civa);
        datosTotal.put(UtilBD.CAMPO_TOTAL, totalpedido);
        db.update(UtilBD.TABLA_PEDIDOS,datosTotal,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});


        return true;
    }

    public boolean agregaProducto(String pedido, String producto, int cantidad) {
        Cursor res, resSiva, resIva;
        SQLiteDatabase db = this.getWritableDatabase();
        String idmpedido;
        ContentValues datosMPedido = new ContentValues();
        ContentValues datosSiniva = new ContentValues();
        ContentValues datosTotal = new ContentValues();
        Double siniva, civa, totalpedido, precio;

        res =  db.rawQuery( "select idmpedido, cantidad, ptotal, precio from mpedidos where idpedido='"+pedido+"' and idproducto='"+producto+"'", null );
        res.moveToFirst();
        if(res.getCount()!=0){

            Double ttot=0.00, ncant=0.00, ntot=0.00;
            int tcant=0;
            //ncant = Double.parseDouble(cant);
            //ntot = Double.parseDouble(tot);

            precio = res.getDouble(res.getColumnIndex(UtilBD.CAMPO_PRECIO));

            idmpedido = res.getString(res.getColumnIndex(UtilBD.CAMPO_IDMPEDIDO));
            tcant = res.getInt(res.getColumnIndex(UtilBD.CAMPO_CANTIDAD))+cantidad;
            ttot = res.getDouble(res.getColumnIndex(UtilBD.CAMPO_PTOTAL))+precio;

            datosMPedido.put(UtilBD.CAMPO_CANTIDAD, tcant);
            datosMPedido.put(UtilBD.CAMPO_PTOTAL, ttot);

            db.update(UtilBD.TABLA_MPEDIDOS,datosMPedido,UtilBD.CAMPO_IDMPEDIDO+"=?", new String[]{idmpedido});

            //CALCULA TOTAL SIN IVA
            resSiva = db.rawQuery( "SELECT SUM(ptotal) as siniva from mpedidos where idpedido='"+pedido+"' and iva='0'", null );
            resSiva.moveToFirst();


            if(resSiva.getCount()==0){
                Log.e("SIN IVA","es nulo");
                siniva=0.00;

                Log.e("NUEVO SIN IVA","val: "+siniva);
            }else{
                siniva = resSiva.getDouble(resSiva.getColumnIndex("siniva"));
                Log.e("SIN IVA","val: "+siniva);
            }

            datosSiniva.put(UtilBD.CAMPO_TSIVA, siniva);
            db.update(UtilBD.TABLA_PEDIDOS,datosSiniva,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

            //CALCULA TOTAL CON IVA
            resIva = db.rawQuery( "SELECT SUM(ptotal) as civa from mpedidos where idpedido='"+pedido+"' and iva='1'", null );
            resIva.moveToFirst();
            civa = resIva.getDouble(resIva.getColumnIndex("civa"));
            totalpedido = siniva+civa;

            datosTotal.put(UtilBD.CAMPO_TIVA, civa);
            datosTotal.put(UtilBD.CAMPO_TOTAL, totalpedido);
            db.update(UtilBD.TABLA_PEDIDOS,datosTotal,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

            db.close();



        }
        return true;
    }

    public boolean eliminaPedido(String pedido){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UtilBD.TABLA_MPEDIDOS,UtilBD.CAMPO_IDPEDIDO+"=?",new String[]{pedido});
        db.delete(UtilBD.TABLA_PEDIDOS,UtilBD.CAMPO_IDPEDIDO+"=?",new String[]{pedido});

        return true;
    }

    public boolean eliminaMPedido(String pedido){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(UtilBD.TABLA_MPEDIDOS,UtilBD.CAMPO_IDPEDIDO+"=?",new String[]{pedido});

        return true;
    }

    public boolean finPedido(String pedido){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datosFin = new ContentValues();

        datosFin.put(UtilBD.CAMPO_FINALIZADO, 1);
        db.update(UtilBD.TABLA_PEDIDOS,datosFin,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

        return true;
    }

    public boolean eliminaPedidofin(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        String idpedido;

        res =  db.rawQuery( "select idpedido from pedidos where finalizado='1'", null );
        res.moveToFirst();
        if(res.getCount()!=0){

            idpedido = res.getString(res.getColumnIndex("idpedido"));
            db.delete(UtilBD.TABLA_MPEDIDOS,UtilBD.CAMPO_IDPEDIDO+"=?",new String[]{idpedido});
            db.delete(UtilBD.TABLA_PEDIDOS,UtilBD.CAMPO_IDPEDIDO+"=?",new String[]{idpedido});
        }

        return true;
    }

    public boolean mostrarBoton(String idlocal) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select idmpedido from mpedidos inner join pedidos " +
                "on mpedidos.idpedido=pedidos.idpedido where idlocal='"+idlocal+"' and finalizado='0'", null );
        res.moveToFirst();
        if(res.getCount()!=0){
            return true;
        }else{
            return false;
        }

    }

    public boolean actualizaPedido(String pedido, Double tsiva, Double tiva, Double total){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues datosActualiza = new ContentValues();

        datosActualiza.put(UtilBD.CAMPO_TSIVA, tsiva);
        datosActualiza.put(UtilBD.CAMPO_TIVA, tiva);
        datosActualiza.put(UtilBD.CAMPO_TOTAL, total);
        db.update(UtilBD.TABLA_PEDIDOS,datosActualiza,UtilBD.CAMPO_IDPEDIDO+"=?", new String[]{pedido});

        return true;
    }

}
