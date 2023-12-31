package org.caffeine.octane.entities.guild

import org.caffeine.octane.client.Client
import org.caffeine.octane.client.user.ClientUser
import org.caffeine.octane.entities.Snowflake
import org.caffeine.octane.entities.channels.GuildChannel

data class GuildImpl(
    override var id : Snowflake,
    override var name : String = "",
    override var icon : String = "",
    override var description : String? = null,
    override var splash : String? = null,
    override var discoverySplash : String? = null,
    override var banner : String? = null,
    override var ownerId : Snowflake,
    override var applicationId : Snowflake? = null,
    override var region : String = "",
    override var afkChannelId : Snowflake?,
    override var afkTimeout : Int = 0,
    override var systemChannelId : Snowflake?,
    override var widgetEnabled : Boolean = false,
    override var widgetChannelId : Snowflake,
    override var verificationLevel : Int = 0,
    override var defaultMessageNotifications : Int = 0,
    override var mfaLevel : Int = 0,
    override var explicitContentFilter : Int = 0,
    override var maxPresences : Int = 0,
    override var maxMembers : Int = 0,
    override var maxVideoChannelUsers : Int = 0,
    override var vanityUrlCode : String? = null,
    override var premiumTier : Int = 0,
    override var premiumSubscriptionCount : Int = 0,
    override var systemChannelFlags : Int = 0,
    override var preferredLocale : String = "",
    override var rulesChannelId : Snowflake? = null,
    override var publicUpdatesChannelId : Snowflake? = null,
    override var embedEnabled : Boolean = false,
    override var embedChannelId : Snowflake,
    override var client : Client,
) : Guild {

    override suspend fun fetchMemberById(id : Snowflake) : GuildMember =
        client.user.fetchGuildMember(client.user.fetchUser(id), this)

    override val channels : Map<Snowflake, GuildChannel>
        get() = client.user.guildChannels.filterValues { it.guild.id == id }

    override val emojis : Map<Snowflake, Emoji>
        get() = client.user.emojis.filterValues { it.guild.id == id }

    override val vanityUrl : String? = if (vanityUrlCode != null) "https://discord.gg/$vanityUrlCode" else null
    override fun muteForever() {
        if (client.user !is ClientUser) return
        (client.user as ClientUser).muteGuild(this, -1)
    }
}