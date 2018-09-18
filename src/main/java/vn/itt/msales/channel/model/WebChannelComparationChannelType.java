/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.model;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import vn.itt.msales.entity.Channel;

/**
 *
 * @author ChinhNQ
 */
public class WebChannelComparationChannelType implements Comparator<Channel> {
 private Collator vnCollator = Collator.getInstance(new Locale("vi"));

    @Override
    public int compare(Channel o1, Channel o2) {
        if (o1.getChannelTypes().getId() == null) {
            return -1;
        } else if (o2.getChannelTypes().getId() == null) {
            return 1;
        } else {
            return vnCollator.compare(o1.getChannelTypes().getId().toString(), o2.getChannelTypes().getId().toString());
        }
    }
}
