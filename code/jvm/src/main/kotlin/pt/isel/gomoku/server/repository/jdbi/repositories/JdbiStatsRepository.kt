package pt.isel.gomoku.server.repository.jdbi.repositories

import org.jdbi.v3.core.Handle
import pt.isel.gomoku.domain.stats.MatchesStats
import pt.isel.gomoku.server.http.model.stats.RawWinStats
import pt.isel.gomoku.server.http.model.user.UserIdAndName
import pt.isel.gomoku.server.repository.interfaces.StatsRepository
import pt.isel.gomoku.server.repository.jdbi.statements.StatsStatements

class JdbiStatsRepository(private val handle: Handle) : StatsRepository {

    override fun getTopRanks(limit: Int?): List<UserIdAndName> {
        return handle.createQuery(StatsStatements.GET_TOP_RANKS)
            .bind("limit", limit)
            .mapTo(UserIdAndName::class.java)
            .list()
    }

    override fun getScoreStatsByUser(userId: Int): RawWinStats {
        return handle.createQuery(StatsStatements.GET_WINS_BY_USER)
            .bind("userId", userId)
            .mapTo(RawWinStats::class.java)
            .one()
    }

    override fun getMatchesStatsByUser(userId: Int): MatchesStats {
        return handle.createQuery(StatsStatements.GET_MATCHES_PLAYED_BY_USER)
            .bind("userId", userId)
            .mapTo(MatchesStats::class.java)
            .one()
    }
}