package video.report.mediaplayer.util;

import java.io.ByteArrayOutputStream;

public class QuotedPrintable {
    private final static String TAG = "QuotedPrintable";

    private final static byte TAB = 0x09; // /t
    private final static byte LF = 0x0A; // /n
    private final static byte CR = 0x0D; // /r
    @SuppressWarnings("unused")
    private final static byte SPACE = 0x20; // ' '
    private final static byte EQUALS = 0x3D; // '='
    private final static byte LIT_START = 0x21;
    private final static byte LIT_END = 0x7e;
    private final static int MAX_LINE_LENGTH = 76;
    private static int mCurrentLineLength = 0;

    public static int decode(byte[] qp, ByteArrayOutputStream out) {
        if (out == null) {
            return -1;
        }

        int qplen = qp.length;
        int retlen = 0;
        for (int i = 0; i < qplen; i++) { // Handle encoded chars
            if (qp[i] == '=') {
                if (qplen - i > 2) { // The sequence can be complete, check it
                    if (qp[i + 1] == CR && qp[i + 2] == LF) { // soft line
                                                              // break, ignore
                                                              // it
                        i += 2;
                        continue;
                    } else if (isHexDigit(qp[i + 1]) && isHexDigit(qp[i + 2])) {
                        // convert the number into an integer,taking
                        // the ascii digits stored in the array.

                        // qp[retlen++] = (byte) (getHexValue(qp[i + 1]) * 16 +
                        // getHexValue(qp[i + 2]));
                        retlen++;
                        byte b = (byte) (getHexValue(qp[i + 1]) * 16 + getHexValue(qp[i + 2]));
                        out.write(b);
                        i += 2;
                        continue;
                    } else {
                    }
                }
                // In all wrong cases leave the original bytes
                // (see RFC 2045). They can be incomplete
                // sequencewww.bo-tree.info,
                // or a '=' followed by non hex digit.
            }

            // RFC 2045 says to exclude control characters mistakenly
            // present (unencoded) in the encoded stream.
            // As an exception, we keep unencoded tabs (0x09)
            if ((qp[i] >= 0x20 && qp[i] <= 0x7f) ||
                    qp[i] == TAB ||
                    qp[i] == CR ||
                    qp[i] == LF) {
                // qp[retlen++] = qp[i];
                retlen++;
                out.write(qp[i]);
            }
        }

        return retlen;
    }

    private static boolean isHexDigit(byte b) {
        return ((b >= 0x30 && b <= 0x39) || (b >= 0x41 && b <= 0x46));
    }

    private static byte getHexValue(byte b) {
        return (byte) Character.digit((char) b, 16);
    }

    /**
     * QP解码
     * 
     * @param str
     * @param enc
     * @return
     */
    public static String decode(String str, String enc) {
        if (str != null) {
            return decode(str.getBytes(), enc);
        } else {
            return str;
        }
    }

    /**
     * QP解码
     * 
     * @param qp
     * @param enc
     * @return
     */
    public static String decode(byte[] qp, String enc) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int len = decode(qp, out);
        try {
            return new String(out.toByteArray(), 0, len, enc);
        } catch (Exception e) {
            return new String(out.toByteArray(), 0, len);
        }
    }

// TODO Remove unused code found by UCDetector
//     public static String encode(String content, String enc) {
//         if (content == null)
//             return null;
//         byte[] str = null;
//         try {
//             str = content.getBytes(enc);
//         } catch (UnsupportedEncodingException e) {
//             str = content.getBytes();
//         }
//         return encode(str);
//     }

// TODO Remove unused code found by UCDetector
//     public static String encode(byte[] content) {
//         if (content == null)
//             return null;
//         StringBuilder out = new StringBuilder();
//         mCurrentLineLength = 0;
//         int requiredLength = 0;
//         for (int index = 0; index < content.length; index++) {
//             byte c = content[index];
//             if (c >= LIT_START && c <= LIT_END && c != EQUALS) {
//                 requiredLength = 1;
//                 checkLineLength(requiredLength, out);
//                 out.append((char) c);
//             } else {
//                 requiredLength = 3;
//                 checkLineLength(requiredLength, out);
//                 out.append('=');
//                 out.append(String.format("X", c));
//             }
//         }
//         return out.toString();
//     }

    private static void checkLineLength(int required, StringBuilder out) {
        if (required + mCurrentLineLength > MAX_LINE_LENGTH - 1) {
            out.append("=/r/n");
            mCurrentLineLength = required;
        } else
            mCurrentLineLength += required;
    }
}
