package io.github.alanabarbosa.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.alanabarbosa.integrationtests.vo.PostVO;

public class PostEmbeddedVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("postResponseBasicVOList")
	private List<PostVO> posts;

	public PostEmbeddedVO() {
		super();
	}

	public List<PostVO> getPosts() {
		return posts;
	}

	public void setPosts(List<PostVO> posts) {
		this.posts = posts;
	}

	@Override
	public int hashCode() {
		return Objects.hash(posts);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostEmbeddedVO other = (PostEmbeddedVO) obj;
		return Objects.equals(posts, other.posts);
	}
}
