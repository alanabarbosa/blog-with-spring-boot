package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@JsonPropertyOrder({"id", "title", "content", "createdAt", "updatedAt", "publishedAt", "slug", "status", "user_id"})
public class PostVO extends RepresentationModel<PostVO> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Mapping("id")
	@JsonProperty("id")
    private Long key;
    private String title;
    private String content;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("published_at")
    private LocalDateTime publishedAt;
    private String slug;
    private Boolean status;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne
    @JoinColumn(name = "image_desktop_id")
    private File imageDesktop;

    @ManyToOne
    @JoinColumn(name = "image_mobile_id")
    private File imageMobile;
    
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY) 
    private List<Comment> comments;

    public PostVO() {}
    
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

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
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

	@Override
	public int hashCode() {
		return Objects.hash(category, content, createdAt, key, imageDesktop, imageMobile, publishedAt, slug, status,
				title, updatedAt, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostVO other = (PostVO) obj;
		return Objects.equals(category, other.category) && Objects.equals(content, other.content)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(key, other.key)
				&& Objects.equals(imageDesktop, other.imageDesktop) && Objects.equals(imageMobile, other.imageMobile)
				&& Objects.equals(publishedAt, other.publishedAt) && Objects.equals(slug, other.slug)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(user, other.user);
	}
}
