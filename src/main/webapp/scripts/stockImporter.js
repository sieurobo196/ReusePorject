jQuery(document).ready(function($) {		
	$('#goods').focus();
});
function searchGoodsSerial() {
	var stockId = $('#stockId').val();
	var goodsId = $('#goods').val();
	var fromSerial = $('#s_from').val();
	var toSerial = $('#s_to').val();
	var addSerial = getAddSerial();
	var url = getAppRootPath() + "/stock/getGoodsSerial?stockId=" + stockId + "&goodsId=" + goodsId + "&fromSerial=" + fromSerial + "&toSerial=" + toSerial + "&addSerial=" + addSerial;
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			fillGoodsSerialData(data.data);
		}
	});
}
function formatSerial(st, value) {
	while (st.length > value.length) {
		value = '0' + value;
	}
	return value;
}
function getLong(st) {
	if (st == null || st.trim() == '') {
		return 0;
	}
	var re = 0;
	try {
		re = parseFloat(st.trim());
	} catch (e) {
	}
	return re;
}
function addGoods() {
	if ($('#goods').val() == '0') {
		alert('Chưa chọn hàng hóa!');
		return;
	}
	var amount = $('#amount').val();
	if (amount.length == 0 || amount == '0') {
		alert('Chưa nhập số lượng!');
		return;
	}
	var fromSerial = $('#fromSerial').val();
	if (fromSerial.length == 0 || fromSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	var toSerial = $('#toSerial').val();
	if (toSerial.length == 0 || toSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="true" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/importer/";
	document.forms[0].submit();
}

function addInventoryGoods() {
	if ($('#employer').val() == '0') {
		alert('Chưa chọn nhân viên!');
		return;
	}
	if ($('#goodsType').val() == '0') {
		alert('Chưa chọn hàng !');
		return;
	}
	
	var fromSerial = $('#fromSerial').val();
	if (fromSerial.length == 0 || fromSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	var toSerial = $('#toSerial').val();
	if (toSerial.length == 0 || toSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="1" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForStaff";
	document.forms[0].submit();
}

function importInventoryGoods() {
	
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="2" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForStaff";
	document.forms[0].submit();
}

function submitInventoryGoods() {
	
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="3" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForStaff";
	document.forms[0].submit();
}

function addAgentInventoryGoods() {
	
	if ($('#goodsType').val() == '0') {
		alert('Chưa chọn hàng !');
		return;
	}
	
	var fromSerial = $('#fromSerial').val();
	if (fromSerial.length == 0 || fromSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	var toSerial = $('#toSerial').val();
	if (toSerial.length == 0 || toSerial == '0') {
		alert('Chưa nhập serial!');
		return;
	}
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="1" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForAgent";
	document.forms[0].submit();
}

function importAgentInventoryGoods() {
	
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="2" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForAgent";
	document.forms[0].submit();
}

function submitAgentInventoryGoods() {
	
	$('#loading').show();
	$(document.forms[0]).append('<input type="hidden" value="3" name="addGoods">');
	document.forms[0].action = getAppRootPath() + "/stock/ImportInventoryForAgent";
	document.forms[0].submit();
}



function deleteGoods(id) {
	$('#loading').show();
	document.forms[0].action = getAppRootPath() + "/stock/importer/deleteGoods/" + id;
	document.forms[0].submit();
}
function changeGoods(el) {
	el = $(el);
	if ('goods' == el.attr('id')) {
		$('#amount').val('0');
		$('#fromSerial').val('');
		$('#toSerial').val('');
		return;
	}
	$('#amount').val($('#amount').val().trim());
	$('#fromSerial').val($('#fromSerial').val().trim());
	$('#toSerial').val($('#toSerial').val().trim());
	if ('amount' == el.attr('id')) {
		var amount = getLong(el.val());
		var frS = getLong($('#fromSerial').val());
		var toS = getLong($('#toSerial').val());
		if (amount != 0 && frS != 0) {
			$('#toSerial').val(formatSerial($('#fromSerial').val(), new String(frS + amount - 1)))
		}
	} else if ('fromSerial' == el.attr('id')) {
		var amount = getLong($('#amount').val());
		var frS = getLong(el.val());
		var toS = getLong($('#toSerial').val());
		if (amount != 0 && frS != 0) {
			$('#toSerial').val(formatSerial(el.val(), new String(frS + amount - 1)))
		}
	} else if ('toSerial' == el.attr('id')) {
		var amount = getLong($('#amount').val());
		var frS = getLong($('#fromSerial').val());
		var toS = getLong(el.val());
		if (frS != 0 && toS != 0) {
			$('#amount').val(toS - frS + 1)
		}
	}
}
function changeChannel(channel, desStock) {
	var ch = channel.val();
	var agentId = $('#agentId').val();
	var url = getAppRootPath() + "/stock/getDesStockList?channel=" + ch + '&agentId=' + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(desStock, data.data);
		}
	});
	// reset infor
	$('#desCode').val("");
	$('#desName').val("");
	$('#desInstead').val("");
	$('#desPhoneNum').val("");
	$('#desAddress').val("");
}
//thay het cac option
function renderComboBox($domCbo, items) {
	renderComboBoxEmpty($domCbo);// remove old options
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '" + item.id + "'>" + item.name
				+ "</option>");
		$domCbo.append($domOption);
	}
}
//xoa cac option
function renderComboBoxEmpty($domCbo) {
	if ($domCbo.find('option').length > 0) {
		$domCbo.empty();
	}
}
function importGoods() {
	if ($('input[name="arrGoodsId"]').length == 0) {
		alert('Chưa nhập hàng hóa!');
		return;
	}
	$('#loading').show();
	document.forms[0].action = getAppRootPath() + "/stock/doImport/";
	document.forms[0].submit();
}