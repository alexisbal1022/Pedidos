package ec.compumax.pedidos.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import ec.compumax.pedidos.R;


public class AyudaFragment extends Fragment {

    View vista;

    FrameLayout content_principal;

    String telfmoto, telfpedidos, distancia, preciokm;

    double distanciad, preciokmd;

    TextView lbl_telfpedido, lbl_telflogistica;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista = inflater.inflate(R.layout.fragment_ayuda, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences("Preferences", 0);
        telfmoto = prefs.getString("telfmotos", "");
        telfpedidos = prefs.getString("telfpedidos", "");


        content_principal = (FrameLayout) vista.findViewById(R.id.content_principal);
        lbl_telfpedido = (TextView) vista.findViewById(R.id.lbl_telfpedido);
        lbl_telflogistica = (TextView) vista.findViewById(R.id.lbl_telflogistica);

        Glide.with(getContext())
                .asBitmap()
                .load(R.drawable.fondogris)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        content_principal.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });

        /*Glide.with(getContext())
                .load(R.drawable.fondogris)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        Drawable drawable = new BitmapDrawable(getContext().getResources(), resource);
                        content_principal.setBackground(drawable);
                    }
                });
*/
        lbl_telflogistica.setText(telfmoto);
        lbl_telfpedido.setText(telfpedidos);

        return vista;
    }

}
