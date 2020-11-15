package TseInfo6.TwitterDashboard;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import TseInfo6.TwitterDashboard.Model.Tweet;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class Utilities {
	/**
	 * Convert a date as String to a Date object
	 * @param dateAsString
	 * @return param as Date object
	 */
	public static Date dateFromString(final String dateAsString) {
		final DateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
		df.setLenient(true);
		
		Date date;
		try {
			date = df.parse(dateAsString);
		}
		catch(ParseException ex) {
			date = null;
		}
		
		return date;
	}
	/**
	 * Convert a date object to string
	 * @param date
	 * @return string conversion of the input date
	 */
	public static String dateToString(final Date date) {
		final DateFormat df = new SimpleDateFormat("E d M Y", Locale.FRANCE);
		return df.format(date);
	}
	
	/**
	 * Returns filter by max id if needed (maxId != null)
	 * @param uri the current uri being built
	 * @param maxId
	 * @return the final query
	 */
	public static String addMaxIdFilter(final String uri, Long maxId) {
		if(maxId != null) {
			return uri + "&max_id=" + maxId.toString();
		} else {
			return uri;
		}
	}
	
	/**
	 * Utility function to get min id inside an array of ids
	 * @param array
	 * @return the min id
	 */
	public static Long getMinIdOfTweetArray(Tweet[] array) {
		return Arrays.stream(array).mapToLong(tweet -> tweet.getId()).min().orElseGet(null);
	}
	/**
	 * Converts an array into a list
	 * @param <T> : type of the array
	 * @param a : array of values to convert
	 * @return List of the values
	 */
	public static <T> ArrayList<T> arrayToList(T[] a) {   
	    return new ArrayList<T>(Arrays.stream(a).collect(Collectors.toList()));
	}
	/**
	 * Transforme un nombre en un string en mettant la lettre K ou M
	 * suivant la puissance de 10, si le nombre depasse 10 000
	 * @param nombre
	 * @return nombre converti en string avec un formatage pour les puissances de 10
	 */
	public static String formatLongToStringK(Long nombre)
	{
		Long resultatEuclide=Long.divideUnsigned(nombre, 1000);
		Long rest=nombre-resultatEuclide*1000;
		if (resultatEuclide>0)
		{
			if (nombre<10000)
			{
				return resultatEuclide+","+rest;
			}
			// Cas ou le nombre est inferieur a un
			else if(resultatEuclide<1000){
				return resultatEuclide+"."+rest+"K";
			}
			else {
					rest=resultatEuclide;
					resultatEuclide=Long.divideUnsigned(nombre, 1000000);
					rest-=resultatEuclide*1000;
				return resultatEuclide+"."+rest+"M";
			}
		}
		else {
			return nombre.toString();
		}
	}
	/**
	 * Permmet de changer le style graphique d'un bouton de vert a rouge et reciproquement
	 * @param button the button you want to change color
	 * @param isGreen : boolean, true if the button is currently green and must to be red
	 * @param textIfGreen : text to set when the button will be green
	 * @param textIfRed : text to set when the button will be red
	 * false if it is currently red and must be green
	 */
	public static void buttonGreenToRed(Button button,boolean isGreen,String textIfRed,String textIfGreen)
	{
		String color="black";
		if(isGreen)
		{
			color="red";
			button.setText(textIfRed);
			addOnEnteredProperties(button, "Dark"+color);
			addOnExitedProperties(button, color);
		}
		else
		{
			color="green";
			button.setText(textIfGreen);
			addOnEnteredProperties(button, "light"+color);
			addOnExitedProperties(button, color);
		}
		changeTextBorderColor(button, color);
		
	}
	
	/**
	 * Modifier le style du bouton pour modifier sa couleur et sa bordure
	 * @param button : bouton dont on souhaite modifier les caracteristiques
	 * @param color : nouvelle couleur du bouton
	 */
	private static void changeTextBorderColor(Button button,String color)
	{
		button.setStyle("-fx-border-color:"+color+";-fx-text-fill:"+color+";");
	}
	
	/**
	 * Utility function to change cursor shape
	 * @param button the button to which add teh handler
	 * @param color the wanted color
	 */
	private static void addOnEnteredProperties(Button button,String color) {
		button.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// TODO Auto-generated method stub
				button.setCursor(Cursor.HAND);
				changeTextBorderColor(button, color);
			}
		});
	}
	
	/**
	 * Modifie la couleur du texte et des bordures a la sortie du bouton
	 * @param button : bouton a modifer
	 * @param color : couleur des bordure et du texte a mettre en sortie
	 */
	private static void addOnExitedProperties(Button button, String color)
	{
		button.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				button.setCursor(Cursor.DEFAULT);
				changeTextBorderColor(button, color);
			}
		});
	}
	/**
	 * Renvoit si le screenName est deja dans la liste de favoris ou non
	 * @param screen_name : nom a tester
	 * @param favUsers : List of the favorites of the user
	 * @return bool : true : in the list; false : not in the list
	 */
	public static boolean isScreenNameInFavorites(final String screen_name, List<ArrayList<String>> favUsers) {
		boolean found = false;
		for(ArrayList<String> user : favUsers) {
			if(user.get(1).equals(screen_name)) {
				found = true;
			}
		}
		return found;
	}
	public static String asLowercaseWithFirstLetterCapitalized(final String str) {
		return Character.toUpperCase(str.charAt(0)) + str.toLowerCase().substring(1);
	}
	
	/**
	 * Permet de verifier que le text de l'inputText est conforme, si possible le conformise
	 * sinon leve une exception
	 * @param inputText
	 * @throws RuntimeException
	
	 */
	public static void searchBarTextSanityze(TextField inputText,boolean eraseBlackSpace) throws RuntimeException
	{
		String placeholder=inputText.getPromptText();
		String invalidPlaceHolder="Invalid input";
		if(eraseBlackSpace) {
			inputText.setText(inputText.getText().replace(" ", ""));
		}
		if(inputText.getText().isEmpty())
		{
			if(placeholder!=invalidPlaceHolder)
			{
				inputText.setPromptText(invalidPlaceHolder);
				// au bout de 3 secondes, on remet l'ancien placeholder
				Timer timer=new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						// Au bout d'une seconde, le bouton est de nouveau cliquable
						inputText.setPromptText(placeholder);
					}
				}, 3*1000);
			}
			throw new RuntimeException("The input is empty");
		}
	}
	
	public static void searchBarTextSanityze(TextField inputText) throws RuntimeException{
		searchBarTextSanityze(inputText, true);
	}
	
}
