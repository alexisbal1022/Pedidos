package ec.compumax.pedidos.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

import ec.compumax.pedidos.Informacion;
import ec.compumax.pedidos.MainActivity;
import ec.compumax.pedidos.Olvide;
import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterFavorito;
import ec.compumax.pedidos.Recycler.AdapterLocal;
import ec.compumax.pedidos.Recycler.DataAdapter;
import ec.compumax.pedidos.Recycler.DataLocal;
import ec.compumax.pedidos.Recycler.RecyclerViewAdapter;

public class CategoriasFragment extends Fragment {

    private static final String TAG = "CATEGORIAFRAGMENT";
    View vista;

    List<DataAdapter> DataAdapterClassList;
    List<DataLocal> DataAdapterClassListf;
    RecyclerView recyclerView, recyclerViewf;
    RecyclerView.Adapter recyclerViewadapter, recyclerViewadapterf;
    View ChildView;
    int RecyclerViewClickedItemPOS;

    //String urlC = "https://app.pedidosplus.com/webservice_pedidos/carga_categoria.php";
    String urlC = "https://app.pedidosplus.com/wsProvincias/carga_categoria.php";
    //String urlF = "https://app.pedidosplus.com/webservice_pedidos/carga_favoritos.php";
    String urlF = "https://app.pedidosplus.com/wsProvincias/carga_favoritos.php";

    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray1 = new ArrayList<String>();

    ArrayList<String> ListViewClickItemArrayf = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray1f = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray2f = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray3f = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray4f = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray5f = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray6f = new ArrayList<String>();

    ProgressBar pBar1;
    ImageButton btn_salir;
    EditText txt_busca;
    LinearLayout llProgressBar;

    Date date1, date2, dateNueva, date3;
    String actualf, actualh, login, base_datos, ciudadid, usuario_base, clave_base;

    int loginon, loginface;

    String rutaGlobal;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_categorias, container, false);
        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        login = prefs.getString("loginusu", "");
        base_datos = prefs.getString("base_datos", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");
        loginon = prefs.getInt("logoplus", 0);
        ciudadid = prefs.getString("ciudad", "");
        loginface = prefs.getInt("loginface", 0);
        rutaGlobal = prefs.getString("rutaGlobal", "");

        cargarRuta();

        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_categoria);
        recyclerViewf = (RecyclerView) vista.findViewById(R.id.recycler_favoritos);
        pBar1 = (ProgressBar) vista.findViewById(R.id.progressBar1);
        btn_salir = (ImageButton) vista.findViewById(R.id.btn_salir);
        txt_busca = (EditText) vista.findViewById(R.id.txt_buscar);
        llProgressBar = (LinearLayout) vista.findViewById(R.id.llProgressBar);

        Date date = new Date();
        //DateFormat dateFormat = new SimpleDateFormat("u");
        //actualf = dateFormat.format(date);

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

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbarcateg);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Inicio");

        DataAdapterClassList = new ArrayList<>();
        DataAdapterClassListf = new ArrayList<>();

        llProgressBar.setVisibility(View.VISIBLE);

        mostrarData(urlC, base_datos, usuario_base, clave_base);
        mostrarFavorito(urlF, login, base_datos, usuario_base, clave_base, ciudadid);

        Configuration config = getResources().getConfiguration();


        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //Toast.makeText(getContext(), "landscape", Toast.LENGTH_SHORT).show();
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
            recyclerViewf.setLayoutManager(new GridLayoutManager(getContext(), 4));


        } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //Toast.makeText(getContext(), "portrait", Toast.LENGTH_SHORT).show();
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
            recyclerViewf.setLayoutManager(new GridLayoutManager(getContext(), 3));

        }

        recyclerViewf.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerViewf, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                final DataLocal GetDataAdapter2 = DataAdapterClassListf.get(position);
                ImageButton favoritoimg = (ImageButton) view.findViewById(R.id.btn_favorito);
                ImageView imglocal = (ImageView) view.findViewById(R.id.img_local);
                CardView cvlocal = (CardView) view.findViewById(R.id.cv_localclic);
                TextView txt_local = (TextView) view.findViewById(R.id.txt_local);
                TextView txt_dia = (TextView) view.findViewById(R.id.txt_dia);
                ImageView menuDots = (ImageView) view.findViewById(R.id.menuDots);
                TextView txt_recargo = (TextView) view.findViewById(R.id.txt_recargo);
                //RelativeLayout rl_local=(RelativeLayout)view.findViewById(R.id.rl_local);
                CardView cv_localclic = (CardView) view.findViewById(R.id.cv_localclic);

                cv_localclic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String localrecargo = ListViewClickItemArray3f.get(position);
                        String latitud = ListViewClickItemArray4f.get(position);
                        String longitud = ListViewClickItemArray5f.get(position);
                        String estado_local = ListViewClickItemArray6f.get(position);

                        String nomlocal = GetDataAdapter2.getLocal();
                        String nomimg = GetDataAdapter2.getImagenurl();
                        String idlocal = GetDataAdapter2.getIdloc();
                        String fav = GetDataAdapter2.getFavorito();
                        String categ = GetDataAdapter2.getIdcategoria();

                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("local", idlocal);
                        editorn.putString("categ", categ);
                        editorn.putString("nomimg", nomimg);
                        editorn.putString("nomlocal", nomlocal);
                        editorn.putString("localrecargo", localrecargo);
                        editorn.putString("latitud", latitud);
                        editorn.putString("longitud", longitud);

                        editorn.commit();

                        if (estado_local.contains("1")) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                                // Create new fragment to add (Fragment B)
                                Fragment fragment = new ProductosFragment();
                                fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));


                                // Add Fragment B
                                FragmentTransaction ft = getFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_principal, fragment)
                                        .addToBackStack(null);
                                ft.commit();

                            }
                        } else {
                            //Toast.makeText(getContext(),"El local aún esta cerrado",Toast.LENGTH_SHORT).show();
                            dialogoCerrado();
                        }

                    }
                });


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            GestureDetector gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent motionEvent) {

                    return true;
                }

            });

            @Override
            public boolean onInterceptTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

                ChildView = Recyclerview.findChildViewUnder(motionEvent.getX(), motionEvent.getY());

                if (ChildView != null && gestureDetector.onTouchEvent(motionEvent)) {

                    RecyclerViewClickedItemPOS = Recyclerview.getChildAdapterPosition(ChildView);
                    String idc = ListViewClickItemArray.get(RecyclerViewClickedItemPOS);
                    String nca = ListViewClickItemArray1.get(RecyclerViewClickedItemPOS);

                    SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorn = prefsn.edit();
                    editorn.putString("categ", idc);
                    editorn.putString("nomcat", nca);
                    editorn.commit();

                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                            new LocalesFragment()).commit();

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

        btn_salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("logoplus", 0);
                editor.commit();
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //updateUI(null);
                            }
                        });
                startActivity(new Intent(getContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));

                ((Activity) getContext()).finish();

            }
        });

        /*txt_busca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                ((RecyclerViewAdapter) recyclerViewadapter).getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/


        return vista;
    }

    private void cargarRuta() {
        db.collection("pais")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("ruta") != null) {
                                    rutaGlobal = doc.getString("ruta");
                                    Log.e(TAG, "onEvent: " + rutaGlobal);
                                    SharedPreferences prefsn = getContext().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorn = prefsn.edit();
                                    editorn.putString("rutaGlobal", rutaGlobal);
                                    editorn.commit();
                                }
                            }
                        }
                    }
                });
    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {

        } else {
            //goLoginScreen();
        }
    }

    public void mostrarData(final String ServerURL, final String base, final String usuario, final String clave) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

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
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataAdapter GetDataAdapter2 = new DataAdapter();

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        GetDataAdapter2.setCategoria(jsonArrayChild.getString("ncategoria"));

                        //GetDataAdapter2.setData(jsonArrayChild.getString("imagen"));
                        GetDataAdapter2.setImagenurl(jsonArrayChild.getString("imagen"));
                        GetDataAdapter2.setIdcat(jsonArrayChild.getString("idcategoria"));

                        ListViewClickItemArray.add(jsonArrayChild.getString("idcategoria"));
                        ListViewClickItemArray1.add(jsonArrayChild.getString("ncategoria"));


                        DataAdapterClassList.add(GetDataAdapter2);

                    }

                    //progressBar.setVisibility(View.GONE);

                    recyclerViewadapter = new RecyclerViewAdapter(getContext(), DataAdapterClassList, ciudadid, rutaGlobal);

                    recyclerView.setAdapter(recyclerViewadapter);

                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(base, usuario, clave);
    }

    public void mostrarFavorito(final String ServerURL, final String login, final String base, final String usuario, final String clave, final String ciudad) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("login", login));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuario));
                paramsn.add(new BasicNameValuePair("clave", clave));
                paramsn.add(new BasicNameValuePair("ciudad", ciudad));

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
                try {
                    json = new JSONObject(result);
                    String dian, horai, horaf;
                    int dianS;
                    DateFormat dateFormatn = new SimpleDateFormat("HH:mm");


                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataLocal GetDataAdapter2 = new DataLocal();

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        GetDataAdapter2.setLocal(jsonArrayChild.getString("nombre"));
                        //GetDataAdapter2.setData(jsonArrayChild.getString("logo"));
                        GetDataAdapter2.setImagenurl(jsonArrayChild.getString("logo"));
                        GetDataAdapter2.setId(jsonArrayChild.getString("telefono"));
                        GetDataAdapter2.setIdloc(jsonArrayChild.getString("idlocal"));
                        GetDataAdapter2.setFavorito(jsonArrayChild.getString("favorito"));
                        GetDataAdapter2.setIdcategoria(jsonArrayChild.getString("idcategoria"));
                        String nrecargo = jsonArrayChild.getString("recargo");
                        //String nfavorito = jsonArrayChild.getString("favorito");

                        Log.e("recargo", nrecargo);

                        if (nrecargo.equals("1")) {
                            Log.e("recargo", "con");
                            GetDataAdapter2.setRecargo("Envío Gratis");
                        } else if (nrecargo.equals("0")) {
                            GetDataAdapter2.setRecargo("");
                            Log.e("recargo", "sin");
                        }


                        ListViewClickItemArrayf.add(jsonArrayChild.getString("nombre"));
                        ListViewClickItemArray1f.add(jsonArrayChild.getString("logo"));
                        ListViewClickItemArray2f.add(jsonArrayChild.getString("idlocal"));
                        ListViewClickItemArray3f.add(jsonArrayChild.getString("recargo"));
                        ListViewClickItemArray4f.add(jsonArrayChild.getString("latitud"));
                        ListViewClickItemArray5f.add(jsonArrayChild.getString("longitud"));

                        dian = jsonArrayChild.getString("dia");
                        horai = jsonArrayChild.getString("hinicio");
                        horaf = jsonArrayChild.getString("hfin");

                        dianS = Integer.parseInt(dian) + 1;

                        if (actualf.equals(String.valueOf(dianS))) {

                            date2 = dateFormatn.parse(horai);
                            date3 = dateFormatn.parse(horaf);

                            Log.e("date2", "" + date2);
                            Log.e("date3", "" + date3);

                            if ((date2.compareTo(date1) <= 0) && (date3.compareTo(date1) >= 0)) {
                                GetDataAdapter2.setColord(Color.parseColor("#00796B"));
                                GetDataAdapter2.setDia("Abierto");
                                Log.e("date1", "" + date1);
                                ListViewClickItemArray6f.add("1");
                            } else {
                                GetDataAdapter2.setDia("Cerrado");
                                GetDataAdapter2.setColord(Color.RED);
                                Log.e("date1c", "" + date1);
                                ListViewClickItemArray6f.add("0");
                            }

                        } else {
                            GetDataAdapter2.setDia("Cerrado");
                            GetDataAdapter2.setColord(Color.RED);
                            Log.e("dia", "" + dianS);
                            Log.e("diac", "" + actualf);
                            ListViewClickItemArray6f.add("0");
                        }

                        Log.e("resval", "est vacio" + jsonArrayChild.getString("nombre"));

                        //Adding subject name here to show on click event.
                        //EstadoC.add(jsonArrayChild.getString("fecha"));

                        DataAdapterClassListf.add(GetDataAdapter2);

                    }

                    recyclerViewadapterf = new AdapterFavorito(getContext(), DataAdapterClassListf, login, base_datos, ciudadid, usuario_base, clave_base, rutaGlobal);
                    recyclerViewf.setAdapter(recyclerViewadapterf);
                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login, base, usuario, clave, ciudad);
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


    public static interface ClickListener {
        public void onClick(View view, int position);

        public void onLongClick(View view, int position);
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clicklistener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recycleView, final ClickListener clicklistener) {

            this.clicklistener = clicklistener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recycleView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clicklistener != null) {
                        clicklistener.onLongClick(child, recycleView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clicklistener != null && gestureDetector.onTouchEvent(e)) {
                clicklistener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void dialogoCerrado() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setIcon(R.drawable.ic_error_outline_black_24dp)
                .setTitle("Local Cerrado")
                .setMessage("Lo sentimos por el momento este local está cerrado")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }
}
