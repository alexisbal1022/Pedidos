package ec.compumax.pedidos.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
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
import java.util.ArrayList;
import java.util.List;


import ec.compumax.pedidos.FacturaPedido;
import ec.compumax.pedidos.MainActivity;
import ec.compumax.pedidos.Menu;
import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterHistorial;
import ec.compumax.pedidos.Recycler.AdapterMiPedido;
import ec.compumax.pedidos.Recycler.ContentAdapter;
import ec.compumax.pedidos.Recycler.DataMiPedido;
import ec.compumax.pedidos.Recycler.DataMiPedidoI;
import ec.compumax.pedidos.Recycler.DataProducto;
import ec.compumax.pedidos.Recycler.DataTotales;
import ec.compumax.pedidos.Recycler.DataTotalesI;
import ec.compumax.pedidos.Recycler.Item;
import ec.compumax.pedidos.Recycler.RecyclerItemClickListener;
import ec.compumax.pedidos.Registrarse;

public class MiPedidoFragment extends Fragment {

    View vista;
    String urlMipedido = "https://app.pedidosplus.com/wsProvincias/carga_mpedidos.php";
    String urlMipTotales = "https://app.pedidosplus.com/wsProvincias/carga_pedidos.php";
    //String urlElimina = "https://app.pedidosplus.com/webservice_pedidos/elimina_pedido.php";
    String urlElimina = "https://app.pedidosplus.com/wsProvincias/cancela_pedido.php";
    String idpedido,otrop,idlocalh;

    TextView lbl_subtotal, lbl_subtotalcon, lbl_iva, lbl_total, txt_elimina;
    Button btn_finaliza, btn_cancelap, btn_mas, btn_volvermas;

    List<DataMiPedido> DataAdapterClassList;
    List<DataMiPedido> DataAdapterClassListm;

    private List<Item> items = new ArrayList<>();
    RecyclerView recyclerView;
    AdapterMiPedido recyclerViewadapter;

    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();



    AppBarLayout app_barmp;
    LinearLayout llProgressBar;

    String base_datos, idpedidoInterno;

    DBHelperPedidos mydb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_mipedido, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        idpedido = prefs.getString("idpedido", "");
        otrop = prefs.getString("otrop", "");
        idlocalh = prefs.getString("local", "");
        base_datos = prefs.getString("base_datos", "");
        idpedidoInterno = prefs.getString("idpedidoInterno", "");

        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_mipedido);
        lbl_subtotal = (TextView) vista.findViewById(R.id.lbl_subtotal);
        lbl_subtotalcon = (TextView) vista.findViewById(R.id.lbl_subtotalcon);
        lbl_iva = (TextView) vista.findViewById(R.id.lbl_iva);
        lbl_total = (TextView) vista.findViewById(R.id.lbl_total);
        btn_finaliza = (Button) vista.findViewById(R.id.btn_finaliza);
        btn_cancelap = (Button) vista.findViewById(R.id.btn_cancelap);
        app_barmp = (AppBarLayout) vista.findViewById(R.id.app_barmp);
        btn_mas = (Button) vista.findViewById(R.id.btn_mas);
        btn_volvermas = (Button) vista.findViewById(R.id.btn_volvermas);
        llProgressBar = (LinearLayout)vista.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);

        DataAdapterClassList = new ArrayList<>();

        Log.e("Miped","este "+idpedido);
        Log.e("otrop","este "+otrop);
        Log.e("idlocalh","este "+idlocalh);

        //mostrarMipedido(urlMipedido, idpedido, base_datos);
        //mostrarMipTotalesN(urlMipTotales, idpedido, base_datos);

        mostrarMfacturasin(idpedidoInterno);
        mostrarFacturasin(idpedidoInterno);

        Configuration config = getResources().getConfiguration();

        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        }

        recyclerView.setHasFixedSize(true);

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar_mp);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        btn_volvermas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();
            }
        });

        if(otrop!="1") {
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mi pedido");

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().getSupportFragmentManager().beginTransaction().
                            remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();

                    //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,
                    //      new ProductosFragment()).commit();
                }
            });
            btn_mas.setVisibility(View.GONE);
        }else if (otrop=="1"){
            btn_mas.setVisibility(View.VISIBLE);
            btn_volvermas.setVisibility(View.GONE);

        }else if (otrop=="2"){
            btn_mas.setVisibility(View.GONE);
        }

        btn_mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                        new ProductosFragment()).commit();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frame_boton,
                                new BotonFragment()).commit();

                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("otrop", "2");
                editorn.commit();
                btn_mas.setVisibility(View.GONE);
            }
        });

        btn_finaliza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //finalizaPedido(urlFin,idpedido);
                //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                  //      new FacturaFragment()).commit();

                //getActivity().getSupportFragmentManager().beginTransaction().
                //      remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();

                Intent intent = new Intent(getContext(), FacturaPedido.class);
                startActivity(intent);

            }
        });
        btn_cancelap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogoEliminar();
            }
        });

        return vista;
    }

    private void mostrarMfacturasin(String pedido) {

        Cursor cursor = mydb.mostrarMfactura(pedido);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){

            DataMiPedidoI GetDataAdapter2 = new DataMiPedidoI();

            GetDataAdapter2.setNumero(cursor.getInt(cursor.getColumnIndex(UtilBD.CAMPO_CANTIDAD)));
            GetDataAdapter2.setPedido(cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_NPRODUCTO)));
            GetDataAdapter2.setDetalle(cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_DETALLE)));
            GetDataAdapter2.setPrecio(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_PTOTAL)));
            GetDataAdapter2.setId(cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_IDMPEDIDO)));
            GetDataAdapter2.setIdproducto(cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_IDPRODUCTO)));
            GetDataAdapter2.setPreciou(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_PRECIO)));
            GetDataAdapter2.setIdpedido(cursor.getString(cursor.getColumnIndex(UtilBD.CAMPO_IDPEDIDO)));


            items.add(GetDataAdapter2);
            cursor.moveToNext();
        }

        recyclerViewadapter = new AdapterMiPedido(items,getContext(),base_datos);

        recyclerView.setAdapter(recyclerViewadapter);

        recyclerViewadapter.notifyDataSetChanged();
    }

    private void mostrarFacturasin(String pedido) {

        Cursor cursor = mydb.mostrarFactura(pedido);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){

            DataTotalesI GetDataAdapter2 = new DataTotalesI();

            GetDataAdapter2.setSubtotal(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TSIVA)));
            GetDataAdapter2.setIva(0.00);
            GetDataAdapter2.setNiva(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TIVA)));
            GetDataAdapter2.setTotal(cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TOTAL)));

            Double totalf = cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TOTAL));

            SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorn = prefsn.edit();
            editorn.putString("totalf", totalf.toString());
            editorn.commit();


            items.add(GetDataAdapter2);

            cursor.moveToNext();
        }

        llProgressBar.setVisibility(View.GONE);

        recyclerViewadapter = new AdapterMiPedido(items,getContext(),base_datos);

        recyclerView.setAdapter(recyclerViewadapter);
    }

    public void dialogoEliminar() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("¿Cancelar Pedido?")
                .setMessage("Al hacerlo tu pedido se eliminará")

                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("boton", "0");
                        editorn.commit();

                        //eliminaPedido(urlElimina, idpedido, base_datos);
                        mydb.eliminaPedido(idpedidoInterno);

                        Intent intent = new Intent(getContext(), Menu.class);
                        startActivity(intent);

                        ((Activity)getContext()).finish();





                        /*getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.frame_boton,
                                        new BotonFragment()).commit();*/
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }

    public void mostrarMipedido(final String ServerURL, final String idp, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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
                        GetDataAdapter2.setId(jsonArrayChild.getString("idmpedido"));
                        GetDataAdapter2.setIdproducto(jsonArrayChild.getString("idproducto"));
                        GetDataAdapter2.setPreciou(jsonArrayChild.getString("precio"));
                        GetDataAdapter2.setIdpedido(idpedido);

                        //Adding subject name here to show on click event.
                        //EstadoC.add(jsonArrayChild.getString("fecha"));
                        ListViewClickItemArray.add(jsonArrayChild.getString("nproducto"));

                        items.add(GetDataAdapter2);
                        //DataAdapterClassList.add(GetDataAdapter2);
                        //ClassList.add(GetDataAdapter2);

                    }

                    //progressBar.setVisibility(View.GONE);

                    //recyclerViewadapter = new AdapterMiPedido(getContext(), DataAdapterClassList);

                    //recyclerView.setAdapter(recyclerViewadapter);

                    recyclerViewadapter = new AdapterMiPedido(items,getContext(),base_datos);

                    recyclerView.setAdapter(recyclerViewadapter);

                    recyclerViewadapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base);
    }

    public void mostrarMipTotalesN(final String ServerURL, final String idp, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            List<DataMiPedido> SubjectFullFormList;

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataTotales GetDataAdapter2 = new DataTotales();

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        GetDataAdapter2.setSubtotal(jsonArrayChild.getString("tsiva"));
                        GetDataAdapter2.setIva(jsonArrayChild.getString("iva"));
                        GetDataAdapter2.setNiva(jsonArrayChild.getString("tiva"));
                        GetDataAdapter2.setTotal(jsonArrayChild.getString("total"));

                        String totalf = jsonArrayChild.getString("total");

                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("totalf", totalf);
                        editorn.commit();

                        //Adding subject name here to show on click event.
                        //EstadoC.add(jsonArrayChild.getString("fecha"));

                        items.add(GetDataAdapter2);
                        //ClassList.add(GetDataAdapter2);

                    }

                    llProgressBar.setVisibility(View.GONE);

                    recyclerViewadapter = new AdapterMiPedido(items,getContext(),base_datos);

                    recyclerView.setAdapter(recyclerViewadapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base);
    }

    public void mostrarMipTotales(final String ServerURL, final String idp, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            List<DataMiPedido> SubjectFullFormList;

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
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

                JSONObject json = null;

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        lbl_subtotal.setText(jsonArrayChild.getString("tsiva"));
                        lbl_subtotalcon.setText(jsonArrayChild.getString("tiva"));
                        lbl_iva.setText(jsonArrayChild.getString("iva"));
                        lbl_total.setText(jsonArrayChild.getString("total"));

                    }

                    //progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base);
    }

    public void eliminaPedido(final String ServerURL, final String idp, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pedido", idp));
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
        sendPostReqAsyncTask.execute(idp,base);
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
}
