package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.File;
import jakarta.persistence.*;

public class PostVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private Long id;	
    private String title;	
    private String content;    
    private LocalDateTime createdAt;    
    private LocalDateTime updatedAt;    
    private LocalDateTime publishedAt;    
    private String slug;    
    private Boolean status;	
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private Set<File> files = new HashSet<>();

    public PostVO() {}


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


	public Long getUserId() {
		return userId;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}


	public Category getCategory() {
		return category;
	}


	public void setCategory(Category category) {
		this.category = category;
	}


	public Set<File> getFiles() {
		return files;
	}


	public void setFiles(Set<File> files) {
		this.files = files;
	}


	@Override
	public int hashCode() {
		return Objects.hash(category, content, createdAt, id, publishedAt, slug, status, title, updatedAt, userId);
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
				&& Objects.equals(publishedAt, other.publishedAt) && Objects.equals(slug, other.slug)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(userId, other.userId);
	}

    
}
