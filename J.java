public class J extends Tetromino 
{ 
  public J() { this(new int[][] {{1,0,0},{1,1,1}}); } 
  private J(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}