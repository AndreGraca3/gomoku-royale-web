package pt.isel.gomoku.server.http.response.problem

sealed class BadRequestProblem(
    status: Int,
    subType: String,
    detail: String? = null,
    data: Any? = null
) : Problem(subType, status, "Bad Request", detail, data) {

    class InvalidRequestContent() : BadRequestProblem(
        400,
        "Invalid request body content",
        "The request content is invalid",
        null
    )

    class InvalidMethod() : BadRequestProblem(
        405,
        "Invalid method for resource",
        "The method is not allowed",
        null
    )
}