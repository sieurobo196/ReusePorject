/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vn.itt.msales.entity.response;

import java.util.List;

/**
 *
 * @author vtm036
 * @param <T>
 */
public class MsalesResults<T> {

    private Long count;
    private List<T> contentList;

    public MsalesResults() {

    }

    public MsalesResults(List<T> calss, Long count) {
        this.contentList = calss;
        this.count = count;
    }

    public List<T> getContentList() {
        return contentList;
    }

    public void setContentList(List<T> cal) {
        this.contentList = cal;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

}
