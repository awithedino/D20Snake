package gamesrc;
import gamesrc.ui.GameUI;
import gamesrc.ui.PlayerSelectionDialog;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
             PlayerSelectionDialog selectionDialog = new PlayerSelectionDialog();
             selectionDialog.setVisible(true); // Show

             int selectedPlayers = selectionDialog.getSelectPlayerCount();

             if (selectedPlayers >= 2 && selectedPlayers <= 4) {
                 new GameUI(selectedPlayers);
             } else {
                 System.out.println("Game cancelled or no valid player count selected.");
             }
         });
    }
}