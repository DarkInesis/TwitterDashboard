package TseInfo6.TwitterDashboard.View;

import TseInfo6.TwitterDashboard.Event.TopUserClickedEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class LabelUserClickable extends Label {
	public LabelUserClickable(String name, String screenName) {
		this.setText(name);
		this.setOnMouseEntered(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent evt)
			{
				setTextFill(Color.CYAN);
				setCursor(Cursor.HAND);
			}
		});
		
		this.setOnMouseExited(new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent evt)
			{
				setTextFill(Color.WHITE);
				setCursor(Cursor.DEFAULT);
			}
		});
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt)
			{
				fireEvent(new TopUserClickedEvent(screenName));
			}
		});
	}
}
