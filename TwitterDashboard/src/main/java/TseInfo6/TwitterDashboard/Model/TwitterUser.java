package TseInfo6.TwitterDashboard.Model;

import java.util.Date;
import java.util.Objects;

public class TwitterUser {
	private String screenName;
	private String name;
	private String location;
	private String description;
	private Long followersCount;
	private Long friendsCount;
	private boolean verified;
	private Date createdAt;
	private String imageUrl;
	private Long statusesCount;
	private String bannerUrl=null;
	public TwitterUser(String screenName, String name, String location, String description, Long followersCount,
			Long friendsCount, boolean verified, Date createdAt, String imageUrl,String bannerUrl, Long statusesCount) {
		super();
		this.screenName = screenName;
		this.name = name;
		this.location = location;
		this.description = description;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.verified = verified;
		this.createdAt = createdAt;
		this.imageUrl = imageUrl;
		this.bannerUrl=bannerUrl;
		this.statusesCount = statusesCount;
	}
	
	public TwitterUser(String screenName, String name, String location, String description, Long followersCount,
			Long friendsCount, boolean verified, Date createdAt, String imageUrl, Long statusesCount) {
		super();
		this.screenName = screenName;
		this.name = name;
		this.location = location;
		this.description = description;
		this.followersCount = followersCount;
		this.friendsCount = friendsCount;
		this.verified = verified;
		this.createdAt = createdAt;
		this.imageUrl = imageUrl;
		this.statusesCount = statusesCount;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdAt, description, followersCount, friendsCount, imageUrl, location, name, screenName,
				statusesCount, verified);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TwitterUser)) {
			return false;
		}
		TwitterUser other = (TwitterUser) obj;
		return Objects.equals(createdAt, other.createdAt) && Objects.equals(description, other.description)
				&& Objects.equals(followersCount, other.followersCount)
				&& Objects.equals(friendsCount, other.friendsCount) && Objects.equals(imageUrl, other.imageUrl)
				&& Objects.equals(location, other.location) && Objects.equals(name, other.name)
				&& Objects.equals(screenName, other.screenName) && Objects.equals(statusesCount, other.statusesCount)
				&& verified == other.verified;
	}
	@Override
	public String toString() {
		return "TwitterUser [screenName=" + screenName + ", name=" + name + ", location=" + location + ", description="
				+ description + ", followersCount=" + followersCount + ", friendsCount=" + friendsCount + ", verified="
				+ verified + ", createdAt=" + createdAt + ", imageUrl=" + imageUrl + ", statusesCount=" + statusesCount
				+ "]";
	}
	public String getScreenName() {
		return screenName;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(Long followersCount) {
		this.followersCount = followersCount;
	}
	public Long getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(Long friendsCount) {
		this.friendsCount = friendsCount;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public Long getStatusesCount() {
		return statusesCount;
	}
	public void setStatusesCount(Long statusesCount) {
		this.statusesCount = statusesCount;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
}
