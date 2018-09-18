package vn.itt.msales.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import vn.itt.msales.common.json.MsalesJsonUtils;
import vn.itt.msales.entity.response.MsalesResponse;

/**
 * Đây là class dùng để hiện thị trong các option của Listbox hay Combobox Sử
 * dụng class này để cast các đối tượng sau khi query có chỉ định column sẽ giúp
 * giảm bộ nhớ sử dụng và tránh được việc phải chuyển đổi cũng như sắp xếp lại
 * list- là những thao tác làm chậm các ajax query
 *
 * @author admin
 */


public class OptionItem implements java.io.Serializable {
    private static final long serialVersionUID = -3333653292055665718L;

    private Integer id;
    private String name;// tên
    
    @JsonIgnore
    private String code;// mã viết tắt nếu cần

    @JsonIgnore
    private boolean checked;

    /**
     * Constructor có dùng mã viết tắt
     *
     */
    public OptionItem() {
    }

    public OptionItem(Integer id, String name) {
        this.id = id;
        this.name = name;
        this.code = "";
    }

    public OptionItem(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public OptionItem(Integer id, String name, boolean checked) {
        this.id = id;
        this.name = name;
        this.checked = checked;
    }

    /**
     * Constructor ma
     *
     * @param code
     * @param name
     */
    public OptionItem(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    /**
     * Tạo ra 1 list chỉ có 1 phần tử duy nhất do đó người dùng chỉ có 1 chọn
     * lựa.
     *
     * @param id
     * @param name
     * @return
     */
    public static List<OptionItem> NoOptionList(Integer id, String name) {
        List<OptionItem> list = new ArrayList<>(1);
        list.add(new OptionItem(id, name));
        return list;
    }

    public static List<OptionItem> createOptionListFromResponseString(String msalesResponseString) {
        List<OptionItem> list = new ArrayList<>();
        try {
            MsalesResponse msalesResponse = MsalesJsonUtils.getObjectFromJSON(msalesResponseString, MsalesResponse.class);
            if (msalesResponse.getStatus().getCode() != 200) {
                return list;//empty
            }
            ArrayList<LinkedHashMap> items = (ArrayList) ((LinkedHashMap) msalesResponse.getContents()).get("contentList");
            //LinkedHashMap[] items = MsalesJsonUtils.getObjectFromJSON(contenList, LinkedHashMap[].class);
            for (LinkedHashMap item : items) {
                int id = (int) item.get("id");
                String name = item.get("name").toString();
                OptionItem optionItem = new OptionItem(id, name);
                if (item.get("code") != null) {
                    String code = item.get("code").toString();
                    optionItem.setCode(code);
                }
                list.add(optionItem);
            }
        } catch (Exception ex) {
            System.out.println("Loi" + ex.getMessage());
        }
        return list;
    }

    public static List<OptionItem> createOptionItemListFromHashMap(List<HashMap> list) {
        List<OptionItem> ret = new ArrayList<>();
        for (HashMap hashMap : list) {
            int id = (int) hashMap.get("id");
            String name = hashMap.get("name").toString();
            OptionItem optionItem = new OptionItem(id, name);
            if (hashMap.get("code") != null) {
                String code = hashMap.get("code").toString();
                optionItem.setCode(code);
            }
            ret.add(optionItem);
        }
        return ret;
    }

    public static boolean checkExistId(int id, List<OptionItem> optionItemList) {
        for (OptionItem optionItem : optionItemList) {
            if (id == optionItem.getId()) {
                return true;
            }
        }
        return false;
    }
    
}
