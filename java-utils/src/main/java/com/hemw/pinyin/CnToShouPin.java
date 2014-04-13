package com.hemw.pinyin;

/**
 * 提取汉字拼音首字母<br>
 * 注意：不支持多音字处理 <br>
 * <b>创建日期</b>：2011-3-2
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class CnToShouPin {
	// 简体中文的编码范围从B0A1（45217）一直到F7FE（63486）
	private static int BEGIN = 45217;
	private static int END = 63486;

	// 按照声母表示，这个表是在GB2312中的出现的第一个汉字，也就是说“啊”是代表首字母a的第一个汉字。 i, u, v都不做声母, 自定规则跟随前面的字母
	private static char[] charTable = { '啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期', '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', };
	// 对应首字母区间表
    private static char[] initialTable = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'h', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 't', 't', 'w', 'x', 'y', 'z', };
	// 二十六个字母区间对应二十七个端点，GB2312码汉字区间十进制表示
	private static int[] table = new int[27];

	// 初始化
	static {
		for (int i = 0; i < charTable.length; i++) {
			table[i] = gbValue(charTable[i]); // 得到GB2312码的首字母区间端点表，十进制。
		}
		table[26] = END;// 区间表结尾
	}

	// ------------------------public方法区------------------------
	/**
	 * 返回汉字的首拼，英文字母返回对应的大写字母，其他非简体汉字返回 '0'<br>
	 * 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串 最重要的一个方法，思路如下：一个个字符读入、判断、输出
	 * @param SourceStr 汉字
	 * @return 首拼
	 */
	public static String cn2ShouPin(String SourceStr) {
		String Result = "";
		for (int i = 0; i < SourceStr.length(); i++) {
            Result += Char2Initial(SourceStr.charAt(i));
        }
		return Result;
	}

	// ------------------------private方法区------------------------
	/**
	 * 获取字符的声母，若为英文，则返回其对应的大写字母，其他非简体汉字返回 '0'
	 */
	private static char Char2Initial(char ch) {
		// 对英文字母的处理：小写字母转换为大写，大写的直接返回
		if (ch >= 'a' && ch <= 'z')
			return (char) (ch - 'a' + 'A');
		if (ch >= 'A' && ch <= 'Z')
			return ch;

		// 获取简体中文字符的 GB2312 编码值
		int gb = gbValue(ch);// 汉字转换首字母
		if ((gb < BEGIN) || (gb >= END)) // 如果编码值不在范围之内，则直接返回 0
			return '0';

        for (int i = 0; i < table.length - 1; i++) {
            // 判断匹配码表区间，匹配到就直接返回，判断区间形如“[,)”
            if ((gb >= table[i]) && (gb < table[i + 1]))
                return initialTable[i];
        }

        return '0';
	}

	/**
	 * 获取简体中文字符的 GB2312 编码值
	 * @param cn 汉字
	 * @return 如果传入的字符不是简体中文，则返回 0
	 */
	private static int gbValue(char ch) {
		String str = String.valueOf(ch);
        try {
            byte[] bytes = str.getBytes("GB2312");
            if (bytes.length < 2)
                return 0;
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
	}
}
