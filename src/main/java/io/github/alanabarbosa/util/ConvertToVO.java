package io.github.alanabarbosa.util;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import io.github.alanabarbosa.mapper.DozerMapper;

public class ConvertToVO {

    private static final Logger logger = Logger.getLogger(ConvertToVO.class.getName());

    public static <T, U> List<U> processEntities(Long id, List<T> entities, Class<U> voClass, String errorMessage) {
        List<U> voList = null;
        try {
            voList = DozerMapper.parseListObjects(entities, voClass);
        } catch (Exception e) {
            logger.log(Level.SEVERE, errorMessage + " " + id, e);
        }
        return voList;
    }
}
