package io.github.alanabarbosa.util;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import io.github.alanabarbosa.controllers.CommentController;
import io.github.alanabarbosa.controllers.PostController;
import io.github.alanabarbosa.data.vo.v1.CommentBasicVO;
import io.github.alanabarbosa.data.vo.v1.PostBasicVO;

public class HateoasUtils {

    public static void addLink(CommentBasicVO entity, Long id, String rel) {
        try {
            entity.add(linkTo(methodOn(CommentController.class).findById(id)).withRel(rel));
        } catch (Exception e) {
            Logger.getLogger(HateoasUtils.class.getName())
                  .severe("Error adding HATEOAS link for " + rel + ": " + e.getMessage());
        }
    }

    public static void addLink(PostBasicVO entity, Long id, String rel) {
        try {
            entity.add(linkTo(methodOn(PostController.class).findById(id)).withRel(rel));
        } catch (Exception e) {
            Logger.getLogger(HateoasUtils.class.getName())
                  .severe("Error adding HATEOAS link for " + rel + ": " + e.getMessage());
        }
    }

    public static void addCommentLinks(List<CommentBasicVO> comments) {
        comments.forEach(comment -> 
            addLink(comment, comment.getKey(), "comments-details"));
    }

    public static void addPostLinks(List<PostBasicVO> posts) {
        posts.forEach(post -> 
            addLink(post, post.getKey(), "posts-details"));
    }
}