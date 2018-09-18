
function openDepositForm(userId) {

	document.getElementById('buttonCommissionForm').style.display="none" ;
	document.getElementById('buttonDepositForm').style.display="inline" ;
	
	document.getElementById('titleDepositForm').style.display="inline" ;
	document.getElementById('titleCommissionForm').style.display="none" ;
	
	loadMember(userId,0);
	tb_show('Nạp tiền cho nhân viên','TB_inline?height=350&amp;width=500&amp;inlineId=searchDialog1');
}


function openCommissionForm(userId) {
	document.getElementById('buttonCommissionForm').style.display="inline" ;
	document.getElementById('buttonDepositForm').style.display="none" ;
	
	document.getElementById('titleDepositForm').style.display="none" ;
	document.getElementById('titleCommissionForm').style.display="inline" ;
	loadMember(userId,1);
	tb_show('Chuyển hoa hồng sang tiền cho nhân viên','TB_inline?height=350&amp;width=500&amp;inlineId=searchDialog1');
}

function loadMember(userId,type){
	
	var url = getAppRootPath() + "/ajax/getUserInfo?userId=" + userId ;
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			
			var stringified = JSON.stringify(data);
			var obj = JSON.parse(stringified)
			console.log( obj.data.userName );
			document.getElementById('tennv').innerHTML=obj.data.userName;
			document.getElementById('sdtnv').innerHTML=obj.data.phoneNumber;
			document.getElementById('emailnv').innerHTML=obj.data.email;
			//document.getElementById('chinhanhnv').innerHTML=obj.data.email;
			//document.getElementById('dailynv').innerHTML=obj.data.phoneNumber;
			document.getElementById('sodunv').innerHTML=obj.data.balance;
			document.getElementById('hoahongnv').innerHTML=obj.data.commission;
			document.getElementById('diemthuongnv').innerHTML=obj.data.reward;
			document.getElementById('userId').value=userId;
			if(type==1){
				document.getElementById('soTien').value = obj.data.commission;
			}
			$('#loading').hide();
		}
	});
}

function deposit(){
	
	var tennv = document.getElementById('tennv').innerHTML;
	var sotien = document.getElementById('soTien').value;
	var userId = document.getElementById('userId').value;
	var content = document.getElementById('noidung').value;
	var flag = confirm("Bạn có muốn chuyển hoa hồng sang tiền cho nhân viên:" + tennv + " với số tiền: " + sotien + "vnd" );
	if (flag==true)
	  {
		
		var url = getAppRootPath() + "/user/recharge/";
		
		$.ajax({
			url:url, 
			type:"POST", 
			dataType: "json",
			data: JSON.stringify( {userId:userId,amount:sotien,content:content}),
		

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


function transCommission(){
	
	var tennv = document.getElementById('tennv').innerHTML;
	var sotien = document.getElementById('soTien').value;
	var userId = document.getElementById('userId').value;
	var content = document.getElementById('noidung').value;
	var flag = confirm("Bạn có muốn nạp tiền cho nhân viên:" + tennv + " với số tiền: " + sotien + "vnd" );
	if (flag==true)
	  {
		
		var url = getAppRootPath() + "/user/transCommission/";
		
		$.ajax({
			url:url, 
			type:"POST", 
			dataType: "json",
			data: JSON.stringify( {userId:userId,amount:sotien,content:content}),
		

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


function searchMember() {
	var channel = $('#s_type').val();
	var branchId = $('#chiNhanh').val();
	var dictrictId = $('#s_dist').val();
	var wardId = $('#s_ward').val();
	var code = $('#s_code').val();
	var url = getAppRootPath() + "/kbh/getMember?channel=" + channel + "&branchId=" + branchId + "&districtId=" + dictrictId + 
		"&wardId=" + wardId + "&code=" + code;
	$('#loading').show();
	$.ajax({
		type : 'GET',
		dataType : 'json',
		url : url,
		success : function(data) {
			$('#loading').hide();
			fillMemberData(data);
		}
	});
}
// cache objects return by search
var searchObject = new Array();
var searchObjectMap = new Object();
function fillMemberData(data) {
	var sb = $('#searchTable');
	sb.empty();
	$('#checkbox_all')[0].checked = false;
	searchObject = new Array();// 
	searchObjectMap = new Object();
	for ( var i = 0; i < data.length; i++) {
		var item = data[i];
		var id = "s_" + item.id;
		var row = '<tr>';
		row += '<td width="5%" align="center"><input type="checkbox" id="'+id+'" name="s_member" value="'+item.id+'"/></td>';
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
function createLable(id, value) {
	return '<label for="' + id + '">' + value + '</label>';
}
function addMember() {
	var all = $('#searchTable input:checked');
	if (all.length == 0) {
		alert('Chưa chọn thành viên!');
		return;
	}
	var fm = $("#dailyListForm");
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		fm.append('<input type="hidden" name="addMember" value="'+item.value+'"/>');
	}
	$('#loading').show();
	fm.append('<input type="hidden" name="memberType" value="'+$('#s_type').val()+'"/>');
	fm.action = getAppRootPath() + "/kbh/kbhcap";
	fm.submit();
}
function selectAllMember(el) {
	var all = $('#searchTable input');
	for ( var i = 0; i < all.length; i++) {
		var item = all[i];
		item.checked = el.checked;
	}
}