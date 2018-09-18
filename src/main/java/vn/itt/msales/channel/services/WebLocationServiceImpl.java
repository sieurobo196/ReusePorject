/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.channel.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import vn.itt.msales.common.OptionItem;
import vn.itt.msales.entity.Location;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
public class WebLocationServiceImpl implements WebLocationService {

    @Autowired
    private DataService dataService;

    @Override
    public List<Location> getListLocationByType(int locationType) {
        String hql = "FROM Location where locationType =" + locationType + " order by name";
        return dataService.executeSelectHQL(Location.class, hql, false, 0, 0);
    }

    @Override
    public List<Location> getListLocationByParent(int locationParent) {
        String hql = "FROM Location where parents.id =" + locationParent + " order by name";
        return dataService.executeSelectHQL(Location.class, hql, false, 0, 0);
    }
    
    public List<OptionItem> getListLocationByListChannelId(List<OptionItem> listChannelId){
    	List<OptionItem> listLocation = new ArrayList<>();
    	String string = "";
    	for(OptionItem optionItem : listChannelId){
    		string += optionItem.getId() + ",";
    	}
    	string += "''";
    	String hql = "SELECT ChannelLocation.locations.id as locationId, ChannelLocation.locations.name as name, "
    			+ " ChannelLocation.locations.locationType as locationType"
    			+ " FROM ChannelLocation as ChannelLocation "
    			+ " WHERE ChannelLocation.deletedUser = 0"
    			+ " AND ChannelLocation.locations.deletedUser = 0"
    			+ " AND ChannelLocation.channels.id IN ("+string+")";
    	List<HashMap> hashMaps = dataService.executeSelectHQL(HashMap.class, hql, true, 0, 0);
    	for(HashMap hm : hashMaps){
    		int locationType = 0;
    		if(hm.get("locationType") != null){
    			locationType = (int) hm.get("locationType");
    		}
    	
    		int locationId = 0;
    		if(hm.get("locationId") != null){
    			locationId = (int) hm.get("locationId");
    		}
    		OptionItem optionItem = new OptionItem();
    		if(locationType == 1){
    			boolean _bool = true;
    			for(OptionItem opItem : listLocation){
    				if(opItem.getId() == locationId){
    					_bool = false;
    					break;
    				}
    			}
    			if(_bool){
    				optionItem.setId((Integer) hm.get("locationId"));
        			optionItem.setName((String) hm.get("name"));
        			listLocation.add(optionItem);
    			}    			
    		}else if(locationType == 2){
    			Location location = dataService.getRowById(locationId, Location.class);
    			boolean _bool = true;
    			for(OptionItem opItem : listLocation){
    				if(opItem.getId() == location.getParents().getId()){
    					_bool = false;
    					break;
    				}
    			}
    			if(_bool){
    				optionItem.setId(location.getParents().getId());
    				optionItem.setName(location.getParents().getName());
    				listLocation.add(optionItem);
    			}
    		}else if(locationType == 3){
    			Location location = dataService.getRowById(locationId, Location.class);
    			boolean _bool = true;
    			for(OptionItem opItem : listLocation){
    				if(opItem.getId() == location.getParents().getParents().getId()){
    					_bool = false;
    					break;
    				}
    			}
    			if(_bool){
    				optionItem.setId(location.getParents().getParents().getId());
    				optionItem.setName(location.getParents().getParents().getName());
    				listLocation.add(optionItem);
    			}
    		}   		
    	}
    	
    	return listLocation;
    }
}
