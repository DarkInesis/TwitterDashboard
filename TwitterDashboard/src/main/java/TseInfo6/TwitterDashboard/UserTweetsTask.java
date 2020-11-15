package TseInfo6.TwitterDashboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.Supplier;

import TseInfo6.TwitterDashboard.Model.Tweet;

public class UserTweetsTask implements Supplier<ArrayList<Tweet>> {

	private String username;

	public UserTweetsTask(String username) {
		super();
		this.username = username;
	}

	@Override
	public ArrayList<Tweet> get(){

		ArrayList<Tweet> resultList = new ArrayList<>();
		Long lastMinId = null;
		Long limit = 3120L; // 97.5% of 3200 which is the maximum allowed
		int retries = 0;

		do
		{
			try {
				TwitterRequestManager manager = TwitterRequestManager.getManager();
				Tweet[] tweets =
						manager.getUserTweets(username, lastMinId != null ? lastMinId - 1 : null);

				if(tweets != null && tweets.length > 1) {
					if(lastMinId == null) {
						limit = Math.min(tweets[0].getAuthor().getStatusesCount(), 3120);
					}
					retries = 0;
					lastMinId = Utilities.getMinIdOfTweetArray(tweets);
					resultList.addAll(Utilities.arrayToList(tweets));
				} else {
					if(retries < 30 && resultList.size() < limit) {
						++retries;
						continue;
					} else break;
				}
			}
			catch(InterruptedException ex) {
				ex.printStackTrace();
				continue;
			}
			catch(IOException ex) {
				throw new RuntimeException(ex);
			} 
		} while(true);

		return resultList;
	}

}
