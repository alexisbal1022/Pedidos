package ec.compumax.pedidos.Recycler;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ec.compumax.pedidos.Otros.CircleTransform;
import ec.compumax.pedidos.R;

public class AdapterHistorial extends RecyclerView.Adapter<AdapterHistorial.ViewHolder> implements Filterable {

    Context context;
    List<DataHistorial> dataAdapters;
    private List<DataHistorial> localList;

    String idciudad,rutaGlobal;

    public AdapterHistorial(Context context, List<DataHistorial> dataAdapters, String idciudad, String rutaGlobal) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;
        this.localList = dataAdapters;
        this.idciudad = idciudad;
        this.rutaGlobal = rutaGlobal;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_historial, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        DataHistorial dataAdapter =  dataAdapters.get(position);

        holder.txtLocal.setText(dataAdapter.getLocal());
        //holder.imgHistorial.setImageBitmap(dataAdapter.getImagen());
        holder.txtTotal.setText(dataAdapter.getTotal());
        holder.txtFecha.setText(dataAdapter.getFecha());
        holder.txtPedido.setText("# "+dataAdapter.getId());

        String loc = dataAdapter.getIdlocal();
        String categ = dataAdapter.getIdcategoria();
        String normal = dataAdapter.getImagenurl();
        String codificada = Uri.encode(normal);
        String cab = rutaGlobal+"imgciudad"+idciudad+"/categoria"+categ+"/local"+loc+"/";
        Log.e("normal",""+normal);
        Log.e("codificada",""+codificada);
        Log.e("las dos",""+cab+codificada);

        Glide.with(context)
                .load(cab+codificada)
                .transform(new CircleCrop())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgHistorial);

        /*Picasso.with(context)
                .load(cab+codificada)
                .transform(new CircleTransform())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgHistorial);*/

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
                    List<DataHistorial> filteredList = new ArrayList<>();
                    for (DataHistorial row : localList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getLocal().toLowerCase().contains(charString.toLowerCase()) || row.getFecha().contains(charString)) {
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
                dataAdapters = (ArrayList<DataHistorial>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtLocal, txtTotal, txtFecha, txtPedido;
        public ImageView imgHistorial;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLocal = (TextView) itemView.findViewById(R.id.lbl_hlocal);
            imgHistorial = (ImageView) itemView.findViewById(R.id.img_historial);
            txtTotal = (TextView) itemView.findViewById(R.id.lbl_htot);
            txtFecha = (TextView) itemView.findViewById(R.id.lbl_hfecha);
            txtPedido = (TextView) itemView.findViewById(R.id.lbl_numpedido);
        }
    }




}
