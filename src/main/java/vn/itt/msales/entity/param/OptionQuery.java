/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.param;

import java.util.ArrayList;

/**
 *
 * @author vtm
 */
public class OptionQuery {
    //1:insert; 2:update
    private int type;
    private  ArrayList objects;

    public OptionQuery() {
    }

    public OptionQuery(int type, ArrayList objects) {
        this.type = type;
        this.objects = objects;
    }

   

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ArrayList<Object> getObjects() {
        return objects;
    }

    public void setObjects(ArrayList<Object> objects) {
        this.objects = objects;
    }
    
    
    
}
