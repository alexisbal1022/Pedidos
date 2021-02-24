package ec.compumax.pedidos.Recycler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ec.compumax.pedidos.Otros.CircleTransform;
import ec.compumax.pedidos.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Filterable{

    Context context;
    List<DataAdapter> dataAdapters;

    private List<DataAdapter> contactList;

    String idciudad, rutaGlobal;


    public RecyclerViewAdapter(Context context, List<DataAdapter> dataAdapters, String idciudad, String rutaGlobal) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;

        this.contactList = dataAdapters;
        this.idciudad = idciudad;
        this.rutaGlobal = rutaGlobal;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_categorias, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        DataAdapter dataAdapter =  dataAdapters.get(position);

        holder.txtCategoria.setText(dataAdapter.getCategoria());
        //holder.imgcategoria.setImageBitmap(dataAdapter.getImagen());
        String cat = dataAdapter.getIdcat();
        String normal = dataAdapter.getImagenurl();
        String codificada = Uri.encode(normal);
        //String cab = "https://pedidosplus.com/app/pedidos/_lib/file/imgciudad"+idciudad+"/categoria"+cat+"/";
        String cab = rutaGlobal+"imgciudad"+idciudad+"/categoria"+cat+"/";
        Log.e("normal",""+normal);
        Log.e("codificada",""+codificada);
        Log.e("las dos",""+cab+codificada);

        Glide.with(context)
                .load(cab+codificada)
                .circleCrop()
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgcategoria);

        /*Picasso.with(context)
                .load(cab+codificada)
                //.transform(new CircleTransform())
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(holder.imgcategoria);*/

    }

    @Override
    public int getItemCount() {
        return dataAdapters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtCategoria;
        public ImageView imgcategoria;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoria = (TextView) itemView.findViewById(R.id.txt_categoria);
            imgcategoria = (ImageView) itemView.findViewById(R.id.img_categoria);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString == null || charString.isEmpty()) {
                    dataAdapters = contactList;
                } else {
                    List<DataAdapter> filteredList = new ArrayList<>();
                    for (DataAdapter row : contactList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getCategoria().toLowerCase().contains(charString.toLowerCase())) {
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
                dataAdapters = (ArrayList<DataAdapter>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
