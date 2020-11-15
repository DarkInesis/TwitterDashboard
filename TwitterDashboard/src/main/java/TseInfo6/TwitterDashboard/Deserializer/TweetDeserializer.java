package TseInfo6.TwitterDashboard.Deserializer;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import TseInfo6.TwitterDashboard.Utilities;
import TseInfo6.TwitterDashboard.Model.Tweet;
import TseInfo6.TwitterDashboard.Model.TwitterUser;

public class TweetDeserializer implements JsonDeserializer<Tweet> {
	
	@Override
	public Tweet deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject obj = json.getAsJsonObject();
		
		final Date createdAt = Utilities.dateFromString(obj.get("created_at").getAsString());
		ArrayList<String> hashtags = new ArrayList<String>();
		
		JsonObject entities = obj.get("entities").getAsJsonObject();
		JsonArray jsonHashtags = entities.get("hashtags").getAsJsonArray();
		jsonHashtags.forEach(element -> {
			 String hashtag = element.getAsJsonObject().get("text").getAsString();
			 hashtags.add(hashtag);
		});
		
		Gson gson= new GsonBuilder()
        		.registerTypeAdapter(TwitterUser.class, new TwitterUserDeserializer())
        		.create();
        TwitterUser author = gson.fromJson(obj.get("user").getAsJsonObject(), TwitterUser.class);
       
		final Long retweetCount = obj.get("retweet_count").getAsLong();
		final Long favoriteCount = obj.get("favorite_count").getAsLong();
		final Long id = obj.get("id").getAsLong();
		
		return new Tweet(createdAt, hashtags, retweetCount, favoriteCount, author, id);
		
	}

}