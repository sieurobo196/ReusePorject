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
	
	if (valid) {
		$('#frmCommission').submit();
	}

}
function paidCommission(tennv, sotien) {
	var valid = true;
	
	var flag = confirm("Bạn có muốn thanh toán hoa hồng cho nhân viên:" + tennv + " với số tiền: " + sotien + "" );
	if (flag==true)
	  {
		document.getElementById('paidStatus').value=1;
		$('#frmCommission').submit();
	  }
	

}


function channelCommission(numOfStaff, sotien) {
	var valid = true;
	
	var flag = confirm("Bạn có muốn thanh toán hoa hồng cho " + numOfStaff + " nhân viên với số tiền: " + sotien + "" );
	if (flag==true)
	  {
		document.getElementById('paidStatus').value=1;
		$('#frmCommission').submit();
	  }
	

}


function isSoNguyenDuong(txt) {
	var regex = /^[1-9][0-9]*$/g;
	return (txt.length > 0) && regex.test(txt);
}
