/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.services;

import java.util.List;

import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Location;

/**
 *
 * @author vtm036
 */
public interface WebLocationService {

    public List<Location> getListLocationByType(int locationType);

    public List<Location> getListLocationByParent(int locationParent);
    
    public List<OptionItem> getListLocationByListChannelId(List<OptionItem> listChannelId);
}
