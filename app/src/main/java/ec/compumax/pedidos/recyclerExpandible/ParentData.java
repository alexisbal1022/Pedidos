package ec.compumax.pedidos.recyclerExpandible;

import android.os.Parcel;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import java.util.List;

public class ParentData extends ExpandableGroup<ChildData> {
    public ParentData(String title, List<ChildData> items) {
        super(title, items);
    }

    protected ParentData(Parcel in) {
        super(in);
    }
}
