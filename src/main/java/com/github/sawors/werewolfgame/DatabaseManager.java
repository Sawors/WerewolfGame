package com.github.sawors.werewolfgame;

import com.github.sawors.werewolfgame.database.UserDataType;
import com.github.sawors.werewolfgame.database.UserId;
import com.github.sawors.werewolfgame.database.UserPreference;
import com.github.sawors.werewolfgame.database.UserTag;
import com.github.sawors.werewolfgame.discord.GuildDataType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.Nullable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {
    // |====================================[GIT GUD]=====================================|
    // |                     Reminder for the newbie I'm in SQL :                         |
    // | -> Set  : INSERT into [table]([column]) VALUES([value])                          |
    // | -> Get  : SELECT [column] FROM [table] // WHERE [condition]=[something]          |
    // | -> Edit : UPDATE [table] SET [column] = [value] // WHERE [condition]=[something] |
    // | -> Del  : DELETE FROM [table] WHERE [condition]=[something]                      |
    // |==================================================================================|
    
    public static void connectInit(){
        try(Connection co = connect()){
            //  Init "Users" table
            co.createStatement().execute(linkingDatabaseInitQuery());
            co.createStatement().execute(guildOptionsDatabaseInitQuery());
            LinkedUser sawors = new LinkedUser(new UserId(),"SaworsUwu", UUID.fromString("f96b1fab-2391-4c41-b6aa-56e6e91950fd"),"315237447065927691",null,null);
            sawors.addPreference(UserPreference.DO_NOT_AUTOMOVE);
            sawors.addPreference(UserPreference.USE_GLOBAL_SYNCHRONISATION);
            sawors.addPreference(UserPreference.USE_CUSTOM_NAME);
            sawors.addTag(UserTag.WEREWOLF);
            sawors.addTag(UserTag.ADVANCEMENT_RUSH);
            saveUserData(sawors);
        } catch (
                SQLException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
    //sawors01
    private static Connection connect(){
        Connection co;
        try{
            String target = "jdbc:sqlite:"+ Main.getDbFile().getCanonicalFile();
            co = DriverManager.getConnection(target);
            return co;
        } catch (
                IOException |
                SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String linkingDatabaseInitQuery(){
        return "CREATE TABLE IF NOT EXISTS Users ("
                + UserDataType.USERID+" text NOT NULL UNIQUE,"
                + UserDataType.NAME+" text,"
                + UserDataType.MCUUID+" text UNIQUE,"
                + UserDataType.DISCORDID+" text UNIQUE,"
                + UserDataType.PREFERENCES+" text DEFAULT '[]',"
                + UserDataType.TAGS+" text DEFAULT '[]'"
                + ");"
                ;
    }
    
    public static void saveUserData(LinkedUser user){
        try(Connection co = connect()){
            co.prepareStatement("INSERT into Users Values('"
            +user.getId()+"','"
            +user.getName()+"','"
            +user.getMinecraftId()+"','"
            +user.getDiscordId()+"','"
            +user.getPreferences()+"','"
            +user.getTags()+"')"
            ).execute();
            
        } catch(SQLException e){
            try(Connection co = connect()){
                String query = "UPDATE Users SET "
                        +UserDataType.NAME+" = '"+user.getName()+"', "
                        +UserDataType.MCUUID+" = '"+user.getMinecraftId()+"', "
                        +UserDataType.DISCORDID+" = '"+user.getDiscordId()+"', "
                        +UserDataType.PREFERENCES+" = '"+user.getPreferences()+"', "
                        +UserDataType.TAGS+" = '"+user.getTags()+"'";
                // TODO : add overwrite for confirmation if overwrite detected
                Main.logAdmin("User "+user.getName()+"#"+user.getId()+" has conflicting data with an already registered user, updating it's data...");
                HashMap<UserDataType, UserId> conflicts = getConflictingDataType(user);
                Main.logAdmin("Conflicts : "+conflicts);
                if(conflicts.containsKey(UserDataType.USERID)){
                    //overriding the data
                    query += " WHERE "+UserDataType.USERID+" = '"+user.getId()+"'";
                    co.prepareStatement(query).execute();
                } else if(conflicts.containsKey(UserDataType.MCUUID) && conflicts.containsKey(UserDataType.DISCORDID)){
                    if(Objects.equals(conflicts.get(UserDataType.MCUUID).toString(), conflicts.get(UserDataType.DISCORDID).toString())){
                        // same user conflicting for both, overwriting
                        query += " WHERE "+UserDataType.MCUUID+" = '"+user.getMinecraftId()+"'";
                        co.prepareStatement(query).execute();
                    } else {
                        Main.logAdmin("MC : "+conflicts.get(UserDataType.MCUUID));
                        Main.logAdmin("DC : "+conflicts.get(UserDataType.DISCORDID));
                        Main.logAdmin("Multiple matching users, overwriting cancelled");
                        // do nothing since we have multiple matching users
                    }
                }
                
                
                
                /*String query = "UPDATE Users SET "
                        +UserDataType.NAME+" = '"+"user.getName()"+"', "
                        +UserDataType.MCUUID+" = '"+user.getMinecraftId()+"', "
                        +UserDataType.DISCORDID+" = '"+user.getDiscordId()+"', "
                        +UserDataType.PREFERENCES+" = '"+user.getPreferences()+"', "
                        +UserDataType.TAGS+" = '"+user.getTags()+"' "+
                        "WHERE "+UserDataType.DISCORDID+" = '"+user.getDiscordId()+"'";*/
                /*Main.logAdmin(query);
                co.prepareStatement(query
                ).execute();*/
            }catch (SQLException e2){
                e2.printStackTrace();
            }
        }
    }
    
    public static void setUserUUID(UserId user, UUID id){
    
    }
    
    private static HashMap<UserDataType, UserId> getConflictingDataType(LinkedUser user){
        HashMap<UserDataType, UserId> conflicts = new HashMap<>();
        String userconflict = getUserData(user.getId(), UserDataType.USERID);
        UserId mcconflict = getUserId(user.getMinecraftId());
        UserId discordconflict = getUserId(user.getDiscordId());
    
        if(userconflict != null){
            conflicts.put(UserDataType.USERID, UserId.fromString(userconflict));
        }
        if(mcconflict != null){
            conflicts.put(UserDataType.MCUUID, mcconflict);
        }
        if(discordconflict != null){
            conflicts.put(UserDataType.DISCORDID, discordconflict);
        }
        
        return conflicts;
    }
    
    protected static LinkedUser retrieveUserData(UserId user){
        String name;
        String mcuuid;
        String discordid;
        Set<UserPreference> prefs = new HashSet<>();
        Set<UserTag> tags = new HashSet<>();
        try(Connection co = connect()){
            ResultSet data = co.prepareStatement("SELECT * FROM Users WHERE "+UserDataType.USERID+"='"+user+"'").executeQuery();
            if(!data.isClosed()){
                name = data.getString(UserDataType.NAME.toString());
                mcuuid = data.getString(UserDataType.NAME.toString());
                discordid = data.getString(UserDataType.NAME.toString());
                Set<String> sprefs = Set.of(data.getString(UserDataType.NAME.toString()).replaceAll("\\[", "").replaceAll("]","").replaceAll(",","").split(" "));
                Set<String> stags  = Set.of(data.getString(UserDataType.NAME.toString()).replaceAll("\\[", "").replaceAll("]","").replaceAll(",","").split(" "));
            
                for(String str : sprefs){
                    try{
                        prefs.add(UserPreference.valueOf(str));
                    }catch (IllegalArgumentException e){
                        Main.logAdmin(str+" preference is not recognised as a correct UserPreference, it usually indicates a corrupted or incompatible user preference storage");
                    }
                }
    
                for(String str : stags){
                    try{
                        tags.add(UserTag.valueOf(str));
                    }catch (IllegalArgumentException e){
                        Main.logAdmin(str+" preference is not recognised as a correct UserPreference, it usually indicates a corrupted or incompatible user preference storage");
                    }
                }
                LinkedUser retrieveduser = new LinkedUser(user, name,UUID.fromString(mcuuid),discordid,prefs,tags);
                if(Main.useUserCache()){
                    Main.cacheUser(retrieveduser);
                }
                return retrieveduser;
            }
            return null;
        }catch(SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static UserId getUserId(UUID minecraftuuid){
        try(Connection co = connect()){
            ResultSet dataget = co.prepareStatement("SELECT "+UserDataType.USERID+" FROM Users WHERE "+UserDataType.MCUUID+"='"+minecraftuuid.toString()+"'").executeQuery();
            if(!dataget.isClosed()){
                return UserId.fromString(dataget.getString(UserDataType.USERID.toString()));
            } else {
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static UserId getUserId(String discordid){
        try(Connection co = connect()){
            ResultSet dataget = co.prepareStatement("SELECT "+UserDataType.USERID+" FROM Users WHERE "+UserDataType.DISCORDID+"='"+discordid+"'").executeQuery();
            if(!dataget.isClosed()){
                return UserId.fromString(dataget.getString(UserDataType.USERID.toString()));
            } else {
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    private static String getUserData(UserId user, UserDataType data){
        try(Connection co = connect()){
            ResultSet dataget = co.prepareStatement("SELECT "+data+" FROM Users WHERE "+UserDataType.USERID+"='"+user+"'").executeQuery();
            if(!dataget.isClosed()){
                return dataget.getString(data.toString());
            } else {
                return null;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    
    public static String getDiscordId(UserId user){
        return getUserData(user, UserDataType.DISCORDID);
    }
    
    public static UUID getMinecraftUUID(UserId user){
        String id = getUserData(user, UserDataType.MCUUID);
        
        return id != null ? UUID.fromString(id) : null;
    }
    
    //
    //      GUILD OPTIONS METHODS
    //
    
    private static String guildOptionsDatabaseInitQuery(){
        return "CREATE TABLE IF NOT EXISTS Guilds ("
                + GuildDataType.GUILD_ID+" integer NOT NULL UNIQUE,"
                + GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID+" integer,"
                + GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID+" integer,"
                + GuildDataType.ADMIN_TEXT_CHANNEL_ID+" integer,"
                + GuildDataType.GUILD_OPTIONS+" text DEFAULT '[]'"
                + ");"
                ;
    }
    
    public static void registerGuildBlank(Guild guild){
        try(Connection co = connect()){
            co.prepareStatement("INSERT INTO Guilds Values("
                    +guild.getId()+", "
                    +0+", "
                    +0+", "
                    +0+", "
                    +"'[]'"
                    +")").execute();
        } catch (SQLException e){
            e.printStackTrace();
            Main.logAdmin("Guild "+guild.getName()+":"+guild.getId()+" already registered (this is NOT an error)");
        }
    }
    
    public static void registerGuild(Guild guild, @Nullable TextChannel admin, @Nullable TextChannel invites, @Nullable VoiceChannel waintingroom){
        long adminid = 0L;
        long invitesid = 0L;
        long waintingid = 0L;
        if(admin != null){
            adminid = admin.getIdLong();
        }
        if(invites != null){
            invitesid = invites.getIdLong();
        }
        if(waintingroom != null){
            waintingid = waintingroom.getIdLong();
        }
        try(Connection co = connect()){
            co.prepareStatement("INSERT INTO Guilds Values("
                    +guild.getId()+", "
                    +adminid+", "
                    +invitesid+", "
                    +waintingid+", "
                    +"'[]'"
                    +")").execute();
        } catch (SQLException e){
            Main.logAdmin("Guild "+guild.getName()+":"+guild.getId()+" already registered, overwriting its data...");
            try(Connection co = connect()){
                co.prepareStatement("UPDATE Guilds SET "
                        +GuildDataType.ADMIN_TEXT_CHANNEL_ID+" = "+adminid+", "
                        +GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID+" = "+invitesid+", "
                        +GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID+" = "+waintingid
                        +" WHERE "+GuildDataType.GUILD_ID+" = "+guild.getId()
                ).execute();
            } catch (SQLException e2){
                e.printStackTrace();
            }
            
        }
    }
    
    public static void registerGuildAuto(Guild guild){
        TextChannel admins = null;
        TextChannel invites = null;
        VoiceChannel waiting = null;
    
        List<GuildChannel> channels = guild.getChannels();
        for(GuildChannel chan : channels){
            String name = chan.getName();
            switch(name){
                case"lg-admins":
                case"lg-admin":
                    if(chan.getType().isMessage()){
                        admins = (TextChannel) chan;
                    }
                    break;
                case"lg-invites":
                case"lg-parties":
                    if(chan.getType().isMessage()){
                        invites = (TextChannel) chan;
                    }
                    break;
                case"LG Waiting Room":
                case"lg waiting room":
                    if(chan.getType().isAudio()){
                        waiting = (VoiceChannel) chan;
                    }
                    break;
            }
        }
    
        DatabaseManager.registerGuild(guild,admins,invites,waiting);
    }
    
    protected static void setGuildData(GuildChannel channel, GuildDataType datatype){
        if(datatype != GuildDataType.GUILD_ID && datatype != GuildDataType.GUILD_OPTIONS){
            try(Connection co = connect()){
                ResultSet checkreg = co.prepareStatement("SELECT * FROM Guilds WHERE "+GuildDataType.GUILD_ID+"="+channel.getGuild().getId()).executeQuery();
                String query;
                if(checkreg.isClosed()){
                    long adminid = 0L;
                    long invitesid = 0L;
                    long waitingid = 0L;
                    switch(datatype){
                        case ADMIN_TEXT_CHANNEL_ID:
                            adminid = channel.getIdLong();
                        case GAME_INVITES_TEXT_CHANNEL_ID:
                        case WAITING_ROOM_VOICE_CHANNEL_ID:
                    }
                    query = "INSERT INTO Guilds Values("
                            +channel.getGuild().getId()+", "
                            +adminid+", "
                            +invitesid+", "
                            +waitingid+", "
                            +"'[]'"
                            +")";
                } else {
                    query = "UPDATE Guilds SET "+datatype+"="+channel.getId()+" WHERE "+GuildDataType.GUILD_ID+" = "+channel.getGuild().getId();
                }
                co.prepareStatement(query).execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    
    public static void setGuildAdminChannel(TextChannel channel){
        setGuildData(channel, GuildDataType.ADMIN_TEXT_CHANNEL_ID);
    }
    
    public static void setGuildInvitesChannel(TextChannel channel){
        setGuildData(channel, GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID);
    }
    
    public static void setGuildWaitingChannel(VoiceChannel channel){
        setGuildData(channel, GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID);
    }
    
    public static TextChannel getGuildAdminChannel(Guild guild){
        String chanid = getGuildData(guild.getId(), GuildDataType.ADMIN_TEXT_CHANNEL_ID);
        if(chanid != null && !chanid.equals("0")){
            return guild.getChannelById(TextChannel.class, chanid);
        }
        return null;
    }
    public static TextChannel getGuildInvitesChannel(Guild guild){
        String chanid = getGuildData(guild.getId(), GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID);
        if(chanid != null && !chanid.equals("0")){
            return guild.getChannelById(TextChannel.class, chanid);
        }
        return null;
    }
    public static VoiceChannel getGuildWaitingChannel(Guild guild){
        String chanid = getGuildData(guild.getId(), GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID);
        if(chanid != null && !chanid.equals("0")){
            return guild.getChannelById(VoiceChannel.class, chanid);
        }
        return null;
    }
    
    private static String getGuildData(String guildid, GuildDataType data){
        try(Connection co = connect()) {
            ResultSet rset = co.prepareStatement("SELECT "+data+" FROM Guilds WHERE "+GuildDataType.GUILD_ID+"="+guildid).executeQuery();
            if(!rset.isClosed()){
                return rset.getString(data.toString());
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
}
