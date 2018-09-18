package vn.itt.msales.config.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.itt.msales.database.dbrouting.MsalesBranches;
import vn.itt.msales.entity.Promotion;
import vn.itt.msales.promotion.service.PromotionService;
import vn.itt.msales.services.DataService;

/**
 *
 * @author ChinhNQ
 */
@Service
public class MsalesPromotionEvent {

    @Autowired
    private PromotionService promotionService;
    @Autowired
    private DataService dataService;

    public void changePromotionStatus() {

        try {
            for (int branh : MsalesBranches.BRANCHES) {
                // open database
                dataService.setBranch(branh);
                List<Promotion> listRun = promotionService.getRunPromotion();
                List<Promotion> listEnd = promotionService.getEndPromotion();

                if (listRun != null && !listRun.isEmpty()) {
                    List<Promotion> list = setPromotionRunning(listRun);
                    dataService.updateArray(list);
                    System.out.println(">>>changePromotionStatus#PromotionRun: " + list.size());
                }

                if (listEnd != null && !listEnd.isEmpty()) {
                    List<Promotion> list = setPromotionEnd(listEnd);
                    dataService.updateArray(list);
                    System.out.println(">>>changePromotionStatus#PromotionEnd: " + list.size());
                }
            }
        } catch (Exception e) {
            System.err.println(">>>changePromotionStatus: " + e.getMessage());
        }
    }

    private List<Promotion> setPromotionRunning(List<Promotion> list) {
        List<Promotion> listPromotions = new ArrayList<>();
        for (Promotion promotion : list) {
            // set promotion from 21 (begin created) to 22 (running)
            promotion.setStatusId(22);

            listPromotions.add(promotion);
        }

        return listPromotions;
    }

    private List<Promotion> setPromotionEnd(List<Promotion> list) {
        List<Promotion> listPromotions = new ArrayList<>();
        for (Promotion promotion : list) {
            // set promotion from 22 (is running) to 28 (end)
            promotion.setStatusId(28);
            listPromotions.add(promotion);
        }
        return listPromotions;
    }
}
