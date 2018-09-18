
function selectLevel(level) {
	var levels = $('select[name="parentLevel"]');
	level = $(level);
	if (level.attr('id') == levels.last().attr('id')) {
		return;
	}
	var branchId = $('#chiNhanh').val();
	var levelId = level.attr('id').split('_')[1];
	var start = false;
	var loadPos = false;
	if (levels.length > 1 && levels[levels.length - 2].id == level.attr('id')) {
		loadPos = true;
	}
	for ( var i = 0; i < levels.length; i ++) {
		var item = levels[i];
		if ($(item).attr('id') == level.attr('id')) {
			start = true;
			continue;
		}
		if (start) {
			levelId = item.id.split('_')[1];
			break;
		}
	}
	var parent = level.val();
	var url = contextPath + "/kbh/getMemberByBranch/"+branchId+"/"+levelId+"/"+parent+"/"+loadPos;
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			var nextLevel = null, start;
			for ( var i = 0; i < levels.length; i ++) {
				var item = levels[i];
				if ($(item).attr('id') == level.attr('id')) {
					start = true;
					continue;
				}
				if (start && nextLevel == null) {
					nextLevel = item;
					continue;
				}
				if (nextLevel) {
					renderComboBoxEmpty(item, $(item)[0].firstChild.text);
				}
			}
			renderComboBox(nextLevel, data, nextLevel.firstChild.text);
			
		}
	});
}
function selectBranch(branch) {
	loadDistrict(branch);
	var levels = $('select[name="parentLevel"]');
	if (levels.length == 0) {
		return;
	}
	var branchId = $(branch).val();
	var level = levels.attr('id').split('_')[1];
	var parent = 0;
	var url = contextPath + "/kbh/getMemberByBranch/"+branchId+"/"+level+"/"+parent+"/false";
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			renderComboBox(levels[0], data, levels[0].firstChild.text);
			for ( var i = 1; i < levels.length; i++) {
				var lv = levels[i];
				renderComboBoxEmpty(lv, lv.firstChild.text);
			}
		}
	});
}
function loadDistrict(branch) {
	var url = contextPath + "/kbh/getDistrictByBranch/"+branch.value;
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			renderComboBox("#quanHuyen", data, "--- Quận/Huyện ---");
			renderComboBox("#s_dist", data, "--- Quận/Huyện ---");
			renderComboBoxEmpty("#phuongXa", "--- Phường/Xã ---");
			renderComboBoxEmpty("#s_ward", "--- Phường/Xã ---");
		}
	});
}
function selectTown(domTown, ward) {
	var $domTown = $(domTown);
	war = $(ward);
	var townUid = $domTown.val();
	if (townUid != 0) {
		var url = contextPath + "/channel/findByTownUid/" + townUid;
		$.ajax({
			type : 'GET',
			url : url,
			success : function(data) {
				renderComboBox(war[0], data, war[0].firstChild.text);
			}
		});
	} else {
		
		renderComboBoxEmpty(war[0], war[0].firstChild.text);
	}
}

function selectPhanLoai(phanLoai) {
	
	
	if (phanLoai != 0) {
		var url = contextPath + "/ajax/getRoleForAddUser/" + phanLoai;
		$.ajax({
			type : 'GET',
			url : url,
			success : function(data) {
				renderComboBox("#vaiTro", data,'-- Chọn Vai Trò --');
			}
		});
	} else {
		
		renderComboBoxEmpty("#vaiTro",'-- Chọn Vai Trò --');
	}
}

function renderComboBox(comboBoxSelector, items, label) {
	var $domCbo = $(comboBoxSelector);
	renderComboBoxEmpty(comboBoxSelector, label);
	
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		
		var $domOption = $("<option value = '" + item.id + "'>" + item.name
				+ "</option>");
		$domCbo.append($domOption);
	}
}
function renderComboBoxEmpty(comboBoxSelector, label) {
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();// remove old options
	var $domOption = $("<option value = '0'>" + label + "</option>");
	$domCbo.append($domOption);
}

function timKiemDL(){
	if (!isSelectParent()) {
		return;
	}
	document.forms['dailyListForm'].action = contextPath + '/kbh/kbhcap';
	document.forms['dailyListForm'].submit();
}

function deleteDL(id, type) {
	var ans = confirm('Xóa Đại Lý này? Nếu xóa sẽ xóa tất cả các đại lý con.');
	if (!ans) return;
	var fm = $(document.forms['dailyListForm']);
	fm.append('<input type="hidden" name="deleteId" value="'+id+'"/>');
	fm.append('<input type="hidden" name="memberType" value="'+type+'"/>');
	fm.submit();
}

function XemDL(id) {
	var url = contextPath + "/kbh/kbhcap?id=" + id;
	document.forms["dailyListForm"].action = url;
	document.forms["dailyListForm"].submit();
}
function isSelectParent() {
	var levels = $('select[name="parentLevel"]');
	if (levels.length != 0 && levels[levels.length - 1].value == 0) {
		alert('Chưa chọn thành viên cha!');
		return false;
	}
	return true;
}
function backToBrach() {
	$('select[name="parentLevel"]').remove();
	$('#capDaiLy').val(1);
	document.forms['dailyListForm'].action = contextPath + '/kbh/kbhcap';
	document.forms['dailyListForm'].submit();
}

function findChannel() {
    var channelList = $('#list_channel_vertical').find('select');
    for (var i = channelList.length - 1; i >= 0; i--) {
        if (channelList[i].value != 0) {
            channelSelectedId = parseInt(channelList[i].value);
            break;
        }
    }
}