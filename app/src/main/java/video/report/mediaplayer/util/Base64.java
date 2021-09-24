package video.report.mediaplayer.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//=============================================================================
//                                 CLASS DEFINITIONS
//=============================================================================

public class Base64 {

    public static String encode(byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder();

        try {
            encoder.encode(data, 0, data.length, bOut);
        } catch (IOException e) {
            throw new RuntimeException("exception encoding base64 string: " + e);
        }

        return bOut.toString();
    }

    public static byte[] decode(byte[] data) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        Base64Encoder encoder = new Base64Encoder();

        try {
            encoder.decode(data, 0, data.length, bOut);
        } catch (IOException e) {
            throw new RuntimeException("exception decoding base64 string: " + e);
        }

        return bOut.toByteArray();
    }

}
