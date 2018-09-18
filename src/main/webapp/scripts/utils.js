/**
 * Parse HTML and find the element by id
 */
function parseHTML(html, idStr) {
	var root = document.createElement("div");
	root.innerHTML = html;
	return findElement(root, idStr);
}

/**
 * Find child element
 */
function findElement(parent, elementId) {
	var allChilds = parent.childNodes;
	for ( var i = 0; i < allChilds.length; i++) {
		var child = allChilds[i];
		if (child.id == elementId) {
			return child;
		} else {
			var temp = findElement(child, elementId);
			if (temp != null) {
				return temp;
			}
		}
	}
	return null;
}

/**
 * Submit ajax using get method
 */
function submitAjaxGet(url, resultId, listElements) {
	url = createUrlByUrlAndElement(url, listElements);
	// Submit ajax
	$.ajax({
		type : "GET",
		url : url,
		dataType : "html",
		// Action handle
		success : function(data) {
			fetchDataToElement(resultId, data);
		}
	});
}

/**
 * Submit ajax and refresh list element
 */
function submitAjaxGetReplaceListElement(url, listelementId, listElements, scrollElementId) {
	url = createUrlByUrlAndElement(url, listElements);
	// Submit ajax
	$.ajax({
		type : "GET",
		url : url,
		dataType : "html",
		// Success handle
		success : function(data) {
			// Refresh elements
			for ( var int = 0; int < listelementId.length; int++) {
				var elementId = listelementId[int];
				fetchDataToElement(elementId, data);
			}
			// Scroll
			if (scrollElementId != undefined) {
				$('html, body').animate({
					scrollTop : $(scrollElementId).offset().top - 10
				}, 0);
			}
		}
	});
}

/**
 * Submit ajax and refresh list element
 */
function submitAjaxPostReplaceListElement(url, listelementId, listElements, scrollElementId) {
	showLoading();
	var data = {};
	for ( var i = 0; i < listElements.length; i++) {
		var element = listElements[i];
		data[element.name] = element.value;
	}
	// Submit ajax
	$.ajax({
		type : "POST",
		url : url,
		data : data,
		// Success handle
		success : function(data) {
			// Refresh elements
			for ( var int = 0; int < listelementId.length; int++) {
				var elementId = listelementId[int];
				fetchDataToElement(elementId, data);
			}
			// Scroll
			if (scrollElementId != undefined) {
				$('html, body').animate({
					scrollTop : $(scrollElementId).offset().top - 10
				}, 0);
			}
			hideLoading();
		}
	});
}

/**
 * Add element data to url
 */
function createUrlByUrlAndElement(url, listElements) {
	for ( var i = 0; i < listElements.length; i++) {
		var element = listElements[i];
		if (element != null && element != undefined) {
			if (url.indexOf("?") == -1) {
				url = url + "?";
			} else {
				url = url + "&";
			}
			url = url + element.name + "=" + element.value;
		}
	}
	return url;
}

/**
 * Fetch loaded html data to element content by id
 */
function fetchDataToElement(elementId, data) {
	var gotcha = parseHTML(data, elementId);
	if (gotcha == undefined) {
		$('#' + elementId).html("");
	} else {
		$('#' + elementId).html(gotcha.innerHTML);
	}
}

/**
 * Reload Google Map
 */
function reloadMap(latitude, longitude, mapType, zoom) {
	var mapTypeId = google.maps.MapTypeId.ROADMAP;
	if (mapType == 'SATELLITE') {
		mapTypeId = google.maps.MapTypeId.SATELLITE;
	} else if (mapType == 'HYBRID') {
		mapTypeId = google.maps.MapTypeId.HYBRID;
	} else if (mapType == 'TERRAIN') {
		mapTypeId = google.maps.MapTypeId.TERRAIN;
	}
	var mapOptions = {
		zoom : parseInt(zoom),
		zoomControl: true,
		center : new google.maps.LatLng(latitude, longitude),
		mapTypeId : mapTypeId,
		disableDefaultUI : true,
		scrollwheel : false,
		disableDoubleClickZoom : true,
	};
	var map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
	createOverlay(map);
}

/**
 * Submit Google Map option
 */
function submitMapOption() {
	var latitude = document.getElementById('latitude').value;
	var longitude = document.getElementById('longitude').value;
	var zoom = document.getElementById('zoom').value;
	var mapType = document.getElementById('mapType').value;
	reloadMap(latitude, longitude, mapType, zoom);
}

/**
 * Table's checkbox listener
 */
$(document).ready(function() {
	// Select all checkbox listener
	var selectAllCheckBox = $('.select_all_checkbox');
	selectAllCheckBox.click(function() {
		var table = selectAllCheckBox.parent().parent().parent().parent();
		var arrayCheckBox = table.find('td > .select_checkbox');
		var checked = selectAllCheckBox.is(':checked');
		for ( var int = 0; int < arrayCheckBox.length; int++) {
			jQuery(arrayCheckBox[int]).attr('checked', checked);
		}
	});
	// Select checkbox listener
	var selectCheckBox = $('.select_checkbox');
	selectCheckBox.click(function() {
		var table = selectAllCheckBox.parent().parent().parent().parent();
		var selectAll = table.find('th > .select_all_checkbox');
		var arrayCheckBox = table.find('td > .select_checkbox');
		var checked = true;
		for ( var int = 0; int < arrayCheckBox.length; int++) {
			checked = checked && jQuery(arrayCheckBox[int]).attr('checked');
		}
		if (checked == undefined) {
			jQuery(selectAll).attr('checked', false);
		} else {
			jQuery(selectAll).attr('checked', checked);
		}
	});
	/*$('a[href^="/'+location.pathname.split('/')[1]+'/"]').click(function (){showLoading();});
	if (typeof(hideLoading) != "undefined")
		hideLoading();*/
});

/**
 * Paging click
 */
function getListElementArrays(listElement) {
	var elementReplacedArray = getListElementIdArrays(listElement);
	var result = new Array();
	for ( var i = 0; i < elementReplacedArray.length; i++) {
		var elementId = elementReplacedArray[i];
		result[i] = document.getElementById(elementId);
	}
	return result;
}

/**
 * Paging click
 */
function getListElementIdArrays(listElement) {
	var elementReplacedArray = listElement.split(',');
	var result = new Array();
	for ( var i = 0; i < elementReplacedArray.length; i++) {
		var elementId = elementReplacedArray[i];
		result[i] = jQuery.trim(elementId);
	}
	return result;
}

/**
 * Paging size select
 */
function pagingSizeSelected(select, listRefreshElement, sizeParamName, sizeSelectedLink, listAdditionElement, method) {
	var selectedValue = select.options[select.selectedIndex].value;
	var submitLink = sizeSelectedLink + "&" + sizeParamName + "=" + selectedValue;
	if (method == 'Get') {
		submitAjaxGetReplaceListElement(submitLink, getListElementIdArrays(listRefreshElement), listAdditionElement);
	} else {
		submitAjaxPostReplaceListElement(submitLink, getListElementIdArrays(listRefreshElement), listAdditionElement);
	}
}

function openPopup(link, title) {
	jQuery("#popupContainer > .popupDisplay > .header > .title").html(title);
	// Add loading image
	var loadingImg = jQuery('<img style="position: relative;" width="60px" height="60px"/>');
	loadingImg.attr('src', '../images/loading.gif');
	var popupContent = jQuery('#popupContent');
	popupContent.html(loadingImg);
	// Show popup
	var popup = jQuery("#popupContainer");
	popup.css('height', jQuery(document).height());
	popup.show();
	// Popup display
	var popupDisplay = jQuery("#popupContainer > .popupDisplay");
	var popupDisplayTop = $(window).height() / 2 - popupDisplay.height() / 2 + $(window).scrollTop();
	popupDisplay.css('marginTop', popupDisplayTop + "px");
	popupDisplay.css('marginLeft', ($(window).width() / 2 - popupDisplay.width() / 2) + "px");
	// Loading image position
	loadingImg.css('left', popupContent.width() / 2 - loadingImg.width() / 2);
	loadingImg.css('top', (popupDisplay.height() - jQuery("#popupContainer > .popupDisplay > .header").height()) / 2
			- loadingImg.height() / 2);
	submitAjaxGetReplaceListElementWithDifferentelement(link, "rightcol", getListElementIdArrays('popupContent'),
			new Array(), function() {
				popupDisplayTop = $(window).height() / 2 + $(window).scrollTop() - popupDisplay.height() / 2;
				popupDisplay.css('marginTop', popupDisplayTop + "px");
				popupDisplay.css('marginLeft', ($(window).width() / 2 - popupDisplay.width() / 2) + "px");
			});
}

function closePopup() {
	var popup = jQuery("#popupContainer");
	popup.hide();
}

/**
 * Submit ajax url with parameters receive from listElements and refresh
 * elements with id in listelementId. If scrollElementId not empty, scroll to
 * element with id scrollElementId.
 */
function submitAjaxGetReplaceListElementWithDifferentelement(url, dataElementId, listelementId, listElements,
		actionAfterSucess) {
	url = createUrlByUrlAndElement(url, listElements);
	jQuery.ajax({
		type : "GET",
		url : url,
		dataType : "html",
		success : function(data) {
			for ( var int = 0; int < listelementId.length; int++) {
				var elementId = listelementId[int];
				refreshElementWithDifferentElement(data, dataElementId, elementId);
			}
			if (actionAfterSucess != undefined) {
				actionAfterSucess();
			}
		}
	});
}

/**
 * Refresh element with new html data
 */
function refreshElementWithDifferentElement(data, dataElementId, elementId) {
	var gotcha = parseHTML(data, dataElementId);
	if (gotcha == undefined) {
		jQuery('#' + elementId).html("");
	} else {
		jQuery('#' + elementId).html(gotcha.innerHTML);
	}
}

function restrictInputDoubleOnly(event, sourceElement) {
	var value = sourceElement.value;
	var editedValue = "";
	for ( var int = 0; int < value.length; int++) {
		var currentChar = value[int];
		if (currentChar == '.') {
			if (editedValue.indexOf('.') < 0) {
				editedValue = editedValue + currentChar;
			}
		} else {
			if (isNumber(currentChar)) {
				editedValue = editedValue + currentChar;
			}
		}
	}
	sourceElement.value = editedValue;
}

function isNumber(character) {
	try {
		var number = Number(character);
		return number >= 0 && number <= 9;
	} catch (e) {
		return false;
	}
}

function testIPAddress(ipElementId, emptyMessage) {
	var value = jQuery.trim($('#' + ipElementId).val());
	if (value == '') {
		alert(emptyMessage);
		return;
	}
	window.open("http://" + value);
	return true;
}

function showPopup(element, popupId, event) {
	var popup = $('#' + popupId);
	var percentHeight = $(window).height() / $(document).height();
	popup.css('top', event.clientY);
	popup.css('left', event.clientX);
	popup.show();
	if (popup.offset().top + popup.width() > $(document).height()) {
		$(document).find('body').height(popup.offset().top + popup.width());
	}
}

function hidePopup(popupId) {
	var popup = $('#' + popupId);
	popup.hide();
}

//Load list District due to selected city
function citySelected(citySelect, districtSelect) {
	var url = location.protocol+"//"+ location.host + location.pathname + "/getDistrict";
	submitSelectSelection(citySelect,districtSelect, url, "cityId");
}

//Load list District due to selected city
function districtSelected(districtSelect, wardSelect) {
	var url = location.protocol+"//"+ location.host + location.pathname + "/getWard";
	submitSelectSelection(districtSelect, wardSelect, url, "districtId");
}

//Load list District due to selected district
function submitSelectSelection(sourceSelect, destSelect, url, paramName) {
	// Empty ward
	destSelect.empty();
	var option = $(document.createElement("option"));
	option.val(-1);
	option.appendTo(destSelect);
	// Get selected value
	var selectedValues = sourceSelect.find('option:selected');
	if (selectedValues!=undefined && selectedValues.length > 0){
		var selectedValue = selectedValues[0].value;
		if (selectedValue >= 0){
			// Create url
			var location = window.location;
			if (url.indexOf('?')>=0){
				url += "&";
			}else{
				url += "?";
			}
			url += paramName+"="+selectedValue;
			// Submit ajax
			$.ajax({
				url:url,
				type: 'POST',
				dataType : "json",
				success: function(data){
					//Parser JSON
					for ( var int = 0; int < data.length; int++) {
						var ward = data[int];
						// create option
						var option = $(document.createElement("option"));
						option.val(ward.id);
						option.html(ward.ten);
						if (int==0){
							option.attr('selected', 'selected');
						}
						// add to ward select
						option.appendTo(destSelect);
					}
					destSelect.select2();
				}
			});
		}
	}
}

function doPagination(submitURL, additionalParams, submitMethod, formSubmit) {
	var finalURL = submitURL;
	showLoading();
	if (additionalParams != '' && additionalParams != undefined) {
	// Get all HTML elements which will be passed server side to as parameters
		var arrElements = getListElementArrays(additionalParams);
		// Build the final submit URL in which parameters are encoded as pairs of key=value
		finalURL = createUrlByUrlAndElement(submitURL, arrElements);
	}
	if (submitMethod != null && submitMethod != undefined) {
		submitMethod = submitMethod.toUpperCase();
		if (submitMethod == 'GET') {
			window.location = finalURL;
		}
		else {
			document.forms[formSubmit].action = finalURL;
			document.forms[formSubmit].method = 'POST';
			document.forms[formSubmit].submit();
		}
	}
}
//Lấy danh sách tỉnh thành phố.
function getProvince() {
    getListProvince(1,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_city_id', list, '-- Tỉnh/Thành phố --');
                } else {
                    alert(JSON.stringify(list));
                }

            }, function (error) {
        alert(JSON.stringify(error));
    });
}
//GetListChannelByChannelTypeId
function getListChannelByChannelTypeId(channelTypeId, successCallback, errorCallback) {
    var contents = new Object();
    contents.channelTypeId = channelTypeId;
    requestToServer('POST', contextPath + '/ajax/channel/getCbChannelByChannelTypeId', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
//Lấy danh sách vùng miền
function getListVungMien() {
	getListChannelByChannelTypeId(1,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_vung_mien', list, '-- Vùng miền --');
                } else {
                    alert(JSON.stringify(list));
                }
            }, function (error) {
        alert(JSON.stringify(error));
    });
}
//Lấy listNhaPhanPhoi
function getListNhaPhanPhoi() {
	getListChannelByChannelTypeId(3,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_channel_id', list, '-- Nhà phân phối --');
                } else {
                    alert(JSON.stringify(list));
                }

            }, function (error) {
        alert(JSON.stringify(error));
    });
}
//getListUser
function getCbListUser(successCallback, errorCallback) {
    var contents = new Object();
    requestToServer('POST', contextPath + '/ajax/user/getCbListUser', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getListUser() {
	getCbListUser(
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_user_id', list, '-- Nhân viên --');
                } else {
                    alert(JSON.stringify(list));
                }

            }, function (error) {
        alert(JSON.stringify(error));
    });
}
//getListUserByCity
function getCbListUserByCity(locationId, successCallback, errorCallback) {
    var contents = new Object();
    contents.locationId = locationId;
    requestToServer('POST', contextPath + '/ajax/user/getCbListUserByTinhThanh', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getListUserByCity() {
    var locationId = $("#frm_city_id option:selected").val();
    getCbListUserByCity(locationId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_user_id', list, '-- Nhân viên --');
                } 

            }, function (error) {

    });
}
//Lấy danh sách nhà phân phối theo LocationId
function getCbListChannelByLocationId(locationId, successCallback, errorCallback) {
    var contents = new Object();
    contents.locationId = locationId;
    requestToServer('POST', contextPath + '/ajax/channel/getCbListChannelByLocationId', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getListNhaPhanPhoiByLocation() {
    var locationId = $("#frm_city_id option:selected").val();
    getCbListChannelByLocationId(locationId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                    renderComboBox('#frm_channel_id', list, '-- Nhà phân phối --');
                } 

            }, function (error) {

    });
}
//Lấy danh sách User By ChannelId
function getCbListUserByChannelId(channelId, successCallback, errorCallback) {
    var contents = new Object();
    contents.channelId = channelId;
    requestToServer('POST', contextPath + '/ajax/user/getCbListUserByChannelId', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getListUserByNhaPhanPhoi() {
    var channelId = $("#frm_channel_id option:selected").val();
    getCbListUserByChannelId(channelId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                	renderComboBox('#frm_user_id', list, '-- Nhân viên --');
                } 

            }, function (error) {

    });
}
//Lấy địa chỉ và tên nhân viên chăm sóc
function getAddressByPOSId(channelId, successCallback, errorCallback) {
    var contents = new Object();
    contents.id = channelId;
    requestToServer('POST', contextPath + '/ajax/pos/getPOS', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getAddressAndName() {
    var channelId = $("#posId option:selected").val();
    if(channelId != 0){
    	getAddressByPOSId(channelId,
                function (success) {
                    var list = getContent(success);
                    if (list != null) {
                    	document.getElementById("address").value = list.address;
                    	document.getElementById("nameNVCS").value = list.nameNVCS;
                    } 

                }, function (error) {

        });
    }else{
    	document.getElementById("address").value = "";
    	document.getElementById("nameNVCS").value = "";
    }
}
//Lấy danh sách hàng hóa
function getGoodsByCreatedUser(posId, successCallback, errorCallback) {
    var contents = new Object();
    contents.id = posId;
    requestToServer('POST', contextPath + '/ajax/goods/getGoodsSalesman', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getGoods() {
    var posId = $("#posId option:selected").val();
    if(posId != 0){
    	getGoodsByCreatedUser(posId,
                function (success) {
                    var list = getContentList(success);
                    if (list != null) {
                    	renderComboBox('#goods_name', list, '-- Sản phẩm --');
                    } 

                }, function (error) {

        });
    }else{
    	renderComboBox('#goods_name', 0, '-- Sản phẩm --');
    }
}
//Lấy danh sách GoodsUnit theo GoodsId
function getGoodsUnitByGoodsId(goodsId, successCallback, errorCallback) {
    var contents = new Object();
    contents.goodsId = goodsId;
    requestToServer('POST', contextPath + '/ajax/goods/goodsUnits', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getGoodsUnit() {
    var goodsId = $("#goods_name option:selected").val();
    getGoodsUnitByGoodsId(goodsId,
            function (success) {
                var list = getContentList(success);
                if (list != null) {
                	renderComboBox('#goods_unit', list, '-- Đơn vị --');
                } 

            }, function (error) {

    });
}
//Lấy giá của sản phẩm
function getPriceOfGoods(id, successCallback, errorCallback) {
    var contents = new Object();
    contents.id = id;
    requestToServer('POST', contextPath + '/ajax/goodsUnit/getGoodsUnit', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getPrice() {
    var id = $("#goods_unit option:selected").val();
    getPriceOfGoods(id,
            function (success) {
                var list = getContent(success);
                if (list != null) {
                	document.getElementById("goods_price").value = list.price;
                	var quantity = document.getElementById("goods_quantity").value;
                	var totalPrice = formatNumber(quantity*list.price);
                	var price = formatNumber(list.price);
                	document.getElementById("totalPrice").innerHTML = quantity*list.price;
                	document.getElementById("totalPriceShow").innerHTML = totalPrice;
                	document.getElementById("priceOfGoods").innerHTML = price;
                } 

            }, function (error) {

    });
}

function addGoods() {
//    var trListEmpty = $('#tr_list_empty');
//    if (trListEmpty.length != 0) {
//        $('#goodsTable').empty();
//    }
    var goodsNameCom = $('#goods_name');
    var goodUnitCom = $('#goods_unit');
  //  var mcpSaleStatusCom = $('#mcp_sales_detail_status');

    if (goodsNameCom.val() == 0) {
        alert('Vui lòng chọn sản phẩm.');
        return;
    }

    if (goodUnitCom.val() == 0) {
        alert('Vui lòng chọn đơn vị cho sản phẩm.');
        return;
    }

    var quantity = $('#goods_quantity').val();
    if (parseInt(quantity) < 0) {
        alert('Số lượng phải là số nguyên dương');
        return;
    }
    if(!isSoNguyenDuong(quantity)){
    	 alert('Số lượng phải là số nguyên dương');
         return;
    }
    var price2 = $('#goods_price').val();
    
    if(parseInt(price2) < 0){
    	 alert('Giá sản phẩm không thể nhỏ hơn 0');
         return;
    }
    price = formatNumber(price2);
    var totalPrice = $('#totalPrice').text();

    totalPrice2 = formatNumber(totalPrice);
//    if (mcpSaleStatusCom.val() == 0) {
//        alert('Vui lòng chọn trạng thái.');
//        return;
//    }
    var idGoods = goodsNameCom.val();
    var idGoodsUnit = goodUnitCom.val();
    var id = idGoods + idGoodsUnit;
    if ($('#quantity_' + id).length == 0) {
        var goodsName = goodsNameCom.find(":selected");
        var goodsUnit = goodUnitCom.find(":selected");
      //  var mcpSalesStatus = mcpSaleStatusCom.find(":selected");
        var index = $('#goodsTable').children().length;

        //Name sản phẩm
        var tr = '<tr id="tr_' + index + '">';
        tr += '<td align="center">';
        tr += goodsName.text();
        tr += '	<input type="hidden" name="orderDetailsList[' + index + '].goodsId" value="' + idGoods + '"/>';
        tr += '	<input type="hidden" name="orderDetailsList[' + index + '].goodss.name" value="' + goodsName.text() + '"/>';
        tr += '</td>';

        //Số lượng
        tr += '<td align="center">';
        tr += '<label id="lb_quantity_' + id + '">' + quantity + '</label>';
        tr += '	<input type="hidden" name="orderDetailsList[' + index + '].quantity" id="quantity_' + id + '" value="' + quantity + '" style="width:35px;"/>';
        tr += '</td>';
        
        //Đơn vị
        tr += '<td align="center">';
        tr += '<label>' + goodsUnit.text() + '</label>';
        tr += '	<input type="hidden" name="orderDetailsList[' + index + '].goodsUnitId" value="' + goodsUnit.val() + '"/>';
        tr += '	<input type="hidden" name="orderDetailsList[' + index + '].goodsUnits.name" value="' + goodsUnit.text() + '"/>';
        tr += '</td>';

        //Price

        tr += '<td align="right">';
        tr += '<label id="lb_price_' + id + '">' + price + '</label>';
     //   tr += '	<input type="hidden" name="orderDetailsList[' + index + '].price2" id="price_' + id + '" value="' + price2 + '" style="width:35px;"/>';
        tr += '</td>';
        //Thành tiền
        tr += '<td align="right">';
        tr += '<label id="lb_totalPrice_' + id + '">' + totalPrice2 + '</label>';
      //  tr += '	<input type="hidden" name="orderDetailsList[' + index + '].totalPrice" id="totalPrice_' + id + '" value="' + totalPrice + '" style="width:35px;"/>';
        tr += '</td>';

        tr += '<td align="center">';
        tr += '	<a href="javascript:deleteGoods(' + index + ');">Xóa</a>';
        tr += '</td>';
        tr += '</tr>';
        $('#goodsTable').append($(tr));
    } else {
        $('#quantity_' + id).val(parseFloat(quantity) + parseFloat($('#quantity_' + id).val()));
        $('#lb_quantity_' + id).text($('#quantity_' + id).val());
        var total = $('#quantity_' + id).val()*price2;
        $('#totalPrice_' + id).val(total);
        total = formatNumber(total);
        $('#lb_totalPrice_' + id).text(total);
    }
    document.getElementById("totalPriceShow").innerHTML = "";
    document.getElementById("priceOfGoods").innerHTML = "";
    document.getElementById("goods_quantity").value = 1;
    //$(goods_unit).val(0); $(goods_unit).select2();
   // document.getElementById("goods_unit").options[0].selected = true;
}

function deleteGoods(id) {
    $('#tr_' + id).remove();
}

//Bắt validate
function createOrder(){
    if (validateForm() == 1) {
        $("#setOrderForm").submit();
    }
}
function validateForm() {
    var isOK = true;
//    if (checkEmptys('#mcp_name', 'Vui lòng nhập tên kế hoạch.', '#mcp_name_error')) {
//        isOK &= false;
//    } else {
//        isOK &= true;
//        $('#mcp_name_error').html('');
//    }

    var provinceId = $("#posId option:selected").val();
    if (parseInt(provinceId) === 0) {
        isOK &= false;
        $('#errorOrderPOS').html('Vui lòng chọn điểm bán hàng.');
    } else {
        isOK &= true;
        $('#errorOrderPOS').html('');
    }
    
    var startDate = $('#frm_startDate').val();
    var m = startDate.match(/^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$/);
    if (m == null)
    {
    	 isOK &= false;
         $('#errorSalesTransDate').html('Vui lòng chọn ngày giao hàng.');
    }
    var tr = $('#tr_0').val();
    if(tr == null){
    	isOK &= false;
        $('#errorStockGoods').html('Vui lòng chọn ít nhất một sản phẩm đặt hàng.');
    }
    
    return isOK;
}

function ivalidDate(date)
{
    var dateField = $(date);
    // regular expression to match required date format
    var re = /^(\d{1,2})\/(\d{1,2})\/(\d{4})$/;
    var regs = dateField.val().match(re);
    if (regs) {
        // day value between 1 and 31
        if (regs[1] < 1 || regs[1] > 31) {
            dateField.focus();
            return false;
        }
        // month value between 1 and 12
        if (regs[2] < 1 || regs[2] > 12) {
            dateField.focus();
            return false;
        }
        // year value between 1902 and 2015
        if (regs[3] < 1902 || regs[3] > (new Date()).getFullYear()) {
            dateField.focus();
            return false;
        }
    } else {
        dateField.focus();
        return false;
    }

    return true;
}

/**
 * set value when chekbox on change
 * @param {type} checkBox is checkbon you want set value
 */
function getStatusCheckox(checkBox) {
    $(checkBox).change(function () {
        if (this.checked) {
            this.setAttribute('value', 1);
        } else {
            this.setAttribute('value', 0);
        }
    });
}

function isNumber(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}
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
function isSoNguyenDuong(txt) {
	var regex = /^[1-9][0-9]*$/g;
	return (txt.length > 0) && regex.test(txt);
}

function createChannelType(){
    if (validateFormChannelType() == 1) {
        $("#channelTypeForm").submit();
    }
}

function validateFormChannelType() {
    var isOK = true;
    if (checkEmptys('#code', 'Vui lòng nhập mã loại kênh.', '#errorCode')) {
        isOK &= false;
    } else {
        isOK &= true;
       $('#errorCode').html('');
    }
    if (checkEmptys('#name', 'Vui lòng nhập tên loại kênh.', '#errorName')) {
        isOK &= false;
    } else {
        isOK &= true;
       $('#errorName').html('');
    }

    var code = $('#code').val();
    if(!code.trim().length){
    	 $('#errorCode').html('Mã loại kênh không được trống hoặc chứa toàn khoảng trắng');
    	 isOK &= false;
    }
    var name = $('#name').val();
    if(!name.trim().length){
    	 $('#errorName').html('Tên loại kênh không được trống hoặc chứa toàn khoảng trắng');
    	 isOK &= false;
    }
    return isOK;
}


function validateSearch() {
    var isOK = true;
    var fromHour = $("#fromHour option:selected").val();
    var fromMin = $("#fromMin option:selected").val();
    var toHour = $("#toHour option:selected").val();
    var toMin = $("#toMin option:selected").val();
    
   var frm_from_date = $('#frm_from_date').val();
   if(parseInt(fromHour) != 0 || parseInt(toHour) != 0  || parseInt(fromMin) != 0  || parseInt(toMin) != 0 ){
	   if(frm_from_date.length <= 0){
		   isOK &= false;
	       $('#errorDate').html('Cần nhập ngày tìm kiếm.');
	   }else{
		   var m = frm_from_date.match(/^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$/);
		    if (m == null)
		    {
		    	 isOK &= false;
		         $('#errorDate').html('Cần nhập đúng ngày tìm kiếm(dd/MM/yyyy).');
		    }
	   }
   }
   if(parseInt(toHour) != 0 || parseInt(toMin) != 0){
	   if(parseInt(fromHour) > parseInt(toHour)){
		   isOK &= false;
	       $('#errorToHour').html('Giờ bắt đầu phải trước giờ kết thúc.');
	   }
	   if(parseInt(fromHour) == parseInt(toHour)){
		   if(parseInt(fromMin) > parseInt(toMin)){
			   isOK &= false;
		       $('#errorToHour').html('Thời gian bắt đầu phải trước thời gian kết thúc.'); 
		   }
	   }
   }
   return isOK;
}
function formatNumber (num) {
    return num.toString().replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1.");
}

function createMarkerFromLoTrinh(dsLoTrinh,tenNV, map, pointImage,pointStartImage,pointEndImage) {
	var description = "";
	var kd = 0, vd = 0;
	var kdE = 0, vdE = 0;
	for(var i = 0; i < dsLoTrinh.length; i++){
		vd = dsLoTrinh[i].lat;
		kd = dsLoTrinh[i].lng;
		description = tenNV  + "<br/>" + dsLoTrinh[i].routeAt;
		if(i==0){
			vdE = vd;
			kdE = kd;
			createMarker(vd, kd, map, pointEndImage, description);
		}else{
			if(i == dsLoTrinh.length -1){
				createMarker(vd, kd, map, pointStartImage, description);
			}else{
				//if (vd != vdE || kd != kdE) {
					createMarker(vd, kd, map, pointImage, description);
				//}
			}
		}
		
	}
	
	map.setCenter(new google.maps.LatLng(vdE, kdE));
	
//	$.each(dsLoTrinh, function(i, loTrinh) {
//	if (loTrinh.lat != 0) {
//		description = tenNV  + "<br/>" + loTrinh.routeAt;
//		if(isStart){
//			createMarker(loTrinh.lat, loTrinh.lng, map, pointStartImage, description);
//			isStart = false;
//		} else {
//			vd = loTrinh.lat;
//			kd = loTrinh.lng;
//			createMarker(loTrinh.lat, loTrinh.lng, map, pointImage, description);
//		}
//	}
//
//	});
//		if(vd!=0){
//		createMarker(vd, kd, map, pointEndImage, description);
//		map.setCenter(new google.maps.LatLng(vd, kd));
//		} 
}

function renderComboBoxD(comboBoxSelector, items, label) {
    var $domCbo = $(comboBoxSelector);
    renderComboBoxEmptyD(comboBoxSelector, label);
    for (var i = 0; i < items.length; i++) {
        var item = items[i];
        var $domOption = $("<option value = '" + item.id + "'>" + item.name + "</option>");
        $domCbo.append($domOption);
    }
}

function renderComboBoxEmptyD(comboBoxSelector, label) {
    var $domCbo = $(comboBoxSelector);
    $domCbo.empty();// remove old options
    var $domOption = $("<option value = '0' disabled='true' >" + label + "</option>");
    $domCbo.append($domOption);
    $domCbo.select2();
}
//Lấy danh sách hàng hóa theo goodsCategoryId
function getGoodsByGoodsCategoryId(goodsCategoryId, successCallback, errorCallback) {
    var contents = new Object();
    contents.goodsCategoryId = goodsCategoryId;
    requestToServer('POST', contextPath + '/ajax/getListGoodsByGoodsCategoryIdOfPromotion', contents,
        function (data) {
            success(data, successCallback);
        },
        function (data) {
            error(data, errorCallback);
        }
    );
}
function getGoodsOfPromotion() {
    var goodsCategoryId = $("#goodsCategoryId option:selected").val();
    if(goodsCategoryId != 0){
    	getGoodsByGoodsCategoryId(goodsCategoryId,
                function (success) {
                    var list = getContentList(success);
                    if (list != null) {
                    	renderComboBoxD('#listGoodsId', list, '---Chọn sản phẩm ---');
                    } 

                }, function (error) {

        });
    }else{
    	renderComboBoxD('#listGoodsId', 0, '---Chọn sản phẩm ---');
    }
}
