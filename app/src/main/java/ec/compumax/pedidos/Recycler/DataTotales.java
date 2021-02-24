package ec.compumax.pedidos.Recycler;

public class DataTotales implements Item{

    String total,subtotal,iva,niva;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getNiva() {
        return niva;
    }

    public void setNiva(String niva) {
        this.niva = niva;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
