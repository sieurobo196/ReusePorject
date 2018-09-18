function checkPerformanceSettingForm() {
	var valid = true;
	var tyleDatToiThieu = $('#txtTyLeDatToiThieu').val();
	if (isSoNguyenDuong(tyleDatToiThieu) && parseInt(tyleDatToiThieu) <= 100) {
		$('#errTyLeDatToiThieu').text("");
	} else {
		$('#errTyLeDatToiThieu').text("Nhập số nguyên từ 1 đến 100");
		valid = false;
	}
	var khoangCachToiDa = $('#txtKhoangCachToiDa').val();
	if (isSoNguyenDuong(khoangCachToiDa)) {
		$('#errKhoangCachToiDa').text("");
	} else {
		$('#errKhoangCachToiDa').text("Nhập số nguyên lớn hơn 0");
		valid = false;
	}
	var thoiGianCSKHToiDa = $('#txtThoiGianCSKHToiDa').val();
	if (isSoNguyenDuong(thoiGianCSKHToiDa)) {
		$('#errThoiGianCSKHToiDa').text("");
	} else {
		$('#errThoiGianCSKHToiDa').text("Nhập số nguyên lớn hơn 0");
		valid = false;
	}
	if (valid) {
		$("#frmPerformanceSetting").submit();
	}
}
function isSoNguyenDuong(txt) {
	var regex = /^[1-9][0-9]*$/g;
	return (txt.length > 0) && regex.test(txt);
}
