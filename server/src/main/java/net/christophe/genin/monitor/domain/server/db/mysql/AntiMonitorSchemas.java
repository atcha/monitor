package net.christophe.genin.monitor.domain.server.db.mysql;

import rx.Observable;
import rx.Single;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;


public class AntiMonitorSchemas {

    private static final String[] CREATE_SCRIPTS = new String[]{
            "CREATE TABLE IF NOT EXISTS EVENTS (\n" +
                    "  ID  BIGINT AUTO_INCREMENT PRIMARY KEY,\n" +
                    "  state    INTEGER,\n" +
                    "  document LONGTEXT,\n" +
                    "  ARCHIVE BOOLEAN\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES (\n" +
                    "  ID      VARCHAR(100) NOT NULL PRIMARY KEY,\n" +
                    "  NAME    TEXT NOT NULL,\n" +
                    "  SERVICE TEXT NOT NULL,\n" +
                    "  latestUpdate BIGINT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS TABLES (\n" +
                    "  ID       VARCHAR(767) PRIMARY KEY,\n" +
                    "  NAME     VARCHAR(100),\n" +
                    "  document LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS APIS (\n" +
                    "  ID        VARCHAR(767) PRIMARY KEY,\n" +
                    "  METHOD   VARCHAR(10),\n" +
                    "  FULLURL   TEXT,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  document LONGTEXT\n" +
                    ")",

            "CREATE TABLE IF NOT EXISTS PROJECTS (\n" +
                    "  ID        VARCHAR(767) PRIMARY KEY,\n" +
                    "  NAME      VARCHAR(200),\n" +
                    "  document  LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS VERSIONS (\n" +
                    "  ID        VARCHAR(1000) PRIMARY KEY,\n" +
                    "  IDPROJECT VARCHAR(1000),\n" +
                    "  NAME      VARCHAR(200),\n" +
                    "  document  LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS DEPENDENCIES (\n" +
                    "  RESOURCE VARCHAR(200),\n" +
                    "  USED_BY  VARCHAR(200),\n" +
                    "  document LONGTEXT,\n" +
                    "  PRIMARY KEY (RESOURCE, USED_BY)\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS front_apps (\n" +
                    "    ID VARCHAR(1000) PRIMARY KEY,\n" +
                    "    NAME VARCHAR(1000),\n" +
                    "    LATEST LONG,\n" +
                    "    VERSION VARCHAR(1000),\n" +
                    "    document LONGTEXT\n" +
                    ")",
            "CREATE TABLE IF NOT EXISTS QUEUE\n" +
                    "(\n" +
                    "  ID       bigint auto_increment\n" +
                    "    primary key,\n" +
                    "  state    int      null,\n" +
                    "  document longtext null\n" +
                    ")",
            "DELETE FROM EVENTS",
            "DELETE FROM PROJECTS",
            "DELETE FROM TABLES",
            "DELETE FROM APIS",
            "DELETE FROM VERSIONS",
            "DELETE FROM DEPENDENCIES",
            "DELETE FROM front_apps",
            "DELETE FROM QUEUE",
    };


    /**
     * Methods for creating an schema.
     *
     * @return The results.
     */
    public static Single<String> create() {
        Mysqls mysqls = Mysqls.Instance.get();
        return Single.just("/sql/CREATE_SCHEMA.sql")
                .map(p -> {
                    try {
                        return AntiMonitorSchemas.class.getResource(p).toURI();
                    } catch (URISyntaxException e) {
                        throw new IllegalStateException("err in creating path", e);
                    }
                })
                .map(Paths::get)
                .map(path -> {
                    try {
                        return Files.readAllLines(path);
                    } catch (IOException e) {
                        throw new IllegalStateException("err in reading file", e);
                    }
                })
                .map(lines -> lines.stream().collect(Collectors.joining(" ")))
                .map(content -> Arrays.stream(content.split(";"))
                        .map(String::trim)
                        .toArray(String[]::new)
                )
                .flatMap(mysqls::batch)
                .map(list -> "Creation of EVENTS, PROJECTS, TABLES, API, VERSIONS if not exist :" + list);

    }


}
