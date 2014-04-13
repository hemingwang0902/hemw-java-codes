package com.hemw.regex;

import java.util.regex.Pattern;

public class RegexUtils {
    /** 中文汉字和中文标点 */
    public static final Pattern pattern_chinese = Pattern.compile("^[\u0391-\uFFE5]+$", Pattern.CASE_INSENSITIVE);
	/** 中文汉字 */
	public static final Pattern pattern_chinese_character = Pattern.compile("^[\u4E00-\u9FA5]+$", Pattern.CASE_INSENSITIVE);
	/** ascii字符 */
	public static final Pattern pattern_ascii = Pattern.compile("^[\\x00-\\xFF]+$", Pattern.CASE_INSENSITIVE);
	/** 不是字母、数字和汉字的字符 */
	public static final Pattern pattern_not_alnum_chinese = Pattern.compile("([^\\p{Alnum}\u4E00-\u9FA5])+");

	/** 数字，包括正数、负数、整数、小数等 */
	public static final Pattern pattern_number = Pattern.compile("^-?(?:\\d+|\\d{1,3}(?:,\\d{3})+)(?:\\.\\d+)?$");
	/** 整数 */
	public static final Pattern pattern_digits = Pattern.compile("^\\d+$");
    /** 小写字母 */
    public static final Pattern pattern_Lowercase_only = Pattern.compile("^[a-z]+$");
    /** 大写字母 */
    public static final Pattern pattern_Uppercase_only = Pattern.compile("^[A-Z]+$");
    /** 字母（大写 + 小写） */
    public static final Pattern pattern_letters_only = Pattern.compile("^[a-z]+$", Pattern.CASE_INSENSITIVE);
    /** 字母、数字、空格或下划线 */
    public static final Pattern pattern_alpha_numeric = Pattern.compile("^\\w+$", Pattern.CASE_INSENSITIVE);
	/** 字母、空白字符和常用标点（-.,()'"） */
	public static final Pattern pattern_letters_with_basic_punc = Pattern.compile("^[a-z-.,()'\"\\s]+$", Pattern.CASE_INSENSITIVE);
	/** 非空白字符 */
	public static final Pattern pattern_no_whitespace = Pattern.compile("^\\S+$");
	
	/** Email */
    public static final Pattern pattern_email = Pattern.compile("^((([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+(\\.([a-z]|\\d|[!#\\$%&'\\*\\+\\-\\/=\\?\\^_`{\\|}~]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])+)*)|((\\x22)((((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(([\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x7f]|\\x21|[\\x23-\\x5b]|[\\x5d-\\x7e]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(\\\\([\\x01-\\x09\\x0b\\x0c\\x0d-\\x7f]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF]))))*(((\\x20|\\x09)*(\\x0d\\x0a))?(\\x20|\\x09)+)?(\\x22)))@((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?$", Pattern.CASE_INSENSITIVE);
    /** Url */
    public static final Pattern pattern_url = Pattern.compile("^(https?|ftp):\\/\\/(((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:)*@)?(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]))|((([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|\\d|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.)+(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])*([a-z]|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])))\\.?)(:\\d*)?)(\\/((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)+(\\/(([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)*)*)?)?(\\?((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|[\\uE000-\\uF8FF]|\\/|\\?)*)?(\\#((([a-z]|\\d|-|\\.|_|~|[\\u00A0-\\uD7FF\\uF900-\\uFDCF\\uFDF0-\\uFFEF])|(%[\\da-f]{2})|[!\\$&'\\(\\)\\*\\+,;=]|:|@)|\\/|\\?)*)?$", Pattern.CASE_INSENSITIVE);
	
    /** 邮政编码 */
	public static final Pattern pattern_zip_code = Pattern.compile("^\\d{6}$");
	/** 身份证号码，15位或18位，只验证格式，不验证省市代号的有效性 */
	public static final Pattern pattern_id_card = Pattern.compile("^[1-9]\\d{13}[\\dx]|[1-9]\\d{16}[\\dx]$", Pattern.CASE_INSENSITIVE);
	
	/** 日期，如 2012-01-02、2012/01/02 */
    public static final Pattern pattern_date_ISO = Pattern.compile("^\\d{4}[\\/-]\\d{1,2}[\\/-]\\d{1,2}$");
	/** 时间，如 23:45 */
	public static final Pattern pattern_time = Pattern.compile("^([01][0-9])|(2[0123]):([0-5])([0-9])$");
	
	/** 电话号码，如 01085805678-091 */
	public static final Pattern pattern_phone = Pattern.compile("^(0?\\d{2,3}-?)?\\d{7,8}(-\\d{3,})?$");
	/** 电话号码（包括验证国内区号,国际区号,分机号） */
	public static final Pattern pattern_phone2 = Pattern.compile("^(([0\\+]\\d{2,3}-)?(0\\d{2,3})-)?(\\d{7,8})(-(\\d{3,}))?$");
	/** 手机号码（11位或以0开头的12位号码，不包括国际代号） */
	public static final Pattern pattern_mobile = Pattern.compile("^0?1[358]\\d{9}$");
	
	/** 颜色，如 #FF0000 */
	public static final Pattern pattern_colr = Pattern.compile("^#[a-f0-9]{6}$", Pattern.CASE_INSENSITIVE);
	
	/** IP v4 */
	public static final Pattern pattern_ip4 = Pattern.compile("^(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)\\.(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)$");
}
