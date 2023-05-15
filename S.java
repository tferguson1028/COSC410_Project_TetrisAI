public class S extends Tetromino 
{ 
  public S() { this(new int[][] {{0,1,1},{1,1,0}}); } 
  private S(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}