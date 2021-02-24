package ec.compumax.pedidos.recyclerExpandible;

import android.os.Parcel;
import android.os.Parcelable;

public class ChildData implements Parcelable {
    String name;
    String rucci;
    String telefono;
    String referencia;
    String direccion;
    String moto;
    String placa;
    String estado;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMoto() {
        return moto;
    }

    public void setMoto(String moto) {
        this.moto = moto;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRucci() {
        return rucci;
    }

    public void setRucci(String rucci) {
        this.rucci = rucci;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ChildData(Parcel parcel){

        name = parcel.readString();
        rucci = parcel.readString();
        telefono = parcel.readString();
    }

    public ChildData(String name, String rucci, String telefono, String referencia, String direccion, String moto, String placa, String estado) {
        this.name = name;
        this.rucci = rucci;
        this.telefono = telefono;
        this.direccion = direccion;
        this.referencia = referencia;
        this.moto = moto;
        this.placa = placa;
        this.estado = estado;
    }

    public static final Creator<ChildData> CREATOR = new Creator<ChildData>() {
        @Override
        public ChildData createFromParcel(Parcel in) {
            return new ChildData(in);
        }

        @Override
        public ChildData[] newArray(int size) {
            return new ChildData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(rucci);
        parcel.writeString(telefono);
    }
}
