package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalsTest {
    private val hash = "0123456789abcdef".repeat(4)

    @Test fun aLinkOfTheRightFormIsParsed() {
        val link = DeepLink.parse("ghamon://proposal/p000123?hash=$hash")
        assertEquals(ProposalLink("p000123", hash), link)
        assertEquals("ghamon://proposal/p000123?hash=$hash", DeepLink.make("p000123", hash))
    }

    @Test fun anythingElseIsNotALink() {
        val refused = listOf(
            null, "", "https://proposal/p1?hash=$hash", "ghamon://other/p000123?hash=$hash", "ghamon://proposal/p000123", "ghamon://proposal/p000123?hash=XYZ",
            "ghamon://proposal/P000123?hash=$hash", "ghamon://proposal/p000123?hash=$hash&approve=1", "ghamon://proposal/p0?hash=$hash",
            "ghamon://proposal/../x?hash=$hash", "ghamon://proposal/p000123?hash=${hash.uppercase()}",
        )
        for (url in refused) assertNull(url, DeepLink.parse(url))
    }

    @Test fun aLinkMatchesOnlyTheContentItWasMadeFor() {
        val link = ProposalLink("p000001", hash)
        assertTrue(DeepLink.matches(link, proposal(hash = hash)))
        assertFalse(DeepLink.matches(link, proposal(hash = "f".repeat(64))))
        assertFalse(DeepLink.matches(link, proposal(id = "p000002", hash = hash)))
    }
}
