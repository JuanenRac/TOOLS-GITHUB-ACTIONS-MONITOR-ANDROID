package es.electrohobby3d.ghamonitor.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalsTest {
    private val hash = "0123456789abcdef".repeat(4)

    @Test fun aLinkTheMonitorMakesIsParsed() {
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

    @Test fun aPendingProposalOfSomeoneElseCanBeApproved() {
        assertEquals(Decision.ALLOWED, ProposalRules.canApprove(proposal(createdBy = "ana"), "bea", 1_800_000_000L, true))
    }

    @Test fun fourEyesStopsAnApprovalOfYourOwnProposal() {
        assertEquals(Decision.OWN_PROPOSAL, ProposalRules.canApprove(proposal(createdBy = "ana"), "ana", 1_800_000_000L, true))
        assertEquals(Decision.ALLOWED, ProposalRules.canApprove(proposal(createdBy = "ana"), "ana", 1_800_000_000L, false))
    }

    @Test fun withoutANameTheAppOffersTheButtonAndTheServerDecides() {
        assertEquals(Decision.ALLOWED, ProposalRules.canApprove(proposal(createdBy = "ana"), "", 1_800_000_000L, true))
    }

    @Test fun onlyAPendingProposalThatHasNotRunOutCanBeApproved() {
        assertEquals(Decision.NOT_PENDING, ProposalRules.canApprove(proposal(state = "approved"), "bea", 1L, true))
        assertEquals(Decision.NOT_PENDING, ProposalRules.canApprove(proposal(state = "executed"), "bea", 1L, true))
        assertEquals(Decision.EXPIRED, ProposalRules.canApprove(proposal(expiresAt = 100), "bea", 100, true))
        assertEquals(Decision.ALLOWED, ProposalRules.canApprove(proposal(expiresAt = 0), "bea", 100, true))
    }

    @Test fun rejectingIsPossibleWhilePendingOrApproved() {
        assertTrue(ProposalRules.canReject(proposal(state = "pending"), 1L))
        assertTrue(ProposalRules.canReject(proposal(state = "approved"), 1L))
        assertFalse(ProposalRules.canReject(proposal(state = "rejected"), 1L))
        assertFalse(ProposalRules.canReject(proposal(state = "pending", expiresAt = 50), 60L))
    }

    @Test fun theSecondsLeftNeverGoBelowZero() {
        assertEquals(40L, ProposalRules.secondsLeft(proposal(expiresAt = 100), 60L))
        assertEquals(0L, ProposalRules.secondsLeft(proposal(expiresAt = 100), 500L))
        assertEquals(0L, ProposalRules.secondsLeft(proposal(expiresAt = 0), 500L))
    }
}
