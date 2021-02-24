package ec.compumax.pedidos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import ec.compumax.pedidos.Otros.BottomNavigationBehavior;
import ec.compumax.pedidos.Otros.BottomNavigationViewBehavior;
import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.fragments.AyudaFragment;
import ec.compumax.pedidos.fragments.CategoriasFragment;
import ec.compumax.pedidos.fragments.DetalleFragment;
import ec.compumax.pedidos.fragments.EstadosFragment;
import ec.compumax.pedidos.fragments.FavoritosFragment;
import ec.compumax.pedidos.fragments.HistorialFragment;
import ec.compumax.pedidos.fragments.MiPerfilFragment;
import ec.compumax.pedidos.fragments.PendientesFragment;

public class Menu extends AppCompatActivity {

    private static final String TAG = "MENU";
    String idpedido;
    //String urlElimina = "https://app.pedidosplus.com/webservice_pedidos/elimina_pedido.php";
    String urlElimina = "https://app.pedidosplus.com/wsProvincias/elimina_pedido.php";
    //String urlTelefonos = "https://app.pedidosplus.com/webservice_pedidos/carga_configura.php";
    String urlTelefonos = "https://app.pedidosplus.com/wsProvincias/carga_configura.php";

    int loginon;

    //private TextView mTextMessage;
    String base_datos, usuario_base, clave_base;
    int loginface, logingoogle;
    String loginusu;

    DBHelperPedidos mydb;

    String rutaGlobal;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;

    {
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {

                    case R.id.navigation_home:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new CategoriasFragment()).commit();

                        /*
                        SharedPreferences prefs1 = getSharedPreferences("Preferences", 0);
                        String botonmi_pedidon = prefs1.getString("boton", "");
                        idpedido = prefs1.getString("idpedido", "");

                        Log.e("idpedido","menu: "+idpedido);

                        Log.e("botonmi_pedidon",": "+botonmi_pedidon);
                        Log.e("idped",": "+idpedido);

                        if(botonmi_pedidon.equals("1")){
                            dialogoCancelarInicio();
                            item.setChecked(false);
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new CategoriasFragment()).commit();
                        }

                         */
                        /*fl.setVisibility(View.VISIBLE);
                        fl2.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction()
                                .show(new CategoriasFragment())
                                .commit();*/

                        return true;
                    case R.id.navigation_dashboard:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new EstadosFragment()).commit();

                        /*
                        SharedPreferences prefs4 = getSharedPreferences("Preferences", 0);
                        String botonmi_pedidon4 = prefs4.getString("boton", "");
                        idpedido = prefs4.getString("idpedido", "");

                        Log.e("idpedido","menu: "+idpedido);

                        Log.e("botonmi_pedidon",": "+botonmi_pedidon4);
                        Log.e("idped",": "+idpedido);

                        if(botonmi_pedidon4.equals("1")){
                            dialogoCancelar();
                        }else {
                            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                  //new PendientesFragment()).commit();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new EstadosFragment()).commit();
                        }
                         */

                        /*fl2.setVisibility(View.VISIBLE);
                        fl.setVisibility(View.GONE);

                        getSupportFragmentManager().beginTransaction()
                                .show(new HistorialFragment())
                                .commit();*/

                        return true;
                    case R.id.navigation_notifications:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new MiPerfilFragment()).commit();

                        /*
                        SharedPreferences prefs3 = getSharedPreferences("Preferences", 0);
                        String botonmi_pedidon3 = prefs3.getString("boton", "");
                        idpedido = prefs3.getString("idpedido", "");

                        Log.e("idpedido","menu: "+idpedido);

                        Log.e("botonmi_pedidon",": "+botonmi_pedidon3);
                        Log.e("idped",": "+idpedido);

                        if(botonmi_pedidon3.equals("1")){
                            dialogoCancelarInicio();
                            item.setChecked(false);
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new MiPerfilFragment()).commit();
                        }

                         */
                        return true;


                    case R.id.navigation_favoritos:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new FavoritosFragment()).commit();

                        /*
                        SharedPreferences prefs2 = getSharedPreferences("Preferences", 0);
                        String botonmi_pedidon2 = prefs2.getString("boton", "");
                        idpedido = prefs2.getString("idpedido", "");

                        Log.e("idpedido","menu: "+idpedido);

                        Log.e("botonmi_pedidon",": "+botonmi_pedidon2);
                        Log.e("idped",": "+idpedido);

                        if(botonmi_pedidon2.equals("1")){
                            dialogoCancelar();
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new FavoritosFragment()).commit();
                        }

                         */
                        return true;


                    case R.id.navigation_ayuda:

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new AyudaFragment()).commit();
                        /*

                        SharedPreferences prefs5 = getSharedPreferences("Preferences", 0);
                        String botonmi_pedidon5 = prefs5.getString("boton", "");
                        idpedido = prefs5.getString("idpedido", "");

                        Log.e("idpedido","menu: "+idpedido);

                        Log.e("botonmi_pedidon",": "+botonmi_pedidon5);
                        Log.e("idped",": "+idpedido);

                        if(botonmi_pedidon5.equals("1")){
                            dialogoCancelar();
                        }else {
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                    new AyudaFragment()).commit();
                        }

                         */
                        return true;
                }
                return false;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        mydb = new DBHelperPedidos(this, UtilBD.NOMBRE_BD, null, 1);
        cargarRuta();

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        loginon = prefs.getInt("logoplus", 0);
        base_datos = prefs.getString("base_datos", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");
        loginface = prefs.getInt("loginface", 0);
        logingoogle = prefs.getInt("logingoogle", 0);
        loginusu = prefs.getString("loginusu", "");

        //mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navView.setItemIconTintList(null);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                    new CategoriasFragment()).commit();
            //getSupportFragmentManager().beginTransaction().add(R.id.fragment_container2,
            //      new HistorialFragment()).commit();
        }

        cargaTelefonos(urlTelefonos, base_datos, usuario_base, clave_base);
    }

    private void cargarRuta() {
        db.collection("pais")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("ruta") != null) {
                                    rutaGlobal=doc.getString("ruta");
                                    Log.e(TAG, "onEvent: "+rutaGlobal);
                                    SharedPreferences prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorn = prefsn.edit();
                                    editorn.putString("rutaGlobal", rutaGlobal);
                                    editorn.commit();
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
    }


    public void dialogoCancelar() {

        new AlertDialog.Builder(Menu.this)
                .setMessage(Html.fromHtml("<H3><font color='#C62828'>¿Seguro que desea cancelar el pedido?</font></H3>"))

                .setPositiveButton("(OK)Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("boton", "0");
                        editorn.commit();

                        eliminaPedido(urlElimina, idpedido, base_datos);


                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new HistorialFragment()).commit();


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //mi.setChecked(false);
                    }
                })
                .show();
    }

    public void dialogoCancelarInicio() {

        new AlertDialog.Builder(Menu.this)
                .setMessage(Html.fromHtml("<H3><font color='#C62828'>¿Seguro que desea cancelar el pedido?</font></H3>"))

                .setPositiveButton("(OK)Salir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("boton", "0");
                        editorn.commit();

                        eliminaPedido(urlElimina, idpedido, base_datos);


                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                new CategoriasFragment()).commit();
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

                Log.e("post", "entro");

                Log.e("resval", "val" + result);
                super.onPostExecute(result);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp, base);
    }


    public void cargaTelefonos(final String ServerURL, final String base, final String usuariobase, final String clavebase) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuariobase));
                paramsn.add(new BasicNameValuePair("clave", clavebase));


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

                Log.e("INSERTO", "entro");
                Log.e("post", "entro");

                Log.e("resval", "val" + result);
                super.onPostExecute(result);

                SharedPreferences prefsn;
                String telfpedidos, telfmotos, distancia, preciokm, preciomin;
                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        telfpedidos = (jsonArrayChild.getString("telfpedidos"));
                        telfmotos = (jsonArrayChild.getString("telflogistica"));
                        distancia = (jsonArrayChild.getString("distancia"));
                        preciokm = (jsonArrayChild.getString("preciokm"));
                        preciomin = (jsonArrayChild.getString("preciomin"));


                        prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("telfpedidos", telfpedidos);
                        editorn.putString("telfmotos", telfmotos);
                        editorn.putString("distancia", distancia);
                        editorn.putString("preciokm", preciokm);
                        editorn.putString("preciomin", preciomin);
                        editorn.commit();


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(base);
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){

        }else {
            if(loginon!=1){
                Intent intent = new Intent(Menu.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
