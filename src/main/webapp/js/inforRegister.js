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
	var url = getAppRootPath() + "/channel/addAgent";
	var data = getParam("agentDialog", "a_");
	//data += "&chiNhanh=" + $('#chiNhanh').val();
	data += "&chiNhanh=" + $('#chiNhanh').val();
	var parentId = parseFloat($('select[name="parentLevel"]').last().val());
	data += "&parentID=" + parentId;
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

function login() {
	$('#l_messageError').html("");
	$('#loading').show();
	var url = getAppRootPath() + "/customer/login";
	var data = "userName=" + $('#l_userName').val();
	data += "&pass=" + $('#l_password').val();
	$.ajax({
		  type: "POST",
		  url: url,
		  data: data,
		  success: function(data) {
				if (!data) {
					$('#l_messageError').html("Đăng nhập không thành công. Sai số thuê bao hoặc mật khẩu.");
					$('#loading').hide();
				} else {
					$('#l_messageInfor').html("");
					$('#FrmRegList').append('<input name="userName" type="hidden" value="' + $('#l_userName').val() + '">');
					$('#FrmRegList').append('<input name="password" type="hidden" value="' + $('#l_password').val() + '">');
					submitRegister();
				}
		  },
		  dataType: 'json',
		  contentType: "application/x-www-form-urlencoded; charset=UTF-8"
		});
}

function openLoginForm() {
	tb_show('Đăng nhập Mobifone Portal','TB_inline?height=150&amp;width=350&amp;inlineId=loginDialog');
}

function doRegister(id) {
	if (id == null) {
		if (jQuery("input[id^='id_']:checked").length == 0) {
			alert('Chọn các mẫu đang ký cần duyệt.');
			return;
		}
		if (confirm('Bạn chấp nhận các mẫu đăng ký này?')) {
			openLoginForm();
			//submitRegister();
		}
		return;
	}
	jQuery('#id_' + id).prop( "checked", true);
	if (confirm('Bạn chấp nhận mẫu đăng ký này?')) {
		openLoginForm();
		//submitRegister();
	}
}
function selectAll(item) {
	jQuery("input[id^='id_'][disable!='disable']").prop( "checked", jQuery(item).prop('checked'));
}
function submitRegister() {
	$('#loading').show(); 
	document.FrmRegList.action = contextPath + '/customer/inforRegister';
	document.FrmRegList.submit();
}