package io.github.alanabarbosa.unittests.mapper.mocks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.alanabarbosa.data.vo.v1.CommentVO;
import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.data.vo.v1.UserVO;
import io.github.alanabarbosa.model.Comment;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;

public class MockComment {


    public Comment mockEntity() {
        return mockEntity(0);
    }
    
    public CommentVO mockVO() {
        return mockVO(0);
    }
    
    public List<Comment> mockEntityList() {
        List<Comment> comments = new ArrayList<Comment>();
        for (int i = 0; i < 14; i++) {
            comments.add(mockEntity(i));
        }
        return comments;
    }

    public List<CommentVO> mockVOList() {
        List<CommentVO> comments = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
        	comments.add(mockVO(i));
        }
        return comments;
    }
    
    public Comment mockEntity(Integer number) {
        Comment comment = new Comment();
        comment.setId(number.longValue());
        comment.setContent("Este é um comentario."+ number);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setStatus(((number % 2)==0) ? true : false);
        
        Post post = new Post();
        post.setId(Long.valueOf(number + 1));         
        comment.setPost(post);
        
        User user = new User();
        user.setId(Long.valueOf(number + 1));
        user.setEnabled((((number % 2)==0) ? true : false));
        comment.setUser(user);
        return comment;
    }

    public CommentVO mockVO(Integer number) {
        CommentVO comment = new CommentVO();
        comment.setKey(number.longValue());
        comment.setContent("Este é um comentario."+ number);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setStatus(((number % 2)==0) ? true : false);
        
       /* PostVO post = new PostVO();
        post.setKey(Long.valueOf(number + 1));         
       comment.setPost(post);
        
        UserVO user = new UserVO();
        user.setId(Long.valueOf(number + 1));
        user.setEnabled((((number % 2)==0) ? true : false));
        comment.setUser(user);  */      
        return comment;
    }

}