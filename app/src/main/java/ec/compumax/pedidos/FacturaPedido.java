package ec.compumax.pedidos;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ec.compumax.pedidos.MapaDialog.MapaUbicacion;
import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.MyMapFragmentContainer;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.Recycler.AdapterMiPedido;
import ec.compumax.pedidos.Recycler.DataTotalesI;

public class FacturaPedido extends AppCompatActivity implements MapaUbicacion.MapaDialogListener, OnMapReadyCallback {

    Button btn_realiza_pedido;
    TextView txt_nombreusu, lbldireccion;
    EditText txt_ruc, txt_razon, txt_dir, txt_ref, txt_telf, txt_comenta;

    String urlMF = "https://app.pedidosplus.com/wsProvincias/carga_factura.php";
    String urlFin = "https://app.pedidosplus.com/wsProvincias/fin_pedido.php";
    String urlMpedido="https://app.pedidosplus.com/wsProvincias/inserta_productoLocal.php";
    String urlPedido = "https://app.pedidosplus.com/wsProvincias/inserta_pedidoLocal.php";
    String loginusu, idpedido, razon, ruc, dir, tel, ref, lati, longi, totalf, envio, recargo, comenta;

    MyMapFragmentContainer mycontentmapa;

    ProgressBar pBar1;

    RadioButton radioactual, radiootro, horactual, horaotra, credito, efectivo;
    Geocoder geocoder = null;
    private LocationManager locManager;
    private Location loc;
    LinearLayout llProgressBar;

    Button btn_vermapad, btn_selecubica;

    String localrecargo, totenvio, totalpedido, latitud, longitud, horaentrega;

    Calendar calendar = Calendar.getInstance();
    int hora;
    int min;
    int seg;

    int dia, mes, anio;

    GoogleMap map;
    Boolean actualPosition = true;
    JSONObject jso;
    Double longitudOrigen, latitudOrigen;

    LinearLayout ly_fpago;


    int fpago;

    String local;

    String urlNotificacion = "https://app.pedidosplus.com/wsProvincias/enviar_notificacion_local.php";

    String creditou;

    String var_dist, var_precio, var_preciomin;
    Double distanciad, preciokmd, preciomin;

    EditText lbl_horaentrega;
    TextView lbl_fechaentrega;

    String fechahora, base_datos, idpedidoInterno,usuario_base, clave_base;
    DBHelperPedidos mydb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factura_pedido);

        mydb = new DBHelperPedidos(this, UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        loginusu = prefs.getString("loginusu", "");
        idpedido = prefs.getString("idpedido", "");
        totalf = prefs.getString("totalf", "");
        localrecargo = prefs.getString("localrecargo", "");
        latitud = prefs.getString("latitud", "");
        longitud = prefs.getString("longitud", "");
        fpago = prefs.getInt("fpago", 0);
        local = prefs.getString("local", "");
        var_dist = prefs.getString("distancia", "");
        var_precio = prefs.getString("preciokm", "");
        var_preciomin = prefs.getString("preciomin", "");
        base_datos = prefs.getString("base_datos", "");
        idpedidoInterno = prefs.getString("idpedidoInterno", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");

        distanciad = Double.valueOf(var_dist);
        preciokmd = Double.valueOf(var_precio);
        preciomin = Double.valueOf(var_preciomin);

        Log.e("ENTRA", "VALORES " + distanciad + " " + preciokmd);

        btn_realiza_pedido = (Button) findViewById(R.id.btn_realiza_pedido);
        lbl_fechaentrega = (TextView) findViewById(R.id.lbl_fechaentrega);
        lbl_horaentrega = (EditText) findViewById(R.id.lbl_horaentrega);
        txt_nombreusu = (TextView) findViewById(R.id.txt_nombreusu);
        txt_ruc = (EditText) findViewById(R.id.txt_ruc);
        txt_razon = (EditText) findViewById(R.id.txt_razon);
        txt_dir = (EditText) findViewById(R.id.txt_dir);
        txt_ref = (EditText) findViewById(R.id.txt_ref);
        txt_telf = (EditText) findViewById(R.id.txt_telf);
        txt_comenta = (EditText) findViewById(R.id.txt_comenta);
        lbldireccion = (TextView) findViewById(R.id.lbldireccion);
        radioactual = (RadioButton) findViewById(R.id.radioactual);
        radiootro = (RadioButton) findViewById(R.id.radiootro);
        horactual = (RadioButton) findViewById(R.id.horactual);
        horaotra = (RadioButton) findViewById(R.id.horaotra);
        credito = (RadioButton) findViewById(R.id.credito);
        efectivo = (RadioButton) findViewById(R.id.efectivo);
        //mycontentmapa = (MyMapFragmentContainer) findViewById(R.id.mycontentmapa);
        llProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        Button btn_vermapad = (Button) findViewById(R.id.btn_vermapad);
        btn_selecubica = (Button) findViewById(R.id.btn_selecubica);
        ly_fpago = (LinearLayout) findViewById(R.id.ly_fpago);

        SimpleMaskFormatter smf = new SimpleMaskFormatter("NN:NN");
        MaskTextWatcher mtw = new MaskTextWatcher(lbl_horaentrega, smf);
        lbl_horaentrega.addTextChangedListener(mtw);

        /*
        if (fpago==0){
            ly_fpago.setVisibility(View.GONE);
        }else{
            ly_fpago.setVisibility(View.VISIBLE);
        }
        */

        Log.e("FACTURA", "FPAGO: " + fpago);

        Log.e("totalf", totalf);

        btn_vermapad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMapa();
            }
        });

        llProgressBar.setVisibility(View.VISIBLE);

        mostrarMiFactura(urlMF, loginusu);

        btn_realiza_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String formap = "0";

                if (horaotra.isChecked()) {
                    fechahora = lbl_fechaentrega.getText().toString() + " " + lbl_horaentrega.getText().toString();
                }


                asignaText();
                if (ruc.isEmpty() || razon.isEmpty() || tel.isEmpty() || ref.isEmpty()) {

                    Snackbar snackbar = Snackbar.make(view, "Hay campos vacios", Snackbar.LENGTH_SHORT);
                    View snackbarLayout = snackbar.getView();
                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                    snackbar.show();

                } else {

                    if (lbldireccion.getText().toString().isEmpty()) {

                        Snackbar snackbar = Snackbar.make(view, "Seleccione ubicación porfavor", Snackbar.LENGTH_SHORT);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                    } else {

                        if (horaotra.isChecked() && lbl_horaentrega.getText().toString().isEmpty()) {
                            Snackbar snackbar = Snackbar.make(view, "La hora no puede estar vacía", Snackbar.LENGTH_SHORT);
                            View snackbarLayout = snackbar.getView();
                            snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                            TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                            textView.setTextColor(Color.WHITE);
                            //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                            snackbar.show();
                        } else {

                            SharedPreferences prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorn = prefsn.edit();
                            editorn.putString("boton", "0");
                            editorn.putString("otrop", "0");
                            editorn.commit();

                            //finalizaPedido(urlFin, idpedido, ruc, razon, tel, dir, ref, lati, longi);

                            //dialogoExito();
                            Double latib, longib, lata, lona;
                            latib = Double.valueOf(lati);
                            longib = Double.valueOf(longi);
                            lata = Double.valueOf(latitud);
                            lona = Double.valueOf(longitud);
                            calculaDistancia(lata, lona, latib, longib, totalf);

                            if (efectivo.isChecked()) {
                                formap = "0";
                            } else {
                                if (credito.isChecked()) {
                                    formap = "1";
                                }
                            }

                            if (localrecargo.equals("1")) {
                                confirmaDatos(totalf, "0.00", totalf, formap);
                                //confirmaDatos(totalf, totenvio, totalpedido, formap);
                            } else {
                                //confirmaDatos(totalf, "0.00", totalf, formap);
                                confirmaDatos(totalf, totenvio, totalpedido, formap);
                            }
                        }
                    }
                }

            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_fac);
        FacturaPedido facp = new FacturaPedido();
        this.setSupportActionBar(toolbar);

        if (this.getSupportActionBar() != null)// Habilitar Up Button
            this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("Mi Factura");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();


            }
        });

        /*
        radiootro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mycontentmapa.setVisibility(View.VISIBLE);
                Log.e("aqui", "clic");
                openDialogMapa();
            }
        });
        radioactual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mycontentmapa.setVisibility(View.GONE);
                miUbica();

            }
        });
        if (radioactual.isChecked()) {
            miUbica();
        } */


        horaotra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHora();

                lbl_fechaentrega.setVisibility(View.VISIBLE);
                lbl_horaentrega.setVisibility(View.VISIBLE);

                Log.e("horaotra", "clic" + horaentrega);
            }
        });
        horactual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lbl_fechaentrega.setVisibility(View.GONE);
                lbl_horaentrega.setVisibility(View.GONE);
                lbl_horaentrega.setText("");
                lbl_fechaentrega.setText("");
                //horaentrega = "Inmediata";
                fechahora = "Inmediata";
                Log.e("horactual", "clic" + horaentrega);

            }
        });

        if (horactual.isChecked()) {
            lbl_horaentrega.setText("");
            lbl_fechaentrega.setText("");
            //horaentrega = "Inmediata";
            fechahora = "Inmediata";
            Log.e("horactualn", "check" + horaentrega);
        }

        btn_selecubica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMapa();
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewfac);
        mapFragment.getMapAsync(this);

    }

    public void dialogHora() {

        hora = calendar.get(Calendar.HOUR);
        min = calendar.get(Calendar.MINUTE);
        //seg = calendar.get(Calendar.SECOND);

        dia = calendar.get(Calendar.DAY_OF_MONTH);
        mes = calendar.get(Calendar.MONTH);
        anio = calendar.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        horaentrega = dayOfMonth + "-" + month + "-" + year;
                        lbl_fechaentrega.setText(horaentrega);
                        Log.e("ENTREGA", "DATOS: " + horaentrega);

                    }
                }, anio, mes, dia);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();

        /*

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        horaentrega = i + ":" + i1;
                    }
                }, hora, min, false);
        timePickerDialog.show();

         */
    }

    public void calculaDistancia(Double latA, Double lonA, Double latB, Double lonB, String totalp) {

        Log.e("CALCULO", "VALORES " + distanciad + " " + preciokmd);

        Location locationA = new Location("punto A");

        locationA.setLatitude(latA);
        locationA.setLongitude(lonA);

        Location locationB = new Location("punto B");

        locationB.setLatitude(latB);
        locationB.setLongitude(lonB);

        float distance = locationA.distanceTo(locationB);

        float totkm = distance / 1000;

        Double cal, calt, calf;
        calt = Double.valueOf(totalp);
        float difkm;

        /*if (totkm > 2) {
            difkm = totkm - 2;
            cal = ((double) Math.round((difkm * 0.50) * 100d) / 100d) + 1.50;
            calf = (cal + calt);

            Log.e("diferencia", "" + totkm + "km " + cal + "km");
        } else {
            cal = 1.50;
            calf = cal + calt;
        }*/

        if (totkm > distanciad) {
            difkm = (float) (totkm - distanciad);
            cal = ((double) Math.round((difkm * preciokmd) * 100d) / 100d) + preciomin;
            calf = (cal + calt);

            Log.e("diferencia", "" + totkm + "km " + cal + "km");
        } else {
            cal = preciomin;
            calf = cal + calt;
        }

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator('.');
        DecimalFormat formateador = new DecimalFormat("####.####", simbolos);

        DecimalFormat format = new DecimalFormat("#.00", simbolos);// el numero de ceros despues del entero
        //format.format(/*double,object,long*/); // > retorna string
        totenvio = String.valueOf(cal);
        totalpedido = String.valueOf(format.format(calf));

        Log.e("distancia", "" + distance + "m " + totkm + "km");
    }

    public void confirmaDatos(String total, String valenvio, String valtotal, final String fpago) {

        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(Html.fromHtml("<font color='#C62828'>Total pedido: " + total + "<br>Valor Envío: " + valenvio + "<br>Total: " + valtotal + "</font>"))

                .setPositiveButton("Confirmar Pedido", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finalizaPedido(urlFin, idpedido, ruc, razon, tel, dir, ref, lati, longi, totenvio, totalpedido, fechahora, fpago, comenta, base_datos);
                        subirPedido(idpedidoInterno, fpago);

                    }
                })
                .setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void subirPedido(String pedido, String fpago) {

        String idlocal, tsiva, tiva, total, login;

        Cursor cursor = mydb.mostrarFactura(pedido);
        cursor.moveToFirst();

        idlocal = String.valueOf(cursor.getInt(cursor.getColumnIndex(UtilBD.CAMPO_IDLOCAL)));
        tsiva = String.valueOf(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TSIVA)));
        tiva = String.valueOf(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TIVA)));
        total = String.valueOf(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TOTAL)));
        login = cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_LOGIN));

        insertaPedido(urlPedido, idlocal, login, tsiva, tiva, total, ruc, razon, dir, ref, tel, lati, longi, totenvio, totalpedido, fechahora, fpago, comenta, base_datos,usuario_base,clave_base);
        //insertaPedido(urlPedido,login,base_datos);

    }

    public void insertaPedido(final String ServerURL, final String idlocal, final String user, final String tsiva, final String tiva, final String total, final String ruc, final String razon, final String dir, final String ref, final String telf, final String lati, final String longi, final String envio, final String recargo, final String hora, final String fpago, final String comenta, final String base,final String usuario, final String clave) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("local", idlocal));
                paramsn.add(new BasicNameValuePair("login", user));
                paramsn.add(new BasicNameValuePair("tsiva", tsiva));
                paramsn.add(new BasicNameValuePair("tiva", tiva));
                paramsn.add(new BasicNameValuePair("total", total));
                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("direccion", dir));
                paramsn.add(new BasicNameValuePair("ref", ref));
                paramsn.add(new BasicNameValuePair("telf", telf));
                paramsn.add(new BasicNameValuePair("lati", lati));
                paramsn.add(new BasicNameValuePair("longi", longi));
                paramsn.add(new BasicNameValuePair("envio", envio));
                paramsn.add(new BasicNameValuePair("recargo", recargo));
                paramsn.add(new BasicNameValuePair("hora", hora));
                paramsn.add(new BasicNameValuePair("fpago", fpago));
                paramsn.add(new BasicNameValuePair("comenta", comenta));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuario));
                paramsn.add(new BasicNameValuePair("clave", clave));
                Log.e("local", "entro" + idlocal);
                Log.e("ruc", "entro" + user);
                Log.e("url", "entro" + ServerURL);

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();

                    InputStream instream = httpEntity.getContent();
                    resultado[0] = convertStreamToString(instream);

                    Log.e("resultado", "entro" + resultado[0]);

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                Log.e("resval", "val" + result);
                super.onPostExecute(result);

                String idpedido, idproducto, cantidad, precio, iva, ptotal, observacion;
                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");

                    JSONObject jsonArrayChild = jsonArray.getJSONObject(0);

                    idpedido = (jsonArrayChild.getString("idpedido"));
                    Log.e("NUEVO ", "IDPEDIDO: " + idpedido);



                    Cursor mpedido = mydb.mostrarMfactura(idpedidoInterno);
                    mpedido.moveToFirst();

                    while (mpedido.isAfterLast() == false) {

                        idproducto = mpedido.getString(mpedido.getColumnIndex(UtilBD.CAMPO_IDPRODUCTO));
                        cantidad = String.valueOf(mpedido.getInt(mpedido.getColumnIndex(UtilBD.CAMPO_CANTIDAD)));
                        precio = String.valueOf(mpedido.getDouble(mpedido.getColumnIndex(UtilBD.CAMPO_PRECIO)));
                        iva = String.valueOf(mpedido.getInt(mpedido.getColumnIndex(UtilBD.CAMPO_IVA)));
                        ptotal = String.valueOf(mpedido.getDouble(mpedido.getColumnIndex(UtilBD.CAMPO_PTOTAL)));
                        observacion = mpedido.getString(mpedido.getColumnIndex(UtilBD.CAMPO_OBSERVACION));

                        if (idpedido.length() > 0) {

                            //mydb.actualizaestadoEgreso(idegreso);
                            //insertarVenta(urlInserta, idegreso, login, idproducto, cantidad, precio, iva, ptotal, psin, pcon, base);
                            insertaProducto(urlMpedido,idpedido,idproducto,cantidad,precio,iva,ptotal,observacion,base_datos,usuario_base,clave_base);

                        } else {
                            Log.e("MEGRESO", "ERROR");
                        }

                        mpedido.moveToNext();

                    }

                    dialogoExito();
                    enviaNotificacion(urlNotificacion, idlocal, base_datos, usuario_base,clave_base);




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idlocal, user, tsiva, tiva, total, ruc, razon, telf, dir, ref, lati, longi, envio, recargo, hora, fpago, comenta, base,usuario,clave);
    }

    public void insertaProducto(final String ServerURL, final String idpedido, final String idproducto, final String cantidad, final String precio, final String iva, final String ptotal, final String observa, final String base, final String usuario, final String clave) {

        final String[] resultado = {null};
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pedido", idpedido));
                paramsn.add(new BasicNameValuePair("producto", idproducto));
                paramsn.add(new BasicNameValuePair("cant", cantidad));
                paramsn.add(new BasicNameValuePair("precio", precio));
                paramsn.add(new BasicNameValuePair("iva", iva));
                paramsn.add(new BasicNameValuePair("tot", ptotal));
                paramsn.add(new BasicNameValuePair("observa", observa));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuario));
                paramsn.add(new BasicNameValuePair("clave", clave));

                Log.e("pedidodetalleinserta","es: "+idpedido);

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();

                    InputStream instream = httpEntity.getContent();
                    resultado[0] = convertStreamToString(instream);


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);
                String datres= "Datos insertados";


                if (result.contains(datres)) {
                    //Toast.makeText(getContext(), "Datos insertados exitosamente", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idpedido, idproducto, cantidad, precio, iva, ptotal, observa, base,usuario,clave);
    }


    public void enviaNotificacion(final String ServerURL, final String local, final String base, final String usuario, final String clave) {

        final String[] resultado = {null};
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("local", local));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuario));
                paramsn.add(new BasicNameValuePair("clave", clave));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();

                    InputStream instream = httpEntity.getContent();
                    resultado[0] = convertStreamToString(instream);


                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }
                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                super.onPostExecute(result);

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(local, base, usuario,clave);
    }

    public void dialogoExito() {

        new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setCancelable(false)
                .setMessage("Pedido Exitoso")

                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mydb.finPedido(idpedidoInterno);

                        startActivity(new Intent(FacturaPedido.this, Menu.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();

                        //Intent intent = new Intent(FacturaPedido.this, Menu.class);
                        //startActivity(intent);
                    }
                })
                .show();
    }

    public void asignaText() {
        razon = txt_razon.getText().toString().trim();
        ruc = txt_ruc.getText().toString().trim();
        //dir = txt_dir.getText().toString().trim();
        tel = txt_telf.getText().toString().trim();
        ref = txt_ref.getText().toString().trim();
        dir = lbldireccion.getText().toString().trim();
        comenta = txt_comenta.getText().toString().trim();
    }

    public void mostrarMiFactura(final String ServerURL, final String user) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("user", user));
                //paramsn.add(new BasicNameValuePair("base", base));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();

                    InputStream instream = httpEntity.getContent();
                    resultado[0] = convertStreamToString(instream);

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                Log.e("post", "entro");

                Log.e("resval", "val" + result);
                super.onPostExecute(result);

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        txt_nombreusu.setText(jsonArrayChild.getString("name"));
                        txt_ruc.setText(jsonArrayChild.getString("ruc"));
                        txt_razon.setText(jsonArrayChild.getString("razonsocial"));
                        //txt_dir.setText(jsonArrayChild.getString("direccion"));
                        txt_ref.setText(jsonArrayChild.getString("referencia"));
                        txt_telf.setText(jsonArrayChild.getString("telefono"));
                        creditou = (jsonArrayChild.getString("credito"));

                        if (creditou.equals("1")) {
                            ly_fpago.setVisibility(View.VISIBLE);
                        } else {
                            ly_fpago.setVisibility(View.GONE);
                        }

                    }

                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(user);
    }

    public void finalizaPedido(final String ServerURL, final String idp, final String ruc, final String razon, final String telf, final String dir, final String ref, final String lati, final String longi, final String envio, final String recargo, final String horae, final String fpago, final String comenta, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("telf", telf));
                paramsn.add(new BasicNameValuePair("direccion", dir));
                paramsn.add(new BasicNameValuePair("ref", ref));
                paramsn.add(new BasicNameValuePair("lati", lati));
                paramsn.add(new BasicNameValuePair("longi", longi));
                paramsn.add(new BasicNameValuePair("envio", envio));
                paramsn.add(new BasicNameValuePair("recargo", recargo));
                paramsn.add(new BasicNameValuePair("hora", horae));
                paramsn.add(new BasicNameValuePair("fpago", fpago));
                paramsn.add(new BasicNameValuePair("comenta", comenta));
                paramsn.add(new BasicNameValuePair("base", base));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    HttpEntity httpEntity = httpResponse.getEntity();

                    InputStream instream = httpEntity.getContent();
                    resultado[0] = convertStreamToString(instream);

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                Log.e("post", "entro");

                Log.e("resval", "val" + result);
                super.onPostExecute(result);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp, ruc, razon, telf, dir, ref, lati, longi, envio, recargo, horae, fpago, comenta, base);
    }

    private String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "vacio no hay nada";
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    lbldireccion.setText(DirCalle.getAddressLine(0));
                    txt_dir.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openDialogMapa() {
        MapaUbicacion mapaDialog = new MapaUbicacion();
        mapaDialog.setCancelable(false);
        mapaDialog.show(getSupportFragmentManager(), "example dialog");

    }

    @Override
    public void applyTexts(String direccion, String latitud, String longitud) {
        lati = latitud;
        longi = longitud;
        txt_dir.setText(direccion);
        lbldireccion.setText(direccion);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (ActivityCompat.checkSelfPermission(FacturaPedido.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(FacturaPedido.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(FacturaPedido.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(FacturaPedido.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);


            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(FacturaPedido.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(FacturaPedido.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

            return;
        }
        map.setMyLocationEnabled(true);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (actualPosition) {
                    latitudOrigen = location.getLatitude();
                    longitudOrigen = location.getLongitude();
                    lati = (String.valueOf(location.getLatitude()));
                    longi = (String.valueOf(location.getLongitude()));

                    //latitudOrigen = -1.498943;
                    //longitudOrigen = -78.020953;

                    //lbllatitud.setText(latitudOrigen.toString());
                    //lbllongitud.setText(longitudOrigen.toString());
                    setLocation(location);


                    actualPosition = false;
                    //Toast.makeText(FacturaPedido.this, latitudOrigen+" / "+longitudOrigen, Toast.LENGTH_LONG).show();

                    LatLng miPosicion = new LatLng(latitudOrigen, longitudOrigen);

                    //map.addMarker(new MarkerOptions().position(miPosicion).title("Aqui estoy yo").draggable(true));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudOrigen, longitudOrigen))
                            .zoom(17)
                            .bearing(90)
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
            }
        });


    }

}
