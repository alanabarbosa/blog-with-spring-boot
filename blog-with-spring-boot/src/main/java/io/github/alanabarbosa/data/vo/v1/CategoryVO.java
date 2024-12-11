package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.dozermapper.core.Mapping;

public class CategoryVO extends RepresentationModel<CategoryVO> implements Serializable {

	private static final long serialVersionUID = 1L;
	@Mapping("id")
	@JsonProperty("id")
    private Long key;
    private String name;	
    private String description;
    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    public CategoryVO() {}

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
        CategoryVO categoryVO = (CategoryVO) obj;
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
