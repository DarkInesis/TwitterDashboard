package TseInfo6.TwitterDashboard;

import java.util.ArrayList;
import java.util.List;

import TseInfo6.TwitterDashboard.View.LabelUserClickable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class DisplayFavoriteController {
	@FXML
    private GridPane gridPaneUsers;

	@FXML
	private GridPane gridPaneHashtags;

	// This static method shall fill the GridPane dynamically when it's called
    public void fillVboxUsers(List<ArrayList<String>> listUsers){
    	int i =0;
		for (List<String> user : listUsers)
		{
			//create new objects (circle for the user image and label for the user name)
			//Label label=new Label();
			LabelUserClickable label=new LabelUserClickable(user.get(1), user.get(1));
			Circle circle = new Circle(12);
			// label.setText("@"+user.get(1));
			circle.setFill(new ImagePattern(new Image(user.get(0))));
			gridPaneUsers.add(circle,0, i);
			gridPaneUsers.add(label,1, i);
			RowConstraints r1=new RowConstraints(30);
			gridPaneUsers.getRowConstraints().add(r1);
			i++;
		}
    }

    //This static method shall fill the ListView dynamically when it's called
    public void fillVboxHashtags(List<String> listHashtags){
    	int i=0;
    	for(String hashtag : listHashtags){
            //create new object (label for the hashtag name)
        		Label label = new Label();
						//fill the text
            label.setText("#"+hashtag);
            //add them to the Vbox
			gridPaneHashtags.add(label, 0, i);
			RowConstraints r1=new RowConstraints(30);
			gridPaneHashtags.getRowConstraints().add(r1);
			i++;
         }
    }


    public void fillFields(List<ArrayList<String>> listUsers, List<String> listHashtags) {

    	fillVboxUsers(listUsers);
    	fillVboxHashtags(listHashtags);
    }
}
