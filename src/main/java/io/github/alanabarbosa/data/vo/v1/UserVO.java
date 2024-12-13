package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
@JsonPropertyOrder({"id", "first_name", "last_name","user_name", "bio", "created_at", "enabled", "roles"})
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
    
	@Column(nullable = false, name = "password")
	@JsonProperty(value = "password", access = JsonProperty.Access.WRITE_ONLY)
	private String password;    

    @JsonProperty("bio")
    private String bio;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("enabled")
    private Boolean enabled; 
   
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_role",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )    
    @JsonProperty("roles")
    private List<RoleVO> roles; 

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

	public List<RoleVO> getRoles() {
		return roles;
	}

	public void setRoles(List<RoleVO> roles) {
		this.roles = roles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ Objects.hash(bio, createdAt, enabled, firstName, key, lastName, password, roles, userName);
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
		return Objects.equals(bio, other.bio) && Objects.equals(createdAt, other.createdAt)
				&& Objects.equals(enabled, other.enabled) && Objects.equals(firstName, other.firstName)
				&& Objects.equals(key, other.key) && Objects.equals(lastName, other.lastName)
				&& Objects.equals(password, other.password) && Objects.equals(roles, other.roles)
				&& Objects.equals(userName, other.userName);
	}
}
