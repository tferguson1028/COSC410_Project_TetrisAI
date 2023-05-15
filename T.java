public class T extends Tetromino 
{
  public T() { this(new int[][] {{1,1,1},{0,1,0}}); } 
  private T(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}