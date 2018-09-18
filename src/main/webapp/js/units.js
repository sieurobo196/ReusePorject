/**
 * Check emptype filed
 * @param {type} field is filed you want to check
 * @param {type} message send message to filed
 */
function checkEmptys(field, message, messageId) {
    var el = $(field);
    if (el.val().length === 0) {
        if (messageId === null) {
            $('#a_messageError').html(message);
        } else {
            $(messageId).html(message);
        }
        el.focus();
        return true;
    }
    return false;
}

function checkSpaces(field, message, messageId) {
    var el = $(field).val();
//    var arrayEl = el.split(' ');
//    var isSpaces = true;
//    for(var i = 0; i < arrayEl.length; i++){
//    	if(arrayEl[i] !== ''){
//    		isSpaces = false;
//    		break;
//    	}
//    }
    
    if (el.trim().length === 0) {
        if (messageId === null) {
            $('#a_messageError').html(message);
        } else {
            $(messageId).html(message);
        }
        //el.focus();
        return true;
    }
    return false;
}

function validateForm() {
    var isOK = true;
    if (checkEmptys('#unit_code', 'Mã đơn vị là bắt buộc nhập.', '#unit_code_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#unit_code_error').html('');
    }
    
    if (checkSpaces('#unit_code', 'Mã đơn vị không được chứa toàn khoảng trắng.', '#unit_code_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#unit_code_error').html('');
    }
 
    if (checkEmptys('#unit_name', 'Tên đơn vị là bắt buộc nhập.', '#unit_name_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#unit_name_error').html('');
    }
    
    if (checkSpaces('#unit_name', 'Tên đơn vị không được chứa toàn khoảng trắng.', '#unit_name_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#unit_name_error').html('');
    }

    if (!checkUnit()) {
        isOK &= false;
        $('#unit_child_quantity_error').html('Vui lòng nhập số lượng.');
    } else {
        isOK &= true;
        $('#unit_child_quantity_error').html('');
    }

    return isOK;
}

function postForm() {
    if (validateForm() === 1) {
        $('#unitForm').submit();
    }
}

function deleteUnit(ids, unitName) {
    if (confirm('Bạn có thực sự muốn xóa đơn vị: [' + unitName + ']?')) {
        var data = new Object();
        requestToServer('POST', contextPath + '/ajax/unit/delete/' + ids, data,
                function (success) {
                    if (success == 200) {
                        alert('Đã xóa đơn vị [' + unitName + '] thành công.');
                        $('#unitListForm').submit();
                    } else {
                        alert('Không thể xóa [' + unitName + ']');
                    }
                },
                function (error) {
                    alert('Không thể xóa [' + unitName + ']');
                });
    }
}

function checkUnit() {
    var childUnit = $('#unit_child_unit option:selected').val();
    if (parseInt(childUnit) !== 0) {
        var childQuantity = $('#unit_child_quantity').val();
        if (childQuantity.length === 0) {
            return false;
        }
    }
    return true;
}

function removeText() {
    var childUnit = $('#unit_child_unit option:selected').val();
    if (parseInt(childUnit) === 0) {
        $('#unit_child_quantity').val('');
        $('#unit_child_quantity_error').html('');
    }
}
