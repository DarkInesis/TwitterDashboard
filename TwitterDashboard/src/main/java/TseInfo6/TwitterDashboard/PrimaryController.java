package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import org.apache.commons.codec.DecoderException;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.AddPointInteretEvent;
import TseInfo6.TwitterDashboard.Event.ToggleHashtagFavoriteClickedEvent;

import TseInfo6.TwitterDashboard.Event.ToggleUserFavoriteClickedEvent;
import TseInfo6.TwitterDashboard.Event.TopUserClickedEvent;
import TseInfo6.TwitterDashboard.Server.OAuthClient;
import TseInfo6.TwitterDashboard.Server.ServerManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.event.EventHandler;

public class PrimaryController implements Observer{
	public final String javaRuntimeExeptionStart = "java.lang.RuntimeException: ";
	
	@FXML
	private Button searchButton;

	@FXML
	private TextField inputText;

	@FXML
	private Button compareButton;

	@FXML
	private GridPane grid;
	
	@FXML
	private Button statsButton;
	
	@FXML
	private Button connectButton;
	public static boolean connectButtonIsReady=true;
	public ServerManager serverManager;
	
	@FXML
	private Label screenName;
	
	@FXML
	private BorderPane borderPane;
	@FXML
	private VBox rightVbox;
	@FXML
	private VBox leftVbox;
	private Node oldCenterNode;
	private Node oldRightNode;
	private Node oldLeftNode;

	private final String disconnectText="Disconnect";
	private final String connectText="Connect with Twitter";
	@FXML
	public void initialize() {
		// Lors du clique sur un LabelUserClickable, on remplace la node centrale
		// par celle associee a ce user
		borderPane.addEventFilter(TopUserClickedEvent.CUSTOM_EVENT_TYPE,
				new EventHandler<TopUserClickedEvent>() {
			@Override
			public void handle(TopUserClickedEvent event) {
				try {
					searchForUser(event.getScreenName());
				} catch (IOException e) {
					System.out.println("IOException");
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					// TODO Auto-generated catch block
					System.out.println("GeneralSecuriti");
					e.printStackTrace();
				}
			}
		});
		// Lors du clique sur ajout/suppression des favoris,
		// on regarde si l'utilisateur est dans les favoris, si c'est le cas on le supprime
		// sinon on l'ajoute a la database
		borderPane.addEventFilter(ToggleUserFavoriteClickedEvent.toggleUserFavorite, 
				new EventHandler<ToggleUserFavoriteClickedEvent>() {

					@Override
					public void handle(ToggleUserFavoriteClickedEvent event) {
						String currentUser = TwitterRequestManager.getManager().getScreenName();
						try {
							List<ArrayList<String>> favoriteUsers = DatabaseManager.getManager().getFavoriteUsers(currentUser);
							
							//Check if user is already inside favorite
							boolean found = Utilities.isScreenNameInFavorites(event.getScreenName(), favoriteUsers);
							
							if(found) {
								removeUserFromFavorite(event.getScreenName());
							}
							else {
								addUserFavoris(event.getScreenName(),event.getImageUrl());	
							}
						}
						catch(SQLException ex) {
							ex.printStackTrace();
						}
						
					}
		});
		// Lors du clique sur ajout/suppression des favoris,
		// on regarde si le hashtag est dans les favoris, si c'est le cas on le supprime
		// sinon on l'ajoute a la database
		borderPane.addEventFilter(ToggleHashtagFavoriteClickedEvent.toggleHashtagFavorite,
				new EventHandler<ToggleHashtagFavoriteClickedEvent>() {
					@Override
					public void handle(ToggleHashtagFavoriteClickedEvent event) {
						String currentUser = TwitterRequestManager.getManager().getScreenName();
						try {
							List<String> favoriteHashtags = DatabaseManager.getManager().getFavoriteHashtags(currentUser);
							
							if(favoriteHashtags.contains(event.getHashtagName())) {
								removeHashtagFromFavorite(event.getHashtagName());
							}
							else {
								addHashtagFavorite(event.getHashtagName());
							}
							
						}
						catch(SQLException ex) {
							ex.printStackTrace();
						}
					}		
		});		
		// Lors du clique sur ajout/suppression d'un point d interet, 
		// et que l'on regarde les statistiques, refresh la node
		borderPane.addEventFilter(AddPointInteretEvent.modifPointInteretEvent, 
				new EventHandler<AddPointInteretEvent>() {

					@Override
					public void handle(AddPointInteretEvent event) {
						if(oldCenterNode.getUserData() == DisplayStatsController.class)
						{
							try {
								onStatsButtonClicked();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					}
			
		});
		TwitterRequestManager.getManager().addObserver(this);
		// Initialisation du bouton Connexion
		Utilities.buttonGreenToRed(connectButton, false, disconnectText, connectText);
	}
	
	/**
	 * Gets available place for center node
	 * @return bounds: the available space
	 */
	private Bounds getBounds() {
		return grid.getChildren().get(0).getBoundsInParent();
	}

	/* ######################################################################################## */
	/* 									Gestion des Nodes										*/
	/* ######################################################################################## */
	
	/**
	 * On remplace la node centrale par la nouvelle
	 * @param newNode
	 */
	private void replaceCenterNode(Node newNode) {
		if(oldCenterNode != null) {
			grid.getChildren().remove(oldCenterNode);
		}
		grid.add(newNode, 1, 0);
		oldCenterNode = newNode;
	}

	/**
	 * On remplace la node de droite par la nouvelle
	 * @param newNode
	 */
	private void replaceRightNode(Node newNode) {
		if(oldRightNode != null) {
			rightVbox.getChildren().remove(oldRightNode);
		}
		if(newNode!=null) {
			rightVbox.getChildren().add(newNode);
		}
		oldRightNode = newNode;
	}
	
	/**
	 * On remplace la node de gauche par la nouvelle
	 * @param newNode
	 */
	private void replaceLeftNode(Node newNode) {
		if(oldLeftNode != null) {
			leftVbox.getChildren().remove(oldLeftNode);
		}
		if(newNode!=null)
		{
			leftVbox.getChildren().add(newNode);
		}
		oldLeftNode = newNode;
	}
	
	/* ######################################################################################## */
	/* 								Recherches (node centrale)									*/
	/* ######################################################################################## */
	
	/**
	 * Recherche un user (API Twitter) et remplace la node 
	 * centrale par l'affichage de ce user
	 * @param username Nom de l'utilisateur recherche
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void searchForUser(final String username) throws IOException, GeneralSecurityException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayUser.fxml"));
		final VBox root = loader.load();

		DisplayUserController controller = loader.<DisplayUserController>getController();

		TwitterRequestManager.getManager().getUserByNameAsync(username)
		.thenAccept(tu -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller.fillFields(tu);
					root.setUserData(controller.getClass());
					root.setUserData(controller.getClass());
					replaceCenterNode(root);
				}
			});

		})
		.exceptionally(ex -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Label lab = new Label();
					lab.setText(ex.getMessage().replace(javaRuntimeExeptionStart, ""));
					replaceCenterNode(lab);
				}
			});

			return null;
		});
	}
	
	/**
	 * Remplace la node centrale par la node de comparaison
	 * @throws IOException 
	 */
	public void compare() throws IOException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayCompare.fxml"));
		final VBox root = loader.load();

		DisplayCompareController controller = loader.<DisplayCompareController>getController();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				controller.fillFields();
				root.setUserData(controller.getClass());
				replaceCenterNode(root);
			}
		});
	}
	
	/**
	 * Lance la bonne recherche (suivant si # ou @)
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void search() throws IOException, GeneralSecurityException{
		try {
			Utilities.searchBarTextSanityze(inputText);
			final String param = inputText.getText();
			final Character firstChar = param.charAt(0);
			if(firstChar.equals('@'))
			{
				searchForUser(param.substring(1));
			}
			else if(firstChar.equals('#'))
			{
				searchForHashtag(param.substring(1));
			}
			else
			{
				searchForHashtag(param);
			}
		}catch(RuntimeException ex)
		{
			// error is displayed as placeholder text in searchBar
		}
	}
	
	/**
	 * Recherche un hashtag (API Twitter) et remplace la node 
	 * centrale par l'affichage de ce hashtag
	 * @param hashtagName Nom du hashtag recherche
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void searchForHashtag(final String hashtagName) throws IOException, GeneralSecurityException{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayHashtag.fxml"));
		final VBox root = loader.load();
		DisplayHashtagController controller = loader.<DisplayHashtagController>getController();

		TwitterRequestManager.getManager().getHahstagInfoAsync(hashtagName)
		.thenAccept(hashtag -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					controller.fillFields(hashtag);
					root.setUserData(controller.getClass());
					replaceCenterNode(root);
				}
			});

		})
		.exceptionally(ex -> {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Label lab = new Label();
					String message = ex.getMessage()
					.replace(javaRuntimeExeptionStart, "");
					lab.setText(message);
					replaceCenterNode(lab);
				}
			});
			return null;
		});
	}
	
	/* ######################################################################################## */
	/* 								Gestion de la connexion										*/
	/* ######################################################################################## */
	
	/**
	 * Si connecte, on se deconnecte, sinon on lance la procedure de connexion aupres de Twitter
	 * @throws GeneralSecurityException
	 * @throws DecoderException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws IOException
	 */
	public void connect() throws GeneralSecurityException, DecoderException, InterruptedException, ExecutionException, IOException {
		if(!TwitterRequestManager.getManager().isConnected()) // Cas ou l'on est pas connecte
		{
			if (connectButtonIsReady)
			{
				// Lancement du server
				serverManager = new ServerManager();
				// Lancement de la procedure de connexion
				OAuthClient.getOAuthClient().goToAuthorizeUrl();
				disableConnectButton(1);
				stopServer();
			}
		}
		else // Cas ou l'on est connecte et que l'on se deconnecte
		{
			// On met a jour les donnée de connexion a false et null
			TwitterRequestManager.getManager().setConnectedAndScreenName(false,null);
		}

	}
	
	/**
	 * Rend le bouton de connexion desactivé pendant la durée rentrée
	 * @param seconds : duree de desactivation du bouton connexion
	 */
	public void disableConnectButton(int seconds)
	{
		// On rend le button non cliquable
		connectButtonIsReady=false;
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				// Au bout d'une seconde, le bouton est de nouveau cliquable
				connectButtonIsReady=true;
			}
		}, seconds*1000);	
	}
	
	/**
	 * Arrete le serveur au bout de 5 minutes
	 */
	public void stopServer()
	{
		Timer timerServ=new Timer();
		timerServ.schedule(new TimerTask() {
			@Override
			public void run() {
				// Au bout de 5 minutes, le serveur s'eteind (cas ou l'user n'authorise pas l'aplication)
				serverManager.server.stop(0);
			}
		},5*6*1000);
	}
	
	/**
	 * Rend visible le screenName du user venant de se connecter
	 * @param screenname du user venant de se connecter
	 */
	public void changeScreenNameDisplayed(String screenname)
	{
		screenName.setText(screenname);
		screenName.setVisible(true);
	}
	public void setScreenNameInvisible() {
		screenName.setVisible(false);
	}	
	@Override
	public void update(Observable o, Object arg) {
		if(TwitterRequestManager.getManager().isConnected())
		{
			// Lorsque connecte
			
			String screen_name = (String) arg;
			DatabaseManager.getManager().registerUser(screen_name);
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					Utilities.buttonGreenToRed(connectButton, true,disconnectText, connectText);
					changeScreenNameDisplayed(screen_name);
					setVisibleButtonStats(true);
					try {
						setFavorites();
						setPointInteret();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		else { // Lors de la deconnexion
			setScreenNameInvisible();
			Utilities.buttonGreenToRed(connectButton, false, disconnectText, connectText);
			unsetFavoritesView();
			unsetPointInteretView();
			setVisibleButtonStats(false);
		}
	}
	
	/**
	 * Called by the controller when teh "stats" button is clicked
	 * Loads the Stats page as the new center node
	 * @throws IOException
	 */
	@FXML
	public void onStatsButtonClicked() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayStats.fxml"));
		final VBox root = loader.load();
		
		DisplayStatsController controller = loader.<DisplayStatsController>getController();
		
		TwitterRequestManager manager = TwitterRequestManager.getManager();
		String currentUser = manager.getScreenName();
		
		//get Data
		try {
			
			List<String> categories = DatabaseManager.getManager().getCategories(currentUser);
			manager.getHashtagStatsForAllCategoriesAsync(currentUser, categories)
			.thenAccept( (statsOfCategories) -> {

				try {
					//Get percentages
					int percentages[] = manager.getPercentageUserHashtagOfFavorite(currentUser);
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							controller.fillFields(statsOfCategories, percentages);
							controller.resize(getBounds());
							root.setUserData(controller.getClass());
							replaceCenterNode(root);
						}
					});
					
				}
				catch(SQLException ex) {
					throw new RuntimeException(ex);
				}
				
			});
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	public void setVisibleButtonStats(boolean visibility)
	{
		statsButton.setVisible(visibility);
	}

/* ######################################################################################## */
/* 									Base de donnee modification								*/
/* ######################################################################################## */
	
	/**
	 * Ajoute le user aux favoris dans le database et refresh l'interface graphique
	 * @param screenName : nom du user a ajouter
	 * @param imageUrl : image de ce user
	 */
	public void addUserFavoris(String screenName,String imageUrl)
	{	
		String currentUserName = TwitterRequestManager.getManager().getScreenName();
		DatabaseManager manager =  DatabaseManager.getManager();
		manager.addUserToFavorite(currentUserName, screenName, imageUrl);
		
		try {
			// Refesh partie graphique
			setFavorites();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Supprime le user aux favoris dans le database et refresh l'interface graphique
	 * @param screen_name : nom du user a supprimer des favoris
	 */
	public void removeUserFromFavorite(final String screen_name) {
		String currentUserName = TwitterRequestManager.getManager().getScreenName();
		DatabaseManager manager =  DatabaseManager.getManager();
		manager.removeUserFromFavorite(currentUserName, screen_name);
		
		try {
			// Refesh partie graphique
			setFavorites();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Ajout le hashtag aux favoris dans le database et refresh l'interface graphique
	 * @param hashtagName : nom du hashtag a ajouter
	 */
	public void addHashtagFavorite(String hashtagName)
	{
		String currentUserName = TwitterRequestManager.getManager().getScreenName();
		DatabaseManager manager =  DatabaseManager.getManager();
		manager.addHastagToFavorite(currentUserName, hashtagName);
		try {
			// Refesh partie graphique
			setFavorites();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Supprime le hashtag aux favoris dans le database et refresh l'interface graphique
	 * @param hashtag : nom du hashtag a supprimer
	 */
	public void removeHashtagFromFavorite(final String hashtag) {
		String currentUserName = TwitterRequestManager.getManager().getScreenName();
		DatabaseManager manager =  DatabaseManager.getManager();
		manager.removeHashtagFromFavorite(currentUserName, hashtag);
		try {
			// Refesh partie graphique
			setFavorites();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	/* ######################################################################################## */
	/* 									Favorite View											*/
	/* ######################################################################################## */
	
	/**
	 * Affiche les favoris dans la node de droite
	 * @throws IOException
	 */
	public void setFavorites() throws IOException{
		if(TwitterRequestManager.getManager().isConnected())
		{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayFavorite.fxml"));
		//final AnchorPane root = loader.load();
		final VBox root=loader.load();
		DisplayFavoriteController controller = loader.<DisplayFavoriteController>getController();

		DatabaseManager manager = DatabaseManager.getManager();
		String currentUserName = TwitterRequestManager.getManager().getScreenName();

		try {
			List<ArrayList<String>> listUsers = manager.getFavoriteUsers(currentUserName);
			List<String> listHashtags = manager.getFavoriteHashtags(currentUserName);
			controller.fillFields(listUsers, listHashtags);
			replaceRightNode(root);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
			
		}	
	}
	
	/**
	 * Cache la View des favoris
	 */
	public void unsetFavoritesView()
	{
		replaceRightNode(null);
	}
	
	/* ######################################################################################## */
	/* 									PointInteret View										*/
	/* ######################################################################################## */
	
	/**
	 * Affiche la View des points d'interets dans la node de gauche
	 * @throws IOException
	 */
	public void setPointInteret() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayInteret.fxml"));
		final VBox root = loader.load();
		DisplayInteretController controller = loader.<DisplayInteretController>getController();
		DatabaseManager manager = DatabaseManager.getManager();
		String currentUserName = TwitterRequestManager.getManager().getScreenName();
		try {
			List<String> categories=manager.getCategories(currentUserName);
			controller.fillFields(categories);
			root.setManaged(true);
			replaceLeftNode(root);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Cache la View des points d'interets
	 */
	public void unsetPointInteretView()
	{
		replaceLeftNode(null);
	}
}