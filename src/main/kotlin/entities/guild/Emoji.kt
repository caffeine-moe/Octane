package entities.guild

import client.Client
import entities.Snowflake

interface Emoji {
    val client : Client
    val animated : Boolean
    val id : Snowflake
    val guild : Guild
    val name : String
    val url : String
}