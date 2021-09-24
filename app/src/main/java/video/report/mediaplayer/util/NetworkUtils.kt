package video.report.mediaplayer.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import android.util.Log
import android.webkit.CookieManager
import video.report.mediaplayer.MyApplication
import video.report.mediaplayer.firebase.FireBaseEventUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.net.URLConnection
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

object NetworkUtils {

    @Throws(IOException::class)
    private fun readFromStream(inputStream: InputStream): String {
        var read: Int
        val bArr = ByteArray(32768)
        val byteArrayOutputStream = ByteArrayOutputStream()
        do {
            read = inputStream.read(bArr)
            if (read > 0) {
                byteArrayOutputStream.write(bArr, 0, read)
                continue
            }
        } while (read >= 0)
        return String(byteArrayOutputStream.toByteArray())
    }

    fun fetchUrlContent(url: String?, ua: String? = null, cookie: String? = null): Pair<Int, String?> {

        val v5: URL
        if (TextUtils.isEmpty(url)) {
            return Pair(-1, null)
        }

        var connection: URLConnection? = null
        try {
            v5 = URL(url)
            connection = v5.openConnection()
            if ("https" == v5.protocol) {
                (connection as HttpsURLConnection).hostnameVerifier = HostnameVerifier { arg2, arg3 -> true }
                trustAllHosts((connection as HttpsURLConnection?)!!)
            }

            if (!TextUtils.isEmpty(ua)) {
                connection!!.addRequestProperty("User-Agent", ua)
            }

            try {
                var v1 = CookieManager.getInstance().getCookie(url)
                if (!TextUtils.isEmpty(cookie)) {
                    v1 = "$v1; $cookie"
                    FireBaseEventUtils.getInstance().report("new_policy_check")
                }
                connection!!.addRequestProperty("Cookie", v1)
                CookieManager.getInstance().setAcceptCookie(true)
                connection.connectTimeout = 10000
                connection.connect()
            }catch (e:Exception){
                // WebViewFactory Exception
            }
            if (connection == null) {return Pair(-1, null)}
            val code = (connection as HttpURLConnection).responseCode
            return when {
                code == 200 -> Pair(code, readFromStream(connection.getInputStream()))
                connection.errorStream != null -> Pair(code, readFromStream(connection.errorStream))
                else -> Pair(code, "")
            }
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (connection != null)
                (connection as HttpURLConnection).disconnect()
        }
        return Pair(-1, null)
    }

    fun trustAllHosts(httpsURLConnection: HttpsURLConnection) {
        httpsURLConnection.hostnameVerifier = HostnameVerifier { str, sSLSession -> true }
        try {
            val trustManagerArr = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(x509CertificateArr: Array<X509Certificate>, str: String) {
                    Log.i("trustAllHosts", "checkClientTrusted")
                }

                override fun checkServerTrusted(x509CertificateArr: Array<X509Certificate>, str: String) {
                    Log.i("trustAllHosts", "checkServerTrusted")
                }

                override fun getAcceptedIssuers(): Array<X509Certificate?> {
                    return arrayOfNulls(0)
                }
            })
            try {
                val instance = SSLContext.getInstance("TLS")
                instance.init(null, trustManagerArr, SecureRandom())
                httpsURLConnection.sslSocketFactory = instance.socketFactory
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e2: Exception) {
            e2.printStackTrace()
        }

    }

    fun removeQueryPart(url: String?): String? {
        return if (url?.contains("?") == true)
            url.substring(0, url.indexOf("?"))
        else url
    }

    fun isNetworkConnected(): Boolean {
        return isNetworkConnected(MyApplication.instance)
    }

    private fun isNetworkConnected(context: Context): Boolean {
        return hasConnectedNetwork(context)
    }

    private fun hasConnectedNetwork(context: Context): Boolean {
        val info = getActiveNetworkInfo(context)
        return info?.isConnected == true
    }

    private fun getActiveNetworkInfo(context: Context?): NetworkInfo? {
        context?.let {
            val cm = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
            return cm?.activeNetworkInfo
        }
        return null
    }
}

