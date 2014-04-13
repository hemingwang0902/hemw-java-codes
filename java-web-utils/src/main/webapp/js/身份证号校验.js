/**
 * 根据〖中华人民共和国国家标准 GB 11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
 * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码，最后一位是数字校验码。
 * 六位地址码（第1到6位）：第1到2位为省、第3到4位为市，第5到6位为县；
 * 八位出生日期码（第7到14位）：第7到10位为年、第11到12位为月，第13到14位为日；
 * 三位顺序码（第15到17位）：同一个地区（县）同一天出生的人的顺序号，奇数分配给男性，偶数分配给女性；
 * 最后一位（第18位）为数字校验码，是根据精密的计算公式计算出来的，校验码的具体计算步骤如下：
 *     1. 将身份证号码的第1到第17位数字分别乘以不同的系数，从第1位到第17位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2 ；
 *     2. 将这17位数字与系数相乘的结果相加；
 *     3. 将上一步的计算结果除以11，看余数是多少（求余）；
 *     4. 余数只可能是 0 1 2 3 4 5 6 7 8 9 10 这11个数字，其分别对应的最后一位身份证的号码为 1 0 X 9 8 7 6 5 4 3 2；
 *     5. 通过上面得知：如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ；如果余数是10，身份证的最后一位号码就是2。
 * 
 * 主要是两个通用方法：
 *     checkCard(string card) // 校验身份证号是否合法
 *     changeFivteenToEighteen(string card) // 将 15 位的身份证号转为 18 位的身份证号
 */

/**
 * 省份代码与名称的对应关系
 */
var vcity = {
	11 : "北京",
	12 : "天津",
	13 : "河北",
	14 : "山西",
	15 : "内蒙古",
	21 : "辽宁",
	22 : "吉林",
	23 : "黑龙江",
	31 : "上海",
	32 : "江苏",
	33 : "浙江",
	34 : "安徽",
	35 : "福建",
	36 : "江西",
	37 : "山东",
	41 : "河南",
	42 : "湖北",
	43 : "湖南",
	44 : "广东",
	45 : "广西",
	46 : "海南",
	50 : "重庆",
	51 : "四川",
	52 : "贵州",
	53 : "云南",
	54 : "西藏",
	61 : "陕西",
	62 : "甘肃",
	63 : "青海",
	64 : "宁夏",
	65 : "新疆",
	71 : "台湾",
	81 : "香港",
	82 : "澳门",
	91 : "国外"
};

/**
 * 检验身份证号码是否合法
 * @param card 要校验的身份证号码
 */
checkCard = function(card) {
	// 是否为空
	if (card === '') {
		alert('身份证号不能为空');
		return false;
	}
	
	// 校验长度，身份证号码为15位或者18位，15位时全为数字，18位前17位为数字，最后一位是校验位，可能为数字或字符 X
	if (!(/(^\d{15}$)|(^\d{17}(\d|X)$)/.test(card))) {
		alert('身份证号码格式不正确');
		return false;
	}
	
	// 检查省份
	if (!vcity[card.substr(0, 2)]) {
		alert('身份证号码的省份代码不正确');
		return false;
	}
	
	// 校验生日
	if (checkBirthday(card) === false) {
		alert('身份证号码的生日不正确');
		return false;
	}
	
	// 检验位的检测
	if (checkParity(card) === false) {
		alert('身份证号码的校验位不正确');
		return false;
	}
	
	return true;
};

// 检查生日是否正确
checkBirthday = function(card) {
	var len = card.length;
	// 身份证15位时，次序为省（3位）市（3位）年（2位）月（2位）日（2位）校验位（3位），皆为数字
	if (len == '15') {
		var re_fifteen = /^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/;
		var arr_data = card.match(re_fifteen);
		var year = arr_data[2];
		var month = arr_data[3];
		var day = arr_data[4];
		var birthday = new Date('19' + year + '/' + month + '/' + day);
		return verifyBirthday('19' + year, month, day, birthday);
	}
	
	// 身份证18位时，次序为省（3位）市（3位）年（4位）月（2位）日（2位）校验位（4位），校验位末尾可能为X
	if (len == '18') {
		var re_eighteen = /^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/;
		var arr_data = card.match(re_eighteen);
		var year = arr_data[2];
		var month = arr_data[3];
		var day = arr_data[4];
		var birthday = new Date(year + '/' + month + '/' + day);
		return verifyBirthday(year, month, day, birthday);
	}
	
	return false;
};

// 校验日期
verifyBirthday = function(year, month, day, birthday) {
	var now = new Date();
	var now_year = now.getFullYear();
	// 年月日是否合理
	if (birthday.getFullYear() == year && (birthday.getMonth() + 1) == month
			&& birthday.getDate() == day) {
		// 判断年份的范围（3岁到100岁之间)
		var time = now_year - year;
		if (time >= 3 && time <= 100) {
			return true;
		}
		return false;
	}
	return false;
};

// 校验位的检测
checkParity = function(card) {
	// 15位转18位
	card = changeFivteenToEighteen(card);
	
	if (card.length == '18') {
		var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
		var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
		var cardTemp = 0, i, valnum;
		for (i = 0; i < arrInt.length; i++) {
			cardTemp += (parseInt(card.substr(i, 1)) * arrInt[i]);
		}
		
		valnum = arrCh[cardTemp % 11];
		return (valnum == card.substr(17, 1));
	}
	
	return false;
};

/**
 * 将 15 位的身份证号转为 18 位的身份证号
 */
changeFivteenToEighteen = function(card) {
	if (card.length == '15') {
		var arrInt = new Array(7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2);
		var arrCh = new Array('1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2');
		var cardTemp = 0, i;
		card = card.substr(0, 6) + '19' + card.substr(6, card.length - 6);
		for (i = 0; i < arrInt.length; i++) {
			cardTemp += card.substr(i, 1) * arrInt[i];
		}
		card += arrCh[cardTemp % 11];
		return card;
	}
	return card;
};