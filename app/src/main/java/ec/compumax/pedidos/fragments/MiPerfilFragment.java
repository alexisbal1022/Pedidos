package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

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

import ec.compumax.pedidos.MainActivity;
import ec.compumax.pedidos.R;

public class MiPerfilFragment extends Fragment {

    View vista;
    String urlMF = "https://app.pedidosplus.com/wsProvincias/carga_factura.php";
    String urlPerfil = "https://app.pedidosplus.com/wsProvincias/modifica_perfil.php";
    String loginusu;
    ProgressBar pb_miperfil;

    EditText rucperfil, nombreperfil, dirperfil, telperfil, passperfil, passrperfil;
    TextView usuperfil, nusuperfil;
    Button btn_editar_perfil, btn_habilita_perfil, btn_cancela_perfil;
    LinearLayout llProgressBar;

    String ruc, razon, telefono, direccion, clavep, pass, passr;

    TextInputLayout input_passr;

    //String base_datos,usuario_base, clave_base;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_miperfil, container, false);
        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        loginusu = prefs.getString("loginusu", "");
        clavep = prefs.getString("clavepedido", "");
        //base_datos = prefs.getString("base_datos", "");
        //usuario_base = prefs.getString("usuario_base", "");
        //clave_base = prefs.getString("clave_base", "");

        pb_miperfil = (ProgressBar) vista.findViewById(R.id.pb_miperfil);
        rucperfil = (EditText) vista.findViewById(R.id.rucperfil);
        nombreperfil = (EditText) vista.findViewById(R.id.nombreperfil);
        dirperfil = (EditText) vista.findViewById(R.id.dirperfil);
        telperfil = (EditText) vista.findViewById(R.id.telfperfil);
        usuperfil = (TextView) vista.findViewById(R.id.usuperfil);
        nusuperfil = (TextView) vista.findViewById(R.id.nusuperfil);
        passperfil = (EditText) vista.findViewById(R.id.passperfil);
        passrperfil = (EditText) vista.findViewById(R.id.passrperfil);
        input_passr = (TextInputLayout) vista.findViewById(R.id.input_passr);
        btn_editar_perfil = (Button) vista.findViewById(R.id.btn_editar_perfil);
        btn_habilita_perfil = (Button) vista.findViewById(R.id.btn_habilita_perfil);
        btn_cancela_perfil = (Button) vista.findViewById(R.id.btn_cancela_perfil);
        llProgressBar = (LinearLayout)vista.findViewById(R.id.llProgressBar);
        llProgressBar.setVisibility(View.VISIBLE);

        //pb_miperfil.setVisibility(View.VISIBLE);
        mostrarDatos(urlMF,loginusu);

        passperfil.setText(clavep);
        passrperfil.setText(clavep);
        usuperfil.setText(loginusu);

        btn_habilita_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                habilitaTxt();
            }
        });

        btn_editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llProgressBar.setVisibility(View.VISIBLE);
                obtenerTexto();
                if(razon.isEmpty()||telefono.isEmpty()||direccion.isEmpty()||pass.isEmpty()||passr.isEmpty()){
                    Toast.makeText(getContext(),"Hay campos vacios", Toast.LENGTH_SHORT).show();
                }else{
                    if(passr.equals(pass)){
                        if(validadorDeCedula(ruc,view)){
                            insertaPerfil(urlPerfil,ruc,razon,telefono,direccion,pass,loginusu);
                            deshabilitaTxt();
                        }

                    }else{
                        Toast.makeText(getContext(),"Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

        btn_cancela_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deshabilitaTxt();
            }
        });

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbarperfil);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");

        return vista;
    }

    public void habilitaTxt(){
        btn_editar_perfil.setVisibility(View.VISIBLE);
        btn_cancela_perfil.setVisibility(View.VISIBLE);
        btn_habilita_perfil.setVisibility(View.GONE);
        rucperfil.setEnabled(true);
        nombreperfil.setEnabled(true);
        dirperfil.setEnabled(true);
        telperfil.setEnabled(true);
        passperfil.setEnabled(true);
        passrperfil.setEnabled(true);
        input_passr.setVisibility(View.VISIBLE);
    }


    public void deshabilitaTxt(){
        btn_editar_perfil.setVisibility(View.GONE);
        btn_cancela_perfil.setVisibility(View.GONE);
        btn_habilita_perfil.setVisibility(View.VISIBLE);
        rucperfil.setEnabled(false);
        nombreperfil.setEnabled(false);
        dirperfil.setEnabled(false);
        telperfil.setEnabled(false);
        passperfil.setEnabled(false);
        passrperfil.setEnabled(false);
        input_passr.setVisibility(View.GONE);
    }

    public void obtenerTexto(){
        ruc = rucperfil.getText().toString().trim();
        razon = nombreperfil.getText().toString().trim();
        direccion = dirperfil.getText().toString().trim();
        telefono = telperfil.getText().toString().trim();
        pass = passperfil.getText().toString().trim();
        passr = passrperfil.getText().toString().trim();
    }

    public boolean validadorDeCedula(String cedula, View ver) {

        boolean cedulaCorrecta = false;

        try {

            if (cedula.length() == 10) // ConstantesApp.LongitudCedula
            {
                int tercerDigito = Integer.parseInt(cedula.substring(2, 3));
                if (tercerDigito < 6) {
                    // Coeficientes de validación cédula
                    // El decimo digito se lo considera dígito verificador
                    int[] coefValCedula = {2, 1, 2, 1, 2, 1, 2, 1, 2};
                    int verificador = Integer.parseInt(cedula.substring(9, 10));
                    int suma = 0;
                    int digito = 0;
                    for (int i = 0; i < (cedula.length() - 1); i++) {
                        digito = Integer.parseInt(cedula.substring(i, i + 1)) * coefValCedula[i];
                        suma += ((digito % 10) + (digito / 10));
                    }

                    if ((suma % 10 == 0) && (suma % 10 == verificador)) {
                        cedulaCorrecta = true;
                    } else if ((10 - (suma % 10)) == verificador) {
                        cedulaCorrecta = true;
                    } else {
                        cedulaCorrecta = false;
                    }
                } else {
                    cedulaCorrecta = false;
                }
            } else {
                cedulaCorrecta = false;
            }
        } catch (NumberFormatException nfe) {
            cedulaCorrecta = false;
        } catch (Exception err) {
            System.out.println("Una excepcion ocurrio en el proceso de validadcion");
            cedulaCorrecta = false;
        }

        if (!cedulaCorrecta) {
            //Toast.makeText(Registrar.this, "La cédula ingresada es incorrecta", Toast.LENGTH_LONG).show();
            Snackbar snackbar = Snackbar.make(ver, "La cédula ingresada es incorrecta", Snackbar.LENGTH_LONG);
            View snackbarLayout = snackbar.getView();
            snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
            TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
            snackbar.show();
            llProgressBar.setVisibility(View.GONE);
        }
        return cedulaCorrecta;
    }

    public void mostrarDatos(final String ServerURL, final String user) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("user", user));
                //paramsn.add(new BasicNameValuePair("base", base));
                //paramsn.add(new BasicNameValuePair("usuario", usuario));
                //paramsn.add(new BasicNameValuePair("clave", clave));

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

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        nusuperfil.setText(jsonArrayChild.getString("name"));
                        rucperfil.setText(jsonArrayChild.getString("ruc"));
                        nombreperfil.setText(jsonArrayChild.getString("razonsocial"));
                        dirperfil.setText(jsonArrayChild.getString("direccion"));
                        telperfil.setText(jsonArrayChild.getString("telefono"));

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

    public void insertaPerfil(final String ServerURL, final String ruc, final String razon, final String telf, final String dir, final String pass, final String user) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();


                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("dir", dir));
                paramsn.add(new BasicNameValuePair("telf", telf));
                paramsn.add(new BasicNameValuePair("pass", pass));
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
                super.onPostExecute(result);
                llProgressBar.setVisibility(View.GONE);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(ruc, razon, telf, dir,pass, user);
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
