package TseInfo6.TwitterDashboard.Event;

import javafx.event.Event;
import javafx.event.EventType;


public class ToggleUserFavoriteClickedEvent extends Event{
	private static final long serialVersionUID = 2L;
	public static final EventType<ToggleUserFavoriteClickedEvent> toggleUserFavorite = new EventType<>(ANY,"toggleUserFavorite");
    final String screenName;
    final String imageUrl;
	public ToggleUserFavoriteClickedEvent(String screenName,String imageUrl) {
		super(toggleUserFavorite);
		this.screenName = screenName;
		this.imageUrl=imageUrl;
	}

	public String getScreenName() {
		return screenName;
	}
	public String getImageUrl()
	{
		return this.imageUrl;
	}

}