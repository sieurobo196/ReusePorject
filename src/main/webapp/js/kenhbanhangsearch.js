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
			re = els[i].id.substring(2) + '=' + vl;
		} else {
			re += '&' + els[i].id.substring(2) + '=' + vl;
		}
	}
	return re;
}
function createAgent() {
	if (checkEmpty('a_ten', 'Nhập tên đại lý!')) {
		return;
	}
	if (checkEmpty('a_nguoiLienHe', 'Nhập người liên hệ!')) {
		return;
	}
	if (checkSelected('a_quanHuyen', 'Chọn quận huyện!')) {
		return;
	}
	if (checkSelected('a_phuongXa', 'Chọn phường xã!')) {
		return;
	}
	if (checkEmpty('a_dienThoai', 'Nhập số điện thoại!')) {
		return;
	}
	if (checkEmpty('a_diaChi', 'Nhập số điện thoại!')) {
		return;
	}
	if (document.getElementById('a_email').value.trim().length != 0) {
		if (!validateEmail('a_email')) {
			return;
		}
	}
	$('#a_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/kbh/addAgent";
	var data = getParam("agentDialog", "a_");
	//data += "&chiNhanh=" + $('#chiNhanh').val();
	data += "&cnKenh=" + $('#chiNhanh').val();
	var parentId = parseFloat($('select[name="parentLevel"]').last().val());
	data += "&capDaiLy=" + $('#capDaiLy').val();
	data += "&parentID=" + parentId;
	$.ajax({
		  type: "POST",
		  url: url,
		  data: data,
		  success: function(data) {
				$('#loading').hide();
				if (data.error != null) {
					$('#a_messageError').html(data.error);
				}
				if (data.success != null) {
					$('#a_messageInfor').html(data.success);
					var fm = $("#dailyListForm");
					fm.action = getAppRootPath() + "/kbh/kbhcap";
					fm.submit();
				}
		  },
		  dataType: 'json'
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
	var url = getAppRootPath() + "/kbh/addPos";
	var data = getParam("posDialog", "p_");
	data += "&cnKenh=" + $('#chiNhanh').val();
	var parentId = parseFloat($('select[name="parentLevel"]').last().val());
	data += "&daiLy=" + parentId;
	$.ajax({
		type: "POST",
		url: url,
		data: data,
		success: function(data) {
			$('#loading').hide();
			if (data.error != null) {
				$('#p_messageError').html(data.error);
			}
			if (data.success != null) {
				$('#p_messageInfor').html(data.success);
				var fm = $("#dailyListForm");
				fm.action = getAppRootPath() + "/kbh/kbhcap";
				fm.submit();
			}
		},
		dataType: 'json'
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
	if (checkEmpty('u_matKhauThanhToan', 'Chưa nhập mật khẩu thanh toán!', 'u_messageError')) {
		return;
	}
	if (document.getElementById('u_retypeMatKhauThanhToan').value != document.getElementById('u_matKhauThanhToan').value) {
		$('#u_messageError').html("Mật khẩu thanh toán nhập lại chưa trùng khớp!");
		document.getElementById('u_retypeMatKhauThanhToan').focus();
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
	if (checkSelected('u_vaiTro', 'Chưa chọn vai trò!', 'u_messageError')) {
		return;
	}
	$('#u_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/kbh/addUser";
	var data = getParam("userDialog", "u_");
	data += "&cnKenh=" + $('#chiNhanh').val();
	// set parent
	var parentId = parseFloat($('select[name="parentLevel"]').last().val());
	if (parentId < 0 || saleLevel) { // select POS
		data += "&diemBanHang=" + (parentId * -1);
		data += "&phanLoai=3";
	} else { // select agent
		data += "&daiLy=" + parentId;
		data += "&phanLoai=2";
	}
	$.ajax({
		type: "POST",
		url: url,
		data: data,
		success: function(data) {
			$('#loading').hide();
			if (data.error != null) {
				$('#u_messageError').html(data.error);
			}
			if (data.success != null) {
				$('#u_messageInfor').html(data.success);
				var fm = $("#dailyListForm");
				fm.action = getAppRootPath() + "/kbh/kbhcap";
				fm.submit();
			}
		},
		dataType: 'json'
	});
}
function validateEmail(id, messageId) {
    var emailText = document.getElementById(id).value;
    var pattern = /^[a-zA-Z0-9\-_]+(\.[a-zA-Z0-9\-_]+)*@[a-z0-9]+(\-[a-z0-9]+)*(\.[a-z0-9]+(\-[a-z0-9]+)*)*\.[a-z]{2,4}$/;
    if (pattern.test(emailText)) {
        return true;
    } else {
    	if (messageId == null) {
    		$('#a_messageError').html("Email không hợp lệ!")
		} else {
			$('#'+messageId+'').html("Email không hợp lệ!")
		}
    	document.getElementById(id).focus();
        return false;
    }
}
function checkSelected(field, message, messageId) {
	var el = document.getElementById(field);
	if (el.selectedIndex == 0) {
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
//function openAddForm() {
//    // set full code parent
//	clearForm();
//    var type = 1;//$("#s_type").val();
//	if (type == 1) {
//		tb_show('Tạo đại lý mới','TB_inline?height=450&amp;width=700&amp;inlineId=agentDialog');
//	} else if (type == 2) {
//		tb_show('Tạo điểm bán hàng mới','TB_inline?height=450&amp;width=700&amp;inlineId=posDialog');
//	} else if (type == 3) {
//		tb_show('Tạo nhân viên mới','TB_inline?height=500&amp;width=700&amp;inlineId=userDialog');
//		// reset role selector
//		var sel = $('#u_vaiTro');
//		var ops = sel.children();
//		for (var i = ops.length - 1; i > 0; i--) {
//			$(ops[i]).remove();
//		}
//		var parentId = parseFloat($('select[name="parentLevel"]').last().val());
//		if (parentId < 0 || saleLevel) { // select POS
//			sel.append('<option value="ROLE_POS">ROLE_POS</option>');
//		} else { // select agent
//			sel.append('<option value="ROLE_RSM">ROLE_RSM</option>');
//		}
//        sel.append('<option value="ROLE_SALESMAN_ONLY">ROLE_SALESMAN_ONLY</option>');
//	}
//}
function openSearchForm() {
	if (!isSelectParent()) {
		return;
	}
	$('#searchTable').empty();
    var parentId = 1;//parseFloat($('select[name="parentLevel"]').last().val());
	var ops = $('#s_type option');
	if (parentId < 0 || saleLevel) { // select POS
		$(ops[0]).attr('style', 'display:none');
		$(ops[1]).attr('style', 'display:none');
		$('#s_type').val(3);
	} else { // select agent
		$(ops[0]).attr('style', '');
		$(ops[1]).attr('style', '');
		$('#s_type').val(1);
	}
	tb_show('Tìm kiếm thành viên','TB_inline?height=450&amp;width=700&amp;inlineId=searchDialog');
	clearForm('searchDialog');
}
function searchMember() {
	var channel = $('#s_type').val();
	var branchId = $('#chiNhanh').val();
	var dictrictId = $('#s_dist').val();
	var wardId = $('#s_ward').val();
	var code = $('#s_code').val();
	var url = getAppRootPath() + "/kbh/getMember?channel=" + channel + "&branchId=" + branchId + "&districtId=" + dictrictId + 
		"&wardId=" + wardId + "&code=" + code + "&level=" + $('#capDaiLy').val();
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
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
	fm.action = getAppRootPath() + "/kbh/kbhcap";
	fm.submit();
}
function selectAllMember(el) {
	var all = $('#searchTable input');
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		item.checked = el.checked;
	}
}