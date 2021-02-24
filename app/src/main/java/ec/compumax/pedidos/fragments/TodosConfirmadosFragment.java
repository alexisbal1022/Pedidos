package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.util.ArrayList;
import java.util.List;

import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterHistorial;
import ec.compumax.pedidos.Recycler.DataHistorial;

public class TodosConfirmadosFragment extends Fragment {

    View vista;

    String urlHistorial = "https://app.pedidosplus.com/wsProvincias/carga_historialptodos.php";

    List<DataHistorial> DataAdapterClassList;
    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerViewadapter;
    View ChildView;
    int RecyclerViewClickedItemPOS;

    ArrayList<String> ListViewClickItemArray = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray1 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray2 = new ArrayList<String>();
    ArrayList<String> ListViewClickItemArray3 = new ArrayList<String>();

    String rucusu;

    ProgressBar progressBar1;
    EditText txt_busca;
    LinearLayout llProgressBar;

    Button btn_todos;
    FrameLayout content_historial;

    String ciudadid, base_datos,usuario_base, clave_base,rutaGlobal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_confirmados, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        rucusu = prefs.getString("loginusu", "");
        ciudadid = prefs.getString("ciudad", "");
        base_datos = prefs.getString("base_datos", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_historialp);
        progressBar1 = (ProgressBar) vista.findViewById(R.id.progressBar1);
        txt_busca = (EditText)vista.findViewById(R.id.txt_buscar);
        llProgressBar = (LinearLayout)vista.findViewById(R.id.llProgressBar);
        btn_todos = (Button)vista.findViewById(R.id.btn_todos);
        content_historial = (FrameLayout) vista.findViewById(R.id.content_historial);


        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.fondogris)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        content_historial.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        btn_todos.setVisibility(View.GONE);

        llProgressBar.setVisibility(View.VISIBLE);
        DataAdapterClassList = new ArrayList<>();

        mostrarData(urlHistorial,rucusu,base_datos,usuario_base,clave_base);

        Configuration config = getResources().getConfiguration();


        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

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

                    String idpedidoh = ListViewClickItemArray.get(RecyclerViewClickedItemPOS);
                    String idlocalh = ListViewClickItemArray1.get(RecyclerViewClickedItemPOS);
                    String nomimg = ListViewClickItemArray2.get(RecyclerViewClickedItemPOS);
                    String nomlocal = ListViewClickItemArray3.get(RecyclerViewClickedItemPOS);

                    SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorn = prefsn.edit();
                    editorn.putString("idpedidoh", idpedidoh);
                    editorn.putString("local", idlocalh);
                    editorn.putString("nomimg", nomimg);
                    editorn.putString("nomlocal", nomlocal);
                    Log.e("localH","este "+idlocalh);
                    editorn.commit();

                    getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_historial,
                            new HPedidoFragment()).commit();

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

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbarh);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis Pedidos Confirmados");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_historial)).commit();
            }
        });

        txt_busca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                //((AdapterHistorial) recyclerViewadapter).getFilter().filter(charSequence);
                if (((AdapterHistorial) recyclerViewadapter) != null){
                    ((AdapterHistorial) recyclerViewadapter).getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return vista;
    }


    public void mostrarData(final String ServerURL, final String idcli, final String base,final String usuario, final String clave){

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idcliente", idcli));
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

                    if(jsonArray.length()==0){
                        Snackbar snackbar = Snackbar.make(getView(), "No existen datos para mostrar", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        DataHistorial GetDataAdapter2 = new DataHistorial();

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        GetDataAdapter2.setFecha(jsonArrayChild.getString("fecha"));
                        GetDataAdapter2.setImagenurl(jsonArrayChild.getString("logo"));
                        GetDataAdapter2.setLocal(jsonArrayChild.getString("nombre"));
                        GetDataAdapter2.setTotal(jsonArrayChild.getString("total"));
                        GetDataAdapter2.setFecha(jsonArrayChild.getString("fecha"));
                        GetDataAdapter2.setIdcategoria(jsonArrayChild.getString("idcategoria"));
                        GetDataAdapter2.setIdlocal(jsonArrayChild.getString("idlocal"));

                        ListViewClickItemArray.add(jsonArrayChild.getString("idpedido"));
                        ListViewClickItemArray1.add(jsonArrayChild.getString("idlocal"));
                        ListViewClickItemArray2.add(jsonArrayChild.getString("logo"));
                        ListViewClickItemArray3.add(jsonArrayChild.getString("nombre"));

                        DataAdapterClassList.add(GetDataAdapter2);

                    }

                    recyclerViewadapter = new AdapterHistorial(getContext(),DataAdapterClassList, ciudadid,rutaGlobal);

                    recyclerView.setAdapter(recyclerViewadapter);

                    llProgressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idcli,base,usuario,clave);
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
