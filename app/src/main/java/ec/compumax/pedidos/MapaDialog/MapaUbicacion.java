package ec.compumax.pedidos.MapaDialog;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ec.compumax.pedidos.R;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MapaUbicacion extends AppCompatDialogFragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraMoveCanceledListener, GoogleMap.OnCameraIdleListener {
    private TextView mapa_direccion;
    private TextView mapa_latitud;
    private TextView mapa_longitud;
    private MapaDialogListener listener;
    Geocoder geocoder = null;

    //View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_mapa_ubicacion, null);

        builder.setView(view)
                .setTitle("Arrastre el marcador para obtener su ubicación")
                .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        SupportMapFragment f = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapViewn);
                            getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();

                    }
                })
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String direc = mapa_direccion.getText().toString();
                        String lat = mapa_latitud.getText().toString();
                        String lon = mapa_longitud.getText().toString();
                        listener.applyTexts(direc, lat, lon);
                        dialogInterface.dismiss();
                        SupportMapFragment f = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapViewn);

                            getActivity().getSupportFragmentManager().beginTransaction().remove(f).commit();

                    }
                });

        mapa_direccion = view.findViewById(R.id.mapa_direccion);
        mapa_latitud = view.findViewById(R.id.mapa_latitud);
        mapa_longitud = view.findViewById(R.id.mapa_longitud);

        SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.mapViewn);
        mapFragment.getMapAsync(this);

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (MapaDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() +
                    "must implement ExampleDialogListener");
        }
    }

    GoogleMap map;
    Boolean actualPosition = true;
    Double longitudOrigen, latitudOrigen;



    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        map.setOnCameraIdleListener(this);
        map.setOnCameraMoveStartedListener(this);
        map.setOnCameraMoveListener(this);
        map.setOnCameraMoveCanceledListener(this);

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
                    mapa_latitud.setText(String.valueOf(location.getLatitude()));
                    mapa_longitud.setText(String.valueOf(location.getLongitude()));

                    //latitudOrigen = -1.498943;
                    //longitudOrigen = -78.020953;

                    //lbllatitud.setText(latitudOrigen.toString());
                    //lbllongitud.setText(longitudOrigen.toString());
                    setLocation(location);


                    actualPosition = false;
                    //Toast.makeText(getContext(), latitudOrigen+" / "+longitudOrigen, Toast.LENGTH_LONG).show();

                    LatLng miPosicion = new LatLng(latitudOrigen, longitudOrigen);

                    //map.addMarker(new MarkerOptions().position(miPosicion).title("Aqui estoy yo").draggable(true));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitudOrigen, longitudOrigen))
                            .zoom(17)
                            .bearing(90)
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                }
            }
        });

        //map.setOnMarkerDragListener(this);

    }

    @Override
    public void onCameraMoveStarted(int reason) {

        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            Log.e("REASON_GESTURE","OnCameraMoveStartedListener");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION) {
            Log.e("REASON_API_ANIMATION","OnCameraMoveStartedListener");
        } else if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION) {
            Log.e("REASON_DEVELOPER_A","OnCameraMoveStartedListener");
        }
    }

    @Override
    public void onCameraMove() {
        Log.e("onCameraMove","The camera is moving.");
    }

    @Override
    public void onCameraMoveCanceled() {
        Log.e("onCameraMoveCanceled","Camera movement canceled.");
    }

    @Override
    public void onCameraIdle() {
        Log.e("latitud",""+map.getCameraPosition().target.latitude);
        Log.e("longitud",""+map.getCameraPosition().target.longitude);
        //map.getCameraPosition();
        if((map.getCameraPosition().target.latitude)!=0.0){
            mapa_latitud.setText(String.valueOf(map.getCameraPosition().target.latitude));
            mapa_longitud.setText(String.valueOf(map.getCameraPosition().target.longitude));
            Location loc = new Location("dummyprovider");
            loc.setLatitude(map.getCameraPosition().target.latitude);
            loc.setLongitude(map.getCameraPosition().target.longitude);
            setLocation(loc);
        }else{
            //Toast.makeText(getContext(),"La ubicación esta deshabilitada",Toast.LENGTH_LONG).show();
        }

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
                    mapa_direccion.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public interface MapaDialogListener {
        void applyTexts(String direccion, String latitud, String longitud);
    }


}
