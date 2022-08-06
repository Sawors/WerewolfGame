package io.github.sawors.werewolfgame.game.events;

import io.github.sawors.werewolfgame.LinkedUser;
import io.github.sawors.werewolfgame.Main;
import io.github.sawors.werewolfgame.database.UserId;
import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.GameManager;
import io.github.sawors.werewolfgame.localization.TranslatableText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.util.*;

public abstract class GenericVote extends GameEvent implements RoleEvent{

    protected Integer votetime;
    protected Set<LinkedUser> votepool;
    protected EmbedBuilder votemessage = new EmbedBuilder();
    protected String messagebody;
    protected TextChannel votechannel;
    //       |            has voted for
    protected Map<UserId,               UserId> votemap = new HashMap<>();
    protected Set<UserId> voters;
    protected Set<UserId> votewinnertie = new HashSet<>();
    public GameManager manager;
    protected Set<Message> buttonmessage = new HashSet<>();

    public GenericVote(WerewolfExtension extension){
        super(extension);
        this.votetime = 30;
    };
    
    public void setVotePool(Set<LinkedUser> votepool){
        this.votepool = votepool;
    }
    public Set<LinkedUser> getVotePool( ){
        return Set.copyOf(this.votepool);
    }
    public Set<UserId> getVoters(){return Set.copyOf(this.voters);}
    public boolean canVote(UserId user){
        return voters.contains(user);
    }
    public boolean canVote(LinkedUser user){
        return voters.contains(user.getId());
    }

    public void validate(boolean force, boolean wait){

        onValidationAttempt(force,true);
        Main.logAdmin(votemap.keySet());
        Main.logAdmin(voters);
        Main.logAdmin("contains",(votemap.keySet().containsAll(voters)));
        Main.logAdmin("wait",wait);
        Main.logAdmin("force",force);

        if((votemap.size() > 0 && votemap.keySet().containsAll(voters) && !wait) || force){

            onValidationSuccess(force);

            Main.logAdmin("Votes Complete", votemap);
            Map<UserId, Integer> occurences = new HashMap<>();
            for(UserId voted : votemap.values()){
                occurences.put(voted, Collections.frequency(votemap.values(),voted));
            }
            ArrayList<UserId> sortedvotes = new ArrayList<>(occurences.keySet());
            //shuffle here to avoid predictable election behaviour
            Collections.shuffle(sortedvotes);
            sortedvotes.sort(Comparator.comparingInt(occurences::get));
            Collections.reverse(sortedvotes);

            if(sortedvotes.size() >= 2 && Objects.equals(occurences.get(sortedvotes.get(0)), occurences.get(sortedvotes.get(1)))){
                // Tie !
                for(UserId user : sortedvotes){
                    // referencing from the winner
                    if(Objects.equals(occurences.get(user), occurences.get(sortedvotes.get(0)))){
                        votewinnertie.add(user);
                    } else {
                        break;
                    }
                }
                onTie(List.copyOf(votewinnertie), occurences);
                return;
            }

            Main.logAdmin("Vote Scores", occurences);
            Main.logAdmin("Vote Ranks", sortedvotes);
            Main.logAdmin("Selected",sortedvotes.get(0));

            // always at the end of true validating methods
            onWin(sortedvotes.get(0), Map.copyOf(occurences));
        } else {
            onValidationFail();
        }
    };


    public void setVote(UserId voter, String voted){
        if(voters.contains(voter)){
            if(UserId.fromString(voted) != null){
                UserId usvoted = UserId.fromString(voted);
                if(votemap.containsKey(voter)){
                    onVoteChanged(voter,usvoted);
                } else {
                    onVoteNew(voter,usvoted);
                }
                onVote(voter,usvoted);
                votemap.put(voter,usvoted);
                Main.logAdmin(votemap);
            } else {
                doAction(voter, voted);
            }
        }
        
    }
    
    public void doAction(UserId user, String action){}

    public Map<UserId, UserId> getVoteMap(){
        return Map.copyOf(votemap);
    }

    public void start(GameManager manager, EmbedBuilder embed, boolean createthread){

        this.manager = manager;
        if(votechannel == null){
            votechannel = manager.getMainTextChannel();
        }
        
        List<ActionRow> votebuttons = new ArrayList<>();
        List<Button> tempbuttons = new ArrayList<>();
        for(LinkedUser user : votepool){
            tempbuttons.add(Button.primary("vote:"+manager.getId()+"#"+user.getId(), user.getName()));
            if(tempbuttons.size() >= 3){
                votebuttons.add(ActionRow.of(tempbuttons));
                tempbuttons.clear();
            }
        }
        if(tempbuttons.size()>0){
            //
            votebuttons.add(ActionRow.of(tempbuttons));
            tempbuttons.clear();
        }
        List<List<ActionRow>> splitrows = new ArrayList<>();
        if(votebuttons.size() > 5){
            List<ActionRow> loadlist = new ArrayList<>();
            for(int i = 5; i<votebuttons.size(); i++){
                if(i%5 == 0){
                    if(loadlist.size() > 0){
                        splitrows.add(loadlist);
                    }
                    loadlist = new ArrayList<>();
                }
                loadlist.add(votebuttons.get(i));
            }
            if(loadlist.size() > 0){
                splitrows.add(loadlist);
            }
            List<ActionRow> saferef = List.copyOf(votebuttons);
            votebuttons = new ArrayList<>();
            for(int i = 0; i<5; i++){
                votebuttons.add(saferef.get(i));
            }
        }
        if(manager.getOptions().useVoteTimer()){
            TranslatableText textpool = new TranslatableText(Main.getTranslator(),manager.getLanguage());
            embed.addField(textpool.get("votes.generic.time.title"), textpool.get("votes.generic.time.display").replaceAll("%time%",String.valueOf(votetime)), false);
        }
        Main.logAdmin("votestart");
        votechannel.sendMessageEmbeds(embed.build()).setActionRows(votebuttons).queue(msg -> {
            this.buttonmessage.add(msg);
            if(splitrows.size() > 0){
                for(int i  = 0; i < splitrows.size(); i++){
                    if(i<splitrows.size()-1){
                        votechannel.sendMessage("("+(i+1)+"/"+splitrows.size()+")").setActionRows(splitrows.get(i)).queue(m1 -> buttonmessage.add(m1));
                    } else {
                        votechannel.sendMessage("("+(i+1)+"/"+splitrows.size()+")").setActionRows(splitrows.get(i)).queue(m2 -> {
                            if(createthread){
                                m2.createThreadChannel(new TranslatableText(Main.getTranslator(), manager.getLanguage()).get("channels.thread-init")).queue();
                            }
                            buttonmessage.add(m2);
                        });
                    }
                }
            } else if(createthread){
                msg.createThreadChannel(new TranslatableText(Main.getTranslator(), manager.getLanguage()).get("channels.thread-init")).queue();
            }

        });
    }
    
    public void closeVote(){
        for(Message msg : buttonmessage){
            List<ActionRow> rows = new ArrayList<>();
            for(ActionRow row : msg.getActionRows()){
                List<Button> disabled = new ArrayList<>();
                for(Button button : row.getButtons()){
                    disabled.add(button.asDisabled().withStyle(ButtonStyle.SECONDARY));
                }
                rows.add(ActionRow.of(disabled));
            }
            msg.editMessage(msg).setActionRows(rows).queue();
        }


    }

    private MessageEmbed.Field getSupplementaryRoleField(){
        TranslatableText textpool = new TranslatableText(Main.getTranslator(),manager.getLanguage());
        return new MessageEmbed.Field(textpool.get("supplementary.title"),textpool.get("supplementary.description"),false);
    }

    // events
    //  Validation events
    public void onValidationFail(){}
    public void onValidationSuccess(boolean forced){}
    public void onValidationAttempt(boolean force, boolean wait){}
    //  Vote Events
    public void onVote(UserId voter, UserId voted){}
    public void onVoteNew(UserId voter, UserId voted){
        User u = manager.getDiscordUser(voter);
        User v = manager.getDiscordUser(voted);
        String votername = u != null ? u.getName() : voter.toString();
        String votedname = v != null ? v.getName() : voted.toString();
        TranslatableText texts = new TranslatableText(Main.getTranslator(),manager.getLanguage());
        String msg = texts.get("votes.generic.messages.new-vote").replaceAll("%user%",votername);
        if(manager.getOptions().tellVotes()){
            msg += texts.get("votes.generic.messages.tell-vote").replaceAll("%user%",votedname);
        }
        votechannel.sendMessage(msg).queue();
    }
    public void onVoteChanged(UserId voter, UserId voted){
        User u = manager.getDiscordUser(voter);
        User v = manager.getDiscordUser(voted);
        String votername = u != null ? u.getName() : voter.toString();
        String votedname = v != null ? v.getName() : voted.toString();
        TranslatableText texts = new TranslatableText(Main.getTranslator(),manager.getLanguage());
        String msg = texts.get("votes.generic.messages.change-vote").replaceAll("%user%",votername);
        if(manager.getOptions().tellVotes()){
            msg += texts.get("votes.generic.messages.tell-vote").replaceAll("%user%",votedname);
        }
        votechannel.sendMessage(msg).queue();
    }
    
    // Vote result Events
    public void onWin(UserId winner, Map<UserId, Integer> results){}
    public void onTie(List<UserId> tied, Map<UserId, Integer> results){}
    // Vote time events
    public void onTimeOut(Integer basetime){}
    public void onTimeHalf(Integer basetime, Integer elapsedtime){}
}
