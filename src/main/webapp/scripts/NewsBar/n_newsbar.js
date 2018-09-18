/* USE WORDWRAP AND MAXIMIZE THE WINDOW TO SEE THIS FILE
========================================
 NewsBar v1.4
 License : Freeware (Enjoy it!)
 (c)2004 VASIL DINKOV- PLOVDIV, BULGARIA
========================================
 For IE4+, NS4+, Opera7+ & Konqueror2+
========================================
 Get the NewsBar script at:
 http://www.smartmenus.org/
 LEAVE THESE NOTES PLEASE - delete the comments if you want */

// BUG in Opera:
// If you want to be able to control the body margins
// put the script right after the BODY tag, not in the HEAD!!!

// === 1 === FONT, COLORS, EXTRAS...
n_font='arial,sans-serif';
n_fontSize='12px';
n_fontSizeNS4='12px';
n_fontWeight='normal';
n_fontColor='#4b4b4b';
n_textDecoration='none';
n_fontColorHover='#ff0000';//		| won't work
n_textDecorationHover='underline';//	| in Netscape4
n_bgColor='transparent';//set [='transparent'] for transparent
n_top=0;//	|
n_left=5;//	| defining
n_width=500;//	| the box
n_height=22;//	|
n_position='relative';// absolute/relative
n_timeOut=3;//seconds
n_pauseOnMouseOver=true;
n_speed=10;//1000 = 1 second
n_leadingSign='_';
n_alternativeHTML='NewsBar v1.4 (c)2004 Vasil Dinkov';
// for not supported browsers like Opera<7 - usually
// you may want to put a link to your news page 

// === 2 === THE CONTENT - ['href','text','target']
n_content=[
['','Chuyển phát toàn quốc - giao hàng tận nơi - Thanh toán tại nhà',''],
['http://www.smartmenus.org/','You can always get the latest version at: http://www.smartmenus.org/','_self'],
['http://www.smartmenus.org/','Or you may want to get the most advanced navigation system for your site?','_self']
];

// THE SERIOUS SCRIPT - PLEASE DO NOT TOUCH
n_nS4=document.layers?1:0;n_iE=document.all&&!window.innerWidth&&navigator.userAgent.indexOf("MSIE")!=-1?1:0;n_nSkN=document.getElementById&&(navigator.userAgent.indexOf("Opera")==-1||document.body.innerHTML)&&!n_iE?1:0;n_t=0;n_cur=0;n_l=n_content[0][1].length;n_timeOut*=1000;n_fontSize2=n_nS4&&navigator.platform.toLowerCase().indexOf("win")!=-1?n_fontSizeNS4:n_fontSize;document.write('<style>.nnewsbar,a.nnewsbar,a.nnewsbar:visited,a.nnewsbar:active{font-family:'+n_font+';font-size:'+n_fontSize2+';color:'+n_fontColor+';text-decoration:'+n_textDecoration+';font-weight:'+n_fontWeight+'}a.nnewsbar:hover{color:'+n_fontColorHover+';text-decoration:'+n_textDecorationHover+'}</style>');n_p=n_pauseOnMouseOver?' onmouseover="clearTimeout(n_TIM)" onmouseout="n_TIM=setTimeout(\'n_new()\','+n_timeOut+')">':'>';n_k=n_nS4?'':' style="text-decoration:none;color:'+n_fontColor+';"';function n_new(){if(!(n_iE||n_nSkN||n_nS4))return;var O,mes;O=n_iE?document.all['nnewsb']:n_nS4?document.layers['n_container'].document.layers['nnewsb']:document.getElementById('nnewsb');mes=n_content[n_t][0]!=''&&n_cur==n_l?('<a href="'+n_content[n_t][0]+'" target="'+n_content[n_t][2]+'" class="nnewsbar"'+n_p+n_content[n_t][1].substring(0,n_cur)+n_leadingSign+'</a>'):('<span class="nnewsbar"'+n_k+'>'+n_content[n_t][1].substring(0,n_cur)+n_leadingSign+'</span>');if(n_nS4)with(O.document){open();write(mes);close()}else O.innerHTML=mes;if(n_cur++==n_l){n_cur=0;n_TIM=setTimeout('n_new()',n_timeOut);n_t++;if(n_t==n_content.length)n_t=0;n_l=n_content[n_t][1].length}else{setTimeout('n_new()',n_speed)}}document.write('<div '+(n_nS4?'name':'id')+'="n_container" style="position:'+n_position+';top:'+n_top+'px;left:'+n_left+'px;width:'+n_width+'px;height:'+n_height+'px;"><div '+(n_nS4?'name':'id')+'="nnewsb" style="position:absolute;top:0;left:0;width:'+n_width+'px;height:'+n_height+'px;background-color:'+n_bgColor+';layer-background-color:'+n_bgColor+';text-decoration:none;color:'+n_fontColor+'" class="nnewsbar">'+n_alternativeHTML+'</div></div>');if(!n_nS4)setTimeout('n_new()',1000);else window.onload=n_new;if(n_nS4)onresize=function(){location.reload()}