START TRANSACTION;

INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('user');
INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('moderator');
INSERT INTO `reserv-io`.`roles` (`name`)
VALUES ('admin');

SELECT `id`
INTO @admin_role_id
FROM `roles`
WHERE `name` = 'admin';

INSERT INTO `reserv-io`.`users` (`login`, `password`, `role_id`)
VALUES ('admin', SHA2('admin', 512), @admin_role_id);

INSERT INTO `reserv-io`.`reservation_types` (`name`)
VALUES ('Regular');
INSERT INTO `reserv-io`.`reservation_types` (`name`)
VALUES ('Unavailable');

INSERT INTO `reserv-io`.`action_statuses` (`name`)
VALUES ('Approved');
INSERT INTO `reserv-io`.`action_statuses` (`name`)
VALUES ('Canceled');
INSERT INTO `reserv-io`.`action_statuses` (`name`)
VALUES ('Waiting for approval');
INSERT INTO `reserv-io`.`action_statuses` (`name`)
VALUES ('New time proposed');

COMMIT;