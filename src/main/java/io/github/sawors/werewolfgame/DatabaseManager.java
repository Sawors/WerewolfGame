package io.github.sawors.werewolfgame;

import io.github.sawors.werewolfgame.database.*;
import io.github.sawors.werewolfgame.localization.LoadedLocale;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import org.jetbrains.annotations.NotNull;

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
            /*
            LinkedUser sawors = new LinkedUser(new UserId(),"SaworsUwu", UUID.fromString("f96b1fab-2391-4c41-b6aa-56e6e91950fd"),"315237447065927691",null,null);
            sawors.addPreference(UserPreference.DO_NOT_AUTOMOVE);
            sawors.addPreference(UserPreference.USE_GLOBAL_SYNCHRONISATION);
            sawors.addPreference(UserPreference.USE_CUSTOM_NAME);
            sawors.addTag(UserTag.WEREWOLF);
            sawors.addTag(UserTag.ADVANCEMENT_RUSH);
            saveUserData(sawors);
            */
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
                + UserDataType.NAME+" text NOT NULL,"
                + UserDataType.MCUUID+" text UNIQUE,"
                + UserDataType.DISCORDID+" text UNIQUE,"
                + UserDataType.PREFERENCES+" text DEFAULT '[]',"
                + UserDataType.TAGS+" text DEFAULT '[]'"
                + ");"
                ;
    }
    
    
    public static void registerUser(LinkedUser linkeduser){
        registerUser(linkeduser.getId(), linkeduser.getName(),linkeduser.getMinecraftId(),linkeduser.getDiscordId(),linkeduser.getPreferences(),linkeduser.getTags());
    }
    public static void registerUser(@NotNull UserId id, @Nullable String name, @Nullable UUID mcid, @Nullable String discordid, @Nullable Set<UserPreference> preferences, @Nullable Set<UserTag> tags){
        String userid = id.toString();
        String username = name != null ? name : userid;
        String usermcid = mcid != null ? mcid.toString() : "";
        String userdiscordid = discordid != null ? discordid : "";
        String userpreferences = preferences != null ? preferences.toString() : "[]";
        String usertags = tags != null ? tags.toString() : "[]";
        try(Connection co = connect()){
            co.prepareStatement("INSERT into Users Values('"
            +userid+"','"
            +username+"','"
            +usermcid+"','"
            +userdiscordid+"','"
            +userpreferences+"','"
            +usertags+"')"
            ).execute();
            
        } catch(SQLException e){
            try(Connection co = connect()){
                String query = "UPDATE Users SET "
                        +UserDataType.NAME+" = '"+username+"', "
                        +UserDataType.MCUUID+" = '"+usermcid+"', "
                        +UserDataType.DISCORDID+" = '"+userdiscordid+"', "
                        +UserDataType.PREFERENCES+" = '"+userpreferences+"', "
                        +UserDataType.TAGS+" = '"+usertags+"'";
                // TODO : add overwrite for confirmation if overwrite detected
                Main.logAdmin("User "+username+"#"+userid+" has conflicting data with an already registered user, updating it's data...");
                HashMap<UserDataType, UserId> conflicts = getConflictingDataType(new LinkedUser(id,username,mcid,userdiscordid,preferences,tags));
                Main.logAdmin("Conflicts : "+conflicts);
                if(conflicts.containsKey(UserDataType.USERID)){
                    //overriding the data
                    query += " WHERE "+UserDataType.USERID+" = '"+userid+"'";
                    co.prepareStatement(query).execute();
                } else if(conflicts.containsKey(UserDataType.MCUUID) && conflicts.containsKey(UserDataType.DISCORDID)){
                    if(Objects.equals(conflicts.get(UserDataType.MCUUID).toString(), conflicts.get(UserDataType.DISCORDID).toString())){
                        // same user conflicting for both, overwriting
                        query += " WHERE "+UserDataType.MCUUID+" = '"+usermcid+"'";
                        co.prepareStatement(query).execute();
                    } else {
                        Main.logAdmin("MC : "+conflicts.get(UserDataType.MCUUID));
                        Main.logAdmin("DC : "+conflicts.get(UserDataType.DISCORDID));
                        Main.logAdmin("Multiple matching users, overwriting cancelled");
                        // do nothing since we have multiple matching users
                    }
                }
            }catch (SQLException e2){
                e2.printStackTrace();
            }
        }
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
                mcuuid = data.getString(UserDataType.MCUUID.toString());
                discordid = data.getString(UserDataType.DISCORDID.toString());
                Set<String> sprefs = Set.of(data.getString(UserDataType.PREFERENCES.toString()).replaceAll("\\[", "").replaceAll("]","").replaceAll(",","").split(" "));
                Set<String> stags  = Set.of(data.getString(UserDataType.TAGS.toString()).replaceAll("\\[", "").replaceAll("]","").replaceAll(",","").split(" "));
            
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
    
    public static UserId getUserId(@Nullable UUID minecraftuuid){
        if(minecraftuuid == null){
            return null;
        }
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
    
    public static @Nullable String getUserData(UserId user, UserDataType data){
        try(Connection co = connect()){
            ResultSet dataget = co.prepareStatement("SELECT "+data+" FROM Users WHERE "+UserDataType.USERID+"='"+user+"'").executeQuery();
            if(!dataget.isClosed()){
                String sdata = dataget.getString(data.toString());
                if(sdata == null || sdata.equals("null") || sdata.equals("")){
                    return null;
                }
                return sdata;
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
    
    public static String getName(UserId user){
        String name = getUserData(user, UserDataType.NAME);
        if(name == null || name.length()==0){
            name = user.toString();
        }
        return name;
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
                + GuildDataType.LANGUAGE+" text DEFAULT en_UK,"
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
                    +"en_UK,"
                    +"'[]'"
                    +")").execute();
        } catch (SQLException e){
            e.printStackTrace();
            Main.logAdmin("Guild "+guild.getName()+":"+guild.getId()+" already registered (this is NOT an error)");
        }
    }
    
    public static void registerGuild(Guild guild, @Nullable TextChannel admin, @Nullable TextChannel invites, @Nullable VoiceChannel waintingroom,@Nullable LoadedLocale language){
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
        if(language == null || !Main.getTranslator().getLoadedLocales().contains(language)){
            language = Main.getTranslator().getDefaultLocale();
        }
        try(Connection co = connect()){
            co.prepareStatement("INSERT INTO Guilds Values("
                    +guild.getId()+", "
                    +adminid+", "
                    +invitesid+", "
                    +waintingid+", "
                    +"'"+language+"', "
                    +"'[]'"
                    +")").execute();
        } catch (SQLException e){
            Main.logAdmin("Guild "+guild.getName()+":"+guild.getId()+" already registered, overwriting its data...");
            try(Connection co = connect()){
                co.prepareStatement("UPDATE Guilds SET "
                        +GuildDataType.ADMIN_TEXT_CHANNEL_ID+" = "+adminid+", "
                        +GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID+" = "+invitesid+", "
                        +GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID+" = "+waintingid+", "
                        +GuildDataType.LANGUAGE+" = '"+language+"'"
                        +" WHERE "+GuildDataType.GUILD_ID+" = "+guild.getId()
                ).execute();
            } catch (SQLException e2){
                e.printStackTrace();
                e2.printStackTrace();
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
                case"lg-adm":
                    if(chan.getType().isMessage()){
                        admins = (TextChannel) chan;
                    }
                    break;
                case"lg-invites":
                case"lg-parties":
                case"lg-invs":
                    if(chan.getType().isMessage()){
                        invites = (TextChannel) chan;
                    }
                    break;
                case"LG Waiting Room":
                case"lg waiting room":
                case"General":
                    if(chan.getType().isAudio()){
                        waiting = (VoiceChannel) chan;
                    }
                    break;
            }
        }
    
        DatabaseManager.registerGuild(guild,admins,invites,waiting, Main.getTranslator().getDefaultLocale());
    }
    
    protected static void setGuildData(Guild guild, String data, GuildDataType datatype){
        if(datatype != GuildDataType.GUILD_ID){
            try(Connection co = connect()){
                ResultSet checkreg = co.prepareStatement("SELECT * FROM Guilds WHERE "+GuildDataType.GUILD_ID+"="+guild.getId()).executeQuery();
                String query;
                if(checkreg.isClosed()){
                    String adminid = checkreg.getString(GuildDataType.ADMIN_TEXT_CHANNEL_ID.toString());
                    String invitesid = checkreg.getString(GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID.toString());
                    String waitingid = checkreg.getString(GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID.toString());
                    String language = checkreg.getString(GuildDataType.LANGUAGE.toString());
                    String params = checkreg.getString(GuildDataType.GUILD_OPTIONS.toString());
                    switch(datatype){
                        case ADMIN_TEXT_CHANNEL_ID:
                            adminid = data;
                            break;
                        case GAME_INVITES_TEXT_CHANNEL_ID:
                            invitesid = data;
                            break;
                        case WAITING_ROOM_VOICE_CHANNEL_ID:
                            waitingid = data;
                            break;
                        case LANGUAGE:
                            language = data;
                            break;
                        case GUILD_OPTIONS:
                            params = data;
                            break;
                    }
                    query = "INSERT INTO Guilds Values("
                            +guild.getId()+", "
                            +adminid+", "
                            +invitesid+", "
                            +waitingid+", "
                            +language+", "
                            +params
                            +")";
                } else {
                    String textmark = datatype == GuildDataType.LANGUAGE || datatype == GuildDataType.GUILD_OPTIONS ? "'" : "";
                    query = "UPDATE Guilds SET "+datatype+"="+textmark+data+textmark+" WHERE "+GuildDataType.GUILD_ID+" = "+guild.getId();
                }
                co.prepareStatement(query).execute();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    
    public static void setGuildLanguage(Guild guild, LoadedLocale language){
        setGuildData(guild,language.toString(),GuildDataType.LANGUAGE);
    }
    
    public static LoadedLocale getGuildLanguage(Guild guild){
        String baselang = getGuildData(guild.getId(), GuildDataType.LANGUAGE);
        if(baselang != null && baselang.length() == 5 && baselang.contains("_")){
            return new LoadedLocale(baselang);
        }
        return Main.getTranslator().getDefaultLocale();
    }
    
    public static void setGuildAdminChannel(TextChannel channel){
        setGuildData(channel.getGuild(), channel.getId(), GuildDataType.ADMIN_TEXT_CHANNEL_ID);
    }
    
    public static void setGuildInvitesChannel(TextChannel channel){
        setGuildData(channel.getGuild(), channel.getId(), GuildDataType.GAME_INVITES_TEXT_CHANNEL_ID);
    }
    
    public static void setGuildWaitingChannel(VoiceChannel channel){
        setGuildData(channel.getGuild(), channel.getId(), GuildDataType.WAITING_ROOM_VOICE_CHANNEL_ID);
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
