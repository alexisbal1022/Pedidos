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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
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

public class EstadosFragment extends Fragment {

    View vista;

    Button btn_pendientes, btn_confirmados, btn_encamino, btn_entregados;
    String rucusu;
    CoordinatorLayout coor_estados;
    FrameLayout content_historial;

    String urlContar = "https://app.pedidosplus.com/wsProvincias/carga_contador.php";

    String base_datos, ciudadid,usuario_base, clave_base;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_historial, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        rucusu = prefs.getString("loginusu", "");
        base_datos = prefs.getString("base_datos", "");
        ciudadid = prefs.getString("ciudad", "");
        usuario_base = prefs.getString("usuario_base", "");
        clave_base = prefs.getString("clave_base", "");

        btn_pendientes = (Button)vista.findViewById(R.id.btn_pendientes);
        btn_confirmados = (Button)vista.findViewById(R.id.btn_confirmados);
        btn_encamino = (Button)vista.findViewById(R.id.btn_encamino);
        btn_entregados = (Button)vista.findViewById(R.id.btn_entregados);
        content_historial = (FrameLayout) vista.findViewById(R.id.content_historial);

        /*
        Glide.with(getContext())
                .load(R.drawable.fondogris)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        content_historial.setBackground(drawable);
                    }
                });

         */

        Log.e("estados","fragment");

        mostrarData(urlContar,rucusu, base_datos,usuario_base,clave_base, ciudadid);



        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbares);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mis Pedidos");

        btn_pendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("estado", "1");
                editorn.putString("estadop", "pendientes");
                editorn.commit();
                Log.e("estados","estado 1");
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_historial,
                        //new PendientesFragment()).commit();

                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_historial,
                        new PendientesFragment()).commit();

            }
        });
        btn_confirmados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("estado", "2");
                editorn.putString("estadop", "confirmados");
                editorn.commit();
                Log.e("estados","estado 2");
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_historial,
                        new PendientesFragment()).commit();
            }
        });
        btn_encamino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("estado", "3");
                editorn.putString("estadop", "en camino");
                editorn.commit();
                Log.e("estados","estado 3");
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_historial,
                        new PendientesFragment()).commit();
            }
        });
        btn_entregados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("estados","estado 4");
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_historial,
                        new ConfirmadosFragment()).commit();
            }
        });



        return vista;
    }

    public void mostrarData(final String ServerURL, final String idcli, final String base, final String usuario, final String clave, final String ciudad){

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idcliente", idcli));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("ciudad", ciudad));
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

                String pendiente, confirmado, camino, entregado;

                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        pendiente=(jsonArrayChild.getString("pendientes"));
                        confirmado=(jsonArrayChild.getString("confirmados"));
                        camino=(jsonArrayChild.getString("camino"));
                        entregado=(jsonArrayChild.getString("entregados"));

                        btn_pendientes.setText("Pendientes "+pendiente);
                        btn_confirmados.setText("Confirmados "+confirmado);
                        btn_encamino.setText("En camino "+camino);
                        btn_entregados.setText("Entregados "+entregado);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idcli,base,usuario,clave,ciudad);
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
