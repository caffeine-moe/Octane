package entities.message

import entities.Snowflake


data class MessageSearchFilters(
    var mentioningUserId : Snowflake = Snowflake(0),
    var authorId : Snowflake = Snowflake(0),
    var beforeId : Snowflake = Snowflake(0),
    var afterId : Snowflake = Snowflake(0),
)