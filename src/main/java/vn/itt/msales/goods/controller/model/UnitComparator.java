package vn.itt.msales.goods.controller.model;

import java.text.Collator;
import java.util.Comparator;
import vn.itt.msales.entity.Unit;

/**
 *
 * @author ChinhNQ
 */
public class UnitComparator implements Comparator<Unit> {

    @Override
    public int compare(Unit e1, Unit e2) {
        if (e1.getCode().equalsIgnoreCase(e2.getCode())) {
            return 0;
        } else {
            return -1;
        }
    }
}
