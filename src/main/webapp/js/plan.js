
function selectCity(domCity){
	var $domCity = $(domCity);
	var cityUid = $domCity.val();
	var url=contextPath + "/ajax/getCbListDistrictByParentIdAndChannelId?parentId="+cityUid;
        
	if (cityUid!=0){
		$.ajax({type: 'GET', url: url, success: function(data){	
                    var json = JSON.parse(data);
                    json = json.data;
                    renderComboBox("#quanHuyen", json, true);
		}});
	}
	else{
		renderComboBoxEmptyD("#quanHuyen", "--- Chọn Quận/Huyện ---");
	}	
	renderComboBoxEmpty("#phuongXa");
	renderComboBoxEmpty("#tuyenDuong");
	renderComboBoxEmpty("#listDiemBH");
}

function renderComboBoxEmptyD(comboBoxSelector, label) {
    var $domCbo = $(comboBoxSelector);
    $domCbo.empty();// remove old options
    var $domOption = $("<option value = '0' >" + label + "</option>");
    $domCbo.append($domOption);
    $domCbo.select2();
}

function selectTown(domTown){
    var $domTown = $(domTown);
    var townUid = $domTown.val();
    if (townUid != 0) {
        var url=contextPath + "/ajax/getCbListLocationByParentId?parentId="+townUid;
        $.ajax({type: 'GET', url: url, success: function (data) {
                var json = JSON.parse(data);
                renderComboBox("#phuongXa", json.data, true);
            }});
    }
    else {
        renderComboBoxEmpty("#phuongXa", true);
    }
    renderComboBoxEmpty("#tuyenDuong", true);
    renderComboBoxEmpty("#listDiemBH");
   /*
    var $domTown = $(domTown);
    var townUid = $domTown.val();

    getListWard(parseInt(townUid), function (dataSuccess) {
        var content = getContentList(dataSuccess);
        renderComboBox('#phuongXa', content);
        alert(JSON.stringify(dataSuccess));
    }, function (error) {

    });
    */
}

function employeeFilter(){
	var em = $('#implementIdSearch').val();
	if (em == 0) return;
	showLoading();
	url=contextPath + "/ajax/searchPOS";
	param = "employee=" +em+"&day="+$('#daySearch').val();
	$.ajax({type: 'POST', contentType: 'application/x-www-form-urlencoded; charset=UTF-8',url: url, data:param, success: function(data){	
		renderIndexComboBox("#listDiemBH", data);
	}});
}
function refreshAgent(){
	var $domCity = $("#provinceId");
	var $domTown = $("#quanHuyen");
	var $domWard = $("#phuongXa");
	var $domSearch = $("#nameCodeAddressPos");
	var cityUid = $domCity.val();
	var townUid = $domTown.val();
	var wardUid = $domWard.val();
	var search=$domSearch.val();
	var url='';
	if (parseInt(cityUid) =="0" ){
            alert('Vui lòng chọn một tỉnh thành phố');
	}
    var beginDate = $("#frm_beginDate").val();
    var m = beginDate.match(/^(((0[1-9]|[12]\d|3[01])\/(0[13578]|1[02])\/((19|[2-9]\d)\d{2}))|((0[1-9]|[12]\d|30)\/(0[13456789]|1[012])\/((19|[2-9]\d)\d{2}))|((0[1-9]|1\d|2[0-8])\/02\/((19|[2-9]\d)\d{2}))|(29\/02\/((1[6-9]|[2-9]\d)(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))))$/);
    if (m == null)
    {
    	 alert('Vui lòng chọn ngày thực hiện');
    }
        method = 'POST';
        var contents = new Object();
	if (townUid=="0" && wardUid==null && parseInt(cityUid) > 0 && m != null){
		if (search.length==0) {
			url=contextPath + "/ajax/searchPOS";
                        contents.locationId = cityUid;
                        contents.beginDate = beginDate;
		} else {
			url=contextPath + "/ajax/searchPOS";
                        contents.locationId = cityUid;
                        contents.searchText = search;
                        contents.beginDate = beginDate;
		}
                                
		showLoading();
		$.ajax({type: method, dataType: 'json', contentType: 'application/json',url: url, data:JSON.stringify(contents), success: function(data){
                        var resultData = getContentList(data);	
			renderIndexComboBox("#listDiemBH", resultData);
		}});
	}	
	else if  (wardUid=="0" && parseInt(cityUid) > 0 && parseInt(townUid) > 0 && m != null){
		if (search.length==0) {
                    url=contextPath + "/ajax/searchPOS";
                    contents.locationId = townUid;
                    contents.beginDate = beginDate;
                } else {
                    url=contextPath + "/ajax/searchPOS";
                    contents.locationId = townUid;
                    contents.searchText = search;
                    contents.beginDate = beginDate;
                }
		showLoading();
		$.ajax({type: method, dataType: 'json', contentType: 'application/json',url: url, data:JSON.stringify(contents), success: function(data){
                        var resultData = getContentList(data);
			renderIndexComboBox("#listDiemBH", resultData);
		}});
	}
	else if  (parseInt(cityUid) > 0 && parseInt(townUid) > 0 && parseInt(wardUid) > 0 && m != null){
		if (search.length==0) {
                    url=contextPath + "/ajax/searchPOS";
                    contents.locationId = wardUid;
                    contents.beginDate = beginDate;
                } else {
                    url=contextPath + "/ajax/searchPOS";
                    contents.locationId = wardUid;
                    contents.searchText = search;
                    contents.beginDate = beginDate;
                }
		showLoading();
		$.ajax({type: method, dataType: 'json', contentType: 'application/json',url: url, data:JSON.stringify(contents), success: function(data){
			var resultData = getContentList(data);	
			renderIndexComboBox("#listDiemBH", resultData);
		}});
	}	
	
	$('#employeeSearch').val(0).select2();
}

function renderIndexComboBox(comboBoxSelector, items, reindex){
	//re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();//remove old options
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '"+item.id+"'>"+(i+1)+'. '+ item.posCode + ' - ' + item.name+"</option>");
		$domCbo.append($domOption);
	}
	if (reindex) {
		$domCbo.select2();
	}
	hideLoading();
	
}

function renderComboBox(comboBoxSelector, items, reindex){
	//re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();//remove old options
        
        if (comboBoxSelector === "#quanHuyen") {
            var $domOptionDefault = $("<option value = '0'>--- Chọn Quận/Huyện ---</option>");
            $domCbo.append($domOptionDefault);
        } else if (comboBoxSelector === "#phuongXa") {
            var $domOptionDefault = $("<option value = '0'>--- Chọn Phường/Xã ---</option>");
            $domCbo.append($domOptionDefault);
        }
        
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '"+item.id+"'>"+item.name+"</option>");
		$domCbo.append($domOption);
	}
	if (reindex) {
		$domCbo.select2();
	}
	hideLoading();
	
}
function renderComboBoxEmpty(comboBoxSelector, reindex){
	//re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();//remove old options
	if (reindex) {
		$domCbo.select2();
	}
	hideLoading();
}

//====================
function refreshFilter(){
	
	var $domTown = $("#quanHuyen");
	var $domWard = $("#phuongXa");
	var $domStreet = $("#tuyenDuong");
	var $domSearch = $("#timKiem");
	var cityUid = "1263";
	var townUid = $domTown.val();
	var wardUid = $domWard.val();
	var streetUid = $domStreet.val();
	var search=$domSearch.val();
	var url='';

	if (townUid=="0" && wardUid==null && streetUid==null && parseInt(cityUid) > 0){
		if (search.length==0)
			url=contextPath + "/sale/findByCityUid/"+cityUid;
		else
			url=contextPath + "/sale/findByCityUid/"+cityUid+"/"+search;
		$.ajax({type: 'GET', contentType: 'application/x-www-form-urlencoded; charset=UTF-8',url: url, success: function(data){	
			renderComboBox("#listDiemBH", data);
		}});
	}	
	if  (wardUid=="0" && streetUid==null && parseInt(cityUid) > 0 && parseInt(townUid) > 0){
		if (search.length==0)
			url=contextPath + "/sale/findByTownUid/"+townUid;
		else
			url=contextPath + "/sale/findByTownUid/"+townUid+"/"+search;
		$.ajax({type: 'GET', contentType: 'application/x-www-form-urlencoded; charset=UTF-8', url: url, success: function(data){	
			renderComboBox("#listDiemBH", data);
		}});
	}
	if  (streetUid=="0" && parseInt(cityUid) > 0 && parseInt(townUid) > 0 && parseInt(wardUid) > 0){
		if (search.length==0)
			url=contextPath + "/sale/findByWardUid/"+wardUid;
		else
			url=contextPath + "/sale/findByWardUid/"+wardUid+"/"+search;
		$.ajax({type: 'GET', contentType: 'application/x-www-form-urlencoded; charset=UTF-8',url: url, success: function(data){	
			renderComboBox("#listDiemBH", data);
		}});
	}
	if  (parseInt(cityUid) > 0 && parseInt(townUid) > 0 && parseInt(wardUid) > 0 && parseInt(streetUid) > 0){
		if (search.length==0)
			url=contextPath + "/sale/findByStreetUid/"+streetUid;
		else
			url=contextPath + "/sale/findByStreetUid/"+streetUid+"/"+search;
		$.ajax({type: 'GET', contentType: 'application/x-www-form-urlencoded; charset=UTF-8', url: url, success: function(data){	
			renderComboBox("#listDiemBH", data);
		}});
	}	
}