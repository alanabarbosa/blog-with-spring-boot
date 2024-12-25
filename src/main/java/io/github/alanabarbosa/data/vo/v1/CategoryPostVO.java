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
@JsonPropertyOrder({"id", "name", "description","created_at"})
public class CategoryPostVO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Mapping("id")
	@JsonProperty("id")
	
    private Long key;
    private String name;	
    private String description;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public CategoryPostVO() {}

	public Long getKey() {
		return key;
	}

	public void setKey(Long id) {
		this.key = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CategoryPostVO categoryVO = (CategoryPostVO) obj;
        return Objects.equals(key, categoryVO.key) &&
               Objects.equals(name, categoryVO.name) &&
               Objects.equals(description, categoryVO.description) &&
               Objects.equals(createdAt, categoryVO.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, description, createdAt);
    }
}
