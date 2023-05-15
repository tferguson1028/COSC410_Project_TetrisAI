public enum Action
{
  LEFT(0, "MoveLeft"), RIGHT(1, "MoveRight"), ROTL(2, "RotateLeft"), ROTR(3, "RotateRight"), 
    DROP(4, "Drop"), FALL(5, "Fall"), HOLD(-1, "Hold");
  public final int value; 
  public final String n; 
  private Action(int i, String s) { this.value = i; this.n = s; }
  public String toString() { return this.n; }
}