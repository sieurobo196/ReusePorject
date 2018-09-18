jQuery.getTmpl = function(tmplUrl, callback) {
	jQuery.ajax({url: tmplUrl, cache: false, dataType: 'text', success : function(tmpl){
		tmpl = tmpl.replace(/\$request.contextPath/g, getContextPath());
		callback.call(this, tmpl);
	}});
	return false;
};

jQuery.evalTmpl = function(tmplUrl, dataUrl, callback) {
	jQuery.getTmpl(tmplUrl, function(tmpl){
		if(dataUrl != null){
			jQuery.ajax({type: 'GET', url: dataUrl, cache: false, dataType: 'json', success: function(data){
				var template = $.template( null, tmpl );
				callback.call($.tmpl( template, data ), data);
			}});
		}else{
			var template = $.template( null, tmpl );
			callback.call($.tmpl( template, null ), null);
		}
	});
	return false;
};
/**
* successFunc(tmplData, filledTmpl) will have two input parameters:</br>
* <li>tmplData: data get from tmplDataUrl</li>
* <li>filledTmpl: template which was filled by tmplData</li>
*/
jQuery.fillTmpl = function(tmplUrl, tmplDataUrl, successFunc) {
	jQuery.getTmpl(tmplUrl, function(tmpl){
		if(tmplDataUrl != null){
			jQuery.ajax({type: 'GET', url: tmplDataUrl, cache: false, dataType: 'json', 
			success: function(tmplData){
				var template = $.template( null, tmpl );
				successFunc.call(this, tmplData, $.tmpl( template, tmplData ));
			}});
		}else{
			var test = $("<div/>").html(tmpl);
			successFunc.call(this, null, test.children());
		}
	});
	return false;
};