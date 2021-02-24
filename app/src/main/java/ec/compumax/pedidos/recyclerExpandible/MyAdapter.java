package ec.compumax.pedidos.recyclerExpandible;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

import ec.compumax.pedidos.R;

public class MyAdapter extends ExpandableRecyclerViewAdapter<ParentViewHolder,ChildViewHolders> {

    public Context context;

    public MyAdapter(Context context,List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public ParentViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_titulo,parent,false);

        return new ParentViewHolder(view);
    }

    @Override
    public ChildViewHolders onCreateChildViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_elementos,parent,false);

        return new ChildViewHolders(view);
    }

    @Override
    public void onBindChildViewHolder(ChildViewHolders holder, int flatPosition, ExpandableGroup group, int childIndex) {

        final ChildData childData = ((ParentData)group).getItems().get(childIndex);
        holder.setChildText(childData.getName(),childData.getRucci(),childData.getTelefono(),childData.getDireccion(),childData.getReferencia(),childData.getMoto(),childData.getPlaca(),childData.getEstado());
        holder.textView_child.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Toast.makeText(context, "Selected : " + childData.getName(), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onBindGroupViewHolder(ParentViewHolder holder, int flatPosition, ExpandableGroup group) {

        holder.setGroupName(group);
    }
}
