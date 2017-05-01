CREATE TABLE `reserv-io`.`roles` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_idx` (`name` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`users` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `first_name`  NVARCHAR(35) NULL,
  `last_name`   NVARCHAR(35) NULL,
  `middle_name` NVARCHAR(35) NULL,
  `username`    NVARCHAR(25) NOT NULL,
  `email`       VARCHAR(45)  NOT NULL,
  `password`    VARCHAR(128) NOT NULL,
  `role_id`     INT          NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_user_role_idx` (`role_id` ASC),
  INDEX `first_name_idx` (`first_name` ASC),
  INDEX `last_name_idx` (`last_name` ASC),
  INDEX `middle_name_idx` (`middle_name` ASC),
  UNIQUE INDEX `username_idx` (`username` ASC),
  UNIQUE INDEX `email_idx` (`email` ASC),
  CONSTRAINT `fk_user_role`
  FOREIGN KEY (`role_id`)
  REFERENCES `reserv-io`.`roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`login_attempts` (
  `id`         BIGINT      NOT NULL AUTO_INCREMENT,
  `ip_address` VARCHAR(45) NOT NULL,
  `login_time` DATETIME    NOT NULL,
  `successful` TINYINT(1)  NOT NULL,
  `user_id`    INT         NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `logins` (`successful` ASC, `login_time` ASC, `user_id` ASC),
  INDEX `fk_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `reserv-io`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`reservations` (
  `id`      BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` INT    NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_reservation_user_idx` (`user_id` ASC),
  CONSTRAINT `fk_reservation_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `reserv-io`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`resources` (
  `id`       INT          NOT NULL AUTO_INCREMENT,
  `name`     NVARCHAR(25) NOT NULL,
  `location` NVARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `name_idx` (`name` ASC),
  INDEX `location_idx` (`location` ASC),
  UNIQUE INDEX `unique_idx` (`name` ASC, `location` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`reservation_types` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`reservation_statuses` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_idx` (`name` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`actions` (
  `id`                BIGINT   NOT NULL AUTO_INCREMENT,
  `time`              DATETIME NOT NULL,
  `reservation_start` DATETIME NOT NULL,
  `reservation_end`   DATETIME NOT NULL,
  `user_id`           INT      NOT NULL,
  `resource_id`       INT      NOT NULL,
  `type_id`           INT      NOT NULL,
  `status_id`         INT      NOT NULL,
  `reservation_id`    BIGINT   NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_actions_reservation_type_idx` (`type_id` ASC),
  INDEX `fk_actions_status_idx` (`status_id` ASC),
  INDEX `time_idx` (`time` ASC),
  INDEX `fk_actions_reservation_idx` (`reservation_id` ASC),
  INDEX `fk_user_idx` (`user_id` ASC),
  INDEX `fk_resource_idx` (`resource_id` ASC),
  INDEX `start_idx` (`reservation_start` ASC),
  INDEX `end_idx` (`reservation_end` ASC),
  CONSTRAINT `fk_action_reservation`
  FOREIGN KEY (`reservation_id`)
  REFERENCES `reserv-io`.`reservations` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_action_type`
  FOREIGN KEY (`type_id`)
  REFERENCES `reserv-io`.`reservation_types` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_action_status`
  FOREIGN KEY (`status_id`)
  REFERENCES `reserv-io`.`reservation_statuses` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_action_resource`
  FOREIGN KEY (`resource_id`)
  REFERENCES `reserv-io`.`resources` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_action_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `reserv-io`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
)
  ENGINE = InnoDB;

CREATE TABLE `reserv-io`.`dual` (
  `id` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB;

CREATE VIEW `reserv-io`.`actual_reservations` AS
  SELECT
    `r`.`id`,
    `r`.`user_id` `owner_id`,
    `a`.`user_id` `last_action_user_id`,
    `a`.`resource_id`,
    `a`.`type_id`,
    `a`.`status_id`,
    `a`.`reservation_start`,
    `a`.`reservation_end`,
    `b`.`created_at`,
    `b`.`updated_at`
  FROM `reserv-io`.`actions` `a`
    JOIN (SELECT
            `r`.`id`        `id`,
            MAX(`a`.`id`)   `action_id`,
            MIN(`a`.`time`) `created_at`,
            MAX(`a`.`time`) `updated_at`
          FROM `reserv-io`.`reservations` `r`
            JOIN `reserv-io`.`actions` `a` ON `r`.`id` = `a`.`reservation_id`
          GROUP BY `r`.`id`) `b`
      ON `a`.`id` = `b`.`action_id`
    JOIN `reserv-io`.`reservations` `r` ON `r`.`id` = `a`.`reservation_id`;

CREATE VIEW `reserv-io`.`actual_reserved_resources` AS
  SELECT
    `r`.`id`,
    `a`.`resource_id`,
    `a`.`type_id`,
    `a`.`status_id`,
    `a`.`reservation_start`,
    `a`.`reservation_end`
  FROM `reserv-io`.`actions` `a`
    JOIN (SELECT
            `r`.`id`      `id`,
            MAX(`a`.`id`) `action_id`
          FROM `reserv-io`.`reservations` `r`
            JOIN `reserv-io`.`actions` `a` ON `r`.`id` = `a`.`reservation_id`
          WHERE (`r`.`id` NOT IN
                 (SELECT `r`.`id` `id`
                  FROM `reserv-io`.`reservations` `r`
                    JOIN `reserv-io`.`actions` `a` ON `r`.`id` = `a`.`reservation_id`
                  WHERE (`a`.`status_id` IN
                         (SELECT `id`
                          FROM `reserv-io`.`reservation_statuses`
                          WHERE `name` = 'Canceled')))
                 AND `a`.`status_id` IN
                     (SELECT `id`
                      FROM `reserv-io`.`reservation_statuses`
                      WHERE `name` = 'Approved' OR `name` = 'Accepted'))
          GROUP BY `r`.`id`) `b`
      ON `a`.`id` = `b`.`action_id`
    JOIN `reserv-io`.`reservations` `r` ON `r`.`id` = `a`.`reservation_id`;

DELIMITER $$
CREATE PROCEDURE HAS_OVERLAPPING_RESERVATION(
  IN  RESOURCE  INT,
  IN  STARTS_AT DATETIME,
  IN  ENDS_AT   DATETIME,
  OUT RESULT    BIT)
  BEGIN
    SELECT CASE WHEN EXISTS(
        SELECT *
        FROM actual_reserved_resources r
        WHERE r.resource_id = RESOURCE
              AND r.reservation_start <= STARTS_AT
              AND r.reservation_end >= ENDS_AT)
      THEN TRUE
           ELSE FALSE END
    INTO RESULT;
  END$$

CREATE PROCEDURE HAS_OVERLAPPING_RESERVATION_WITH_TYPE(
  IN  RESOURCE         INT,
  IN  RESERVATION_TYPE INT,
  IN  STARTS_AT        DATETIME,
  IN  ENDS_AT          DATETIME,
  OUT RESULT           BIT)
  BEGIN
    SELECT CASE WHEN EXISTS(
        SELECT *
        FROM actual_reserved_resources r
        WHERE r.resource_id = RESOURCE
              AND r.type_id = RESERVATION_TYPE
              AND r.reservation_start <= STARTS_AT
              AND r.reservation_end >= ENDS_AT)
      THEN TRUE
           ELSE FALSE END
    INTO RESULT;
  END$$