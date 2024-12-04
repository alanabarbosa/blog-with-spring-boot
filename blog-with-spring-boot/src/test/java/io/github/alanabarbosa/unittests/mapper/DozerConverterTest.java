package io.github.alanabarbosa.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    // Define a formatter para comparar datas sem o efeito da hora exata.
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private String formatLocalDateTime(LocalDateTime dateTime) {
        return dateTime.format(formatter);
    }

    @Test
    public void parseEntityToVOTest() {
        PostVO output = DozerMapper.parseObject(inputObject.mockEntity(), PostVO.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Meu Título0", output.getTitle()); 
        assertEquals("Este é o conteúdo do post.0", output.getContent()); 
        assertEquals("meu-titulo0", output.getSlug());

        String nowFormatted = formatLocalDateTime(LocalDateTime.now());
        assertEquals(nowFormatted, formatLocalDateTime(output.getCreatedAt())); 
        assertEquals(nowFormatted, formatLocalDateTime(output.getUpdatedAt())); 
        assertEquals(nowFormatted, formatLocalDateTime(output.getPublishedAt())); 

        assertEquals(true, output.getStatus()); 
        assertEquals(Long.valueOf(1L), output.getUserId());
    }

    @Test
    public void parseEntityListToVOListTest() {
        List<PostVO> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), PostVO.class);
        PostVO outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Meu Título0", outputZero.getTitle());
        assertEquals("Este é o conteúdo do post.0", outputZero.getContent());
        assertEquals("meu-titulo0", outputZero.getSlug());

        String nowFormatted = formatLocalDateTime(LocalDateTime.now());
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getPublishedAt()));
        
        assertEquals(true, outputZero.getStatus());
        assertEquals(Long.valueOf(1L), outputZero.getUserId());

        PostVO outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Meu Título7", outputSeven.getTitle());
        assertEquals("Este é o conteúdo do post.7", outputSeven.getContent());
        assertEquals("meu-titulo7", outputSeven.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getPublishedAt()));
        
        assertEquals(false, outputSeven.getStatus());
        assertEquals(Long.valueOf(7L), outputSeven.getUserId());
        
        PostVO outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Meu Título12", outputTwelve.getTitle());
        assertEquals("Este é o conteúdo do post.12", outputTwelve.getContent());
        assertEquals("meu-titulo12", outputTwelve.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getPublishedAt()));
        
        assertEquals(true, outputTwelve.getStatus());
        assertEquals(Long.valueOf(12L), outputTwelve.getUserId());
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
        assertEquals(Long.valueOf(1L), output.getUserId());
    }

    @Test
    public void parserVOListToEntityListTest() {
        List<Post> outputList = DozerMapper.parseListObjects(inputObject.mockVOList(), Post.class);
        Post outputZero = outputList.get(0);
        
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Meu Título0", outputZero.getTitle());
        assertEquals("Este é o conteúdo do post.0", outputZero.getContent());
        assertEquals("meu-titulo0", outputZero.getSlug());
        
        String nowFormatted = formatLocalDateTime(LocalDateTime.now());
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputZero.getPublishedAt()));

        assertEquals(true, outputZero.getStatus());
        assertEquals(Long.valueOf(1L), outputZero.getUserId());
        
        Post outputSeven = outputList.get(7);
        
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Meu Título7", outputSeven.getTitle());
        assertEquals("Este é o conteúdo do post.7", outputSeven.getContent());
        assertEquals("meu-titulo7", outputSeven.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputSeven.getPublishedAt()));
        
        assertEquals(false, outputSeven.getStatus());
        assertEquals(Long.valueOf(7L), outputSeven.getUserId());
        
        Post outputTwelve = outputList.get(12);
        
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Meu Título12", outputTwelve.getTitle());
        assertEquals("Este é o conteúdo do post.12", outputTwelve.getContent());
        assertEquals("meu-titulo12", outputTwelve.getSlug());
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getCreatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getUpdatedAt()));
        assertEquals(nowFormatted, formatLocalDateTime(outputTwelve.getPublishedAt()));
        
        assertEquals(true, outputTwelve.getStatus());
        assertEquals(Long.valueOf(12L), outputTwelve.getUserId());
    }
}
