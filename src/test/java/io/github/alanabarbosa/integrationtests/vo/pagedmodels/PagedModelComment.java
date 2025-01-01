package io.github.alanabarbosa.integrationtests.vo.pagedmodels;

import java.util.List;

import io.github.alanabarbosa.integrationtests.vo.CommentVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelComment{

	@XmlElement(name = "content")
	private List<CommentVO> content;

	public PagedModelComment() {}

	public List<CommentVO> getContent() {
		return content;
	}

	public void setContent(List<CommentVO> content) {
		this.content = content;
	}
}