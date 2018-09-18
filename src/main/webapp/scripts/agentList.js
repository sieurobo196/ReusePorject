function changeProvince(province,district,ward) {
	district.empty();
	ward.empty();	
	var provinceId = province.val();
	var url = getAppRootPath()+"/ajax/getDistricts?cityId=" + provinceId;
	$.ajax({type: 'GET', dataType:'json', url: url, success: function(data) {
		var cboDistrict = district;
		cboDistrict.append("<option value='0'>--- Chọn Quận/Huyện ---</option>");
		for ( var i = 0; i < data.data.length; i++) {
			var item = data.data[i];
			var cboOoption = $("<option value = '" + item.id + "'>" + item.name + "</option>");
			cboDistrict.append(cboOoption);
		}
	}});
}
function changeDistrict(district,ward) {
	ward.empty();
	var districtId = district.val();
	var url = getAppRootPath()+"/ajax/getWards?districtId=" + districtId;
	$.ajax({type: 'GET', dataType:'json', url: url, success: function(data) {
		var cboWard = ward;
		cboWard.append("<option value='0'>--- Chọn Phường/Xã ---</option>");
		for ( var i = 0; i < data.data.length; i++) {
			var item = data.data[i];
			var cboOoption = $("<option value = '" + item.id + "'>" + item.name + "</option>");
			cboWard.append(cboOoption);
		}
	}});
}
function changeWard(ward) {
	// DO NOTHING
}