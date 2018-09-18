function changeProvince(province, district, ward, text, branch, agent, employer) {
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
	reSelect(branch, 0);
	reSelect(agent, 0);
	renderComboBoxEmpty(ward, "-- Chọn Phường/Xã --");
	renderComboBoxEmpty(employer, "-- Chọn Nhân viên --");
}
function changeDistrict(province, district, ward, text, branch, agent, employer) {
	reSelect(branch, 0);
	reSelect(agent, 0);
	var districtId = district.val();
	var url = getAppRootPath() + "/ajax/getWards?districtId=" + districtId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(ward, data.data, "-- Chọn Phường/Xã --");
		}
	});
	var url = getAppRootPath() + "/ajax/getEmployerByDistrict?districtId="
			+ districtId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(employer, data.data, "-- Nhân viên --");
		}
	});
}
function changeWard(province, district, ward, text, branch, agent, employer) {
	reSelect(branch, 0);
	reSelect(agent, 0);
}

function changeBranch(province, district, ward, text, branch, agent, employer,
		type) {
	type.click();
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
	var provinceId = province.val();
	if (provinceId > 0) {
		var url = getAppRootPath() + "/ajax/getDistricts?cityId=" + provinceId;
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url : url,
			success : function(data) {
				renderComboBox(district, data.data, "-- Quận/Huyện --");
			}
		});
	}
	reSelect(ward, 0);
	text.val("");
}
function changeAgent(province, district, ward, text, branch, agent, employer,
		type) {
	type.click();
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
	reSelect(district, 0);
	reSelect(ward, 0);
	text.val("");
}
function changeEmployer(province, district, ward, text, branch, agent, employer) {
	reSelect(ward, 0);
	text.val("");
}

function changeEmployeeType(employeeType) {
	if (employeeType.value == 1) {
		$("select#branch").attr('disabled', false);
		$("select#agent").attr('disabled', true);
		$("select#agent").val($("select#agent option:first").val());
	} else if (employeeType.value == 2) {
		$("select#agent").attr('disabled', false);
		$("select#branch").attr('disabled', true);
		$("select#branch").val($("select#branch option:first").val());
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
	$domCbo.empty();// remove old options
	var $domOption = $("<option value = '0' selected='selected'>" + msg
			+ "</option>");
	$domCbo.append($domOption);
}
// doi option duoc select
function reSelect($domCbo, value) {
	$domCbo.find('option:selected').removeAttr('selected');
	$domCbo.find("option[value='" + value + "']").attr('selected', 'selected');
}
// doi option duoc select
function reSelect1($domCbo, value, text) {
	$domCbo.find('option:selected').removeAttr('selected');
	var $domOption = $domCbo.find("option[value='" + value + "']");
	if ($domOption.length == 0) {
		$domOption = $("<option value = '" + value + "'>" + text + "</option>");
		$domCbo.append($domOption);
	}
	$domOption.attr('selected', 'selected');
}
