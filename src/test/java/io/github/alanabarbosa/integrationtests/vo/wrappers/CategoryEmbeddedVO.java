package io.github.alanabarbosa.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.alanabarbosa.integrationtests.vo.CategoryVO;

public class CategoryEmbeddedVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("categoryResponseBasicVOList")
	private List<CategoryVO> categories;

	public CategoryEmbeddedVO() {
		super();
	}

	public List<CategoryVO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryVO> categories) {
		this.categories = categories;
	}

	@Override
	public int hashCode() {
		return Objects.hash(categories);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CategoryEmbeddedVO other = (CategoryEmbeddedVO) obj;
		return Objects.equals(categories, other.categories);
	}
}
