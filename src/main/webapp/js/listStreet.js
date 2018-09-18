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
			renderComboBox("#quan", data);
		}});
	}
	else{
		renderComboBoxEmpty("#quan");	
	}
	renderComboBoxEmpty("#phuong");
}
function selectTown(domTown){
	var $domTown = $(domTown);
	var townUid = $domTown.val();
	if (townUid != "0"){
		var url= contextPath + "/ward/findByTownUid/"+townUid;
		$.ajax({type: 'GET', url: url, success: function(data){	
			renderComboBox("#phuong", data);	
		}});
	}
	else{
		renderComboBoxEmpty("#phuong");
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