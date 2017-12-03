package net.christophe.genin.domain.server.db;

import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.nitrite.ConfigurationDto;
import net.christophe.genin.domain.server.json.Jsons;

import java.util.Objects;

/**
 * description of the datas.
 */
public final class Schemas {
    public static final String RAW_COLLECTION = "Apps-Store";
    public static final String RAW_STATE = "state";

    /**
     * Configuration Dto builder / parser.
     */
    public static class Configuration {

        public static JsonObject toJson(ConfigurationDto configurationDto) {
            JsonObject elasticSearch = (Objects.isNull(configurationDto.getEsHost())) ? new JsonObject()
                    : new JsonObject()
                    .put("host", configurationDto.getEsHost())
                    .put("port", configurationDto.getEsPort());

            return new JsonObject()
                    .put("id", configurationDto.getConfId())
                    .put("javaFilters", configurationDto.getJavaFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()))
                    .put("npmFilters", configurationDto.getNpmFilters()
                            .parallelStream().collect(Jsons.Collectors.toJsonArray()))
                    .put("activateElasticSearch", Boolean.TRUE.equals(configurationDto.getActivateElasticSearch()))
                    .put("elasticSearch", elasticSearch);
        }

        @SuppressWarnings("unchecked")
        public static ConfigurationDto fromJson(JsonObject obj) {
            ConfigurationDto configurationDto = new ConfigurationDto();
            configurationDto.setConfId(obj.getLong("id", 0L));
            configurationDto.setJavaFilters(Jsons.builder(obj.getJsonArray("javaFilters")).toListString());
            configurationDto.setNpmFilters(Jsons.builder(obj.getJsonArray("npmFilters")).toListString());
            configurationDto.setActivateElasticSearch(obj.getBoolean("activateElasticSearch", false));
            JsonObject elasticSearch = obj.getJsonObject("elasticSearch", new JsonObject());
            configurationDto.setEsHost(elasticSearch.getString("host"));
            configurationDto.setEsPort(elasticSearch.getInteger("port"));
            return configurationDto;
        }
    }

    public enum Raw {
        groupId, artifactId, version, dependencies, update, apis;

        public enum Tables {
            table, className;

            public static String collection() {
                return Tables.class.getSimpleName().toLowerCase();
            }
        }

        public enum Dependencies {
            groupId, artifactId, version;

            public static String collection() {
                return Dependencies.class.getSimpleName().toLowerCase();
            }
        }

        public enum Apis {
            artifactId, groupId, version, services;

            public enum Services {
                name, methods;

                public enum methods {
                    name, method, returns, path, params, comment;
                }
            }
        }

    }

    public enum Projects {
        id, name, release, snapshot, tables, javaDeps, npmDeps, latestUpdate, changelog, apis;

        public static String collection() {
            return Projects.class.getSimpleName().toLowerCase();
        }
    }

    public enum Apis {
        id, artifactId, groupId, name, method, returns, path, params, comment, since, className, latestUpdate;

        public static String collection() {
            return Apis.class.getSimpleName().toLowerCase();
        }
    }

    public enum Tables {
        id, name, latestUpdate, services;

        public static String collection() {
            return Tables.class.getSimpleName().toLowerCase();
        }
    }

    public enum Version {
        id, name, isSnapshot, tables, javaDeps, npmDeps, latestUpdate, apis, changelog;

        public static final String PREFIX = "version/";


        public static String collection(String id) {
            return PREFIX + id;
        }
    }
}
