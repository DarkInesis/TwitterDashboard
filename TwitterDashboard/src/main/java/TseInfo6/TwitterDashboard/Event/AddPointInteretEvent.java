package TseInfo6.TwitterDashboard.Event;

import javafx.event.Event;
import javafx.event.EventType;

public class AddPointInteretEvent extends Event {
	private static final long serialVersionUID = 4L;
	public static final EventType<AddPointInteretEvent> modifPointInteretEvent = new EventType<>(ANY,"modifPointInteretEvent");
	public AddPointInteretEvent() {
		super(modifPointInteretEvent);
	}
}
