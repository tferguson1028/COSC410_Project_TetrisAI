public class O extends Tetromino 
{ 
  public O() { this(new int[][] {{1,1},{1,1}}); }  
  private O(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}