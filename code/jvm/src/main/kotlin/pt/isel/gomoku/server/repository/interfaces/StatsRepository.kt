package pt.isel.gomoku.server.repository.interfaces

import pt.isel.gomoku.domain.stats.MatchesStats
import pt.isel.gomoku.domain.stats.WinStats
import pt.isel.gomoku.server.http.model.stats.RawWinStats
import pt.isel.gomoku.server.http.model.user.UserIdAndName

interface StatsRepository {
    fun getTopRanks(limit: Int?): List<UserIdAndName>

    fun getScoreStatsByUser(userId: Int): RawWinStats

    fun getMatchesStatsByUser(userId: Int): MatchesStats
}