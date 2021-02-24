package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterProducto;
import ec.compumax.pedidos.Recycler.DataProducto;

public class ProductosFragment extends Fragment {

    View vista;
    List<DataProducto> DataAdapterClassList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    View ChildView;
    int RecyclerViewClickedItemPOS;
    //String urlP="https://app.pedidosplus.com/webservice_pedidos/carga_productos.php";
    String urlP = "https://app.pedidosplus.com/wsProvincias/carga_productos_limit.php";
    //String urlElimina = "https://app.pedidosplus.com/webservice_pedidos/elimina_pedido.php";
    String urlElimina = "https://app.pedidosplus.com/wsProvincias/cancela_pedido.php";
    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray1 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray2 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray3 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray4 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray5 = new ArrayList<String>();
    ImageView img_fondo;
    ProgressBar pb_cargamas;

    String idca, nomlocal, nomimg, idlocal, rucusu, idped, botonmi_pedido, botonmi_pedidon, loginusu, otrop, idpedidonn;
    AppBarLayout app_bar;

    TextView txtnpedido;
    Button mi_pedido;

    String pedidoglobal;

    public LinearLayout layout_id;

    ProgressBar pBar1;

    String urlPi = "https://app.pedidosplus.com/wsProvincias/inserta_pedido.php";
    EditText txt_busca;
    LinearLayout llProgressBar;

    CoordinatorLayout coor_prod;

    String pagina = "1";

    NestedScrollView ns_productos;

    Boolean cargamas;

    int npagina = 1;

    String palabra = "";

    Button btn_buscar, btn_vermas;

    TextView lbl_contactos;

    String telefonoloc;

    private int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private float roundPx;

    String base_datos, idciudad, idpedidoInterno,usuario_base, clave_base,rutaGlobal;

    DBHelperPedidos mydb;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_productos, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        idlocal = prefs.getString("local", "");
        idca = prefs.getString("categ", "");
        nomimg = prefs.getString("nomimg", "");
        nomlocal = prefs.getString("nomlocal", "");
        rucusu = prefs.getString("ruc", "");
        loginusu = prefs.getString("loginusu", "");
        otrop = prefs.getString("otrop", "");
        idpedidonn = prefs.getString("idpedidonn", "");
        telefonoloc = prefs.getString("telefonoloc", "");
        base_datos = prefs.getString("base_datos", "");
        idciudad = prefs.getString("ciudad", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        Log.e("PRODUCTOS", "BASE DATOS "+base_datos);
        Log.e("PRODUCTOS", "USUARIO  "+loginusu);

        pBar1 = (ProgressBar) vista.findViewById(R.id.progressBar1);
        txtnpedido = (TextView) vista.findViewById(R.id.txtnpedido);
        lbl_contactos = (TextView) vista.findViewById(R.id.lbl_contactos);
        llProgressBar = (LinearLayout) vista.findViewById(R.id.llProgressBar);
        coor_prod = (CoordinatorLayout) vista.findViewById(R.id.coor_prod);
        ns_productos = (NestedScrollView) vista.findViewById(R.id.ns_productos);
        pb_cargamas = (ProgressBar) vista.findViewById(R.id.pb_cargamas);
        btn_buscar = (Button) vista.findViewById(R.id.btn_buscar);
        btn_vermas = (Button) vista.findViewById(R.id.btn_vermas);

        llProgressBar.setVisibility(View.VISIBLE);

        if (otrop != "1" && otrop != "2") {

            idpedidoInterno = mydb.insertaPedido(loginusu,idlocal);
            Log.e("pedidointerno", "es: " + idpedidoInterno);
            //insertaPedido(urlPi, idlocal, loginusu, base_datos);
            SharedPreferences prefsn = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editorn = prefsn.edit();
            editorn.putString("idpedidoInterno", idpedidoInterno);
            editorn.commit();

            if(mydb.mostrarBoton(idlocal)) {
                SharedPreferences prefsnb = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editornb = prefsnb.edit();
                editorn.putString("boton", "1");
                editorn.commit();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frame_boton,
                                new BotonFragment()).commit();
            }

        } else {
            Log.e("TAG", "onCreateView: entra repite pedido");
            txtnpedido.setText(idpedidonn);
            idpedidoInterno = idpedidonn;

        }

        Log.e("otrop", "es: " + otrop);
        Log.e("loginusu", "es: " + loginusu);
        Log.e("idlocal", "es: " + idlocal);

        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_productos);

        app_bar = (AppBarLayout) vista.findViewById(R.id.app_barp);

        img_fondo = (ImageView) vista.findViewById(R.id.img_fondo);
        txt_busca = (EditText) vista.findViewById(R.id.txt_buscar);


        String codificada = Uri.encode(nomimg);
        //String cab = "https://guiapuyo.com/pedidos/_lib/file/imgcategoria"+idca+"/local"+idlocal+"/";
        String cab = rutaGlobal+"imgciudad"+idciudad+"/categoria" + idca + "/local" + idlocal + "/";

        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.fondosuperior)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        app_bar.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        Glide.with(getContext())
                .asBitmap()
                .load(cab+codificada)
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        img_fondo.setImageBitmap(getRoundedRectBitmap(resource, 50));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (otrop == "1") {

            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else if (otrop == "2") {
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(nomlocal);
        lbl_contactos.setText(telefonoloc);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs1 = getActivity().getSharedPreferences("Preferences", 0);
                botonmi_pedidon = prefs1.getString("boton", "");

                Log.e("botonmi_pedidon", ": " + botonmi_pedidon);

                String detboton = DetalleFragment.varenvia;

                Log.e("detboton", ": " + detboton);

                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();

                /*
                if (botonmi_pedidon.equals("1")) {
                    dialogoCancelar();

                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().
                            remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();
                }

                 */


                /*
                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("categ", idca);
                editorn.commit();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_categoria,
                        new LocalesFragment()).commit();

                 */
            }
        });
        //mostrarData(urlP,idlocal);

        cargarDatos(urlP, idlocal, pagina, "", base_datos,usuario_base,clave_base);
        Log.e("ACTIVITY", "DATOS: " + pagina + "");

        Log.e("entro", "url+id " + urlP + idlocal);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);

        Configuration config = getResources().getConfiguration();


        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));

        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(getContext(), "portrait", Toast.LENGTH_SHORT).show();
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        DataAdapterClassList = new ArrayList<>();

        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.setHasFixedSize(true);

        /*

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });
            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if(ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    RecyclerViewClickedItemPOS = Recyclerview.getChildAdapterPosition(ChildView);

                    String nomprod = ListViewClickItemArray.get(RecyclerViewClickedItemPOS);
                    String nomimgp = ListViewClickItemArray1.get(RecyclerViewClickedItemPOS);
                    String nomdetap = ListViewClickItemArray2.get(RecyclerViewClickedItemPOS);
                    String preciop = ListViewClickItemArray3.get(RecyclerViewClickedItemPOS);
                    String ivap = ListViewClickItemArray4.get(RecyclerViewClickedItemPOS);
                    String idp = ListViewClickItemArray5.get(RecyclerViewClickedItemPOS);

                    Log.e("PEDIDO", "ID: "+idp);
                    Log.e("PEDIDO", "POSICION: "+RecyclerViewClickedItemPOS);

                    SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorn = prefsn.edit();
                    editorn.putString("nproducto", nomprod);
                    editorn.putString("imagenp", nomimgp);
                    editorn.putString("detalle", nomdetap);
                    editorn.putString("preciop", preciop);
                    editorn.putString("ivap", ivap);
                    editorn.putString("idp", idp);
                    editorn.putString("idpedido", txtnpedido.getText().toString());
                    editorn.commit();


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                        // Create new fragment to add (Fragment B)
                        Fragment fragment = new DetalleFragment();
                        fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));
                        // Add Fragment B
                        FragmentTransaction ft = getFragmentManager()
                                .beginTransaction()
                                .add(R.id.content_principal, fragment)
                                .addToBackStack(null);
                        ft.commit();


                    }

                }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        */

        /*mi_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                        new MiPedidoFragment()).commit();
            }
        });*/

        /*
        txt_busca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //((AdapterProducto) recyclerViewadapter).getFilter().filter(charSequence);

                if (((AdapterProducto) recyclerViewadapter) != null){
                    ((AdapterProducto) recyclerViewadapter).getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        */

        ns_productos.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    Log.e("PRODUCTOS", "ultimo");

                    cargamas = true;

                    if (cargamas) {
                        Log.e("SCROLL", "DATOS: " + pagina + ":" + palabra + ":");

                        btn_vermas.setVisibility(View.VISIBLE);
                        btn_vermas.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pb_cargamas.setVisibility(View.VISIBLE);
                                cargarDatos(urlP, idlocal, pagina, palabra, base_datos,usuario_base,clave_base);
                                btn_vermas.setVisibility(View.GONE);
                            }
                        });


                    }

                }

                if (scrollY < oldScrollY) {

                    cargamas = false;
                    pb_cargamas.setVisibility(View.GONE);
                    btn_vermas.setVisibility(View.GONE);
                }

            }
        });

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                npagina = 1;
                pb_cargamas.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txt_busca.getWindowToken(), 0);
                DataAdapterClassList.clear();
                palabra = txt_busca.getText().toString();
                Log.e("BOTON", "DATOS: " + "1 :" + palabra + ":");
                cargarDatos(urlP, idlocal, "1", palabra, base_datos,usuario_base,clave_base);
                txt_busca.setText("");
            }
        });

        return vista;
    }


    public void cargarDatos(final String url, final String idlo, final String page, final String buscar, final String base,final String usuario, final String clave) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
                        try {

                            parseData(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("local", idlo);
                params.put("page", page);
                params.put("buscar", buscar);
                params.put("base", base);
                params.put("usuario", usuario);
                params.put("clave", clave);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) throws JSONException {

        llProgressBar.setVisibility(View.GONE);
        pb_cargamas.setVisibility(View.GONE);
        JSONObject json = null;

        json = new JSONObject(response);

        JSONArray jsonArray = json.optJSONArray("result");

        if (jsonArray.length() < 1) {
            Toast.makeText(getContext(), "No hay productos", Toast.LENGTH_SHORT).show();
        } else {

            for (int i = 0; i < jsonArray.length(); i++) {

                DataProducto GetDataAdapter2 = new DataProducto();

                JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                GetDataAdapter2.setProducto(jsonArrayChild.getString("nproducto"));

                //GetDataAdapter2.setData(jsonArrayChild.getString("imagen"));
                GetDataAdapter2.setImageurl(jsonArrayChild.getString("imagen"));

                GetDataAdapter2.setPrecio(jsonArrayChild.getString("precio"));
                GetDataAdapter2.setIdlocal(jsonArrayChild.getString("idlocal"));
                GetDataAdapter2.setDetalle(jsonArrayChild.getString("detalle"));
                GetDataAdapter2.setIva(jsonArrayChild.getString("iva"));
                GetDataAdapter2.setId(jsonArrayChild.getString("idproducto"));


                GetDataAdapter2.setIdcategoria(idca);

                ListViewClickItemArray.add(jsonArrayChild.getString("nproducto"));
                //ListViewClickItemArray1.add(jsonArrayChild.getString("imagen"));
                ListViewClickItemArray1.add(jsonArrayChild.getString("imagen"));
                ListViewClickItemArray2.add(jsonArrayChild.getString("detalle"));
                ListViewClickItemArray3.add(jsonArrayChild.getString("precio"));
                ListViewClickItemArray4.add(jsonArrayChild.getString("iva"));
                ListViewClickItemArray5.add(jsonArrayChild.getString("idproducto"));


                //Adding subject name here to show on click event.
                //EstadoC.add(jsonArrayChild.getString("fecha"));

                DataAdapterClassList.add(GetDataAdapter2);

            }
            npagina = npagina + 1;
            pagina = String.valueOf(npagina);

            recyclerViewadapter = new AdapterProducto(getContext(), DataAdapterClassList,idciudad, idpedidoInterno,rutaGlobal);
            recyclerView.setAdapter(recyclerViewadapter);
            //llProgressBar.setVisibility(View.GONE);
        }
    }

    public void dialogoCancelar() {

        new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setMessage(Html.fromHtml("<H3><font color='#C62828'>¿Seguro que desea cancelar el pedido?</font></H3>"))
                .setPositiveButton("(OK)Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("boton", "0");
                        editorn.commit();

                        //eliminaPedido(urlElimina, pedidoglobal, base_datos);
                        mydb.eliminaPedido(idpedidoInterno);

                        getActivity().getSupportFragmentManager().beginTransaction().
                                remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
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

                super.onPostExecute(result);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp,base);
    }

    public void insertaPedido(final String ServerURL, final String idlocal, final String user, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("local", idlocal));
                paramsn.add(new BasicNameValuePair("ruc", user));
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

                super.onPostExecute(result);

                SharedPreferences prefsn;
                String idpedido, rucusu;
                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        idpedido = (jsonArrayChild.getString("idpedido"));
                        txtnpedido.setText(idpedido);
                        pedidoglobal = idpedido;

                        prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("idpedido", idpedido);
                        editorn.commit();



                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idlocal, user, base);
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

    public Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);

        color = 0xff424242;
        paint = new Paint();
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        rectF = new RectF(rect);
        roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return result;
    }
}
