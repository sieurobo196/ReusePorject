function getAgent() {
    getListProvince(0, function (success) {
        var list = getContentList(success);
        if (list != null) {
            renderComboBox('#combo_agent', list, '-- Vùng Miền --');
        } else {
            alert(JSON.stringify(list));
        }

    }, function (error) {
        alert(JSON.stringify(error));
    });
}
/**
 * 
 */
function getProvinceCity() {
    getListProvince(1, function (success) {
        var list = getContentList(success);
        if (list != null) {
            renderComboBox('#combo_province', list, '-- Tỉnh/Thành phố --');
        } else {
            alert(JSON.stringify(list));
        }

    }, function (error) {
        alert(JSON.stringify(error));
    });
}
/**
 * 
 */
function getDistrict() {
    getListProvince(2, function (success) {
        var list = getContentList(success);
        if (list != null) {
            renderComboBox('#combo_district', list, '-- Quận/Huyện --');
        } else {
            alert(JSON.stringify(list));
        }

    }, function (error) {
        alert(JSON.stringify(error));
    });
}
/**
 * Get List Ward By ParentId
 */
function getListWardByDistrictId(mDistrictId, successCallback, errorCallback) {
    var contents = new Object();
    contents.parentId = mDistrictId;

    requestToServer('POST', contextPath + '/ajax/location/getListWardDetails',
            contents, function (data) {
                success(data, successCallback);
            }, function (data) {
        error(data, errorCallback);
    });
}
/**
 * Get List Ward By Search Key
 */
function getListWardByKey(mParentId, key, successCallback, errorCallback) {
    var contents = new Object();
    var mContents = new Object();
    mContents.parentId = mParentId;
    mContents.searchText = key;
    var mPage = new Object();
    mPage.pageNo = 1;
    mPage.recordsInPage = 10;
    contents.contents = mContents;
    contents.page = mPage;
    requestToServer('POST', contextPath
            + '/ajax/location/getListWardDetailsBykey', contents,
            function (data) {
                success(data, successCallback);
            }, function (data) {
        error(data, errorCallback);
    });
}
/**
 * 
 * @param agentId
 * @param successCallback
 * @param errorCallback
 */
function getCbListUserByCity(agentId, successCallback, errorCallback) {
    var contents = new Object();
    contents.parentId = agentId;
    requestToServer('POST', contextPath
            + '/ajax/location/getListLocationByParentId', contents, function (
                    data) {
                success(data, successCallback);
            }, function (data) {
        error(data, errorCallback);
    });
}
/**
 * 
 */
function getListCity() {
    var locationId = $("#combo_agent option:selected").val();
    getCbListUserByCity(locationId, function (success) {
        var list = getContentList(success);
        if (list != null) {
            renderComboBox('#combo_province', list, '-- Tỉnh/Thành phố --');
        }

    }, function (error) {

    });
}
/**
 * 
 */
function getListDistrict() {
    var locationId = $("#combo_province option:selected").val();
    getCbListUserByCity(locationId, function (success) {
        var list = getContentList(success);
        if (list != null) {
            renderComboBox('#combo_district', list, '-- Quận/Huyện --');
        }

    }, function (error) {

    });
}
/**
 * 
 */
function getFormListWards() {
    $("#tbodey_list_location").empty();
    $('#locationForm').submit();
}
/**
 * 
 * @param mLocation
 */
function insertRow(mLocation) {

    var agentId = $("#combo_agent option:selected").val();
    var provinceId = $("#combo_province option:selected").val();
    var districtId = $("#combo_district option:selected").val();
    for (var i = 0; i < mLocation.length; i++) {
        var item = mLocation[i];
        var tr = document.createElement('tr');
        // CREATE TD OF TR IN TABLE
        // ID
        var tdId = document.createElement('td');
        tdId.setAttribute('align', 'center');
        tdId.appendChild(document.createTextNode(i + 1));
        // NAME
        var tdName = document.createElement('td');
        tdName.appendChild(document.createTextNode(item.name));
        // CODE
        var tdCode = document.createElement('td');
        tdCode.appendChild(document.createTextNode(item.code));
        // DISTRICT NAME
        var tdDistrictName = document.createElement('td');
        tdDistrictName.appendChild(document.createTextNode(item.parents.name));
        // DISTRICT NAME
        var tdCityName = document.createElement('td');
        tdCityName.appendChild(document
                .createTextNode(item.parents.parents.name));
        // CREATE LINK SHOW CHENNEL INFO
        var tdLink = document.createElement('td');
        tdLink.setAttribute('align', 'center');
        var link = document.createElement('a');
        link.setAttribute('href', contextPath
                + '/settings/location/wardFormEdit/' + agentId + '/'
                + provinceId + '/' + districtId + '/' + item.id);
        link.className = 'redTxt';
        link.appendChild(document.createTextNode('Chỉnh Sửa'));
        tdLink.appendChild(link);
        // ADD TD TO TR
        tr.appendChild(tdId);
        tr.appendChild(tdName);
        tr.appendChild(tdCode);
        tr.appendChild(tdDistrictName);
        tr.appendChild(tdCityName);
        tr.appendChild(tdLink);
        $('#tbodey_list_location').append(tr);
    }
}

/*******************************************************************************
 * Load Per View By Location Type
 */
function onLoadPerView() {
    var locationType = document.getElementById('location_type').value;
    if (locationType == 1) {
        document.getElementById('tr_combo_agent').style.display = 'none';
        document.getElementById('tr_input_province').style.display = '';
        document.getElementById('tr_combo_province').style.display = 'none';

        document.getElementById('tr_combo_province').style.display = 'none';
        document.getElementById('tr_input_district').style.display = 'none';
        document.getElementById('tr_combo_district').style.display = 'none';
        document.getElementById('tr_input_ward').style.display = 'none';
    } else if (locationType == 2) {
        document.getElementById('tr_input_province').style.display = 'none';
        document.getElementById('tr_combo_province').style.display = '';
        document.getElementById('tr_input_district').style.display = '';
        document.getElementById('tr_combo_district').style.display = 'none';
        document.getElementById('tr_input_ward').style.display = 'none';
        document.getElementById('tr_combo_agent').style.display = 'none';

    } else if (locationType == 3) {// == 3
        document.getElementById('tr_input_province').style.display = 'none';
        document.getElementById('tr_combo_province').style.display = '';
        document.getElementById('tr_input_district').style.display = 'none';
        document.getElementById('tr_combo_district').style.display = '';
        document.getElementById('tr_input_ward').style.display = '';
        document.getElementById('tr_combo_agent').style.display = 'none';
    }
    document.getElementById('code_name_error').innerHTML = null;
    document.getElementById('input_ward_name_error').innerHTML = null;
    document.getElementById('input_province_name_error').innerHTML = null;
    document.getElementById('input_district_name_error').innerHTML = null;
    document.getElementById('input_lat_error').innerHTML = null;
    document.getElementById('input_lng_error').innerHTML = null;

}

/**
 * Create Location
 */
function createLocation(code, name, parentId, locationType, note, lat, lng,
		createdUser, successCallback, errorCallback) {
	var contents = new Object();
	contents.code = code;
	contents.name = name;
	contents.parentId = parentId;
	contents.locationType = locationType;
	contents.note = note;
	contents.lat = lat;
	contents.lng = lng;
	contents.createdUser = createdUser;// Set Default for Test ...

	requestToServer('POST', contextPath + '/ajax/location/createLocation',
			contents, function(data) {
				success(data, successCallback);
			}, function(data) {
				error(data, errorCallback);
			});
}

function getDataLocation() {
    var locationType = document.getElementById('location_type').value;
    var parentId = 0;
    var name = '';

    if (locationType == 1) {
        document.getElementById('tr_input_province').style.display = '';
        name = document.getElementById('input_province_name').value;
        parentId = document.getElementById('combo_agent').value;
        if (name.trim().length == 0) {
            document.getElementById('input_province_name_error').innerHTML = "Chưa nhập tên tỉnh / TP";
        }

    } else if (locationType == 2) {
        document.getElementById('tr_combo_province').style.display = '';
        document.getElementById('tr_input_district').style.display = '';
        name = document.getElementById('input_district_name').value;
        parentId = document.getElementById('combo_province').value;
        if (name.trim().length == 0) {
            document.getElementById('input_district_name_error').innerHTML = "Chưa nhập tên Huyện /Quận";
        }
    } else if (locationType == 3) {// == 3
        document.getElementById('tr_combo_province').style.display = '';
        document.getElementById('tr_combo_district').style.display = '';
        parentId = document.getElementById('combo_district').value;
        document.getElementById('tr_input_ward').style.display = '';
        name = document.getElementById('input_ward_name').value;
        if (name.trim().length == 0) {
            document.getElementById('input_ward_name_error').innerHTML = "Chưa nhập tên Xã /Phường ";
        }
    }

    var code = document.getElementById('code_name').value;
    var lat = document.getElementById('lat').value;
    var lng = document.getElementById('lng').value;
    var note = document.getElementById('note').value;
    if (code.trim().length == 0) {
        document.getElementById('code_name_error').innerHTML = "Chưa nhập Mã viết tắt ";
    }
    if (lat.trim().length == 0) {
        document.getElementById('input_lat_error').innerHTML = "Chưa nhập Vĩ độ";
    }
    if (lng.trim().length == 0) {
        document.getElementById('input_lng_error').innerHTML = "Chưa nhập Kinh độ";
    }
    if (name.trim().length != 0 && code.trim().length != 0 && lat.trim().length != 0 && lng.trim().length != 0) {
        document.getElementById('code_name_error').innerHTML = null;
        document.getElementById('input_ward_name_error').innerHTML = null;
        document.getElementById('input_province_name_error').innerHTML = null;
        document.getElementById('input_district_name_error').innerHTML = null;
        createLocation(
                code,
                name,
                parseInt(parentId),
                parseInt(locationType),
                note,
                parseFloat(lat),
                parseFloat(lng),
                "1"/* createdUser */,
                function (success) {
                    var mCode = getCode(success);
                    if (mCode === 200) {
                        // Show Msg
                        document.getElementById('infoMessage').innerHTML = "Lưu thành công.";
                    } else if (mCode === 703) {
                        document.getElementById('infoMessage').innerHTML = "Tên Tỉnh /Thành phố đã tồn tại";
                    } else if (mCode === 704) {
                        document.getElementById('infoMessage').innerHTML = "Tên Quận /Huyện phố đã tồn tại";

                    }
                    else if (mCode === 705) {

                        document.getElementById('infoMessage').innerHTML = "Tên Phường /Xã đã tồn tại";
                    }
                    else if (mCode === 706) {

                        document.getElementById('infoMessage').innerHTML = "Mã Tỉnh /Thành phố đã tồn tại";
                    }
                    else if (mCode === 707) {

                        document.getElementById('infoMessage').innerHTML = "Mã Quận /Huyện đã tồn tại";
                    } else if (mCode === 708) {

                        document.getElementById('infoMessage').innerHTML = "Mã Phường / xã đã tồn tại";
                    }
                    else {
                        document.getElementById('infoMessage').innerHTML = "Xảy ra lỗi khi lưu dữ liệu.";
                    }
                },
                function (error) {
                    document.getElementById('infoMessage').innerHTML = "Xảy ra lỗi khi lưu dữ liệu.";

                });
    }
}
/**
 * 
 */
function setSelectValue(id, val) {
    var selectBox = document.getElementById(id);
    selectBox.value = val;
}
/**
 * End of file
 */
