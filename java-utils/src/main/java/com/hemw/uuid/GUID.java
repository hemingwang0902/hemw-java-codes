package com.hemw.uuid;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

/**
  *   RandomGUID 
  *   @version   1.2   01/29/02 
  *   @author   Marc   A.   Mnich 
  * 
  *   From   www.JavaExchange.com,   Open   Software   licensing 
  * 
  *   01/29/02   --   Bug   fix:   Improper   seeding   of   nonsecure   Random   object 
  *                           caused   duplicate   GUIDs   to   be   produced.     Random   object 
  *                           is   now   only   created   once   per   JVM. 
  *   01/19/02   --   Modified   random   seeding   and   added   new   constructor 
  *                           to   allow   secure   random   feature. 
  *   01/14/02   --   Added   random   function   seeding   with   JVM   run   time 
  *   In   the   multitude   of   java   GUID   generators,   I   found   none   that 
  *   guaranteed   randomness.     GUIDs   are   guaranteed   to   be   globally   unique 
  *   by   using   ethernet   MACs,   IP   addresses,   time   elements,   and   sequential 
  *   numbers.     GUIDs   are   not   expected   to   be   random   and   most   often   are   
  *   easy/possible   to   guess   given   a   sample   from   a   given   generator.     
  *   SQL   Server,   for   example   generates   GUID   that   are   unique   but   
  *   sequencial   within   a   given   instance. 
  * 
  *   GUIDs   can   be   used   as   security   devices   to   hide   things   such   as   
  *   files   within   a   filesystem   where   listings   are   unavailable   (e.g.   files 
  *   that   are   served   up   from   a   Web   server   with   indexing   turned   off). 
  *   This   may   be   desireable   in   cases   where   standard   authentication   is   not 
  *   appropriate.   In   this   scenario,   the   RandomGUIDs   are   used   as   directories. 
  *   Another   example   is   the   use   of   GUIDs   for   primary   keys   in   a   database 
  *   where   you   want   to   ensure   that   the   keys   are   secret.     Random   GUIDs   can 
  *   then   be   used   in   a   URL   to   prevent   hackers   (or   users)   from   accessing 
  *   records   by   guessing   or   simply   by   incrementing   sequential   numbers. 
  * 
  *   There   are   many   other   possiblities   of   using   GUIDs   in   the   realm   of 
  *   security   and   encryption   where   the   element   of   randomness   is   important. 
  *   This   class   was   written   for   these   purposes   but   can   also   be   used   as   a 
  *   general   purpose   GUID   generator   as   well. 
  * 
  *   RandomGUID   generates   truly   random   GUIDs   by   using   the   system 's   
  *   IP   address   (name/IP),   system   time   in   milliseconds   (as   an   integer),   
  *   and   a   very   large   random   number   joined   together   in   a   single   String   
  *   that   is   passed   through   an   MD5   hash.     The   IP   address   and   system   time   
  *   make   the   MD5   seed   globally   unique   and   the   random   number   guarantees   
  *   that   the   generated   GUIDs   will   have   no   discernable   pattern   and   
  *   cannot   be   guessed   given   any   number   of   previously   generated   GUIDs.     
  *   It   is   generally   not   possible   to   access   the   seed   information   (IP,   time,   
  *   random   number)   from   the   resulting   GUIDs   as   the   MD5   hash   algorithm   
  *   provides   one   way   encryption. 
  * 
  *   ---->   Security   of   RandomGUID:   <----- 
  *   RandomGUID   can   be   called   one   of   two   ways   --   with   the   basic   java   Random
  *   number   generator   or   a   cryptographically   strong   random   generator   
  *   (SecureRandom).     The   choice   is   offered   because   the   secure   random 
  *   generator   takes   about   3.5   times   longer   to   generate   its   random   numbers 
  *   and   this   performance   hit   may   not   be   worth   the   added   security   
  *   especially   considering   the   basic   generator   is   seeded   with   a   
  *   cryptographically   strong   random   seed. 
  * 
  *   Seeding   the   basic   generator   in   this   way   effectively   decouples 
  *   the   random   numbers   from   the   time   component   making   it   virtually   impossible 
  *   to   predict   the   random   number   component   even   if   one   had   absolute   knowledge 
  *   of   the   System   time.     Thanks   to   Ashutosh   Narhari   for   the   suggestion 
  *   of   using   the   static   method   to   prime   the   basic   random   generator. 
  * 
  *   Using   the   secure   random   option,   this   class   compies   with   the   statistical 
  *   random   number   generator   tests   specified   in   FIPS   140-2,   Security 
  *   Requirements   for   Cryptographic   Modules,   secition   4.9.1. 
  * 
  *   I   converted   all   the   pieces   of   the   seed   to   a   String   before   handing 
  *   it   over   to   the   MD5   hash   so   that   you   could   print   it   out   to   make 
  *   sure   it   contains   the   data   you   expect   to   see   and   to   give   a   nice 
  *   warm   fuzzy.     If   you   need   better   performance,   you   may   want   to   stick 
  *   to   byte[]   arrays. 
  * 
  *   I   believe   that   it   is   important   that   the   algorithm   for   
  *   generating   random   GUIDs   be   open   for   inspection   and   modification. 
  *   This   class   is   free   for   all   uses. 
 * UUID是指在一台机器上生成的数字，它保证对在同一时空中的所有机器都是唯一的。通常平台会提供生成UUID的API。UUID按照开放软件基金会(OSF)
 * 制定的标准计算，用到了以太网卡地址、纳秒级时间、芯片ID码和许多可能的数字。由以下几部分的组合：当前日期和时间(UUID的第一个部分与时间有关，
 * 如果你在生成一个UUID之后，过几秒又生成一个UUID，则第一个部分不同，其余相同)，时钟序列，全局唯一的IEEE机器识别号
 * （如果有网卡，从网卡获得，没有网卡以其他方式获得），
 * UUID的唯一缺陷在于生成的结果串会比较长。关于UUID这个标准使用最普遍的是微软的GUID(Globals Unique Identifiers)。
 * 
 * @author <a href="mailto:hemingwang0902@126.com">何明旺</a>
 */
public class GUID {
	private static SecureRandom secureRandom = new SecureRandom();;
	private static final int PAD_BELOW = 0x10;
	private static final int TWO_BYTES = 0xFF;

    private GUID() {
    }

    public static String getRandomGUID() {
        return getRandomGUID(false);
    }

    public static String getRandomGUID(boolean security) {
        String ip = null;
        try {
            ip = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            ip = "127.0.0.1";
        }
        long millis = System.currentTimeMillis();
        long randomNumber = security ? secureRandom.nextLong() : new Random(secureRandom.nextLong()).nextLong();

        String key = ip + ":" + millis + ":" + randomNumber;
        String raw = cryptoByMD5(key).toUpperCase();
        
        StringBuffer sb = new StringBuffer(64);
        sb.append(raw.substring(0, 8));
        sb.append("-");
        sb.append(raw.substring(8, 12));
        sb.append("-");
        sb.append(raw.substring(12, 16));
        sb.append("-");
        sb.append(raw.substring(16, 20));
        sb.append("-");
        sb.append(raw.substring(20));
        return sb.toString();
    }

    public static String cryptoByMD5(String key) {
        return crypto("MD5", key);
    }

	/**
	 * 根据指定算法进行数据加密
	 * @param algorithm 算法，如： SHA1withDSA、SHA-1、MD5、DES、DES3
	 * @param key 要进行加密的数据
	 * @return 加密后的数据
	 */
    public static String crypto(String algorithm, String key) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("系统不支持 MD5 加密算法");
        }

        md5.update(key.getBytes());
        byte[] array = md5.digest();
        StringBuffer sb = new StringBuffer(32);
        for (int j = 0; j < array.length; ++j) {
            int b = array[j] & TWO_BYTES;
            if (b < PAD_BELOW)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }


	// Demonstraton and self test of class
	public static void main(String args[]) {
	    System.out.println(GUID.getRandomGUID());
	}

}
