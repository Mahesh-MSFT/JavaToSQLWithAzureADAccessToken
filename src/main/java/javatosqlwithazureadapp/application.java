package javatosqlwithazureadapp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

import javatosqlwithazureadapp.application;

@SpringBootApplication
@RestController
public class application {
	
	@Value("${Java2SQLWithAAD_ClientID}")
	private String clientID;
	
	@Value("${Java2SQLWithAAD_ClientSecret}")
	private String clientSecret;
	
	@Value("${Java2SQLWithAAD_TokenEndpoint}")
	private String tokenEndpoint;
	
	@Value("${Java2SQLWithAAD_DBServer}")
	private String dbServer;
	
	@Value("${Java2SQLWithAAD_DBName}")
	private String dbName;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(application.class, args);
	}

	@RequestMapping("/")
	Map<String, String> home() {
		Map<String, String> ret = new HashMap<String, String>();
		String accessToken = getAccessTokenFromAzureAD(tokenEndpoint, clientID, clientSecret);
		ret = getData(dbServer, dbName, accessToken);
		return ret;
	}
	
	String getAccessTokenFromAzureAD(String tokenEndpoint, String clientID, String clientSecret) {
		
		String accessToken = "";
		
		try {
			 	AuthenticationContext context = new AuthenticationContext(tokenEndpoint, false, Executors.newFixedThreadPool(1));
			    ClientCredential cred = new ClientCredential(clientID, clientSecret);
			    Future<AuthenticationResult> future = context.acquireToken("https://database.windows.net/", cred, null);
			    accessToken = future.get().getAccessToken();
		}
		catch(Exception e)
		{
			accessToken = e.getMessage();
		}
		
		return accessToken;
	}
	
	Map<String, String> getData(String dbServer, String dbName, String accessToken) {
		
		Map<String, String> ret = new HashMap<String, String>();
		
		try {
				SQLServerDataSource ds = new SQLServerDataSource();
	
		        ds.setServerName(dbServer);
		        ds.setDatabaseName(dbName);
		        ds.setAccessToken(accessToken);
		        ds.setHostNameInCertificate("*.database.windows.net");
		        
		        Connection connection = ds.getConnection();

		        ResultSet rs = connection.createStatement()
		        		.executeQuery("SELECT CartItem, Count(CartItem) from shopping group by CartItem");
		        while(rs.next()){
		        	ret.put(rs.getString(1), rs.getString(2));
    	        }
		        
		        connection.close();

		}
		catch(Exception e) {
			ret.put("Exception: ", e.getMessage());
		}
		
		return ret;
	}
}
