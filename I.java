public class I extends Tetromino 
{
  public I() { this(new int[][] {{1,1,1,1}}); } 
  private I(int[][] s) 
  { 
    super(s); 
    setDirections();
  }
  @Override protected Tetromino retClass(int[][] s) { return new Tetromino(s); }
}