CREATE TABLE IF NOT EXISTS EVENTS (
  ID            BIGINT AUTO_INCREMENT PRIMARY KEY,
  state         INTEGER,
  document      LONGTEXT,
  ARCHIVE       BOOLEAN,
  LATEST_UPDATE BIGINT
);
CREATE TABLE IF NOT EXISTS TABLES (
  ID           VARCHAR(255) NOT NULL PRIMARY KEY,
  NAME         TEXT         NOT NULL,
  SERVICE      TEXT         NOT NULL,
  latestUpdate BIGINT
);
CREATE TABLE IF NOT EXISTS TABLES (
  ID       VARCHAR(255) NOT NULL PRIMARY KEY,
  NAME     VARCHAR(255),
  document LONGTEXT
);
CREATE TABLE IF NOT EXISTS APIS (
  ID        VARCHAR(255) NOT NULL PRIMARY KEY,
  METHOD    VARCHAR(10),
  FULLURL   TEXT,
  IDPROJECT VARCHAR(255),
  document  LONGTEXT
);

CREATE TABLE IF NOT EXISTS PROJECTS (
  ID       VARCHAR(767) PRIMARY KEY,
  NAME     VARCHAR(200),
  document LONGTEXT
);
CREATE TABLE IF NOT EXISTS VERSIONS (
  ID        VARCHAR(255) PRIMARY KEY,
  IDPROJECT VARCHAR(255),
  NAME      VARCHAR(200),
  document  LONGTEXT
);
CREATE TABLE IF NOT EXISTS DEPENDENCIES (
  RESOURCE VARCHAR(200),
  USED_BY  VARCHAR(200),
  document LONGTEXT,
  PRIMARY KEY (RESOURCE, USED_BY)
);
CREATE TABLE IF NOT EXISTS front_apps (
  ID       VARCHAR(255) PRIMARY KEY,
  NAME     VARCHAR(255),
  LATEST   LONG,
  VERSION  VARCHAR(255),
  document LONGTEXT
);
CREATE TABLE IF NOT EXISTS QUEUE
(
  ID       bigint auto_increment primary key,
  state    int      null,
  document longtext null
);
DELETE FROM EVENTS;
DELETE FROM PROJECTS;
DELETE FROM TABLES;
DELETE FROM APIS;
DELETE FROM VERSIONS;
DELETE FROM DEPENDENCIES;
DELETE FROM front_apps;
DELETE FROM QUEUE;