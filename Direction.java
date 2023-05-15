public enum Direction //This is to makes tetromino directions simpler to read, could be numbers and work the same
{
  DOWN(0, "Down"), LEFT(1, "Left"), UP(2, "Up"), RIGHT(3, "Right");
  final int dir;
  final String dirn;
  private Direction(int i, String s) { this.dir = i; this.dirn = s; }
  public String toString() { return dirn; }
}