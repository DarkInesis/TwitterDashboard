package TseInfo6.TwitterDashboard.Server;
// Source : http://fabiouechi.blogspot.com/2011/11/using-google-oauth-java-client-to.html
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthGetAccessToken;
import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;
import com.google.api.client.auth.oauth.OAuthHmacSigner;
import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.sun.net.httpserver.HttpServer;

import TseInfo6.TwitterDashboard.App;
import TseInfo6.TwitterDashboard.TwitterRequestManager;


public class OAuthClient {
	// On limite le nombre d'instance de cette classe à 1;
	private static class SingletonHolder {
		private static final OAuthClient instance=new OAuthClient();
	}
	public static OAuthClient getOAuthClient() {
		return SingletonHolder.instance;
	}
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	// Probablement à cacher
	private static String consumerKey="7blOefOxtEsTG7DjNxEF8qbzR";
	private static String callbacklURL="http://127.0.0.1";
	private static String consumerKey_secret="fJDqgMEfTGja2Vt9GM8004OS6s472qInel01ngGtp6Y8Jiem3Y";
	private static String token_secret="WWz1EQ7Ryln135pjLWblOcNyJRsG9eMV9srid5xqNnr7x";
	private static String access_token="1188826087234723841-BTJGliWtCun1Idq9YSTLSZn9nONJ5r";
	
	// Requetes_ENDPOINT
	final String get_Oauth_Authorize_Token_ENDPOINT="https://api.twitter.com/oauth/authenticate?oauth_token=";
	final String post_Oauth_Request_Token_ENDPOINT="https://api.twitter.com/oauth/request_token";
	final String post_Ouath_access_token_ENDPOINT="https://api.twitter.com/oauth/access_token";
	// Outils faisant les requetes
	OAuthHmacSigner signer;
	OAuthGetTemporaryToken requestToken;
	OAuthCredentialsResponse requestTokenResponse;
	OAuthAuthorizeTemporaryTokenUrl authorizeUrl;
	OAuthGetAccessToken accessToken;
	OAuthParameters parameters;
	HttpRequestFactory factory;
	
	/**
	 * Fait la requete de token aupres de Twitter puis ouvre une page internet
	 * sur laquelle l'utilisateur donne des droits a l'application
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void goToAuthorizeUrl() throws IOException, InterruptedException
	{
		signer=new OAuthHmacSigner();
		signer.clientSharedSecret=consumerKey_secret;
		
		//Step 1 : get a request token
		requestToken=new OAuthGetTemporaryToken(post_Oauth_Request_Token_ENDPOINT);
		requestToken.consumerKey=consumerKey;
		requestToken.transport=TRANSPORT;
		requestToken.signer=signer;
		requestTokenResponse = requestToken.execute();
		
		// updates signer's token shared secret
		signer.tokenSharedSecret = requestTokenResponse.tokenSecret;
		
		// Step 2 : Authorize temporary token URL;
		authorizeUrl = new OAuthAuthorizeTemporaryTokenUrl(get_Oauth_Authorize_Token_ENDPOINT);
		authorizeUrl.temporaryToken = requestTokenResponse.token;
		  
		// Ouverture de cet URL dans un nouvel onglet de navigateur
		App.getApp().getHostServices().showDocument(authorizeUrl.build());
		
	}
	
	/**
	 * Fait la requete d'accessToken aupres de Twitter, 
	 * puis enregistre les tokens dans le TwuitterRequestManager
	 * puis eteint le server
	 * @param server : server a eteindre lorsque la connexion sera terminee
	 * @throws IOException
	 */
	public void upgradeTokens(HttpServer server) throws IOException
	{
		// Step 3: Once the consumer has redirected the user back to the oauth_callback, upgrade temporary token
		// to access token
		  accessToken = new OAuthGetAccessToken(post_Ouath_access_token_ENDPOINT);  
		  accessToken.consumerKey = consumerKey;
		  accessToken.signer = signer;
		  accessToken.transport = TRANSPORT;
		  accessToken.temporaryToken = requestTokenResponse.token;
		  accessToken.verifier=TwitterRequestManager.getManager().getOauth_verifier();
		  
		  OAuthCredentialsResponse accessTokenResponse = new OAuthCredentialsResponse();
		  Map<String,String> map = new HashMap<>(); 
		  TwitterRequestManager.getManager().sendRequestAccessToken().thenAccept(answer-> 
		  {
			  // Recuperation des access token et des donnees utilisateur
			  String[] keyTokenPairs = answer.split("&");
			  for(String pair : keyTokenPairs)
			  {
				  String[] entry=pair.split("=");
				  map.put(entry[0], entry[1]);
			  }
			  String screen_name=map.get("screen_name");
			  // Mise a jour du statut de connexion
			  TwitterRequestManager.getManager().setConnectedAndScreenName(true,screen_name);
			  
			  accessTokenResponse.token=map.get("oauth_token");
			  accessTokenResponse.tokenSecret=map.get("oauth_token_secret");
			  TwitterRequestManager.getManager().setOauth_token(accessTokenResponse.token);
			  TwitterRequestManager.getManager().setOauth_token_secret(accessTokenResponse.tokenSecret);
			  // Une fois connecté, on ferme le server
			  server.stop(0);
			  // Mise a jour du tokenSharedSecret par l'access token secret
			  signer.tokenSharedSecret = accessTokenResponse.tokenSecret;
			  // Initialisation des parametres pour créer le header des requetes
			  parameters = new OAuthParameters();
			  parameters.consumerKey = consumerKey;
			  parameters.token = accessTokenResponse.token;
			  parameters.signer = signer;
			  // utilize accessToken to access protected resources
			  factory = TRANSPORT.createRequestFactory(parameters);
		  });
		  
	}
	/**
	 * @return the factory
	 */
	public HttpRequestFactory getFactory() {
		return factory;
	}
	public OAuthParameters getParameters()
	{
		return parameters;
	}
	public OAuthGetAccessToken getAccessToken()
	{
		return accessToken;
	}
	
}