package io.github.sawors.werewolfgame.game;

public class Narrator {
    
    private int readspeed = 130;
    private final GameManager manager;
    
    public Narrator(GameManager manager){
        this.manager = manager;
    }
    
    public int getReadTime(String text){
        int wordcount = text.split(" ").length;
        return (wordcount/readspeed)*60;
    }
    
    public int readspeed(){
        return readspeed;
    }
    public void readspeed(int wordsperminute){
        this.readspeed = wordsperminute;
    }
}
