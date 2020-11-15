package TseInfo6.TwitterDashboard.Database;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DatabaseManagerTest {

	@Test
	void testDatabaseManager() {
		DatabaseManager manager = DatabaseManager.getManager();
		try {
			//Users
			manager.registerUser("Vincent");
			manager.registerUser("Leo");
			manager.printUsers();
			
			//Categories
			manager.addCategory("Vincent", "Babyfoot");
			manager.addCategory("Leo", "Babyfoot");
			manager.addCategory("Leo", "Babyfoot");
			List<String> categories = manager.getCategories("Vincent");
			List<String> categories2 = manager.getCategories("Leo");
			assertEquals("[Babyfoot]", categories.toString());
			assertEquals("[Babyfoot]", categories2.toString());
		
			
			//Favorites Vincent
			manager.addHastagToFavorite("Vincent", "MerceLaZone");
			manager.addHastagToFavorite("Vincent", "MerceLaZone");
			manager.addUserToFavorite("Vincent", "Jul", "img/to/Jul");
			manager.addUserToFavorite("Vincent", "Jul", "img/to/Jul");
			manager.addUserToFavorite("Leo", "Jul", "img/to/Jul");
			manager.addUserToFavorite("Leo", "Népal", "img/to/Népal");
			manager.addUserToFavorite("Leo", "Nekfeu", "img/to/Nekfeu");
			List<String> favHashtags = manager.getFavoriteHashtags("Vincent");
			assertEquals("[Mercelazone]", favHashtags.toString());
			List<ArrayList<String>> favUsers = manager.getFavoriteUsers("Vincent");
			assertEquals("[[img/to/Jul, Jul]]", favUsers.toString());

			//Category Léo
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Fanny");
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Victoire");
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Victoire");
			manager.insertHashtagIntoCategory("Leo", "Babyfoot", "Fanny");
			manager.insertHashtagIntoCategory("Leo", "Babyfoot", "Défaite");
			List<String> hashtags = manager.getHashtagsInCategory("Vincent", "Babyfoot");
			assertEquals("[Fanny, Victoire]", hashtags.toString());
			
			List<String> hashtags2 = manager.getHashtagsInCategory("Leo", "Babyfoot");
			assertEquals("[Fanny, Défaite]", hashtags2.toString());
			
			//Deletion of Favorite
			manager.removeHashtagFromFavorite("Vincent", "MerceLaZone");
			List<String> favHashtagsAfterDeletion = manager.getFavoriteHashtags("Vincent");
			assertEquals("[]", favHashtagsAfterDeletion.toString());
			
			manager.removeUserFromFavorite("Vincent", "Jul");
			List<ArrayList<String>> favUsersAfterDeletionVincent = manager.getFavoriteUsers("Vincent");
			assertEquals("[]", favUsersAfterDeletionVincent.toString());
			
			manager.removeUserFromFavorite("Leo", "Népal");
			List<ArrayList<String>> favUsersAfterDeletionLeo = manager.getFavoriteUsers("Leo");
			assertEquals("[[img/to/Jul, Jul], [img/to/Nekfeu, Nekfeu]]", favUsersAfterDeletionLeo.toString());
			
			//Deletion of categories
			manager.addCategory("Vincent", "Onlyremaining");
			manager.addCategory("Leo", "Second");
			manager.removeCategory("Vincent", "Babyfoot");
			manager.removeCategory("Leo", "Babyfoot");
			manager.removeCategory("Leo", "Babyfoot"); //Prints error on console because category is not found !
			
			List<String> categoriesVincentAfterDeletion = manager.getCategories("Vincent");
			List<String> categoriesLeoAfterDeletion = manager.getCategories("Leo");
			assertEquals("[Onlyremaining]", categoriesVincentAfterDeletion.toString());
			assertEquals("[Second]", categoriesLeoAfterDeletion.toString());
			
			//Reset
			manager.addCategory("Vincent", "Babyfoot");
			manager.removeCategory("Vincent", "Onlyremaining");
			manager.addCategory("Leo", "Babyfoot");
			manager.removeCategory("Leo", "Second");
			
			//Deletion of hashtag inside categories
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Défaite");
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Victoire");
			manager.insertHashtagIntoCategory("Vincent", "Babyfoot", "Test");
			manager.insertHashtagIntoCategory("Leo", "Babyfoot", "Victoire");
			manager.insertHashtagIntoCategory("Leo", "Babyfoot", "Défaite");
			manager.insertHashtagIntoCategory("Leo", "Babyfoot", "Matchnul");
			
			manager.removeHashtagFromCategory("Leo", "Babyfoot", "Victoire");
			manager.removeHashtagFromCategory("Vincent", "Babyfoot", "Défaite");
			
			
			List<String> hashtagsVincentAfterDeletion = manager.getHashtagsInCategory("Vincent", "Babyfoot");
			assertEquals("[Victoire, Test]", hashtagsVincentAfterDeletion.toString());
			List<String> hashtagsLeoAfterDeletion = manager.getHashtagsInCategory("Leo", "Babyfoot");
			assertEquals("[Défaite, Matchnul]", hashtagsLeoAfterDeletion.toString());
			
			//Reset
			manager.removeCategory("Vincent", "Babyfoot");
			manager.removeCategory("Leo", "Babyfoot");
			
		} catch (SQLException e) {
			e.printStackTrace();
			fail();
		}
		finally {
			manager.close();
		}
	}

}
