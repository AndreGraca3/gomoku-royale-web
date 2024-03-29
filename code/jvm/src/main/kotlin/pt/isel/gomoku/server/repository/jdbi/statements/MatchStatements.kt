package pt.isel.gomoku.server.repository.jdbi.statements

object MatchStatements {

    const val CREATE_MATCH = """
        INSERT INTO match (isPrivate, variant, black_id, state)
        VALUES (:isPrivate, :variant, :black_id, 'SETUP')
    """

    const val GET_MATCH_BY_ID = """
        SELECT id, isPrivate, variant, black_id, white_id, state, type, size, stones, turn
        FROM match join board on match.id = board.match_id
        where id = :id
    """

    const val GET_PUBLIC_MATCH_BY_PREFERENCES = """
        SELECT id, isPrivate, variant, black_id, white_id, turn, size, type, stones, state
        FROM match m
        INNER JOIN board b ON b.match_id = m.id
        WHERE isPrivate = false and size = :size and variant = :variant and state = 'SETUP';
    """

    const val GET_MATCHES_BY_USER_ID = """
        SELECT id, isPrivate, variant, ${PaginationStatements.PAGINATION_PREFIX} 
        FROM match
        where black_id = :userId or white_id = :userId
        ${PaginationStatements.PAGINATION_SUFFIX}
    """

    const val UPDATE_MATCH = """
        UPDATE match
        SET black_id = coalesce(:black_id, black_id),
        white_id = coalesce(:white_id, white_id),
        state = coalesce(:state, state)
        WHERE id = :id
    """

    const val MATCH_STATUS = """
        SELECT id, state
        FROM match
        WHERE (black_id = :userId or white_id = :userId) and (state = 'SETUP' or state = 'ONGOING')
    """

    const val DELETE_MATCH = """
        delete from match where black_id = :userId and state = 'SETUP'
    """
}