package video.report.mediaplayer.util;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileNameUtil {
    
    /**下载文件的默认名字*/
    public static final String DEFAULT_FILENAME = "downloadfile";

    /**
     * Guesses canonical filename that a download would have, using
     * the URL and contentDisposition. File extension, if not defined,
     * is added based on the mimetype
     * @param url Url to the content
     * @param contentDisposition Content-Disposition HTTP header or null
     * @param mimeType Mime-type of the content or null
     * @param appendExtIfNeed append extension when the original filename doesn't have one
     * @return suggested filename
     */
    public static final String guessFileName(
            String url,
            String contentDisposition,
            String mimeType,
            boolean appendExtIfNeed) {
        String filename = null;
        String extension = null;

        // If we couldn't do anything with the hint, move toward the content disposition
        if (filename == null && contentDisposition != null) {
            filename = parseContentDisposition(contentDisposition);
            if (filename != null) {
                int index = filename.lastIndexOf('/') + 1;
                if (index > 0) {
                    filename = filename.substring(index);
                }
            }
        }
        if(DecodeUtils.isMessyCode(filename))
        {
            filename = null;
        }

        // If all the other http-related approaches failed, use the plain uri
        if (filename == null) {
            String decodedUrl = DecodeUtils.URLDecode(url,null);
            if (decodedUrl != null) {
                int queryIndex = decodedUrl.indexOf('?');
                // If there is a query string strip it, same as desktop browsers
                if (queryIndex > 0) {
                    decodedUrl = decodedUrl.substring(0, queryIndex);
                }
                if (!decodedUrl.endsWith("/")) {
                    int index = decodedUrl.lastIndexOf('/') + 1;
                    if (index > 0) {
                        filename = decodedUrl.substring(index);
                        int dotIndex = filename.indexOf('.');
                        if (dotIndex < 0 && appendExtIfNeed) {

                        }
                    }
                }
            }
        }

        // Finally, if couldn't get filename from URI, get a generic filename
        if (filename == null) {
            filename = DEFAULT_FILENAME;
        }

        // Split filename between base and extension
        // Add an extension if filename does not have one
        int dotIndex = filename.indexOf('.');
        if (dotIndex < 0 && appendExtIfNeed) {
            if (!TextUtils.isEmpty(mimeType)) {
                extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (extension != null) {
                    extension = "" + extension;
                }
            }
            if (extension == null) {
                if (!TextUtils.isEmpty(mimeType) && mimeType.toLowerCase().startsWith("text/")) {
                    if (mimeType.equalsIgnoreCase("text/html")) {
                        extension = ".html";
                    } else {
                        extension = ".txt";
                    }
                } else {
                    extension = "";
                }
            }
            return manipulateFileNameLengthIfTooLong(filename, extension.getBytes().length) + extension;
        } else {
            return manipulateFileNameLengthIfTooLong(filename, 0);// do not change file's ext when it has a ext.
//            if (mimeType != null) {
//                // Compare the last segment of the extension against the mime type.
//                // If there's a mismatch, discard the entire extension.
//                int lastDotIndex = filename.lastIndexOf('.');
//                String typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
//                        filename.substring(lastDotIndex + 1));
//                if (typeFromExt != null && !typeFromExt.equalsIgnoreCase(mimeType)) {
//                    extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
//                    if (extension != null) {
//                        extension = "." + extension;
//                    }
//                }
//            }
//            if (extension == null) {
//                extension = filename.substring(dotIndex);
//            }
//            filename = filename.substring(0, dotIndex);
        }

//        return filename + extension;
    }
    
    /** Regex used to parse content-disposition headers */
    /* the content disposition from google drive is something like:
     *     attachment;filename="bmp.jpg";filename*=UTF-8''bmp.jpg
     * so do not try to match the end of string
     */
    private static final Pattern CONTENT_DISPOSITION_PATTERN =
            Pattern.compile("[attachment|inline];\\s*filename\\s*=\\s*(\"?)([^\"]*)\\1\\s*;?",
            Pattern.CASE_INSENSITIVE);

    /*
     * Parse the Content-Disposition HTTP Header. The format of the header
     * is defined here: http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html
     * This header provides a filename for content that is going to be
     * downloaded to the file system. We only support the attachment type.
     * Note that RFC 2616 specifies the filename value must be double-quoted.
     * Unfortunately some servers do not quote the value so to maintain
     * consistent behaviour with other browsers, we allow unquoted values too.
     */
    public static String parseContentDisposition(String contentDisposition) {
        try {
            Matcher m = CONTENT_DISPOSITION_PATTERN.matcher(contentDisposition);
            if (m.find()) {
                return m.group(2);
            }
        } catch (IllegalStateException ex) {
             // This function is defined as returning null when it can't parse the header
        }
        return null;
    }

    /*
     * Linux Ext2/Ext3 file system restrict maximum file name length is 255,
     * CM download will add suffix ".downloadpart"(11byte) as download-transition file
     */
    public static int MAX_LEN_FILENAME = 244;
    public static String manipulateFileNameLengthIfTooLong(String fileName, int additionInBytes) {
        if (fileName != null && fileName.getBytes().length > (MAX_LEN_FILENAME - additionInBytes)) {
            String manupilatedFileName = "";
            int total = 0;
            int end = -1;
            int exInBytes;
            String ex;
            int sep = fileName.indexOf('.');
            if (sep == -1) {
                exInBytes = 0;
                ex = "";
            } else {
                ex = fileName.subSequence(sep, fileName.length()).toString();
                exInBytes = ex.getBytes().length;
            }
            for (int i=0;i<fileName.length();i++) {
                total =  total + fileName.substring(i, i+1).getBytes().length;
                if (total == (MAX_LEN_FILENAME - additionInBytes - exInBytes)) {
                    end = i;
                    break;
                } else if (total > (MAX_LEN_FILENAME - additionInBytes - exInBytes)) {
                    end = i - 1;
                    break;
                }
            }
            if (end >= 0) {
                manupilatedFileName = fileName.substring(0, end + 1) + ex;
            }
            return manupilatedFileName;
        }
        return fileName;
    }

}
