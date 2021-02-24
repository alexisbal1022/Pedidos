package ec.compumax.pedidos.Recycler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.function.Function;

import ec.compumax.pedidos.Otros.DBHelperPedidos;
import ec.compumax.pedidos.Otros.UtilBD;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.fragments.CategoriasFragment;
import ec.compumax.pedidos.fragments.MiPedidoFragment;

public class AdapterMiPedido extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<DataMiPedidoI> dataAdapters;
    String urlEmp = "https://app.pedidosplus.com/wsProvincias/elimina_producto.php";
    String urlEmpt = "https://app.pedidosplus.com/wsProvincias/elimina_productotodo.php";
    String urlMP="https://app.pedidosplus.com/wsProvincias/inserta_producto.php";


    private final int ITEM1 = 1;
    private final int ITEM2 = 2;

    String base_datos;
    DBHelperPedidos mydb;

    private List<Item> items = new ArrayList<>();

    public AdapterMiPedido(Context context, List<DataMiPedidoI> dataAdapters) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;

    }

    public AdapterMiPedido(List<Item> items,Context context,String base_datos) {
        this.items = items;
        this.context = context;
        this.base_datos = base_datos;
        mydb = new DBHelperPedidos(context, UtilBD.NOMBRE_BD, null, 1);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (getItemViewType(position)){
            case ITEM1:
                DataTotalesI item1 = (DataTotalesI) items.get(position);
                Item1Holder item1Holder = (Item1Holder)holder;
                item1Holder.lbl_subtotalt.setText(item1.getSubtotal().toString());
                item1Holder.lbl_subtotalcont.setText(item1.getNiva().toString());
                item1Holder.lbl_ivat.setText(item1.getIva().toString());
                item1Holder.lbl_totalt.setText(item1.getTotal().toString());

                break;
            case ITEM2:
                final DataMiPedidoI item2 = (DataMiPedidoI) items.get(position);
                final Item2Holder item2Holder = (Item2Holder)holder;



                item2Holder.txt_minumero.setText(Integer.toString(item2.getNumero()));
                item2Holder.txt_mipedido.setText(item2.getPedido());
                item2Holder.txt_miprecio.setText(item2.getPrecio().toString());
                item2Holder.txt_midetalle.setText(item2.getDetalle());
                item2Holder.img_elimina.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        items.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,items.size());
                        eliminaProducto(urlEmpt,item2.getId(),base_datos);

                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment myFragment = new MiPedidoFragment();

                        activity.getSupportFragmentManager().beginTransaction()
                                .remove(activity.getSupportFragmentManager()
                                        .findFragmentById(R.id.content_principal))
                                .commit();

                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.content_principal, myFragment)
                                .commit();
                        Log.e("Elimino","Se eliminó");
                    }
                });

                item2Holder.img_menos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        eliminaProducto(urlEmp,item2.getId(),base_datos);
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment myFragment = new MiPedidoFragment();

                        activity.getSupportFragmentManager().beginTransaction()
                                .remove(activity.getSupportFragmentManager()
                                        .findFragmentById(R.id.content_principal))
                                .commit();

                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.content_principal, myFragment)
                                .commit();
                    }
                });
                item2Holder.img_mas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //insertaProducto(urlMP,item2.getIdpedido(),item2.getIdproducto(),"1",item2.getPreciou(),base_datos);
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        Fragment myFragment = new MiPedidoFragment();

                        activity.getSupportFragmentManager().beginTransaction()
                                .remove(activity.getSupportFragmentManager()
                                        .findFragmentById(R.id.content_principal))
                                .commit();

                        activity.getSupportFragmentManager().beginTransaction()
                                .add(R.id.content_principal, myFragment)
                                .commit();
                    }
                });

                item2Holder.img_menu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showPopupMenu(item2Holder.img_menu,item2.getId(),item2.getIdpedido(),item2.getIdproducto(),view);
                    }
                });

                break;
        }

    }

    private void showPopupMenu(View view, String idmp, String idpedido, String idprod, View viewn) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_opciones, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(idmp,idpedido,idprod,viewn));
        popup.show();
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
        ImageButton img_mas, img_menu;
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



    public void eliminaProducto(final String ServerURL, final String idmp, final String base) {

        final String[] resultado = {""};

        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("idmp", idmp));
                paramsn.add(new BasicNameValuePair("base", base));

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
        sendPostReqAsyncTask.execute(idmp,base);
    }

    public void insertaProducto(final String ServerURL, final String idpedido, final String idproducto, final String cantidad, final String precio, final String base) {

        final String[] resultado = {null};
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> paramsn = new ArrayList<NameValuePair>();

                paramsn.add(new BasicNameValuePair("pedido", idpedido));
                paramsn.add(new BasicNameValuePair("producto", idproducto));
                paramsn.add(new BasicNameValuePair("cant", cantidad));
                paramsn.add(new BasicNameValuePair("tot", precio));
                paramsn.add(new BasicNameValuePair("base", base));

                Log.e("pedidodetalleinserta","es: "+idpedido);

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
                String datres= "Datos insertados";


                if (result.contains(datres)) {
                    //Toast.makeText(getContext(), "Datos insertados exitosamente", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();

                }

            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(idpedido, idproducto, cantidad, precio, base);
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

    private class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        String idmp,idpedido,idprod;
        View viewn;
        public MyMenuItemClickListener(String idmp, String idpedido, String idprod, View viewn) {
            this.idmp=idmp;
            this.idpedido=idpedido;
            //this.preciou=preciou;
            this.idprod=idprod;
            this.viewn=viewn;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {

            switch (menuItem.getItemId()) {
                case R.id.menu_eliminar:
                    //Toast.makeText(context, "Add to elimina "+idmp, Toast.LENGTH_SHORT).show();
                    //eliminaProducto(urlEmpt,idmp,base_datos);
                    mydb.eliminaProductoTodo(idmp,idpedido);
                    AppCompatActivity activity = (AppCompatActivity) viewn.getContext();
                    Fragment myFragment = new MiPedidoFragment();

                    activity.getSupportFragmentManager().beginTransaction()
                            .remove(activity.getSupportFragmentManager()
                                    .findFragmentById(R.id.content_principal))
                            .commit();

                    activity.getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_principal, myFragment)
                            .commit();
                    return true;
                case R.id.menu_mas:
                    //Toast.makeText(context, "Add to mas "+idmp, Toast.LENGTH_SHORT).show();
                    //insertaProducto(urlMP,idpedido,idprod,"1",preciou,base_datos);
                    mydb.agregaProducto(idpedido,idprod,1);
                    AppCompatActivity activity1 = (AppCompatActivity) viewn.getContext();
                    Fragment myFragment1 = new MiPedidoFragment();

                    activity1.getSupportFragmentManager().beginTransaction()
                            .remove(activity1.getSupportFragmentManager()
                                    .findFragmentById(R.id.content_principal))
                            .commit();

                    activity1.getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_principal, myFragment1)
                            .commit();
                    return true;
                case R.id.menu_menos:
                    //Toast.makeText(context, "Play menos "+idmp, Toast.LENGTH_SHORT).show();
                    //eliminaProducto(urlEmp,idmp,base_datos);
                    mydb.eliminaProducto(idmp,idpedido);
                    AppCompatActivity activity2 = (AppCompatActivity) viewn.getContext();
                    Fragment myFragment2 = new MiPedidoFragment();

                    activity2.getSupportFragmentManager().beginTransaction()
                            .remove(activity2.getSupportFragmentManager()
                                    .findFragmentById(R.id.content_principal))
                            .commit();

                    activity2.getSupportFragmentManager().beginTransaction()
                            .add(R.id.content_principal, myFragment2)
                            .commit();
                    return true;
                default:
            }
            return false;
        }
    }
}
