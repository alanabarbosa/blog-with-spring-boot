package io.github.alanabarbosa.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.alanabarbosa.integrationtests.vo.UserVO;

public class UserEmbeddedVO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("userResponseBasicVOList")
	private List<UserVO> Users;

	public UserEmbeddedVO() {
		super();
	}

	public List<UserVO> getUsers() {
		return Users;
	}

	public void setUsers(List<UserVO> Users) {
		this.Users = Users;
	}

	@Override
	public int hashCode() {
		return Objects.hash(Users);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserEmbeddedVO other = (UserEmbeddedVO) obj;
		return Objects.equals(Users, other.Users);
	}
}
