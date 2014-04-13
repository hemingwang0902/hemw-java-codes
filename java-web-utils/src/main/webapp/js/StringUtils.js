/**获取字符串的字节数*/
String.prototype.getByteCount = function(){
	return this.replace(/[^\x00-\xff]/g,"xx").length;
}

/**去掉字符串的前后空格*/
String.prototype.trim = function(){
    return this.replace(/(^\s*)|(\s*$)/g, "");
}

/** 判断一个字符串是否指定字符串开始 */
String.prototype.startWith = function(str){  
    return new RegExp("^" + str).test(this);     
}

/** 判断一个字符串是否指定字符串结尾 */
String.prototype.endWith = function(oString){  
    return new RegExp(oString+"$").test(this);     
}

/** 判断一个字符串是不是空字符串 */
String.prototype.isEmpty = function() {
    return (this == "") || (this.match(/^\s+$/));
}

/** 取字符串左边的 N 位 */
String.prototype.left = function(n) {
    if(n > this.length)
    	n = this.length;
    return this.substring(0, n);
}

/** 取字符串右边的 N 位 */
String.prototype.right = function(n) {
    if(n > this.length)
    	n = this.length;
    return this.substring(this.length - n, this.length);
}

/**将String类型解析为Date类型.*/
String.prototype.toDate = function(){
	/*yyyy-MM-dd*/
	var results = str.match(/^ *(\d{2,4})-(\d{1,2})-(\d{1,2}) *$/);
	if (results && results.length > 3) {
		return new Date(results[1], results[2] - 1, results[3]);
	}
	/*yyyy-MM-dd HH:mm*/
	results = str.match(/^ *(\d{2,4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}) *$/);
	if (results && results.length > 5) {
		return new Date(results[1], results[2] - 1, results[3], results[4], results[5]);
	}
	/*yyyy-MM-dd HH:mm:ss*/
	results = str.match(/^ *(\d{2,4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2}) *$/);
	if (results && results.length > 6) {
		return new Date(results[1], results[2] - 1, results[3], results[4], results[5], results[6]);
	}
	/*yyyy-MM-dd HH:mm:ss.SSS*/
	results = str.match(/^ *(\d{2,4})-(\d{1,2})-(\d{1,2}) +(\d{1,2}):(\d{1,2}):(\d{1,2})\.(\d{1,3}) *$/);
	if (results && results.length > 7) {
		return new Date(results[1], results[2] - 1, results[3], results[4], results[5], results[6], results[7]);
	}
	return null;
}
