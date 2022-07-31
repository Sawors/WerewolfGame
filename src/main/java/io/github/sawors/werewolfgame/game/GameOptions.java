package io.github.sawors.werewolfgame.game;

import io.github.sawors.werewolfgame.extensionsloader.WerewolfExtension;
import io.github.sawors.werewolfgame.game.roles.PrimaryRole;

import java.util.HashSet;
import java.util.Set;

public class GameOptions {
    // GAME OPTIONS
    private boolean instantvote = true;
    private boolean autowolf = true;
    private boolean uselogchannel = false;
    private double autowolfpercentage = 0.25;
    private int wolfamount = 1;
    private boolean fancydelays = true;
    private boolean usevotetime = false;
    private boolean tellvotes = false;

    private Set<WerewolfExtension> usedextensions = new HashSet<>();
    private Set<PrimaryRole> addedroles = new HashSet<>();


    public void addExtension(WerewolfExtension extension){
        usedextensions.add(extension);
    }
    public void removeExtension(WerewolfExtension extension){
        usedextensions.remove(extension);
    }
    public Set<WerewolfExtension> getExtensions(){
        return Set.copyOf(usedextensions);
    }

    public void addRole(PrimaryRole role){
        addedroles.add(role);
    }
    public void removeRole(PrimaryRole role){
        addedroles.remove(role);
    }
    public Set<PrimaryRole> getAddedRoles(){
        return Set.copyOf(addedroles);
    }

    public void computeWolfAmount(int playercount){
        if(autoWolf()){
            autowolfpercentage = Math.max(Math.min(autowolfpercentage, 0.75), 0.25);
            // casting to int here does floor the amount as it is a positive number (if roof just do +1)
            wolfamount = (int) (playercount*autowolfPercentage());
        }
        if(wolfamount < 1){
            wolfamount = 1;
        }
        if(wolfamount >= playercount){
            wolfamount = playercount/2;
        }
    }

    public boolean instantVote() {
        return instantvote;
    }

    public boolean instantVote(boolean instantvote) {
        this.instantvote = instantvote;
        return instantvote;
    }
    
    public boolean tellVotes() {
        return tellvotes;
    }
    
    public boolean tellVotes(boolean tellvotes) {
        this.tellvotes = tellvotes;
        return tellvotes;
    }

    public boolean autoWolf() {
        return autowolf;
    }

    public boolean autoWolf(boolean use) {
        this.autowolf = use;
        return autowolf;
    }

    public boolean useLogChannel() {
        return uselogchannel;
    }

    public boolean useLogChannel(boolean use) {
        this.uselogchannel = use;
        return uselogchannel;
    }

    public double autowolfPercentage() {
        return autowolfpercentage;
    }

    public double autowolfPercentage(double percentage) {
        this.autowolfpercentage = percentage;
        return autowolfpercentage;
    }

    public int wolfAmount() {
        return wolfamount;
    }

    public int wolfAmount(int wolfamount) {
        this.wolfamount = wolfamount;
        return wolfamount;
    }

    public boolean useFancyDelays() {
        return fancydelays;
    }

    public boolean useFancyDelays(boolean fancydelays) {
        this.fancydelays = fancydelays;
        return this.fancydelays;
    }

    public boolean useVoteTimer() {
        return usevotetime;
    }

    public boolean useVoteTimer(boolean usevotetime) {
        this.usevotetime = usevotetime;
        return this.usevotetime;
    }
}
