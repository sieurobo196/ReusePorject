/**
 * Global variables and functions
 */
var HCSLaboratory = (function($, window, undefined){	 
	
	function initNavigationLeft(){
		$('#sidebar-left').each(function(){
			var self = $(this);
			
			var duration = 500;
			
			var navs = self.children('.block-nav-left');
			
			navs.each(function(idx){
				var nav = $(this);
				
				var title = nav.children('p.title-1');
				var sub = nav.children('ul');
				
				if (sub.is(':hidden')){
					sub.css('display', 'block');
				}
				var maxHeight = sub.outerHeight(),
					minHeight = 0;
				
				sub.css({
					'height': title.hasClass('active') ? maxHeight : 0,
					'overflow': 'hidden'
				});
				
				title.off('click.nav').on('click.nav', function(e){
					e.preventDefault();
					
					if (title.hasClass('active')){
						title.removeClass('active');						
						sub.stop().animate({
							'height': minHeight
						}, duration);
					}
					else{	
						title.addClass('active');					
						sub.stop().animate({
							'height': maxHeight
						}, duration);
					}
				});
			});
		});
	};
	
	function displayScroll(){	
		var sidebar = $('#sidebar-left');
		var content = $('#content');
		
		if (!sidebar.length || !content.length){
			return;
		}
		
		var sideBarTop = sidebar.offset().top;
		var contentTop = content.offset().top;
		
		var win = $(window),
			winH = win.height();
		
		var footer = $('#footer'),
			footerH = footer.outerHeight();		
			
		var sidebarH = 0,
			contentH = 0;
		
		function setHeight(){
			winH = win.height();
			
			sidebarH = winH - footerH - sideBarTop;
			sidebar.css('height', sidebarH);
			contentH = winH - footerH - contentTop;
			content.css('height', contentH - 41);
		};
		setHeight();
		win.resize(function(){			
			setHeight();
		});
	};
	
	return {		
		init : function(){
			initNavigationLeft();
			displayScroll();
		}
		
	};
})(jQuery, window);

/**
 * Website start here
 */
jQuery(document).ready(function($) {		
	//HCSLaboratory.init();
	
});