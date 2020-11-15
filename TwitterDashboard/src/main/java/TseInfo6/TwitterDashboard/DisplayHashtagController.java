package TseInfo6.TwitterDashboard;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.ToggleHashtagFavoriteClickedEvent;
import TseInfo6.TwitterDashboard.Model.HashtagInfo;
import TseInfo6.TwitterDashboard.Model.TwitterUser;
import TseInfo6.TwitterDashboard.View.LabelUserClickable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class DisplayHashtagController implements Observer{
	@FXML
	private Label hashtagName;

	@FXML
	private Label period;

	@FXML
	private Label nbRT;

	@FXML
	private Label nbFavorite;

	@FXML
	private GridPane classementArray;
	@FXML
	private Button addHashtagFavorite;

	private String hashtagNameString;

	private HashtagInfo hashtagInfo;
	@FXML
	public void initialize() {
		TwitterRequestManager.getManager().addObserver(this);
	}
	
	public void fillFields(final HashtagInfo hashtag) {
		this.hashtagInfo=hashtag;
		// Write Hashtag in uppercase
		this.hashtagNameString = hashtag.getHashtagName().toUpperCase();
		hashtagName.setText("#" + hashtagNameString);
		// Set value of period, nbRT,Favorite into associate field
		period.setText(hashtag.getPeriod());
		nbRT.setText(hashtag.getGlobalRT().toString());
		nbFavorite.setText(hashtag.getGlobalFavorite().toString());
		// Remplissage du tableau du top 5 des utilisateurs du hashtag
		int tailleClassement = hashtag.getTopFive().size();
		for (int ligne = 0; ligne < tailleClassement; ligne++) {
			TwitterUser user = hashtag.getTopFive().get(ligne); // Permet d'obtenir le ième user du top
			ImageView imageProfil = new ImageView(new Image(user.getImageUrl()));
		    HBox hbox=new HBox();
		    Circle circle = new Circle(20);
		    circle.setFill(new ImagePattern(new Image(user.getImageUrl())));
		    hbox.getChildren().add(circle);
			LabelUserClickable nomUser = new LabelUserClickable(user.getName(), user.getScreenName());
			hbox.getChildren().add(nomUser);
	    	classementArray.add(hbox, 1, ligne);
			GridPane.setHalignment(nomUser, HPos.CENTER);
			GridPane.setValignment(nomUser, VPos.CENTER);
			// Rend visible/invisible le bouton d'ajout d'un hashtag au favoris suivant 
			// si un user est connecte 
			if (TwitterRequestManager.getManager().isConnected()) {
				addHashtagFavorite.setVisible(true);
			} else {
				addHashtagFavorite.setVisible(false);
			}
		}
		
		//Handle state
		try {
	   		 String currentUser = TwitterRequestManager.getManager().getScreenName();
	       	 List<String> favHashtags = DatabaseManager.getManager().getFavoriteHashtags(currentUser);
	       	 String capitalizedHashtag = Utilities.asLowercaseWithFirstLetterCapitalized(this.hashtagNameString);
	       	 if(favHashtags.contains(capitalizedHashtag)) {
	       		 this.addHashtagFavorite.setStyle("-fx-text-fill: red");
	       	 }
	       	 else {
	       		this.addHashtagFavorite.setStyle("-fx-text-fill: white");
	       	 }
	   	 }
		   	 catch(SQLException ex) {
		   		 ex.printStackTrace();
	   	 }
	}

	/**
	 * Adds or removes a hashtag from/to the current user's favorite section
	 * Fire an event which will be handled elsewhere (primary controller)
	 */
	public void addHashtagFavorite() {
		//Update state
		if(this.addHashtagFavorite.getStyle().contains("-fx-text-fill: red")) {
			this.addHashtagFavorite.setStyle("-fx-text-fill: white");
		}
		else {
			this.addHashtagFavorite.setStyle("-fx-text-fill: red");
		}
		
		addHashtagFavorite.fireEvent(new ToggleHashtagFavoriteClickedEvent(
				Utilities.asLowercaseWithFirstLetterCapitalized(this.hashtagNameString)));
	}

	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {
			
			@Override
			public void run() {
				fillFields(hashtagInfo);
			}
		});

	}
}
