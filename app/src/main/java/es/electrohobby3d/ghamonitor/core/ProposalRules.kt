// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - what the phone tells a person about a proposal before they decide (the server decides for real).
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
//
// Four eyes: the person who created a proposal cannot approve it. The monitor enforces that; the app only avoids offering a button that would be refused when it can tell, and it
// shows the server's refusal when it cannot (the app does not always know who the person is).
package es.electrohobby3d.ghamonitor.core

enum class Decision { ALLOWED, NOT_PENDING, EXPIRED, OWN_PROPOSAL }

object ProposalRules {
    val STATES = listOf("pending", "approved", "rejected", "executed", "failed", "expired")

    /** [me] is the name the person typed in the settings ("" when they did not); [nowSeconds] is the time of the phone in epoch seconds. */
    fun canApprove(p: Proposal, me: String, nowSeconds: Long, fourEyes: Boolean): Decision = when {
        p.state != "pending" -> Decision.NOT_PENDING
        p.expiresAt in 1..nowSeconds -> Decision.EXPIRED
        fourEyes && me.isNotBlank() && p.createdBy.equals(me.trim(), ignoreCase = false) -> Decision.OWN_PROPOSAL
        else -> Decision.ALLOWED
    }

    /** Rejecting is not bound by four eyes; a proposal can be rejected while it is pending or approved and has not run out. */
    fun canReject(p: Proposal, nowSeconds: Long): Boolean = (p.state == "pending" || p.state == "approved") && !(p.expiresAt in 1..nowSeconds)

    /** The seconds left, never negative; 0 when the proposal has no expiry. */
    fun secondsLeft(p: Proposal, nowSeconds: Long): Long = if (p.expiresAt <= 0) 0 else maxOf(0, p.expiresAt - nowSeconds)
}
