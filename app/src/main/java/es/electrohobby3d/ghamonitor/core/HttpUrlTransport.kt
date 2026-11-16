// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - the real network: one HttpURLConnection per request, no redirects, a limit on the size of the answer.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpUrlTransport(private val api: ApiConfig) : Transport {
    @Throws(IOException::class)
    override fun send(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = api.connectTimeoutMs
            connection.readTimeout = api.readTimeoutMs
            connection.instanceFollowRedirects = false      // the token must never follow a redirect to another host
            connection.useCaches = false
            for ((name, value) in headers) connection.setRequestProperty(name, value)
            if (body != null) {
                connection.doOutput = true
                connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            }
            val status = connection.responseCode
            val stream = if (status >= 400) connection.errorStream else connection.inputStream
            val text = if (stream == null) "" else String(readLimited(stream, api.maxResponseBytes), Charsets.UTF_8)
            return HttpResult(status, text)
        } finally {
            connection.disconnect()
        }
    }

    companion object {
        /** Reads at most [limit] bytes; one more byte than that is an error (an answer that large is not from a monitor). */
        @Throws(IOException::class)
        fun readLimited(stream: InputStream, limit: Int): ByteArray {
            stream.use {
                val out = ByteArrayOutputStream()
                val buffer = ByteArray(8192)
                while (true) {
                    val n = it.read(buffer)
                    if (n < 0) break
                    if (out.size() + n > limit) throw IOException("answer too large")
                    out.write(buffer, 0, n)
                }
                return out.toByteArray()
            }
        }
    }
}
