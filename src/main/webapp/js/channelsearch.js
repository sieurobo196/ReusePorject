function getParam(form, pre) {
	var els = $("[name='"+form+"']").find("[id^='"+pre+"']");
	var re = null;
	for (var i = 0; i < els.length; i++) {
		if (els[i].value == null) continue;
		var vl = els[i].value;
		if (els[i].type.toLowerCase() == "checkbox") {
			vl = els[i].checked;
		}
		if (re == null) {
			re = els[i].id.substring(pre.length) + '=' + vl;
		} else {
			re += '&' + els[i].id.substring(pre.length) + '=' + vl;
		}
	}
	return re;
}

function createArea() {
	if (checkEmpty('a_ten', 'Nhập tên vùng miền !')) {
		return;
	}
	if (checkEmpty('a_maVietTat', 'Nhập mã viết tắt!')) {
		return;
	}
	if (checkSelected('a_nguoiQuanLy', 'Chọn người quản lý!')) {
		return;
	}
	
	$('#a_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/channel/addArea";
	var data = getParam("areaDialog", "a_");
	//data += "&chiNhanh=" + $('#chiNhanh').val();
	data += "&companyID=" + $('#company').val();
	var parents = $('select[name="parentLevel"]');
	if (parents.length != 0) {
		var parentId = parseFloat(parents.last().val());
		data += "&parentID=" + parentId;
	}
	data += "&capDaiLy=" + $('#capDaiLy').val();
	$.ajax({
		  type: "POST",
		  url: url,
		  data: data,
		  success: function(data) {
				if (data.error != null) {
					$('#a_messageError').html(data.error);
					$('#loading').hide();
				}
				if (data.success != null) {
					$('#a_messageInfor').html(data.success);
					var fm = $("#dailyListForm");
					fm.action = getAppRootPath() + "/channel/list";
					fm.submit();
				}
		  },
		  dataType: 'json',
		  contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
}

function createAgent() {
	if (checkEmpty('a_ten', 'Nhập tên vùng miền!')) {
		return;
	}
	if ($('#a_provinces').length==0) {
		$('#a_provincesErr').text("Phải chọn ít nhất 1 tỉnh/thành.");
		return;
	} else {
		$('#a_provincesErr').text("");
	}
	if (checkEmpty('a_managerUser.ten', 'Chưa nhập tên người quản trị!')) {
		return;
	}
	if (checkEmpty('a_managerUser.matKhau', 'Chưa nhập mật khẩu!')) {
		return;
	}
	if (document.getElementById('a_managerUser.retypePassword').value != document.getElementById('a_managerUser.matKhau').value) {
		$('#a_messageError').html("Mật khẩu nhập lại chưa trùng khớp!");
		document.getElementById('a_managerUser.retypePassword').focus();
		return;
	}
	if (checkEmpty('a_managerUser.email', 'Chưa nhập email!')) {
		return;
	}
	if (!validateEmail('a_managerUser.email')) {
		return;
	}
	if (checkEmpty('a_managerUser.soDienThoai', 'Chưa nhập số điện thoại di động!')) {
		return;
	}
	if (checkSelected('a_managerUser.vaiTro', 'Chưa chọn vai trò!')) {
		return;
	}
	$('#a_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/channel/addAgent";
	var data = getParam("agentDialog", "a_");
	//data += "&chiNhanh=" + $('#chiNhanh').val();
	var company = $('#company').val();
	data += "&companyID=" + company;
	data += "&managerUser.companyID=" + company;
	data += "&managerUser.phanLoai=2";
	var parents = $('select[name="parentLevel"]');
	if (parents.length != 0) {
		var parentId = parseFloat(parents.last().val());
		data += "&parentID=" + parentId;
	}
	data += "&capDaiLy=" + $('#capDaiLy').val();
	$.ajax({
		  type: "POST",
		  url: url,
		  data: data,
		  success: function(data) {
				if (data.error != null) {
					$('#a_messageError').html(data.error);
					$('#loading').hide();
				}
				if (data.success != null) {
					$('#a_messageInfor').html(data.success);
					var fm = $("#dailyListForm");
					fm.action = getAppRootPath() + "/channel/list";
					fm.submit();
				}
		  },
		  dataType: 'json',
		  contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
}
function createPos() {
	if (checkEmpty('p_tenCuaHang', 'Nhập tên cửa hàng!', 'p_messageError')) {
		return;
	}
	if (checkEmpty('p_tenChuCuaHang', 'Nhập tên chủ cửa hàng!', 'p_messageError')) {
		return;
	}
	if (checkSelected('p_quanHuyen', 'Chọn quận huyện!', 'p_messageError')) {
		return;
	}
	if (checkSelected('p_phuongXa', 'Chọn phường xã!', 'p_messageError')) {
		return;
	}
	if (document.getElementById('p_email').value.trim().length != 0) {
		if (!validateEmail('p_email', 'p_messageError')) {
			return;
		}
	}
	$('#p_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/channel/addPos";
	var data = getParam("posDialog", "p_");
	data += "&companyID=" + $('#company').val();
	var parentId = parseFloat($('select[name="parentLevel"]').last().val());
	data += "&daiLy=" + parentId;
	$.ajax({
		type: "POST",
		url: url,
		data: data,
		success: function(data) {
			if (data.error != null) {
				$('#p_messageError').html(data.error);
				$('#loading').hide();
			}
			if (data.success != null) {
				$('#p_messageInfor').html(data.success);
				var fm = $("#dailyListForm");
				fm.action = getAppRootPath() + "/channel/list";
				fm.submit();
			}
		},
		dataType: 'json',
		contentType: "application/x-www-form-urlencoded; charset=UTF-8"
	});
}
function createUser() {
	if (checkEmpty('u_ten', 'Chưa nhập tên người dùng!', 'u_messageError')) {
		return;
	}
	if (checkEmpty('u_matKhau', 'Chưa nhập mật khẩu!', 'u_messageError')) {
		return;
	}
	if (document.getElementById('u_retypePassword').value != document.getElementById('u_matKhau').value) {
		$('#u_messageError').html("Mật khẩu nhập lại chưa trùng khớp!");
		document.getElementById('u_retypePassword').focus();
		return;
	}
	if (checkEmpty('u_email', 'Chưa nhập email!', 'u_messageError')) {
		return;
	}
	if (!validateEmail('u_email', 'u_messageError')) {
		return;
	}
	if (checkEmpty('u_soDienThoai', 'Chưa nhập số điện thoại di động!', 'u_messageError')) {
		return;
	}
	if ($('#capDaiLy').val()=='4') {
		if (checkSelected('u_tinhThanhPho', 'Chưa chọn tỉnh thành!', 'u_messageError')) {
			return;
		}
	} else {
		if ($('#u_provinces').length==0) {
			$('#u_provincesErr').text("Phải chọn ít nhất 1 tỉnh/thành.");
			return;
		} else {
			$('#u_provincesErr').text("");
		}
	}
	
	if (checkSelected('u_vaiTro', 'Chưa chọn vai trò!', 'u_messageError')) {
		return;
	}
	if (showSupplier && checkSelected('u_nppId', 'Chưa chọn nhà phân phối!', 'u_messageError')) {
		return;
	}
	$('#u_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/channel/addUser";
	var data = getParam("userDialog", "u_");
	data += "&companyID=" + $('#company').val();
	// set parent
	if ($('#manager').length > 0) {//for add manager in agent or POS form
		if ($('#parentType').val() == '1') {
			data += "&daiLy=" + $('#parentId').val();
			data += "&phanLoai=2";
		} else {
			data += "&diemBanHang=" + $('#parentId').val();
			data += "&phanLoai=3";
		}
	} else {
		var parentId = $('select[name="parentLevel"]').last().val();
		if (parentId.charAt(0) == '2') { // select POS
			data += "&diemBanHang=" + parentId;
			data += "&phanLoai=3";
		} else if (parentId.charAt(0) == '1') { // select agent
			data += "&daiLy=" + parentId;
			data += "&phanLoai=2";
		} else { // select sales man
			data += "&nvQuanLy=" + parentId;
			data += "&phanLoai=5";
		}
	}
	$.ajax({
		type: "POST",
		url: url,
		data: data,
		success: function(data) {
			if (data.error != null) {
				$('#u_messageError').html(data.error);
				$('#loading').hide();
			}
			if (data.success != null) {
				$('#u_messageInfor').html(data.success);
				if ($('#manager').length > 0) {//for add manager in agent or POS form
					document.location.reload();
				} else {
					var fm = $("#dailyListForm");
					fm.action = getAppRootPath() + "/channel/list";
					fm.submit();
				}
			}
		},
		dataType: 'json',
		contentType: "application/x-www-form-urlencoded; charset=UTF-8"
	});
}
function changeRole(el) {
	if ($(el).val()=='ROLE_SALESMAN') {
		var lc = $('#u_nhanVienGiamSat');
		lc.parent().parent().show()
		// check and load leader
		initLeaderList(lc);
	} else {
		$('#u_nhanVienGiamSat').parent().parent().hide()
	}
}
function initLeaderList(lc) {
	if (lc.children().length==0) {
		$.ajax({
			type: "POST",
			url: getAppRootPath() + "/channel/leaderList",
			data: 'stockId=' + $('select[name="parentLevel"]').last().val(),
			success: function(data) {
				renderComboBox(lc, data, '--- Tổ Trưởng ---');
			},
			dataType: 'json',
		});
	}
}

function checkSelected(field, message, messageId) {
	var el = document.getElementById(field);
	if (el.value == 0) {
		if (messageId == null) {
			$('#a_messageError').html(message)
		} else {
			$('#'+messageId+'').html(message)
		}
		el.focus();
		return true;
	}
	return false;
}
function checkEmpty(field, message, messageId) {
	var el = document.getElementById(field);
	if (el.tagName.toLowerCase() == 'select') {
		return checkSelected(field, message, messageId);
	}
	if (el.value.trim().length == 0) {
		if (messageId == null) {
			$('#a_messageError').html(message)
		} else {
			$('#'+messageId+'').html(message)
		}
		el.focus();
		return true;
	}
	return false;
}
function clearForm(mainForm) {
	var childs = $("#TB_ajaxContent").children();
	for (var i = 0; i < childs.length; i++) {
		if (childs[i].attributes['name'] != null && (mainForm == null || childs[i].attributes['name'].value != mainForm)) {
			$("#" + childs[i].attributes['name'].value).append(childs[i]);
		}
	}
}
var showSupplier;

function openAddForm(channelId) {
    $('#channel_full_code_parent').val(channelSelectedCode);
	clearForm();
    var type = 1;//$("#s_type").val();
    var level = 1;//parseFloat($('#capDaiLy').val());
	if (level == 1) {
		tb_show('Tạo Miền mới','TB_inline?height=450&amp;width=700&amp;inlineId=agentDialog');
	} else {
		var title = 'Tạo Nhân Viên BH';
		if (level == 2) {
			title = 'Tạo Trưởng Nhóm';
		} else if (level == 3) {
			title = 'Tạo Giám Sát';
		}
		tb_show(title,'TB_inline?height=550&amp;width=700&amp;inlineId=userDialog');
		// reset role selector
		var sel = $('#u_vaiTro');
		var ops = sel.children();
		for (var i = ops.length - 1; i >= 0; i--) {
			$(ops[i]).remove();
		}
		var parentId = $('select[name="parentLevel"]').last().val();
		showSupplier = false;
		if (parentId.charAt(0) == '2') { // select POS
			sel.append('<option value="ROLE_POS">ROLE_POS</option>');
		} else if (parentId.charAt(0) == '1') { // chon Vung Mien
			sel.append('<option value="ROLE_ASM">Quản lý khu vực</option>');
		} else if (parentId.charAt(0) == '3') { // sales man
			if (level == 3) {// chon Nhom Truong
				sel.append('<option value="ROLE_SUPERVISOR">Giám Sát</option>');
			} else if (level == 4) {// chon Giam Sat
				sel.append('<option value="ROLE_SALESMAN">Nhân Viên BH</option>');
				sel.append('<option value="ROLE_LEADER">Tổ Trưởng</option>');
				showSupplier = true;
				changeRole(sel);
			}
		}
		if (showSupplier) {
			$('#u_nppId').parent().parent().show()
			$('#u_nhanVienBanhTuoi').parent().parent().show()
		} else {
			$('#u_nppId').parent().parent().hide()
			$('#u_nhanVienBanhTuoi').parent().parent().hide()
		}
		sel.select2();
	}
}
function openSearchForm(channelId) {
    openAddForm(channelId);
}
function searchMember() {
	var channel = $('#s_type').val();
	var company = $('#company').val();
	var dictrictId = $('#s_dist').val();
	var wardId = $('#s_ward').val();
	var code = $('#s_code').val();
	var url = getAppRootPath() + "/channel/getMember";
	var parentId = $('select[name="parentLevel"]').last().val();
	if (parentId == null) {
		parentId = 0;
	}
	var data = "channel=" + channel + "&company=" + company + "&districtId=" + dictrictId + 
		"&wardId=" + wardId + "&code=" + code + "&level=" + $('#capDaiLy').val() + "&parentStockId=" + parentId;
	$('#loading').show();
	$.ajax({
		type : 'POST',
		dataType : 'json',
		data : data,
		url : url,
		success : function(data) {
			$('#loading').hide();
			fillMemberData(data);
		}
	});
}
// cache objects return by search
var searchObject = new Array();
var searchObjectMap = new Object();
function fillMemberData(data) {
	var sb = $('#searchTable');
	sb.empty();
	$('#checkbox_all')[0].checked = false;
	searchObject = new Array();// 
	searchObjectMap = new Object();
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center"><input type="checkbox" id="'+id+'" name="s_member" value="'+item.id+'"/></td>';
		row += '<td width="10%" align="center">'+createLable(id, item.code)+'</td>';
		row += '<td width="30%">'+createLable(id, item.name)+'</td>';
		row += '<td width="45%">'+createLable(id, item.address)+'</td>';
		row += '<td width="10%" align="center">'+createLable(id, item.phoneNum.replace(/;/g, '<br>'))+'</td>';
		row += '</tr>';
		searchObject[i] = item;
		searchObjectMap[id] = item;
		sb.append($(row));
	}
}
function createLable(id, value) {
	return '<label for="' + id + '">' + value + '</label>';
}
function addMember() {
	var all = $('#searchTable input:checked');
	if (all.length == 0) {
		alert('Chưa chọn thành viên!');
		return;
	}
	var fm = $("#dailyListForm");
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		fm.append('<input type="hidden" name="addMember" value="'+item.value+'"/>');
	}
	$('#loading').show();
	fm.append('<input type="hidden" name="memberType" value="'+$('#s_type').val()+'"/>');
	fm.action = getAppRootPath() + "/channel/list";
	fm.submit();
}
function selectAllMember(el) {
	var all = $('#searchTable input');
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		item.checked = el.checked;
	}
}