function changeBranch(branch, agent, pos, employer) {
	var branchId = branch.val();
	var url = getAppRootPath() + "/channel/getAgentsByBranch?branchId="
			+ branchId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data, "-- Đại lý --");
		}
	});
	renderComboBoxEmpty(pos, "-- Điểm Bán Hàng --");
	renderComboBoxEmpty(employer, "-- Nhân Viên --");
}
function changeCompany(company, agent, pos, employer) {
	var companyId = company.val();
	var url = getAppRootPath() + "/channel/getAgentsByCompany?companyId="
	+ companyId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(agent, data, agent.find('option').first().text());
		}
	});
	var cat = $('#goodsCatId');
	if (cat.length != 0) {
		url = getAppRootPath() + "/ajax/getGoodsCatByCompany?companyId="
		+ companyId;
		$.ajax({
			type : 'GET',
			dataType : 'json',
			url : url,
			success : function(data) {
				renderComboBox(cat, data, cat.find('option').first().text());
				var goods = $('#serviceId');
				renderComboBoxEmpty(goods, goods.find('option').first().text());
			}
		});
	}
	renderComboBoxEmpty(pos, pos.find('option').first().text());
	renderComboBoxEmpty(employer, employer.find('option').first().text());
}

function changeAgent(agent, pos, employer) {
	var agentId = agent.val();
	var url = getAppRootPath() + "/channel/getPosByAgent?agentId=" + agentId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(pos, data.poses, "-- Điểm Bán Hàng --");
			renderComboBox(employer, data.employer, "-- Nhân Viên --");
		}
	});
}

function changePOS(pos, employer, agent) {
	var posId = pos.val();
	if (posId == 0) {
		if (agent == null) {
			agent = $('#agent');
		}
		changeAgent(agent, pos, employer);
		return;
	}
	var url = getAppRootPath() + "/channel/getUserByPos?posId=" + posId;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(employer, data, "-- Nhân Viên --");
		}
	});
}


function changeGoodsCat(goodsCat,service) {
	var goodsCatId = goodsCat.val();
	var url = getAppRootPath() + "/ajax/getSalesGoodsByCat?catId=" + goodsCatId;
	
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox(service, data.data, "-- Dịch vụ --");
		}
	});
}


function viewAddress(rel_url, path_var) {
	if (path_var > 0) {
		window.location.replace(getAppRootPath() + rel_url + "/" + path_var);
	}
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

function toggleSelection(province, district, ward, pos, branch, agent,
		empDistrict, employer, text) {
	var filterBy = $("select#filterBy").val();
	if (filterBy == "0") {
		reSelect(branch, 0);
		reSelect(empDistrict, 0);// renderComboBoxEmpty(empDistrict, "--
									// Quận/Huyện --");
		reSelect(agent, 0);
		reSelect(employer, 0);// renderComboBoxEmpty(employer, "-- Nhân viên
								// --");
		text.val("");
		$('#dummy1').removeAttr('checked');
		$('#dummy2').removeAttr('checked');
		$('div#filterByPos').show();
		$('div#filterByEmp').hide();
	} else {
		reSelect(province, 0);
		reSelect(district, 0);// renderComboBoxEmpty(district, "-- Quận/Huyện
								// --");
		reSelect(ward, 0);// renderComboBoxEmpty(ward, "-- Phường/Xã --");
		reSelect(pos, 0);// renderComboBoxEmpty(pos, "-- Điểm Bán hàng --");
		text.val("");
		$('div#filterByPos').hide();
		$('div#filterByEmp').show();
	}
}
