function deleteProduct(id) {
    var url = getAppRootPath() + "/product/delete/" + id;
    var ans = confirm('Xóa hàng hóa này?');
    if (!ans)
        return;
    window.location.href = url;
}
function selectCompany(company) {
    var url = getAppRootPath() + "/product/getCatByCompany/" + $(company).val();
    $.ajax({
        type: 'GET',
        dataType: 'json',
        url: url,
        success: function (data) {
            renderComboBox($('#goodsCatId'), data, $('#goodsCatId option:first').text());
            if ($('#parentGoodsId').length != 0) {
                changeCat($('#parentGoodsId'), $('#goodsCatId'));
            }
        }
    });
}
function changeCat(parentGoods, cat) {
    var catId = cat.val();
    var url = getAppRootPath() + "/product/goodsparentbycat/" + $('#companyID').val() + "/" + catId;
    $.ajax({
        type: 'GET',
        dataType: 'json',
        url: url,
        success: function (data) {
            renderComboBox(parentGoods, data, "--- Nhóm hàng ---");
        }
    });
    checkVasField(cat);
}
// kiem tra neu hang hoa thuoc loai vas thi hien thi ma dich vu va goi dich vu
function checkVasField(cat) {
    var catId = cat.val();
    if (catId == '6') {
        $("tr#tr_service").attr('style', '');
        $("tr#tr_shortCode").attr('style', '');
    } else {
        $("tr#tr_service").attr('style', 'display:none');
        $("tr#tr_shortCode").attr('style', 'display:none');
    }
}
//thay het cac option
function renderComboBox($domCbo, items, msg) {
    renderComboBoxEmpty($domCbo, msg);// remove old options
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var $domOption = $("<option value = '" + item.id + "'>" + item.name
                + "</option>");
        $domCbo.append($domOption);
    }
}
//xoa cac option
function renderComboBoxEmpty($domCbo, msg) {
    if ($domCbo.find('option').length > 1) {
        // chi xoa neu danh sach co tu 2 option tro len
        $domCbo.empty();
        var $domOption = $("<option value = '0' selected='selected'>" + msg
                + "</option>");
        $domCbo.append($domOption);
    }
}
function addGoodsUnit() {
    var trListEmpty = $('#tr_list_empty');
    if (trListEmpty.length != 0) {
        $('#goodsTable').empty();
    }

    var goodUnitCom = $('#unitId');
    var childUnit = $('#childUnitIdX');
    if (goodUnitCom.val() == 0) {
        alert('Chưa nhập đơn vị');
        return;
    }

    var quantity = $('#goods_quantity').val();
    if (!isNumber(quantity) || parseInt(quantity) < 0 || parseInt(quantity) == 0) {
        alert('Số lượng phải là số nguyên dương lớn hơn 0');
        return;
    }

    var ChildUnit = document.getElementById("childUnitIdX");
    //  var id = ChildUnit.value;
    var goodsUnit = goodUnitCom.find(":selected");
    var childUnitId = childUnit.find(":selected");
    var id = ChildUnit.value;

    var index = $('#goodsTable').children('tr').length;
//    if (index >= 1) {
//        var childName = $('#childUnitId_' + (index - 1)).find(":selected").text();
//        var childUnitIndex = document.getElementById(("childUnitId_" + (index - 1)).toString()).value;
//
//        if (childUnitIndex !== goodUnitCom.val()) {
//            alert("Đơn vị kế tiếp phải là " + childName);
//            return;
//        }
//    }
    if (index > 3) {
        alert('Quy định 1 sản phẩm chỉ được tối đa 4 đơn vị');
        return;

    }
    for (var j = 0; j < index; j++) {
        var childUnit_j1 = $('#input_' + j).val();
        if (childUnit_j1 !== id && childUnit_j1 !=undefined) {
            alert('Các đơn vị nhỏ phải giống nhau');
            return;
        }
    }
    if (id === "") {
        alert('Đơn vị nhỏ bằng Đơn vị nếu số lượng bằng 1 và Khác đơn vị nếu số lượng khác 1');
        return;
    }
    if (childUnitId.val() == goodsUnit.val() && parseInt(quantity) !== 1) {
        alert('Số lượng phải bằng 1');
        return;
    }
    if (childUnitId.val() !== goodsUnit.val() && parseInt(quantity) == 1) {
        alert('Số lượng phải khác 1');
        return;
    }
    var unitId = document.getElementById("unitId").value;
    for (var i = 0; i < index; i++) {
        var UnitId = $('#unitId_' + i).val();
        if (UnitId == unitId) {
            alert('Đơn vị đã thêm rồi , chọn đơn vị khác');
            return;
        }
//        var ChildUnitId = document.getElementById("childUnitIdX").value;
//        if (UnitId == ChildUnitId) {
//            alert('Đơn vị nhỏ không được giống Đơn vị');
//            return;
//        }
    }
    

    var tr = '<tr id="tr_goods_unit_' + index + '">';
    tr += '<input type="hidden" name="goodsUnits[' + index + '].id"/>';
    tr += '<input type="hidden" name="goodsUnits[' + index + '].deletedUser"/>';
    tr += '<td align="center">';
    tr += '<label id="unit_name_' + index + '">' + goodsUnit.text() + '</label>';
    tr += '	<input id ="unitId_' + index + '" type="hidden" name="goodsUnits[' + index + '].units.id" value="' + goodsUnit.val() + '"/>';
    tr += '</td>';
    tr += '<td align="center">';
    tr += '	<input align="center" type="text" name="goodsUnits[' + index + '].quantity" id="quantity_' + index + '" value="' + quantity + '" style="text-align:center; width:150px;"/>';
    tr += '</td>';
    tr += '<td align="center">';
    tr += '<select style="width:170px" id="childUnitId_' + index + '" class="select2_combo select2-offscreen" name="childUnitId_' + index + '" onchange="SelectElement(' + index + ')">';
    for (var i = 0; i < ChildUnit.options.length; i++) {
        if (ChildUnit.options[i].value === id) {
            tr += '<option value="' + ChildUnit.options[i].value + '"  selected="selected">' + ChildUnit.options[i].text + '</option>';
        } else {
            tr += '<option value="' + ChildUnit.options[i].value + '">' + ChildUnit.options[i].text + '</option>';
        }
    }
    //ChildUnit.innerHTML +
    tr += '</select>';
    tr += '	<input id="input_' + index + '" type="hidden" name="goodsUnits[' + index + '].childUnitIds.id" value="' + childUnitId.val() + '"/>';
    tr += '</td>';
    tr += '<td align="center">';
    tr += '	<a href="javascript:deleteGoodsUnit(' + index + ');">Xóa</a>';
    tr += '</td>';
    tr += '</tr>';
    $('#goodsTable').append($(tr));
    $("#childUnitId_" + index).select2();

}
function deleteGoodsUnit(id) {
    $('#tr_goods_unit_' + id).toggle();
    $('#tr_goods_unit_' + id).remove();
}
function createGoods() {
    var salesPerMonth = $('#price').val();
    var index = $('#goodsTable').children('tr').length;
    for (var i = 0; i < index; i++) {
        var quantity = $('#quantity_' + i).val();
        var unitId = $('#unitId_' + i).val();
        var unitName = $('#unit_name_' + i).text();

        var childUnit = $('#input_' + i).val();

        for (var j = 0; j < index; j++) {
            var childUnit_j = $('#input_' + j).val();
            if (childUnit !== childUnit_j && childUnit_j !== undefined && childUnit !== undefined) {
                alert('Các đơn vị nhỏ phải giống nhau');
                return;
            }
        }
        if (index > 4) {
            alert('Quy định 1 sản phẩm chỉ được tối đa 4 đơn vị');
            return;

        }
        var x = i - 1;
        if (unitId != undefined && childUnit != undefined) {
//            if (x >= 0) {
//                var childUnit_id1 = $('#input_' + x).val();
//
//                if (unitId !== childUnit_id1) {
//                    alert('Đơn vị nhỏ phải là ' + unitName);
//                    return;
//                }
//
//            }

            if (unitId !== childUnit && parseInt(quantity) === 1) {
                alert('Số lượng phải khác 1');
                return;
            }
            if (unitId === childUnit && quantity != undefined && parseInt(quantity) !== 1) {
                alert('Số lượng phải bằng 1');
                return;

            }
            if (parseInt(quantity) === 0) {
                alert('Số lượng phải là số nguyên dương lớn hơn 0');
                return;
            }
        }
    }

//    var y = index - 1;
//    var quantity_y = $('#quantity_' + y).val();
//    if (quantity_y != undefined && parseInt(quantity_y) != 1) {
//        alert('Phải thêm đơn vị nhỏ nhất');
//        return;
//    }
    var a=0;
    for(var i=0;i<4 ;i++){
        var quantity = $('#quantity_' + i).val();
        if(quantity !=undefined && parseInt(quantity)===1){
            a=1;
        }
    }
    if(a==0){
        alert('Phải thêm đơn vị nhỏ nhất');
        return;
    }
    if (salesPerMonth.length > 0) {
        var salesPerMonthVal = salesPerMonth.split(',');
        salesPerMonth = '';
        for (var i = 0; i < salesPerMonthVal.length; i++) {
            salesPerMonth += salesPerMonthVal[i].toString();
        }
        document.getElementById("price").value = parseInt(salesPerMonth);
    }
    $("#goods").submit();
}

function SelectElement(val)
{

    var childUnit = $('#childUnitId_' + val + '').find(":selected").val();
    var inputChild = $('input_' + val + '').val();

    inputChild = childUnit;
    document.getElementById("input_" + val + "").value = inputChild;

}
