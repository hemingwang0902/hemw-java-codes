function createXmlHttpRequest() {
    var req = false;
    if (window.XMLHttpRequest) {//针对FireFox，Mozillar，Opera，Safari，IE7，IE8
        req = new XMLHttpRequest();
        if (req.overrideMimeType) { //针对某些特定版本的mozillar浏览器的BUG进行修正
            req.overrideMimeType("text/xml");
        }
    } else if (window.ActiveXObject) {
        //针对IE6，IE5.5，IE5, 两个可以用于创建XMLHTTPRequest对象的控件名称，保存在一个js的数组中排在前面的版本较新
        var activexName = ["MSXML2.XMLHTTP","Microsoft.XMLHTTP"];
        for (var i = 0; i < activexName.length; i++) {
            try{
                req = new ActiveXObject(activexName[i]);
                break;
            } catch(e){
                req = false;
            }
        }
    }
    return req;
}