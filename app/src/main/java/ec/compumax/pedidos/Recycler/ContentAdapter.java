package ec.compumax.pedidos.Recycler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ec.compumax.pedidos.R;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(DataMiPedido item);
    }

    private final List<DataMiPedido> items;
    private final OnItemClickListener listener;

    public ContentAdapter(List<DataMiPedido> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_mipedido, parent, false);
        return new ViewHolder(v);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener);
    }

    @Override public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_minumero;
        TextView txt_mipedido;
        TextView txt_miprecio;
        TextView txt_midetalle;
        ImageButton img_elimina;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_minumero = (TextView) itemView.findViewById(R.id.txt_minumero);
            txt_mipedido = (TextView) itemView.findViewById(R.id.txt_mipedido);
            txt_miprecio = (TextView) itemView.findViewById(R.id.txt_miprecio);
            txt_midetalle = (TextView) itemView.findViewById(R.id.txt_midetalle);
            img_elimina = (ImageButton) itemView.findViewById(R.id.img_elimina);
        }

        public void bind(final DataMiPedido item, final OnItemClickListener listener) {
            txt_minumero.setText(item.getNumero());
            txt_mipedido.setText(item.getPedido());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
