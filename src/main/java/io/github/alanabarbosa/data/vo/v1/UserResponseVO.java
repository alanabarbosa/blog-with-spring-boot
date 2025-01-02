package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import io.github.alanabarbosa.model.File;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "first_name", "last_name","user_name", "bio", "created_at", "enabled", "roles"})
public class UserResponseVO extends RepresentationModel<UserResponseVO> implements Serializable {

    private static final long serialVersionUID = 1L;

	@Mapping("id")
	@JsonProperty("id")
    private Long key;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;
    
    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("enabled")
    private Boolean enabled;

    private File file;
    
    private List<CommentResponseBasicVO2> comments;
    
    private List<PostResponseBasicVO> posts;

    public UserResponseVO() {}
    
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<CommentResponseBasicVO2> getComments() {
		return comments;
	}

	public void setComments(List<CommentResponseBasicVO2> comments) {
		this.comments = comments;
	}
	public List<PostResponseBasicVO> getPosts() {
		return posts;
	}

	public void setPosts(List<PostResponseBasicVO> posts) {
		this.posts = posts;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(bio, comments, createdAt, enabled, file, firstName, key, lastName, posts, userName);
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
		UserResponseVO other = (UserResponseVO) obj;
		return Objects.equals(bio, other.bio) && Objects.equals(comments, other.comments)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(enabled, other.enabled)
				&& Objects.equals(file, other.file) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(key, other.key) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(posts, other.posts) && Objects.equals(userName, other.userName);
	} 
}
