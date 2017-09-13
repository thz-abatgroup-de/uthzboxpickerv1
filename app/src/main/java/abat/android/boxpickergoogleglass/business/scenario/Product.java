package abat.android.boxpickergoogleglass.business.scenario;

/**
 * Created by DAJ on 28.01.2016.
 */
public class Product {
    private String matnr;
    private String shelf;
    private String huNo;
    private String pUnits;

    public Product(String matnr, String shelf, String huNo, String pUnits) {
        this.matnr = matnr;
        this.shelf = shelf;
        this.huNo = huNo;
        this.pUnits = pUnits;
    }

    public Product(){}

    public String getMatnr() {
        return matnr;
    }

    public String getShelf() {
        return shelf;
    }

    public String getHuNo() {
        return huNo;
    }

    public String getPUnits(){ return pUnits; }                     //SEDI

    public void setMatnr(String matnr) {
        this.matnr = matnr;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public void setHuNo(String huNo) {
        this.huNo = huNo;
    }

    public void setPUnits(String pUnits){this.pUnits = pUnits; }    //SEDI

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        return getMatnr().equals(product.getMatnr());
    }

    @Override
    public int hashCode() {
        return getMatnr().hashCode();
    }
}
