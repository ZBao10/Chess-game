public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel();                          // Initialize the game model
        GameController controller = new GameController(model);      // Set up the controller
        controller.startGame();                                     // Start the game loop
    }
}
