package io.github.alanabarbosa.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false, length = 80)
    private String title;
	
	@Lob
	@Column(nullable = false)
    private String content;    
	
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private String slug;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;    
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;    
    
    @Column(name = "published_At", nullable = false)
    private LocalDateTime publishedAt;
    
    @Column(nullable = false)
    private Boolean status;    
    
    @Column(name = "categories_id", nullable = false)
    private Long categoriesId;    
    
    @Column(name = "image_id", nullable = false)
    private Long imageId;
	
    public Post() {
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

	public Long getAuthorId() {
		return userId;
	}

	public void setAuthorId(Long authorId) {
		this.userId = authorId;
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Long getCategoriesId() {
		return categoriesId;
	}

	public void setCategoriesId(Long categoriesId) {
		this.categoriesId = categoriesId;
	}

	public Long getImageId() {
		return imageId;
	}

	public void setImageId(Long imageId) {
		this.imageId = imageId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, categoriesId, content, createdAt, id, imageId, publishedAt, slug, status, title,
				updatedAt);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Post other = (Post) obj;
		return Objects.equals(userId, other.userId) && Objects.equals(categoriesId, other.categoriesId)
				&& Objects.equals(content, other.content) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(id, other.id) && Objects.equals(imageId, other.imageId)
				&& Objects.equals(publishedAt, other.publishedAt) && Objects.equals(slug, other.slug)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(updatedAt, other.updatedAt);
	}
}
