package ec.compumax.pedidos.Recycler;

public class DataTotalesI implements Item{

    Double total,subtotal,iva,niva;

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public Double getIva() {
        return iva;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public Double getNiva() {
        return niva;
    }

    public void setNiva(Double niva) {
        this.niva = niva;
    }

    @Override
    public int getViewType() {
        return 1;
    }
}
