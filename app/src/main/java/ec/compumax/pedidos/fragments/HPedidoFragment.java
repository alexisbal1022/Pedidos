package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterMiPedidoH;
import ec.compumax.pedidos.Recycler.DataMiPedido;
import ec.compumax.pedidos.Recycler.Item;
import ec.compumax.pedidos.recyclerExpandible.ChildData;
import ec.compumax.pedidos.recyclerExpandible.MyAdapter;
import ec.compumax.pedidos.recyclerExpandible.ParentData;

public class HPedidoFragment extends Fragment {

    View vista;
    String urlMipedido = "https://app.pedidosplus.com/wsProvincias/carga_mpedidos.php";
    String urlMipTotales = "https://app.pedidosplus.com/wsProvincias/carga_pedidos.php";
    String urlinsertaOtro = "https://app.pedidosplus.com/wsProvincias/inserta_otropedido.php";
    String urlMoto = "https://app.pedidosplus.com/wsProvincias/carga_motorizado_pedido.php";
    String urlLocal = "https://app.pedidosplus.com/wsProvincias/consulta_local.php";
    String idpedido,idpedidoob,loginusu;

    TextView lbl_subtotal,lbl_subpedido,lbl_subenvio,lbl_total;
    Button btn_otravez;

    AppBarLayout app_barmp;

    List<DataMiPedido> DataAdapterClassList;
    RecyclerView recyclerView,recyclerdetalle;
    RecyclerView.Adapter recyclerViewadapter;
    View ChildView ;
    int RecyclerViewClickedItemPOS;

    private List<Item> items = new ArrayList<>();
    LinearLayout llProgressBar;

    String placa, nmoto;

    List<ParentData> list = new ArrayList<>();
    MyAdapter myAdapter;

    String rucl,razonl,dirl,refl,telfl,base_datos,usuario_base, clave_base;

    String actualf;
    Date date1, date2, date3;

    String horai, horaf, idlocalh;
    int dianS, estadoLocal;

    DBHelperPedidos mydb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_hpedido, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        idpedido = prefs.getString("idpedidoh", "");
        idlocalh = prefs.getString("localh", "");
        loginusu = prefs.getString("loginusu", "");
        base_datos = prefs.getString("base_datos", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");

        lbl_subtotal = (TextView)vista.findViewById(R.id.lbl_subtotal);
        lbl_subpedido = (TextView)vista.findViewById(R.id.lbl_subpedido);
        lbl_subenvio = (TextView)vista.findViewById(R.id.lbl_subenvio);
        lbl_total = (TextView)vista.findViewById(R.id.lbl_total);
        btn_otravez = (Button)vista.findViewById(R.id.btn_otravez);
        app_barmp = (AppBarLayout)vista.findViewById(R.id.app_barmp);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_hmipedido);
        llProgressBar = (LinearLayout)vista.findViewById(R.id.llProgressBar);

        llProgressBar.setVisibility(View.VISIBLE);

        DataAdapterClassList = new ArrayList<>();

        mostrarMipedido(urlMipedido,idpedido, base_datos,usuario_base,clave_base);
        mostrarMipTotales(urlMipTotales,idpedido, base_datos,usuario_base,clave_base);
        mostrarMoto(urlMoto,idpedido,base_datos,usuario_base,clave_base);

        Configuration config = getResources().getConfiguration();

        recyclerdetalle = (RecyclerView)vista.findViewById(R.id.datos_factura);
        recyclerdetalle.setLayoutManager(new LinearLayoutManager(getContext()));

        //List<ParentData> list = getList();

        recyclerdetalle.setLayoutManager(new LinearLayoutManager(getContext()));

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        }

        recyclerView.setHasFixedSize(true);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });

            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView Recyclerview, @NonNull MotionEvent motionEvent) {
                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {
                    RecyclerViewClickedItemPOS = Recyclerview.getChildAdapterPosition(ChildView);
                }

                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar_mp);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Detalles de pedido");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_historial)).commit();
            }
        });

        consultarLocal(urlLocal,idlocalh,base_datos,usuario_base,clave_base);

        btn_otravez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(estadoLocal==1){
                    Calendar c1 = Calendar.getInstance();
                    actualf = String.valueOf(c1.get(Calendar.DAY_OF_WEEK));

                    DateFormat dateFormatn = new SimpleDateFormat("HH:mm");

                    //Calendar c1 = Calendar.getInstance();
                    int horaActual, minutosActual;
                    horaActual = c1.get(Calendar.HOUR_OF_DAY);
                    minutosActual = c1.get(Calendar.MINUTE);
                    String horaActual2 = horaActual + ":" + minutosActual;

                    Log.e("horaActual2", "" + horaActual2);


                    try {
                        date1 = dateFormatn.parse(horaActual2);
                        //dateNueva = dateFormatn.format(date1);
                        Log.e("date1inicio", "" + date1);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (actualf.equals(String.valueOf(dianS))) {

                        try {
                            date2 = dateFormatn.parse(horai);
                            date3 = dateFormatn.parse(horaf);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if ((date2.compareTo(date1) <= 0) && (date3.compareTo(date1) >= 0)) {
                            //ABIERTO
                            dialogoOtro();
                            Log.e("TAG", "onClick: local abierto" );
                        } else {
                            //CERRADO
                            Log.e("TAG", "onClick: local cerrado" );
                            dialogoCerrado();
                        }

                    } else {
                        //CERRADO
                        Log.e("TAG", "onClick: local cerrado" );
                        dialogoCerrado();
                    }
                }else{
                    //cerrado
                    Log.e("TAG", "onClick: local cerrado no dia" );
                    dialogoCerrado();
                }

            }
        });



        return vista;
    }

    public void consultarLocal(final String ServerURL, final String idlocal, final String base,final String usuario, final String clave){

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("local", idlocal));
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

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;

                try {
                    String dian;
                    json = new JSONObject(result);
                    JSONArray jsonArray = json.optJSONArray("result");

                    if(jsonArray.length()==0){
                        estadoLocal = 0;

                    }else {

                        estadoLocal = 1;

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                            dian = jsonArrayChild.getString("dia");
                            horai = jsonArrayChild.getString("hinicio");
                            horaf = jsonArrayChild.getString("hfin");

                            dianS = Integer.parseInt(dian) + 1;

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idlocal,base,usuario,clave);
    }

    private List<ParentData> getList() {

        List<ParentData> list_parent =new ArrayList<>();
        List<ChildData> list_data_child = new ArrayList<>();

        list_data_child.add(new ChildData(razonl,rucl,telfl,refl,dirl,nmoto,placa,"4"));

        list_parent.add(new ParentData("Datos de Factura y Envío",list_data_child));

        return list_parent;
    }

    public void mostrarMoto(final String ServerURL, final String idp, final String base,final String usuario, final String clave){

        final String[] resultado = {""};

        Log.e("totales",""+idp);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pedido", idp));
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

                Log.e("post","entro");

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        placa = jsonArrayChild.getString("placa");
                        nmoto = jsonArrayChild.getString("nombre");


                    }

                    list = getList();

                    myAdapter = new MyAdapter(getContext(),list);
                    recyclerdetalle.setAdapter(myAdapter);

                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base,usuario,clave);
    }

    public void dialogoOtro() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("Volver a Pedir")
                .setMessage("¿Seguro que desea volver a solicitar este pedido?")
                .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //insertaPedido(urlinsertaOtro,loginusu,idpedido,base_datos,usuario_base,clave_base);

                        idpedidoob=mydb.insertaPedido(loginusu,idlocalh);
                        insertarPedidoInterno(idpedidoob);
                        Log.e("idpedidoconsulta",": "+idpedidoob);
                        dialogoContinua();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void insertarPedidoInterno(String pedido){
        //idpedidoob=mydb.insertaPedido(loginusu,idlocalh);
        mostrarMipedidoInterno(urlMipedido,idpedido, base_datos,usuario_base,clave_base,pedido);
        mostrarMipTotalesInterno(urlMipTotales,idpedido, base_datos,usuario_base,clave_base,pedido);
    }

    public void dialogoCerrado() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("Local Cerrado")
                .setMessage("Lo sentimos este local se encuentra cerrado")

                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .show();
    }

    public void dialogoContinua() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setMessage("Nuevo Pedido Creado")
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("idpedido", idpedidoob);
                        editorn.putString("idpedidonn", idpedidoob);
                        editorn.putString("idpedidoInterno", idpedidoob);
                        editorn.putString("boton", "1");
                        editorn.putString("otrop", "1");
                        editorn.commit();

                        Log.e("idpedidoob",": "+idpedidoob);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new CategoriasFragment()).commit();
                        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                                new MiPedidoFragment()).commit();
                    }
                })
                .show();
    }

    public void insertaPedido(final String ServerURL, final String user, final String pedido, final String base, final String usuario, final String clave){

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("ruc", user));
                paramsn.add(new BasicNameValuePair("pedido", pedido));
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

                    Log.e("resultado","entro"+resultado[0]);

                } catch (ClientProtocolException e) {

                } catch (IOException e) {

                }

                return resultado[0];
            }

            @Override
            protected void onPostExecute(String result) {

                Log.e("post","entro");

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        idpedidoob=(jsonArrayChild.getString("idpedido"));

                        Log.e("respuestaidp","val"+idpedidoob);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(user,pedido,base, usuario,clave);
    }


    public void mostrarMipedidoInterno(final String ServerURL, final String idp, final String base,final String usuario, final String clave, final String interno){

        final String[] resultado = {""};
        Log.e("pedidos",""+idp);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                Log.e("post","entro");

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;

                String producto,observacion,nproducto,detalle;
                Double precio, cant;
                int cantidad, iva;

                try {
                    json = new JSONObject(result);
                    JSONArray jsonArray = json.optJSONArray("result");

                    if(jsonArray.length()>0) {
                        mydb.eliminaMPedido(interno);
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        cant = Double.valueOf(jsonArrayChild.getString("cantidad"));
                        nproducto = jsonArrayChild.getString("nproducto");
                        detalle = jsonArrayChild.getString("detalle");
                        observacion = jsonArrayChild.getString("observacion");
                        producto = jsonArrayChild.getString("idproducto");
                        precio = Double.valueOf(jsonArrayChild.getString("precio"));
                        iva = Integer.parseInt(jsonArrayChild.getString("iva"));
                        cantidad= cant.intValue();

                        mydb.insertaMPedido(interno,producto,cantidad,precio,iva,observacion,nproducto,detalle);

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base,usuario,clave);
    }

    public void mostrarMipTotalesInterno(final String ServerURL, final String idp, final String base, final String usuario, final String clave, final String interno){

        final String[] resultado = {""};

        Log.e("totales",""+idp);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                JSONObject json = null;

                Double tiva, tsiva, total;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        tsiva = Double.valueOf(jsonArrayChild.getString("tsiva"));
                        tiva = Double.valueOf(jsonArrayChild.getString("tiva"));
                        total = Double.valueOf(jsonArrayChild.getString("total"));

                        mydb.actualizaPedido(interno,tsiva,tiva,total);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp, base, usuario, clave);
    }

    public void mostrarMipedido(final String ServerURL, final String idp, final String base,final String usuario, final String clave){

        final String[] resultado = {""};
        Log.e("pedidos",""+idp);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                Log.e("post","entro");

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataMiPedido GetDataAdapter2 = new DataMiPedido();

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        GetDataAdapter2.setNumero(jsonArrayChild.getString("cantidad"));
                        GetDataAdapter2.setPedido(jsonArrayChild.getString("nproducto"));
                        GetDataAdapter2.setPrecio(jsonArrayChild.getString("ptotal"));
                        GetDataAdapter2.setDetalle(jsonArrayChild.getString("detalle"));

                        //Adding subject name here to show on click event.
                        //EstadoC.add(jsonArrayChild.getString("fecha"));

                        //DataAdapterClassList.add(GetDataAdapter2);
                        items.add(GetDataAdapter2);

                    }

                    //progressBar.setVisibility(View.GONE);

                    //recyclerViewadapter = new AdapterMiPedido(getContext(),DataAdapterClassList);
                    //recyclerView.setAdapter(recyclerViewadapter);

                    recyclerViewadapter = new AdapterMiPedidoH(items);

                    recyclerView.setAdapter(recyclerViewadapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base,usuario,clave);
    }

    public void mostrarMipTotales(final String ServerURL, final String idp, final String base, final String usuario, final String clave){

        final String[] resultado = {""};

        Log.e("totales",""+idp);

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                Log.e("post","entro");

                Log.e("resval","val"+result);
                super.onPostExecute(result);

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        lbl_total.setText(jsonArrayChild.getString("trecargo"));
                        lbl_subenvio.setText(jsonArrayChild.getString("tenvio"));
                        lbl_subpedido.setText(jsonArrayChild.getString("total"));

                        rucl = jsonArrayChild.getString("ruc");
                        razonl = jsonArrayChild.getString("razonsocial");
                        dirl = jsonArrayChild.getString("direccion");
                        telfl = jsonArrayChild.getString("telefono");
                        refl = jsonArrayChild.getString("referencia");

                        /*
                        lbl_subtotal.setText(jsonArrayChild.getString("tsiva"));
                        lbl_subtotalcon.setText(jsonArrayChild.getString("tiva"));
                        lbl_iva.setText(jsonArrayChild.getString("iva"));
                        lbl_total.setText(jsonArrayChild.getString("total"));
                        */

                    }

                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp, base, usuario, clave);
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
            }
            finally {
                is.close();
            }
            return sb.toString();
        } else {
            return "vacio no hay nada";
        }
    }
}
