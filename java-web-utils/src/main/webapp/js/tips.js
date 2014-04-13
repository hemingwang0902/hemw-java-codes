/**设为首页*/
function setHomepage(){
	if (document.all){
        document.body.style.behavior='url(#default#homepage)';
  		document.body.setHomePage('http://www.google.com');
    }else if(window.sidebar){
		if(window.netscape){
        	try{
            	netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
         	}catch (e){
				alert( "该操作被浏览器拒绝，如果想启用该功能，请在地址栏内输入 about:config,然后将项 signed.applets.codebase_principal_support 值该为true"); 
				return; 
         	}
    	}
    	var prefs = Components.classes['@mozilla.org/preferences-service;1'].getService(Components. interfaces.nsIPrefBranch);
    	prefs.setCharPref('browser.startup.homepage','http://www.google.com');
	}
}

/**加入收藏夹*/
function addFavorite(){
	if (document.all){
       window.external.addFavorite('http://www.google.com','Google');
    }else if (window.sidebar){
       window.sidebar.addPanel('Google', 'http://www.google.com', "");
 	}
}

/**最大化窗口*/
function setMaximize(){
	window.moveTo(0,0);
	window.resizeTo(window.screen.width,window.screen.height);
}

/**使 js 脚本不报错*/
window.onerror = function() {return true;};

/*
网页宽高说明：
	网页可见区域宽： document.body.clientWidth
	网页可见区域高： document.body.clientHeight
	网页可见区域宽： document.body.offsetWidth (包括边线的宽)
	网页可见区域高： document.body.offsetHeight (包括边线的高)
	网页正文全文宽： document.body.scrollWidth
	网页正文全文高： document.body.scrollHeight
	网页被卷去的高： document.body.scrollTop
	网页被卷去的左： document.body.scrollLeft
	网页正文部分上： window.screenTop
	网页正文部分左： window.screenLeft
	屏幕分辨率的高： window.screen.height
	屏幕分辨率的宽： window.screen.width
	屏幕可用工作区高度： window.screen.availHeight
	屏幕可用工作区宽度： window.screen.availWidth
*/

/**获取页面大小（兼容各种主流浏览器）*/
function getPageSize() {
    var xScroll, yScroll;
    if (window.innerHeight && window.scrollMaxY) {
        xScroll = document.body.scrollWidth;
        yScroll = window.innerHeight + window.scrollMaxY;
    } else {
        if (document.body.scrollHeight > document.body.offsetHeight) { // all but Explorer Mac
            xScroll = document.body.scrollWidth;
            yScroll = document.body.scrollHeight;
        } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
            xScroll = document.body.offsetWidth;
            yScroll = document.body.offsetHeight;
        }
    }
    var windowWidth, windowHeight;
    if (self.innerHeight) { // all except Explorer
        windowWidth = self.innerWidth;
        windowHeight = self.innerHeight;
    } else {
        if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
            windowWidth = document.documentElement.clientWidth;
            windowHeight = document.documentElement.clientHeight;
        } else {
            if (document.body) { // other Explorers
                windowWidth = document.body.clientWidth;
                windowHeight = document.body.clientHeight;
            }
        }
    }
    // for small pages wi qth total height less then height of the viewport
    if (yScroll < windowHeight) {
        pageHeight = windowHeight;
    } else {
        pageHeight = yScroll;
    }
    // for small pages with total width less then width of the viewport
    if (xScroll < windowWidth) {
        pageWidth = windowWidth;
    } else {
        pageWidth = xScroll;
    }
    var page = new Object();
    page.pageWidth = pageWidth;
    page.pageHeight = pageHeight;
    page.windowWidth = windowWidth;
    page.windowHeight = windowHeight;
    return page;
}

/**
 *获取 URL 参数， 如果未传入要获取的参数名，则返回一个形如 {p1:"a", p2:"b"} 的包含所有参数对象
 *@param param 要获参数值的参数名称
*/
function getParameter(param){
	if(!location.search){
		return "";
	}else{
		if(param){
			var sValue=location.search.match(new RegExp("[\?\&]" + param + "=([^&]*)(\&?)", "i"));
	     	return sValue?sValue[1]:"";
		}else{
			var params = new Object();
			var query = location.search.substring(1);
			var pairs = query.split("&");
			for(var i = 0; i < pairs.length; i++) {
				var pos = pairs[i].indexOf('=');
				if (pos == -1) continue;
				var key = pairs[i].substring(0,pos);
				var value = decodeURIComponent(pairs[i].substring(pos+1));
				params[key] = value;
			}
			return params;
		}
	}
}

/**
 * 打开一个新页面
*/
function openPage(options){
	if(!options)options = new Object();
	if(!options.url)options.url = "about:blank";
	if(!options.channelmode)options.channelmode = "no";
	if(!options.fullscreen)options.fullscreen = "no";
	if(!options.location)options.location = "no";
	if(!options.menubar)options.menubar = "no";
	if(!options.resizable)options.resizable = "no";
	if(!options.status)options.status = "no";
	if(!options.toolbar)options.toolbar = "no";
	if(options.scrollbars == undefined)options.scrollbars = "yes";
	if(options.titlebar == undefined)options.titlebar = "yes";
	if(options.replace == undefined)options.replace = true;
	var sFeatures = "channelmode="+options.channelmode+",fullscreen="+options.fullscreen+",location="+options.location+",menubar="+options.menubar+",resizable="+options.resizable+",status="+options.status+",toolbar="+options.toolbar+",scrollbars="+options.scrollbars+",titlebar="+options.titlebar;
	if(options.width)
        sFeatures += (",width=" + options.width);
	if(options.height)
        sFeatures += (",height=" + options.height);
	if(options.left)
        sFeatures += (",left=" + options.left);
	if(options.top)
        sFeatures += (",top=" + options.top);
	return window.open(options.url, options.name, sFeatures, options.replace);
}

/**
 * 不弹出提示直接关闭页面
 * 说明：firefox需要修改配置，在Firefox地址栏里输入 about:config，在配置列表中找到dom.allow_scripts_to_close_windows，将其值改为 true（默认是false，是为了防止脚本乱关窗口）
*/
function closePage(win){
	if(!win)
		win = window;

	win.opener = null;
	win.open("","_self", "");
	win.close();
}