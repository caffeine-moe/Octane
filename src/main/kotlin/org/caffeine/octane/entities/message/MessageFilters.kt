package org.caffeine.octane.entities.message

import org.caffeine.octane.entities.Snowflake


data class MessageFilters(
    var mentioningUserId : Snowflake = Snowflake(0),
    var authorId : Snowflake = Snowflake(0),
    var beforeId : Snowflake = Snowflake(0),
    var afterId : Snowflake = Snowflake(0),
    var limit : Int = 0,
    var needed : Int = 0,
)