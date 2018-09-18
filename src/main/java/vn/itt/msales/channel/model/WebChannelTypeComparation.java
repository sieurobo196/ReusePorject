/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import vn.itt.msales.entity.ChannelType;

/**
 *
 * @author vtm036
 */
public class WebChannelTypeComparation implements Comparator<ChannelType> {

    private Collator vnCollator = Collator.getInstance(new Locale("vi"));

    @Override
    public int compare(ChannelType o1, ChannelType o2) {
        if (o1.getId() == null) {
            return -1;
        } else if (o2.getId() == null) {
            return 1;
        } else {
            return vnCollator.compare(o1.getId().toString(), o2.getId().toString());
        }
    }
}
