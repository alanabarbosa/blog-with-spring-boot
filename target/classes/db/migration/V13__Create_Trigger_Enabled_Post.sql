/*DELIMITER //
CREATE TRIGGER set_published_at_before_insert
BEFORE INSERT ON post
FOR EACH ROW
BEGIN
    IF NEW.status = 1 AND NEW.published_at IS NULL THEN
        SET NEW.published_at = NOW();
    END IF;
END;
//
DELIMITER ;*/