import java.util.ArrayList;
import java.util.Arrays;

//Many Comments were kept in as they were used for testing, sorry for the mess.
public class TetrisAgent extends Player
{
  protected class EndState
  {
    public int[][] data;
    public Tetromino focus;
    public Direction dir;
    public int xPos, yPos;
    public EndState(int[][] data, Tetromino focus, Direction dir, int y, int x) 
    { 
      this.data = data;
      this.focus = focus;
      this.dir = dir;
      this.xPos = x;
      this.yPos = y;
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------
  
  private abstract class SubAgent extends Player
  {
    protected SubAgent(){}
    abstract protected int getPriority(int[][] grid);
    abstract protected int evaluateEnd(EndState e); 
    public EndState getBestEnd(ArrayList<EndState> a)
    {
      EndState best = null;
      int max = Integer.MIN_VALUE+1;
      for(EndState e : a)
      {
        int i = evaluateEnd(e);
        if(max < i)
        {
          max = i;
          best = e;
        }
      }
//      System.out.println(Arrays.deepToString(best.data));
      return best;
    }
    public String toString() { return "Hi, I'm the " + this.getClass(); }
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  
  private class FillAgent extends SubAgent//Agent that fills holes in the board that don't complete a line. It does not try to get high scores.
  {
    protected FillAgent(){}
    protected int getPriority(int[][] grid)
    {
      int priority = 10;
      int ret = 0;
      for(int i = 0; i < grid.length; i++)
        for(int j = 0; j < grid[0].length; j++)
          if(grid[i][j] == 1)
          {
            boolean flag = false;
            int z = j+1, v = 0;
            while(z < grid[0].length)
            {
              if(grid[i][z] == 1) { flag = true; break; }
              if(grid[i][z] == 0) v++;
              z++;
            }
            if(flag) ret += v;
          }
      return priority + ret;
    }
    protected int evaluateEnd(EndState e)
    {
      //The more 1s next to each other on a line the better, unless it clears a line.
      int ret = 0;
      boolean full = true;
      for(int i = e.yPos; i < e.data.length; i++)
      {
        for(int j = 0; j < e.data[0].length; j++)
        {
          if(e.data[i][j] == 1) ret += i; //Add by i to give lower lines of almost full better chances.
          if(e.data[i][j] == 0) { full = false; ret -= 2; }
        }
      }
      if(full) ret -= 20;
      return ret;
    }
  }
  
  private class BuildAgent extends SubAgent //Agent that places Tetrominos in a manner that will allow for another to easily build off of them.
  {
    protected BuildAgent(){}
    
    protected int getPriority(int[][] grid)
    {
      int priority = -100; //If there's no empty spaces, a starting priority of 0 would be able to compete with other sub-agents
      int ret = 0;
      for(int i = 0; i < grid.length; i++)
        for(int j = 0; j < grid[0].length; j++)
          if(grid[i][j] == 0) ret++;
          else ret -= 10;
      return (priority+ret)/2;
    }
    protected int evaluateEnd(EndState e)
    {
      //Agent should look for the position that has the most zeros next to it.
      int ret = 0;
      int height = e.data.length;
      int maxL = 0;
      int maxR = 0;
      for(int i = e.yPos; i < e.data.length; i++)
      {
        for(int j = e.xPos; j < e.data[0].length-1; j++)
        {
          if(e.data[i][j] == 1)
          {
            if(i < height) height = i;
            for(int z = j+1; z < e.data[0].length; z++)
              if(e.data[i][z] == 0) maxR += 3*i;
          }else break;
        }
          
        for(int j = e.xPos; j >= 0; j--)
        {
          if(e.data[i][j] == 1)
          {
            if(i < height) height = i;
            for(int z = j+1; z < e.data[0].length; z++)
              if(e.data[i][z] == 0) maxL += 3*i;
          }else break;
        }
      }
      if(maxL > maxR) ret = maxL*height;
      else ret = maxR*height;
      
      for(int i = e.xPos; i < e.xPos + e.focus.shape(e.dir)[0].length; i++)
        for(int j = e.yPos; j < e.data.length; j++)
          if(e.data[i][j] == 0) ret--;
      return ret;
    }
  }
  
  private class HeightAgent extends SubAgent //Agent that tries to increase the height of the lowest point on a board. (Fills big desparities in height)
  {
    protected HeightAgent(){}
    protected int getPriority(int[][] grid)
    {
      int priority = 5;
      int min = 0;
      int max = grid.length-1;
      for(int j = 0; j < grid[0].length; j++)
        for(int i = 0; i < grid.length; i++)
          if(grid[i][j] == 1)
          {
            if(i < max) max = i;
            if(i > min) min = i;
            break;
          }
      return priority+(3*(min-max));
    } 
    protected int evaluateEnd(EndState e)
    {   
      int ret = 0;
      int min = 0;
      int max = e.data.length;
      for(int j = 0; j < e.data[0].length; j++)
        for(int i = 0; i < e.data.length; i++)
          if(e.data[i][j] == 1)
          {
            if(i < max) max = i;
            if(i > min) min = i;
            break;
          }
      
      for(int i = e.xPos; i < e.xPos + e.focus.shape(e.dir)[0].length; i++) //Prevents endstates with holes under them.
        for(int j = e.yPos; j < e.data.length; j++)
          if(e.data[j][i] == 0) ret--;
      
      return (e.data.length+ret)/((min-max)+1);
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  
  private class ClearAgent_low extends SubAgent //Agent that tries to get big line clears, ones that generally score more points.
  {
    protected ClearAgent_low(){}
    protected int getPriority(int[][] grid)
    {
      int priority = 2;
      int ret = 0;
      for(int j = 1; j < grid[0].length-1; j++)
      {
        int numZeros = 0;
        for(int i = 1; i < grid.length; i++) //This will count the number of places that can be cleared in a straight downward line.
        {
          if(i == grid.length-1 || grid[i][j] == 1)
          {
            for(int z = j+1; z < grid[0].length; z++)
              if(grid[i-1][z] == 0) { ret -= 10; break; }
              else ret++;
            for(int z = j-1; z >= 0; z--)
              if(grid[i-1][z] == 0) { ret -= 10; break; }
              else ret++;
            for(int z = i-1; z >= 0; z--)
              if(grid[z][j] == 1) { ret -= 10; break; }
              else ret++;
          }
        }
      }
      return priority*ret;
    }
    protected int evaluateEnd(EndState e)
    {          
      int ret = 0;
      for(int i = e.yPos; i < e.data.length; i++)
      {
        boolean lineClear = true;
        for(int j = 0; j < e.data[0].length; j++)
          if(e.data[i][j] == 0) lineClear = false;
        if(lineClear) ret += 10;
      }
      
      if(ret <= 0) //If it can't find something by itself use the fill agent without avertion to full
      {
        ret = 0;
        for(int i = e.yPos; i < e.data.length; i++)
        {
          boolean full = true;
          for(int j = 0; j < e.data[0].length; j++)
          {
            if(e.data[i][j] == 1) ret += i; //Add by i to give lower lines of almost full better chances.
            if(e.data[i][j] == 0) { full = false; ret -= 2; }
          }
          if(full) ret += 10;
        }
        return ret;
      }
      
      return ret;
    }
  }
  
  private class ClearAgent_high extends SubAgent //Agent that tries to get shorter line clears, ones that will combo.
  {
    protected ClearAgent_high(){}
    protected int getPriority(int[][] grid)
    {
      int priority = 20;
      int ret = 0;
      int numZeros = 0;
      for(int i = 1; i < grid.length; i++)
        for(int j = 0; j < grid[0].length; j++)
          if(grid[i][j] == 0)
          {
            for(int a = j+1; a < grid[0].length; a++)
              if(grid[i][a] == 1) ret += 4; else ret--;
            for(int a = j-1; a >= 0; a--)
              if(grid[i][a] == 1) ret += 4; else ret--;
          }
      return priority*ret;
    }
    protected int evaluateEnd(EndState e)
    {
      
      int ret = 0;
      boolean full = true;
      for(int i = e.yPos; i < e.data.length; i++)
      {
        for(int j = 0; j < e.data[0].length; j++)
        {
          if(e.data[i][j] == 1) ret += (e.data.length-i); //Add by i to give higher lines of almost full better chances.
          if(e.data[i][j] == 0) { full = false; ret -= 2; }
        }
      }
      if(!full) ret -= 50;
      
      if(ret <= 0) //If it can't find something by itself use the fill agent without avertion to full
      {
        ret = 0;
        for(int i = e.yPos; i < e.data.length; i++)
        {
          full = true;
          for(int j = 0; j < e.data[0].length; j++)
          {
            if(e.data[i][j] == 1) ret += i; //Add by i to give lower lines of almost full better chances.
            if(e.data[i][j] == 0) { full = false; ret -= 2; }
          }
          if(full) ret += 10;
        }
        return ret;
      }
      
      return ret;       
    }
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------
  //-----------------------------------------------------------------------------------------------------------------------------
  
  private ArrayList<EndState> endStates = new ArrayList<EndState>();
  
  private Tetris game; //The game won't have to be continually called as a parameter in methods if using this.
  private SubAgent[] subAgents;
  private int[] priorities;
  
  private ArrayList<Action> actionChain = new ArrayList<Action>();
  public TetrisAgent(Tetris game)
  { 
    this.game = game;
    this.subAgents = new SubAgent[4];
    this.priorities = new int[subAgents.length];
    subAgents[0] = new FillAgent();
//    subAgents[1] = new BuildAgent();
    subAgents[1] = new HeightAgent();
    subAgents[2] = new ClearAgent_low();
    subAgents[3] = new ClearAgent_high();
    
    //This is for testing perposes
//    this.subAgents = new SubAgent[1];
//    this.priorities = new int[subAgents.length];
//    subAgents[0] = new FillAgent(); 
  }
  
  public Action doAction()
  {
    if(actionChain.isEmpty())
    {
      endStates.clear();
      createEndStates();
      int d = evaluateDanger();
//      System.out.println("\t\t\tLevel: " + d);
      for(int i = 0; i < priorities.length; i++)
      {
        priorities[i] = subAgents[i].getPriority(game.grid);
        
        //On lower danger levels, the building agents will be more likely to be chosen.
        if(i < 2) priorities[i] -= d*5;
//        System.out.println("\t\t** Agent: " + i + ": " + priorities[i]);
      }
      
      SubAgent best = getBestSubAgent();
      EndState e = best.getBestEnd(endStates);
      
//      if(e != null) System.out.println(best);
//      System.out.println(", I'm getting end state: " + Arrays.deepToString(e.data));
      actionChain = getActions(e);
    }
    Action a = null;
    if(actionChain.size() != 0) a = actionChain.remove(0);
    //If an agent doesn't know what to do, it will hold the tetomino. If it already held a piece, the tetomino will drop. 
    if(a == null && !game.heldLast) a = Action.HOLD;
    if(a == null && game.heldLast) a = Action.DROP;
//    System.out.print("act-" + a.value + "  ");
    return a;
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  private ArrayList<Action> getActions(EndState e)
  {
    ArrayList<Action> a = new ArrayList<Action>();
    if(e == null) return a;
    a.add(Action.FALL);
    for(int i = 0; i < e.dir.ordinal(); i++)
      a.add(Action.ROTL);
    int i = game.focusX;
    while(i != e.xPos)
    {
      if(i > e.xPos) {a.add(Action.LEFT); i--; }
      else {a.add(Action.RIGHT); i++; }
    }
    a.add(Action.DROP);
    return a;
  }
  
  private SubAgent getBestSubAgent()
  {
    int max = Integer.MIN_VALUE;
    int ret = 0;
    for(int i = 0; i < priorities.length; i++)
      if(priorities[i] > max)
      {
        max = priorities[i];
        ret = i;
      }
    return subAgents[ret];
  }
  
  private int evaluateDanger()
  {
    
    //Used to determine which subagent is needed.
    int dangerLevel = 0;
    for(int i = game.grid.length-1; i >= 0; i--)
    {
      int d = 0;
      for(int j = 0; j < game.grid[0].length; j++)
        if(game.grid[i][j] == 1) dangerLevel += (d+1)*(game.grid.length-i);
    }
    return dangerLevel;
  }
  
  //-----------------------------------------------------------------------------------------------------------------------------
  
  private int[][] copyGrid(int[][] c) //creates a copy so it doesn't impact the game.
  {
    int a = c.length;
    int b = c[0].length;
    int[][] grid = new int[a][b];
    for(int i = 0; i < a; i++)
      for(int j = 0; j < b; j++)
        grid[i][j] = c[i][j];
    return grid;
  }
  
  private void createEndStates()
  {
    //!! BLOCKS ARE COLLIDING WITH THEMSELVES AGAIN
    //To make the end states, you must place the piece
    for(Direction d: Direction.values())
    {
      int[][] grid = game.grid;
      int[][] focus = game.focus.shape(d);
      int h = focus.length;
      int l = focus[0].length;
      //If the shapes on a rotation are the same skip the end states for those. Thank's again stack exchange for deepEquals
      if(d.equals(Direction.RIGHT) && Arrays.deepEquals(focus, game.focus.shape(Direction.LEFT))) continue;
      if(d.equals(Direction.UP) && Arrays.deepEquals(focus, game.focus.shape(Direction.DOWN))) continue;
      
      //Special for O Tetromino
      if(!d.equals(Direction.DOWN) && Arrays.deepEquals(focus, new O().shape())) continue;
//      System.out.println(d);
      for(int i = 0; i < game.width; i++)
      {
        boolean flag = false;
        if(i+l-1 >= game.width) break; //Omit if portion out of bounds. If this one is, the rest will be to.
        
        int[][] end = new int[grid.length][grid[0].length];
        for(int a = 0; a < end.length; a++)
          for(int b = 0; b < end[0].length; b++)
            end[a][b] = grid[a][b];
        
        int hit = 0;
        for(int j = 0; j < game.height; j++)
        {          
          if(j+h > game.height) { flag = true; break; } //Omit if portion is out of bounds. If this one is, the rest will be to.

          //Checks for collision with any part of the Tetromino
          boolean col = false;
//          System.out.println("***At: (" + j + "," + i + ")");
          for(int a = 0; a < h; a++)
          {
            for(int b = 0; b < l; b++)
            {
              if(focus[a][b] == 1  && j+a+1 < game.height && end[j+a+1][i+b] == 1) { hit = j; col = true; }
              if(j+h == game.height) { hit = game.height-h; col = true; }
//              System.out.print(" - t_col: (" + b + "," + a + ")_" + col);
              if(col) break;
            }
          }
//          System.out.println("\nHitAt: (" + i + "," + hit + ")_" + col);
          if(col) break; //If it collides, check if valid.
        }
        
        //Places the piece on a copy of the grid and checks if the piece collides with anything while placing.
        for(int a = 0; a < h; a++)
        {
          for(int b = 0; b < l; b++)
          {
            if(focus[a][b] == 1)
            {
              if(end[hit+a][i+b] == 1) {flag = true; break;}
              end[hit+a][i+b] = game.focus.shape(d)[a][b];
            }
          }
          if(flag) break;
        }
        
        if(flag) continue;
//        System.out.println("EndStateGot: " + Arrays.deepToString(end));
        endStates.add(new EndState(end, game.focus, d, hit, i));
      }
    }
  }
}













