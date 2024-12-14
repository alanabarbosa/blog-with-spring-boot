package io.github.alanabarbosa.unittests.mapper.mocks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Role;
import io.github.alanabarbosa.model.User;

public class MockUser {


    public User mockEntity() {
        return mockEntity(0);
    }
    
    public UserVO mockVO() {
        return mockVO(0);
    }
    
    public List<User> mockEntityList() {
        List<User> users = new ArrayList<User>();
        for (int i = 0; i < 14; i++) {
            users.add(mockEntity(i));
        }
        return users;
    }

    public List<UserVO> mockVOList() {
        List<UserVO> users = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
        	users.add(mockVO(i));
        }
        return users;
    }
    
    public User mockEntity(Integer number) {
        User user = new User();
        user.setId(number.longValue());
        user.setFirstName("This is a first name."+ number);
        user.setLastName("This is a last name."+ number);
        user.setUserName("This is a username."+ number);
        user.setPassword("This is a password."+ number);
        user.setBio("This is a bio." + number);
        user.setAccountNonExpired(((number % 2)==0) ? true : false);
        user.setAccountNonLocked(((number % 2)==0) ? true : false);
        user.setCredentialsNonExpired(((number % 2)==0) ? true : false);
        user.setEnabled(((number % 2)==0) ? true : false);
        user.setCreatedAt(LocalDateTime.now());
        
        Comment comment = new Comment();
        comment.setContent("This is a comment content.");
        comment.setId(Long.valueOf(number + 1));
        user.setComments(List.of(comment));
        
       /* File file = new File();
        file.setId(Long.valueOf(number + 1));
        user.setFile(file);*/

        Role role = new Role();
        role.setName("ROLE_USER_" + number);
        user.setRoles(List.of(role));
        
		return user;
    }

    public UserVO mockVO(Integer number) {
        UserVO userVO = new UserVO();
        userVO.setKey(number.longValue());
        userVO.setFirstName("This is a first name." + number);
        userVO.setLastName("This is a last name." + number);
        userVO.setUserName("This is a username." + number);
        userVO.setPassword("This is a password." + number);
        userVO.setBio("This is a bio." + number);
        userVO.setAccountNonExpired(((number % 2) == 0));
        userVO.setAccountNonLocked(((number % 2) == 0));
        userVO.setCredentialsNonExpired(((number % 2) == 0));
        userVO.setEnabled(((number % 2) == 0));
        userVO.setCreatedAt(LocalDateTime.now());

        Comment comment = new Comment();
        comment.setContent("This is a comment content.");
        comment.setId(Long.valueOf(number + 1));
        userVO.setComments(List.of(comment));

        Role role = new Role();
        role.setName("ROLE_USER_" + number);
        userVO.setRoles(List.of(role));

        return userVO;
    }

}