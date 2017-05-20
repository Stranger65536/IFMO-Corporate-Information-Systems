START TRANSACTION;

INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('USER');
INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('MODERATOR');
INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('ADMIN');

SELECT `id`
INTO @admin_role_id
FROM `roles`
WHERE `name` = 'admin';

INSERT INTO `reserv-io`.`users` (`username`, `email`, `password`, `role_id`)
VALUES ('admin', 'reserv-io-admin@emc.com', SHA2('admin', 512), @admin_role_id);

INSERT INTO `reserv-io`.`reservation_types` (`name`)
VALUES ('REGULAR');
INSERT INTO `reserv-io`.`reservation_types` (`name`)
VALUES ('UNAVAILABLE');

INSERT INTO `reserv-io`.`reservation_statuses` (`name`)
VALUES ('APPROVED');
INSERT INTO `reserv-io`.`reservation_statuses` (`name`)
VALUES ('CANCELED');
INSERT INTO `reserv-io`.`reservation_statuses` (`name`)
VALUES ('WAITING_FOR_APPROVAL');
INSERT INTO `reserv-io`.`reservation_statuses` (`name`)
VALUES ('NEW_TIME_PROPOSED');

COMMIT;