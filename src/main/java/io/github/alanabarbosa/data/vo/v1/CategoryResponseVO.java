package io.github.alanabarbosa.data.vo.v1;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import io.github.alanabarbosa.model.Post;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "name", "description","created_at"})
public class CategoryResponseVO extends RepresentationModel<CategoryResponseVO>  implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Mapping("id")
	@JsonProperty("id")
	
    private Long key;
    private String name;
    
    @OneToMany(mappedBy = "category", fetch = FetchType.EAGER)
    private List<PostBasicVO> posts;

    public CategoryResponseVO() {}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public List<PostBasicVO> getPosts() {
        return posts;
    }

    public void setPosts(List<PostBasicVO> posts) {
        this.posts = posts;
    }
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(key, name);
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
		CategoryResponseVO other = (CategoryResponseVO) obj;
		return Objects.equals(key, other.key) && Objects.equals(name, other.name);
	}
}
