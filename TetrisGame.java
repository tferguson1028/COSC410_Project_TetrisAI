import java.io.IOException;
import java.util.Scanner;

public class TetrisGame
{
  public static void main(String[] args) throws IOException
  {
    Scanner kb = new Scanner(System.in);
    long startTime = System.currentTimeMillis();
    int highestCombo = 0;
    Tetris game = new Tetris();
//    Tetris game = new Tetris(10, 6, 1000, true, 20);
//    Player p = new Player();
    Player p = new TetrisAgent(game);
//    while(kb.nextLine().charAt(0) == 'a') // For frame by frame analysis
    while(!game.gameOver)
    {
//      System.out.println(System.currentTimeMillis() - startTime);
      if((System.currentTimeMillis() - startTime )%100 == 0) //For seeing the game more clearly
      {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        game.drawGame();
        game.step(p);

        if(game.combo > highestCombo)
          highestCombo = game.combo;
      }
    }
    kb.close();
    System.out.println("Game Over.\nTotal Score: " + game.totalScore + "\nHighest Combo: " + highestCombo);
    System.out.println("\nPerformance Value: " + performance(16,150,game.linesCleared));
  }

  public static double performance(double x, double a, double b)
  { return ((18.0 / x) * ((a + 1.0) * 2)*(a / 4.0)) - (((b + 1.0) * 2.0)*(b / 4.0)); }
}
