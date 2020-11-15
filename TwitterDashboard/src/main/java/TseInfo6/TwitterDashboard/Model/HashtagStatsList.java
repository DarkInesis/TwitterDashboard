package TseInfo6.TwitterDashboard.Model;

import java.util.List;
import java.util.Objects;

public class HashtagStatsList {
	private List<HashtagStats> hashtagsList;
	private String category;
	
	/**
	 * Returns if teh list is empty, i.e. for each element, element.isEmpty() is true
	 * @param the list to check
	 * @return true of all elements are empty
	 */
	public static boolean isEmpty(List<HashtagStats> stats) {
		return stats.stream()
				.map( elt -> elt.isEmpty() )
				.reduce(true, (a, b) -> a && b);
	}
	
	/**
	 * Returns if this object is empty
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		return isEmpty(this.hashtagsList);
	}
	
	public HashtagStatsList(List<HashtagStats> hashtagsList, String category) {
		super();
		this.hashtagsList = hashtagsList;
		this.category = category;
	}

	@Override
	public int hashCode() {
		return Objects.hash(category, hashtagsList);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof HashtagStatsList)) {
			return false;
		}
		HashtagStatsList other = (HashtagStatsList) obj;
		return Objects.equals(category, other.category) && Objects.equals(hashtagsList, other.hashtagsList);
	}

	@Override
	public String toString() {
		return "HashtagStatsList [hashtagsList=" + hashtagsList + ", category=" + category + "]";
	}

	public List<HashtagStats> getHashtagsList() {
		return hashtagsList;
	}

	public void setHashtagsList(List<HashtagStats> hashtagsList) {
		this.hashtagsList = hashtagsList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	
}
