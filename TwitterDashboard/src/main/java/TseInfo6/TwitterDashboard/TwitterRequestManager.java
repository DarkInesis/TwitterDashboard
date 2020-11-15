package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Deserializer.TweetDeserializer;
import TseInfo6.TwitterDashboard.Deserializer.TwitterUserDeserializer;
import TseInfo6.TwitterDashboard.Model.HashtagInfo;
import TseInfo6.TwitterDashboard.Model.HashtagStats;
import TseInfo6.TwitterDashboard.Model.HashtagStatsList;
import TseInfo6.TwitterDashboard.Model.Tweet;
import TseInfo6.TwitterDashboard.Model.TwitterUser;
import TseInfo6.TwitterDashboard.Server.OAuthClient;



public final class TwitterRequestManager extends Observable{

	private HttpClient client;
	private static String BEARER_TOKEN;
	private static Gson gson;
	// Probablement à cacher
	private static String consumerKey="7blOefOxtEsTG7DjNxEF8qbzR";
	private static String callbacklURL="https://127.0.0.1";
	private static String oauth_version="1.0";
	private static String oauth_nonce;
	private static String oauth_signature;
	private static String oauth_signature_methode="HMAC-SHA1";
	private static String oauth_timestamp;
	private static String consumerKey_secret="fJDqgMEfTGja2Vt9GM8004OS6s472qInel01ngGtp6Y8Jiem3Y";
	private static String token_secret="WWz1EQ7Ryln135pjLWblOcNyJRsG9eMV9srid5xqNnr7x";
	private static String signing_key;
	private static String access_token="1188826087234723841-BTJGliWtCun1Idq9YSTLSZn9nONJ5r";
	// Variables remplies lors de l'identification
	private static String oauth_token;
	private static String oauth_token_secret;
	private static String oauth_verifier;
	private boolean isConnected=false;
	private String screen_name="";

	/* Requests */
	final String USER_LOOKUP_ENDPOINT = "https://api.twitter.com/1.1/users/lookup.json?screen_name=";
	final String USER_TIMELINE_ENDPOINT = "https://api.twitter.com/1.1/statuses/user_timeline.json?count=200&screen_name=";
	final String HASHTAG_ENDPOINT="https://api.twitter.com/1.1/search/tweets.json?count=100&result_type=popular&&q=%23";
	final String get_Oauth_Token_ENDPOINT="https://api.twitter.com/oauth/authenticate?oauth_token=";
	final String post_Oauth_Request_Token_ENDPOINT="https://api.twitter.com/oauth/request_token";
	final String post_Ouath_access_token_ENDPOINT="https://api.twitter.com/oauth/access_token";
	private static class SingletonHolder {
		private static final TwitterRequestManager instance = new TwitterRequestManager();
	}

	private TwitterRequestManager() {
		BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAANomAwEAAAAAHNGUJZELY%2BHKg9qQg03hzTvqec4%3DErTczLGzUWJrwBxsAIjpp6NX88NNddFuVLpJEgZTiEX2OlbhOx";
		this.client = HttpClient.newHttpClient();
		TwitterRequestManager.gson = new GsonBuilder()
            		.registerTypeAdapter(TwitterUser.class, new TwitterUserDeserializer())
            		.registerTypeAdapter(Tweet.class, new TweetDeserializer())
	            	.create();
	}

	public static TwitterRequestManager getManager() {
		return SingletonHolder.instance;
	}

	/* ######################################################################################## */
	/* 									Core Requests 											*/
	/* ######################################################################################## */

	/**
	 * Build the actual {@link HttpRequest} which will by used to make requests
	 * 
	 * @param uri : the uri where is the resource
	 * @see getResponseBodyAsync
	 * @return {@link HttpRequest}
	 */
	private HttpRequest buildHttpRequest(final String uri)  {

		return HttpRequest.newBuilder().
				uri(URI.create(uri))
				.setHeader("Authorization", "Bearer " + BEARER_TOKEN)
				.build();
	}
	
	/**
	 * Retrieves the body of the http response ASYNCHRONOUSLY
	 * @param uri : the uri where is the resource
	 * @return {@link CompletableFuture} <{@link String}>
	 */
	private CompletableFuture<String> getResponseBodyAsync(final String uri) {
			HttpRequest request = buildHttpRequest(uri);
			return client.sendAsync(request, BodyHandlers.ofString())
		      .thenApply(HttpResponse::body);
	}
	

	public CompletableFuture<String> sendRequestAccessToken()
	{
		HttpRequest request=HttpRequest.newBuilder().
		uri(URI.create("https://api.twitter.com/oauth/access_token"))
		.setHeader("Authorization",OAuthClient.getOAuthClient().getAccessToken().createParameters().getAuthorizationHeader())
		.build();
		//HttpRequest request = buildHttpRequest(uri);
		return client.sendAsync(request, BodyHandlers.ofString())
	      .thenApply(HttpResponse::body);
	}

	/**
	 * Retrieves a {@link TwitterUser} by screen_name ASYNCRONOUSLY
	 * @param username : the screen_name
	 * @return {@link CompletableFuture}<{@link TwitterUser}>
	 */
	public CompletableFuture<TwitterUser> getUserByNameAsync(final String username) {

		String uri = USER_LOOKUP_ENDPOINT + username;
		return getResponseBodyAsync(uri).thenApply(body -> {
			//Check if body is ill formed
			if(body.startsWith("{\"errors\":")) {
				throw new RuntimeException("The requested user was not found !");
			}
			
			TwitterUser user = TwitterRequestManager.gson.fromJson(body, TwitterUser.class);
			return user;
		});
	}

	/**
	 * Retrieves tweets of a user ASYNCHRONOUSLY
	 * @param usernaùe: the screen_name
	 * @see getResponseBodyAsync
	 * @return {@link CompletableFuture}<{@link Tweet}[]>
	 */
	public CompletableFuture<Tweet[]> getUserTweetsAsync(final String username)  {
		String uri = USER_TIMELINE_ENDPOINT + username;
		return getResponseBodyAsync(uri).thenApply(body -> {
			Tweet[] tweets = TwitterRequestManager.gson.fromJson(body, Tweet[].class);
			return tweets;
		});
	}
	
	/**
	 * getHashtagTweets returns the list of tweets which are tagged with the correct hashtag
	 * @param hastagName : string of the hashtag without the character "#"
	 * @return {@link CompletableFuture} <{@link Tweet}[]>
	 */
	public CompletableFuture<Tweet[]> getHashtagTweetsAsync(final String hastagName)  {
		
		String uri = HASHTAG_ENDPOINT + hastagName;
		return getResponseBodyAsync(uri).thenApply(body -> {
			
			final String regex = "\"statuses\":(.+),\"search.+";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(body);
		    matcher.find();
		    final String bodyTruncated = matcher.group(1);
		    
		    if(bodyTruncated.equals("[]")) {
				throw new RuntimeException("The hashtag you requested was not found !");
			}
			Tweet[] tweets = TwitterRequestManager.gson.fromJson(bodyTruncated, Tweet[].class);
			
			return tweets;
		});
	}

	/* ######################################################################################## */
/* 									Compound Requests 											*/
	/* ######################################################################################## */
	
	/**
	 * Gets all info which can be gathered for a hashtag
	 * @param hastagName : string of the hashtag without the character "#"
	 * @return {@link CompletableFuture} <{@link HashtagInfo}>
	 */
	public CompletableFuture<HashtagInfo> getHahstagInfoAsync(final String hastagName)  {
		return 	getHashtagTweetsAsync(hastagName)
		.thenApply(tweets ->{
			String period=null;
			long globalRT = 0, globalFavorite = 0;
			
			List<TwitterUser> topFive=new ArrayList<TwitterUser>();
			List<Tweet> arrayTweetTop=new ArrayList<Tweet>(Arrays.asList(tweets));
			if (arrayTweetTop.size()>=5) // Cas classique où il y a plus de 5 tweets
			{
				arrayTweetTop.forEach(tweetTop->
				{if(!topFive.contains(tweetTop.getAuthor()) && topFive.size()<5)
						{
							topFive.add(tweetTop.getAuthor());
						};
				});
			}
			else // Cas particulier où il y a moins de 5 tweets, on prend donc tous les auteurs
			{
				arrayTweetTop.forEach(tweetTop->
				{if(!topFive.contains(tweetTop.getAuthor()))
						{
							topFive.add(tweetTop.getAuthor());
						};
				});
			}
			if (tweets.length>0)
			{
				Date dateMin=tweets[0].getCreatedAt();
				Date dateMax=tweets[0].getCreatedAt();

		    	for (Tweet t : tweets) {
		    		globalRT += t.getRetweetCount();
		    		globalFavorite += t.getFavoriteCount();
		    		if(t.getCreatedAt().compareTo(dateMax)>0)
		    		{
		    			dateMax=t.getCreatedAt();
		    		}
		    		else if(t.getCreatedAt().compareTo(dateMin)<0)
		    		{
		    			dateMin=t.getCreatedAt();
		    		}
		    	}
		    	period = Utilities.dateToString(dateMin) + " - " + Utilities.dateToString(dateMax);
			}
			HashtagInfo hashtag = new HashtagInfo(period,hastagName, (long) tweets.length, globalRT, globalFavorite,topFive);
	    	return hashtag;
		});
	}


	/* ######################################################################################## */
	/* 									Synchronous Requests 									*/
	/* ######################################################################################## */

	/**
	 * Gets body of response SYNCHRONOUSLY
	 * @param uri : uri of resource
	 * @return String : Response body
	 * @throws IOException InterruptedException
	 */
	private String getResponseBody(final String uri) throws IOException, InterruptedException {
		HttpRequest request = buildHttpRequest(uri);
		return client.send(request, BodyHandlers.ofString()).body();
	}

	/**
	 * Gets all tweets of a user SYNCRONOUSLY
	 * @param username : screen_name
	 * @param maxId for chained requests
	 * @return {@link Tweet}[]
	 * @throws IOException
	 * @throws InterruptedException
	 * @see UserTweetsTask
	 */
	public Tweet[] getUserTweets(final String username, Long maxId) throws IOException, InterruptedException {
		String uri = USER_TIMELINE_ENDPOINT + username;

		if(maxId != null) {
			uri += "&max_id=" + maxId.toString();
		}

		String body = getResponseBody(uri);
		Tweet[] tweets = null;
		try {
			tweets = TwitterRequestManager.gson.fromJson(body, Tweet[].class);
		} catch(JsonParseException ex) {
			ex.printStackTrace();
		}
		return tweets;

	}


	/* ######################################################################################## */
	/* 									Getter/Setters for tokens											*/
	/* ######################################################################################## */



	/**
	 * @return the access_token
	 */
	public String getAccess_token() {
		return access_token;
	}

	/**
	 * @param access_token the access_token to set
	 */
	public void setAccess_token(String access_token) {
		TwitterRequestManager.access_token = access_token;
	}

	/**
	 * @return the oauth_token
	 */
	public String getOauth_token() {
		return oauth_token;
	}

	/**
	 * @param oauth_token the oauth_token to set
	 */
	public void setOauth_token(String oauth_token) {
		TwitterRequestManager.oauth_token = oauth_token;
	}

	/**
	 * @return the oauth_token_secret
	 */
	public String getOauth_token_secret() {
		return oauth_token_secret;
	}

	/**
	 * @param oauth_token_secret the oauth_token_secret to set
	 */
	public void setOauth_token_secret(String oauth_token_secret) {
		TwitterRequestManager.oauth_token_secret = oauth_token_secret;
	}

	/**
	 * @return the oauth_verifier
	 */
	public String getOauth_verifier() {
		return oauth_verifier;
	}

	/**
	 * @param oauth_verifier the oauth_verifier to set
	 */
	public void setOauth_verifier(String oauth_verifier) {
		TwitterRequestManager.oauth_verifier = oauth_verifier;
	}

	/**
	 * @return the isConnected
	 */
	public  boolean isConnected() {
		return isConnected;
	}
	public String getScreenName()
	{
		return this.screen_name;
	}
	public void setConnectedAndScreenName(boolean isConnected, String screen_name) {
		this.isConnected = isConnected;
		this.screen_name=screen_name;
		setChanged();
		notifyObservers(screen_name);
	}
	
	/* ######################################################################################## */
	/* 									Statistics											*/
	/* ######################################################################################## */

	/**
	 * Get the repartition of user/hashtag in user's favorites
	 * The total sums up to 100 unless there is no user nor hashtag inside favorites
	 * in that case both are 0
	 * @param username scree_name
	 * @return int[2] : int[0] is hashtag percentage and int[1] is user percentage
	 * @throws SQLException
	 */
	public int[] getPercentageUserHashtagOfFavorite(final String username) throws SQLException {
		int favHashCount = DatabaseManager.getManager().getFavoriteHashtagCount(username);
		int favUserCOunt = DatabaseManager.getManager().getFavoriteUserCount(username);
		
		int sum = favHashCount + favUserCOunt;
		int[] res;
		
		if(sum != 0) {
			double hashPercentage = ( (double) favHashCount / sum) * 100.0;
			double userPercentage = ((double) favUserCOunt / sum) * 100.0;
			
			int hashPercentageRounded = (int) Math.floor(hashPercentage);
			int userPercentageRounded = (int) Math.floor(userPercentage);
			
			if(hashPercentageRounded + userPercentageRounded != 100) {
				if (hashPercentage - hashPercentageRounded > userPercentage - userPercentageRounded) {
					hashPercentageRounded += 1;
				}
				else {
					userPercentageRounded += 1;
				}
			}
			res = new int[] {hashPercentageRounded, userPercentageRounded};
		}
		else {
			res = new int[] {0, 0};
		}
		
		return res;
	}
	
	/**
	 * Gets stats for each hashtag inside the category ASYNCHRONOUSLY
	 * Converts a List of {@link CompletableFuture} into a {@link CompletableFuture} of List
	 * @param username the username
	 * @param category the category
	 * @return {@link CompletableFuture}<{@link HashtagStatsList}> 
	 * @throws SQLException
	 * @throws IOException
	 */
	public CompletableFuture<HashtagStatsList> getHashtagStatsOnCategoryAsync(final String username, final String category) throws SQLException, IOException {
		
		DatabaseManager manager = DatabaseManager.getManager();
		List<String> hashtags = manager.getHashtagsInCategory(username, category);
		
		List<CompletableFuture<HashtagInfo>> hashtagInfoList = hashtags.stream()
		.map( (String hashtag) -> getHahstagInfoAsync(hashtag))
		.collect(Collectors.toList());
		
		
		CompletableFuture<Void> voidFuture = 
		CompletableFuture.allOf(hashtagInfoList.toArray(new CompletableFuture[hashtagInfoList.size()]));
		
		CompletableFuture<List<HashtagInfo>> futureList = voidFuture.thenApply(voidValue -> {
			return hashtagInfoList.stream()
					   .map(hashtagInfoFuture -> hashtagInfoFuture.join())
			           .collect(Collectors.toList());
		});
		
		return futureList.thenApply(infoList -> {
			//individual stats
			List<HashtagStats> res = infoList.stream().map( elt -> {
				return new HashtagStats(elt.getNumberOfTweet(), elt.getGlobalRT(), elt.getGlobalFavorite(), elt.getHashtagName());
			})
			.collect(Collectors.toList());
		
			return new HashtagStatsList(res, category);
		
		});
	}
	
	/**
	 * Gets total stats (by summing) for all categories 
	 * @param stats : a list of stats. Each list element is itself a wrapper around a list of hashtag 
	 * stats for each hashatg inside the category
	 * @return {@link List}<{@link HashtagStats}>
	 * @see getTotalHashtagStatsForACategory
	 */
	public List<HashtagStats> getTotalHashtagStatsForEachCategory(List<HashtagStatsList> stats) {
		return stats.stream().map( (singleCategoryStats) -> getTotalHashtagStatsForACategory(singleCategoryStats))
		.collect(Collectors.toList());
	}
	
	/**
	 * Gets stats sum for a single category
	 * @param singleStats a list of stats for each hashtag inside category
	 * @return {@link HashtagStats}
	 */
	public HashtagStats getTotalHashtagStatsForACategory(HashtagStatsList singleStats) {
		String category = singleStats.getCategory();
		
		HashtagStats totalCategoryStats = singleStats.getHashtagsList().stream()
				.map( (elt) -> {
					elt.setName(category);
					return elt;
				})
				.reduce(new HashtagStats(0L, 0L, 0L, category), HashtagStats::add);
		
		
		return totalCategoryStats;
	}
	
	/**
	 * Converts a List of {@link CompletableFuture} into a {@link CompletableFuture} of List ASYNCHRONOUSLY
	 * Get stats for all of the categories
	 * @param username the current user name
	 * @param categories the categories to get stats on
	 * @return {@link CompletableFuture}<{@link List}<{@link HashtagStatsList}>>
	 * @throws SQLException
	 * @see {@link HashtagStats}
	 */
	public CompletableFuture<List<HashtagStatsList>> getHashtagStatsForAllCategoriesAsync(final String username, List<String> categories) throws SQLException {
		
		List<CompletableFuture<HashtagStatsList>> requests = categories.stream().map( (String category) -> {
			try {
				CompletableFuture<HashtagStatsList> res = getHashtagStatsOnCategoryAsync(username, category);
				return res;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		})
		.collect(Collectors.toList());
		
		CompletableFuture<Void> voidFuture = 
		CompletableFuture.allOf(requests.toArray(new CompletableFuture[requests.size()]));
		
		CompletableFuture<List<HashtagStatsList>> futureList = voidFuture.thenApply(voidValue -> {
			return requests.stream()
					   .map(hashtagStatsFuture -> hashtagStatsFuture.join())
			           .collect(Collectors.toList());
		});
		
		return futureList;
	}
	
	
}
