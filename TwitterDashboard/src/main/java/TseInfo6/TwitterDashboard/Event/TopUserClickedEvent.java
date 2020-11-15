package TseInfo6.TwitterDashboard.Event;

import javafx.event.Event;
import javafx.event.EventType;

public class TopUserClickedEvent extends Event{
	

	private static final long serialVersionUID = 1L;
	public static final EventType<TopUserClickedEvent> CUSTOM_EVENT_TYPE = new EventType<>(ANY,"CUSTOM_EVENT_TYPE");
    final String screenName;
    
	public TopUserClickedEvent(String screenName) {
		super(CUSTOM_EVENT_TYPE);
		
		this.screenName = screenName;
	}

	public String getScreenName() {
		return screenName;
	}

}
