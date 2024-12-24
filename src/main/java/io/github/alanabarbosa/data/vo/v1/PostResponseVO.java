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
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "title", "content", "createdAt", "updatedAt", "publishedAt", "slug", "status", "user_id"})
public class PostResponseVO extends RepresentationModel<PostResponseVO> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Mapping("id")
	@JsonProperty("id")
    private Long key;
    private String title;
    private String content;
    
    @Column(name = "created_at", nullable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;    
    
    @Column(name = "updated_at", nullable = false)
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;    
    
    @Column(name = "published_at", nullable = true)
    @JsonProperty("published_at")
    private LocalDateTime publishedAt;
    private String slug;
    @JsonProperty("status")
    private Boolean status;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @Mapping("user")
    private UserResponseVO user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    @Mapping("category")
    private CategoryVO category;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_desktop_id", nullable = true)
    private File imageDesktop;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_mobile_id", nullable = true)
    private File imageMobile;
    
    /*@OneToMany(mappedBy = "post", fetch = FetchType.EAGER) 
    private List<CommentVO> comments;*/

    public PostResponseVO() {}
    
    @PrePersist
    public void prePresist() {
    	if (createdAt == null) createdAt = LocalDateTime.now();
    	updatedAt = createdAt;
    }
    
    @PreUpdate
    public void preUpdate() {
    	updatedAt = LocalDateTime.now();
    	if (status && publishedAt == null) publishedAt = LocalDateTime.now();
    }

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

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	

	public UserResponseVO getUser() {
		return user;
	}

	public void setUser(UserResponseVO user) {
		this.user = user;
	}

	public CategoryVO getCategory() {
		return category;
	}

	public void setCategory(CategoryVO category) {
		this.category = category;
	}

	public File getImageDesktop() {
		return imageDesktop;
	}

	public void setImageDesktop(File imageDesktop) {
		this.imageDesktop = imageDesktop;
	}

	public File getImageMobile() {
		return imageMobile;
	}

	public void setImageMobile(File imageMobile) {
		this.imageMobile = imageMobile;
	}

	/*public List<CommentVO> getComments() {
		return comments;
	}

	public void setComments(List<CommentVO> comments) {
		this.comments = comments;
	}*/

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(category, content, createdAt, imageDesktop, imageMobile, key,
				publishedAt, slug, status, title, updatedAt, user);
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
		PostResponseVO other = (PostResponseVO) obj;
		return Objects.equals(category, other.category)
				&& Objects.equals(content, other.content) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(imageDesktop, other.imageDesktop) && Objects.equals(imageMobile, other.imageMobile)
				&& Objects.equals(key, other.key) && Objects.equals(publishedAt, other.publishedAt)
				&& Objects.equals(slug, other.slug) && Objects.equals(status, other.status)
				&& Objects.equals(title, other.title) && Objects.equals(updatedAt, other.updatedAt)
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
