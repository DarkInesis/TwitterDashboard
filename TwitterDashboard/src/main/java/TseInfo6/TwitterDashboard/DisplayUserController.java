package TseInfo6.TwitterDashboard;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.ToggleUserFavoriteClickedEvent;
import TseInfo6.TwitterDashboard.Model.TwitterUser;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class DisplayUserController implements Observer {

     @FXML
     private Circle imageCircle;
     
     @FXML
     private Pane banner;
     @FXML
     private Label screenNameDisplayUser;
     
     @FXML
     private ImageView verifiedIcon;
     
     @FXML
     private Label name;
     
     @FXML
     private Label bio;
     
     @FXML
     private Label followersCount;
     
     @FXML 
     private Label friendsCount;
     @FXML
     private Button addUserFavori;
     @FXML
     private Label createdAt;
     private String screenName="";
     private String imageUrl="";
     private TwitterUser userDisplayed;
     
 	@FXML
 	public void initialize() {
 		TwitterRequestManager.getManager().addObserver(this);
 	}
 	
     public void fillFields(final TwitterUser tu) {
    	 // Si il y a une bannier on l'affiche sinon on affiche rien
    	 if (tu.getBannerUrl()!=null)
    	 {
    		 this.userDisplayed=tu;
    		 Image banniere=new Image(tu.getBannerUrl());
    		 banner.setBackground(new Background(new BackgroundImage(
    				 			banniere,
    				 			null,
    				 			null,
    				 			null,
    				 			new BackgroundSize(0, 0, true, true,false, true)
    				 			)));
    	 }
    	 this.imageUrl=tu.getImageUrl();
    	 imageCircle.setFill(new ImagePattern(new Image(imageUrl)));
    	 this.screenName=tu.getScreenName();
    	 screenNameDisplayUser.setText("@"+screenName);
    	 verifiedIcon.setVisible(tu.isVerified());
    	 name.setText(tu.getName());
    	 bio.setText(tu.getDescription());
    	 
    	 // On formate le nombre de followers et d'amis en affichant les milliers et millions par un K ou M
    	 followersCount.setText(Utilities.formatLongToStringK(tu.getFollowersCount()));
    	 friendsCount.setText(Utilities.formatLongToStringK(tu.getFriendsCount()));
    	 createdAt.setText(Utilities.dateToString(tu.getCreatedAt()));
    	 
    	 // Suivant si un user est connecté ou non, on affiche le bouton d'ajout aux favoris
    	 if(TwitterRequestManager.getManager().isConnected())
    	 {
    		 addUserFavori.setVisible(true);
    	 }
    	 else {
    		 addUserFavori.setVisible(false);
    	 }
    	 
    	 //Handle follow state
    	 try {
    		 String currentUser = TwitterRequestManager.getManager().getScreenName();
        	 List<ArrayList<String>> favUsers = DatabaseManager.getManager().getFavoriteUsers(currentUser);
        	 if(Utilities.isScreenNameInFavorites(screenName, favUsers)) {
        		 this.addUserFavori.setText("Unfollow");
        	 }
        	 else {
        		 this.addUserFavori.setText("Follow");
        	 }
    	 }
    	 catch(SQLException ex) {
    		 ex.printStackTrace();
    	 }
    	 
     }
     /**
      * Lors d'un clique sur le bouton ajout user, modifie le text et averti 
      * d un changement d etat (follow/unfollow) pour ensuite modifier la database
      */
     public void addUserFavori()
     {
    	 //Toggle State
    	 if(this.addUserFavori.getText().equals("Follow")) {
    		 this.addUserFavori.setText("Unfollow");
    	 }
    	 else {
    		 this.addUserFavori.setText("Follow");
    	 }
    	 //Update
    	 addUserFavori.fireEvent(new ToggleUserFavoriteClickedEvent(screenName, imageUrl));
     }
     
	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fillFields(userDisplayed);
			}
		});
	}
}