function changeArea(vungMien, tinhThanhPho) {

	var vungMienId = vungMien.val();
	
	var url = getAppRootPath() + "/ajax/getArea?vungMienId=" + vungMienId;
	$.ajax({
		type: 'GET', 
		dataType:'json', 
		url: url, 
		success: function(data) {
			renderComboBox(tinhThanhPho, data.data, tinhThanhPho[0].firstChild.text);
		}
	});
}

function changeCity(province, district) {

	var provinceId = province.val();
	
	var url = getAppRootPath() + "/ajax/getDistricts?cityId=" + provinceId;
	$.ajax({
		type: 'GET', 
		dataType:'json', 
		url: url, 
		success: function(data) {
			renderComboBox(district, data.data, district[0].firstChild.text);
		}
	});
}

function changeProvince(province, district, ward) {

	var provinceId = province.val();
	
	var url = getAppRootPath() + "/ajax/getDistricts?cityId=" + provinceId;
	$.ajax({
		type: 'GET', 
		dataType:'json', 
		url: url, 
		success: function(data) {
			renderComboBox(district, data.data, district[0].firstChild.text);
		}
	});
	renderComboBoxEmpty(ward, ward[0].firstChild.text);
}

function changeDistrict(district, ward) {

	var districtId = district.val();

	var url = getAppRootPath() + "/ajax/getWards?districtId=" + districtId;
	$.ajax({
		type: 'GET', 
		dataType:'json', 
		url: url, 
		success: function(data) {
			renderComboBox(ward, data.data, ward[0].firstChild.text);
		}
	});
}


function selectBranchUser(branch, agent) {
	var branchId = branch.val();
	var url = getAppRootPath() + "/ajax/getAgentsByBranch?branchId=" + branchId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data.data, "-- Đại Lý --");
		}
	});
}


function changeUserCompany(company, agent, pos) {
	var companyId = company.val();
	var url = getAppRootPath() + "/ajax/getAgentsByCompany?companyId=" + companyId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data.data, agent.find('option').first().text());
		}
	});
	if (pos != null && pos.length != 0) {
		if (pos instanceof Array) {
			for (var i = 0; i < pos.length; i++ ) {
				renderComboBoxEmpty(pos[i], pos[i].find('option').first().text());
			}
		} else {
			renderComboBoxEmpty(pos, pos.find('option').first().text());
		}
	}
}

function changeUserAgent(agent, pos) {
	var agentId = agent.val();
	var url = getAppRootPath() + "/ajax/getPosByAgent?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(pos, data.data, "-- Điểm Bán Hàng --");
		}
	});
}

function changeBranchListChannel(branch, agent, pos, user) {
	var branchId = branch.val();
	var url = getAppRootPath() + "/ajax/getAgentsByBranchChannel?branchId=" + branchId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data.data, "-- Đại Lý --");
		}
	});
	renderComboBoxEmpty(pos, "-- Điểm Bán Hàng --");
	renderComboBoxEmpty(user, "-- Nhân Viên --");
}




function changeBranchList(branch, agent, pos, user) {
	var branchId = branch.val();
	var url = getAppRootPath() + "/ajax/getAgentsByBranch?branchId=" + branchId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data.data, "-- Đại Lý --");
		}
	});
	renderComboBoxEmpty(pos, "-- Điểm Bán Hàng --");
	renderComboBoxEmpty(user, "-- Nhân Viên --");
}

function changeAgentList(agent, pos, user) {
	var agentId = agent.val();
	var url = getAppRootPath() + "/ajax/getPosByAgent?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(pos, data.data, "-- Điểm Bán Hàng --");
		}
	});
	renderComboBoxEmpty(user, "-- Nhân Viên --");
}


function changeAgentUserList(agent, user) {
	var agentId = agent.val();
	var url = getAppRootPath() + "/ajax/getEmployersByAgent?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(user, data.data, "-- Nhân Viên --");
		}
	});
	
}

function changePosList(pos, user) {
	var posId = pos.val();
	var url = getAppRootPath() + "/ajax/getUserByPos?posId=" + posId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(user, data.data, "-- Nhân Viên --");
		}
	});
}

function changeProvince(province, district, ward, pos) {
	var provinceId = province.val();
	var url = getAppRootPath() + "/ajax/getDistricts?cityId=" + provinceId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(district, data.data, "-- Quận/Huyện --");
		}
	});
	renderComboBoxEmpty(ward, "-- Phường/Xã --");
	renderComboBoxEmpty(pos, "-- Điểm Bán Hàng --");
}

function changeDistrict(district, ward, pos) {
	var districtId = district.val();
	var url = getAppRootPath() + "/ajax/getWards?districtId=" + districtId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(ward, data.data, "-- Phường/Xã --");
		}
	});
	renderComboBoxEmpty(pos, "-- Điểm Bán Hàng --");
}

function changeWard(ward, pos) {
	var wardId = ward.val();
	var url = getAppRootPath() + "/ajax/getPointOfSalesByWard?wardId=" + wardId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(pos, data.data, "-- Điểm Bán Hàng --");
		}
	});
}

function changeBranch(branch, agent, empDistrict, employer, dummy) {
	dummy.click();
	var branchId = branch.val();
	var url = getAppRootPath() + "/ajax/getEmployersByBranch?branchId="
			+ branchId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(employer, data.data, "-- Nhân viên --");
		}
	});
	// reSelect(agent, 0);
}


function changeAgent(branch, agent, empDistrict, employer, dummy) {
	dummy.click();
	var agentId = agent.val();
	var url = getAppRootPath() + "/ajax/getEmployersByAgent?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(employer, data.data, "-- Nhân viên --");
		}
	});
	//renderComboBoxEmpty(empDistrict, "-- Quận/Huyện --");
}

function viewAddress(rel_url, path_var) {
	if (path_var > 0) {
		window.location.replace(getAppRootPath() + rel_url + "/" + path_var);
	}
}

function changeEmployeeType(employeeType) {
	if (employeeType.value == 1) {
		$("select#branch").attr('disabled', false);
		$("select#branch").select2();
		$("select#agent").attr('disabled', true);
		$("select#agent").val($("select#agent option:first").val());
		$("select#agent").select2();
	} else if (employeeType.value == 2) {
		$("select#agent").attr('disabled', false);
		$("select#agent").select2();
		$("select#branch").attr('disabled', true);
		$("select#branch").val($("select#branch option:first").val());
		$("select#branch").select2();
	}
	$district = $('#districtOfEmployer');
	if ($district.length != 0) {
		renderComboBoxEmpty($district, $('#districtOfEmployer option:first').text());
		$district.select2();
	}
	$pointOfSale = $('#pointOfSale');
	if ($pointOfSale.length != 0) {
		renderComboBoxEmpty($pointOfSale, $('#pointOfSale option:first').text());
		$pointOfSale.select2();
	}
	$employer = $('#employer');
	if ($employer.length != 0) {
		renderComboBoxEmpty($employer, $('#employer option:first').text());
		$employer.select2();
	}
}

// thay het cac option
function renderComboBox($domCbo, items, msg) {
	renderComboBoxEmpty($domCbo, msg);// remove old options
	
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '" + item.id + "'>" + item.name
				+ "</option>");
		$domCbo.append($domOption);
	}
}
// xoa cac option
function renderComboBoxEmpty($domCbo, msg) {
	if ($domCbo == null) return;
	if ($domCbo.find('option').length > 1) {
		// chi xoa neu danh sach co tu 2 option tro len
		$domCbo.empty();
		var $domOption = $("<option value = '0' selected='selected'>" + msg
				+ "</option>");
		$domCbo.append($domOption);
	}
	$domCbo.select2();
}
// doi option duoc select
function reSelect($domCbo, value) {
	if ($domCbo.find('option').length > 1) {
		// chi chon lai neu danh sach co tu 2 option tro len
		$domCbo.find('option:selected').removeAttr('selected');
		$domCbo.find("option[value='" + value + "']").attr('selected',
				'selected');
	}
}

function toggleSelection() {
	var filterBy = $("select#filterBy").val();
	$('#text').val("");
	if (filterBy == "0") {
		$('div#filterByPos').show();
		$('div#filterByEmp').hide();
		$('#province').removeAttr('disabled');
		$('#province').select2();
		$('#provinceArea').attr('disabled','disabled');
		$('#agent').val(0);
		$('#agent').select2();
		$('#area').val(0);
		$('#area').select2();
		$('#provinceArea').val(0);
		$('#provinceArea').select2();
		$('#employer').val(0);
		$('#employer').select2();
	} else {
		$('div#filterByPos').hide();
		$('div#filterByEmp').show();
		$('#province').attr('disabled','disabled');
		$('#provinceArea').removeAttr('disabled');
		$('#provinceArea').select2();
		$('#province').val(0);
		$('#province').select2();
		$('#district').val(0);
		$('#district').select2();
		$('#ward').val(0);
		$('#ward').select2();
		$('#pointOfSale').val(0);
		$('#pointOfSale').select2();
	}
}


function deleteUserPOS(posId){
	var flag = confirm("Bạn có muốn xóa điểm bán hàng của nhân viên?" );
	if (flag==true)
	{
		var url = getAppRootPath() + "/pos/deleteUserPOS?posId=" + posId;
			$.ajax({
				type : 'POST',
				dataType : 'json',
				url : url,
				success : function(data) {
					if (data.status != 'OK') {
						alert('Có lỗi xảy ra khi xóa dữ liệu!');
					} else{
						alert('Xóa dữ liệu thành công.');
						location.reload();
					}
				}
			});
	  }
	
}

function changeRegionList(region, des, reset) {
	var agentId = region.val();
	var url = getAppRootPath() + "/ajax/getAreaByRegion?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(des, data, des[0].firstChild.text);
		}
	});
	if (reset != null && reset.length != 0) {
		if (reset instanceof Array) {
			for (var i = 0; i < reset.length; i++ ) {
				renderComboBoxEmpty(reset[i], reset[i].find('option').first().text());
			}
		} else {
			renderComboBoxEmpty(reset, reset.find('option').first().text());
		}
	}
}
function changeAreaList(area, des, reset) {
	var areaId = area.val();
	var url = getAppRootPath() + "/ajax/getProvinceByArea?areaId=3" + areaId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(des, data, des[0].firstChild.text);
		}
	});
	if (reset != null && reset.length != 0) {
		if (reset instanceof Array) {
			for (var i = 0; i < reset.length; i++ ) {
				renderComboBoxEmpty(reset[i], reset[i].find('option').first().text());
			}
		} else {
			renderComboBoxEmpty(reset, reset.find('option').first().text());
		}
	}
}
function changeProvinceList(province, user) {
	var provinceId = province.val();
	var url = getAppRootPath() + "/ajax/getUserByProvince?provinceId=" + provinceId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(user, data, user[0].firstChild.text);
		}
	});
}