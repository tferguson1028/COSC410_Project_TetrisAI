public class Z extends Tetromino 
{ 
  public Z() { this(new int[][] {{1,1,0},{0,1,1}}); } 
  private Z(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}