package pt.isel.gomoku.server.repository.jdbi

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.KotlinPlugin
import org.jdbi.v3.postgres.PostgresPlugin
import pt.isel.gomoku.server.repository.jdbi.mappers.BoardMapper

fun Jdbi.configureWithAppRequirements(): Jdbi {
    return this
        .installPlugin(KotlinPlugin())
        .installPlugin(PostgresPlugin())
        .registerColumnMapper(BoardMapper())
}