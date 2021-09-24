
package video.report.mediaplayer.util;

import android.text.TextUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一些解码类的集合
 * 
 */
public class DecodeUtils {

    private static final String TAG = DecodeUtils.class.getSimpleName();

    public static final String CHARSET_UNKNOWN = "unknown";
    public static final String CHARSET_GB2312 = "GB2312";
    public static final String CHARSET_UTF8 = "UTF-8";

    public enum Encoding {
        /** Base64编码 */
        BASE64,
        /** QuotedPrintable编码 */
        QP
    }

    /**
     * Mime解码的查找表元素
     */
    private static class MimeDecodeLookup {
        /** 字符串索引 */
        public String indexStr;
        /** 对应的编码方式 */
        public Encoding encoding;
        /** 对应的字符集 */
        public String charset;

        public MimeDecodeLookup(String indexStr, Encoding encoding, String charset) {
            this.indexStr = indexStr;
            this.encoding = encoding;
            this.charset = charset;
        }
    }

    /** 查找表 */
    private static ArrayList<MimeDecodeLookup> mMimeDecodeLookupTable = new ArrayList<MimeDecodeLookup>();
    static {
        // 初始化查找表
        mMimeDecodeLookupTable
                .add(new MimeDecodeLookup("=?utf8?b?", Encoding.BASE64, CHARSET_UTF8));
        mMimeDecodeLookupTable.add(new MimeDecodeLookup("=?utf8?q?", Encoding.QP, CHARSET_UTF8));
        mMimeDecodeLookupTable
                .add(new MimeDecodeLookup("=?utf-8?b?", Encoding.BASE64, CHARSET_UTF8));
        mMimeDecodeLookupTable.add(new MimeDecodeLookup("=?utf-8?q?", Encoding.QP, CHARSET_UTF8));
        mMimeDecodeLookupTable.add(new MimeDecodeLookup("=?gb2312?b?", Encoding.BASE64,
                CHARSET_GB2312));
        mMimeDecodeLookupTable
                .add(new MimeDecodeLookup("=?gb2312?q?", Encoding.QP, CHARSET_GB2312));
    }

    /** 网站与对应的编码方式列表 */
    private static String[][] mUrlList = {
            {
                    "preview.mail.163.com", CHARSET_UTF8
            }, // 163预览
            {
                    "preview.mail.126.com", CHARSET_UTF8
            }, // 126预览
            {
                    "mm.mail.163.com", CHARSET_UTF8
            }, // WAP版163
            {
                    "mm.mail.126.com", CHARSET_UTF8
            }, // WAP版126
            {
                    "mail.163.com", CHARSET_GB2312
            }, // 163其他
            {
                    "mail.126.com", CHARSET_GB2312
            }
            // 126其他
    };

    /**
     * 若输入字符串是Mime格式（Multipurpose Internet Mail Extensions）编码，则返回解码后的字符串
     * 
     * @param str mime编码的字符串，格式定义如下:<br />
     *            =?charset?encoding?data?possibly repeated?=
     * @return
     */
    public static String mimeDecode(String str) {
        String ret = str;

        if (!TextUtils.isEmpty(str)) {
            String lowercaseString = str.toLowerCase();

            // 判断是否以?=结尾
            int end_pos = lowercaseString.indexOf("?=");
            if (end_pos > 0) {
                // 遍历查找表，匹配字符串索引
                for (MimeDecodeLookup lookup : mMimeDecodeLookupTable) {
                    String indexStr = lookup.indexStr;

                    int start_pos = lowercaseString.indexOf(indexStr);
                    // 若能匹配上，则进行解码
                    if ((start_pos >= 0) &&
                            (start_pos + indexStr.length() < end_pos)) {
                        try {
                            // 截取出正文部分
                            String content_str = str.substring(start_pos + indexStr.length(),
                                    end_pos);
                            // 进行解码
                            content_str = decode(content_str, lookup.encoding, lookup.charset);
                            // 将解码后的字符串拼接上编码以外的字符部分返回
                            ret = str.substring(0, start_pos) + content_str
                                    + str.substring(end_pos + 2);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * 根据字符集以及编码方式进行解码
     * 
     * @param str
     * @param enc
     * @param charset
     * @return
     */
    public static String decode(String str, Encoding enc, String charset) {
        String ret = str;

        if (enc == Encoding.BASE64) {
            ret = base64Decode(str, charset);
        } else if (enc == Encoding.QP) {
            ret = QuotedPrintable.decode(str, charset);
        } else {
            // ...
        }

        return ret;
    }

    /**
     * 进行Base64解码
     * 
     * @param str
     * @param charset
     * @return
     */
    public static String base64Decode(String str, String charset) {

        String ret = str;

        if (str != null) {
            try {
                byte[] tmp = Base64.decode(str.getBytes());
                ret = new String(tmp, charset);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ret;
    }

    /**
     * 根据网站的url判断其下载文件名的编码方式
     * 
     * @param url
     * @return 对应的编码方式,如果找不到则为CHARSET_UNKNOWN
     */
    public static String getCharsetByUrl(String url) {
        String charset = CHARSET_UNKNOWN;
        if (TextUtils.isEmpty(url)) {
            return charset;
        }

        for (int i = 0; i < mUrlList.length; i++) {
            if (url.indexOf(mUrlList[i][0]) >= 0) {
                charset = mUrlList[i][1];
                break;
            }
        }

        return charset;
    }

    /**
     * 某个网站的下载文件名是否是GB2312编码
     * 
     * @param url
     * @return true - 是, false - 不是
     */
    public static boolean isGB2312Url(String url) {
        String charset = getCharsetByUrl(url);
        if (charset != null && charset.equals(CHARSET_GB2312)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 将GB2312或者UTF-8的原始编码字符串进行解码
     * 
     * @param srcString 编码字符串
     * @param Gb2312 指定编码格式，true代表GB2312，false代表UTF-8
     * @return
     */
    public static String rawDecode(String srcString, boolean Gb2312) {
        String ret = srcString;

        if (srcString != null) {
            byte[] b = srcString.getBytes();
            if (b == null || b.length < 1) {
                ret = srcString;
            } else {
                byte[] c = new byte[b.length];
                int pos = 0;
                for (int i = 0; i < b.length; i++) {
                    if (b[i] >= 0) {
                        c[pos] = b[i];
                        pos++;
                    } else if ((i + 1) < b.length) {
                        c[pos] = (byte) ((b[i] & 0x03) << 6 | (b[i + 1] & 0x3f));
                        pos++;
                        i++;
                    }
                }

                byte[] d = new byte[pos];
                for (int i = 0; i < pos; i++) {
                    d[i] = c[i];
                }

                try {
                    if (Gb2312) {
                        ret = new String(d, CHARSET_GB2312);
                    } else {
                        ret = new String(d, CHARSET_UTF8);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ret = "";
                }
            }
        }

        return ret;
    }

    /**
     * 从disposition中恢复出解码后的字符串
     * 
     * @param srcString 编码字符串
     * @param Gb2312 指定编码格式，true代表GB2312，false代表UTF-8
     * @param rawDecode 是否使用rawDecode
     * @return 解码后的字符串
     */
    public static String recoverString(String srcString, boolean Gb2312, boolean rawDecode) {
        String ret = srcString;
        if (TextUtils.isEmpty(srcString)) {
            return ret;
        }

        // 1. mime decode
        ret = DecodeUtils.mimeDecode(srcString);
        if (!ret.equals(srcString)) {
            // 若解码后的字符串与解码前不同，则认为解码成功
            return ret;
        }

        // 2. URL decode
        try {
            if (Gb2312) {
                ret = URLDecoder.decode(srcString, CHARSET_GB2312);
            } else {
                ret = URLDecoder.decode(srcString, CHARSET_UTF8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!ret.equals(srcString)) {
            // 若解码后的字符串与解码前不同，则认为解码成功
            return ret;
        }

        // 3. raw decode
        if (rawDecode) {
            ret = rawDecode(srcString, Gb2312);
        }

        return ret;
    }

   /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        if(strName == null || strName.equals(""))
        {
            return false;
        }
        Pattern p = Pattern.compile("\\s*|t*|r*|n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }

    }
    //使用这个智能URLDecode
    public static String URLDecode(String url,String defEncode)
    {
        String rst = "";
        if(defEncode != null && !defEncode.equals(""))
        {
            try
            {
                rst = URLDecoder.decode(url,defEncode);
            }
            catch (Exception e)
            {
                rst = "";
            }
        }

        if(rst.equals("") || isMessyCode(rst))
        {
            for(String type :mURLDecodeType)
            {
                if(!type.equals(defEncode))
                {
                    try
                    {
                        rst = URLDecoder.decode(url,type);
                    }
                    catch (Exception e)
                    {
                        rst = "";
                    }
                    if(!rst.equals("") && !isMessyCode(rst))
                    {
                        return rst;
                    }
                }
            }
        }

        return rst;
    }
    private static String[] mURLDecodeType = {CHARSET_UTF8,CHARSET_GB2312};
}
