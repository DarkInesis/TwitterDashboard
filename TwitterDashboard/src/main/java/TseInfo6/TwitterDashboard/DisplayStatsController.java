package TseInfo6.TwitterDashboard;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Event.AddPointInteretEvent;
import TseInfo6.TwitterDashboard.Model.HashtagStats;
import TseInfo6.TwitterDashboard.Model.HashtagStatsList;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class DisplayStatsController implements Observer {

	@FXML
	private VBox root;
	
	@FXML
	private ScrollPane scrollPane;
	
	@FXML
	private VBox scrollContent;

	
 	@FXML
 	public void initialize() {
 		TwitterRequestManager.getManager().addObserver(this);
 		// Lorsqu'un point d'interet est ajouté, on refresh la page
 		root.addEventFilter(AddPointInteretEvent.modifPointInteretEvent,
 				new EventHandler<AddPointInteretEvent>() {
			
 			@Override
			public void handle(AddPointInteretEvent event) {
 				System.out.println("here");
				String currentUser=TwitterRequestManager.getManager().getScreenName();
				int[] percentages;
				try {
					percentages = TwitterRequestManager.getManager().getPercentageUserHashtagOfFavorite(currentUser);
					List<String> categories = DatabaseManager.getManager().getCategories(currentUser);
					TwitterRequestManager.getManager().getHashtagStatsForAllCategoriesAsync(currentUser, categories)
							.thenAccept( (statsOfCategories) -> {
								Platform.runLater(new Runnable() {
									
									@Override
									public void run() {
										fillFields(statsOfCategories, percentages);
									}
								});
								
							});
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
 	}
	
 	/**
	 * Resized the scrollpane to the available space
	 * @param bounds : the bounds to use when resizing
	 */
	public void resize(Bounds bounds) {
		double height = bounds.getHeight() - 120;
		scrollPane.setPrefHeight(height);
		scrollPane.setMinHeight(height);
		scrollPane.setMaxHeight(height);
	}
	
	public void fillFields(List<HashtagStatsList> statsOfCategories, int[] percentages) {
		
		//reset
		scrollContent.getChildren().clear();
		
		//Set title
		Label titleLabel = new Label();
		titleLabel.setId("statsLabel");
		titleLabel.setText("Centers of Interest Stats");
		VBox.setMargin(titleLabel, new Insets(30, 0, 0, 0));
		scrollContent.getChildren().add(titleLabel);
		
		//Create Pie chart for favorites
		boolean showFavChart = (percentages[0] != 0 || percentages[1] != 0);
		PieChart favChart;
		if(showFavChart) {
			favChart = new PieChart();
			favChart.setTitle("Favorites Stats");
			PieChart.Data hashSlice = new PieChart.Data("Hashtags", percentages[0]);
			PieChart.Data userSlice = new PieChart.Data("Users", percentages[1]);
			favChart.getData().add(hashSlice);
			favChart.getData().add(userSlice);
			favChart.setLabelsVisible(false);
			
			scrollContent.getChildren().add(favChart);
		}
		
		//Create bar charts for each category
		boolean allListsAreEmpty = statsOfCategories.stream()
				.map( elt -> elt.isEmpty() )
				.reduce(true, (a, b) -> a && b);
		
		if(!allListsAreEmpty) {
			for(HashtagStatsList categoryStats : statsOfCategories) {
				
				CategoryAxis xAxis = new CategoryAxis();
				xAxis.setLabel("Hashtags");
				NumberAxis yAxis = new NumberAxis();
				yAxis.setLabel("Average Per Tweet (Most Popular)");
				
				BarChart<String, Number> barChart = new BarChart<String, Number>(xAxis, yAxis);
				barChart.setTitle(categoryStats.getCategory());
				
				XYChart.Series<String, Number> favoriteCountSeries = new XYChart.Series<String, Number>();
				favoriteCountSeries.setName("Favs");
				XYChart.Series<String, Number> rtCountSeries = new XYChart.Series<String, Number>();
				rtCountSeries.setName("RTs");
				
				for(HashtagStats hashtagStats : categoryStats.getHashtagsList()) {
					long favPerTweetAverage = Math.round((double) hashtagStats.getFavStat() / hashtagStats.getTweetCount());
					long rtPerTweetAverage = Math.round( (double) hashtagStats.getRtStat() / hashtagStats.getTweetCount() );
					
					favoriteCountSeries.getData().add(new XYChart.Data<String, Number>(hashtagStats.getName(), favPerTweetAverage));
					rtCountSeries.getData().add(new XYChart.Data<String, Number>(hashtagStats.getName(), rtPerTweetAverage));
				}
				barChart.getData().add(favoriteCountSeries);
				barChart.getData().add(rtCountSeries);
				barChart.setLegendSide(Side.RIGHT);
				scrollContent.getChildren().add(barChart);
			}
		}
		
		//Create total stats
		//Create Pie chart for favs and 
		List<HashtagStats> totalStats = TwitterRequestManager.getManager().getTotalHashtagStatsForEachCategory(statsOfCategories);
		List<HashtagStats> averageStats = totalStats.stream().map(HashtagStats::average).collect(Collectors.toList());
		
		boolean allCategoriesAreEmpty = HashtagStatsList.isEmpty(averageStats);
		
		if(!averageStats.isEmpty() && !allCategoriesAreEmpty) {
			PieChart totalFavChart = new PieChart();
			totalFavChart.setTitle("Average Fav per Tweet (All categories)");
			totalFavChart.setLabelsVisible(false);
			totalFavChart.setLegendSide(Side.RIGHT);
			
			PieChart totalRtChart = new PieChart();
			totalRtChart.setTitle("Average RT per Tweet (All categories)");
			totalRtChart.setLabelsVisible(false);
			totalRtChart.setLegendSide(Side.RIGHT);
			
			
			averageStats.stream().map( (elt) -> {
				return new PieChart.Data(elt.getName(), elt.getFavStat());
			})
			.forEach( (elt) -> totalFavChart.getData().add(elt) );
			
			averageStats.stream().map( (elt) -> {
				return new PieChart.Data(elt.getName(), elt.getRtStat());
			})
			.forEach( (elt) -> totalRtChart.getData().add(elt) );
			
			
			scrollContent.getChildren().add(totalFavChart);
			scrollContent.getChildren().add(totalRtChart);
		}
		
		//Update title if nothing to show
		if(!showFavChart && allListsAreEmpty && allCategoriesAreEmpty) {
			titleLabel.setText("Statistics Page is Empty");
		}
		
	}

	@Override
	public void update(Observable o, Object arg) {
		if(!TwitterRequestManager.getManager().isConnected())
		{
			root.getChildren().clear();
			root.setUserData(null);
		}
	}
}
