package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.AddPointInteretEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class DisplayInteretController {
	@FXML
	private VBox pointInteretVbox;
	@FXML
	private TextField inputCategory;

	public void fillFields(List<String> categories) throws IOException, SQLException {
		pointInteretVbox.getChildren().clear();
		for (String category : categories) {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("pointInteret.fxml"));
			VBox root = new VBox();
			root = loader.load();
			PointInteretController controller = loader.<PointInteretController>getController();
			controller.fillFields(TwitterRequestManager.getManager().getScreenName(), category);
			root.setManaged(true);
			pointInteretVbox.getChildren().add(root);
		}
	}

	/**
	 * Methode appelée par le bouton + de l'interface pour ajouter une categorie
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	@FXML
	public void addCategory() throws IOException, SQLException {
		try {
			Utilities.searchBarTextSanityze(inputCategory, false);
			addCategory(TwitterRequestManager.getManager().getScreenName(), inputCategory.getText());
			inputCategory.fireEvent(new AddPointInteretEvent());
		} catch (RuntimeException ex) {
			// error is displayed as placeholder text in searchBar
		}
	}

	private void addCategory(final String username, final String category) throws IOException, SQLException {
		DatabaseManager manager = DatabaseManager.getManager();
		manager.addCategory(username, category);
		fillFields(manager.getCategories(username));
	}

	/**
	 * Supprime la categorie de la database et refresh l'interface graphique
	 * 
	 * @param username : nom de l'utilisateur
	 * @param category : nom de la categorie a supprimer
	 * @throws IOException
	 * @throws SQLException
	 */
	private void removeCategory(final String username, final String category) throws IOException, SQLException {
		DatabaseManager manager = DatabaseManager.getManager();
		manager.removeCategory(username, category);
		// Refresh de l'interface graphique
		fillFields(manager.getCategories(username));
	}

	/**
	 * Methode appelée par le bouton - de l'interface pour supprimer une categorie
	 * 
	 * @throws IOException
	 * @throws SQLException
	 */
	@FXML
	public void removeCategory() throws IOException, SQLException {
		try {
			Utilities.searchBarTextSanityze(inputCategory, false);
			removeCategory(TwitterRequestManager.getManager().getScreenName(), inputCategory.getText());
			inputCategory.fireEvent(new AddPointInteretEvent());
		} catch (RuntimeException ex) {
			// error is displayed as placeholder text in searchBar
		}
	}
}
