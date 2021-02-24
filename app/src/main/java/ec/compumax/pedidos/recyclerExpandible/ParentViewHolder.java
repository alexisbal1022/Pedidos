package ec.compumax.pedidos.recyclerExpandible;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import ec.compumax.pedidos.R;

public class ParentViewHolder extends GroupViewHolder {
    public TextView textView_parent;
    public TextView expandDown;
    public TextView expandUp;

    public ParentViewHolder(View itemView) {
        super(itemView);
        textView_parent = itemView.findViewById(R.id.parentTitle);
        expandDown = itemView.findViewById(R.id.expandDown);
        expandUp = itemView.findViewById(R.id.expandUp);
    }

    @Override
    public void expand() {
        super.expand();
        expandDown.setVisibility(View.VISIBLE);
        expandUp.setVisibility(View.GONE);
        //textView_parent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_down_black_24dp,0);
    }

    @Override
    public void collapse() {
        super.collapse();
        expandDown.setVisibility(View.GONE);
        expandUp.setVisibility(View.VISIBLE);
        //textView_parent.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_keyboard_arrow_up_black_24dp,0);
    }

    public void setGroupName(ExpandableGroup groupName){
        textView_parent.setText(groupName.getTitle());
    }
}
