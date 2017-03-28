CREATE TABLE `reserv-io`.`roles` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`users` (
  `id`          INT          NOT NULL AUTO_INCREMENT,
  `first_name`  NVARCHAR(35) NULL,
  `last_name`   NVARCHAR(35) NULL,
  `middle_name` NVARCHAR(35) NULL,
  `login`       NVARCHAR(25) NOT NULL,
  `password`    VARCHAR(128) NOT NULL,
  `role_id`     INT          NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ident_idx` (`first_name` ASC, `last_name` ASC, `middle_name` ASC, `login` ASC),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  INDEX `fk_user_role_idx` (`role_id` ASC),
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
  `id`      BIGINT     NOT NULL AUTO_INCREMENT,
  `user_id` INT        NOT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `main_search_idx` (`id` ASC, `user_id` ASC, `deleted` ASC),
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
  UNIQUE INDEX `name_idx` (`name` ASC, `location` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`reservation_types` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
)
  ENGINE = InnoDB;


CREATE TABLE `reserv-io`.`action_statuses` (
  `id`   INT         NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(25) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
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
  INDEX `main_search_idx` (`reservation_start` ASC, `reservation_end` ASC, `resource_id` ASC, `type_id` ASC, `status_id` ASC, `user_id` ASC, `reservation_id` ASC),
  INDEX `fk_action_user_idx` (`user_id` ASC),
  INDEX `fk_action_resource_idx` (`resource_id` ASC),
  INDEX `fk_action_status_idx` (`status_id` ASC),
  INDEX `fk_action_type_idx` (`type_id` ASC),
  INDEX `fk_action_reservation_idx` (`reservation_id` ASC),
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
  REFERENCES `reserv-io`.`action_statuses` (`id`)
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
