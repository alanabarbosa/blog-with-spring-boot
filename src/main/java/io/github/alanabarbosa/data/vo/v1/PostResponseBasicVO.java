package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "title"})
public class PostResponseBasicVO extends RepresentationModel<PostResponseBasicVO> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Mapping("id")
	@JsonProperty("id")
    private Long key;
    private String title;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @Mapping("user")
    @JsonIgnore
    private UserResponseBasicVO user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @Mapping("category")
    @JsonIgnore
    private CategoryResponseBasicVO category;
    
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER) 
    @JsonIgnore
    private List<CommentBasicVO> comments;

    public PostResponseBasicVO() {}
    
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	

	public UserResponseBasicVO getUser() {
		return user;
	}

	public void setUser(UserResponseBasicVO user) {
		this.user = user;
	}

	public CategoryResponseBasicVO getCategory() {
		return category;
	}

	public void setCategory(CategoryResponseBasicVO category) {
		this.category = category;
	}
	

	public List<CommentBasicVO> getComments() {
		return comments;
	}

	public void setComments(List<CommentBasicVO> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(category, comments, key, title, user);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostResponseBasicVO other = (PostResponseBasicVO) obj;
		return Objects.equals(category, other.category) && Objects.equals(comments, other.comments)
				&& Objects.equals(key, other.key) && Objects.equals(title, other.title)
				&& Objects.equals(user, other.user);
	}

	
/*
	@Override
	public String toString() {
		return "PostVO [key=" + key + ", title=" + title + ", content=" + content + ", createdAt=" + createdAt
				+ ", updatedAt=" + updatedAt + ", publishedAt=" + publishedAt + ", slug=" + slug + ", status=" + status
				+ ", user=" + user + ", category=" + category + ", imageDesktop=" + imageDesktop + ", imageMobile="
				+ imageMobile + ", comments=" + comments + "]";
	}*/
	
	
}
