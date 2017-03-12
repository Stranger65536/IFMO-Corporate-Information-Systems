-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema reserv-io
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `reserv-io` ;

-- -----------------------------------------------------
-- Schema reserv-io
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `reserv-io` ;
USE `reserv-io` ;

-- -----------------------------------------------------
-- Table `reserv-io`.`roles`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reserv-io`.`roles` ;

CREATE TABLE IF NOT EXISTS `reserv-io`.`roles` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reserv-io`.`users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reserv-io`.`users` ;

CREATE TABLE IF NOT EXISTS `reserv-io`.`users` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` NVARCHAR(35) NULL,
  `last_name` NVARCHAR(35) NULL,
  `middle_name` NVARCHAR(35) NULL,
  `login` NVARCHAR(35) NOT NULL,
  `password` VARCHAR(128) NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `ident_idx` (`first_name` ASC, `last_name` ASC, `middle_name` ASC, `login` ASC),
  UNIQUE INDEX `login_UNIQUE` (`login` ASC),
  INDEX `fk_users_roles_idx` (`role_id` ASC),
  CONSTRAINT `fk_users_roles`
    FOREIGN KEY (`role_id`)
    REFERENCES `reserv-io`.`roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reserv-io`.`login_attempts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reserv-io`.`login_attempts` ;

CREATE TABLE IF NOT EXISTS `reserv-io`.`login_attempts` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `ip_address` VARCHAR(45) NOT NULL,
  `login_time` DATETIME NOT NULL,
  `successful` TINYINT(1) NOT NULL,
  `user_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `logins` (`successful` ASC, `login_time` ASC, `user_id` ASC),
  INDEX `fk_login_attempts_users1_idx` (`user_id` ASC),
  CONSTRAINT `fk_login_attempts_users1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reserv-io`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reserv-io`.`resources`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reserv-io`.`resources` ;

CREATE TABLE IF NOT EXISTS `reserv-io`.`resources` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` NVARCHAR(45) NOT NULL,
  `location` NVARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `name` (`name` ASC, `location` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `reserv-io`.`reservations`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `reserv-io`.`reservations` ;

CREATE TABLE IF NOT EXISTS `reserv-io`.`reservations` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `reserved_at` DATETIME NOT NULL,
  `reservation_start` DATETIME NOT NULL,
  `reservation_end` DATETIME NOT NULL,
  `last_modified` DATETIME NOT NULL,
  `deleted` TINYINT(1) NOT NULL DEFAULT 0,
  `resource_id` INT NOT NULL,
  INDEX `reservation_time` (`reservation_start` ASC, `reservation_end` ASC, `deleted` ASC, `user_id` ASC, `resource_id` ASC),
  PRIMARY KEY (`id`),
  INDEX `fk_reservations_user1_idx` (`user_id` ASC),
  INDEX `fk_reservations_resource1_idx` (`resource_id` ASC),
  CONSTRAINT `fk_reservations_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `reserv-io`.`users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_reservations_resource1`
    FOREIGN KEY (`resource_id`)
    REFERENCES `reserv-io`.`resources` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE = '';
GRANT USAGE ON *.* TO dbuser;
 DROP USER dbuser;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'dbuser' IDENTIFIED BY 'awesomepassword';

GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP ON TABLE `reserv-io`.* TO 'dbuser';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
