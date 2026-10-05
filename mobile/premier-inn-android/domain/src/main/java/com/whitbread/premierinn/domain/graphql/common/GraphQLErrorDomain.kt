package com.whitbread.premierinn.domain.graphql.common

data class GraphQLErrorDomain(
        val path: List<String>,
        val errorType: String?,
        val message: String?
)