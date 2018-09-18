
function addRegister(userId) {
    window.location = getAppRootPath() + "/user/add?registerId=" + userId;
}

function rejectUser(userId) {
    var flag = confirm("Bạn có muốn từ chối nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/register/reject?userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Người sử dụng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    $('#status' + userId).html('Thất Bại');
                }
            }
        });
    }
}

function lockUser(userId) {
    var isLock = $('#locked' + userId).attr("checked") == 'checked';
    var flag = confirm("Bạn có muốn " + (isLock ? "mở khóa" : "khóa") + " nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/lock?userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    if(data.status == "OVERSALE"){
                        alert("Vượt quá " +data.saleMax+ " nhân viên sale của Công ty đăng ký.");
                    }
                    else{
                        alert('Người sử dụng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                    }
                } else {
                    if (isLock)
                    {
                        $('#locked' + userId).attr("checked", false);
                    }
                    else
                    {
                         $('#locked' + userId).attr("checked", 'checked');
                    }
                    $('#lockedLink' + userId).html(isLock ? 'Khóa' : 'Mở Khóa');
                }
            }
        });
    }
}

function addNewUser() {
    window.location = getAppRootPath() + "/user/add"
}
function editUser(userId) {
    $('#editUser #userId').val(userId);
    $('#editUser').submit();
}
function resetPassword(userId) {
    var flag = confirm("Bạn có muốn đổi mật khẩu cho nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/resetPassword?userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Người sử dụng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    alert('Đã hủy mật khẩu cũ, mật khẩu mới của người sử dụng này là 123456')
                }
            }
        });
    }
}


function deleteFeedBack(fbId) {
    var flag = confirm("Bạn có muốn xóa nôi dung này?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/feedback/delete?fbId=" + fbId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Nội  không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    alert('Đã xóa thành công.');
                    location.reload();
                }
            }
        });
    }
}

function deleteVasAcc(userId) {
    var deleteDate = $('#deleteDate').val();
    var userVasAccount = $('#userVasAccount').val();

    var flag = true;//confirm("Bạn có muốn xóa tài khỏan VAS của nhân viên?" );
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/deleteVas?vasAcc=" + userVasAccount + "&userId=" + userId + "&deleteDate=" + deleteDate;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                tb_remove();
                if (data.status != 'OK') {
                    if (data.status != 'Not OK') {
                        alert('Tài khỏan VAS không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                    } else {
                        alert('Tài khỏan VAS đã có giao dịch vào ngày bạn chọn.');
                    }

                } else {
                    alert('Đã hủy tài khỏan VAS thành công');
                    location.reload();
                }
            }
        });
    }

}

function deleteEVoucherAcc(userId) {
    var flag = confirm("Bạn có muốn xóa tài khỏan eVoucher của nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/deleteEVoucher?userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Tài khỏan VAS không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    alert('Đã hủy tài khỏan eVoucher thành công');
                    location.reload();
                }
            }
        });
    }
}

function updateDefaultVasAcc(vasAcc, userId) {
    var flag = confirm("Bạn có muốn sét tài khỏan VAS này mặc định cho nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/updateDefaultVasAcc?vasAcc=" + vasAcc + "&userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Tài khỏan VAS không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    alert('Đã cập nhật thành công');
                    location.reload();
                }
            }
        });
    }
}

function resetPasswordTT(userId) {
    var flag = confirm("Bạn có muốn đổi mật khẩu thanh toán cho nhân viên?");
    if (flag == true)
    {
        var url = getAppRootPath() + "/user/resetPasswordTT?userId=" + userId;
        $.ajax({
            type: 'POST',
            dataType: 'json',
            url: url,
            success: function (data) {
                if (data.status != 'OK') {
                    alert('Người sử dụng không tồn tại hoặc có lỗi truy cập cơ sở dữ liệu!');
                } else {
                    alert('Đã hủy mật khẩu cũ, mật khẩu mới của người sử dụng này là 123456')
                }
            }
        });
    }
}

function channelCommission(vas_name, vas_phone, vas_id, user_name, user_id) {
    var valid = true;

    var flag = confirm("Bạn có thêm thông tin tài khoản VAS:" + vas_name + "  số điện thoại: " + vas_phone + " cho nhân viên" + user_name);
    if (flag == true)
    {
        $('#frmCommission').submit();
    }


}

//doi option duoc select
function reSelect($domCbo, value) {
    if ($domCbo.find('option').length > 1) {
        // chi chon lai neu danh sach co tu 2 option tro len
        $domCbo.find('option:selected').removeAttr('selected');
        $domCbo.find("option[value='" + value + "']").attr('selected',
                'selected');
    }
}
function changeAgent(branch, agent) {
    reSelect(branch, 0);
}
function changeBranch(branch, agent) {
    reSelect(agent, 0);
}
function changeAgent1(branch, agent, dummy) {
    reSelect(branch, 0);
    dummy.click();
}
function changeBranch1(branch, agent, dummy) {
    reSelect(agent, 0);
    dummy.click();
}

function openDaiLyChoose() {
//	if ($('#goods').val() == 0) {
//		alert("Chọn hàng hóa xuất!")
//		return;
//	}
//	$('#s_goods').val($('#goods').val());
//	$("#searchTable").html('');
//	$("#selectedTable").html('');
//	$("#total").text('0');
    tb_show('Chọn Đại Lý', 'TB_inline?height=450&amp;width=900&amp;inlineId=daiLyDialog');
}