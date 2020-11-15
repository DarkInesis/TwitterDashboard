package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DisplayCompareController implements Observer{
	@FXML
	private Button SearchButton1Compare;
	
	@FXML
	private Button SearchButton2Compare;
	
	@FXML
	private TextField SearchTextField1Compare;
	@FXML
	private TextField SearchTextField2Compare;
	@FXML
	private Button meComparer;
	@FXML
	private GridPane GridPaneCompare;
	@FXML
	private HBox rightCompare;
	@FXML
	private HBox leftCompare;
	
	
	private Node oldCenterNode1;
	private Node oldCenterNode2;
	@FXML
	public void initialize() {
		TwitterRequestManager.getManager().addObserver(this);
	}
	public void fillFields() {
		if(TwitterRequestManager.getManager().isConnected())
		{
			setVisibleMeComparer(true);
		}
		else {
			setVisibleMeComparer(false);
		}
	}
	
	/**
	 * Replace the node (right or left) by the new one
	 * @param newNode
	 * @param numCase : 0 for replace leftNode, 1 for replace rightNode
	 */
	private void replaceNode(Node newNode,int numCase) {
		if (numCase==0)
		{
			if(oldCenterNode1 != null) {;
				leftCompare.getChildren().remove(oldCenterNode1);
			}
			leftCompare.getChildren().add(newNode);
			oldCenterNode1 = newNode;
		}
		else if (numCase==1)
		{
			if(oldCenterNode2 != null) {
				rightCompare.getChildren().remove(oldCenterNode2);
			}
			rightCompare.getChildren().add(newNode);
			oldCenterNode2 = newNode;
		}
		newNode.setManaged(true);
	}
	
	/**
	 * Set the button "Moi" visible or invisible
	 * @param bool : true : visible; false : invisible
	 */
	public void setVisibleMeComparer(boolean bool)
	{
		meComparer.setVisible(bool);
		meComparer.setManaged(bool);
	}
	
	/**
	 * Replace the node (right or left) by the UserNode given in parameter
	 * @param screenName : name of the user we want to compare
	 * @param numCase : 0 : leftNode, 1 : rightNode
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void replaceNodebyUserNode(String screenName,int numCase) throws IOException, GeneralSecurityException
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("displayUser.fxml"));
		final VBox root = loader.load();
		DisplayUserController controller = loader.<DisplayUserController>getController();
		TwitterRequestManager.getManager()
			.getUserByNameAsync(screenName).thenAccept(tu->{
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						controller.fillFields(tu);
						replaceNode(root, numCase);
					}
				});
		})
			.exceptionally(ex->{
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Label lab = new Label();
						lab.setText(ex.getMessage());
						replaceNode(lab,numCase);
					}
				});
				return null;
			});
	}
	
	/**
	 * Get the screenName set in SearchTextField1Compare and replace the left node by
	 * the node associate to this user
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void compare1() throws IOException, GeneralSecurityException
	{
		try {
			Utilities.searchBarTextSanityze(SearchTextField1Compare);
			int numCase=0;
			String screenName=getInput(SearchTextField1Compare);
			replaceNodebyUserNode(screenName, numCase);
		}catch (RuntimeException ex){
			// error is displayed as placeholder text in searchBar
		}

	}
	
	/**
	 * Get the screenName set in SearchTextField2Compare and replace the right node by
	 * the node associate to this user
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void compare2() throws IOException, GeneralSecurityException
	{
		try {
			Utilities.searchBarTextSanityze(SearchTextField2Compare);
			int numCase=1;
			String screenName=getInput(SearchTextField2Compare);
			replaceNodebyUserNode(screenName, numCase);
		}catch (RuntimeException ex) {
			// error is displayed as placeholder text in searchBar
		}

	}
	
	/**
	 * Get the screenName of the app user and replace the left node by
	 * the userNode associate to him
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public void meComparer() throws IOException, GeneralSecurityException
	{
		int numCase=0;
		String screenName=TwitterRequestManager.getManager().getScreenName();
		SearchTextField1Compare.setText(screenName);
		replaceNodebyUserNode(screenName, numCase);
	}
	
	/**
	 * Format the imput to an usable input in all case
	 * @param input : TextField we want to get and format the input
	 * @return String containing a username without the @ caracter
	 */
	public String getInput(TextField input)
	{
		final String param=input.getText();
		final Character firstChar = param.charAt(0);
		if(firstChar.equals('@'))
		{
			return param.substring(1);
		}
		else
		{
			return param;
		}
	}
	@Override
	public void update(Observable o, Object arg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				fillFields();
			}
		});
	}
}

