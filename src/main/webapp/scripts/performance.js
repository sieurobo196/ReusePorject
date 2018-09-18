// doi option duoc select
// function reSelect1($domCbo, value, text) {
// $domCbo.find('option:selected').removeAttr('selected');
// var $domOption = $domCbo.find("option[value='" + value + "']");
// if ($domOption.length == 0) {
// $domOption = $("<option value = '" + value + "'>" + text + "</option>");
// $domCbo.append($domOption);
// }
// $domOption.attr('selected', 'selected');
// }
// hien thi loc mo rong
function viewExtendedFilter() {
	if ($('#grExtendedFilter').is(':hidden')) {
		$('.extendedFilter').show();
	} else {
		$('.extendedFilter').hide();
	}
}
// xem ket qua
function viewResults() {
	var valid = true;
	var tyleDatToiThieu = $('#minTyleDat').val();
	if (isSoNguyenDuong(tyleDatToiThieu) && parseInt(tyleDatToiThieu) <= 100) {
		$('#errTyleDat').text("");
	} else {
		$('#errTyleDat').text("Nhập số nguyên từ 1 đến 100");
		valid = false;
	}
	var khoangCachToiDa = $('#maxKhoangCach').val();
	if (isSoNguyenDuong(khoangCachToiDa)) {
		$('#errKhoangCach').text("");
	} else {
		$('#errKhoangCach').text("Nhập số nguyên lớn hơn 0");
		valid = false;
	}
	var thoiGianCSKHToiDa = $('#maxThoiGianCSKH').val();
	if (isSoNguyenDuong(thoiGianCSKHToiDa)) {
		$('#errThoiGianCSKH').text("");
	} else {
		$('#errThoiGianCSKH').text("Nhập số nguyên lớn hơn 0");
		valid = false;
	}
	if (valid) {
		$('#extendedFilter').hide();
		$('#frmPerformance').submit();
	}

}
// xoa loc mo rong
function resetFilter() {
	$('#province'), $('#district'), $('#pointOfSale'), $('#branch'),
			$('#agent'), $('#employer')
	reSelect($('#province'), 0);
	reSelect($('#district'), 0);
	reSelect($('#pointOfSale'), 0);
	reSelect($('#branch'), 0);
	reSelect($('#agent'), 0);
	reSelect($('#employer'), 0);
	$('#frm_startDate').val("");
	$('#frm_endDate').val("");
	reSelect($('#selectPhanNan'), -1);
	// $('#minTyleDat').val("");
	reSelect($('#selectTyleDat'), -1);
	// $('#maxKhoangCach').val("");
	reSelect($('#selectKhoangCach'), -1);
	// $('#maxThoiGianCSKH').val("");
	reSelect($('#selectThoiGianCSKH'), -1);
	reSelect($('#selectXepLoai'), -1);
}
function isSoNguyenDuong(txt) {
	var regex = /^[1-9][0-9]*$/g;
	return (txt.length > 0) && regex.test(txt);
}
