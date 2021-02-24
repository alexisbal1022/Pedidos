package ec.compumax.pedidos.recyclerExpandible;

import android.view.View;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;

import ec.compumax.pedidos.R;

public class ChildViewHolders extends ChildViewHolder {

    public TextView textView_child, txt_rucci, txt_telefono, txt_direccion, txt_referencia, txt_placa, txt_moto, lbl6,lbl7;

    public ChildViewHolders(View itemView) {
        super(itemView);
        textView_child = itemView.findViewById(R.id.option2);
        txt_rucci = itemView.findViewById(R.id.option1);
        txt_telefono = itemView.findViewById(R.id.option5);
        txt_direccion = itemView.findViewById(R.id.option3);
        txt_referencia = itemView.findViewById(R.id.option4);
        txt_moto = itemView.findViewById(R.id.option6);
        txt_placa = itemView.findViewById(R.id.option7);
        lbl6 = itemView.findViewById(R.id.lbl6);
        lbl7 = itemView.findViewById(R.id.lbl7);
    }

    public void setChildText(String name, String ruc, String telefono, String direccion, String referencia, String moto, String placa, String estado){
        textView_child.setText(name);
        txt_rucci.setText(ruc);
        txt_telefono.setText(telefono);
        txt_direccion.setText(direccion);
        txt_referencia.setText(referencia);
        txt_placa.setText(placa);
        txt_moto.setText(moto);

        if(estado.equals("3")||estado.equals("4")){
            txt_placa.setVisibility(View.VISIBLE);
            txt_moto.setVisibility(View.VISIBLE);
            lbl6.setVisibility(View.VISIBLE);
            lbl7.setVisibility(View.VISIBLE);

        }else{
            txt_placa.setVisibility(View.GONE);
            txt_moto.setVisibility(View.GONE);
            lbl6.setVisibility(View.GONE);
            lbl7.setVisibility(View.GONE);
        }
    }
}
