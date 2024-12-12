package io.github.alanabarbosa.unittests.mapper.mocks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import io.github.alanabarbosa.data.vo.v1.CategoryVO;
import io.github.alanabarbosa.model.Category;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.model.User;

public class MockCategory {


    public Category mockEntity() {
        return mockEntity(0);
    }
    
    public CategoryVO mockVO() {
        return mockVO(0);
    }
    
    public List<Category> mockEntityList() {
        List<Category> categories = new ArrayList<Category>();
        for (int i = 0; i < 14; i++) {
            categories.add(mockEntity(i));
        }
        return categories;
    }

    public List<CategoryVO> mockVOList() {
        List<CategoryVO> comments = new ArrayList<>();
        for (int i = 0; i < 14; i++) {
        	comments.add(mockVO(i));
        }
        return comments;
    }
    
    public Category mockEntity(Integer number) {
        Category categories = new Category();
        categories.setId(number.longValue());
        categories.setName("Este é um name."+ number);
        categories.setDescription("Este é uma description."+ number);
        categories.setCreatedAt(LocalDateTime.now());
        
        return categories;
    }

    public CategoryVO mockVO(Integer number) {
        CategoryVO categories = new CategoryVO();
        categories.setKey(number.longValue());
        categories.setName("Este é um name."+ number);
        categories.setDescription("Este é uma description."+ number);
        categories.setCreatedAt(LocalDateTime.now());       
        return categories;
    }

}