package io.github.alanabarbosa.integrationtests.vo.pagedmodels;

import java.util.List;

import io.github.alanabarbosa.integrationtests.vo.CategoryVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelCategory {

	@XmlElement(name = "content")
	private List<CategoryVO> content;

	public PagedModelCategory() {}

	public List<CategoryVO> getContent() {
		return content;
	}

	public void setContent(List<CategoryVO> content) {
		this.content = content;
	}
}