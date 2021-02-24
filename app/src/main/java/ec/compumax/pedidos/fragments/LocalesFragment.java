package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

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
import ec.compumax.pedidos.Recycler.AdapterLocal;
import ec.compumax.pedidos.Recycler.DataLocal;

public class LocalesFragment extends Fragment {

    private static final String TAG = "LocalesFragment";
    View vista;

    List<DataLocal> DataAdapterClassList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    View ChildView;

    //String urlC = "https://guiapuyo.com/webservice_pedidos/carga_locales.php";
    String urlC = "https://app.pedidosplus.com/wsProvincias/carga_locales_todo_f.php";
    //String urlP = "https://app.pedidosplus.com/webservice_pedidos/wsProvincias/inserta_pedido.php";
    String idca, nomcat;
    int RecyclerViewClickedItemPOS;
    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray1 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray2 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray3 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray4 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray5 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray6 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray7 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray8 = new ArrayList<String>();
    ImageView mProductImage;

    TextView glo_idpedidon, txt_ncat, txt_dia;

    ProgressBar pBar1;
    EditText txt_busca;
    LinearLayout llProgressBar;
    String actualf, actualh;
    Date date1, date2, dateNueva, date3;

    int glo_estadolocal = 0;

    CoordinatorLayout coor_locales;

    String login, ciudadid, base_datos,usuario_base, clave_base;
    DBHelperPedidos mydb;

    String correoplus,rutaGlobal;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_locales, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);
        mydb.eliminaPedidofin();

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        idca = prefs.getString("categ", "");
        nomcat = prefs.getString("nomcat", "");
        login = prefs.getString("loginusu", "");
        ciudadid = prefs.getString("ciudad", "");
        //ciudadid = prefs.getString("ciudad", "");
        base_datos = prefs.getString("base_datos", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_locales);
        glo_idpedidon = (TextView) vista.findViewById(R.id.glo_idpedidon);
        txt_ncat = (TextView) vista.findViewById(R.id.txt_ncat);
        txt_dia = (TextView) vista.findViewById(R.id.txt_dia);
        pBar1 = (ProgressBar) vista.findViewById(R.id.progressBar1);
        txt_busca = (EditText) vista.findViewById(R.id.txt_buscar);
        llProgressBar = (LinearLayout) vista.findViewById(R.id.llProgressBar);
        coor_locales = (CoordinatorLayout) vista.findViewById(R.id.coor_locales);


        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.fondopedidosplus)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        coor_locales.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        /*
        Glide.with(getContext())
                .load(R.drawable.fondogris)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        coor_locales.setBackground(drawable);
                    }
                });

*/
        llProgressBar.setVisibility(View.VISIBLE);

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


        txt_ncat.setText(nomcat);

        mostrarData(urlC, idca, login, ciudadid, base_datos,usuario_base,clave_base);

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

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                final DataLocal GetDataAdapter2 = DataAdapterClassList.get(position);
                ImageButton favoritoimg = (ImageButton) view.findViewById(R.id.btn_favorito);
                ImageView imglocal = (ImageView) view.findViewById(R.id.img_local);
                CardView cvlocal = (CardView) view.findViewById(R.id.cv_localclic);
                TextView txt_local = (TextView) view.findViewById(R.id.txt_local);
                TextView txt_dia = (TextView) view.findViewById(R.id.txt_dia);
                ImageView menuDots = (ImageView) view.findViewById(R.id.menuDots);
                TextView txt_recargo = (TextView) view.findViewById(R.id.txt_recargo);
                //RelativeLayout rl_local = (RelativeLayout) view.findViewById(R.id.rl_local);
                CardView cv_localclic = (CardView) view.findViewById(R.id.cv_localclic);

                cv_localclic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String localrecargo = ListViewClickItemArray3.get(position);
                        String latitud = ListViewClickItemArray4.get(position);
                        String longitud = ListViewClickItemArray5.get(position);
                        String estado_local = ListViewClickItemArray6.get(position);
                        String telefono = ListViewClickItemArray8.get(position);
                        int fpago = Integer.parseInt(ListViewClickItemArray7.get(position));

                        String nomlocal = GetDataAdapter2.getLocal();
                        String nomimg = GetDataAdapter2.getImagenurl();
                        String idlocal = GetDataAdapter2.getIdloc();
                        String fav = GetDataAdapter2.getFavorito();

                        SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editorn = prefsn.edit();
                        editorn.putString("local", idlocal);
                        editorn.putString("categ", idca);
                        editorn.putString("nomimg", nomimg);
                        editorn.putString("nomlocal", nomlocal);
                        editorn.putString("localrecargo", localrecargo);
                        editorn.putString("latitud", latitud);
                        editorn.putString("longitud", longitud);
                        editorn.putString("telefonoloc", telefono);
                        editorn.putInt("fpago", fpago);
                        editorn.commit();

                        Log.e("LOCALES", "FPAGO: "+fpago);

                        if (estado_local.contains("1")) {
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                                // Create new fragment to add (Fragment B)
                                Fragment fragment = new ProductosFragment();
                                fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                                mProductImage = (ImageView) vista.findViewById(R.id.menuDots);

                                // Add Fragment B
                                FragmentTransaction ft = getFragmentManager()
                                        .beginTransaction()
                                        .add(R.id.content_principal, fragment)
                                        .addToBackStack(null);
                                ft.commit();

                            }*/

                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                                    new ProductosFragment()).commit();
                        } else {
                            //Toast.makeText(getContext(), "El local aún esta cerrado", Toast.LENGTH_SHORT).show();
                            dialogoCerrado();
                        }

                    }
                });


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        /*
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

                    String nomlocal = ListViewClickItemArray.get(RecyclerViewClickedItemPOS);
                    String nomimg = ListViewClickItemArray1.get(RecyclerViewClickedItemPOS);
                    String idlocal = ListViewClickItemArray2.get(RecyclerViewClickedItemPOS);
                    String localrecargo = ListViewClickItemArray3.get(RecyclerViewClickedItemPOS);
                    String latitud = ListViewClickItemArray4.get(RecyclerViewClickedItemPOS);
                    String longitud = ListViewClickItemArray5.get(RecyclerViewClickedItemPOS);
                    String estado_local = ListViewClickItemArray6.get(RecyclerViewClickedItemPOS);

                    Log.e("dio clic", "card local " + nomlocal);

                    SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorn = prefsn.edit();
                    editorn.putString("local", idlocal);
                    editorn.putString("categ", idca);
                    editorn.putString("nomimg", nomimg);
                    editorn.putString("nomlocal", nomlocal);
                    editorn.putString("localrecargo", localrecargo);
                    editorn.putString("latitud", latitud);
                    editorn.putString("longitud", longitud);

                    editorn.commit();

                    if(estado_local.contains("1")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            setExitTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                            // Create new fragment to add (Fragment B)
                            Fragment fragment = new ProductosFragment();
                            fragment.setEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(android.R.transition.explode));

                            mProductImage = (ImageView) vista.findViewById(R.id.menuDots);

                            // Add Fragment B
                            FragmentTransaction ft = getFragmentManager()
                                    .beginTransaction()
                                    .add(R.id.content_principal, fragment)
                                    .addToBackStack(null);
                            ft.commit();

                        }
                    }else{
                        Toast.makeText(getContext(),"El local aún esta cerrado",Toast.LENGTH_SHORT).show();
                    }
                    /*
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.content_categoria,
                            new DetalleFragment()).commit();*/
          /*      }

                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView Recyclerview, MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        }); */

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar_local);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(nomcat);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();
            }
        });

        txt_busca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (((AdapterLocal) recyclerViewadapter) != null) {
                    ((AdapterLocal) recyclerViewadapter).getFilter().filter(charSequence);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return vista;
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

    public void mostrarData(final String ServerURL, final String idc, final String login, final String ciudad, final String base, final String usuario, final String clave) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("categ", idc));
                paramsn.add(new BasicNameValuePair("login", login));
                paramsn.add(new BasicNameValuePair("ciudad", ciudad));
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
                try { json = new JSONObject(result);
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
                        GetDataAdapter2.setIdcategoria(idca);
                        String nrecargo = jsonArrayChild.getString("recargo");
                        String fpago = jsonArrayChild.getString("fpago");


                        if (nrecargo.equals("1")) {
                            GetDataAdapter2.setRecargo("Envío Gratis");
                        } else if (nrecargo.equals("0")) {
                            GetDataAdapter2.setRecargo("");
                        }


                        ListViewClickItemArray.add(jsonArrayChild.getString("nombre"));
                        ListViewClickItemArray1.add(jsonArrayChild.getString("logo"));
                        ListViewClickItemArray2.add(jsonArrayChild.getString("idlocal"));
                        ListViewClickItemArray3.add(jsonArrayChild.getString("recargo"));
                        ListViewClickItemArray4.add(jsonArrayChild.getString("latitud"));
                        ListViewClickItemArray5.add(jsonArrayChild.getString("longitud"));
                        ListViewClickItemArray7.add(jsonArrayChild.getString("fpago"));
                        ListViewClickItemArray8.add(jsonArrayChild.getString("contacto"));

                        dian = jsonArrayChild.getString("dia");
                        horai = jsonArrayChild.getString("hinicio");
                        horaf = jsonArrayChild.getString("hfin");

                        dianS = Integer.parseInt(dian)+1;


                        if (actualf.equals(String.valueOf(dianS))) {

                            date2 = dateFormatn.parse(horai);
                            date3 = dateFormatn.parse(horaf);

                            if ((date2.compareTo(date1) <= 0) && (date3.compareTo(date1) >= 0)) {
                                GetDataAdapter2.setColord(Color.parseColor("#00796B"));
                                GetDataAdapter2.setDia("Abierto");
                                ListViewClickItemArray6.add("1");
                            } else {
                                GetDataAdapter2.setDia("Cerrado");
                                GetDataAdapter2.setColord(Color.RED);
                                ListViewClickItemArray6.add("0");
                            }

                        } else {
                            GetDataAdapter2.setDia("Cerrado");
                            GetDataAdapter2.setColord(Color.RED);
                            ListViewClickItemArray6.add("0");
                        }


                        DataAdapterClassList.add(GetDataAdapter2);

                    }

                    recyclerViewadapter = new AdapterLocal(getContext(), DataAdapterClassList, login, base_datos,ciudadid,usuario_base,clave_base,rutaGlobal);
                    recyclerView.setAdapter(recyclerViewadapter);
                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idc,login,ciudad,base,usuario,clave);
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

}
