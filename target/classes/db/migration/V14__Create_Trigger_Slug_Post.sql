/*DELIMITER //

CREATE TRIGGER generate_slug_before_insert
BEFORE INSERT ON post
FOR EACH ROW
BEGIN
    IF NEW.status = 1 THEN
        SET NEW.slug = LOWER(REPLACE(NEW.title, ' ', '-'));
        SET NEW.slug = REGEXP_REPLACE(NEW.slug, '[^a-z0-9-]', '');
        SET NEW.slug = TRIM(NEW.slug);
    ELSE
        SET NEW.slug = NULL;
    END IF;
END;

//
DELIMITER ;