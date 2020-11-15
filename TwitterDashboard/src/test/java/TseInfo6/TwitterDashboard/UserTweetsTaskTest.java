package TseInfo6.TwitterDashboard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

import TseInfo6.TwitterDashboard.Model.Tweet;

class UserTweetsTaskTest {

	@Test
	void test() {

		
		CompletableFuture<ArrayList<Tweet>> promise = 
				CompletableFuture.supplyAsync(new UserTweetsTask("realDonaldTrump"));
		
		try {
			ArrayList<Tweet> tweets =  promise.get();
			System.out.println(tweets.size());
			assertTrue(tweets.size() >= 3120);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
		
		
	}

}
