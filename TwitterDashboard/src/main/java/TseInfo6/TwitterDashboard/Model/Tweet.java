package TseInfo6.TwitterDashboard.Model;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Tweet implements Comparable<Tweet>{
	
	private Date createdAt;
	private ArrayList<String> hashtags;
	private Long retweetCount;
	private Long favoriteCount;
	private TwitterUser author;
	private Long id;
	

	public Tweet(Date createdAt, ArrayList<String> hashtags, Long retweetCount, Long favoriteCount, TwitterUser author,
			Long id) {
		super();
		this.createdAt = createdAt;
		this.hashtags = hashtags;
		this.retweetCount = retweetCount;
		this.favoriteCount = favoriteCount;
		this.author = author;
		this.id = id;
	}


	@Override
	public String toString() {
		return "Tweet [createdAt=" + createdAt + ", hashtags=" + hashtags + ", retweetCount=" + retweetCount
				+ ", favoriteCount=" + favoriteCount + ", author=" + author + ", id=" + id + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(author, createdAt, favoriteCount, hashtags, id, retweetCount);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Tweet)) {
			return false;
		}
		Tweet other = (Tweet) obj;
		return Objects.equals(author, other.author) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(favoriteCount, other.favoriteCount) && Objects.equals(hashtags, other.hashtags)
				&& Objects.equals(id, other.id) && Objects.equals(retweetCount, other.retweetCount);
	}


	public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	public ArrayList<String> getHashtags() {
		return hashtags;
	}

	public void setHashtags(ArrayList<String> hashtags) {
		this.hashtags = hashtags;
	}

	public Long getRetweetCount() {
		return retweetCount;
	}

	public void setRetweetCount(Long retweetCount) {
		this.retweetCount = retweetCount;
	}

	public Long getFavoriteCount() {
		return favoriteCount;
	}

	public void setFavoriteCount(Long favoriteCount) {
		this.favoriteCount = favoriteCount;
	}

	public TwitterUser getAuthor() {
		return author;
	}

	public void setAuthor(TwitterUser author) {
		this.author = author;
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int compareTo(Tweet t) {
		//Retourne 0 si est égal, -1 si est plus petit, +1 si il est plus grand, que celui passé en paramètre
		long popularityOwn=favoriteCount+retweetCount;
		long populatityOther=t.getFavoriteCount()+t.getRetweetCount();
		if(popularityOwn==populatityOther) {return 0;}
		else if (popularityOwn<populatityOther) {return -1;}
		else {return 1;}
	}

	
}
