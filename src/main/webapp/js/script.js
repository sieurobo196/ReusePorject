
    //chinh phan quyen
    $(document).ready(function () {
        if ('${readonly}' != null && '${readonly}' != '' && '${readonly}' != NaN && '${readonly}'=='true')
        {
            $('select').each(function () {
                $(this).attr("disabled", '${readonly}');
            });
            $("input[type='text']").each(function () {
                $(this).attr("readonly", '${readonly}');
            });
            $("input[type='checkbox']").each(function () {
                $(this).attr("disabled", '${readonly}');
            });
            $("input[class^='textInput tcal']").each(function () {
                $(this).attr("disabled", '${readonly}');
            });

            $("a[name='checkView']").each(function () {
                $(this).remove();
            });
        }
    });

function getAppRootPath() {
	return location.protocol + "//" + location.host //;
			+ contextPath;
}
function submitActionForm(formId, url) {
	waitingDownLoad();
	$("#" + formId).attr("action", url);
	$("#" + formId).submit();
}

function redirect(url) {
	waitingDownLoad();
	document.location.href = url;
}

function delRedirect(url) {
	waitingDownLoad();
	if (confirm('Bạn có chắc rằng bạn muốn xóa?')) {
		document.location.href = url;
	} else {
		$(".loading-indicator-overlay").remove();
	}

}

function submitExportForm(formId, url) {
	waitingDownLoad();
	var $form = $("#" + formId);
	var prUrl = $form.attr('action');
	$form.attr("action", url);
	$form.submit();
	$form.attr("action", prUrl);
	flag = finishDownload(true);

}

function showLoading($el) {
	if ($el != null) {
		$el.append(
		'<div id="loading_id" class="loading-indicator-overlay" > <div class="loading-indicator"> </div></div>')
	} else {
		$('#content').append(
		'<div id="loading_id" class="loading-indicator-overlay" > <div class="loading-indicator"> </div></div>')			
	}
}	

function hideLoading() {
	$("#loading_id").remove();			
}

function waitingDownLoad() {
	$("#content")
			.append(
					'<div class="loading-indicator-overlay" > <div class="loading-indicator"> </div></div>  ');
}

function finishDownload(flag) {
	var cookieValue = getCookie('downloaded');
	if (cookieValue != null && cookieValue == 'true') {
		$(".loading-indicator-overlay").remove();
		document.cookie = 'downloaded=; Path=/; expires=Thu, 01 Jan 1970 00:00:01 GMT;';
		flag = false;
	}
	if (flag)
		setTimeout(function() {
			finishDownload(flag);
		}, 1000);
}

function getCookie(c_name) {
	var c_value = document.cookie;
	var c_start = c_value.indexOf(" " + c_name + "=");
	if (c_start == -1) {
		c_start = c_value.indexOf(c_name + "=");
	}
	if (c_start == -1) {
		c_value = null;
	} else {
		c_start = c_value.indexOf("=", c_start) + 1;
		var c_end = c_value.indexOf(";", c_start);
		if (c_end == -1) {
			c_end = c_value.length;
		}
		c_value = unescape(c_value.substring(c_start, c_end));
	}
	return c_value;
}

function setCookie(c_name, value, exdays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + exdays);
	var c_value = escape(value)
			+ ((exdays == null) ? "" : "; expires=" + exdate.toUTCString());
	document.cookie = c_name + "=" + c_value;
}

function activeMenu() {
	var url = $(location).attr('pathname');
	url = url.substring(0, url.lastIndexOf('/'));
	url = url.substring(url.indexOf("/") + 1, url.length);
	url = url.substring(url.indexOf("/") + 1, url.length);
	$('.title-child').each(function() {
		var param = $(this).attr("param");
		if (param.indexOf(url) > -1) {
			$(this).attr('style', 'font-weight:bold');
			$(this).parent().parent().attr('style', 'display:block');
			$(this).parent().parent().parent().addClass('active');
		}
	});
}
if ($('.uploadfile').length) {
	$('.uploadfile').change(function() {
		$('.filePath').val($(this).val());
	});
}

function giaNhapKit(itemId) {

	var giaNhap = $('#gia_nhap_' + itemId).val();
	var url = contextPath + "/ajax/giaBoKit/giaNhap?ms=" + itemId + "&giaNhap="
			+ giaNhap;

	$.ajax({
		url : url,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {

			if (data.items == false) {
				$("#check_valid_" + itemId).attr("style", "color:red");
			} else {
				$("#check_valid_" + itemId).attr("style", "");
			}

		}

	});
}

function giaBanKit(itemId) {
	var giaBan = $('#gia_ban_' + itemId).val();
	var url = contextPath + "/ajax/giaBoKit/giaBan?ms=" + itemId + "&giaBan="
			+ giaBan;

	$.ajax({
		url : url,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {

			if (data.items == false) {
				$("#check_valid_" + itemId).attr("style", "color:red");
			} else {

				$("#check_valid_" + itemId).attr("style", "");
			}
		}

	});
}

function editPrice(itemId) {
	$("#gia_nhap_" + itemId).removeAttr("disabled");
	$("#gia_ban_" + itemId).removeAttr("disabled");
}

function giaNhap3G(itemId) {

	var giaNhap = $('#gia_nhap_' + itemId).val();
	var url = contextPath + "/ajax/giaBo3G/giaNhap?ms=" + itemId + "&giaNhap="
			+ giaNhap;

	$.ajax({
		url : url,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {

			if (data.items == false) {
				$("#check_valid_" + itemId).attr("style", "color:red");
			} else {
				$("#check_valid_" + itemId).attr("style", "");
			}

		}

	});
}

function giaBan3G(itemId) {

	var giaBan = $('#gia_ban_' + itemId).val();
	var url = contextPath + "/ajax/giaBo3G/giaBan?ms=" + itemId + "&giaBan="
			+ giaBan;

	$.ajax({
		url : url,
		dataType : "json",
		contentType : "application/json; charset=utf-8",
		success : function(data) {

			if (data.items == false) {
				$("#check_valid_" + itemId).attr("style", "color:red");
			} else {
				$("#check_valid_" + itemId).attr("style", "");
			}

		}

	});
}

function eventAddOnchangeChietKhau(element) {
	var msTheCao = $(element).attr('msTheCao');
	var msNhaMang = $(element).attr('msNhaMang');
	var chietKhauNhap = $('#chiet_khau_nhap_' + msTheCao).val();
	var chietKhauXuat = $('#chiet_khau_xuat_' + msTheCao).val();
	if ((chietKhauNhap != "" && !($.isNumeric(chietKhauNhap)))) {
		$("#td_nhap_" + msTheCao).attr("style", "background:red");
	} else if ((chietKhauXuat != "" && !($.isNumeric(chietKhauXuat)))) {
		$("#td_xuat_" + msTheCao).attr("style", "background:red");
	} else {
		$("#td_nhap_" + msTheCao).attr("style", "");
		$("#td_xuat_" + msTheCao).attr("style", "");
		var msChiNhanh = $('#msChiNhanh').val();
		var msQuanHuyen = $('#msQuanHuyen').val();
		var tuNgay = $('#frm_startDate').val();
		var denNgay = $('#frm_endDate').val();
		var skipData = $('#skipDataTrue').is(":checked");
		var url = contextPath + "/ajax/theCao/chietKhauTheCao?msChiNhanh="
				+ msChiNhanh + "&msQuanHuyen=" + msQuanHuyen + "&msTheCao="
				+ msTheCao + "&msNhaMang=" + msNhaMang + "&chietKhauNhap="
				+ chietKhauNhap + "&chietKhauXuat=" + chietKhauXuat
				+ "&tuNgay=" + tuNgay + "&denNgay=" + denNgay + "&skipData="
				+ skipData;
		if (!(chietKhauXuat == "" && chietKhauNhap == "")) {
			$
					.ajax({
						url : url,
						dataType : "json",
						contentType : "application/json; charset=utf-8",
						success : function(data) {
							if (!data.items.result) {
								var str = "Quá trình cập nhật thông tin đã xảy ra lổi: \n Thông tin chỉ được cập nhật từ : "
										+ data.items.fromDate
										+ " đến ngày:"
										+ data.items.toDate;
								alert(str);
							}

						},
						error : function(request, status, error) {
							alert(request.responseText);
						}

					});
		}
	}
}

function eventListUpdateOnchangeChietKhau(element) {
	var msTheCao = $(element).attr('msTheCao');
	var msNhaMang = $(element).attr('msNhaMang');
	var ngay = $(element).attr('ngay');
	var chietKhauNhap = $(
			'#ck_nhap_' + msTheCao + '_' + ngay.replace(/\//g, '')).val();
	var chietKhauXuat = $(
			'#ck_xuat_' + msTheCao + '_' + ngay.replace(/\//g, '')).val();
	if ((chietKhauNhap != "" && !($.isNumeric(chietKhauNhap)))) {
		$('#td_nhap_' + msTheCao + '_' + ngay.replace(/\//g, '')).attr("style",
				"background:red");
	} else if ((chietKhauXuat != "" && !($.isNumeric(chietKhauXuat)))) {
		$('#td_xuat_' + msTheCao + '_' + ngay.replace(/\//g, '')).attr("style",
				"background:red");
	} else {
		$("#td_nhap_" + msTheCao + '_' + ngay.replace(/\//g, '')).attr("style",
				"");
		$("#td_xuat_" + msTheCao + '_' + ngay.replace(/\//g, '')).attr("style",
				"");
		var msChiNhanh = $(element).attr("msChiNhanh");
		var msQuanHuyen = $(element).attr("msQuanHuyen");
		var url = contextPath + "/ajax/theCao/chietKhauTheCao?msChiNhanh="
				+ msChiNhanh + "&msQuanHuyen=" + msQuanHuyen + "&msTheCao="
				+ msTheCao + "&msNhaMang=" + msNhaMang + "&chietKhauNhap="
				+ chietKhauNhap + "&chietKhauXuat=" + chietKhauXuat
				+ "&tuNgay=" + ngay + "&denNgay=" + ngay + "&skipData=false";
		$.ajax({
			url : url,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(data) {
				if (!data.items.result) {
					var str = "Cập nhật thông tin không thành công";
					alert(str);
				}
			},
			error : function(request, status, error) {
				alert(request.responseText);
			}

		});
	}
}

function eventListAddOnchangeChietKhau(element) {
	var menhGia = $(element).attr('menhGia');
	var msNhaMang = $(element).attr('msNhaMang');
	var ngay = $(element).attr('ngay');
	var chietKhauNhap = $(
			'#ck_nhap_' + menhGia + '_' + msNhaMang + '_'
					+ ngay.replace(/\//g, '')).val();
	var chietKhauXuat = $(
			'#ck_xuat_' + menhGia + '_' + msNhaMang + '_'
					+ ngay.replace(/\//g, '')).val();
	if ((chietKhauNhap != "" && !($.isNumeric(chietKhauNhap)))) {
		$(
				'#td_nhap_' + menhGia + '_' + msNhaMang + '_'
						+ ngay.replace(/\//g, '')).attr("style",
				"background:red");
	} else if ((chietKhauXuat != "" && !($.isNumeric(chietKhauXuat)))) {
		$(
				'#td_xuat_' + menhGia + '_' + msNhaMang + '_'
						+ ngay.replace(/\//g, '')).attr("style",
				"background:red");
	} else {
		$(
				"#td_nhap_" + menhGia + '_' + msNhaMang + '_'
						+ ngay.replace(/\//g, '')).attr("style", "");
		$(
				"#td_xuat_" + menhGia + '_' + msNhaMang + '_'
						+ ngay.replace(/\//g, '')).attr("style", "");
		var msChiNhanh = $(element).attr("msChiNhanh");
		var msQuanHuyen = $(element).attr("msQuanHuyen");
		var url = contextPath + "/ajax/theCao/chietKhauTheCaoAddFromList?msChiNhanh="
				+ msChiNhanh + "&msQuanHuyen=" + msQuanHuyen + "&menhGia="
				+ menhGia + "&msNhaMang=" + msNhaMang + "&chietKhauNhap="
				+ chietKhauNhap + "&chietKhauXuat=" + chietKhauXuat
				+ "&tuNgay=" + ngay + "&denNgay=" + ngay;
		$.ajax({
			url : url,
			dataType : "json",
			contentType : "application/json; charset=utf-8",
			success : function(data) {
				if (!data.items.result) {
					var str = "Cập nhật thông tin không thành công";
					alert(str);
				}
			},
			error : function(request, status, error) {
				alert(request.responseText);
			}

		});
	}
}