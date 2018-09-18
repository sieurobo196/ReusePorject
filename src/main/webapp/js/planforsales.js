function resetPlanOfSaleForm() {
  $('#mcp_form')[0].reset();
  $('#mcp_combo_implement').val(0);
  $('#mcp_combo_implement').select2();
  var date = new Date();
    $('#combo_current_month').val(date.getMonth());
    $('#combo_current_month').select2();
  $('#goods_name').val(0);
  $('#goods_name').select2();

  $('#goods_unit').val(0);
  $('#goods_unit').select2();
}

function getUnitsByGoods() {
  var goodsId = $("#goods_name option:selected")
  var data = new Object();
  data.goodsId = goodsId.val();
  requestToServer('POST', contextPath + '/ajax/goods/goodsUnits', data,
          function (dataSuccess) {
            var items = getContentList(dataSuccess);
            if (items != null) {
              renderOptionUnits('#goods_unit', items, '-- Chọn đơn vị --');
            }
          },
          function (error) {
            console.log('can not get goods list');
          });
}

function renderOptionUnits(comboBoxSelector, items, label) {
  var $domCbo = $(comboBoxSelector);
  renderComboBoxEmpty(comboBoxSelector, label);
  for (var i = 0; i < items.length; i++) {
    var item = items[i];
    var $domOption = $("<option value = '" + item.units.id + "'>" + item.units.name + "</option>");
    $domCbo.append($domOption);
  }
}

function resetForm() {
  // reset all input
  $('#mcp_form').trigger("reset");
  $('#frm_startDate').val(getCurrentDate());
  $('#frm_finishDate').val(getCurrentDate());
}

function getImplementEmployee() {
//  var provinceId = $("#mcp_combo_implement option:selected");
    getMonth();
//    if (provinceId.val() != 0) {
//        $('#mcp_name').val(provinceId.text().split('-')[1] + ' - ' + getCurrentDate());
//        $('#mcp_implement_error').html('');
//    }
}

function getCurrentDate() {
  var d = new Date();
  var month = d.getMonth() + 1;
    var day = d.getDate();
    //(day < 10 ? '0' : '') + day + 
  return (month < 10 ? '0' : '') + month + '/' + d.getFullYear();
}

function getMonth() {
    var d = new Date();
    var month = $("#combo_current_month option:selected").val();
    month = parseInt(month);

    // set name plan again
    var provinceId = $("#mcp_combo_implement option:selected");
    if (provinceId.val() == 0) {
        $('#mcp_implement_error').html('Vui lòng chọn nhân viên thực hiện kế hoạch');
        return;
    } else {
        $('#mcp_implement_error').html('');
        $('#mcp_name_error').html('');
    }
    var year = d.getFullYear();
    if (month < d.getMonth()) {
        year = year + 1;
    }
    var mon = ((month + 1)  < 10 ? '0' : '') + (month + 1);
    $('#mcp_name').val(provinceId.text().split('-')[1] + ' - ' + mon + '/' + year);
}


function dateFormat(timeField) {
  var date = $(timeField);
  var dateTime = date.val().split(' ');
  date.val(dateTime[0]);
}

/**
 * Check emptype filed
 * @param {type} field is filed you want to check
 * @param {type} message send message to filed
 */
function checkEmptys(field, message, messageId) {
  var el = $(field);
  if (el.val().length === 0) {
    if (messageId === null) {
      $('#a_messageError').html(message);
    } else {
      $(messageId).html(message);
    }
    el.focus();
    return true;
  }
  return false;
}

function validateForm() {
  var isOK = true;
  if (checkEmptys('#mcp_name', 'Vui lòng nhập tên kế hoạch.', '#mcp_name_error')) {
    isOK &= false;
  } else {
    isOK &= true;
    $('#mcp_name_error').html('');
  }

  var provinceId = $("#mcp_combo_implement option:selected").val();
  if (parseInt(provinceId) === 0) {
    isOK &= false;
    $('#mcp_implement_error').html('Vui lòng chọn nhân viên thực hiện kế hoạch');
  } else {
    isOK &= true;
    $('#mcp_implement_error').html('');
  }

//  if (checkEmptys('#frm_startDate', 'Vui lòng chọn ngày bắt đầu.', '#date_name_error') ||
//          checkEmptys('#frm_finishDate', 'Vui lòng chọn ngày kết thúc.', '#date_name_error')) {
//    isOK &= false;
//  } else {
//    isOK &= true;
//    $('#date_name_error').html('');
//  }
  // check format date of th begine date and finish date

//    if (ivalidDate('#frm_startDate') && ivalidDate('#frm_finishDate')) {
//        isOK &= true;
//        $('#date_name_error').html('');
//
//    } else {
//        isOK &= false;
//        $('#date_name_error').html('Thời gian không hợp lệ');
//    }

  		if (checkEmptys('#salesPerMonth', 'Vui lòng nhập doanh số tháng.', '#mcp_salesPerMonth_error')) {
  			isOK &= false;
  		} else {
  			isOK &= true;
  			$('#mcp_salesPerMonth_error').html('');
  		}
  		
//  		if (checkEmptys('#salesFocusPerMonth', 'Vui lòng nhập doanh số sản phẩm trọng tâm tháng.', '#mcp_salesFocusPerMonth_error')) {
//  			isOK &= false;
//  		} else {
//  			isOK &= true;
//  			$('#mcp_salesFocusPerMonth_error').html('');
//  		}

//    if ($('#tr_list_empty').length !== 0) {
//        isOK &= false;
//        $('#table_sales_details').html('Vui lòng chọn sản phẩm cho chỉ tiêu.');
//    } else {
//        isOK &= true;
//        $('#table_sales_details').html('');
//    }

  		

	  if (isOK == 1) {
		var newPOSVal = $('#newPOS').val();
		var salesPerMonth = $('#salesPerMonth').val();
  		var salesPerMonthVal  = salesPerMonth.split(',');
  		salesPerMonth = '';
  		for(var i = 0; i < salesPerMonthVal.length; i++){
  			salesPerMonth += salesPerMonthVal[i].toString();
  		}
  		
		var salesFocusPerMonthVal = $('#salesFocusPerMonth').val();
		var salesFocusPerMonth  = salesFocusPerMonthVal.split(',');
		salesFocusPerMonthVal = '';
  		for(var i = 0; i < salesFocusPerMonth.length; i++){
  			salesFocusPerMonthVal += salesFocusPerMonth[i].toString();
  		}
  		
		if (!isNumber(newPOSVal) || parseInt(newPOSVal) < 0) {
			isOK &= false;
			$('#mcp_newPOS_error').html(
					'Số phát triển điểm mới phải là số nguyên dương.');
		} else {
			isOK &= true;
			$('#mcp_newPOS_error').html('');
		}

// if (!isNumber(salesPerMonthVal) || parseInt(salesPerMonthVal) < 0) {
//      isOK &= false;
//      $('#mcp_salesPerMonth_error').html('Doanh số phải là số nguyên dương.');
//    } else {
//      isOK &= true;
//      $('#mcp_salesPerMonth_error').html('');
//    }

//    if (!isNumber(salesFocusPerMonthVal) || parseInt(salesFocusPerMonthVal) < 0) {
//      isOK &= false;
//      $('#mcp_salesFocusPerMonth_error').html('Doanh số SP trọng tâm tháng phải là số nguyên dương.');
//    } else {
//        if (parseInt(salesPerMonthVal) < parseInt(salesFocusPerMonthVal)) {
//        isOK &= false;
//        $('#mcp_salesFocusPerMonth_error').html('Doanh số sản phẩm trọng tâm phải nhỏ hơn doanh số.');
//      } else {
//        isOK &= true;
//        $('#mcp_salesFocusPerMonth_error').html('');
//      }
//    }
       if (parseInt(salesPerMonth) < parseInt(salesFocusPerMonthVal)) {
            isOK &= false;
            $('#mcp_salesFocusPerMonth_error').html('Doanh số sản phẩm trọng tâm phải nhỏ hơn doanh số.');
        } else {
        	isOK &= true;
        	$('#mcp_salesFocusPerMonth_error').html('');
        	if(isOK == 1){
        		document.getElementById("salesPerMonth").value = parseInt(salesPerMonth);
          	    document.getElementById("salesFocusPerMonth").value = parseInt(salesFocusPerMonthVal);
        	}
        }
   }

  return isOK;
}

function createMCP() {
  if (validateForm() == 1) {
    $("#mcp_form").submit();
  }
}

function ivalidDate(date)
{
  var dateField = $(date);
  // regular expression to match required date format
  var re = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
  var regs = dateField.val().match(re);
  if (regs) {
    // day value between 1 and 31
    if (regs[1] < 1 || regs[1] > 31) {
      dateField.focus();
      return false;
    }
    // month value between 1 and 12
    if (regs[2] < 1 || regs[2] > 12) {
      dateField.focus();
      return false;
    }
    // year value between 1902 and 2015
    if (regs[3] < 1902 || regs[3] > (new Date()).getFullYear()) {
      dateField.focus();
      return false;
    }
  } else {
    dateField.focus();
    return false;
  }

  return true;
}

/**
 * set value when chekbox on change
 * @param {type} checkBox is checkbon you want set value
 */
function getStatusCheckox(checkBox) {
  $(checkBox).change(function () {
    if (this.checked) {
      this.setAttribute('value', 1);
    } else {
      this.setAttribute('value', 0);
    }
  });
}

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}
//function deleteGoods(id) {
//  $('#tr_' + id).remove();
//}
function addGoods() {
  var trListEmpty = $('#tr_list_empty');
    if (trListEmpty.length !== 0) {
        $('#goodsTable').empty();
        $('#table_sales_details').html('');
  }
  var goodsNameCom = $('#goods_name');
  var goodUnitCom = $('#goods_unit');
  var mcpSaleStatusCom = $('#mcp_sales_detail_status');

  if (goodsNameCom.val() == 0) {
    alert('Vui lòng chọn sản phẩm.');
    return;
  }

  if (goodUnitCom.val() == 0) {
    alert('Vui lòng chọn đơn vị cho sản phẩm.');
    return;
  }

  var quantity = $('#goods_quantity').val();
  if (!isNumber(quantity) || parseInt(quantity) < 0) {
    alert('Số lượng phải là số nguyên dương');
    return;
  }

  if (mcpSaleStatusCom.val() == 0) {
    alert('Vui lòng chọn trạng thái.');
    return;
  }
  var idGoods = goodsNameCom.val();
  if ($('#quantity_' + idGoods).length == 0) {
    var goodsName = goodsNameCom.find(":selected");
    var goodsUnit = goodUnitCom.find(":selected");
    var mcpSalesStatus = mcpSaleStatusCom.find(":selected");
    var index = $('#goodsTable').children('tr').length;

    var tr = '<tr id="tr_mcp_sales_details_' + index + '">';
        tr += '<input type="hidden" id="input_mcp_sales_details_' + index + '_id" name="mcp.mcpSalesDetailss[' + index + '].id" value="0"/>';
        tr += '<input type="hidden" ="input_mcp_sales_details_' + index + '_deletedUser" name="mcp.mcpSalesDetailss[' + index + '].deletedUser" value="0"/>';
    tr += '<td align="left">';
    tr += goodsName.text();
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].goodss.id" value="' + idGoods + '"/>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].goodss.name" value="' + goodsName.text() + '"/>';
    tr += '</td>';

    tr += '<td align="center">';
    tr += '<label>' + goodsUnit.text() + '</label>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].units.id" value="' + goodsUnit.val() + '"/>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].units.name" value="' + goodsUnit.text() + '"/>';
    tr += '</td>';

    tr += '<td align="center">';
    tr += '<label id="lb_quantity_' + idGoods + '">' + quantity + '</label>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].quantity" id="quantity_' + idGoods + '" value="' + quantity + '" style="width:35px;"/>';
    tr += '</td>';

    tr += '<td align="center" style="display: none">';
    tr += '<label>' + mcpSalesStatus.text() + '</label>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].statuss.id" value="' + mcpSalesStatus.val() + '"/>';
        tr += '	<input type="hidden" name="mcp.mcpSalesDetailss[' + index + '].statuss.name" value="' + mcpSalesStatus.text() + '"/>';
    tr += '</td>';

    tr += '<td align="center" style="display: none">';
    if ($('#mcp_sales_detail_isactive').is(":checked")) {
            tr += '<input disabled="true" type="checkbox" name="mcp.mcpSalesDetailss[' + index + '].isActive" style="width:35px;" checked="true" value="1"/>';
    } else {
            tr += '<input disabled="true" type="checkbox" name="mcp.mcpSalesDetailss[' + index + '].isActive"  style="width:35px;" value="0"/>';
    }

    tr += '</td>';
    tr += '<td align="center">';
    tr += '	<a href="javascript:deleteGoods(' + index + ');">Xóa</a>';
    tr += '</td>';
    tr += '</tr>';
        $('#goodsTable').append($(tr));
        resetCombo('#goods_name');
        resetCombo('#goods_unit');
        $('#goods_quantity').val(0);
  } else {
    $('#quantity_' + idGoods).val(parseFloat(quantity) + parseFloat($('#quantity_' + idGoods).val()));
    $('#lb_quantity_' + idGoods).text($('#quantity_' + idGoods).val());
  }
}

function resetCombo(comboname, val) {
    $(comboname).val(val).change();
    $(comboname).select2();
}

function deleteGoods(id) {
    var mcpdtId = $('#input_mcp_sales_details_' + id + '_id');
    if (parseInt(mcpdtId.val()) === 0) {
        $('#tr_mcp_sales_details_' + id).remove();
    } else {
        $('#tr_mcp_sales_details_' + id).toggle();
        $('#input_mcp_sales_details_' + id + '_deletedUser').val(1);
    }

}
function changeNumber(el) {
  if (!isNumber(el.value) || parseFloat(el.value) < 0) {
    alert('Phải nhập vào số lớn hơn 0');
    el.value = '0';
    el.focus();
    el.select();
  }
}