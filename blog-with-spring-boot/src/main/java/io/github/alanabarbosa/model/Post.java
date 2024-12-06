package io.github.alanabarbosa.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "post")
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false, length = 255)
    private String title;
	
	@Lob
	@Column(nullable = false, length = 1000)
    private String content;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;    
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;    
    
    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    
    @Column(nullable = false)
    private String slug;
    
    @Column(nullable = false)
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

    public Post() {}
    
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
		Post other = (Post) obj;
		return Objects.equals(category, other.category) && Objects.equals(content, other.content)
				&& Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id)
				&& Objects.equals(imageDesktop, other.imageDesktop) && Objects.equals(imageMobile, other.imageMobile)
				&& Objects.equals(publishedAt, other.publishedAt) && Objects.equals(slug, other.slug)
				&& Objects.equals(status, other.status) && Objects.equals(title, other.title)
				&& Objects.equals(updatedAt, other.updatedAt) && Objects.equals(user, other.user);
	}
}
