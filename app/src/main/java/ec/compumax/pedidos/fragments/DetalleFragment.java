package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
//import android.widget.NumberPicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;
import com.travijuu.numberpicker.library.NumberPicker;

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

import ec.compumax.pedidos.Otros.CircleTransform;
import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.Recycler.DataLocal;

public class DetalleFragment extends Fragment {

    View vista;
    //NumberPicker np_cantidad;
    String detalle, nomprod, nomimgp, preciop, usulogin, ivap, idpedido, idp, idlocal, idca;
    int cant;
    AppBarLayout app_bar;

    TextView detail_name, detail_description, detail_price, obser;
    EditText txt_observa;
    Button btn_agrega;

    String urlMP = "https://app.pedidosplus.com/wsProvincias/inserta_producto.php";
    NumberPicker numberPicker;

    static String varenvia;

    ImageView img_fondo;

    private int color;
    private Paint paint;
    private Rect rect;
    private RectF rectF;
    private Bitmap result;
    private Canvas canvas;
    private float roundPx;

    String base_datos;

    String idciudad, idpedidoInterno, rutaGlobal;
    DBHelperPedidos mydb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_detalle, container, false);

        mydb = new DBHelperPedidos(getContext(), UtilBD.NOMBRE_BD, null, 1);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        detalle = prefs.getString("detalle", "");
        nomprod = prefs.getString("nproducto", "");
        nomimgp = prefs.getString("imagenp", "");
        preciop = prefs.getString("preciop", "");
        ivap = prefs.getString("ivap", "");
        usulogin = prefs.getString("loginusu", "");
        idpedido = prefs.getString("idpedido", "");
        idpedidoInterno = prefs.getString("idpedidoInterno", "");
        idp = prefs.getString("idp", "");
        idlocal = prefs.getString("local", "");
        idca = prefs.getString("categ", "");
        base_datos = prefs.getString("base_datos", "");
        idciudad = prefs.getString("ciudad", "");
        rutaGlobal = prefs.getString("rutaGlobal", "");

        Log.e("pedidodetalle", "es: " + idpedido);

        app_bar = (AppBarLayout) vista.findViewById(R.id.app_bar);

        detail_name = (TextView) vista.findViewById(R.id.detail_name);
        img_fondo = (ImageView) vista.findViewById(R.id.img_fondo);
        detail_description = (TextView) vista.findViewById(R.id.detail_description);
        detail_price = (TextView) vista.findViewById(R.id.detail_price);
        obser = (TextView) vista.findViewById(R.id.obser);
        txt_observa = (EditText) vista.findViewById(R.id.txt_observa);
        btn_agrega = (Button) vista.findViewById(R.id.btn_agrega);
        numberPicker = (NumberPicker) vista.findViewById(R.id.np_detalle);

        numberPicker.setMin(1);

        detail_description.setText(detalle);
        detail_name.setText(nomprod);
        detail_price.setText(preciop);

        String codificada = Uri.encode(nomimgp);
        String cab = rutaGlobal + "imgciudad" + idciudad + "/categoria" + idca + "/local" + idlocal + "/";
        Log.e("codificada", "" + codificada);
        Log.e("las dos", "" + cab + codificada);

        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.fondosuperior)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        app_bar.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });


        Glide.with(getContext())
                .asBitmap()
                .load(cab+codificada)
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        app_bar.setBackground(drawable);
                        img_fondo.setImageBitmap(getRoundedRectBitmap(resource, 50));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        /*Picasso.with(getContext())
                .load(cab + codificada)
                //.transform(new CircleTransform())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        //app_bar.setBackground(new BitmapDrawable(bitmap));
                        img_fondo.setImageBitmap(getRoundedRectBitmap(bitmap, 50));
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });*/

        //np_cantidad = (NumberPicker) vista.findViewById(R.id.np_cantidad);
        //np_cantidad.setMinValue(1);
        //np_cantidad.setMaxValue(10);

        //np_cantidad.setWrapSelectorWheel(true);
        cant = 1;
        numberPicker.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                //Toast.makeText(getContext(),"valor "+value,Toast.LENGTH_SHORT).show();
                cant = value;
                Log.e("TAG", "valueChanged: " + cant);
            }
        });
        /*np_cantidad.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                Toast.makeText(getContext(),"valor "+newVal,Toast.LENGTH_SHORT).show();
                cant = newVal;
            }
        });*/

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(nomprod);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,
                //      new ProductosFragment()).commit();

                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();


            }
        });

        btn_agrega.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double dp = Double.parseDouble(preciop);
                double ptot = cant * dp;
                String cantn = String.valueOf(cant);
                String ptotn = String.valueOf(ptot);
                int niva = Integer.parseInt(ivap);

                //Log.e("TOTALES","valores: +"+cantn+" "+preciop+" "+ivap+" "+ptotn);
                //insertaProducto(urlMP,idpedido,idp,cantn,preciop,ivap,ptotn,txt_observa.getText().toString().trim(),base_datos);

                Log.e("TAG", "onClick: " + cant + " " + dp);

                if (mydb.insertaMPedido(idpedidoInterno, idp, cant, dp, niva, txt_observa.getText().toString().trim(), nomprod, detalle)) {
                    mydb.close();
                } else {
                    Log.e("Insertar", "No se inserto" + idp);
                }

                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("boton", "1");
                //editorn.putString("idpedido", pedido);
                editorn.commit();

                varenvia = "3";

                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_principal,
                //      new ProductosFragment()).commit();

                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frame_boton,
                                new BotonFragment()).commit();

            }
        });

        return vista;
    }


    public void insertaProducto(final String ServerURL, final String idpedido, final String idproducto, final String cantidad, final String precio, final String iva, final String ptotal, final String observa, final String base) {

        final String[] resultado = {null};
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pedido", idpedido));
                paramsn.add(new BasicNameValuePair("producto", idproducto));
                paramsn.add(new BasicNameValuePair("cant", cantidad));
                paramsn.add(new BasicNameValuePair("precio", precio));
                paramsn.add(new BasicNameValuePair("iva", iva));
                paramsn.add(new BasicNameValuePair("tot", ptotal));
                paramsn.add(new BasicNameValuePair("observa", observa));
                paramsn.add(new BasicNameValuePair("base", base));

                Log.e("pedidodetalleinserta", "es: " + idpedido);

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
                String datres = "Datos insertados";


                if (result.contains(datres)) {
                    //Toast.makeText(getContext(), "Datos insertados exitosamente", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(idpedido, idproducto, cantidad, precio, iva, ptotal, observa, base);
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

    public Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        canvas = new Canvas(result);

        color = 0xff424242;
        paint = new Paint();
        rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        rectF = new RectF(rect);
        roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.BLACK);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return result;
    }

}
