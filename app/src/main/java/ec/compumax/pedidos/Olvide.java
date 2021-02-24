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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import ec.compumax.pedidos.Otros.SendMailAsynTask;

public class Olvide extends AppCompatActivity {

    Button olv_cambia;
    EditText olv_usuario, olv_contra, olv_ncontra;
    String urlCambia = "https://app.pedidosplus.com/wsProvincias/cambia_contrasena_usuario.php";
    String urlVer = "https://app.pedidosplus.com/wsProvincias/consulta_correo.php";

    TextInputLayout con_olv_usuario, con_olv_contra, con_olv_ncontra;
    ProgressBar progressBar1;

    String sEmail, sPass, rutaGlobal;

    Session session;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_olvide);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        sEmail = prefs.getString("correoplus", "");
        sPass = prefs.getString("pswdplus", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        olv_cambia = (Button) findViewById(R.id.olv_cambia);
        olv_usuario = (EditText) findViewById(R.id.olv_usuario);
        olv_contra = (EditText) findViewById(R.id.olv_contra);
        olv_ncontra = (EditText) findViewById(R.id.olv_ncontra);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);

        con_olv_usuario = (TextInputLayout) findViewById(R.id.con_olv_usuario);
        con_olv_contra = (TextInputLayout) findViewById(R.id.con_olv_contra);
        con_olv_ncontra = (TextInputLayout) findViewById(R.id.con_olv_ncontra);


        //sEmail="alexisbal1022@gmail.com";
        //sEmail = "soportepedidosplus@gmail.com";
        //sPass="Alexis15111992@";
        //sPass = "Alexis.-2020@";

        olv_cambia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                progressDialog = ProgressDialog.show(Olvide.this,
                        "Porfavor espere", "Enviando correo...", true);
                //progressBar1.setVisibility(View.VISIBLE);

                if (olv_usuario.getText().toString().isEmpty()) {

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
                    consultaUsuario(urlVer,olv_usuario.getText().toString().trim(),view);
                    //vaciarTxt();

                }

            }
        });


        /*

        olv_cambia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar1.setVisibility(View.VISIBLE);

                if(olv_usuario.getText().toString().isEmpty()||olv_ncontra.getText().toString().isEmpty()||olv_contra.getText().toString().isEmpty()) {

                    Snackbar snackbar = Snackbar.make(view, "Hay campos vacios", Snackbar.LENGTH_LONG);
                    View snackbarLayout = snackbar.getView();
                    snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                    TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                    snackbar.show();
                    progressBar1.setVisibility(View.GONE);
                    vaciarTxt();
                }else {

                    String clave = olv_contra.getText().toString().trim();

                    if(clave.equals(olv_ncontra.getText().toString().trim())){
                        cambiaContra(urlCambia, olv_contra.getText().toString(), olv_usuario.getText().toString());
                        vaciarTxt();
                    }else {
                        Snackbar snackbar = Snackbar.make(view, "Las contraseñas no coinciden", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                        progressBar1.setVisibility(View.GONE);
                    }


                }

            }
        });

        */
    }




    public void vaciarTxt() {
        olv_usuario.setText("");
        olv_contra.setText("");
        olv_ncontra.setText("");
    }

    public void consultaUsuario(final String ServerURL, final String user, final View ver) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("user", user));

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

                    String usu, correo;

                    JSONArray jsonArray = json.optJSONArray("result");

                    if (jsonArray.length() == 0) {
                        Log.e("resval", "user psw incorrecto");
                        //pBar2.setVisibility(View.GONE);
                        //tv2.setVisibility(View.GONE);
                        //llProgressBar.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(ver, "Usuario no encontrado", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();
                        progressDialog.dismiss();

                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        usu = (jsonArrayChild.getString("login"));
                        correo = (jsonArrayChild.getString("email"));

                        Random random = new Random();

                        int randomNumber = random.nextInt(9999);

                        if (usu.equals(olv_usuario.getText().toString().trim())) {

                            enviaCorreo(correo, usu, String.valueOf(randomNumber));

                        } else {

                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(user);
    }


    public void enviaCorreo(String correo, String user, String contra) {
        //progressDialog.dismiss();


        String cuerpo = "<!DOCTYPE html>    \n" +
                "<html>    \n" +
                "<head>\n" +
                "\n" +
                "<title>Push Email</title>\n" +
                "<link rel=\"shortcut icon\" href=\"favicon.ico\">\n" +
                "\n" +
                "<style type=\"text/css\">\n" +
                "table[name=\"blk_permission\"], table[name=\"blk_footer\"] {display:none;} \n" +
                "</style>\n" +
                "\n" +
                "<meta name=\"googlebot\" content=\"noindex\" />\n" +
                "<META NAME=\"ROBOTS\" CONTENT=\"NOINDEX, NOFOLLOW\"/><link rel=\"stylesheet\" href=\"/style/dhtmlwindow.css\" type=\"text/css\" />\n" +
                "<script type=\"text/javascript\" src=\"/script/dhtmlwindow.js\">\n" +
                "\n" +
                "</script>\n" +
                "<link rel=\"stylesheet\" href=\"/style/modal.css\" type=\"text/css\" />\n" +
                "<script type=\"text/javascript\" src=\"/script/modal.js\"></script>\n" +
                "<script type=\"text/javascript\">\n" +
                "\tfunction show_popup(popup_name,popup_url,popup_title,width,height) {var widthpx = width +  \"px\";var heightpx = height +  \"px\";emailwindow = dhtmlmodal.open(popup_name , 'iframe', popup_url , popup_title , 'width=' + widthpx + ',height='+ heightpx + ',center=1,resize=0,scrolling=1');}\n" +
                " function show_modal(popup_name,popup_url,popup_title,width,height){var widthpx = width +  \"px\";var heightpx = height +  \"px\";emailwindow = dhtmlmodal.open(popup_name , 'iframe', popup_url , popup_title , 'width=' + widthpx + ',height='+ heightpx + ',modal=1,center=1,resize=0,scrolling=1');}\n" +
                "var popUpWin=0;\n" +
                "\tfunction popUpWindow(URLStr,PopUpName, width, height){if(popUpWin) { if(!popUpWin.closed) popUpWin.close();}var left = (screen.width - width) / 2;var top = (screen.height - height) / 2;popUpWin = open(URLStr, PopUpName,\t'toolbar=0,location=0,directories=0,status=0,menub\tar=0,scrollbar=0,resizable=0,copyhistory=yes,width='+width+',height='+height+',left='+left+', \ttop='+top+',screenX='+left+',screenY='+top+'');}\n" +
                "</script>\n" +
                "    \n" +
                "<meta content=\"width=device-width, initial-scale=1.0\" name=\"viewport\">    \n" +
                "<style type=\"text/css\">    \n" +
                "/*** BMEMBF Start ***/    \n" +
                "[name=bmeMainBody]{min-height:1000px;}    \n" +
                "@media only screen and (max-width: 480px){table.blk, table.tblText, .bmeHolder, .bmeHolder1, table.bmeMainColumn{width:100% !important;} }        \n" +
                "@media only screen and (max-width: 480px){.bmeImageCard table.bmeCaptionTable td.tblCell{padding:0px 20px 20px 20px !important;} }        \n" +
                "@media only screen and (max-width: 480px){.bmeImageCard table.bmeCaptionTable.bmeCaptionTableMobileTop td.tblCell{padding:20px 20px 0 20px !important;} }        \n" +
                "@media only screen and (max-width: 480px){table.bmeCaptionTable td.tblCell{padding:10px !important;} }        \n" +
                "@media only screen and (max-width: 480px){table.tblGtr{ padding-bottom:20px !important;} }        \n" +
                "@media only screen and (max-width: 480px){td.blk_container, .blk_parent, .bmeLeftColumn, .bmeRightColumn, .bmeColumn1, .bmeColumn2, .bmeColumn3, .bmeBody{display:table !important;max-width:600px !important;width:100% !important;} }        \n" +
                "@media only screen and (max-width: 480px){table.container-table, .bmeheadertext, .container-table { width: 95% !important; } }        \n" +
                "@media only screen and (max-width: 480px){.mobile-footer, .mobile-footer a{ font-size: 13px !important; line-height: 18px !important; } .mobile-footer{ text-align: center !important; } table.share-tbl { padding-bottom: 15px; width: 100% !important; } table.share-tbl td { display: block !important; text-align: center !important; width: 100% !important; } }        \n" +
                "@media only screen and (max-width: 480px){td.bmeShareTD, td.bmeSocialTD{width: 100% !important; } }        \n" +
                "@media only screen and (max-width: 480px){td.tdBoxedTextBorder{width: auto !important;}}    \n" +
                "@media only screen and (max-width: 480px){table.blk, table[name=tblText], .bmeHolder, .bmeHolder1, table[name=bmeMainColumn]{width:100% !important;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeImageCard table.bmeCaptionTable td[name=tblCell]{padding:0px 20px 20px 20px !important;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeImageCard table.bmeCaptionTable.bmeCaptionTableMobileTop td[name=tblCell]{padding:20px 20px 0 20px !important;} }    \n" +
                "@media only screen and (max-width: 480px){table.bmeCaptionTable td[name=tblCell]{padding:10px !important;} }    \n" +
                "@media only screen and (max-width: 480px){table[name=tblGtr]{ padding-bottom:20px !important;} }    \n" +
                "@media only screen and (max-width: 480px){td.blk_container, .blk_parent, [name=bmeLeftColumn], [name=bmeRightColumn], [name=bmeColumn1], [name=bmeColumn2], [name=bmeColumn3], [name=bmeBody]{display:table !important;max-width:600px !important;width:100% !important;} }    \n" +
                "@media only screen and (max-width: 480px){table[class=container-table], .bmeheadertext, .container-table { width: 95% !important; } }    \n" +
                "@media only screen and (max-width: 480px){.mobile-footer, .mobile-footer a{ font-size: 13px !important; line-height: 18px !important; } .mobile-footer{ text-align: center !important; } table[class=\"share-tbl\"] { padding-bottom: 15px; width: 100% !important; } table[class=\"share-tbl\"] td { display: block !important; text-align: center !important; width: 100% !important; } }    \n" +
                "@media only screen and (max-width: 480px){td[name=bmeShareTD], td[name=bmeSocialTD]{width: 100% !important; } }    \n" +
                "@media only screen and (max-width: 480px){td[name=tdBoxedTextBorder]{width: auto !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeImageCard table.bmeImageTable{height: auto !important; width:100% !important; padding:20px !important;clear:both; float:left !important; border-collapse: separate;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblInline table.bmeImageTable{height: auto !important; width:100% !important; padding:10px !important;clear:both;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblInline table.bmeCaptionTable{width:100% !important; clear:both;} }    \n" +
                "@media only screen and (max-width: 480px){table.bmeImageTable{height: auto !important; width:100% !important; padding:10px !important;clear:both; } }    \n" +
                "@media only screen and (max-width: 480px){table.bmeCaptionTable{width:100% !important;  clear:both;} }    \n" +
                "@media only screen and (max-width: 480px){table.bmeImageContainer{width:100% !important; clear:both; float:left !important;} }    \n" +
                "@media only screen and (max-width: 480px){table.bmeImageTable td{padding:0px !important; height: auto; } }    \n" +
                "@media only screen and (max-width: 480px){td.bmeImageContainerRow{padding:0px !important;}}    \n" +
                "@media only screen and (max-width: 480px){img.mobile-img-large{width:100% !important; height:auto !important;} }    \n" +
                "@media only screen and (max-width: 480px){img.bmeRSSImage{max-width:320px; height:auto !important;}}    \n" +
                "@media only screen and (min-width: 640px){img.bmeRSSImage{max-width:600px !important; height:auto !important;} }    \n" +
                "@media only screen and (max-width: 480px){.trMargin img{height:10px;} }    \n" +
                "@media only screen and (max-width: 480px){div.bmefooter, div.bmeheader{ display:block !important;} }    \n" +
                "@media only screen and (max-width: 480px){.tdPart{ width:100% !important; clear:both; float:left !important; } }    \n" +
                "@media only screen and (max-width: 480px){table.blk_parent1, table.tblPart {width: 100% !important; } }    \n" +
                "@media only screen and (max-width: 480px){.tblLine{min-width: 100% !important;}}     \n" +
                "@media only screen and (max-width: 480px){.bmeMblCenter img { margin: 0 auto; } }       \n" +
                "@media only screen and (max-width: 480px){.bmeMblCenter, .bmeMblCenter div, .bmeMblCenter span  { text-align: center !important; text-align: -webkit-center !important; } }    \n" +
                "@media only screen and (max-width: 480px){.bmeNoBr br, .bmeImageGutterRow, .bmeMblStackCenter .bmeShareItem .tdMblHide { display: none !important; } }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblInline table.bmeImageTable, .bmeMblInline table.bmeCaptionTable, td.bmeMblInline { clear: none !important; width:50% !important; } }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblInlineHide, .bmeShareItem .trMargin { display: none !important; } }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblInline table.bmeImageTable img, .bmeMblShareCenter.tblContainer.mblSocialContain, .bmeMblFollowCenter.tblContainer.mblSocialContain{width: 100% !important; } }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStack> .bmeShareItem{width: 100% !important; clear: both !important;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeShareItem{padding-top: 10px !important;} }    \n" +
                "@media only screen and (max-width: 480px){.tdPart.bmeMblStackCenter, .bmeMblStackCenter .bmeFollowItemIcon {padding:0px !important; text-align: center !important;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStackCenter> .bmeShareItem{width: 100% !important;} }    \n" +
                "@media only screen and (max-width: 480px){ td.bmeMblCenter {border: 0 none transparent !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeLinkTable.tdPart td{padding-left:0px !important; padding-right:0px !important; border:0px none transparent !important;padding-bottom:15px !important;height: auto !important;}}    \n" +
                "@media only screen and (max-width: 480px){.tdMblHide{width:10px !important;} }    \n" +
                "@media only screen and (max-width: 480px){.bmeShareItemBtn{display:table !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStack td {text-align: left !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStack .bmeFollowItem{clear:both !important; padding-top: 10px !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStackCenter .bmeFollowItemText{padding-left: 5px !important;}}    \n" +
                "@media only screen and (max-width: 480px){.bmeMblStackCenter .bmeFollowItem{clear:both !important;align-self:center; float:none !important; padding-top:10px;margin: 0 auto;}}    \n" +
                "@media only screen and (max-width: 480px){    \n" +
                ".tdPart> table{width:100% !important;}    \n" +
                "}    \n" +
                "@media only screen and (max-width: 480px){.tdPart>table.bmeLinkContainer{ width:auto !important; }}    \n" +
                "@media only screen and (max-width: 480px){.tdPart.mblStackCenter>table.bmeLinkContainer{ width:100% !important;}}     \n" +
                ".blk_parent:first-child, .blk_parent{float:left;}    \n" +
                ".blk_parent:last-child{float:right;}    \n" +
                "/*** BMEMBF END ***/    \n" +
                "    \n" +
                "table[name=\"bmeMainBody\"], body {background-color:#a6e9d7;}    \n" +
                " td[name=\"bmePreHeader\"] {background-color:transparent;}    \n" +
                " td[name=\"bmeHeader\"] {background:#ffffff;background-color:#1fc899;}    \n" +
                " td[name=\"bmeBody\"], table[name=\"bmeBody\"] {background-color:#ffffff;}    \n" +
                " td[name=\"bmePreFooter\"] {background-color:#ffffff;}    \n" +
                " td[name=\"bmeFooter\"] {background-color:transparent;}    \n" +
                " td[name=\"tblCell\"], .blk {font-family:initial;font-weight:normal;font-size:initial;}    \n" +
                " table[name=\"blk_blank\"] td[name=\"tblCell\"] {font-family:Arial, Helvetica, sans-serif;font-size:14px;}    \n" +
                " [name=bmeMainContentParent] {border-color:#808080;border-width:0px;border-style:none;border-radius:0px;border-collapse:separate;border-spacing:0px;overflow:hidden;}    \n" +
                " [name=bmeMainColumnParent] {border-color:transparent;border-width:0px;border-style:none;border-radius:0px;}    \n" +
                " [name=bmeMainColumn] {border-color:transparent;border-width:0px;border-style:none;border-radius:0px;border-collapse:separate;border-spacing:0px;}    \n" +
                " [name=bmeMainContent] {border-color:transparent;border-width:0px;border-style:none;border-radius:0px;border-collapse:separate;border-spacing:0px;}    \n" +
                "    \n" +
                "</style>    \n" +
                "</head>    \n" +
                "<body marginheight=0 marginwidth=0 topmargin=0 leftmargin=0 style=\"height: 100% !important; margin: 0; padding: 0; width: 100% !important;min-width: 100%;\">    \n" +
                "    \n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" name=\"bmeMainBody\" style=\"background-color: rgb(250, 210, 92);\" bgcolor=\"#a6e9d7\"><tbody><tr><td width=\"100%\" valign=\"top\" align=\"center\">    \n" +
                "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" name=\"bmeMainColumnParentTable\"><tbody><tr><td name=\"bmeMainColumnParent\" style=\"border-collapse: separate; border: 0px none transparent; border-radius: 0px;\">       \n" +
                "<table name=\"bmeMainColumn\" class=\"bmeHolder bmeMainColumn\" style=\"max-width: 600px; border-image: initial; border-radius: 0px; border-collapse: separate; border-spacing: 0px; overflow: visible;\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\"><tbody><tr><td width=\"100%\" class=\"blk_container bmeHolder\" name=\"bmePreHeader\" valign=\"top\" align=\"center\" style=\"color: rgb(102, 102, 102); border: 0px none transparent;\" bgcolor=\"\"></td></tr>   <tr><td width=\"100%\" class=\"bmeHolder\" valign=\"top\" align=\"center\" name=\"bmeMainContentParent\" style=\"border: 0px none rgb(128, 128, 128); border-radius: 0px; border-collapse: separate; border-spacing: 0px; overflow: hidden;\">    \n" +
                "<table name=\"bmeMainContent\" style=\"border-radius: 0px; border-collapse: separate; border-spacing: 0px; border: 0px none transparent;\" width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\"><tbody><tr><td width=\"100%\" class=\"blk_container bmeHolder\" name=\"bmeHeader\" valign=\"top\" align=\"center\" style=\"border: 0px none transparent; background-color: rgb(250, 188, 7); color: rgb(56, 56, 56);\" bgcolor=\"#1fc899\"><div id=\"dv_11\" class=\"blk_wrapper\" style=\"\">    \n" +
                "\n" +
                "</div><div id=\"dv_3\" class=\"blk_wrapper\" style=\"\">    \n" +
                "\n" +
                "</div><div id=\"dv_18\" class=\"blk_wrapper\" style=\"\">    \n" +
                "\n" +
                "</div><div id=\"dv_1\" class=\"blk_wrapper\" style=\"\">    \n" +
                "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"blk\" name=\"blk_text\"><tbody><tr><td>    \n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"bmeContainerRow\"><tbody><tr><td class=\"tdPart\" valign=\"top\" align=\"center\">    \n" +
                "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"600\" name=\"tblText\" style=\"float:left; background-color:transparent;\" align=\"left\" class=\"tblText\"><tbody><tr><td valign=\"top\" align=\"left\" name=\"tblCell\" style=\"padding: 5px 20px; font-family: Arial, Helvetica, sans-serif; font-size: 14px; font-weight: 400; color: rgb(56, 56, 56); text-align: left;\" class=\"tblCell\"><div style=\"line-height: 125%; text-align: center;\"><span style=\"font-size: 48px; font-family: Impact, Chicago; color: #ffffff; line-height: 125%;\">    \n" +
                "<em><strong>Pedidos Plus</strong></em></span></div></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></div><div id=\"dv_15\" class=\"blk_wrapper\" style=\"\">    \n" +
                "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"blk\" name=\"blk_text\"><tbody><tr><td>    \n" +
                "<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\" width=\"100%\" class=\"bmeContainerRow\"><tbody><tr><td class=\"tdPart\" valign=\"top\" align=\"center\">    \n" +
                "<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"600\" name=\"tblText\" style=\"float:left; background-color:transparent;\" align=\"left\" class=\"tblText\"><tbody><tr><td valign=\"top\" align=\"left\" name=\"tblCell\" style=\"padding: 5px 20px; font-family: Arial, Helvetica, sans-serif; font-size: 14px; font-weight: 400; color: rgb(56, 56, 56); text-align: left;\" class=\"tblCell\"><div style=\"line-height: 150%; text-align: center;\"><span style=\"font-size: 24px; font-family: Tahoma, Arial, Helvetica, sans-serif; color: #ffffff; line-height: 150%;\">Su usuario es: "+user+" y su contraseña es: "+contra+"</span></div></td></tr>" +
                "<tr><td valign=\"top\" align=\"left\" name=\"tblCell\" style=\"padding: 5px 20px; font-family: Arial, Helvetica, sans-serif; font-size: 14px; font-weight: 400; color: rgb(56, 56, 56); text-align: left;\" class=\"tblCell\"><div style=\"line-height: 150%; text-align: center;\"><span style=\"font-size: 24px; font-family: Tahoma, Arial, Helvetica, sans-serif; color: #ffffff; line-height: 150%;\">Cuando inicie sesión cambie la contraseña porfavor</span></div></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></div><div id=\"dv_17\" class=\"blk_wrapper\" style=\"\">    \n" +
                "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"blk\" name=\"blk_image\"><tbody><tr><td>    \n" +
                "<table width=\"100%\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\"><tbody><tr><td align=\"center\" class=\"bmeImage\" style=\"border-collapse: collapse; padding: 20px;\"><img    \n" +
                " src=\""+rutaGlobal+"imglogo/logo_pedidos.png\" class=\"mobile-img-large\" width=\"160\" style=\"max-width: 160px; display: block; width: 160px;\" alt=\"\" border=\"0\"></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></div><div id=\"dv_19\" class=\"blk_wrapper\" style=\"\">    \n" +
                "<table width=\"600\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" class=\"blk\" name=\"blk_divider\" style=\"\"><tbody><tr><td class=\"tblCellMain\" style=\"padding-top:20px; padding-bottom:20px;padding-left:20px;padding-right:20px;\">    \n" +
                "<table class=\"tblLine\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"border-top-width: 0px; border-top-style: none; min-width: 1px;\"><tbody><tr><td><span></span></td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table></div>\n" +
                "</td></tr> \n" +
                "\n" +
                "   </tbody>    \n" +
                "</table></td> </tr>  <tr><td width=\"100%\" class=\"blk_container bmeHolder\" name=\"bmeFooter\" valign=\"top\" align=\"center\" style=\"color: rgb(102, 102, 102); border: 0px none transparent;\" bgcolor=\"\"><div id=\"dv_12\" class=\"blk_wrapper\">    \n" +
                "\n" +
                "</div></td></tr> </tbody>    \n" +
                "</table> </td></tr></tbody>    \n" +
                "</table></td></tr></tbody>    \n" +
                "</table>    \n" +
                "</body>    \n" +
                "</html>";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);



        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");

        properties.put("mail.smtp.debug", "true");
        //properties.put("mail.smtp.starttls.enable", "true");
        //properties.put("mail.smtp.ssl.trust", "smtp.googlemail.com");
        properties.put("mail.smtp.port", "465");


        //properties.put("mail.smtp.ssl.trust", host);
        try {

            session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sEmail,sPass);
                }
            });

            session.setDebug (true);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(sEmail));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(correo));
            message.setSubject("Restauración de contraseña");
            //message.setText("Su nueva contraseña es: "+randomNumber);
            message.setContent(cuerpo, "text/html; charset=utf-8");


                        /*
                        Transport transport = session.getTransport("smtp");
                        transport.connect("c54561.sgvps.net", 465, sEmail, sPass);
                        transport.sendMessage(message,message.getAllRecipients());
                        transport.close();
                        */
            Transport.send(message);

            //new SendMail().execute(message);

            //cambiaContra(urlCambia, String.valueOf(randomNumber),user);
            cambiaContra(urlCambia, contra,user);
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Olvide.this, R.style.AlertDialogTheme);
            builder.setCancelable(false);
            builder.setTitle("Recuperar contraseña");
            builder.setMessage("Se envió un mensaje a tu correo electrónico con una nueva contraseña");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    vaciarTxt();
                    finish();
                }
            });
            builder.show();


        } catch (MessagingException e) {
            progressDialog.dismiss();
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(Olvide.this, R.style.AlertDialogTheme);
            builder.setCancelable(false);
            builder.setTitle("Recuperar contraseña");
            builder.setMessage("Ha ocurrido un problema, porfavor comunicate con soportepedidosplus@gmail.com");
            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                    vaciarTxt();
                    finish();
                }
            });
            builder.show();
        }

        //progressDialog.dismiss();
    }

    public void cambiaContra(final String ServerURL, final String pswd, final String user) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pswd", pswd));
                paramsn.add(new BasicNameValuePair("user", user));

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
                //progressBar1.setVisibility(View.VISIBLE);
                //dialogoOk(result);
                //progressDialog.dismiss();

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(pswd, user);
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

    public void dialogoOk(String respuesta) {

        new AlertDialog.Builder(Olvide.this)
                .setCancelable(false)
                .setTitle("Cambiar Contraseña")
                .setMessage(respuesta)

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Olvide.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .show();
    }

    private class SendMail extends AsyncTask<Message, String, String> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(Olvide.this,
                    "Porfavor espere", "Enviando correo...", true);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);

                return "enviado";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.equals("enviado")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Olvide.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar contraseña");
                builder.setMessage("Se envió un mensaje a tu correo electrónico con una nueva contraseña");
                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(Olvide.this);
                builder.setCancelable(false);
                builder.setTitle("Recuperar contraseña");
                builder.setMessage("Ha ocurrido un problema porfavor comunicate con soporte@compumax.ec");
                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        }
    }
}
