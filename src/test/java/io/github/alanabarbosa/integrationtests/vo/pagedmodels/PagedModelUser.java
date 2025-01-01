package io.github.alanabarbosa.integrationtests.vo.pagedmodels;

import java.util.List;

import io.github.alanabarbosa.integrationtests.vo.UserVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelUser {

	@XmlElement(name = "content")
	private List<UserVO> content;

	public PagedModelUser() {}

	public List<UserVO> getContent() {
		return content;
	}

	public void setContent(List<UserVO> content) {
		this.content = content;
	}
}