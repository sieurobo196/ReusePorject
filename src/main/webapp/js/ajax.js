function handleGlobalAjaxError(){
	$.ajaxSetup({
		error: function(xhr, status, err){
			alert(xhr.status);
		},
		contentType: "charset=utf-8"
	});
	return false;
}

(function($) {
	$.postAjax = function(url, data, callback) {
		if(typeof data === "string"){
			return $.ajax({
		        type: 'POST',
		        url: url,
		        cache: false,
		        contentType: 'application/json',
		        data: data,
		        dataType: 'json',
		        success: callback
		    });
		}else if(window.JSON && window.JSON.stringify){
		    return $.ajax({
		        type: 'POST',
		        url: url,
		        cache: false,
		        contentType: 'application/json',
		        data: window.JSON.stringify(data),
		        dataType: 'json',
		        success: callback
		    });
		}
		return null;
	};
})(jQuery);