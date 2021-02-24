package ec.compumax.pedidos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

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
import java.util.Timer;
import java.util.TimerTask;

import ec.compumax.pedidos.fragments.ProvinciaFragment;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    Button btn_login;
    EditText txt_user, txt_pswd;
    String gloruc, tokenid;

    TextView registrar, olvide, tv2;
    ProgressBar pBar2;
    ImageView img_inicio;
    View view2;

    int pStatus = 0;
    private Handler handler = new Handler();
    private static final String TAG = "MainActivity";

    //String UrlL = "https://app.pedidosplus.com/webservice_pedidos/login.php";
    String UrlL = "https://app.pedidosplus.com/wsProvincias/login_usuario.php";
    String UrlProvicnias = "https://app.pedidosplus.com/wsProvincias/carga_provincias.php";
    String UrlCiudad = "https://app.pedidosplus.com/wsProvincias/carga_ciudades.php";
    String UrlcorreoPus = "https://app.pedidosplus.com/wsProvincias/carga_correoplus.php";

    LinearLayout llProgressBar;

    int loginon;

    LoginButton btn_loginfacebook;
    private CallbackManager mCallbackManager;

    Spinner sp_provincia, sp_ciudad;

    ArrayList<String> provincias;
    ArrayList<String> nidprovincia = new ArrayList<String>();
    ArrayList<String> baseprovincia = new ArrayList<String>();
    ArrayList<String> usuariobase = new ArrayList<String>();
    ArrayList<String> clavebase = new ArrayList<String>();
    ArrayList<String> nciudad = new ArrayList<String>();
    ArrayList<String> nidciudad = new ArrayList<String>();
    ArrayList<String> idpciudad = new ArrayList<String>();

    String base_datos, ciudadid;

    SignInButton btn_logingoogle;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    int logingoogle;

    String correoplus, pswdplus, rutaGlobal;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCallbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        provincias = new ArrayList<String>();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SharedPreferences prefs = getSharedPreferences("Preferences", 0);
        loginon = prefs.getInt("logoplus", 0);
        logingoogle = prefs.getInt("logingoogle", 0);

        txt_user = (EditText) findViewById(R.id.txt_user);
        txt_pswd = (EditText) findViewById(R.id.txt_pswd);
        registrar = (TextView) findViewById(R.id.registrar);
        olvide = (TextView) findViewById(R.id.olvide);
        tv2 = (TextView) findViewById(R.id.textView2);
        llProgressBar = findViewById(R.id.llProgressBar);
        img_inicio = (ImageView) findViewById(R.id.img_inicio);
        view2 = (View) findViewById(R.id.view2);
        btn_loginfacebook = (LoginButton) findViewById(R.id.btn_loginfacebook);
        sp_provincia = (Spinner) findViewById(R.id.sp_provincia);
        sp_ciudad = (Spinner) findViewById(R.id.sp_ciudad);
        btn_logingoogle = (SignInButton) findViewById(R.id.btn_logingoogle);

        pBar2 = (ProgressBar) findViewById(R.id.progressBar2);

        pBar2.setVisibility(View.VISIBLE);

        provincias.add("Provincia");
        nidprovincia.add("00");
        baseprovincia.add("00");
        usuariobase.add("00");
        clavebase.add("00");
        nciudad.add("Ciudad");
        nidciudad.add("00");


        btn_login = (Button) findViewById(R.id.btn_login);


        /*
        Log.e("MAIN", "normal " + loginon);
        if (loginon == 1) {
            Log.e("logeado", "esta logueado");
            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();
        } else {
            Log.e("logeado", "no esta logueado");
            if (AccessToken.getCurrentAccessToken() != null) {
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);
                finish();
                Log.e("facebook", "si esta logueado");
            } else {
                if (logingoogle == 1) {
                    Intent intent = new Intent(MainActivity.this, Menu.class);
                    startActivity(intent);
                    finish();
                    Log.e("google", "si esta logueado ");
                } else {
                    Log.e("google", "no esta logueado");
                }
            }
        }
         */


        Glide.with(this)
                .load(R.drawable.logo1)
                .override(300, 300)
                .into(img_inicio);

        view2.setBackground(getDrawable(R.drawable.fondoamarilloinicio));

        //btn_loginfacebook.setPermissions();
        btn_loginfacebook.setReadPermissions("email");

        btn_loginfacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                handleFacebookAccessToken(loginResult.getAccessToken());

                /*SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("tokenid", tokenid);
                //editor.putString("base_datos", base_datos);
                //editor.putString("ciudad", ciudadid);
                editor.putInt("loginface", 1);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Informacion.class);
                intent.putStringArrayListExtra("provincias", provincias);
                intent.putStringArrayListExtra("nidprovincia", nidprovincia);
                intent.putStringArrayListExtra("baseprovincia", baseprovincia);
                intent.putStringArrayListExtra("usuariobase", usuariobase);
                intent.putStringArrayListExtra("clavebase", clavebase);
                intent.putStringArrayListExtra("nciudad", nciudad);
                intent.putStringArrayListExtra("nidciudad", nidciudad);
                intent.putStringArrayListExtra("idpciudad", idpciudad);
                intent.putExtra("correoplus", correoplus);
                intent.putExtra("pswdplus", pswdplus);

                startActivity(intent);*/

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Cancelo la solicitud", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "Error de solicitud " + error, Toast.LENGTH_SHORT).show();
            }
        });

        btn_logingoogle.setSize(SignInButton.SIZE_WIDE);
        btn_logingoogle.setColorScheme(SignInButton.COLOR_DARK);
        btn_logingoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginGoogle();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txt_user.getWindowToken(), 0);

                llProgressBar.setVisibility(View.VISIBLE);
                //pBar2.setVisibility(View.VISIBLE);
                //tv2.setVisibility(View.VISIBLE);
                consultaLogin(UrlL, txt_user.getText().toString().trim(), txt_pswd.getText().toString().trim(), view, tokenid);
            }
        });

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("correoplus", correoplus);
                editor.putString("pswdplus", pswdplus);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Registrarse.class);
                //Intent intent = new Intent(MainActivity.this, Login.class);
                startActivity(intent);

                //ProvinciaFragment pf = new ProvinciaFragment(provincias, nidprovincia, baseprovincia, nciudad, nidciudad, idpciudad);
                //pf.show(getSupportFragmentManager(), "Dialogo Provincia");

            }
        });

        olvide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("correoplus", correoplus);
                editor.putString("pswdplus", pswdplus);
                editor.commit();

                Intent intent = new Intent(MainActivity.this, Olvide.class);
                startActivity(intent);
            }
        });

        locationStart();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            String channelId = "fcm_default_channel";
            String channelName = "Weather";
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_HIGH));
        }

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d("token1", "Key: " + key + " Value: " + value);
            }
        }

        getToken();
        //mostrarProvicnias(UrlProvicnias);
        //mostrarCiudades(UrlCiudad);
        //mostrarCorreoPlus(UrlcorreoPus);
        cargarCorreoFS();

        /*
        sp_provincia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                pBar2.setVisibility(View.VISIBLE);
                base_datos=baseprovincia.get(position);
                mostrarCiudades(UrlCiudad, nidprovincia.get(position));
                //Toast.makeText(MainActivity.this, baseprovincia.get(position), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_ciudad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, nidciudad.get(position), Toast.LENGTH_LONG).show();
                ciudadid = nidciudad.get(position);
                if(nidciudad.get(position).contains("00")){
                    btn_login.setEnabled(false);
                    btn_loginfacebook.setEnabled(false);
                    btn_login.setBackground(getDrawable(R.drawable.boton_login_bloqueado));
                }else {
                    btn_login.setEnabled(true);
                    btn_loginfacebook.setEnabled(true);
                    btn_login.setBackground(getDrawable(R.drawable.boton_login));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

         */


    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.e(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void loginGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResultN(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.e("LOGUEADOG", "SI ESTA");
        } else {
            //goLoginScreen();
            Log.e("LOGUEADOG", "NO ESTA");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("ACTIVITYRES", "DATO " + requestCode);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(null);
                // [END_EXCLUDE]
            }
        }

        /*if (requestCode == SIGN_IN_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            Log.e("CONDICION", "ENTRA " + requestCode);


        }*/
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.aclogin), "Falló la autenticación", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null){
            SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("tokenid", tokenid);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, Informacion.class);
            intent.putStringArrayListExtra("provincias", provincias);
            intent.putStringArrayListExtra("nidprovincia", nidprovincia);
            intent.putStringArrayListExtra("baseprovincia", baseprovincia);
            intent.putStringArrayListExtra("usuariobase", usuariobase);
            intent.putStringArrayListExtra("clavebase", clavebase);
            intent.putStringArrayListExtra("nciudad", nciudad);
            intent.putStringArrayListExtra("nidciudad", nidciudad);
            intent.putStringArrayListExtra("idpciudad", idpciudad);
            intent.putExtra("correoplus", correoplus);
            intent.putExtra("pswdplus", pswdplus);
            startActivity(intent);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            Log.e("EXITO", "DATO " + result);

            SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("tokenid", tokenid);
            //editor.putString("base_datos", base_datos);
            //editor.putString("ciudad", ciudadid);
            editor.putInt("logingoogle", 1);
            editor.commit();

            Intent intent = new Intent(MainActivity.this, Informacion.class);
            intent.putStringArrayListExtra("provincias", provincias);
            intent.putStringArrayListExtra("nidprovincia", nidprovincia);
            intent.putStringArrayListExtra("baseprovincia", baseprovincia);
            intent.putStringArrayListExtra("usuariobase", usuariobase);
            intent.putStringArrayListExtra("clavebase", clavebase);
            intent.putStringArrayListExtra("nciudad", nciudad);
            intent.putStringArrayListExtra("nidciudad", nidciudad);
            intent.putStringArrayListExtra("idpciudad", idpciudad);
            intent.putExtra("correoplus", correoplus);
            intent.putExtra("pswdplus", pswdplus);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "No se puede iniciar sesión", Toast.LENGTH_SHORT).show();
            Log.e("GOOGLEHA", "FALLO " + result.getStatus());
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) this.getSystemService(Activity.LOCATION_SERVICE);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
    }

    public void getToken() {
        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Log.e(TAG, "Key: " + token);
                        tokenid = token;
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                        //txtRegId.setText(token);

                    }
                });
        // [END retrieve_current_token]
    }

    public void consultaLogin(final String ServerURL, final String user, final String pswd, final View ver, final String token) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("user", user));
                paramsn.add(new BasicNameValuePair("clave", pswd));
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

                super.onPostExecute(result);
                Log.e("resvalc", "est vacio" + result);

                JSONObject json = null;
                try {
                    json = new JSONObject(result);

                    String usu, rucusu, creditou;

                    JSONArray jsonArray = json.optJSONArray("result");

                    if (jsonArray.length() == 0) {
                        Log.e("resval", "user psw incorrecto");
                        //pBar2.setVisibility(View.GONE);
                        //tv2.setVisibility(View.GONE);
                        llProgressBar.setVisibility(View.GONE);
                        Snackbar snackbar = Snackbar.make(ver, "Usuario y/o contraseña incorrectos", Snackbar.LENGTH_LONG);
                        View snackbarLayout = snackbar.getView();
                        snackbarLayout.setBackgroundColor(getResources().getColor(R.color.colorSnack));
                        TextView textView = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
                        textView.setTextColor(Color.WHITE);
                        //textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_outline_black_24dp, 0, 0, 0);
                        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.barsnack));
                        snackbar.show();

                    }

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        usu = (jsonArrayChild.getString("login"));
                        rucusu = (jsonArrayChild.getString("ruc"));
                        creditou = (jsonArrayChild.getString("credito"));
                        gloruc = rucusu;

                        Log.e("usu", "" + usu);

                        if (usu.equals(txt_user.getText().toString())) {
                            SharedPreferences prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("loginusu", usu);
                            editor.putString("ruc", gloruc);
                            editor.putInt("logoplus", 1);
                            editor.putString("clavepedido", txt_pswd.getText().toString().trim());
                            editor.putString("creditou", creditou);
                            //editor.putString("base_datos", base_datos);
                            //editor.putString("ciudad", ciudadid);
                            editor.commit();

                            Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_SHORT).show();

                            /*
                            Intent intent = new Intent(MainActivity.this, Informacion.class);
                            intent.putStringArrayListExtra("provincias", provincias);
                            intent.putStringArrayListExtra("nidprovincia", nidprovincia);
                            intent.putStringArrayListExtra("baseprovincia", baseprovincia);
                            intent.putStringArrayListExtra("nciudad", nciudad);
                            intent.putStringArrayListExtra("nidciudad", nidciudad);
                            intent.putStringArrayListExtra("idpciudad", idpciudad);
                            startActivity(intent);

                             */

                            ProvinciaFragment pf = new ProvinciaFragment(provincias, nidprovincia, baseprovincia, usuariobase, clavebase, nciudad, nidciudad, idpciudad);
                            pf.show(getSupportFragmentManager(), "Dialogo Provincia");

                            //final Intent intent = new Intent(MainActivity.this, Menu.class);
                            //startActivity(intent);

                            txt_user.setText("");
                            txt_pswd.setText("");
                            //pBar2.setVisibility(View.GONE);
                            //tv2.setVisibility(View.GONE);
                            llProgressBar.setVisibility(View.GONE);


                        } else {

                            Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(user, pswd, token);
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
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    public void mostrarProvicnias(final String ServerURL) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                //List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                //paramsn.add(new BasicNameValuePair("user", user));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    //httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

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

                        baseprovincia.add(jsonArrayChild.getString("base_datos"));
                        nidprovincia.add(jsonArrayChild.getString("idprovincia"));
                        usuariobase.add(jsonArrayChild.getString("usuario_base"));
                        clavebase.add(jsonArrayChild.getString("clave_base"));

                        provincias.add(jsonArrayChild.getString("nprovincia"));


                    }

                    //pBar2.setVisibility(View.GONE);
                    //sp_provincia.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, provincias));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void mostrarCiudades(final String ServerURL) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                //List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                //paramsn.add(new BasicNameValuePair("provincia", provincia));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    //httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

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

                nidciudad.clear();
                nciudad.clear();
                idpciudad.clear();
                nciudad.add("Ciudad");
                nidciudad.add("00");
                idpciudad.add("00");

                try {
                    json = new JSONObject(result);

                    JSONArray jsonArray = json.optJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonArrayChild = jsonArray.getJSONObject(i);

                        nidciudad.add(jsonArrayChild.getString("idciudad"));
                        nciudad.add(jsonArrayChild.getString("nciudad"));
                        idpciudad.add(jsonArrayChild.getString("idprovincia"));


                    }

                    pBar2.setVisibility(View.GONE);
                    //sp_ciudad.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, nciudad));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    public void mostrarCorreoPlus(final String ServerURL) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                //List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                //paramsn.add(new BasicNameValuePair("user", user));

                try {
                    HttpClient httpClient = new DefaultHttpClient();

                    HttpPost httpPost = new HttpPost(ServerURL);

                    //httpPost.setEntity(new UrlEncodedFormEntity(paramsn));

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

                        correoplus=(jsonArrayChild.getString("email"));
                        pswdplus=(jsonArrayChild.getString("pswd"));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(MainActivity.this, "fallo " + connectionResult, Toast.LENGTH_LONG);
        Log.e("GOOGLE", "FALLO " + connectionResult);
    }

    public void cargarCorreoFS(){
        db.collection("pais")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if (doc.get("email") != null) {
                                    correoplus=doc.getString("email");
                                    pswdplus=doc.getString("pswd");
                                    rutaGlobal=doc.getString("ruta");
                                    Log.e(TAG, "onEvent: "+correoplus);
                                    SharedPreferences prefsn = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editorn = prefsn.edit();
                                    editorn.putString("rutaGlobal", rutaGlobal);
                                    editorn.commit();
                                }
                            }
                        }
                    }
                });

        db.collection("provincias")
                .orderBy("nprovincia")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        //assert queryDocumentSnapshots != null;
                        if(queryDocumentSnapshots!=null){
                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if(doc.get("provincia")!=null){
                                    Log.e(TAG, "onEvent: "+doc.getString("base_datos") );
                                    baseprovincia.add(doc.getString("base_datos"));
                                    nidprovincia.add(doc.getString("provincia"));
                                    usuariobase.add(doc.getString("usuario_base"));
                                    clavebase.add(doc.getString("clave_base"));

                                    provincias.add(doc.getString("nprovincia"));

                                }
                            }
                        }
                    }
                });

        db.collection("ciudades")
                .orderBy("nciudad")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        assert queryDocumentSnapshots != null;
                        if(!queryDocumentSnapshots.isEmpty()){

                            nidciudad.clear();
                            nciudad.clear();
                            idpciudad.clear();
                            nciudad.add("Ciudad");
                            nidciudad.add("00");
                            idpciudad.add("00");

                            for(QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                if(doc.get("ciudad")!=null){

                                    nidciudad.add(doc.getString("ciudad"));
                                    nciudad.add(doc.getString("nciudad"));
                                    idpciudad.add(doc.getString("provincia"));


                                }
                            }
                        }
                    }
                });

        pBar2.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user!=null){
            Intent intent = new Intent(MainActivity.this, Menu.class);
            startActivity(intent);
            finish();
        }else{
            if(loginon==1){
                Intent intent = new Intent(MainActivity.this, Menu.class);
                startActivity(intent);
                finish();
            }
        }

    }
}
