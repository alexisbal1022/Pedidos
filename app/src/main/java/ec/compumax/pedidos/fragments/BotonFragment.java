package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.AdapterMiPedido;
import ec.compumax.pedidos.Recycler.DataMiPedido;
import ec.compumax.pedidos.Recycler.DataTotales;
import ec.compumax.pedidos.Recycler.DataTotalesI;

public class BotonFragment extends Fragment {

    View vista;
    Button btn_mipedidon;
    LinearLayout ly;

    String botonmi_pedido;

    String urlMipTotales = "https://app.pedidosplus.com/wsProvincias/carga_pedidos.php";
    String idpedidot, base_datos;

    DBHelperPedidos mydb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_boton, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        botonmi_pedido = prefs.getString("boton", "");
        idpedidot = prefs.getString("idpedidoInterno", "");
        base_datos = prefs.getString("base_datos", "");

        Log.e("botonmi_pedido","es: "+botonmi_pedido);

        btn_mipedidon = (Button)vista.findViewById(R.id.mi_pedidon);
        ly = (LinearLayout)vista.findViewById(R.id.ly_btn);

        if(botonmi_pedido.equals("1")){
            //mostrarMipTotalesN(urlMipTotales,idpedidot,base_datos);
            mostrarFacturasin(idpedidot);
            ly.setVisibility(View.VISIBLE);
            Log.e("visible","es: "+botonmi_pedido);
        }else{
            ly.setVisibility(View.INVISIBLE);
            Log.e("invisible","es: "+botonmi_pedido);
        }

        btn_mipedidon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                        new MiPedidoFragment()).commit();
            }
        });


        return vista;
    }

    private void mostrarFacturasin(String pedido) {

        Cursor cursor = mydb.mostrarFactura(pedido);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){

            Double totalf = cursor.getDouble(cursor.getColumnIndex(UtilBD.CAMPO_TOTAL));

            btn_mipedidon.setText("Ver mi pedido ("+totalf+")");

            cursor.moveToNext();
        }

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

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        String totalf = jsonArrayChild.getString("total");

                        btn_mipedidon.setText("Ver mi pedido ("+totalf+")");


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
