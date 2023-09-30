import client.connection.payloads.gateway.SerialEmoji
import client.connection.payloads.gateway.SerialGuildChannel
import client.connection.payloads.gateway.SerialGuildMember
import client.connection.payloads.gateway.SerialRole

@kotlinx.serialization.Serializable
data class SerialGuild(
    val afk_channel_id : String = "",
    val afk_timeout : Int = 0,
    val application_id : String? = null,
    val banner : String? = null,
    val default_message_notifications : Int = 0,
    val description : String? = null,
    val discovery_splash : String? = null,
    val explicit_content_filter : Int = 0,
    val features : List<String> = listOf(),
    // val guild_scheduled_events: List<String> = listOf(),
    val members : MutableList<SerialGuildMember> = mutableListOf(),
    val icon : String = "",
    val id : String = "",
    val joined_at : String = "",
    val large : Boolean = false,
    val lazy : Boolean = false,
    val max_members : Int = 0,
    val max_video_channel_users : Int = 0,
    val member_count : Int = 0,
    val mfa_level : Int = 0,
    val name : String = "",
    val nsfw : Boolean = false,
    val nsfw_level : Int = 0,
    val owner_id : String = "",
    val widget_enabled : Boolean = false,
    val preferred_locale : String = "",
    val premium_progress_bar_enabled : Boolean = false,
    val premium_subscription_count : Int = 0,
    val premium_tier : Int = 0,
    val public_updates_channel_id : String? = null,
    val region : String = "",
    val rules_channel_id : String? = null,
    val splash : String? = null,
    val system_channel_flags : Int = 0,
    val system_channel_id : String? = null,
    // val threads: List<Any> = listOf(),
    val vanity_url_code : String? = null,
    val verification_level : Int = 0,
    val channels : List<SerialGuildChannel> = listOf(),
    val roles : List<SerialRole> = listOf(),
    val emojis : List<SerialEmoji> = listOf(),
    // val voice_states: List<Any> = listOf()
)