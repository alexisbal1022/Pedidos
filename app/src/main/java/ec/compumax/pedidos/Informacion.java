package ec.compumax.pedidos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

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
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ec.compumax.pedidos.fragments.ProvinciaFragment;

import static ec.compumax.pedidos.Registrarse.validaRucEP;
import static ec.compumax.pedidos.Registrarse.validacionCedula;
import static ec.compumax.pedidos.Registrarse.validacionRUC;

public class Informacion extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "Informacion";
    EditText txt_nombre, txt_ruc, txt_razon, txt_direccion, txt_referencia, txt_telefono;
    TextView txt_usuario;
    private ProfileTracker profileTracker;

    Button btn_cancelar, btn_continuar;

    String urlComprobar = "https://app.pedidosplus.com/wsProvincias/comprobar_usuario.php";
    String urlInserta = "https://app.pedidosplus.com/wsProvincias/inserta_usuario.php";
    String usuario, nombre, ruc, razon, direccion, referencia, telefono, token;

    LinearLayout ly_informacion;

    ProgressBar pb_informacion;

    int loginface, logingoogle;

    GoogleApiClient googleApiClient;

    ArrayList<String> provincias;
    ArrayList<String> nidprovincia;
    ArrayList<String> baseprovincia;
    ArrayList<String> usuariobase;
    ArrayList<String> clavebase;
    ArrayList<String> nciudad;
    ArrayList<String> nidciudad;
    ArrayList<String> idpciudad;

    String sEmail, sPass;
    Session session;
    String tokenid;

    Spinner sp_tipo;

    ArrayList<String> ididentifica = new ArrayList<String>();
    ArrayList<String> nombreidentifica = new ArrayList<String>();

    String tipoiden, rutaGlobal;

    private static final int num_provincias = 24;

    private static int[] coeficientes = {4, 3, 2, 7, 6, 5, 4, 3, 2};
    private static int constante = 11;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        token = prefs.getString("tokenid", "");
        loginface = prefs.getInt("loginface", 0);
        logingoogle = prefs.getInt("logingoogle", 0);
        tokenid = prefs.getString("tokenid", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        provincias = (ArrayList<String>) getIntent().getStringArrayListExtra("provincias");
        nidprovincia = (ArrayList<String>) getIntent().getStringArrayListExtra("nidprovincia");
        baseprovincia = (ArrayList<String>) getIntent().getStringArrayListExtra("baseprovincia");
        usuariobase = (ArrayList<String>) getIntent().getStringArrayListExtra("usuariobase");
        clavebase = (ArrayList<String>) getIntent().getStringArrayListExtra("clavebase");
        nidciudad = (ArrayList<String>) getIntent().getStringArrayListExtra("nidciudad");
        nciudad = (ArrayList<String>) getIntent().getStringArrayListExtra("nciudad");
        idpciudad = (ArrayList<String>) getIntent().getStringArrayListExtra("idpciudad");
        sEmail = getIntent().getStringExtra("correoplus");
        sPass = getIntent().getStringExtra("pswdplus");

        txt_usuario = (TextView) findViewById(R.id.txt_usuario);
        txt_nombre = (EditText) findViewById(R.id.txt_nombre);
        txt_ruc = (EditText) findViewById(R.id.txt_ruc);
        txt_razon = (EditText) findViewById(R.id.txt_razon);
        txt_direccion = (EditText) findViewById(R.id.txt_direccion);
        txt_referencia = (EditText) findViewById(R.id.txt_referencia);
        txt_telefono = (EditText) findViewById(R.id.txt_telefono);
        btn_cancelar = (Button) findViewById(R.id.btn_cancelar);
        btn_continuar = (Button) findViewById(R.id.btn_continuar);
        ly_informacion = (LinearLayout) findViewById(R.id.ly_informacion);
        pb_informacion = (ProgressBar) findViewById(R.id.pb_informacion);

        sp_tipo = (Spinner) findViewById(R.id.sp_tipo);

        nombreidentifica.add("Tipo de identificación");
        nombreidentifica.add("Cédula");
        nombreidentifica.add("RUC");
        nombreidentifica.add("Pasaporte");
        ididentifica.add("00");
        ididentifica.add("05");
        ididentifica.add("04");
        ididentifica.add("06");


        sp_tipo.setAdapter(new ArrayAdapter<String>(Informacion.this, android.R.layout.simple_spinner_dropdown_item, nombreidentifica));

        sp_tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                tipoiden = ididentifica.get(position);
                Log.e("REGISTRO", "onItemSelected: " + ididentifica.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                mGoogleSignInClient.signOut().addOnCompleteListener(Informacion.this,
                        new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //updateUI(null);
                            }
                        });
                goLoginScreen();
            }
        });

        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_informacion.setVisibility(View.VISIBLE);

                obtenerDatos();

                if (nombre.isEmpty() || ruc.isEmpty() || razon.isEmpty() || direccion.isEmpty() || referencia.isEmpty() || telefono.isEmpty()) {
                    Toast.makeText(Informacion.this, "Hay campos vacios", Toast.LENGTH_SHORT);
                    pb_informacion.setVisibility(View.GONE);
                } else {
                    if (tipoiden.contains("00")) {
                        Snackbar snackbar = Snackbar.make(v, "Seleccione un tipo de identificación", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                        pb_informacion.setVisibility(View.GONE);
                    } else {
                        if (tipoiden.contains("05")) {
                            if (validadorDeCedula(ruc, v)) {
                                insertaUsuario(urlInserta, usuario, ruc, nombre, razon, telefono, direccion, referencia, token, tipoiden);

                            }
                        } else {

                            if (tipoiden.contains("04")) {

                                if (ruc.length() == 13) {
                                    if (validacionCedula(ruc.substring(0, 10))) {
                                        insertaUsuario(urlInserta, usuario, ruc, nombre, razon, telefono, direccion, referencia, token, tipoiden);

                                    } else {
                                        if (validacionRUC(ruc)) {
                                            insertaUsuario(urlInserta, usuario, ruc, nombre, razon, telefono, direccion, referencia, token, tipoiden);
                                        } else {
                                            if (validaRucEP(ruc)) {
                                                insertaUsuario(urlInserta, usuario, ruc, nombre, razon, telefono, direccion, referencia, token, tipoiden);
                                            } else {
                                                Snackbar snackbar = Snackbar.make(v, "RUC incorrecto", Snackbar.LENGTH_LONG);
                                                View snackbarLayout = snackbar.getView();
                                                snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                snackbar.show();
                                                pb_informacion.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                } else {
                                    Snackbar snackbar = Snackbar.make(v, "RUC incorrecto", Snackbar.LENGTH_LONG);
                                    View snackbarLayout = snackbar.getView();
                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                    snackbar.show();
                                    pb_informacion.setVisibility(View.GONE);
                                }

                            } else {
                                insertaUsuario(urlInserta, usuario, ruc, nombre, razon, telefono, direccion, referencia, token, tipoiden);
                            }

                        }
                    }

                }


            }
        });

        obtenerDatosFirebase();
    }

    public void obtenerDatosFirebase() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            // UID specific to the provider
            String uid = user.getUid();

            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            txt_nombre.setText(name);
            txt_razon.setText(name);
            txt_usuario.setText(email);

            comprobarRegistro(urlComprobar, email, tokenid);


        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            txt_usuario.setText(account.getEmail());
            txt_razon.setText(account.getDisplayName());
            txt_nombre.setText(account.getDisplayName());
            comprobarRegistro(urlComprobar, account.getEmail(), tokenid);
        } else {
            //goLoginScreen();
        }
    }

    private void obtenerDatos() {
        usuario = txt_usuario.getText().toString().trim();
        nombre = txt_nombre.getText().toString().trim();
        ruc = txt_ruc.getText().toString().trim();
        razon = txt_razon.getText().toString().trim();
        direccion = txt_direccion.getText().toString().trim();
        referencia = txt_referencia.getText().toString().trim();
        telefono = txt_telefono.getText().toString().trim();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void requestEmail(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (response.getError() != null) {
                    Toast.makeText(getApplicationContext(), response.getError().getErrorMessage(), Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    String email = object.getString("email");
                    //txt_direccion.setText(object.getString("first_name"));
                    comprobarRegistro(urlComprobar, email, tokenid);
                    setEmail(email);
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void setEmail(String email) {
        txt_usuario.setText(email);
    }

    private void displayProfileInfo(Profile profile) {
        String id = profile.getId();
        String name = profile.getName();
        //String photoUrl = profile.getProfilePictureUri(100, 100).toString();

        //txt_ruc.setText(id);
        txt_nombre.setText(name);
        txt_razon.setText(name);
    }

    public void comprobarRegistro(final String ServerURL, final String usuario, final String token) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("user", usuario));
                paramsn.add(new BasicNameValuePair("token", token));

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

                    String ruc, credito;

                    if (jsonArray.length() > 0) {

                        //for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(0);

                        ruc = (jsonArrayChild.getString("ruc"));
                        credito = (jsonArrayChild.getString("credito"));

                        SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("loginusu", usuario);
                        editor.putString("ruc", ruc);
                        //editor.putString("clavepedido", txt_pswd.getText().toString().trim());
                        editor.putString("creditou", credito);
                        editor.commit();

                        ProvinciaFragment pf = new ProvinciaFragment(provincias, nidprovincia, baseprovincia, usuariobase, clavebase, nciudad, nidciudad, idpciudad);
                        pf.show(getSupportFragmentManager(), "Dialogo Provincia");
                        Log.e(TAG, "onPostExecute: abrio provincia 1");

                        //Intent intent = new Intent(Informacion.this, Menu.class);
                        //startActivity(intent);
                        //finish();

                        //}

                    } else {
                        //Toast.makeText(Informacion.this, "No esta registrado", Toast.LENGTH_LONG).show();
                        pb_informacion.setVisibility(View.GONE);
                        ly_informacion.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(usuario, token);
    }


    public void insertaUsuario(final String ServerURL, final String login, final String ruc, final String name, final String razon, final String telf, final String dir, final String ref, final String token, final String tipo) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("login", login));
                paramsn.add(new BasicNameValuePair("name", name));
                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("dir", dir));
                paramsn.add(new BasicNameValuePair("ref", ref));
                paramsn.add(new BasicNameValuePair("telf", telf));
                paramsn.add(new BasicNameValuePair("token", token));
                paramsn.add(new BasicNameValuePair("tipo", tipo));

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

                //progressBar1.setVisibility(View.GONE);

                pb_informacion.setVisibility(View.GONE);

                if (result.contains("11")) {
                    enviaCorreo(login, login);
                    SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("loginusu", login);
                    editor.putString("ruc", ruc);
                    editor.putString("creditou", "0");
                    editor.commit();
                    //Intent intent = new Intent(Informacion.this, Menu.class);
                    //startActivity(intent);

                    ProvinciaFragment pf = new ProvinciaFragment(provincias, nidprovincia, baseprovincia, usuariobase, clavebase, nciudad, nidciudad, idpciudad);
                    pf.show(getSupportFragmentManager(), "Dialogo Provincia");
                    Log.e(TAG, "onPostExecute: abrio provincia 2");

                    //finish();

                }
                //dialogoOk(result);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(login, ruc, razon, telf, dir, ref, name, token, tipo);
    }

    public void enviaCorreo(String correo, String codigo) {

        String cuerpo = "<!DOCTYPE html>\n" +
                "<html lang=\"es\" class=\"aAX\">\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<div dir=\"ltr\" style=\"background-color:#d6d6d5;margin:0;min-width:100%;padding:0;width:100%\">\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#d6d6d5;border:0;border-collapse:collapse;border-spacing:0\" bgcolor=\"#d6d6d5\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\" style=\"display:block\">\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:0;border-collapse:collapse;border-spacing:0;max-width:700px\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td style=\"background-color:#ffffff\">\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "  <tbody>\n" +
                "  <tr>\n" +
                "    <td align=\"left\" style=\"direction:ltr;text-align:left;padding:10px 14px 10px 14px;padding-left:0;background-color:#FFA000\" bgcolor=\"#FFA000\">\n" +
                "\n" +
                "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "      <tbody>\n" +
                "        <tr>\n" +
                "        \n" +
                "\n" +
                "        <td style=\"direction:ltr;text-align:left;font-size:0\">\n" +
                "        \n" +
                "\n" +
                "        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t1of12\" style=\"border:none;border-collapse:collapse;border-spacing:0;max-width:56px;width:100%;display:inline-block;vertical-align:middle\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "          <td style=\"direction:ltr;text-align:left;padding-left:12px;padding-right:12px\">\n" +
                "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "              <tbody>\n" +
                "                <tr>\n" +
                "                <td height=\"2\" style=\"direction:ltr;text-align:left;font-size:0;line-height:1px\">\n" +
                "                &nbsp;\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              </tbody>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "          </tr>\n" +
                "        </tbody>\n" +
                "        </table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "      \n" +
                "\n" +
                "  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t11of12\" style=\"border:none;border-collapse:collapse;border-spacing:0;max-width:616px;width:100%;display:inline-block;vertical-align:middle\">\n" +
                "    <tbody>\n" +
                "      <tr>\n" +
                "      <td style=\"direction:ltr;text-align:left;padding-left:0;padding-right:0\">\n" +
                "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "        <tbody>\n" +
                "          <tr>\n" +
                "            <td style=\"direction:ltr;text-align:left;font-size:0\">\n" +
                "              \n" +
                "\n" +
                "              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t4of12\" style=\"border:none;border-collapse:collapse;border-spacing:0;display:inline-block;max-width:408px;vertical-align:middle;width:100%\">\n" +
                "                <tbody>\n" +
                "                  <tr>\n" +
                "                  <td style=\"direction:ltr;text-align:left;padding-left:12px;padding-right:12px\">\n" +
                "                      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "                        <tbody>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "                      \n" +
                "                      <tr>\n" +
                "                        <td style=\"direction:ltr;text-align:left;font-size:0\">\n" +
                "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "                        <tbody><tr>\n" +
                "                        <td style=\"direction:ltr;text-align:left;font-size:0;padding-top:0px;padding-bottom:0px\">\n" +
                "\n" +
                "                        <img src=\"" + rutaGlobal + "imglogo/logo_pedidos.png\" \n" +
                "\t\t\t\t\t\twidth=\"70\" height=\"70\" alt=\"Uber\" style=\"clear:both;display:block;max-width:100%;outline:none;text-decoration:none\" \n" +
                "\t\t\t\t\t\tclass=\"CToWUd\">\n" +
                "\n" +
                "                        </td>\n" +
                "                        </tr>\n" +
                "                        </tbody></table>\n" +
                "                        </td>\n" +
                "                      </tr>\n" +
                "                      \n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "                  </tbody>\n" +
                "\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</tbody></table>\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:0;border-collapse:collapse;border-spacing:0;margin:auto;max-width:700px\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\">\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#fff;border:0;border-collapse:collapse;border-spacing:0;margin:auto\" bgcolor=\"#ffffff\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\">\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:0;border-collapse:collapse;border-spacing:0\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"center\" style=\"background-color:#ffffff\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"border:0;border-collapse:collapse;border-spacing:0\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:0;border-collapse:collapse;border-spacing:0\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:0;border-collapse:collapse;border-spacing:0\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td>\n" +
                "\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td align=\"left\" style=\"padding:0px 14px 0px 14px;direction:ltr;text-align:left\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"padding-bottom:25px;direction:ltr;text-align:left\">\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t10of12\" align=\"center\" style=\"Margin:0 auto;border:none;border-collapse:collapse;border-spacing:0;max-width:560px;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"font-size:1px;height:1px;line-height:1px;padding-left:0px!important;padding-right:0px!important;direction:ltr;text-align:left\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"color:rgb(0,0,0);font-family:&#39;UberMoveText-Regular&#39;,&#39;HelveticaNeue-Light&#39;,&#39;Helvetica Neue Light&#39;,Helvetica,Arial,sans-serif;font-size:20px;line-height:26px;direction:ltr;text-align:left\">\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t5of12\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;max-width:280px;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"padding-left:12px;padding-right:12px;padding-top:25px;direction:ltr;text-align:left\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "<tbody>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<tr>\n" +
                "<td style=\"direction:ltr;text-align:left\">\n" +
                "<h3 style=\"Margin:0;color:#000000;font-family:&#39;UberMove-Medium&#39;,&#39;HelveticaNeue-Light&#39;,&#39;Helvetica Neue Light&#39;,Helvetica,Arial,sans-serif;font-size:34px;font-weight:normal;line-height:40px;padding:0;padding-bottom:20px;padding-top:7px\">¡Bienvenido a Pedidos Plus!</h3></td>\n" +
                "</tr>\n" +
                "\n" +
                "<tr>\n" +
                "<td style=\"color:rgb(0,0,0);font-family:&#39;UberMoveText-Regular&#39;,&#39;HelveticaNeue-Light&#39;,&#39;Helvetica Neue Light&#39;,Helvetica,Arial,sans-serif;font-size:16px;line-height:22px;padding-bottom:20px;padding-top:7px;direction:ltr;text-align:left\"><p>Ya eres parte de la comunidad Pedidos Plus. <br>\n" +
                "Ingresa a nuestra aplicación movil para realizar pedidos a domicilio a nuestros diferentes locales afiliados.</p>" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<tr>\n" +
                "<td style=\"direction:ltr;text-align:left;padding-top:7px;padding-bottom:7px\">\n" +
                "\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t5of12\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;max-width:280px;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"padding-left:12px;padding-right:12px;padding-top:25px;direction:ltr;text-align:left\"><table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"direction:ltr;text-align:left\">\n" +
                "\n" +
                "<img src=\"" + rutaGlobal + "imglogo/logo_pedidos.png\" width=\"256\" height=\"\" style=\"display:block;width:100%;max-width:256px;height:auto;outline:none;text-decoration:none\" border=\"0\" alt=\"\" class=\"CToWUd\">\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table></td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "<table width=\"100%\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#FFA000;border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td align=\"left\" style=\"direction:ltr;text-align:left;padding:0 14px 0 14px\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border:none;border-collapse:collapse;border-spacing:0;width:100%\">\n" +
                "<tbody>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<tr>\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t10of12\" align=\"center\" style=\"Margin:0 auto;border:none;border-collapse:collapse;border-spacing:0;max-width:560px;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"direction:ltr;text-align:left;padding-left:0;padding-right:0\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;direction:rtl;table-layout:fixed;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"font-size:0;text-align:left\">\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" class=\"m_578392451583606558t4of12\" style=\"border:none;border-collapse:collapse;border-spacing:0;direction:ltr;display:inline-block;max-width:224px;vertical-align:top;width:100%\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"direction:ltr;text-align:left;padding-left:12px;padding-right:12px\">\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed\">\n" +
                "<tbody><tr>\n" +
                "<td style=\"padding-bottom:12px;direction:ltr;text-align:left\">\n" +
                "\n" +
                "\n" +
                "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" align=\"left\" style=\"border:none;border-collapse:collapse;border-spacing:0;table-layout:fixed;width:130px\">\n" +
                "<tbody>\n" +
                "<tr>\n" +
                "<td width=\"43\" height=\"10\" align=\"center\" style=\"direction:ltr;text-align:left\">\n" +
                "\n" +
                "</td>\n" +
                "\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "\n" +
                "</tbody></table>\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</tbody></table>\n" +
                "</td>\n" +
                "</tr>\n" +
                "\n" +
                "\n" +
                "</tbody></table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "</td>\n" +
                "</tr>\n" +
                "</tbody>\n" +
                "</table>\n" +
                "\n" +
                "\n" +
                "</div>";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        //properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", "465");


        //properties.put("mail.smtp.ssl.trust", host);
        try {

            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sEmail, sPass);
                }
            });


            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correo));
            message.setSubject("Bienvenido!");
            //message.setText("Su nueva contraseña es: "+codigo);
            message.setContent(cuerpo, "text/html; charset=utf-8");


                        /*
                        Transport transport = session.getTransport("smtp");
                        transport.connect("c54561.sgvps.net", 465, sEmail, sPass);
                        transport.sendMessage(message,message.getAllRecipients());
                        transport.close();
                        */
            Transport.send(message);


        } catch (MessagingException e) {
            e.printStackTrace();
            Log.e("CORREO", "ERROR: " + e);

        }


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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            pb_informacion.setVisibility(View.GONE);
            //Toast.makeText(Registrar.this, "La cédula ingresada es incorrecta", Toast.LENGTH_LONG).show();
            Snackbar snackbar = Snackbar.make(ver, "La cédula ingresada es incorrecta", Snackbar.LENGTH_LONG);
            View snackbarLayout = snackbar.getView();
            snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
            TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
            snackbar.show();
        }
        return cedulaCorrecta;
    }

}
