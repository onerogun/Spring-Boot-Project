package og.prj.adminservice.product;

import java.util.ArrayList;
import java.util.List;

public class AmountWrapper {
    private List<Product> wrapper;

    public AmountWrapper() {
        if (wrapper == null) {
            wrapper = new ArrayList<>();
        }
    }

    public void addProduct(Product product) {
       this.wrapper.add(product);
    }

    public List<Product> getWrapper() {
        return wrapper;
    }

    public void setWrapper(List<Product> wrapper) {
        this.wrapper = wrapper;
    }
}
