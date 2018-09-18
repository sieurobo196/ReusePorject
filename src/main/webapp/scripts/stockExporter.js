function openSerialChoose() {
	if ($('#goods').val() == 0) {
		alert("Chọn hàng hóa xuất!")
		return;
	}
	$('#s_goods').val($('#goods').val());
	$("#searchTable").html('');
	$("#selectedTable").html('');
	$("#total").text('0');
	tb_show('Chọn chi tiết hàng hóa','TB_inline?height=450&amp;width=900&amp;inlineId=serialDialog');
}
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
// cache objects return by search
var searchObject = new Array();
var searchObjectMap = new Object();
function fillGoodsSerialData(data) {
	$('#loading').hide();
	var sb = $('#searchTable');
	var re = "";
	searchObject = new Array();// 
	searchObjectMap = new Object();
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = item[0]+'_'+item[1]+'_'+item[2];
		var row = '<div id="'+id+'">';
		row += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
		row += '<tr>';
		row += '<td width="30%" align="center">'+createAdd(id)+'&nbsp;&nbsp;&nbsp;'+createFrom(id, item[0])+'</td>';
		row += '<td width="30%" align="center">'+createTo(id, item[1])+'</td>';
		row += '<td width="20%" align="right">'+createAmount(id, item[2])+'</td>';
		row += '<td width="20%" align="right">';
		row += createExport(id);
		row += '</td>';
		row += '</tr>';
		row += '</table>';
		row += '</div>';
		re += row;
		var ob = new Object();
		ob.id = id;
		ob.fr = item[0];
		ob.to = item[1];
		ob.vl = item[2];
		searchObject[i] = ob;
		searchObjectMap[id] = ob;
	}
	sb.html(re);
}
function createExport(id) {
	return '<input onChange="chooseSerial(\''+id+'\');" type="text" style="width:80%" id="ex'+id+'"/>';
}
function getExport(id) {
	return $('#ex'+id);
}
function createTo(id, value) {
	// add prefix for to serial
	value = formatSerial(id, value);
	return '<label id="to'+id+'">'+value+'</label>';
}
function formatSerial(id, value) {
	var realId = id;
	if (realId.indexOf("x") == 0) {
		realId = realId.substring(1, realId.length);
	}
	var ob = searchObjectMap[realId];
	if (ob != null) {
		value = new String(value).trim();
		while (ob.fr.length > value.length) {
			value = '0' + value;
		}
	}
	return value;
}
function getTo(id) {
	return $('#to'+id);
}
function getToX(id) {
	return $('#tox'+id);
}
function createFrom(id, value) {
	return '<label id="fr'+id+'">'+value+'</label>';
}
function getFrom(id) {
	return $('#fr'+id);
}
function getFromX(id) {
	return $('#frx'+id);
}
function createAmount(id, value) {
	return '<label id="sl'+id+'">'+value+'</label>';
}
function getAmount(id) {
	return $('#sl'+id);
}
function getAmountX(id) {
	return $('#slx'+id);
}
function getItem(id) {
	return $('#item-'+id);
}
function getLong(st) {
	var re = 0;
	try {
		re = parseFloat(st);
	} catch (e) {
	}
	return re;
}
function chooseSerial(id){
	var item = getItem(id);
	var ob = searchObjectMap[id];
	var objVl = getLong(ob.vl);
	if (item.length != 0) {// exist this serial
		var vol = getLong(getExport(id).val());
		if (vol > objVl) {//check and set max volume
			vol = objVl;
			getExport(id).val(vol);
		}
		var amountX = getAmountX(id);
		var amount = getAmount(id);
		var volX = getLong(amountX.text());
		// subtract volume
		amount.text(objVl - vol);
		// reset export amount
		amountX.text(vol);
		// reset to export
		var toX = getToX(id);
		toX.text(formatSerial(id, getLong(ob.fr) + vol - 1));
		// calculate total
		var total = $("#total");
		total.text(getLong(total.text()) - volX + vol);
		return;
	}
	var vol = getLong(getExport(id).val());
	if (vol > objVl) {//check and set max volume
		vol = objVl;
		getExport(id).val(vol);
	}
	var to = getLong(getFrom(id).text()) + vol - 1;
	var row = '<div id="item-'+id+'">';
	row += '<table width="100%" border="0" cellspacing="0" cellpadding="0">';
	row += '<tr>';
	row += '<td width="35%" align="center">'+createDelete(id) +'&nbsp;&nbsp;&nbsp;'+createFrom('x'+id, getFrom(id).text())+'</td>';
	row += '<td width="35%" align="center">'+createTo('x'+id, to)+'</td>';
	row += '<td width="30%" align="right">'+createAmount('x'+id, vol)+'</td>';
	row += '</tr>';
	row += '</table>';
	row += '</div>';
	$("#selectedTable").append(row);
	// subtract volume
	var amount = getAmount(id);
	amount.text(getLong(amount.text()) - vol);
	// calculate total
	var total = $("#total");
	total.text(getLong(total.text()) + vol);
}
function chooseAllSerial() {
	var allDiv = $('#searchTable div');
	for ( var i = 0; i < allDiv.length; i++) {
		var div = allDiv[i];
		var id = div.id;
		var ob = searchObjectMap[id];
		getExport(id).val(ob.vl);
		chooseSerial(id);
	}
}
function addSerial(id) {
	var ob = searchObjectMap[id];
	getExport(id).val(ob.vl);
	chooseSerial(id);
}
function createAdd(id) {
	return '<a style="color:green" title="Thêm" href="javascript:addSerial(\'' + id + '\')">+</a>';
}
function createDelete(id) {
	return '<a style="color:red" title="Xóa" href="javascript:deleteSerial(\'' + id + '\')">x</a>';
}
function deleteSerial(id) {
	var ex = getExport(id);
	if (ex.length != 0) {//check to return volume if exist
		var ob = searchObjectMap[id];
		ex.val(0);
		getAmount(id).text(ob.vl);
	}
	// calculate total
	var vol = getLong(getAmountX(id).text());
	var total = $("#total");
	total.text(getLong(total.text()) - vol);
	// remove this item
	getItem(id).remove();
}
function deleteAllSerial() {
	var allDiv = $('#selectedTable div');
	for ( var i = 0; i < allDiv.length; i++) {
		var div = allDiv[i];
		var id = div.id.split('-')[1];
		deleteSerial(id);
	}
}
function getAddSerial(isVolume) {
	var allDiv = $('#selectedTable div');
	var re = '';
	var goods = $('#goods').val();
	for ( var i = 0; i < allDiv.length; i++) {
		var div = allDiv[i];
		var id = div.id.split('-')[1];
		if (re.length == 0) {
			re = goods + '_' + getFromX(id).text() + '_' + getToX(id).text();
			if (isVolume) {
				re += '_' + getAmountX(id).text();
			}
		} else {
			re += '|' + goods + '_' + getFromX(id).text() + '_' + getToX(id).text();
			if (isVolume) {
				re += '_' + getAmountX(id).text();
			}
		}
	}
	return re;
}
function completedChoose() {
	$('#serial').val(getAddSerial(true));
	$('#amount').val($('#total').text());
	tb_remove();
	// add to goods list
	addGoods();
}
function addGoods() {
	if ($('#goods').val() == '0') {
		alert('Chọn hàng hóa!');
		return;
	}
	if (!checkInventory()) {
		return;
	}
	var amount = $('#amount').val();
	if (amount.length == 0 || amount == '0') {
		alert('Nhập số lượng hàng!');
		return;
	}
	$('#loading').show();
	document.forms[0].action = getAppRootPath() + "/stock/exporter/addGoods";
	document.forms[0].submit();
}
function deleteGoods(id) {
	$('#loading').show();
	document.forms[0].action = getAppRootPath() + "/stock/exporter/deleteGoods/" + id;
	document.forms[0].submit();
}
function changeDesStock() {
	var ch = $('#channel').val();
	var id = $('#desStockId').val();
	var url = getAppRootPath() + "/stock/getDesInfor?channel=" + ch + '&id=' + id;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			var si = data.data;
			$('#desCode').val(si.desCode);
			$('#desName').val(si.desName);
			$('#desInstead').val(si.desInstead);
			$('#desPhoneNum').val(si.desPhoneNum);
			$('#desAddress').val(si.desAddress);
		}
	});
}
function changeChannel(channel, desStock) {
	var ch = channel.val();
	var stockId = $('#stockId').val();
	var url = getAppRootPath() + "/stock/getDesStockList?channel=" + ch + '&parentStock=' + stockId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(desStock, data);
			$('#district').val(0);
		}
	});
	// reset infor
	resetInfor();
}
function changeDistrict() {
	var channel = $('#channel');
	var desStock = $('#desStockId');
	var ch = channel.val();
	var stockId = $('#stockId').val();
	var district = $('#district').val();
	var url = getAppRootPath() + "/stock/getDesStockList?channel=" + ch + '&parentStock=' + stockId + '&district=' + district;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(desStock, data);
		}
	});
	// reset infor
	resetInfor();
}
function resetInfor(){
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
	$domCbo.select2();
}
//xoa cac option
function renderComboBoxEmpty($domCbo) {
	if ($domCbo.find('option').length > 0) {
		$domCbo.empty();
	}
}
function exportGoods() {
	if ($('#desStockId').val() == 0) {
		alert('Chưa chọn kho xuất!');
		return;
	}
	if ($('#channel').val() == 2) {
		if ($('#deployedUser').val() == '0') {
			alert('Chọn người triển khai!');
			return;
		}
		if ($('#eventId').val() == '0') {
			alert('Chọn sự kiện!');
			return;
		}
	}
	if ($('input[name="arrGoodsId"]').length == 0) {
		alert('Chưa chọn hàng hóa!');
		return;
	}
	$('#loading').show();
	document.forms[0].action = getAppRootPath() + "/stock/doExporter/";
	document.forms[0].submit();
}
function changeGoods() {
	$('#amount').val(0);
	$('#serial').val('');
}