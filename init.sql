-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema ping-pong
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `ping-pong` ;

-- -----------------------------------------------------
-- Schema ping-pong
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `ping-pong` DEFAULT CHARACTER SET utf8 ;
USE `ping-pong` ;

-- -----------------------------------------------------
-- Table `ping-pong`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ping-pong`.`roles` ;

CREATE TABLE IF NOT EXISTS `ping-pong`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ping-pong`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ping-pong`.`users` ;

CREATE TABLE IF NOT EXISTS `ping-pong`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` NVARCHAR(127) NULL,
  `last_name` NVARCHAR(127) NULL,
  `middle_name` NVARCHAR(127) NULL,
  `login` NVARCHAR(20) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`id`, `role_id`),
  INDEX `fk_users_roles_idx` (`role_id` ASC),
  FULLTEXT INDEX `name_idx` (`first_name` ASC, `last_name` ASC, `middle_name` ASC),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  CONSTRAINT `fk_users_roles`
    FOREIGN KEY (`role_id`)
    REFERENCES `ping-pong`.`roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
KEY_BLOCK_SIZE = 1;


-- -----------------------------------------------------
-- Table `ping-pong`.`resource`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ping-pong`.`resource` ;

CREATE TABLE IF NOT EXISTS `ping-pong`.`resource` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` NVARCHAR(45) NOT NULL,
  `location` NVARCHAR(45) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ping-pong`.`reservations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ping-pong`.`reservations` ;

CREATE TABLE IF NOT EXISTS `ping-pong`.`reservations` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `users_id` INT NOT NULL,
  `reserved_at` DATETIME NOT NULL,
  `reservation_start` DATETIME NOT NULL,
  `reservation_end` DATETIME NOT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `resource_id` INT NOT NULL,
  PRIMARY KEY (`id`, `users_id`, `resource_id`),
  INDEX `fk_reservations_users1_idx` (`users_id` ASC),
  INDEX `reservation_time` (`reservation_start` ASC, `reservation_end` ASC),
  INDEX `fk_reservations_resource1_idx` (`resource_id` ASC),
  INDEX `deleted` (`deleted` ASC),
  CONSTRAINT `fk_reservations_users1`
    FOREIGN KEY (`users_id`)
    REFERENCES `ping-pong`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reservations_resource1`
    FOREIGN KEY (`resource_id`)
    REFERENCES `ping-pong`.`resource` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `ping-pong`.`login_attempts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `ping-pong`.`login_attempts` ;

CREATE TABLE IF NOT EXISTS `ping-pong`.`login_attempts` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `ip_address` VARCHAR(45) NOT NULL,
  `login_time` DATETIME NOT NULL,
  `successfull` TINYINT(1) NOT NULL,
  `users_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_login_attempts_users1_idx` (`users_id` ASC),
  INDEX `logins` (`successfull` ASC, `login_time` ASC),
  CONSTRAINT `fk_login_attempts_users1`
    FOREIGN KEY (`users_id`)
    REFERENCES `ping-pong`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE = '';
GRANT USAGE ON *.* TO dbuser;
 DROP USER dbuser;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'dbuser' IDENTIFIED BY 'awesomepassword';

GRANT SELECT, INSERT ON TABLE `ping-pong`.* TO 'dbuser';
GRANT SELECT, INSERT, UPDATE, DELETE, TRIGGER, CREATE ON TABLE `ping-pong`.* TO 'dbuser';
SET SQL_MODE = '';
GRANT USAGE ON *.* TO dbtest;
 DROP USER dbtest;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'dbtest' IDENTIFIED BY 'testpassword';

GRANT ALL ON `ping-pong`.* TO 'dbtest';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
