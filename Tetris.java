import java.util.Random;
import java.util.ArrayList;
import java.io.IOException;

public class Tetris
{
  
  //Game Related
  private Random r = new Random();
  public final int lineClears, height, width;
  public final boolean useQueue;
  public int[][] grid;
  
  //Tetromino Related
  private ArrayList<Tetromino> set = new ArrayList<Tetromino>();
  private ArrayList<Tetromino> queue = new ArrayList<Tetromino>();
  public Tetromino focus = new EMPTY();
  public Tetromino hold = new EMPTY();
  public int[][] current = {{0,0,0,0},{0,0,0,0}};
  public int focusX = 0, focusY = 0;
  
  //Other/Player Related
  public boolean gameOver = false;
  public boolean heldLast = false, newFocus = true;
  public int totalScore = 0, combo = 1;
  private final char tIcon = 'O', tEmpty = '.';
  public int numMovesLeft = 10, numMoves = 0, linesCleared = 0; //Simulates to difficulty. Shouldn't be lower than 4
  
  public Tetris(){ this(10, 20, 150, true, 10); }
  public Tetris(int w, int h, int lc, boolean q, int nml)
  {
    this.height = h;
    this.width = w;
    this.lineClears = lc;
    this.useQueue = q;
    this.numMovesLeft = nml;
    this.grid = new int[height][width];
    for(int i = 0; i < height; i++)
      for(int j = 0; j < width; j++)
        grid[i][j] = 0;
    set.add(new I());
    set.add(new T());
    set.add(new O());
    set.add(new L());
    set.add(new J());
    set.add(new S());
    set.add(new Z());
  }
  
  //Game methods ------------------------------------------------------------------------
  public int step(Player p)  throws IOException //Returns an int value of the score the player has gotten for that step.
  {
    int score = 0;
    if(linesCleared == lineClears || gameOver) return -1;
    if(newFocus)
    {
      score = evaluateGrid();
      newFocus();
      totalScore += score * combo;
      return score * combo;
    }
    
    if(lineClears%20 == 19 && numMovesLeft > 3)
      numMovesLeft--;
    
    //Actions code
    updateGrid(p);
    return 0;
  }
  
  private void newFocus()
  {
    if(useQueue) 
    {
      if(queue.isEmpty()) 
        queue = new ArrayList<Tetromino>(set);
      focus = queue.remove(r.nextInt(queue.size()));
    }else focus = queue.get(r.nextInt(queue.size()));
    
    focus.currentDir = Direction.DOWN;
    current = focus.shape();
    focusX = 5 - focus.center[0];
    focusY = 0;
    heldLast = false;
    newFocus = false;
    setFocusOnGrid();
    
    //Evaluates game overs
    if(numMoves == 0)
      for(int i = 0; i < current.length; i++)
        for(int j = 0; j < current[0].length; j++)
          if(grid[i+focusY][j+focusX] == 2) //If a tetromino spawns on another, the game is over
            gameOver = true;
  }
  
  private void updateGrid(Player p)  throws IOException 
  {    
    if(valid(Direction.DOWN))
    {
      clearFocusOnGrid();
      if(numMoves < numMovesLeft) 
      { 
        makeMove(p.doAction().value); 
        current = focus.shape(); 
        numMoves++;
      }else 
        fall(Direction.DOWN);
      setFocusOnGrid();
    }else fall(Direction.DOWN);
  }
  
  private void clearFocusOnGrid()
  {    
    for(int i = 0; i < current.length; i++)
      for(int j = 0; j < current[0].length; j++)
        if(current[i][j] == 1)
          grid[i+focusY][j+focusX] = 0;
  }

  private void setFocusOnGrid()
  {
    // grid[i+focusY][j+focusX] += 1; //Add to the grid rather than setting it. Makes gameOver condition easier to tell. 
    for(int i = 0; i < current.length; i++)
      for(int j = 0; j < current[0].length; j++)
        if(current[i][j] == 1)
          grid[i+focusY][j+focusX] += 1;
  }
  
  private void makeMove(int i)
  {
    switch(i)
    {
      case(0): moveLeft(Direction.LEFT); break;
      case(1): moveRight(Direction.RIGHT); break;
      case(2): rotateLeft(focus.currentDir); break;
      case(3): rotateRight(focus.currentDir); break;
      case(4): drop(Direction.DOWN); break;
      case(-1): hold(); break;
      default: fall(Direction.DOWN);  
    }
  }
  
  private int evaluateGrid()
  {    
    int score = 0;
    for(int i = 0; i < height; i++)
    {
      boolean clear = true;
      for(int j = 0; j < width; j++)
        if(grid[i][j] == 0)
           clear = false;
      if(clear)
      {
        score = (score + 1) * 2; //If doing score++ constant combos of 1 would be just as good as a tetris.
        linesCleared++;
        for(int j = 0; j < width; j++)
          grid[i][j] = 0;
        moveGridDown(i);
      }
    }
    if(score == 0) combo = 0;
    else combo++;
    return score;
  }
  
  private void moveGridDown(int i)
  {
    for(int j = i; j > 0; j--)
      for(int k = 0; k < width; k++)
      if(j == 0) 
      grid[j][k] = 0;
    else 
      grid[j][k] = grid[j-1][k];
  }
  
  //Drawing methods ------------------------------------------------------------------------
  private String headerToString()
  {
    String s = "";
    s += "Tetris\tScore: " + totalScore + "\tCombo: " + combo;
    s += "\nLine Clear Goal: " + lineClears + "\nLines Cleared:   " + linesCleared;
    s += "\nHolding: ";
    int[][] k = hold.shape(Direction.DOWN);
    for(int i = 0; i < k.length; i++)
    {
      for(int j = 0; j < k[i].length; j++)
        if(k[i][j] == 1) s += tIcon; else s += tEmpty;
      s += "\n         ";
    }
    s += "\n";
    return s;
  }
  
  private String bodyToString()
  {
    String s = "\t";
    for(int i = 0; i < width + 2; i++)
      s += "=";
    for(int i = 0; i < height; i++)
    {
      s += "\n\t";
      for(int j = 0; j < width + 2; j++)
      {
        if(j == 0 || j == width + 1){ s += "|"; continue; }
        if(grid[i][j-1] == 1) s += tIcon; else s += tEmpty;
      }
    }
    s += "\n\t";
    for(int i = 0; i < width + 2; i++)
      s += "=";
    return s;
  }
  
  public String toString() { return headerToString() + bodyToString(); }
  public void drawGame() { System.out.println(headerToString() + bodyToString() + "\n"); }
  
  //Control methods ------------------------------------------------------------------------
  public void drop(Direction d) { while(valid(d)) fall(d); }
  public void fall(Direction d) 
  { 
    numMoves = 0; 
    if(valid(d)) focusY++; 
    else newFocus = true; 
  }
  
  public void moveLeft(Direction d) { if(valid(d)) focusX--; }
  public void moveRight(Direction d) { if(valid(d)) focusX++; }
  
  public void hold()
  {
    if(heldLast) return;
    if(hold.getClass() == new EMPTY().getClass()) { hold = focus; clearFocusOnGrid(); newFocus(); }
    else 
    {
      clearFocusOnGrid();
      Tetromino temp = hold;
      hold = focus;
      focus = temp;
      focus.currentDir = Direction.DOWN;
      current = focus.shape();
      focusX = 5 - focus.center[0];
      focusY = 0;
      heldLast = true;
    }
  }
  
  public void rotateLeft(Direction d)
  {
    switch(d)
    {
      case DOWN: if(!colliding(Direction.LEFT)) focus.currentDir = Direction.LEFT; break;
      case LEFT: if(!colliding(Direction.UP)) focus.currentDir = Direction.UP; break;
      case UP: if(!colliding(Direction.RIGHT)) focus.currentDir = Direction.RIGHT; break;
      case RIGHT: if(!colliding(Direction.DOWN)) focus.currentDir = Direction.DOWN; break;
    }
  }
  
  public void rotateRight(Direction d)
  {
    switch(d)
    {
      case DOWN: if(!colliding(Direction.RIGHT)) focus.currentDir = Direction.RIGHT; break;
      case LEFT: if(!colliding(Direction.DOWN)) focus.currentDir = Direction.DOWN; break;
      case UP: if(!colliding(Direction.LEFT)) focus.currentDir = Direction.LEFT; break;
      case RIGHT: if(!colliding(Direction.UP)) focus.currentDir = Direction.UP; break;
    }
  }
  
  public boolean colliding(Direction d) //for rotation
  {
    //Clearing the focus is not needed here as this is never called before the focus is cleared, unlike valid().
    int[][] temp = focus.shape(d);
    int h = temp.length;
    int l = temp[0].length;
    for(int i = 0; i < h; i++)
    {
      if(focusY+i-1 < 0 || focusY+i+1 >= height) return true;
      for(int j = 0; j < l; j++)
      { 
        if(focusY+i-1 < 0 || focusY+i+1 >= height) return true;
        if(focusX+j-1 < 0 || focusX+j+1 >= width) return true;
        
        if(grid[focusY+i-1][focusX+j+1] == 1) return true;
        if(grid[focusY+i-1][focusX+j-1] == 1) return true;
        if(grid[focusY+i+1][focusX+j+1] == 1) return true;
        if(grid[focusY+i+1][focusX+j-1] == 1) return true;
      } 
    }
    return false;
  }
  
  private boolean valid(Direction d) //for movement
  {
    int h = current.length;
    int l = current[0].length;
    
    clearFocusOnGrid(); //Prevents overlapping collision 
    for(int i = 0; i < h; i++)
      for(int j = 0; j < l; j++)
        if(current[i][j] == 1)
          switch(d)
          {
            case UP: return true;
            case DOWN: 
              if(focusY+h >= height || grid[focusY+i+1][focusX+j] == 1)
              {
                setFocusOnGrid();
                return false;
              }
              break;
            case LEFT:
              if(focusX <= 0 || grid[focusY+i][focusX+j-1] == 1)
              {
                setFocusOnGrid();
                return false;
              }
              break;
            case RIGHT:
              if(focusX+l >= width || grid[focusY+i][focusX+j+1] == 1)
              {
                setFocusOnGrid();
                return false;
              }
              break;
          }
    return true;
  }
}


//Old Collision in case new one fails at some point
    
//    switch(d)
//    {
//      case DOWN:
//        for(int i = 0; i < current[h-1].length; i++)
//          if(current[h-1][i] == 1 && (focusY+h >= height || grid[focusY+h][focusX+i] == 1))
//            return false;
//        break;
//        
//      case LEFT:
//        for(int i = 0; i < current.length; i++)
//          if(current[i][0] == 1 && (focusX <= 0 || grid[focusY+i][focusX-1] == 1))
//            return false;
//        break;
//        
//      case RIGHT:
//        for(int i = 0; i < current.length; i++)
//          if(current[i][l-1] == 1 && (focusX+l >= width || grid[focusY+i][focusX+l] == 1))
//            return false;
//        break;
//    }
    
//    int ymov = 0;
//    int xmov = 0; 
//    switch(d)
//    {
//      case DOWN: ymov = 1; break;
//      case LEFT: xmov = -1; break;
//      case RIGHT: xmov = 1; break;
//    }
//    
//    if(focusY+h+ymov >= height) return false;
//    if(focusX+xmov < 0 || focusX+l+xmov > width) return false;
//    
//    for(int i = 0; i < h; i++)
//      for(int j = 0; j < l; j++)
//        if(grid[focusY+i+ymov][focusX+j+xmov] == 1) return false;

//  private boolean notFocusBlock(int y, int x)
//  {
//    int h = current.length;
//    int l = current[0].length;
//    boolean flag = true;
//    for(int i = 0; i < h; i++)
//      for(int j = 0; j < l; j++)
//        if(grid[focusY+i][focusX+j] != current[i][j]) flag = false;
//    return flag;
//  }
