package ec.compumax.pedidos.Recycler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

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

import ec.compumax.pedidos.R;
import ec.compumax.pedidos.fragments.MiPedidoFragment;

public class AdapterMiPedidoH extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<DataMiPedido> dataAdapters;
    String urlEmp = "https://app.pedidosplus.com/wsProvincias/elimina_producto.php";


    private final int ITEM1 = 1;
    private final int ITEM2 = 2;

    private List<Item> items = new ArrayList<>();

    public AdapterMiPedidoH(Context context, List<DataMiPedido> dataAdapters) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;

    }

    public AdapterMiPedidoH(List<Item> items) {
        this.items = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        RecyclerView.ViewHolder viewHolder;
        switch (viewType){
            case ITEM1: viewHolder = new Item1Holder(inflater.inflate(R.layout.cardview_totales,parent,false));
                break;
            case ITEM2: viewHolder = new Item2Holder(inflater.inflate(R.layout.cardview_mipedido,parent,false));
                break;
            default: viewHolder = new Item1Holder(inflater.inflate(R.layout.cardview_totales,parent));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)){
            case ITEM1:
                DataTotales item1 = (DataTotales) items.get(position);
                Item1Holder item1Holder = (Item1Holder)holder;
                item1Holder.lbl_subtotalt.setText(item1.getSubtotal());
                item1Holder.lbl_subtotalcont.setText(item1.getNiva());
                item1Holder.lbl_ivat.setText(item1.getIva());
                item1Holder.lbl_totalt.setText(item1.getTotal());



                break;
            case ITEM2:
                final DataMiPedido item2 = (DataMiPedido) items.get(position);
                Item2Holder item2Holder = (Item2Holder)holder;

                item2Holder.txt_minumero.setText(item2.getNumero());
                item2Holder.txt_mipedido.setText(item2.getPedido());
                item2Holder.txt_miprecio.setText(item2.getPrecio());
                item2Holder.txt_midetalle.setText(item2.getDetalle());
                item2Holder.img_elimina.setVisibility(View.GONE);
                item2Holder.img_menos.setVisibility(View.GONE);
                item2Holder.img_mas.setVisibility(View.GONE);
                item2Holder.img_menu.setVisibility(View.GONE);

                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class Item1Holder extends RecyclerView.ViewHolder{
        TextView lbl_subtotalt,lbl_subtotalcont,lbl_ivat,lbl_totalt;
        public Item1Holder(View itemView) {
            super(itemView);
            lbl_subtotalt = (TextView) itemView.findViewById(R.id.lbl_subtotalt);
            lbl_subtotalcont = (TextView) itemView.findViewById(R.id.lbl_subtotalcont);
            lbl_ivat = (TextView) itemView.findViewById(R.id.lbl_ivat);
            lbl_totalt = (TextView) itemView.findViewById(R.id.lbl_totalt);
        }
    }

    class Item2Holder extends RecyclerView.ViewHolder{
        TextView txt_minumero;
        TextView txt_mipedido;
        TextView txt_miprecio;
        TextView txt_midetalle;
        ImageButton img_elimina;
        ImageButton img_menos;
        ImageButton img_mas,img_menu;
        public Item2Holder(View itemView) {
            super(itemView);
            txt_minumero = (TextView) itemView.findViewById(R.id.txt_minumero);
            txt_mipedido = (TextView) itemView.findViewById(R.id.txt_mipedido);
            txt_miprecio = (TextView) itemView.findViewById(R.id.txt_miprecio);
            txt_midetalle = (TextView) itemView.findViewById(R.id.txt_midetalle);
            img_elimina = (ImageButton) itemView.findViewById(R.id.img_elimina);
            img_menos = (ImageButton) itemView.findViewById(R.id.img_menos);
            img_mas = (ImageButton) itemView.findViewById(R.id.img_mas);
            img_menu = (ImageButton) itemView.findViewById(R.id.img_menu);
        }
    }



    public void eliminaProducto(final String ServerURL, final String idmp) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idmp", idmp));

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
        sendPostReqAsyncTask.execute(idmp);
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
