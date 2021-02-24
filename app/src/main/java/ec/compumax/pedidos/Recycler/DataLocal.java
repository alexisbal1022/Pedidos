package ec.compumax.pedidos.Recycler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class DataLocal {
    String id;
    String local, data, recargo,dia,idloc,imagenurl, idcategoria,favorito;
    Bitmap imagen;
    int colord, colorf;

    public int getColorf() {
        return colorf;
    }

    public void setColorf(int colorf) {
        this.colorf = colorf;
    }

    public String getFavorito() {
        return favorito;
    }

    public void setFavorito(String favorito) {
        this.favorito = favorito;
    }

    public String getIdcategoria() {
        return idcategoria;
    }

    public void setIdcategoria(String idcategoria) {
        this.idcategoria = idcategoria;
    }

    public String getImagenurl() {
        return imagenurl;
    }

    public void setImagenurl(String imagenurl) {
        this.imagenurl = imagenurl;
    }

    public String getIdloc() {
        return idloc;
    }

    public void setIdloc(String idloc) {
        this.idloc = idloc;
    }

    public int getColord() {
        return colord;
    }

    public void setColord(int colord) {
        this.colord = colord;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getRecargo() {
        return recargo;
    }

    public void setRecargo(String recargo) {
        this.recargo = recargo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        try {
            byte[] byteData = Base64.decode(data, Base64.DEFAULT);
            this.imagen = BitmapFactory.decodeByteArray( byteData, 0,
                    byteData.length);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap getImagen() {
        return imagen;
    }
}
