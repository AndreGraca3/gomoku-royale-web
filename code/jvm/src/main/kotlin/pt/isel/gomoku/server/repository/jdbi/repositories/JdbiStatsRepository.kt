package pt.isel.gomoku.server.repository.jdbi.repositories

import org.jdbi.v3.core.Handle
import pt.isel.gomoku.domain.Rank
import pt.isel.gomoku.server.repository.dto.MatchesStats
import pt.isel.gomoku.server.repository.dto.PaginationResult
import pt.isel.gomoku.server.repository.dto.RawWinStats
import pt.isel.gomoku.server.repository.dto.UserItem
import pt.isel.gomoku.server.repository.interfaces.StatsRepository
import pt.isel.gomoku.server.repository.jdbi.statements.StatsStatements

class JdbiStatsRepository(private val handle: Handle) : StatsRepository {
    override fun createStatsEntry(userId: Int) {
        handle.createUpdate(StatsStatements.CREATE_ENTRY)
            .bind("userId", userId)
            .execute()
    }

    override fun getTopRanks(skip: Int, limit: Int): PaginationResult<UserItem> {
        val usersCollection = handle.createQuery(StatsStatements.GET_TOP_RANKS)
            .bind("skip", skip)
            .bind("limit", limit)
            .mapTo(UserItem::class.java)
            .list()

        return PaginationResult(
            results = usersCollection,
            total = usersCollection.firstOrNull()?.count ?: 0
        )
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

    override fun getUserRank(userId: Int): Rank {
        return handle.createQuery(StatsStatements.GET_RANK)
            .bind("userId", userId)
            .mapTo(Rank::class.java)
            .one()
    }

    override fun updateWinStats(userId: Int, player: Char) {
        handle.createUpdate(StatsStatements.UPDATE_WIN_STATS)
            .bind("user_id", userId)
            .bind("player", player)
            .execute()
    }

    override fun updateMMR(userId: Int, mmrChange: Int): Int {
        return handle.createUpdate(StatsStatements.UPDATE_MMR)
            .bind("userId", userId)
            .bind("mmrChange", mmrChange)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()
    }
}