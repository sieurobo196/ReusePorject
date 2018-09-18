/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.common;

import java.util.Comparator;
import vn.itt.msales.entity.Location;

/**
 *
 * @author PhucPD
 */
public class WebMsalesComparatorLocation implements Comparator<Location> {

    @Override
    public int compare(Location o1, Location o2) {
        if (o1.getName() == null) {
            return -1;
        } else if (o2.getName() == null) {
            return 1;
        } else {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
