public class EMPTY extends Tetromino 
{ 
  public EMPTY() { this(new int[][] {{0,0},{0,0}}); } 
  private EMPTY(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}
