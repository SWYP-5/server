-- 데이터베이스 생성
CREATE DATABASE IF NOT EXISTS app_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE app_db;

-- 테이블을 추가할 때마다 여기에 CREATE TABLE 구문을 작성하세요
-- 예시:
-- CREATE TABLE IF NOT EXISTS users (
--     id          BIGINT       AUTO_INCREMENT PRIMARY KEY,
--     email       VARCHAR(255) NOT NULL UNIQUE,
--     created_at  DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6)
-- );