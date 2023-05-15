public class L extends Tetromino 
{ 
  public L() { this(new int[][] {{0,0,1},{1,1,1}}); } 
  private L(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}