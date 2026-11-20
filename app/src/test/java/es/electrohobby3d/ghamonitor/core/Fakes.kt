// Test doubles shared by the unit tests: a network that records what was asked, a store in memory and a box that "seals" by reversing the text.
package es.electrohobby3d.ghamonitor.core

import java.io.File
import java.io.IOException

object Assets {
    fun configText(): String {
        val candidates = listOf(File("src/main/assets/app_config.json"), File("app/src/main/assets/app_config.json"))
        return candidates.first { it.isFile }.readText(Charsets.UTF_8)
    }

    fun config(): AppConfig = AppConfig.parse(configText())
}

class Sent(val method: String, val url: String, val headers: Map<String, String>, val body: String?)

class FakeTransport(private val answer: (Sent) -> HttpResult) : Transport {
    val requests = ArrayList<Sent>()

    override fun send(method: String, url: String, headers: Map<String, String>, body: String?): HttpResult {
        val sent = Sent(method, url, headers, body)
        requests.add(sent)
        return answer(sent)
    }

    companion object {
        fun ok(body: String) = FakeTransport { HttpResult(200, body) }
        fun status(code: Int, body: String) = FakeTransport { HttpResult(code, body) }
        fun down() = FakeTransport { throw IOException("connection to 192.168.1.5 refused") }
    }
}

class MemoryStore : KeyValueStore {
    val map = HashMap<String, String>()
    override fun get(key: String): String? = map[key]
    override fun put(key: String, value: String) { map[key] = value }
    override fun remove(key: String) { map.remove(key) }
}

class ReverseBox : SecretBox {
    override fun seal(plain: String): String = "sealed:" + plain.reversed()
    override fun open(sealed: String): String? = if (sealed.startsWith("sealed:")) sealed.removePrefix("sealed:").reversed() else null
}

fun proposal(
    id: String = "p000001",
    state: String = "pending",
    createdBy: String = "system:sentinel",
    expiresAt: Long = 2_000_000_000L,
    hash: String = "a".repeat(64),
) = Proposal(id, "main", "owner/repo", "rerun", "a flaky test", createdBy, 1_700_000_000L, expiresAt, state, hash, "")
