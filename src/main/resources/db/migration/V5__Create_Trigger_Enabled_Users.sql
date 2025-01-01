/*DELIMITER //
CREATE TRIGGER set_created_at_before_insert
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    IF NEW.enabled = 1 AND NEW.created_at IS NULL THEN
        SET NEW.created_at = NOW();
    END IF;
END;
//
DELIMITER ;