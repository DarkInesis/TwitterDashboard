package TseInfo6.TwitterDashboard.Event;

import javafx.event.Event;
import javafx.event.EventType;

public class ToggleHashtagFavoriteClickedEvent extends Event{
	

	private static final long serialVersionUID = 3L;
	public static final EventType<ToggleHashtagFavoriteClickedEvent> toggleHashtagFavorite = new EventType<>(ANY,"toggleHashtagFavorite");
    final String hashtagName;
    
	public ToggleHashtagFavoriteClickedEvent(String hashtagName) {
		super(toggleHashtagFavorite);
		
		this.hashtagName = hashtagName;
	}

	public String getHashtagName() {
		return hashtagName;
	}

}
