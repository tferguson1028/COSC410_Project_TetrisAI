public class Tetromino
{
  
  private final int[][] shape;
  public Direction currentDir = Direction.DOWN;
  public Tetromino[] directions = new Tetromino[4];
  public final int[] center = new int[2];
  public final int length;
  public final int height;
  
  protected Tetromino(int[][] shape)
  {
    this.shape = shape;
    this.length = shape[0].length;
    this.height = shape.length;
    this.center[0] = shape[0].length/2;
    this.center[1] = shape.length/2;
  }
  
  protected void setDirections()
  {
    //Default
    int[][] s = new int[height][length];
    for(int i = 0; i < height; i++)
      for(int j = 0; j < length; j++)
        s[i][j] = shape[i][j];
    directions[0] = retClass(s);
    
    //1 rotation (clockwise)
    s = new int[length][height];
    for(int i = 0; i < height; i++)
      for(int j = 0; j < length; j++)
        s[j][i] = shape[height-1-i][j];
    directions[1] = retClass(s);
    
    //2 rotations
    s = new int[height][length];
    for(int i = 0; i < height; i++)
      for(int j = 0; j < length; j++)
        s[i][j] = shape[height-1-i][length-1-j];
    directions[2] = retClass(s);
    
    //3 rotations
    s = new int[length][height];
    for(int i = 0; i < height; i++)
      for(int j = 0; j < length; j++)
        s[j][i] = shape[i][length-1-j];
    directions[3] = retClass(s);
  }
  
  protected Tetromino retClass(int[][] s) { return new Tetromino(s); } //should be overridden
  
  public int[][] shape(Direction d)
  {
    switch(d)
    {
      case DOWN: return directions[0].shape;
      case RIGHT: return directions[1].shape;
      case UP: return directions[2].shape;
      case LEFT: return directions[3].shape;
    }
    return directions[0].shape;
  }
  
  public int[][] shape()
  {
    switch(currentDir)
    {
      case DOWN: return directions[0].shape;
      case RIGHT: return directions[1].shape;
      case UP: return directions[2].shape;
      case LEFT: return directions[3].shape;
    }
    return directions[0].shape;
  }

  public String toString()
  {
    String s = "";
    for(int d = 0; d < directions.length; d++)
    {
      int[][] a = directions[d].shape;
      for(int i = 0; i < a.length; i++)
      {
        for(int j = 0; j < a[0].length; j++) s += a[i][j];
        s += "\n";
      }
      s += "\n\n";
    }
    return s;
  }
}





