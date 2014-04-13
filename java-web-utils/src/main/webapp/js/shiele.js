/**
 *屏蔽功能类(屏蔽F5、Ctrl+N、Shift+F10、Alt+F4、右键菜单......) 
 *@createDate 2009-07-30
 *@author Carl He
*/

/** 屏蔽F1帮助 */
window.onhelp = function(){return false;}

/**
 *屏蔽 F5、Ctrl+N、Shift+F10、Alt+F4
 *如果想要屏蔽其他键，则找到对应的 keyCode 再依照此方法即可
*/
document.onkeydown = function(event){
	event = window.event || event;
	if(event.keyCode==116 || (event.ctrlKey && event.keyCode==78) || (event.shiftKey && event.keyCode==121) || (event.altKey && event.keyCode==115)){
		event.keyCode =0;
		event.returnvalue = false;
	}
}

/** 屏蔽鼠标右键 */
document.oncontextmenu = function(){return false;}

//或者

document.onmousedown = function(event){
	event = window.event || event;
	if(document.all && event.button == 2) {
		event.returnvalue=false;
	}
}

/**
 * 屏蔽“后退”功能(<a href="javascript:replaceLocation('http://www.google.com')">Google</a>)
 * @param url 页面要转向的URL
*/
function replaceLocation(url){
	document.location.replace(url);
}

/** 屏蔽选中网页内容 */
document.onselectstart=function(){return false;}

/** 屏蔽复制网页内容 */
document.body.oncopy = function(){return false;}

/** 屏蔽剪切网页内容 */
document.body.oncut = function(){return false;}

/** 屏蔽向网页粘贴内容 */
document.body.onpaste = function(){return false;}

/** 屏蔽拷屏（不停的清空剪贴板） */
window.setInterval('window.clipboardData("Text", "")', 100);

/**
 * 屏蔽查看源文件( <body onload=clear()> )
*/
function clear() {    
	var source=document.body.firstChild.data;    
	document.open();    
	document.close();    
	document.body.innerHTML = source;    
}

/** 防止他人把你的网页嵌入到框架中 */
if(window != window.top){
	window.top.location = window.location;
}