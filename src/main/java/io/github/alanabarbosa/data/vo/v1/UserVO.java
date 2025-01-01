package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.dozermapper.core.Mapping;

import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.Role;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JacksonXmlRootElement(localName = "UserVO")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "first_name", "last_name","user_name", "password", "bio", "created_at", "account_non_expired", "account_non_locked", "credentials_non_expired",  "enabled", "roles"})
public class UserVO extends RepresentationModel<UserVO> implements Serializable {

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
    
    //@JsonProperty("password")
    @JsonProperty(value = "password")
	private String password;
	
    @JsonProperty("account_non_expired")
	private Boolean accountNonExpired;
   
    @JsonProperty("account_non_locked")
	private Boolean accountNonLocked;
	
    @JsonProperty("credentials_non_expired")
	private Boolean credentialsNonExpired;	

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("enabled")
    private Boolean enabled;
    
    @JsonProperty("comments")
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER) 
    private List<Comment> comments;

    @OneToOne
    @JoinColumn(name = "file_id")
    private File file;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    public UserVO() {}
    

/* public List<String> getRoleNames() {
        return roles.stream()
            .map(RoleVO::getName)
            .collect(Collectors.toList());
    }*/
    
    @PrePersist
    public void prePresist() {
    	if (createdAt == null) createdAt = LocalDateTime.now();
    }
    
    @PrePersist
    protected void onCreate() {
        if (accountNonExpired == null) {
            accountNonExpired = true;
        }
        if (accountNonLocked == null) {
            accountNonLocked = true;
        }
        if (credentialsNonExpired == null) {
            credentialsNonExpired = true;
        }
        if (enabled == null) {
            enabled = true; 
        }
    }     


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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public Boolean getAccountNonExpired() {
		return accountNonExpired = true;
	}


	public void setAccountNonExpired(Boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}


	public Boolean getAccountNonLocked() {
		return accountNonLocked = true;
	}


	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}


	public Boolean getCredentialsNonExpired() {
		return credentialsNonExpired = true;
	}


	public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
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


	public List<Comment> getComments() {
		return comments;
	}


	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}


	public File getFile() {
		return file;
	}


	public void setFile(File file) {
		this.file = file;
	}


	public List<Role> getRoles() {
		return roles;
	}


	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(accountNonExpired, accountNonLocked, bio, comments, createdAt,
				credentialsNonExpired, enabled, file, firstName, key, lastName, password, roles, userName);
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
		UserVO other = (UserVO) obj;
		return Objects.equals(accountNonExpired, other.accountNonExpired)
				&& Objects.equals(accountNonLocked, other.accountNonLocked) && Objects.equals(bio, other.bio)
				&& Objects.equals(comments, other.comments) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(credentialsNonExpired, other.credentialsNonExpired)
				&& Objects.equals(enabled, other.enabled) && Objects.equals(file, other.file)
				&& Objects.equals(firstName, other.firstName) && Objects.equals(key, other.key)
				&& Objects.equals(lastName, other.lastName) && Objects.equals(password, other.password)
				&& Objects.equals(roles, other.roles) && Objects.equals(userName, other.userName);
	} 
}
