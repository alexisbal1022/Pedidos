package ec.compumax.pedidos.Recycler;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.squareup.picasso.Picasso;

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
import ec.compumax.pedidos.R;

public class AdapterFavorito extends RecyclerView.Adapter<AdapterFavorito.MyViewHolder> implements Filterable {

    Context context;
    List<DataLocal> dataAdapters;
    private List<DataLocal> localList;
    String urlFav = "https://app.pedidosplus.com/wsProvincias/elimina_favorito.php";

    String login, base_datos, idciudad,usuario_base, clave_base, rutaGlobal;

    //SharedPreferences prefs;


    public AdapterFavorito(Context context, List<DataLocal> dataAdapters, String login, String base_datos, String idciudad, String usuario_base, String clave_base, String rutaGlobal) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;
        this.localList = dataAdapters;
        //prefs = context.getSharedPreferences("Preferences", 0);
        //login = prefs.getString("loginusu", "");
        this.login = login;
        this.base_datos = base_datos;
        this.idciudad = idciudad;
        this.usuario_base = usuario_base;
        this.clave_base = clave_base;
        this.rutaGlobal = rutaGlobal;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_locales, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final DataLocal dataAdapter = dataAdapters.get(position);

        holder.txtLocal.setText(dataAdapter.getLocal());
        holder.imgLocal.setImageBitmap(dataAdapter.getImagen());
        holder.txtIdlocal.setText(dataAdapter.getId());
        holder.txt_recargo.setText(dataAdapter.getRecargo());
        holder.txt_dia.setText(dataAdapter.getDia());
        holder.txt_dia.setTextColor(dataAdapter.getColord());
        String idlocal = dataAdapter.getIdloc();
        String fav = dataAdapter.getFavorito();
        holder.btn_favorito.setBackgroundTintList(context.getResources().getColorStateList(R.color.favoritosi));

        if(dataAdapter.getDia().equals("Abierto")){
            holder.btn_cerrado.setVisibility(View.GONE);
        }else{
            holder.btn_abierto.setVisibility(View.GONE);
        }

        holder.btn_favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertaFavorito(urlFav, dataAdapter.getIdloc(), login, base_datos,usuario_base,clave_base);
                //holder.btn_favorito.setBackgroundTintList(context.getResources().getColorStateList(R.color.colorAccent));
                dataAdapters.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,dataAdapters.size());
            }
        });


        String loc = dataAdapter.getIdloc();
        String categ = dataAdapter.getIdcategoria();
        String normal = dataAdapter.getImagenurl();
        String codificada = Uri.encode(normal);
        String cab = rutaGlobal+"imgciudad"+idciudad+"/categoria" + categ + "/local" + loc + "/";
        Log.e("normal", "" + normal);
        Log.e("codificada", "" + codificada);
        Log.e("las dos", "" + cab + codificada);

        Glide.with(context)
                .load(cab+codificada)
                .transform(new CircleCrop())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgLocal);

        /*Picasso.get()
                .load(cab + codificada)
                .transform(new CircleTransform())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgLocal);*/


    }

    @Override
    public int getItemCount() {
        return dataAdapters.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString == null || charString.isEmpty()) {
                    dataAdapters = localList;
                } else {
                    List<DataLocal> filteredList = new ArrayList<>();
                    for (DataLocal row : localList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getLocal().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    dataAdapters = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataAdapters;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dataAdapters = (ArrayList<DataLocal>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtLocal, txtIdlocal, txt_recargo, txt_dia;
        public ImageView imgLocal, btn_abierto, btn_cerrado;
        public ImageButton btn_favorito;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txtLocal = (TextView) itemView.findViewById(R.id.txt_local);
            imgLocal = (ImageView) itemView.findViewById(R.id.img_local);
            btn_favorito = (ImageButton) itemView.findViewById(R.id.btn_favorito);
            txtIdlocal = (TextView) itemView.findViewById(R.id.txt_idlocal);
            txt_recargo = (TextView) itemView.findViewById(R.id.txt_recargo);
            txt_dia = (TextView) itemView.findViewById(R.id.txt_dia);
            btn_abierto = (ImageView) itemView.findViewById(R.id.btn_abierto);
            btn_cerrado = (ImageView) itemView.findViewById(R.id.btn_cerrado);

        }

        @Override
        public void onClick(View v) {

        }
    }


    public void insertaFavorito(final String ServerURL, final String idlocal, final String login, final String base,final String usuario, final String clave) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idlocal", idlocal));
                paramsn.add(new BasicNameValuePair("login", login));
                paramsn.add(new BasicNameValuePair("base", base));
                paramsn.add(new BasicNameValuePair("usuario", usuario));
                paramsn.add(new BasicNameValuePair("clave", clave));

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
        sendPostReqAsyncTask.execute(idlocal, login, base, usuario, clave);
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
