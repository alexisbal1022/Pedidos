package ec.compumax.pedidos;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Registrarse extends AppCompatActivity {

    EditText reg_usuario, reg_referencia, reg_rcontra, reg_contra, reg_nombres, reg_direccion, reg_telefono, fac_ruc, fac_razon, fac_direccion;
    CheckBox check_igual;
    Button btn_regusuario;
    ProgressBar progressBar1;

    String usuario, nombre, direccion, telefono, ruc, razon, referencia, contra;
    String urlUsuario = "https://app.pedidosplus.com/wsProvincias/inserta_usuarion.php";
    String sEmail, sPass;

    Session session;

    Spinner sp_tipo;

    ArrayList<String> ididentifica = new ArrayList<String>();
    ArrayList<String> nombreidentifica = new ArrayList<String>();

    String tipoiden, rutaGlobal;

    private static  final int num_provincias = 24;

    private static int[] coeficientes = {4,3,2,7,6,5,4,3,2};
    private static int constante = 11;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarse);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        sEmail = prefs.getString("correoplus", "");
        sPass = prefs.getString("pswdplus", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        reg_usuario = (EditText) findViewById(R.id.reg_usuario);
        reg_nombres = (EditText) findViewById(R.id.reg_nombres);
        reg_direccion = (EditText) findViewById(R.id.reg_direccion);
        reg_telefono = (EditText) findViewById(R.id.reg_telefono);
        reg_referencia = (EditText) findViewById(R.id.reg_referencia);
        reg_contra = (EditText) findViewById(R.id.reg_contra);
        reg_rcontra = (EditText) findViewById(R.id.reg_rcontra);
        fac_ruc = (EditText) findViewById(R.id.fac_ruc);
        fac_razon = (EditText) findViewById(R.id.fac_razon);
        fac_direccion = (EditText) findViewById(R.id.fac_direccion);
        btn_regusuario = (Button) findViewById(R.id.btn_regusuario);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        sp_tipo = (Spinner) findViewById(R.id.sp_tipo);

        nombreidentifica.add("Tipo de identificación");
        nombreidentifica.add("Cédula");
        nombreidentifica.add("RUC");
        nombreidentifica.add("Pasaporte");
        ididentifica.add("00");
        ididentifica.add("05");
        ididentifica.add("04");
        ididentifica.add("06");


        sp_tipo.setAdapter(new ArrayAdapter<String>(Registrarse.this, android.R.layout.simple_spinner_dropdown_item, nombreidentifica));

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

        //sEmail = "soportepedidosplus@gmail.com";
        //sPass = "Alexis.-2020@";

        check_igual = (CheckBox) findViewById(R.id.check_igual);

        check_igual.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Log.e("check", "si");
                    igualFactura();
                } else {
                    Log.e("check", "no");
                    diferenteFactura();
                }
            }
        });

        btn_regusuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = ProgressDialog.show(Registrarse.this,
                        "Porfavor espere", "Enviando correo...", true);

                //progressBar1.setVisibility(View.VISIBLE);
                obtenerText();
                if (usuario.isEmpty() || nombre.isEmpty() || contra.isEmpty() || ruc.isEmpty() || razon.isEmpty() || telefono.isEmpty() || direccion.isEmpty() || referencia.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Hay campos vacios", Snackbar.LENGTH_LONG);
                    View snackbarLayout = snackbar.getView();
                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                    snackbar.show();
                    //progressBar1.setVisibility(View.GONE);
                    //vaciarTxt();
                    progressDialog.dismiss();

                } else {
                    if (tipoiden.contains("00")) {
                        Snackbar snackbar = Snackbar.make(view, "Seleccione un tipo de identificación", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                        //progressBar1.setVisibility(View.GONE);
                        progressDialog.dismiss();

                    } else {
                        if (tipoiden.contains("05")) {
                            //CEDULA
                            if (validadorDeCedula(ruc, view)) {

                                if (contra.equals(reg_rcontra.getText().toString().trim())) {
                                    if (!validarEmail(reg_usuario.getText().toString())) {
                                        reg_usuario.setError("Correo no válido");
                                        Log.e("Error", "Correo no valido");
                                        //til_correo.setError();
                                        //progressBar1.setVisibility(View.GONE);
                                        progressDialog.dismiss();
                                        Snackbar snackbar = Snackbar.make(view, "Correo no válido", Snackbar.LENGTH_LONG);
                                        View snackbarLayout = snackbar.getView();
                                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                        textView.setTextColor(Color.WHITE);
                                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                        snackbar.show();
                                    } else {
                                        Log.e("Error", "Correo valido");
                                        //insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia,tipoiden);
                                        enviaCorreo(usuario,usuario);
                                    }
                                    //vaciarTxt();
                                } else {
                                    Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                                    View snackbarLayout = snackbar.getView();
                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                    snackbar.show();
                                    //progressBar1.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                }

                            }

                        } else {
                            if (tipoiden.contains("04")) {

                                if(ruc.length()==13){
                                    if (validacionCedula(ruc.substring(0, 10))) {
                                        if (contra.equals(reg_rcontra.getText().toString().trim())) {
                                            if (!validarEmail(reg_usuario.getText().toString())) {
                                                reg_usuario.setError("Correo no válido");
                                                Log.e("Error", "Correo no valido");
                                                //til_correo.setError();
                                                //progressBar1.setVisibility(View.GONE);
                                                progressDialog.dismiss();
                                                Snackbar snackbar = Snackbar.make(view, "Correo no válido", Snackbar.LENGTH_LONG);
                                                View snackbarLayout = snackbar.getView();
                                                snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                snackbar.show();
                                            } else {
                                                Log.e("Error", "Correo valido");
                                                enviaCorreo(usuario,usuario);
                                                //insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia,tipoiden);
                                            }
                                            //vaciarTxt();
                                        } else {
                                            Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                                            View snackbarLayout = snackbar.getView();
                                            snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                            TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                            textView.setTextColor(Color.WHITE);
                                            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                            snackbar.show();
                                            progressDialog.dismiss();
                                            //progressBar1.setVisibility(View.GONE);
                                        }
                                    }else {

                                        if (validacionRUC(ruc)) {
                                            if (contra.equals(reg_rcontra.getText().toString().trim())) {
                                                if (!validarEmail(reg_usuario.getText().toString())) {
                                                    reg_usuario.setError("Correo no válido");
                                                    Log.e("Error", "Correo no valido");
                                                    //til_correo.setError();
                                                    //progressBar1.setVisibility(View.GONE);
                                                    progressDialog.dismiss();
                                                    Snackbar snackbar = Snackbar.make(view, "Correo no válido", Snackbar.LENGTH_LONG);
                                                    View snackbarLayout = snackbar.getView();
                                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                    textView.setTextColor(Color.WHITE);
                                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                    snackbar.show();
                                                } else {
                                                    Log.e("Error", "Correo valido");
                                                    enviaCorreo(usuario,usuario);
                                                    //insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia, tipoiden);
                                                }
                                                //vaciarTxt();
                                            } else {
                                                Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                                                View snackbarLayout = snackbar.getView();
                                                snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                snackbar.show();
                                                progressDialog.dismiss();
                                                //progressBar1.setVisibility(View.GONE);
                                            }
                                        }else {

                                            if (validaRucEP(ruc)) {
                                                if (contra.equals(reg_rcontra.getText().toString().trim())) {
                                                    if (!validarEmail(reg_usuario.getText().toString())) {
                                                        reg_usuario.setError("Correo no válido");
                                                        Log.e("Error", "Correo no valido");
                                                        //til_correo.setError();
                                                        //progressBar1.setVisibility(View.GONE);
                                                        progressDialog.dismiss();
                                                        Snackbar snackbar = Snackbar.make(view, "Correo no válido", Snackbar.LENGTH_LONG);
                                                        View snackbarLayout = snackbar.getView();
                                                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                        textView.setTextColor(Color.WHITE);
                                                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                        snackbar.show();
                                                    } else {
                                                        Log.e("Error", "Correo valido");
                                                        enviaCorreo(usuario,usuario);
                                                        //insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia, tipoiden);
                                                    }
                                                    //vaciarTxt();
                                                } else {
                                                    Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                                                    View snackbarLayout = snackbar.getView();
                                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                    textView.setTextColor(Color.WHITE);
                                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                    snackbar.show();
                                                    //progressBar1.setVisibility(View.GONE);
                                                    progressDialog.dismiss();
                                                }

                                            } else {
                                                Snackbar snackbar = Snackbar.make(view, "RUC incorrecto", Snackbar.LENGTH_LONG);
                                                View snackbarLayout = snackbar.getView();
                                                snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                                TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                                textView.setTextColor(Color.WHITE);
                                                textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                                snackbar.show();
                                                //progressBar1.setVisibility(View.GONE);
                                                progressDialog.dismiss();
                                            }
                                        }
                                    }
                                }else{
                                    Snackbar snackbar = Snackbar.make(view, "RUC incorrecto", Snackbar.LENGTH_LONG);
                                    View snackbarLayout = snackbar.getView();
                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                    snackbar.show();
                                    //progressBar1.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                }




                            } else {
                                if (contra.equals(reg_rcontra.getText().toString().trim())) {
                                    if (!validarEmail(reg_usuario.getText().toString())) {
                                        reg_usuario.setError("Correo no válido");
                                        Log.e("Error", "Correo no valido");
                                        //til_correo.setError();
                                        //progressBar1.setVisibility(View.GONE);
                                        progressDialog.dismiss();
                                        Snackbar snackbar = Snackbar.make(view, "Correo no válido", Snackbar.LENGTH_LONG);
                                        View snackbarLayout = snackbar.getView();
                                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                        textView.setTextColor(Color.WHITE);
                                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                        snackbar.show();
                                    } else {
                                        Log.e("Error", "Correo valido");
                                        enviaCorreo(usuario,usuario);
                                        //insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia,tipoiden);
                                    }
                                    //vaciarTxt();
                                } else {
                                    Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                                    View snackbarLayout = snackbar.getView();
                                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                                    textView.setTextColor(Color.WHITE);
                                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                                    snackbar.show();
                                    //progressBar1.setVisibility(View.GONE);
                                    progressDialog.dismiss();
                                }
                            }
                        }
                    }

                }


            }
        });
    }

    public void dialogoOk(String respuesta) {

        new AlertDialog.Builder(Registrarse.this, R.style.AlertDialogTheme)
                .setCancelable(false)
                .setTitle("Registro de Usuario")
                .setMessage(respuesta)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vaciarTxt();
                        Intent intent = new Intent(Registrarse.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private void obtenerText() {
        nombre = reg_nombres.getText().toString().trim();
        usuario = reg_usuario.getText().toString().trim();
        ruc = fac_ruc.getText().toString().trim();
        razon = fac_razon.getText().toString().trim();
        direccion = reg_direccion.getText().toString().trim();
        telefono = reg_telefono.getText().toString().trim();
        referencia = reg_referencia.getText().toString().trim();
        //correo = reg_correo.getText().toString().trim();
        contra = reg_contra.getText().toString().trim();
    }

    public void vaciarTxt() {
        reg_nombres.setText("");
        reg_usuario.setText("");
        fac_ruc.setText("");
        fac_razon.setText("");
        reg_direccion.setText("");
        reg_telefono.setText("");
        reg_referencia.setText("");
        //reg_correo.setText("");
        reg_contra.setText("");

    }

    public void igualFactura() {
        //fac_ruc.setText(reg_cedula.getText());
        fac_razon.setText(reg_nombres.getText());
        fac_direccion.setText(reg_direccion.getText());

    }

    public void diferenteFactura() {
        fac_ruc.setText("");
        fac_razon.setText("");
        fac_direccion.setText("");

    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void insertaUsuario(final String ServerURL, final String usuario, final String pswd, final String ruc, final String name, final String razon, final String telf, final String dir, final String ref, final String tipo) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("login", usuario));
                paramsn.add(new BasicNameValuePair("pswd", pswd));
                paramsn.add(new BasicNameValuePair("name", name));
                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("dir", dir));
                paramsn.add(new BasicNameValuePair("ref", ref));
                paramsn.add(new BasicNameValuePair("telf", telf));
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

                //if (result.contains("Datos insertados")) {
                    //enviaCorreo(usuario, usuario);

                //}
                dialogoOk(result);

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(pswd, usuario, ruc, razon, telf, dir, ref, name, tipo);
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
                "                        <img src=\""+rutaGlobal+"imglogo/logo_pedidos.png\" \n" +
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
                "Ingresa a nuestra aplicación movil para realizar pedidos a domicilio a nuestros diferentes locales afiliados, tu usuario es: \n" + codigo +
                "</p>\n" +
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
                "<img src=\""+rutaGlobal+"imglogo/logo_pedidos.png\" width=\"256\" height=\"\" style=\"display:block;width:100%;max-width:256px;height:auto;outline:none;text-decoration:none\" border=\"0\" alt=\"\" class=\"CToWUd\">\n" +
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

            insertaUsuario(urlUsuario, usuario, contra, ruc, nombre, razon, telefono, direccion, referencia,tipoiden);
            progressDialog.dismiss();
            /*
            AlertDialog.Builder builder = new AlertDialog.Builder(Registrarse.this);
            builder.setCancelable(false);
            builder.setTitle("Registro de Usuario");
            builder.setMessage("Registro de Usuario exitoso");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    vaciarTxt();
                    finish();
                }
            });
            builder.show();

             */


        } catch (MessagingException e) {
            e.printStackTrace();
            Log.e("CORREO", "ERROR: " + e);

            AlertDialog.Builder builder = new AlertDialog.Builder(Registrarse.this, R.style.AlertDialogTheme);
            builder.setCancelable(false);
            builder.setTitle("Registro de Usuario");
            builder.setMessage("Ha ocurrido un error con el correo electrónico que ingresó");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();

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
            //progressBar1.setVisibility(View.GONE);
            progressDialog.dismiss();
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

    public static Boolean validacionCedula(String cedula) {
        int prov = Integer.parseInt(cedula.substring(0, 2));

        if (!((prov > 0) && (prov <= num_provincias))) {
            return false;
        }

        int[] d = new int[10];
        for (int i = 0; i < d.length; i++) {
            d[i] = Integer.parseInt(cedula.charAt(i) + "");
        }

        int imp = 0;
        int par = 0;

        for (int i = 0; i < d.length; i += 2) {

            d[i] = ((d[i] * 2) > 9) ? ((d[i] * 2) - 9) : (d[i] * 2);
            imp += d[i];
        }

        for (int i = 1; i < (d.length - 1);
        i += 2){
            par += d[i];
        }

        int suma = imp + par;

        int d10 = Integer.parseInt(String.valueOf(suma + 10).substring(0, 1) + "0") - suma;

        d10 = (d10 == 10) ? 0 : d10;

        if (d10 == d[9]) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean validacionRUC(String ruc){
        int prov = Integer.parseInt(ruc.substring(0, 2));

        if (!((prov > 0) && (prov <= num_provincias))) {

            return false;
        }

        int[] d = new int[10];
        int suma = 0;

        for (int i = 0; i < d.length; i++) {
            d[i] = Integer.parseInt(ruc.charAt(i) + "");
        }

        for (int i=0; i< d.length - 1; i++) {
            d[i] = d[i] * coeficientes[i];
            suma += d[i];
        }

        int aux, resp;

        aux = suma % constante;
        resp = constante - aux;

        resp = (resp == 10) ? 0 : resp;

        if (resp == d[9]) {
            return true;
        }
        else
            return false;
    }

    public static Boolean validaRucEP(String ruc){
        int prov = Integer.parseInt(ruc.substring(0, 2));
        boolean val = false;

        if (!((prov > 0) && (prov <= num_provincias))) {
            return val;
        }

        Integer v1,v2,v3,v4,v5,v6,v7,v8,v9;
        Integer sumatoria;
        Integer modulo;
        Integer digito;
        Integer sustraendo;
        int[] d = new int[ruc.length()];

        for (int i = 0; i < d.length; i++) {
            d[i] = Integer.parseInt(ruc.charAt(i) + "");
        }

        v1 = d[0]* 3;
        v2 = d[1]* 2;
        v3 = d[2]* 7;
        v4 = d[3]* 6;
        v5 = d[4]* 5;
        v6 = d[5]* 4;
        v7 = d[6]* 3;
        v8 = d[7]* 2;
        v9 = d[8];

        sumatoria = v1+v2+v3+v4+v5+v6+v7+v8;
        modulo = sumatoria % 11;
        sustraendo = modulo * 11;
        digito = 11-(sumatoria - sustraendo);


        if(digito == v9){
            val = true;
        }else
            val = false;
        return val;
    }

}
