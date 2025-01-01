package io.github.alanabarbosa.integrationtests.vo.pagedmodels;

import java.util.List;

import io.github.alanabarbosa.integrationtests.vo.PostVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelPost {

	@XmlElement(name = "content")
	private List<PostVO> content;

	public PagedModelPost() {}

	public List<PostVO> getContent() {
		return content;
	}

	public void setContent(List<PostVO> content) {
		this.content = content;
	}
}