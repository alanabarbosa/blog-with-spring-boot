package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "content", "created_at","status"})
public class CommentResponseVO extends RepresentationModel<CommentResponseVO> implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@Mapping("id")
	@JsonProperty("id")
    private Long key;
    private String content;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    private Boolean status; 
    
    @Mapping("post")
    private PostResponseBasicVO post;
    
    @Mapping("user")
    private UserResponseBasicVO user;

    public CommentResponseVO() {}

    public CommentResponseVO(Long key, String content, LocalDateTime createdAt) {
        this.key = key;
        this.content = content;
        this.createdAt = createdAt;
    }
    
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public PostResponseBasicVO getPost() {
		return post;
	}

	public void setPost(PostResponseBasicVO post) {
		this.post = post;
	}

	public UserResponseBasicVO getUser() {
		return user;
	}

	public void setUser(UserResponseBasicVO user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(content, createdAt, key, status, user);
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
		CommentResponseVO other = (CommentResponseVO) obj;
		return Objects.equals(content, other.content) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(key, other.key)
				&& Objects.equals(status, other.status) && Objects.equals(user, other.user);
	}
	
}