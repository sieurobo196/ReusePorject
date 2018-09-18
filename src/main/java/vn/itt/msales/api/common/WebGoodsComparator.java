package vn.itt.msales.api.common;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import vn.itt.msales.entity.Goods;

/**
 *
 * @author ChinhNQ
 */
public class WebGoodsComparator implements Comparator<Goods> {

    private Collator vnCollator = Collator.getInstance(new Locale("vi"));

    @Override
    public int compare(Goods o1, Goods o2) {
        if (o1.getName() == null) {
            return -1;
        } else if (o2.getName() == null) {
            return 1;
        } else {
            return vnCollator.compare(o1.getName(), o2.getName());
        }
    }
}
