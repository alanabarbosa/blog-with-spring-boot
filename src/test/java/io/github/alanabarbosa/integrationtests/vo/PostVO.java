package io.github.alanabarbosa.integrationtests.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreUpdate;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "title", "content","created_at", "updated_at", "published_at", "slug", "status", "category_id", "image_desktop_id", "image_mobile_id", "comments"})
public class PostVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Long id;
    private String title;
    private String content;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;    
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;    
    
    @JsonProperty("published_at")
    @Column(name = "published_at", nullable = true)
    private LocalDateTime publishedAt;
    
    private String slug;
    
    @JsonProperty("status")
    private Boolean status;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true) 
    @JoinColumn(name = "image_desktop_id", nullable = true)
    private File imageDesktop;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, optional = true) 
    @JoinColumn(name = "image_mobile_id", nullable = true)
    private File imageMobile;
    
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY) 
    private List<Comment> comments;

    public PostVO() {}
    
  /*  @PrePersist
    public void prePresist() {
    	if (createdAt == null) createdAt = LocalDateTime.now();
    	updatedAt = createdAt;
    }*/
    
    @PreUpdate
    public void preUpdate() {
    	updatedAt = LocalDateTime.now();
    	if (status && publishedAt == null) publishedAt = LocalDateTime.now();
    }	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
		return Objects.hash(category, content, createdAt, id, imageDesktop, imageMobile, publishedAt, slug, status,
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
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id)
				&& Objects.equals(imageDesktop, other.imageDesktop) && Objects.equals(imageMobile, other.imageMobile)
				&& Objects.equals(publishedAt, other.publishedAt) && Objects.equals(slug, other.slug)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(user, other.user);
	}
}
