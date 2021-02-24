package ec.compumax.pedidos.Recycler;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionInflater;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ec.compumax.pedidos.Otros.CircleTransform;
import ec.compumax.pedidos.Otros.PicassoCache;
import ec.compumax.pedidos.R;
import ec.compumax.pedidos.fragments.ConfirmadosFragment;
import ec.compumax.pedidos.fragments.DetalleFragment;
import ec.compumax.pedidos.fragments.MiPedidoFragment;

public class AdapterProducto extends RecyclerView.Adapter<AdapterProducto.ViewHolder> implements Filterable {

    Context context;
    List<DataProducto> dataAdapters;
    private List<DataProducto> localList;

    String idciudad, idpedidoInterno,rutaGlobal;

    //String pedido;

    public AdapterProducto(Context context, List<DataProducto> dataAdapters, String idciudad, String idpedidoInterno, String rutaGlobal) {
        super();
        this.context = context;
        this.dataAdapters = dataAdapters;
        this.localList = dataAdapters;
        this.idciudad = idciudad;
        this.idpedidoInterno = idpedidoInterno;
        this.rutaGlobal = rutaGlobal;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_productos, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final DataProducto dataAdapter =  dataAdapters.get(position);

        holder.txtProducto.setText(dataAdapter.getProducto());
        //holder.imgProducto.setImageBitmap(dataAdapter.getImagen());
        holder.txtPrecio.setText(dataAdapter.getPrecio());

        String categ = dataAdapter.getIdcategoria();
        String loc = dataAdapter.getIdlocal();
        String normal = dataAdapter.getImageurl();
        String codificada = Uri.encode(normal);
        String cab = rutaGlobal+"imgciudad"+idciudad+"/categoria"+categ+"/local"+loc+"/";


        Glide.with(context)
                .asBitmap()
                .load(cab+codificada)
                .error(R.drawable.ic_add_shopping_cart_black_24dp)
                .into(new BitmapImageViewTarget(holder.imgProducto){
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.imgProducto.setImageDrawable(circularBitmapDrawable);
                    }
                });


        holder.imgProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefsn = context.getSharedPreferences("Preferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editorn = prefsn.edit();
                editorn.putString("nproducto", dataAdapter.getProducto());
                editorn.putString("imagenp", dataAdapter.getImageurl());
                editorn.putString("detalle", dataAdapter.getDetalle());
                editorn.putString("preciop", dataAdapter.getPrecio());
                editorn.putString("ivap", dataAdapter.getIva());
                editorn.putString("idp", dataAdapter.getId());
                editorn.putString("idpedidoInterno", idpedidoInterno);
                editorn.commit();

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                Fragment myFragment = new DetalleFragment();

                activity.getSupportFragmentManager().beginTransaction()
                        .add(R.id.content_principal, myFragment)
                        .commit();



            }
        });
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
                    List<DataProducto> filteredList = new ArrayList<>();
                    for (DataProducto row : localList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getProducto().toLowerCase().contains(charString.toLowerCase())) {
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
                dataAdapters = (ArrayList<DataProducto>) filterResults.values;
                notifyDataSetChanged();
            }
        };

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtProducto, txtPrecio;
        public ImageView imgProducto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProducto = (TextView) itemView.findViewById(R.id.txt_productos);
            imgProducto = (ImageView) itemView.findViewById(R.id.img_productos);
            txtPrecio = (TextView) itemView.findViewById(R.id.txt_precio);
        }
    }




}
