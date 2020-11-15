package TseInfo6.TwitterDashboard.Deserializer;

import java.lang.reflect.Type;
import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import TseInfo6.TwitterDashboard.Utilities;
import TseInfo6.TwitterDashboard.Model.TwitterUser;

public class TwitterUserDeserializer implements JsonDeserializer<TwitterUser> {
	
	@Override
	public TwitterUser deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		
		JsonObject obj;
		if(json.isJsonArray()) {
			JsonArray arr = json.getAsJsonArray();
			obj = arr.get(0).getAsJsonObject();
		} else {
			obj = json.getAsJsonObject();
		}
		
		
		final String screenName = obj.get("screen_name").getAsString();
		final String name = obj.get("name").getAsString();
		final String location = obj.get("location").getAsString();
		final String description = obj.get("description").getAsString();
		final Long followersCount = obj.get("followers_count").getAsLong();
		final Long friendsCount = obj.get("friends_count").getAsLong();
		final boolean verified = obj.get("verified").getAsBoolean();
		final String imageUrl = obj.get("profile_image_url").getAsString();
		// get bigger images
				imageUrl.replace("_normal", "_bigger");
				final Date createdAt = Utilities.dateFromString(obj.get("created_at").getAsString());
				
				final Long statusesCount = obj.get("statuses_count").getAsLong();
		// Prise en compte du cas ou le user n'a pas de banniere
		try {
			final String bannerUrl=obj.get("profile_banner_url").getAsString()+"/600x200";
			return new TwitterUser(screenName, name, location, description, followersCount, friendsCount, verified, createdAt, imageUrl,bannerUrl, statusesCount);
		}catch (Exception e) {
			return new TwitterUser(screenName, name, location, description, followersCount, friendsCount, verified, createdAt, imageUrl, statusesCount);
		}
	}
	
}
