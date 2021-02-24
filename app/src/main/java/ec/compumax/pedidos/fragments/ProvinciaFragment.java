package ec.compumax.pedidos.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.lang.reflect.Array;
import java.util.ArrayList;

import ec.compumax.pedidos.Informacion;
import ec.compumax.pedidos.MainActivity;
import ec.compumax.pedidos.Menu;
import ec.compumax.pedidos.R;


public class ProvinciaFragment extends DialogFragment implements GoogleApiClient.OnConnectionFailedListener {

    Spinner sp_provincia, sp_ciudad;
    Button btn_cancelar, btn_continuar;
    ArrayList<String> nprovincia;
    ArrayList<String> idprovincia;
    ArrayList<String> baseprovincia;
    ArrayList<String> usuariobase;
    ArrayList<String> clavebase;
    ArrayList<String> nciudad;
    ArrayList<String> nidciudad;
    ArrayList<String> idpciudad;
    ArrayList<String> nombreciudad = new ArrayList<String>();
    ArrayList<String> codigociudad = new ArrayList<String>();

    int loginon, loginface, logingoogle;

    GoogleApiClient googleApiClient;

    String base_datos, ciudadid, usuario_base, clave_base;

    public ProvinciaFragment(ArrayList<String> nprovincia, ArrayList<String> idprovincia, ArrayList<String> baseprovincia, ArrayList<String> usuariobase,ArrayList<String> clavebase,ArrayList<String> nciudad, ArrayList<String> nidciudad, ArrayList<String> idpciudad) {
        // Required empty public constructor
        this.nprovincia = nprovincia;
        this.idprovincia = idprovincia;
        this.baseprovincia = baseprovincia;
        this.usuariobase = usuariobase;
        this.clavebase = clavebase;
        this.nciudad = nciudad;
        this.nidciudad = nidciudad;
        this.idpciudad = idpciudad;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        loginon = prefs.getInt("logoplus", 0);
        loginface = prefs.getInt("loginface", 0);
        logingoogle = prefs.getInt("logingoogle", 0);

        return crearDialogo();
    }

    private AlertDialog crearDialogo() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_provincia, null);
        setCancelable(false);
        builder.setView(v);


        sp_provincia = v.findViewById(R.id.sp_provincia);
        sp_ciudad = v.findViewById(R.id.sp_ciudad);
        btn_cancelar = v.findViewById(R.id.btn_cancelar);
        btn_continuar = v.findViewById(R.id.btn_continuar);

        //nprovincia = (ArrayList<String>) getActivity().getIntent().getStringArrayListExtra("nprovincia");
        //idprovincia = (ArrayList<String>) getActivity().getIntent().getStringArrayListExtra("idprovincia");

        sp_provincia.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, nprovincia));

        sp_provincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                base_datos = baseprovincia.get(position);
                usuario_base= usuariobase.get(position);
                clave_base = clavebase.get(position);

                if (!nombreciudad.isEmpty()) {
                    nombreciudad.clear();
                    codigociudad.clear();
                }

                nombreciudad.add("Ciudad");
                codigociudad.add("Ciudad");

                //Toast.makeText(getContext(), idprovincia.get(position), Toast.LENGTH_SHORT).show();
                for (int i = 0; i < nidciudad.size(); i++) {
                    if (idprovincia.get(position).equals(idpciudad.get(i))) {
                        nombreciudad.add(nciudad.get(i));
                        codigociudad.add(nidciudad.get(i));
                        Log.e("CIUDAD", "DATO " + nciudad.get(i));
                    } else {
                        Log.e("CIUDADno", "DATO " + idpciudad.get(i));
                    }
                }
                sp_ciudad.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombreciudad));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getContext(), codigociudad.get(position), Toast.LENGTH_SHORT).show();
                ciudadid = codigociudad.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (loginface == 1) {
                    dismiss();
                    LoginManager.getInstance().logOut();
                    goLoginScreen();
                    SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("loginface", 0);
                    editor.commit();

                } else {
                    if (logingoogle == 1) {

                        dismiss();
                        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();

                        googleApiClient = new GoogleApiClient.Builder(getContext())
                                //enableAutoManage(getActivity(),this)
                                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                                .build();

                        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(@NonNull Status status) {
                                if (status.isSuccess()) {
                                    goLoginScreen();
                                    SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putInt("logingoogle", 0);
                                    editor.commit();
                                } else {
                                    Toast.makeText(getContext(), "No se puedo cerrar sesión", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        if (loginon == 1) {
                            SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt("logoplus", 0);
                            editor.commit();
                            dismiss();
                        }
                    }
                }
            }
        });
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();

                SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("base_datos", base_datos);
                editor.putString("usuario_base", usuario_base);
                editor.putString("clave_base", clave_base);
                editor.putString("ciudad", ciudadid);
                editor.commit();

                Intent intent = new Intent(getContext(), Menu.class);
                startActivity(intent);
                //getActivity().finish();

            }
        });

        return builder.create();
    }

    private void goLoginScreen() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
