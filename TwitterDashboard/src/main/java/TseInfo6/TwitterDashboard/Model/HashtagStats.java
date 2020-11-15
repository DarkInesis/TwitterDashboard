package TseInfo6.TwitterDashboard.Model;

import java.util.Objects;

public class HashtagStats {
	
	private Long tweetCount;
	private Long rtStat;
	private Long favStat;
	private String name;
	
	/**
	 * Adds two {@link HashtagStats} objects.
	 * All fields are summed except the name which is computed in another way.
	 * If both names are equal then teh result keeps the name. Else the result name is null.
	 * @param a first param
	 * @param b second param
	 * @return the sum
	 * @see average
	 */
	public static HashtagStats add(HashtagStats a, HashtagStats b) {
		String name = (a.getName() == null || b.getName() == null) || !a.getName().equals(b.getName()) ? null : a.getName(); 
		return new HashtagStats(a.getTweetCount() + b.getTweetCount(), 
				a.getRtStat() + b.getRtStat(), a.getFavStat() + b.getFavStat(), name);
	}
	
	/**
	 * Computes the average of a {@link HashtagStats} object.
	 * Simply divided RT and fav count by the tweetcount and sets the tweets count to 1.
	 * TweetCount is set to 1 to allow further stats with these objects. 
	 * @param st the object
	 * @return the average
	 */
	public static HashtagStats average(HashtagStats st) {
		long averageFav = Math.round((double) st.getFavStat() / st.getTweetCount());
		long averageRt = Math.round((double) st.getRtStat() / st.getTweetCount());
		
		return new HashtagStats(st.getTweetCount() == 0 ? 0L : 1L, averageRt, averageFav, st.name);
	}
	
	/**
	 * Return if object is empty, i.e. teh tweet count is equal to 0
	 * @return
	 */
	public boolean isEmpty() {
		return this.tweetCount == 0;
	}
	
	public HashtagStats(Long tweetCount, Long rtStat, Long favStat, String name) {
		super();
		this.tweetCount = tweetCount;
		this.rtStat = rtStat;
		this.favStat = favStat;
		this.name = name;
	}

	@Override
	public String toString() {
		return "HashtagStats [tweetCount=" + tweetCount + ", rtStat=" + rtStat + ", favStat=" + favStat + ", name="
				+ name + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(favStat, name, rtStat, tweetCount);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof HashtagStats)) {
			return false;
		}
		HashtagStats other = (HashtagStats) obj;
		return Objects.equals(favStat, other.favStat) && Objects.equals(name, other.name)
				&& Objects.equals(rtStat, other.rtStat) && Objects.equals(tweetCount, other.tweetCount);
	}

	public Long getTweetCount() {
		return tweetCount;
	}

	public void setTweetCount(Long tweetCount) {
		this.tweetCount = tweetCount;
	}

	public Long getRtStat() {
		return rtStat;
	}

	public void setRtStat(Long rtStat) {
		this.rtStat = rtStat;
	}

	public Long getFavStat() {
		return favStat;
	}

	public void setFavStat(Long favStat) {
		this.favStat = favStat;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
