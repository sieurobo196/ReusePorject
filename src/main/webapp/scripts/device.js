function inactiveDevice(deviceId) {
	if (confirm('Bạn có thật sự muốn ngưng kích hoạt thiết bị được chọn?')) {
		// Inactive selected devices
		var url = getAppRootPath()+"/device/deactivate?deviceId="+deviceId;
		$.ajax({type: 'POST', dataType:'json', url: url, success: function(data) {
			if(data.status!='OK') {
				alert('Thiết bị không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
			}
			else {
				$('#activated'+deviceId).html('false');
			}
		}});
	}
}
function deleteDevice(deviceId) {
	if (confirm('Bạn có thật sự muốn xóa thiết bị đã chọn?')) {
		// Delete selected devices
		var url = getAppRootPath()+"/device/delete?deviceId="+deviceId;
		$.ajax({type: 'POST', dataType:'json', url: url, success: function(data) {
			if(data.status!='OK') {
				alert('Thiết bị không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
			}
			else {
				$('#'+deviceId).hide();
			}
			}
		});
	}
}

function approveReq(stockId) {
	if (confirm('Bạn muốn duyệt yêu cầu hàng từ nhân viên  ?')) {
		// Accept new request
		var url = getAppRootPath()+"/order/approve?stockId="+stockId;
		$.ajax({type: 'POST', dataType:'json', url: url, success: function(data) {
			if(data.status!='OK') {
				alert('Yêu cầu hàng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
			}
			else {
				//window.location.reload();
				window.location.href = getAppRootPath()+"/order/requestGoods/list";
			}
		}});
	}
}

function cancelReq(stockId) {
	if (confirm('Bạn muốn hủy yêu cầu hàng từ nhân viên  ?')) {
		// Accept new request
		var url = getAppRootPath()+"/order/cancel?stockId="+stockId;
		$.ajax({type: 'POST', dataType:'json', url: url, success: function(data) {
			if(data.status!='OK') {
				alert('Yêu cầu hàng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
			}
			else {
				//window.location.reload();
				window.location.href = getAppRootPath()+"/order/requestGoods/list";
			}
		}});
	}
}