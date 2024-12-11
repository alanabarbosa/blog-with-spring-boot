package io.github.alanabarbosa.unittests.mapper.mocks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.File;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;

public class MockPost {


    public Post mockEntity() {
        return mockEntity(0);
    }
    
    public PostVO mockVO() {
        return mockVO(0);
    }
    
    public List<Post> mockEntityList() {
        List<Post> Posts = new ArrayList<Post>();
        for (int i = 0; i < 14; i++) {
            Posts.add(mockEntity(i));
        }
        return Posts;
    }

    public List<PostVO> mockVOList() {
        List<PostVO> Posts = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
            Posts.add(mockVO(i));
        }
        return Posts;
    }
    
    public Post mockEntity(Integer number) {
        Post post = new Post();
        post.setId(number.longValue());
        post.setTitle("Meu Título" + number);
        post.setContent("Este é o conteúdo do post."+ number);
        post.setSlug("meu-titulo"+ number);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        post.setStatus(((number % 2)==0) ? true : false);
        post.setCategory(new Category());
        post.setImageDesktop(new File()); 
        post.setImageMobile(new File());
        User user = new User();
        user.setId(Long.valueOf(number + 1)); 
        user.setEnabled((((number % 2)==0) ? true : false));
        post.setUser(user);;
        return post;
    }

    public PostVO mockVO(Integer number) {
        PostVO post = new PostVO();
        post.setKey(number.longValue());
        post.setTitle("Meu Título" + number);
        post.setContent("Este é o conteúdo do post."+ number);
        post.setSlug("meu-titulo"+ number);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        post.setPublishedAt(LocalDateTime.now());
        post.setStatus(((number % 2)==0) ? true : false);
        post.setCategory(new CategoryVO());
        post.setImageDesktop(new File());
        post.setImageMobile(new File());
        UserVO user = new UserVO();
        user.setId(Long.valueOf(number + 1));
        user.setEnabled((((number % 2)==0) ? true : false));
        post.setUser(user);
        return post;
    }

}