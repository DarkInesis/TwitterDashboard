package TseInfo6.TwitterDashboard.Model;

import java.util.List;

public class HashtagInfo {
	private String period;
	private String hashtagName; // without the # character
	private Long numberOfTweet;
	private Long globalRT;
	private Long globalFavorite;
	private List<TwitterUser> topFive;
	/**
	 * @param period : string which contains the date of begin and the date of end of our request
	 * @param hashtagName 
	 * @param numberOfTweet 
	 * @param globalRT : total number of retweets
	 * @param globalFavorite : total number of favorites
	 * @param domain
	 * @param topFive : contains the 5 user whose tweet whith the hashtag has best performed
	 */
	public HashtagInfo(String period, String hashtagName, Long numberOfTweet, Long globalRT, Long globalFavorite, List<TwitterUser> topFive) {
		super();
		this.period = period;
		this.hashtagName = hashtagName;
		this.numberOfTweet = numberOfTweet;
		this.globalRT = globalRT;
		this.globalFavorite = globalFavorite;
		this.topFive = topFive;
	}
	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}
	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}
	/**
	 * @return the hashtagName
	 */
	public String getHashtagName() {
		return hashtagName;
	}
	/**
	 * @param hashtagName the hashtagName to set
	 */
	public void setHashtagName(String hashtagName) {
		this.hashtagName = hashtagName;
	}
	/**
	 * @return the numberOfTweet
	 */
	public Long getNumberOfTweet() {
		return numberOfTweet;
	}
	/**
	 * @param numberOfTweet the numberOfTweet to set
	 */
	public void setNumberOfTweet(Long numberOfTweet) {
		this.numberOfTweet = numberOfTweet;
	}
	/**
	 * @return the globalRT
	 */
	public Long getGlobalRT() {
		return globalRT;
	}
	/**
	 * @param globalRT the globalRT to set
	 */
	public void setGlobalRT(Long globalRT) {
		this.globalRT = globalRT;
	}
	/**
	 * @return the globalFavorite
	 */
	public Long getGlobalFavorite() {
		return globalFavorite;
	}
	/**
	 * @param globalFavorite the globalFavorite to set
	 */
	public void setGlobalFavorite(Long globalFavorite) {
		this.globalFavorite = globalFavorite;
	}
	/**
	 * @return the topFive
	 */
	public List<TwitterUser> getTopFive() {
		return topFive;
	}
	/**
	 * @param topFive the topFive to set
	 */
	public void setTopFive(List<TwitterUser> topFive) {
		this.topFive = topFive;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((globalFavorite == null) ? 0 : globalFavorite.hashCode());
		result = prime * result + ((globalRT == null) ? 0 : globalRT.hashCode());
		result = prime * result + ((hashtagName == null) ? 0 : hashtagName.hashCode());
		result = prime * result + ((numberOfTweet == null) ? 0 : numberOfTweet.hashCode());
		result = prime * result + ((period == null) ? 0 : period.hashCode());
		result = prime * result + ((topFive == null) ? 0 : topFive.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HashtagInfo other = (HashtagInfo) obj;
		if (globalFavorite == null) {
			if (other.globalFavorite != null)
				return false;
		} else if (!globalFavorite.equals(other.globalFavorite))
			return false;
		if (globalRT == null) {
			if (other.globalRT != null)
				return false;
		} else if (!globalRT.equals(other.globalRT))
			return false;
		if (hashtagName == null) {
			if (other.hashtagName != null)
				return false;
		} else if (!hashtagName.equals(other.hashtagName))
			return false;
		if (numberOfTweet == null) {
			if (other.numberOfTweet != null)
				return false;
		} else if (!numberOfTweet.equals(other.numberOfTweet))
			return false;
		if (period == null) {
			if (other.period != null)
				return false;
		} else if (!period.equals(other.period))
			return false;
		if (topFive == null) {
			if (other.topFive != null)
				return false;
		} else if (!topFive.equals(other.topFive))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "HashtagInfo [period=" + period + ", hashtagName=" + hashtagName + ", numberOfTweet=" + numberOfTweet
				+ ", globalRT=" + globalRT + ", globalFavorite=" + globalFavorite + ", topFive="
				+ topFive + "]";
	}
	

}
