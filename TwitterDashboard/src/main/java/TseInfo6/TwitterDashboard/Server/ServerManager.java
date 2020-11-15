package TseInfo6.TwitterDashboard.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import TseInfo6.TwitterDashboard.TwitterRequestManager;

public class ServerManager {
	public HttpServer server;
    public ServerManager() throws IOException {
    	server = HttpServer.create(new InetSocketAddress(8080), 0);
    	ConnectedHandler handler=new ConnectedHandler();
        HttpContext context = server.createContext("/connected",handler);
        server.start();
    }
    public class ConnectedHandler implements HttpHandler
    {	
    	String query;
    	@Override
    	public void handle(HttpExchange exchange) throws IOException {
    		query=extractQuery(exchange);
	        setOauth_id_Verif(query);
	        writePage(exchange);
	        // Une fois qu'on a les donnees retournees par twitter, on procede a la suite de l'authentification
	        OAuthClient.getOAuthClient().upgradeTokens(server);
    	}
    	public String getQuery() {
    		return query;
    	}
    	/**
    	 * Ecriture de la page HTML
    	 * @param exchange
    	 * @throws IOException
    	 */
        public void writePage(HttpExchange exchange) throws IOException
        {
            //String response = "Vous etes connectes. Retournez a present sur l'application";
            
        	String response = getHtmlResponse();
        	
        	exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
    
    /**
     * A partir de l exchange, recupere la query
     * @param exchange
     * @return String : query retournee par Twitter
     */
    private String extractQuery(HttpExchange exchange) {
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        return query;
    }
    /**
     * Parse et stocke les tokens renvoyes par Twitter
     * @param query : query retourné par l'API twitter contenant les valeurs a stocker
     */
    public void setOauth_id_Verif(String query)
    {
    	// On extrait les tokens retournes par twitter, et on les stocke dans le Manager
    	String[] TwitterAnwser=query.split("&");
    	String OauthToken=TwitterAnwser[0].split("=")[1];
    	String OauthVerifier=TwitterAnwser[1].split("=")[1];
    	TwitterRequestManager.getManager().setOauth_token(OauthToken);
    	TwitterRequestManager.getManager().setOauth_verifier(OauthVerifier);
    }
    public HttpServer getServer()
    {
    	return server;
    }
    /**
     * 
     * @return String : Code HTML de la page a afficher
     */
    private String getHtmlResponse() {
    	return "<html lang='en'>\n" + 
    			"  <head>\n" + 
    			"    <meta charset='utf-8' />\n" + 
    			"    <title>Connection Page</title>\n" + 
    			"    <style type='text/css'>\n" + 
    			"      body {\n" + 
    			"        background-color: #212121;\n" + 
    			"        width: 100%;\n" + 
    			"        height: 100%;\n" + 
    			"      }\n" + 
    			"\n" + 
    			"      p {\n" + 
    			"        color: #1da1f2;\n" + 
    			"        font-size: 1.2em;\n" + 
    			"        text-align: center;\n" + 
    			"      }\n" + 
    			"\n" + 
    			"      .container {\n" + 
    			"        margin: 2em;\n" + 
    			"		padding-top: 240px;\n" + 
    			"        border: 1px solid #1da1f2;\n" + 
    			"      }\n" + 
    			"\n" + 
    			"      img {\n" + 
    			"        margin: 1em;\n" + 
    			"        width: 240px;\n" + 
    			"        height: 195px;\n" + 
    			"        position: absolute;\n" + 
    			"        left: -240px;\n" + 
    			"		top: 2em;\n" + 
    			"      }\n" + 
    			"    </style>\n" + 
    			"  </head>\n" + 
    			"\n" + 
    			"  <body>\n" + 
    			"    <div class='container'>\n" + 
    			"      <img\n" + 
    			"        id='bird'\n" + 
    			"        src='https://upload.wikimedia.org/wikipedia/fr/thumb/c/c8/Twitter_Bird.svg/1200px-Twitter_Bird.svg.png'\n" + 
    			"        alt=''\n" + 
    			"      />\n" + 
    			"      <p>Vous êtes bien connecté à Twitter</p>\n" + 
    			"      <p>Veuillez retourner sur l'application</p>\n" + 
    			"    </div>\n" + 
    			"\n" + 
    			"    <script\n" + 
    			"      src='https://code.jquery.com/jquery-3.4.1.min.js'\n" + 
    			"      integrity='sha256-CSXorXvZcTkaix6Yvo6HppcZGetbYMGWSFlBw8HfCJo='\n" + 
    			"      crossorigin='anonymous'\n" + 
    			"    ></script>\n" + 
    			"    <script\n" + 
    			"      src='https://code.jquery.com/ui/1.12.1/jquery-ui.min.js'\n" + 
    			"      integrity='sha256-VazP97ZCwtekAsvgPBSUwPFKdrwD3unUfSGVYrahUqU='\n" + 
    			"      crossorigin='anonymous'\n" + 
    			"    ></script>\n" + 
    			"    <script>\n" + 
    			"      $('document').ready(() => {\n" + 
    			"        let moveX = $('body').width() / 2 + $('#bird').width() / 2 ;\n" + 
    			"        //alert(moveX);\n" + 
    			"        $('#bird').animate({ left: '+=' + moveX }, 2000, 'easeOutBounce');\n" + 
    			"      });\n" + 
    			"    </script>\n" + 
    			"  </body>\n" + 
    			"</html>\n" + 
    			"";
    }

}