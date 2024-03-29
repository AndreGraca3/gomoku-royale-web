package pt.isel.gomoku.server.http.model

import pt.isel.gomoku.domain.Rank
import pt.isel.gomoku.server.http.response.siren.SirenClass
import pt.isel.gomoku.server.repository.dto.MatchesStats
import pt.isel.gomoku.server.repository.dto.WinStats

class TopRanksOutputModel(
    val ranks: List<UserItemOutputModel>,
    val total: Int,
) : OutputModel() {
    override fun getSirenClasses() = listOf(SirenClass.stats, SirenClass.collection)
}

class UserStatsOutputModel(val rank: Rank, val winStats: WinStats, val matchesStats: MatchesStats) : OutputModel() {
    override fun getSirenClasses() = listOf(SirenClass.stats)
}