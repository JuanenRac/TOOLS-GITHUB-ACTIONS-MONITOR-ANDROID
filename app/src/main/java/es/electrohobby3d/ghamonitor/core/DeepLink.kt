// TOOLS-GITHUB-ACTIONS-MONITOR-ANDROID - ghamon://proposal/<id>?hash=<hex>: a link only OPENS a proposal; nothing is decided on the phone.
// Copyright (C) 2026 JuanenRac (Electro Hobby 3D). GPL-3.0-or-later.
package es.electrohobby3d.ghamonitor.core

data class ProposalLink(val proposalId: String, val contentHash: String)

object DeepLink {
    private val RE = Regex("^ghamon://proposal/([a-z0-9]{3,32})\\?hash=([0-9a-f]{64})\$")

    /** The proposal a link names, or null for anything that is not exactly this form (other schemes, paths, extra parameters). */
    fun parse(url: String?): ProposalLink? {
        val match = RE.matchEntire(url.orEmpty()) ?: return null
        return ProposalLink(match.groupValues[1], match.groupValues[2])
    }

    fun make(proposalId: String, contentHash: String): String = "ghamon://proposal/$proposalId?hash=$contentHash"

    /** True when the content the monitor holds now is the one the link was made for. A link whose hash differs opens the proposal with a warning. */
    fun matches(link: ProposalLink, proposal: Proposal): Boolean = link.proposalId == proposal.id && link.contentHash == proposal.hash
}
