package ec.compumax.pedidos.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.Locale;

import ec.compumax.pedidos.MapaDialog.MapaUbicacion;
import ec.compumax.pedidos.Menu;
import ec.compumax.pedidos.Otros.MyMapFragmentContainer;
import ec.compumax.pedidos.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FacturaFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, MapaUbicacion.MapaDialogListener {

    View vista;

    Button btn_realiza_pedido;
    TextView txt_nombreusu, lbldireccion;
    EditText txt_ruc, txt_razon, txt_dir, txt_ref, txt_telf;

    String urlMF = "https://app.pedidosplus.com/wsProvincias/carga_factura.php";
    String urlFin = "https://app.pedidosplus.com/wsProvincias/fin_pedido.php";
    String loginusu, idpedido, razon, ruc, dir, tel, ref, lati, longi;

    MyMapFragmentContainer mycontentmapa;

    ProgressBar pBar1;

    RadioButton radioactual, radiootro;
    Geocoder geocoder = null;
    private LocationManager locManager;
    private Location loc;
    LinearLayout llProgressBar;

    Button btn_vermapad,btn_selecubica;

    GoogleMap map;
    Boolean actualPosition = true;
    JSONObject jso;
    Double longitudOrigen, latitudOrigen;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_factura, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        loginusu = prefs.getString("loginusu", "");
        idpedido = prefs.getString("idpedido", "");

        btn_realiza_pedido = (Button) vista.findViewById(R.id.btn_realiza_pedido);
        txt_nombreusu = (TextView) vista.findViewById(R.id.txt_nombreusu);
        txt_ruc = (EditText) vista.findViewById(R.id.txt_ruc);
        txt_razon = (EditText) vista.findViewById(R.id.txt_razon);
        txt_dir = (EditText) vista.findViewById(R.id.txt_dir);
        txt_ref = (EditText) vista.findViewById(R.id.txt_ref);
        txt_telf = (EditText) vista.findViewById(R.id.txt_telf);
        lbldireccion = (TextView) vista.findViewById(R.id.lbldireccion);
        radioactual = (RadioButton) vista.findViewById(R.id.radioactual);
        radiootro = (RadioButton) vista.findViewById(R.id.radiootro);
        mycontentmapa = (MyMapFragmentContainer) vista.findViewById(R.id.mycontentmapa);
        llProgressBar = (LinearLayout)vista.findViewById(R.id.llProgressBar);
        Button btn_vermapad = (Button)vista.findViewById(R.id.btn_vermapad);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        btn_vermapad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogMapa();
            }
        });

        llProgressBar.setVisibility(View.VISIBLE);

        mostrarMiFactura(urlMF, loginusu);

        btn_realiza_pedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                asignaText();

                SharedPreferences prefsn = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("boton", "0");
                editorn.putString("otrop", "0");
                editorn.commit();

                finalizaPedido(urlFin, idpedido, ruc, razon, tel, dir, ref, lati, longi);

                dialogoExito();

            }
        });

        Toolbar toolbar = (Toolbar) vista.findViewById(R.id.toolbar_fac);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)// Habilitar Up Button
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mi Factura");

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().
                        remove(getActivity().getSupportFragmentManager().findFragmentById(R.id.content_principal)).commit();

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                        .replace(R.id.frame_boton,
                                new BotonFragment()).commit();
            }
        });

        radiootro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mycontentmapa.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Mantenga presionado el marcador y muevalo hacia donde desee", Toast.LENGTH_LONG).show();
            }
        });
        radioactual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mycontentmapa.setVisibility(View.GONE);
                miUbica();

            }
        });

        if (radioactual.isChecked()){
            miUbica();
        }

        return vista;
    }

    public void miUbica(){
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);


            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

            return;
        }else{
            locManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
            loc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.e("latitud", String.valueOf(loc.getLatitude()));
            Log.e("longitud", String.valueOf(loc.getLongitude()));
            lati = String.valueOf(loc.getLatitude());
            longi = String.valueOf(loc.getLongitude());
            setLocation(loc);
        }
    }


    public void dialogoExito() {

        new AlertDialog.Builder(getContext(), R.style.AlertDialogTheme)
                .setCancelable(false)
                .setMessage("Pedido Exitoso")
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(getContext(), Menu.class);
                        startActivity(intent);


                    }
                })
                .show();
    }

    public void asignaText() {
        razon = txt_razon.getText().toString().trim();
        ruc = txt_ruc.getText().toString().trim();
        //dir = txt_dir.getText().toString().trim();
        tel = txt_telf.getText().toString().trim();
        ref = txt_ref.getText().toString().trim();
        dir = lbldireccion.getText().toString().trim();
    }

    public void mostrarMiFactura(final String ServerURL, final String user) {

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

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        txt_nombreusu.setText(jsonArrayChild.getString("name"));
                        txt_ruc.setText(jsonArrayChild.getString("ruc"));
                        txt_razon.setText(jsonArrayChild.getString("razonsocial"));
                        //txt_dir.setText(jsonArrayChild.getString("direccion"));
                        //txt_ref.setText(jsonArrayChild.getString("referencia"));
                        txt_telf.setText(jsonArrayChild.getString("telefono"));

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

    public void finalizaPedido(final String ServerURL, final String idp, final String ruc, final String razon, final String telf, final String dir, final String ref, final String lati, final String longi) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idpedido", idp));
                paramsn.add(new BasicNameValuePair("ruc", ruc));
                paramsn.add(new BasicNameValuePair("razon", razon));
                paramsn.add(new BasicNameValuePair("telf", telf));
                paramsn.add(new BasicNameValuePair("direccion", dir));
                paramsn.add(new BasicNameValuePair("ref", ref));
                paramsn.add(new BasicNameValuePair("lati", lati));
                paramsn.add(new BasicNameValuePair("longi", longi));

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

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idp, ruc, razon, telf, dir, ref, lati, longi);
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
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);


            }

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }

            return;
        }
        map.setMyLocationEnabled(true);
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

                if (actualPosition) {
                    latitudOrigen = location.getLatitude();
                    longitudOrigen = location.getLongitude();

                    //latitudOrigen = -1.498943;
                    //longitudOrigen = -78.020953;

                    //lbllatitud.setText(latitudOrigen.toString());
                    //lbllongitud.setText(longitudOrigen.toString());
                    setLocation(location);


                    actualPosition = false;
                    //Toast.makeText(getContext(), latitudOrigen+" / "+longitudOrigen, Toast.LENGTH_LONG).show();

                    LatLng miPosicion = new LatLng(latitudOrigen, longitudOrigen);

                    map.addMarker(new MarkerOptions().position(miPosicion).title("Aqui estoy yo").draggable(true));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudOrigen, longitudOrigen))
                            .zoom(17)
                            .bearing(90)
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
            }
        });

        map.setOnMarkerDragListener(this);

    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    lbldireccion.setText(DirCalle.getAddressLine(0));
                    txt_dir.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "Marker " + marker.getId() + " DragStart");

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "Marker " + marker.getId() + " Drag@" + marker.getPosition());

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "Marker " + marker.getId() + " DragEnd" + marker.getPosition());
        LatLng nloc = marker.getPosition();
        Location nloca = new Location(LocationManager.GPS_PROVIDER);
        nloca.setLatitude(nloc.latitude);
        nloca.setLongitude(nloc.longitude);
        lati=String.valueOf(nloca.getLatitude());
        longi=String.valueOf(nloca.getLongitude());
        setLocation(nloca);

    }

    public void openDialogMapa() {
        //MapaUbicacion mapaDialog = new MapaUbicacion();
        //mapaDialog.onStart();
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.content_principal,
                new MapaUbicacion()).commit();
        //mapaDialog.show(getContext(),"");
    }

    @Override
    public void applyTexts(String direccion, String latitud, String longitud) {
        lati = latitud;
        longi = longitud;
        txt_dir.setText(direccion);
    }
}
