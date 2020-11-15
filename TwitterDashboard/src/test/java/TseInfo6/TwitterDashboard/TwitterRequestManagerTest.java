package TseInfo6.TwitterDashboard;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import TseInfo6.TwitterDashboard.Database.DatabaseManager;
import TseInfo6.TwitterDashboard.Model.HashtagStats;

class TwitterRequestManagerTest {

	@Test
	void testHashtagStats() {
		try {
			//Fill database
			DatabaseManager manager = DatabaseManager.getManager();
			manager.registerUser("Astro");
			manager.addCategory("Astro", "Space");
			manager.insertHashtagIntoCategory("Astro", "Space", "Space");
			manager.insertHashtagIntoCategory("Astro", "Space", "Nasa");
			manager.insertHashtagIntoCategory("Astro", "Space", "Asteroids");
			manager.insertHashtagIntoCategory("Astro", "Space", "Sky");

			
			TwitterRequestManager.getManager().getHashtagStatsOnCategoryAsync("Astro", "Space")
			.thenAccept( listOfHashStats -> {
				System.out.println(listOfHashStats);
				HashtagStats stats = TwitterRequestManager.getManager()
						.getTotalHashtagStatsForACategory(listOfHashStats);
				System.out.println(stats);
			}).join();
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testHashtagPercentage() {
		DatabaseManager manager = DatabaseManager.getManager();
		manager.registerUser("Astro");
		manager.addHastagToFavorite("Astro", "Zone");
		manager.addHastagToFavorite("Astro", "Australia");
		manager.addHastagToFavorite("Astro", "Rap");
		manager.addHastagToFavorite("Astro", "More");
		manager.addUserToFavorite("Astro", "Ninho", "url/to/Ninho");
		manager.addUserToFavorite("Astro", "Heuss", "url/to/Heuss");
		
		try {
			int[] res = TwitterRequestManager.getManager().getPercentageUserHashtagOfFavorite("Astro");
			System.out.println(res[0] + "% | " + res[1] + "%");
			assertEquals(67, res[0]);
			assertEquals(33, res[1]);
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test 
	void testHashtagStatsForAllCategories() {
		try {
			//Fill database
			DatabaseManager manager = DatabaseManager.getManager();
			manager.registerUser("Astro");
			manager.addCategory("Astro", "Sport");
			manager.insertHashtagIntoCategory("Astro", "Sport", "Foot");
			manager.insertHashtagIntoCategory("Astro", "Sport", "Basketball");
			manager.insertHashtagIntoCategory("Astro", "Sport", "Handball");
			manager.insertHashtagIntoCategory("Astro", "Sport", "Soccer");

			List<String> categories = manager.getCategories("Astro");
			
			TwitterRequestManager.getManager().getHashtagStatsForAllCategoriesAsync("Astro", categories)
			.thenAccept( listOfHashStats -> {
				System.out.println("##########################");
				System.out.println("FOr all categories: ");
				System.out.println(listOfHashStats);
				
				assertTrue(listOfHashStats.size() == categories.size());
			}).join();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail();
		}
	}

}
