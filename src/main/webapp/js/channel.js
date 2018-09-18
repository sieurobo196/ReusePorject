function initLink() {
    var path = $('#td_level_path');
    path.empty();
    var companyLink = $('<a tag="#select_channel_' + channelTypeArray[0] + '" onclick="changComboFromA(this)" href="javascript:;"><strong>' + companyName + '» </strong></a>');
    path.append(companyLink);
    var lent = channelTypeArray.length;
    for (var i = 0; i < lent - 1; i++) {
        var comSelected = $('#select_channel_' + channelTypeArray[i]);
        if (comSelected.val() != '0' && comSelected.val() != null) {
            var text = comSelected.find(':selected').text();
            if (text.indexOf('-') > 0) {
                var texts = text.split('-');
                text = texts[1];
            }
            path.append($('<a combo="#select_channel_' + channelTypeArray[i] + '" tag="' + comSelected.find(':selected').val() + '" onclick="linkAChange(this)" href="javascript:;"><strong>' + text + '» </strong></a>'));
        }
    }
}

function removeAllContentCombo(combo) {
    var idString = $(combo).attr('id').split('_');
    var idchanIdSelected = idString[idString.length - 1];
    var index = channelTypeArray.indexOf(parseInt(idchanIdSelected));
    selectChannelType = 'select_channel_' + channelTypeArray[index + 1];
    if (index + 1 !== channelTypeArray.length - 1) {
        for (var i = index + 1; i < channelTypeArray.length; i++) {
            $('#select_channel_' + channelTypeArray[i]).find('option')
                    .remove()
                    .end();
        }
    }
}

function comboChanCompany(comboChannel) {
    var combo = $(comboChannel);
    var tag = parseInt(combo.attr('tag'));
    if (tag !== channelTypeArray[channelTypeArray.length - 1]) {
        var comboval = combo.val();
        channelSelectedID = parseInt(comboval);
        if (channelSelectedID > 0) {
            var tagA = $('<a href="javascript:;" tag="' + channelSelectedID + '" ></a>');
            getLinkAndMemberChannel(tagA);
        } else {
            var type = combo.attr('id').split('_');
            if (parseInt(type[2]) != channelTypeArray[0]) {
                for (var i = channelTypeArray.length - 1; i >= 0; i--) {
                    var id = $('#select_channel_' + channelTypeArray[i] + ' option:selected').val();
                    if (!isNaN(id) && parseInt(id) !== 0) {
                        var tagA = $('<a href="javascript:;" tag="' + parseInt(id) + '" chanType="' + parseInt(type[2]) + '" ></a>');
                        getAllChannelType(tagA);
                        break;
                    }
                }
            }
            else {
                var tagA = $('<a href="javascript:;" tag="0" chanType="' + parseInt(type[2]) + '" ></a>');
                getAllChannelType(tagA);
            }
        }
    }
}

function linkAChange(linka) {
    var tagA = $(linka);
    var chanId = parseInt(tagA.attr('tag'));
    $(tagA.attr('combo')).val(chanId).change();
}

function checkdDeleteAll() {
    var tbody = $('#table_channel_location').children('tr');
    var lent = tbody.length;
    if (lent === 0) {
        return true;
    } else {
        for (var i = 0; i < lent; i++) {
            var tr = tbody[i];
            var dispaly = $(tr).css('display');
            if (dispaly !== 'none') {
                return false;
            }
        }
    }
    return true;
}

function checkNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function initData(index) {
  if(index === 1) {
    var tableProvince = $('#table_channel_province');
    for (var i = 0; i < channelLocationList.length; i++) {
      var tr = '<tr id="tr_${province.id}_channel_province">';
      tr += '<td align="left">';
      tr += '<input type="hidden" id="proList_${status.index}_createdUser" name="locationList[${status.index}].createdUser" value="${channelLocal.createdUser}" />';
      tr +=  '<label><input id="proList_${status.index}_locations_id" type="checkbox" name="locationList[${status.index}].locations.id" value="${channelLocal.locations.id}"/>${channelLocal.locations.name}</label>';
      tr += '</td>';
      tr += '</tr>';
    }
  }
}

function checkBoxChange(checkbox) {
    $(checkbox).change(function () {
        var com = this.getAttribute('id').split('_');
        if (this.checked) {
//            console.log('#proList_' + parseInt(com[1]) + '_createdUser');
            $('#proList_' + parseInt(com[1]) + '_createdUser').attr('value', -1);
        } else {
            $('#proList_' + parseInt(com[1]) + '_createdUser').attr('value', 1);
        }
    });
}
function changeLocation() {
    var provinceId = $("#location_type option:selected").val();
    if (provinceId == 1) {
        $('#location_type_district').hide();
        $('#location_type_province').show();
        $('#table_channel_location').empty();
        $('#input_is_province').attr('value', 1);
    } else if (provinceId == 2) {
        $('#location_type_district').show();
        $('#location_type_province').hide();
        $('#input_is_province').attr('value', 0);
        $('#table_channel_location').empty();
    }
}
function deleteGoodsFocus(locationRow) {
    var row = $(locationRow);

    var combo = locationRow.split('_');
    var lent = combo.length;
    if (lent === 5) {
        var created = $('#input_' + parseInt(combo[1]) + '_createdUser');
        if (parseInt(created.val()) > 0) {
            row.toggle();
            $('#input_' + parseInt(combo[1]) + '_deletedUser').attr("value", 1);
        } else {
            row.remove();
        }
        var comboboxGoods = $('#combo_goods_channel_focus');
        var goodsId = $('#input_' + parseInt(combo[1]) + '_goodss_id').val();
        var name = row.children('td').first().text().trim();
        comboboxGoods.append($('<option value="' + goodsId + '">' + name + '</option>'));
        comboboxGoods.select2();
    }
}

function checkDuplicate() {
    var fullCode = $('#full_code_parent').val() + '_' + $('#channel_code').val();
    var contents = new Object();
    contents.fullCode = fullCode;
    requestToServer('POST', contextPath + '/ajax/channel/checkDuplicate', contents,
            function (data) {
                if (data.duplicated === true) {
                    $('#a_messageError').html('Mã kênh <b>[' + fullCode + ']</b> đã tồn tại.');
                } else {
                    $('#channel_form').submit();
                }
            },
            function (error) {
                
            });
    //
}

function deleteLocation(locationRow) {
    var row = $(locationRow);

    var combo = locationRow.split('_');
    var lent = combo.length;
    if (lent === 4) {

        var created = $('#input_' + parseInt(combo[1]) + '_chnallo_createdUser');
        if (parseInt(created.val()) > 0) {
            row.toggle();
            $('#input_' + parseInt(combo[1]) + '_chnallo_deletedUser').attr("value", 1);
        } else {
            row.remove();
        }
        var proName = $('#channel_province');
        var chan = '#input_' + parseInt(combo[1]) + '_chnallo_location_id';
        var localId = $(chan).val();
        var name = row.children('td').first().text().trim();
        proName.append($('<option value="' + localId + '">' + name + '</option>'));
        proName.select2();
    }
}

function addLocation(locationCombo, message) {
    
    var local = $(locationCombo);
    var tableLent = $('#table_channel_location').children('tr').length;
    if (local.val() == 0) {
        alert(message);
        return;
    }
    $('#channel_location_error').html('');
    var id = local.val();
    var op = local.find(":selected");
    var name = op.text();
    var tr = '<tr id="tr_' + tableLent + '_channel_location">';
    tr += '<td align="left">';
    tr += name;
    tr += '<input type="hidden" id="input_' + tableLent + '_chnallo_id" name="locationList[' + (tableLent) + '].id"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_chnallo_locations_name" name="locationList[' + (tableLent) + '].locations.name' + '" value="' + name + '"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_chnallo_locations_id" name="locationList[' + (tableLent) + '].locations.id' + '" value="' + id + '"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_chnallo_createdUser" name="locationList[' + (tableLent) + '].createdUser' + '" value="0"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_chnallo_deletedUser" name="locationList[' + (tableLent) + '].deletedUser' + '" value="0"/>';
    tr += '</td>';
    tr += '<td align="center">';
    tr += "<a href=\"javascript:deleteLocation('#tr_" + tableLent + "_channel_location');\">Xóa</a>";
    tr += '</td>';
    tr += '</tr>';
    $('#table_channel_location').append($(tr));
    op.remove();
    local.select2();
}

function addGoodsFocus(goodsCombo, message) {
    var goods = $(goodsCombo);
    var tableLent = $('#table_' + goods.attr('id')).children('tr').length;
    if (goods.val() == 0) {
        alert(message);
        return;
    }
    var id = goods.val();
    var op = goods.find(":selected");
    var name = op.text();
    var tr = '<tr id="tr_' + tableLent + '_goods_channel_focus">';
    tr += '<td align="left">';
    tr += name;
    tr += '<input type="hidden" id="input_' + tableLent + '_id" name="goodsChannelFocusList[' + (tableLent) + '].id' + '"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_goods.name" name="goodsChannelFocusList[' + (tableLent) + '].goodss.name' + '" value="' + name + '"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_goods.id" name="goodsChannelFocusList[' + (tableLent) + '].goodss.id' + '" value="' + id + '"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_createdUser" name="goodsChannelFocusList[' + (tableLent) + '].createdUser' + '" value="0"/>';
    tr += '<input type="hidden" id="input_' + tableLent + '_deletedUser" name="goodsChannelFocusList[' + (tableLent) + '].deletedUser' + '" value="0"/>';
    tr += '</td>';
    tr += '<td align="center">';
    tr += "<a href=\"javascript:deleteGoodsFocus('#tr_" + tableLent + "_goods_channel_focus');\">Xóa</a>";
    tr += '</td>';
    tr += '</tr>';
    $('#table_combo_goods_channel_focus').append($(tr));
    op.remove();
    goods.select2();

}

function getLocationName(tablename) {
    var locationList;
    if (tablename === 'channel_province') {
        locationList = 'proList';
    } else if (tablename === 'channel_district') {
        locationList = 'districtList';
    } else if (tablename === 'channel_ward') {
        locationList = 'warList';
    }

    return locationList;
}

/**
 * Gen user name from full name and plus company name.
 * @param {type} fullName is full name
 * @returns {undefined} is username gen auto follow fullname
 */
function generUsername(fullName) {
    var username = removeAccent(fullName);
    var res = username.split(' ');
    var genUserName = "";
    if (res.length > 0) {
        for (var i = 0; i < res.length - 1; i++) {
            genUserName += res[i].substring(0, 1);
        }
        genUserName += res[res.length - 1];
    } else {
        genUserName = username;
    }
    $('#u_maDangNhap').val(genUserName + '@itt');
}

/*
 * Remove accent vietnamese
 */
function removeAccent(obj) {
    var str;
    if (eval(obj))
        str = eval(obj).value;
    else
        str = obj;
    str = str.toLowerCase().trim();
    str = str.replace(/à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ/g, "a");
    str = str.replace(/è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ/g, "e");
    str = str.replace(/ì|í|ị|ỉ|ĩ/g, "i");
    str = str.replace(/ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ/g, "o");
    str = str.replace(/ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ/g, "u");
    str = str.replace(/ỳ|ý|ỵ|ỷ|ỹ/g, "y");
    str = str.replace(/đ/g, "d");
    return str;
}

/**
 * Create combobox channel when click channel parent
 * @param {type} items is list channel get from table
 *
 */
function getComboBox(channelList) {
    $('#divCombobox').empty();
    $('#body_channel_list').empty();
}

function getLinkAndMemberChannel(tagChannel) {
    getLink(channelItems, tagChannel);
    getListChannelByParent(tagChannel);
}

function getAllChannelType(tagChannel) {
    getLink(channelItems, tagChannel);
    getListChannelByType(tagChannel);
}

function getListChannelByParent(channelId) {
    showLoading();
    var parent = parseInt($(channelId).attr('tag'));
    if (isNaN(parent)) {
        channelSelectedID = channelId;
    } else {
        channelSelectedID = parent;
    }
    resetCombo();
    getListChannelTypeByParentId(channelSelectedID,
        function (data) {
            var items = getContentList(data);
            if (items != null && items.length > 0) {
                channelItems = items;
                insertRow(items);
            } else {
                channelEmply();
                }
            hideLoading();
        },
        function (error) {
                channelEmply();
                hideLoading();
        });
}

function getListChannelByType(channelType) {
    showLoading();
    var ids = parseInt($(channelType).attr('chanType'));
    var chanId = parseInt($(channelType).attr('tag'));
    resetCombo();
    getListChannelByTypeId(chanId, ids,
        function (data) {
            var items = getContentList(data);
            if (items != null && items.length > 0) {
                channelItems = items;
                insertRow(items);
            } else {
                channelEmply();
                }
                hideLoading();
        },
        function (error) {
                channelEmply();
                hideLoading();
        });
}

function resetCombo() {
    var comboChanelType;
    if (comboVal !== 0) {
        comboChanelType = $('#' + selectChannelType);
    } else {
        comboChanelType = $(selectChannelTypeCurrent);
    }
    var idString = comboChanelType.attr('id').split('_');
    var idchanIdSelected = idString[idString.length - 1];
    if (channelTypeArray[0] !== parseInt(idchanIdSelected)) {
        comboChanelType.empty();
    }
    var index = channelTypeArray.indexOf(parseInt(idchanIdSelected));
    if (index !== channelTypeArray.length - 1) {
        for (var i = index; i < channelTypeArray.length; i++) {
            var op = $('#select_channel_' + channelTypeArray[i]);
            op.append($("<option></option>")
                    .attr("value", 0)
                    .text(jsonChannelType[i].name));
            op.val(0);
            op.select2();

        }
    }
}

function insertRow(listChannel) {
    var comboChanelType;
    if (comboVal !== 0) {
        comboChanelType = $('#' + selectChannelType);
    } else {
        comboChanelType = $(selectChannelTypeCurrent);
    }
    for (var i = 0; i < listChannel.length; i++) {
        var channel = listChannel[i];
        // insert member to combo
        if (channelSelectedID !== 0) {
            comboChanelType.append($("<option></option>")
                    .attr("value", channel.id)
                    .text('[' + channel.fullCode + ']' + '-' + channel.name));

        }
        // create td of table
        var tr = document.createElement('tr');
        // create td of tr in table
        var td = document.createElement('td');
        // add number serial
        td.appendChild(document.createTextNode(i + 1));
        // add td to tr
        tr.appendChild(td);
        // create short name
        td = document.createElement('td');
        var aHref = document.createElement('a');
//        aHref.setAttribute('tag', channel.id);
//        aHref.setAttribute("onclick", 'getLinkAndMemberChannel(this)');
        aHref.setAttribute('href', editPath + channel.id);
        var textlink = document.createTextNode(channel.fullCode);
        aHref.appendChild(textlink);
        td.appendChild(aHref);
        // add td to tr
        tr.appendChild(td);
        // create input hiden set channel id
        var ccID = document.createElement('input');
        ccID.setAttribute('type', 'hidden');
        ccID.setAttribute('value', channel.id);
        tr.appendChild(ccID);
        // create phone
        td = document.createElement('td');
        aHref = document.createElement('a');
//        aHref.setAttribute('tag', channel.id);
//        aHref.setAttribute("onclick", 'getLinkAndMemberChannel(this))');
        aHref.setAttribute('href', editPath + channel.id);
        textlink = document.createTextNode(channel.name);
        aHref.appendChild(textlink);
        td.appendChild(aHref);
        // add td to tr
        tr.appendChild(td);
        // create channel tel
        td = document.createElement('td');
        var tel = "";
        if (typeof channel.tel !== 'undefined') {
            tel = channel.tel;
        }
        td.appendChild(document.createTextNode(tel));
        // add td to tr
        tr.appendChild(td);
        // create channel tyle
        td = document.createElement('td');
        var channelType = channel.channelTypes;
        td.appendChild(document.createTextNode(channelType.name));
        // add td to tr
        tr.appendChild(td);
        // create action col
        td = document.createElement('td');
        td.setAttribute('align', 'center');
        // create link show chennel info
        var link = document.createElement('a');
        link.setAttribute('tag', channel.id);
        if(comboChanelType.attr('id') !== 'select_channel_'+channelTypeArray[channelTypeArray.length - 1]) {
          link.setAttribute("onclick", 'selectItemCombo(' + channel.id + ', \''+comboChanelType.attr('id')+'\')');
          link.setAttribute('href', 'javascript:;');
          link.className = 'redTxt';
          link.appendChild(document.createTextNode('Xem kênh'));
            td.appendChild(link);
            
        }
        if (!isLink && comboChanelType.attr('id') !== 'select_channel_' + channelTypeArray[channelTypeArray.length - 1]) {
            td.appendChild(document.createTextNode(' | '));
        }
        if (!isLink) {
            // crate link delete
            link = document.createElement('a');
            link.setAttribute("onclick", 'javascript:deleteChannel(' + channel.id + ', "' + channel.name + '")');
            link.setAttribute('href', 'javascript:;');
            link.className = 'redTxt';
            link.appendChild(document.createTextNode('Xóa'));
            td.appendChild(link);
        }
        // add td to tr
        tr.appendChild(td);
        $('#body_channel_list').append(tr);
    }
}

function changComboFromA(tagA) {
    var combo = $(tagA).attr('tag');
    $(combo).val(0).change();
//    $(combo).select2();
}

function selectItemCombo(itemcombo, comboid) {
        $('#' + comboid).val(itemcombo).change();
        $('#' + comboid).select2();
//    var cha = '#select_channel_' + channelTypeArray[0];
//    if (cha === selectChannelTypeCurrent) {
//        $(selectChannelTypeCurrent).val(itemcombo).change();
//        $(selectChannelTypeCurrent).select2();
//    } else {
//        $('#' + selectChannelType).val(itemcombo).change();
//        $('#' + selectChannelType).select2();
//    }
}

function getLink(listChannel, tagA) {
    $('#divCombobox').empty();
    channelSelectedID = parseInt($(tagA).attr('tag'));
    channelSelectedCode = $(tagA).attr('code');
    getAllMember(listChannel);
}

function getAllMember(items) {
// render combobox
    getComboBox(items);
}
/**
 * Create row messge if get list channel is empty
 */
function channelEmply() {
// create td of table
    var tr = document.createElement('tr');
    // create td of tr in table
    var td = document.createElement('td');
    // set text to td
    td.appendChild(document.createTextNode("Không tồn tại thành viên"));
    // set colspan to td
    td.setAttribute('colSpan', '7');
    // set align text in td is center
    td.setAttribute('align', 'center');
    // add td to tr
    tr.appendChild(td);
    // add tr to tbbody
    $('#body_channel_list').append(tr);
}

function validateForm() {
    var isOK = true;
    if (checkEmptys('#channel_name', 'Tên kênh bắt buộc nhập', '#channel_name_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#channel_name_error').html('');
    }
    if (checkEmptys('#channel_code', 'Mã kênh bắt buộc nhập.', '#channel_code_error')) {
        isOK &= false;
    } else {
        isOK &= true;
        $('#channel_code_error').html('');
    }
    if (checkEmptys('#channel_lat', 'Kinh độ bắt buộc nhập.', '#channel_lat_error')) {
        isOK &= false;
    } else {
        var lagVal = $('#channel_lat').val();
        if (!checkNumber(lagVal)) {
            isOK &= false;
            $('#channel_lat_error').html('Kinh độ không hợp lệ.');
        } else {
            isOK &= true;
            $('#channel_lat_error').html('');
        }
    }
    if (checkEmptys('#channel_lag', 'Vĩ độ bắt buộc nhập.', '#channel_lag_error')) {
        isOK &= false;
    } else {
        var lagVal = $('#channel_lag').val();
        if (!checkNumber(lagVal)) {
            isOK &= false;
            $('#channel_lag_error').html('Vĩ độ không hợp lệ.');
        } else {
            isOK &= true;
            $('#channel_lag_error').html('');
        }
    }

    if (checkdDeleteAll()) {
        isOK &= false;
        $('#channel_location_error').html('Vui lòng chọn địa điểm của kênh.');
    } else {
        isOK &= true;
        $('#channel_location_error').html('');
    }

    var status = $("#channel_status option:selected").val();
    if (parseInt(status) === 0) {
        isOK &= false;
        $('#channel_status_error').html('Vui lòng chọn trạng thái');
    } else {
        isOK &= true;
        $('#channel_status_error').html('');
    }

    var email = $("#channel_email").val();
    if (isValidEmailAddress(email) || email.trim().length === 0) {
        isOK &= true;
        $('#channel_status_error').html('');
    } else {
        isOK &= false;
        $('#channel_email_error').html('Email không hợp lệ');
    }

    var phoneNUmber = $('#channel_tel').val();
    if (phoneNUmber.trim().length === 0 || validatePhoneAndFax(phoneNUmber)) {
        isOK &= true;
        $('#channel_tel_error').html('');
    } else {
        isOK &= false;
        $('#channel_tel_error').html('Số điện thoại không hợp lệ');
    }

    var faxNmber = $('#channel_fax').val();
    if (faxNmber.trim().length === 0 || validatePhoneAndFax(faxNmber)) {
        isOK &= true;
        $('#channel_fax_error').html('');
    } else {
        isOK &= false;
        $('#channel_fax_error').html('Số Fax không hợp lệ');
    }

    return isOK;
}
function create() {
    var sequenceChan = "";
    for (var i = channelTypeArray.length - 1; i >= 0; i--)
    {
        var com = $('#select_channel_' + channelTypeArray[i]);
        if (com.val() != 0 && isNumber(com.val())) {
            sequenceChan += parseInt(com.val()) + '_';
        }
    }
    if (sequenceChan.length > 0) {
        sequenceChan = sequenceChan.substr(0, sequenceChan.length - 1);
    } else {
        sequenceChan = 0;
    }
    window.location.assign(contextPath + '/channel/create?parents=' + sequenceChan);
    
}

function createChannel() {
    if (validateForm() === 1) {
        checkDuplicate();
    }
}

function updateChannel() {
    if (validateForm() === 1) {
        $('#channel_form').submit();
    }
}
/**
 *
 * crate new channel
 */
function addNewChannel(data) {
    requestToServer('POST', contextPath + '/ajax/channel/createChannel', data,
            function (success) {
                var code = getCode(success);
                if (code === 200) {
                    $('#body_channel_list').empty();
                    // refresh list
                    getListChannelByParent(channelSelectedID);
                    // reset data in popup
                    // close popup
                    tb_remove();
                }
            }, function (error) {
    });
}

function updateChannel() {
    if (validateForm() == 1) {
        $("#channel_form").submit();
    }
}

function deleteChannel(channelId, channelname) {
  if (confirm('Bạn có thực sự muốn xóa kênh: ' + channelname +'?')) {
    var data = new Object();
    data.id = channelId;
    requestToServer('POST', contextPath + '/ajax/channel/delete', data,
            function (successData) {
                    var item = getContent(successData);
                    if (getCode(successData) == 200) {
                        alert('Xóa kênh thành công.');
                        var tagA = $('<a href="javascript:;" tag="' + channelSelectedID + '" onclick="getLinkAndMemberChannel(this)" class="redTxt" ></a>');
                        getLinkAndMemberChannel(tagA);
                    } else if (getCode(successData) == 406) {
                        alert('Không thể xoá vì có dữ liệu phụ thuộc.');
                    } else {
                        alert('Quá trình xóa kênh xãy ra lỗi. Xin vui lòng thử lại.');
                    }
            },
            function (error) {
              alert('Quá trình xóa kênh xãy ra lỗi. Xin vui lòng thử lại.');
            });
  }
}


function getChannelType(channelTypeId) {
    getListChannelTypeByParent(channelTypeId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#channel_type', list, '-- Chọn kênh --');
                }
            },
            function (error) {

            });
}

function clickCompanyLink() {
    var tagA = $('<a href="javascript:;" tag="0" chanType="1" onclick="getLinkAndMemberChannel(this)" class="redTxt" ></a>');
    getAllChannelType(tagA);
}

function levelPathClick(id) {
    resetCombo();
    selectItemCombo(id);
}

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

function getProvince() {
    getListProvince(1,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#channel_province', list, '-- Tỉnh/Thành phố --');
                }

            }, function (error) {
    });
}

function getDistrict(combo) {
    var provinceId = $(combo).val();
    getListDistrict(provinceId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#channel_district', list, '-- Quận/Huyện --');
                }

            }, function (error) {

    });
}

function getWard() {
    var provinceId = $("#channel_district option:selected").val();
    getListDistrict(provinceId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#channel_ward', list, '-- Phường/Xã --');
                }

            }, function (error) {

    });
}

function getChannelStatus() {
    getListStatus(5, // 5 is status for channel
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#channel_status', list, '-- Trạng thái --');
                }

            }, function (error) {
    });
}

function checkGPS(gps) {
    var reges = new RegExp(/^\d{0,2}(\.\d{0,2}){0,1}$/);
    return reges.test(gps);
}

function validatePhoneAndFax(phoneOrFax) {
    var pattern = new RegExp(/(^(\+\d{0,2}\s)?\(?\d{3}\)?\d{3}\d{4}$)|(^(\+\d{0,2}\s)?\(?\d{3}\)?\d{4}\d{4}$)/);
    return pattern.test(phoneOrFax);
}

function isValidEmailAddress(emailAddress) {
    var pattern = new RegExp(/^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i);
    return pattern.test(emailAddress);
}
;