package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.List;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.AddPointInteretEvent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class PointInteretController {
	@FXML
	private Label nameCategory;
	@FXML
	private TextField searchCategory;
	@FXML
	private Button addHashtagCategory;
	@FXML
	private GridPane gridPaneHashtags;

	private String username;
	private String category;

	public void fillFields(String username, String nomCategory) throws SQLException {
		this.username = username;
		this.category = nomCategory;

		DatabaseManager manager = DatabaseManager.getManager();
		nameCategory.setText(nomCategory);
		List<String> listHashtags = manager.getHashtagsInCategory(this.username, nomCategory);

		// Initialisation
		int i = 0;
		gridPaneHashtags.getChildren().clear();
		gridPaneHashtags.getRowConstraints().clear();
		// Remplissage de tableau des hashtags associe a la categorie
		for (String hashtagString : listHashtags) {
			// Creation et ajout du label
			Label hashtagLabel = new Label("#" + hashtagString.toUpperCase());
			gridPaneHashtags.add(hashtagLabel, 0, i);

			// Creation et ajout du bouton
			Button delete = new Button();
			delete.setText("-");
			delete.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					try {
						deleteHashtagFromCategory(username, hashtagString, category);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			gridPaneHashtags.add(delete, 1, i);
			// Ajout de la contrainte de taille sur chaque ligne
			gridPaneHashtags.getRowConstraints().add(new RowConstraints(30));
			i++;
		}

	}

	/**
	 * Ajoute le hashtag renseigne dans l'input associe a la categorie a la database
	 * puis refresh l'interface grahique
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public void addHashtagCategory() throws GeneralSecurityException, IOException {
		try {
			Utilities.searchBarTextSanityze(searchCategory);
			TwitterRequestManager.getManager().getHashtagTweetsAsync(searchCategory.getText()).thenAccept(tweets -> {
				if (tweets.length > 0) {
					DatabaseManager manager = DatabaseManager.getManager();
					// Ajout du hashtag dans la categorie
					manager.insertHashtagIntoCategory(username, nameCategory.getText(), searchCategory.getText());
					// Mise a jour de l'interface graphique
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							try {
								fillFields(username, nameCategory.getText());
								addHashtagCategory.fireEvent(new AddPointInteretEvent());
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
					});
				} else {
					searchCategory.setText("Invalide");
				}
			});
		} catch (RuntimeException e) {
			// error is displayed as placeholder text in searchBar
		}

	}

	/**
	 * Supprime le hashtag de la database puis refresh l'interface grahique
	 * 
	 * @param username : nom de l'utilisateur connecte
	 * @param hashtag  : nom du hashtag
	 * @param category : nom de la categorie
	 * @throws SQLException
	 */
	public void deleteHashtagFromCategory(final String username, final String hashtag, final String category)
			throws SQLException {
		// Suppression du hashtag
		DatabaseManager manager = DatabaseManager.getManager();
		manager.removeHashtagFromCategory(username, category, hashtag);
		// Mise a jour graphique
		fillFields(username, nameCategory.getText());
		gridPaneHashtags.fireEvent(new AddPointInteretEvent());
	}
}