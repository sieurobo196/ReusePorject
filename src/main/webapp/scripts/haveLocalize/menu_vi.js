$(document).ready(function() {
	var $firstPara3 = $('#leftcol');
	$firstPara3.hide();
	jQuery('a.ClickHere').click(function() {
		jQuery('#leftcol').slideToggleWidth();
		var $link3 = $(this);
		if ($link3.text() == "Hiện menu") {
			jQuery('#rightcol').animate({
				"width" : "731px"
			});
			$link3.text('Ẩn menu');
		} else {
			$link3.text('Hiện menu');
			jQuery('#rightcol').animate({
				"width" : "100%"
			});
		}
		try {
			submitMapOption();
		} catch (e) {
		}
		return false;
	});

	jQuery.fn.extend({
		slideRight : function() {
			return this.each(function() {
				jQuery(this).animate({
					width : 'show'
				});
			});
		},
		slideLeft : function() {
			return this.each(function() {
				jQuery(this).animate({
					width : 'hide'
				});
			});
		},
		slideToggleWidth : function() {
			return this.each(function() {
				var el = jQuery(this);
				if (el.css('display') == 'none') {
					el.slideRight();
				} else {
					el.slideLeft();
				}
			});
		}
	});
});