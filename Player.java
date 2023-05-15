import java.io.IOException;
public class Player
{
  public final char bindMoveLeft, bindMoveRight, bindRotLeft, bindRoteRight, bindDrop, bindFall, bindHold;  
  public Player(){ this('a','d','q','e','w','s','f'); }
  public Player(char b1, char b2, char b3, char b4, char b5, char b6, char b7)
  { 
    bindMoveLeft = b1; 
    bindMoveRight = b2; 
    bindRotLeft = b3; 
    bindRoteRight = b4; 
    bindDrop = b5; 
    bindFall = b6; 
    bindHold = b7; 
  }
  
  public Action doAction() throws IOException { return getAction(System.in.read()); }
  public Action getAction(int i) { return getAction((char)i); }
  public Action getAction(char c)
  {
    if(c == bindMoveLeft) return Action.LEFT;
    if(c == bindMoveRight) return  Action.RIGHT;
    if(c == bindRotLeft) return Action.ROTL;
    if(c == bindRoteRight) return Action.ROTR;
    if(c == bindDrop) return Action.DROP;
    if(c == bindHold) return Action.HOLD;
    return Action.FALL;
  }
}