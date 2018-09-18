var channel;

function openSearchVasInfoForm(id,tennv) {
	document.getElementById('userId').value=id;
	document.getElementById('tennv').value=tennv;
	
	
	$('#spopup_chiNhanh').removeAttr("selected");
	$('#spopup_agent').removeAttr("selected");
	tb_show('Tìm kiếm thành viên','TB_inline?height=450&amp;width=700&amp;inlineId=searchVASDialog');
	$('#searchTable').empty();
}

function openDeleteVasInfoForm(vasAcc) {
	document.getElementById('userVasAccount').value=vasAcc;
	
	tb_show('Xóa số VAS thành viên','TB_inline?height=200&amp;width=450&amp;inlineId=deleteVASDialog');
	$('#searchTable').empty();
}


function openSearchUserPOSInfoForm(id,tennv) {
	document.getElementById('upos_userId').value=id;
	document.getElementById('upos_tennv').value=tennv;
	$('#spopup_chiNhanh').removeAttr("selected");
	$('#spopup_agent').removeAttr("selected");
	tb_show('Tìm kiếm thành viên','TB_inline?height=450&amp;width=700&amp;inlineId=searchUserPOSDialog');
	$('#searchTable').empty();
}

function openSearchEVoucherInfoForm(id) {
	document.getElementById('EV_userId').value=id;
	document.getElementById('inUserName').value='';
	
	tb_show('Cập nhật eVoucher','TB_inline?height=300&amp;width=500&amp;inlineId=searchEVoucherDialog');
	$('#searchTable').empty();
	var url = getAppRootPath() + "/ajax/getUserInfo?userId="+id;
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			$('#loading').hide();
			document.getElementById('userName').innerHTML=data.data.userName;
			document.getElementById('userCode').innerHTML=data.data.userCode;
			document.getElementById('email').innerHTML=data.data.email;
			document.getElementById('phoneNumber').innerHTML=data.data.phoneNumber;
		}
	});
	
}

function openSearchUserInfoForm() {
	tb_show('Tìm kiếm thành viên','TB_inline?height=450&amp;width=700&amp;inlineId=searchUserDialog');
	$('#searchUserInfoTable').empty();
}


function openSearchForm(chl) {
	channel = chl;
	if (channel == 1) { // agent
		$('#s_dist').hide();
		$('#s_ward').hide();
	} else {
		$('#s_dist').show();
		$('#s_ward').show();
	}
	tb_show('Tìm kiếm thành viên','TB_inline?height=450&amp;width=700&amp;inlineId=searchDialog');
	$('#searchTable').empty();
}
function searchMember() {
	var dictrictId = 0;
	var wardId = 0;
	var companyId = $('#companyID').val();
	if (channel == 2) {
		dictrictId = $('#s_dist').val();
		wardId = $('#s_ward').val();
	}
	
	var code = $('#s_code').val();
	var url = getAppRootPath() + "/user/searchMember";
	var pa = "channel=" + channel + "&companyId=" + companyId + "&districtId=" + dictrictId + 
		"&wardId=" + wardId + "&code=" + code ;
	
	$('#loading').show();
	$.ajax({
		type : 'POST',
		dataType : 'json',
		url : url,
		data : pa,
		success : function(data) {
			$('#loading').hide();
			fillMemberData(data);
		},
		contentType: "application/x-www-form-urlencoded; charset=UTF-8"
	});
}


function searchVasAcc() {
	
	var branchId = $('#spopup_chiNhanh').val();
	var agentId = $('#spopup_agent').val();
	var code = $('#s_phone').val();
	
	if((branchId==null)||(branchId<=0)){
		alert("Bạn chưa chọn chi nhánh");
		return;
	}
	
	
	if((agentId==null)||(agentId<=0)){
		alert("Bạn chưa chọn tổng đại lý");
		return;
	}
	
	
	var url = getAppRootPath() + "/user/searchVasAcc?branchId=" + branchId + "&agentId=" + agentId + "&code=" + code ;
	
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			$('#loading').hide();
			fillVasAccData(data);
		}
	});
}

function searchUserPOS() {
	
	var branchId = $('#upos_chiNhanh').val();
	var districtId = $('#s_dist').val();
	var wardId = $('#s_ward').val();
	var code = $('#upos_code').val();
	
	if((branchId==null)||(branchId<=0)){
		alert("Bạn chưa chọn chi nhánh");
		return;
	}
	
	
	var url = getAppRootPath() + "/user/getPOSforUser?branchId=" + branchId + "&districtId=" + districtId +"&wardId=" + wardId + "&code=" + code ;
	
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			$('#loading').hide();
			fillUserPOSData(data);
		}
	});
}


function searchUserAcc() {
	
	var branchId = $('#spopup_chiNhanh').val();
	var agentId = $('#spopup_agent').val();
	var code = $('#spopup_code').val();
	
	if((branchId==null)||(branchId<=0)){
		alert("Bạn chưa chọn chi nhánh");
		return;
	}
	
	
//	if((agentId==null)||(agentId<=0)){
//		alert("Bạn chưa chọn tổng đại lý");
//		return;
//	}
	
	
	var url = getAppRootPath() + "/user/searchUserInfo?branchId=" + branchId + "&agentId=" + agentId + "&code=" + code ;
	
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			$('#loading').hide();
			fillUserInfoData(data);
		}
	});
}

function searchDaiLy(){
	
}

function fillVasAccData(data) {
	var sb = $('#searchVASTable');
	var userId = document.getElementById('userId').value;
	
	sb.empty();
	
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center">'+(i+1)+'</td>';
		row += '<td width="10%" align="center">'+createLable(id, item.id)+'</td>';
		row += '<td width="30%">'+createLable(id, item.tenDiemBan)+'</td>';
		row += '<td width="25%">'+createLable(id, item.maDaiLy)+'</td>';
		row += '<td width="15%" align="center">'+createLable(id, item.status)+'</td>';
		row += '<td width="15%" align="center"><a href="javascript:;" onClick="addVasAcc('+item.id+','+userId+',\''+item.maDaiLy+'\');" class="redTxt"><span><span>Thêm</span></span><a/></td>';
		row += '</tr>';
		
		sb.append($(row));
	}
}


function fillUserInfoData(data) {
	var sb = $('#searchUserInfoTable');
	//var userId = document.getElementById('userId').value;
	//alert('aaaaaaaaaaaaaaaaaaaaaaaaaa');
	sb.empty();
	
	for ( var i = 0; i < data.length; i++) {
		
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center">'+(i+1)+'</td>';
		row += '<td width="10%" align="center">'+createLable(id, item.maNhanVien)+'</td>';
		row += '<td width="30%">'+createLable(id, item.tenNhanVien)+'</td>';
		row += '<td width="25%">'+createLable(id, item.maDaiLy)+'</td>';
		//row += '<td width="15%" align="center">'+createLable(id, item.maDaiLy)+'</td>';
		row += '<td width="15%" align="center"><a href="javascript:;" onClick="addUserInfo(\''+item.maNhanVien+'\',\''+item.tenNhanVien+'\',\''+item.tenDaiLy+'\',\''+item.daiLyId+'\');" class="redTxt"><span><span>Thêm</span></span><a/></td>';
		row += '</tr>';
		
		sb.append($(row));
	}
	
}

function addUserInfo(maNhanVien,tenNhanVien,tenDaiLy,daiLyId){
	$('#maDangNhap').val(maNhanVien);
	$('#maNhanVien').val(maNhanVien);
	$('#ten').val(tenNhanVien);
	$('#chooseAgent').text(tenDaiLy);
	//$('input:radio[name=phanLoai]:checked').val(2);
	$('#nhanVienDaiLy').click();
	$('#daiLy').val(daiLyId);
	
	tb_remove();
}
// cache objects return by search
var searchObject = new Array();
var searchObjectMap = new Object();
function fillMemberData(data) {
	var sb = $('#searchTable');
	sb.empty();
	searchObject = new Array();// 
	searchObjectMap = new Object();
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center"><input type="radio" id="'+id+'" name="s_member" value="'+item.id+'" onchange="selectMember(this)"/></td>';
		row += '<td width="10%" align="center">'+createLable(id, item.code)+'</td>';
		row += '<td width="30%">'+createLable(id, item.name)+'</td>';
		row += '<td width="45%">'+createLable(id, item.address)+'</td>';
		row += '<td width="10%" align="center">'+createLable(id, item.phoneNum.replace(/;/g, '<br>'))+'</td>';
		row += '</tr>';
		searchObject[i] = item;
		searchObjectMap[id] = item;
		sb.append($(row));
	}
}

function fillUserPOSData(data) {
	var sb = $('#searchUserPOSInfoTable');
	sb.empty();
	$('#checkbox_all')[0].checked = false;
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center"><input type="checkbox" id="'+id+'" name="s_member" value="'+item.id+'"/></td>';
		row += '<td width="10%" align="center">'+createLable(id, item.code)+'</td>';
		row += '<td width="30%">'+createLable(id, item.name)+'</td>';
		row += '<td width="45%">'+createLable(id, item.address)+'</td>';
		row += '<td width="10%" align="center">'+createLable(id, item.phoneNum)+'</td>';
		row += '</tr>';
		sb.append($(row));
	}
}

function createLable(id, value) {
	return '<label for="' + id + '">' + value + '</label>';
}
function selectMember(el) {
	var item = searchObjectMap[el.id];
	if (channel == 1) {
		$('#nhanVienDaiLy').click();
		$('#chooseAgent').text(item.name);
		$('#daiLy').val(el.value);
		$('#chooseUm').text('N/A');
		$('#nvBanHang').val(0);
		$('#choosePos').text('N/A');
		$('#diemBanHang').val(0);
		
		if((item.loaiDaiLy!=null)&&(item.loaiDaiLy==4)&&
				(jQuery('#maNhanVien').attr('readonly') === undefined)//check is update
				){
			$("#maNhanVien").val('');
			$("#maNhanVien").attr('disabled', true);
		}
	} else if (channel == 2) {
		$('#nhanVienDBH').click();
		$('#choosePos').text(item.name);
		$('#diemBanHang').val(el.value);
		$('#chooseUm').text('N/A');
		$('#nvBanHang').val(0);
		$('#chooseAgent').text('N/A');
		$('#daiLy').val(0);
	} else if (channel == 3) {
		$('#nhanVienBH').click();
		$('#chooseUm').text(item.name);
		$('#nvBanHang').val(el.value);
		$('#choosePos').text('N/A');
		$('#diemBanHang').val(0);
		$('#chooseAgent').text('N/A');
		$('#daiLy').val(0);
	}
	tb_remove();
	$('#searchTable').html('');
}


function selectBranch(branch) {
	loadDistrict(branch);
	
}

function selectAgentByBranchId(branchId) {
	loadAgents(branchId);
	
}

function loadAgents(branch) {
	var cn = $('#spopup_chiNhanh').val();
	var url = contextPath + "/ajax/getAgentsByBranch?branchId="+cn;
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			renderComboBox("#spopup_agent", data.data, "--- Đại Lý ---");
		}
	});
}

function loadDistrict(branch) {
	var url = contextPath + "/kbh/getDistrictByBranch/"+branch.value;
	$.ajax({
		type : 'GET',
		url : url,
		success : function(data) {
			renderComboBox("#s_dist", data, "--- Quận/Huyện ---");
			renderComboBoxEmpty("#phuongXa", "--- Phường/Xã ---");
			
		}
	});
}
function selectTown(domTown, ward) {
	if (domTown == null) return;
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


function addVasAcc(vasAcc, userId,agentCode){
	var tennv = document.getElementById('tennv').value;
	var flag = confirm("Bạn có muốn thêm tài khoản VAS: "+vasAcc+ " cho nhân viên: " + tennv  );
	if (flag==true)
	  {
		
		var url = getAppRootPath() + "/user/addNewVasAcc/";
		
		$.ajax({
			url:url, 
			type:"POST", 
			dataType: "json",
			data: JSON.stringify( {"userId":userId,"vasAcc":vasAcc,"agentCode":agentCode}),
		

			contentType: "application/json; charset=utf-8",
				//data:$('#keHoachBanHang').serialize(), 
				success: function(response) {
					var stringified = JSON.stringify(response);
					var obj = JSON.parse(stringified)
					console.log( obj.data.message );
					console.log( obj.data.result );
					//alert(obj.data.message);
					if(obj.data.code==200){
						tb_remove();
						alert(obj.data.message);
						location.reload(); 
					}else{
						tb_remove();
						alert(obj.data.message);
						//document.getElementById('error_1').innerHTML = obj.data.message;
					}
				
					//$('#keHoachBanHang').find('.error').html(response);
			},
			error: function(xhr, error) {
				document.getElementById('error_1').innerHTML = 'Xảy ra lỗi khi lưu dữ liệu.';
			}
		});
	
	
	  }
	
}


function addEVoucherAcc(){
	var tennv = document.getElementById('userName').innerHTML;
	var evoucherAcc = document.getElementById('inUserName').value;
	var userId = document.getElementById('EV_userId').value;
	var flag = confirm("Bạn có muốn thêm tài khoản VAS: "+evoucherAcc+ " cho nhân viên: " + tennv );
	if (flag==true)
	  {
		
		var url = getAppRootPath() + "/user/addNewEVoucherAcc/";
		
		$.ajax({
			url:url, 
			type:"POST", 
			dataType: "json",
			data: JSON.stringify( {"userId":userId,"eVoucherAcc":evoucherAcc}),
		

			contentType: "application/json; charset=utf-8",
				//data:$('#keHoachBanHang').serialize(), 
				success: function(response) {
					var stringified = JSON.stringify(response);
					var obj = JSON.parse(stringified)
					console.log( obj.data.message );
					console.log( obj.data.result );
					//alert(obj.data.message);
					if(obj.data.code==200){
						tb_remove();
						alert(obj.data.message);
						location.reload(); 
					}else{
						tb_remove();
						alert(obj.data.message);
						//document.getElementById('error_1').innerHTML = obj.data.message;
					}
				
					//$('#keHoachBanHang').find('.error').html(response);
			},
			error: function(xhr, error) {
				document.getElementById('error_1').innerHTML = 'Xảy ra lỗi khi lưu dữ liệu.';
			}
		});
	
	
	  }
	
}

/*var $domTown = $("#s_dist");
war = $("#s_ward");
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
}*/


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
	$domCbo.select2();
}

function addMember() {
	var all = $('#searchUserPOSInfoTable input:checked');
	if (all.length == 0) {
		alert('Chưa chọn thành viên!');
		return;
	}
	var fm = $("#userList");
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		fm.append('<input type="hidden" name="addMember" value="'+item.value+'"/>');
	}
	$('#loading').show();
	var userId = document.getElementById('upos_userId').value;
	alert(userId);
	fm.append('<input type="hidden" name="upos_userId" value="'+userId+'"/>');
	fm.append('<input type="hidden" name="funcType" value="addPos"/>');
	fm.action = getAppRootPath() + "/user/list";
	fm.submit();
}

function selectAllMember(el) {
	var all = $('#searchUserPOSInfoTable input');
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		item.checked = el.checked;
	}
}
