var contextPath = window.location.pathname.substring(0,
		window.location.pathname.indexOf("/", 2));

$(document).ready(function() {
	handleGlobalAjaxError();//in ajax.js file
	
	//var url="cities/all";
	//$.ajax({type: 'GET', url: url, success: function(data){
	//	var cities = data;
	//	renderComboBox("#city", cities);
	//}});
});

function selectCity(domCity){
	var $domCity = $(domCity);
	var cityUid = $domCity.val();
	if (cityUid != "0"){
		var url=contextPath + "/town/findByCityUid/"+cityUid;
		$.ajax({type: 'GET', url: url, success: function(data){	
			renderComboBox("#quanHuyen", data);
		}});
	}
	else{
		renderComboBoxEmpty("#quanHuyen");
	}
}

function renderComboBox(comboBoxSelector, items){
	//re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();//remove old options
	for ( var i = 0; i < items.length; i++) {
		var item = items[i];
		var $domOption = $("<option value = '"+item.uid+"'>"+item.name+"</option>");
		$domCbo.append($domOption);
	}
	$domCbo.select2();
}
function renderComboBoxEmpty(comboBoxSelector){
	//re-render provinces
	var $domCbo = $(comboBoxSelector);
	$domCbo.empty();//remove old options
	$domCbo.select2();
}


function selectArea(vungMienElement){
	var $domArea = $(vungMienElement);
	var vungMienId = $domArea.val();
	if (vungMienId != "0"){
		var url=contextPath + "/ajax/getArea?"+vungMienId;
		$.ajax({type: 'GET', url: url, success: function(data){	
			renderComboBox("#tinhThanh", data);
		}});
	}
	else{
		renderComboBoxEmpty("#tinhThanh");
	}
}