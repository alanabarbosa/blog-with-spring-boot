package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import io.github.alanabarbosa.model.File;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "first_name", "last_name","user_name", "bio", "created_at", "enabled", "roles"})
public class UserBasicVO extends RepresentationModel<UserBasicVO> implements Serializable {

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

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;    

    public UserBasicVO() {}
    
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

	/*public List<CommentBasicVO> getComments() {
		return comments;
	}

	public void setComments(List<CommentBasicVO> comments) {
		this.comments = comments;
	}

	public List<PostBasicVO> getPosts() {
		return posts;
	}

	public void setPosts(List<PostBasicVO> posts) {
		this.posts = posts;
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(bio, createdAt, enabled, file, firstName, key, lastName, userName);
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
		UserBasicVO other = (UserBasicVO) obj;
		return Objects.equals(bio, other.bio) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(enabled, other.enabled) && Objects.equals(file, other.file)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(key, other.key)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(userName, other.userName);
	} 
}

