package io.github.alanabarbosa.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.alanabarbosa.data.vo.v1.PostVO;
import io.github.alanabarbosa.mapper.DozerMapper;
import io.github.alanabarbosa.model.Post;
import io.github.alanabarbosa.unittests.mapper.mocks.MockPost;

public class DozerConverterTest {
    
    MockPost inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockPost();
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    @Test
    public void parseEntityToVOTest() {
        PostVO output = DozerMapper.parseObject(inputObject.mockEntity(), PostVO.class);
        assertEquals(Long.valueOf(0L), output.getKey());
        assertEquals("Meu Título0", output.getTitle()); 
        assertEquals("Este é o conteúdo do post.0", output.getContent()); 
        assertEquals("meu-titulo0", output.getSlug());

        String nowFormatted = formatLocalDateTime(LocalDateTime.now());
        assertEquals(nowFormatted, formatLocalDateTime(output.getCreatedAt())); 
        assertEquals(nowFormatted, formatLocalDateTime(output.getUpdatedAt())); 
        assertEquals(nowFormatted, formatLocalDateTime(output.getPublishedAt())); 

        assertEquals(true, output.getStatus()); 
        assertEquals(Long.valueOf(1L), output.getUser().getKey());
    }

    @Test
    public void parseEntityListToVOListTest() {
        List<PostVO> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), PostVO.class);

        String nowFormatted = formatLocalDateTime(LocalDateTime.now());

        PostVO outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getKey());
        assertEquals("Meu Título0", outputZero.getTitle());
        assertEquals("Este é o conteúdo do post.0", outputZero.getContent());
        assertEquals("meu-titulo0", outputZero.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getPublishedAt()));
        assertEquals(true, outputZero.getStatus());
        assertEquals(Long.valueOf(1L), outputZero.getUser().getKey());
      
        PostVO outputSeven = outputList.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getKey());
        assertEquals("Meu Título7", outputSeven.getTitle());
        assertEquals("Este é o conteúdo do post.7", outputSeven.getContent());
        assertEquals("meu-titulo7", outputSeven.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getPublishedAt()));
        assertEquals(false, outputSeven.getStatus());
        assertEquals(Long.valueOf(8L), outputSeven.getUser().getKey());

        PostVO outputTwelve = outputList.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getKey());
        assertEquals("Meu Título12", outputTwelve.getTitle());
        assertEquals("Este é o conteúdo do post.12", outputTwelve.getContent());
        assertEquals("meu-titulo12", outputTwelve.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getPublishedAt()));
        assertEquals(true, outputTwelve.getStatus());
        assertEquals(Long.valueOf(13L), outputTwelve.getUser().getKey());
    }


    @Test
    public void parseVOToEntityTest() {
        Post output = DozerMapper.parseObject(inputObject.mockVO(), Post.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Meu Título0", output.getTitle());
        assertEquals("Este é o conteúdo do post.0", output.getContent());
        assertEquals("meu-titulo0", output.getSlug());

        String nowFormatted = formatLocalDateTime(LocalDateTime.now());
        assertEquals(nowFormatted, formatLocalDateTime(output.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(output.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(output.getPublishedAt()));
        
        assertEquals(true, output.getStatus());
        assertEquals(Long.valueOf(1L), output.getUser().getId());
    }

    @Test
    public void parserVOListToEntityListTest() {
        List<Post> outputList = DozerMapper.parseListObjects(inputObject.mockVOList(), Post.class);
        Post outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Meu Título0", outputZero.getTitle());
        assertEquals("Este é o conteúdo do post.0", outputZero.getContent());
        assertEquals("meu-titulo0", outputZero.getSlug());
        
        LocalDateTime now = LocalDateTime.now();
        
        assertTrue(isWithinTolerance(outputZero.getCreatedAt(), now), "A diferença de tempo é maior do que o esperado para createdAt.");
        assertTrue(isWithinTolerance(outputZero.getUpdatedAt(), now), "A diferença de tempo é maior do que o esperado para updatedAt.");
        assertTrue(isWithinTolerance(outputZero.getPublishedAt(), now), "A diferença de tempo é maior do que o esperado para publishedAt.");
        
        assertEquals(true, outputZero.getStatus());
        assertEquals(Long.valueOf(1L), outputZero.getUser().getId());
        
        Post outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Meu Título7", outputSeven.getTitle());
        assertEquals("Este é o conteúdo do post.7", outputSeven.getContent());
        assertEquals("meu-titulo7", outputSeven.getSlug());

        assertTrue(isWithinTolerance(outputSeven.getCreatedAt(), now), "A diferença de tempo é maior do que o esperado para createdAt.");
        assertTrue(isWithinTolerance(outputSeven.getUpdatedAt(), now), "A diferença de tempo é maior do que o esperado para updatedAt.");
        assertTrue(isWithinTolerance(outputSeven.getPublishedAt(), now), "A diferença de tempo é maior do que o esperado para publishedAt.");
        
        assertEquals(false, outputSeven.getStatus());
        System.out.println("Id Seven:" + outputSeven.getUser());
        assertEquals(Long.valueOf(8L), outputSeven.getUser().getId());
        
        Post outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Meu Título12", outputTwelve.getTitle());
        assertEquals("Este é o conteúdo do post.12", outputTwelve.getContent());
        assertEquals("meu-titulo12", outputTwelve.getSlug());
        
        assertTrue(isWithinTolerance(outputTwelve.getCreatedAt(), now), "A diferença de tempo é maior do que o esperado para createdAt.");
        assertTrue(isWithinTolerance(outputTwelve.getUpdatedAt(), now), "A diferença de tempo é maior do que o esperado para updatedAt.");
        assertTrue(isWithinTolerance(outputTwelve.getPublishedAt(), now), "A diferença de tempo é maior do que o esperado para publishedAt.");
        
        assertEquals(true, outputTwelve.getStatus());
        assertEquals(Long.valueOf(13L), outputTwelve.getUser().getId());
    }
    
    private boolean isWithinTolerance(LocalDateTime timeToCheck, LocalDateTime now) {
        Duration difference = Duration.between(timeToCheck, now);
        return Math.abs(difference.getSeconds()) <= 2;
    }    
}
