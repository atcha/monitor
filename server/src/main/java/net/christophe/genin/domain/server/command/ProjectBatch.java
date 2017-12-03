package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.nitrite.ConfigurationDto;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.dizitart.no2.filters.Filters.eq;

public class ProjectBatch extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBatch.class);

    @Override
    public void start() throws Exception {
        new Treatments.Periodic(this, logger).run(this::periodic);
    }

    private synchronized boolean periodic() {

        final NitriteCollection collection = Dbs.instance
                .getCollection(Schemas.RAW_COLLECTION);
        collection
                .find(eq(Schemas.RAW_STATE, Treatments.PROJECTS.getState()))
                .toList()
                .stream()
                .findFirst().ifPresent(doc -> {
            final JsonObject json = Dbs.Raws.toJson(doc);
            final String artifactId = json.getString(Schemas.Raw.artifactId.name());

            final NitriteCollection projectCollection = Dbs.instance.getCollection(Schemas.Projects.collection());
            final Document document = Optional.ofNullable(projectCollection
                    .find(eq(Schemas.Projects.name.name(), artifactId))
                    .firstOrDefault()
            ).orElseGet(
                    () -> Document.createDocument(Schemas.Projects.latestUpdate.name(), 0L)
                            .put(Schemas.Projects.id.name(), Dbs.newId())
            );
            updateFromJson(document, json).ifPresent((dc) -> {
                logger.info("New data for " + artifactId + ". Document must be updated.");
                projectCollection.update(dc, true);
            });
            // Dans tous les cas marqués le doc comme traiter.
            collection.update(doc.put(Schemas.RAW_STATE, Treatments.TABLES.getState()));
        });

        return true;
    }

    private Optional<Document> updateFromJson(Document document, JsonObject json) {
        final long update = json.getLong(Schemas.Raw.update.name());
        final Long lUpdate = Long.valueOf(document.get(Schemas.Projects.latestUpdate.name()).toString());
        final String artifactId = json.getString(Schemas.Raw.artifactId.name());
        if (lUpdate < update) {

            document.put(Schemas.Projects.name.name(), artifactId);
            final String version = json.getString(Schemas.Raw.version.name());
            if (isSnapshot(version)) {
                document.put(Schemas.Projects.snapshot.name(), version);
            } else {
                document.put(Schemas.Projects.release.name(), version);
            }


            final List<String> tables = extractTables(json);
            document.put(Schemas.Projects.tables.name(), tables);
            final List<String> allDeps = extractJavaDeps(json);
            final ConfigurationDto conf = Optional.ofNullable(Dbs.instance
                    .repository(ConfigurationDto.class)
                    .find().firstOrDefault())
                    .orElseGet(ConfigurationDto::new);

            List<String> javaFilters = conf.getJavaFilters();
            final List<String> javaDeps = allDeps.parallelStream()
                    .map(String::toUpperCase)
                    .filter(s ->
                            javaFilters.isEmpty() ||
                                    javaFilters.parallelStream()
                                            .map(String::toUpperCase)
                                            .anyMatch(s::contains)
                    ).collect(Collectors.toList());
            document.put(Schemas.Projects.javaDeps.name(), javaDeps);
            Optional.ofNullable(json.getString(Schemas.Projects.changelog.name()))
                    .ifPresent(s -> document.put(Schemas.Projects.changelog.name(), s));
            final List<String> apis = extractUrls(json);
            document.put(Schemas.Projects.apis.name(), apis);
            document.put(Schemas.Projects.latestUpdate.name(), update);
            return Optional.of(document);
        }
        logger.info("No data for " + artifactId + ". Document must not be updated: " + lUpdate + " > " + update);
        return Optional.empty();
    }

    static boolean isSnapshot(String version) {
        return version.contains("SNAPSHOT");
    }

    static List<String> extractJavaDeps(JsonObject json) {


        return Jsons.builder(json.getJsonArray(Schemas.Raw.Dependencies.collection())).toStream()
                .map(js -> js.getString(Schemas.Raw.Dependencies.artifactId.name(), ""))
                .distinct()
                .collect(Collectors.toList());
    }

    static List<String> extractUrls(JsonObject json) {
        JsonObject apis = json.getJsonObject(Schemas.Raw.apis.name(), new JsonObject());
        JsonArray services = apis.getJsonArray("services", new JsonArray());


        return Jsons.builder(services).toStream()
                .map(js -> js.getJsonArray("methods", new JsonArray()))
                .flatMap(arr -> Jsons.builder(arr).toStream())
                .map(js -> {
                    final String method = js.getString("method", "");
                    final String path = js.getString("path", "");
                    return method + " - " + path;
                })
                .distinct()
                .collect(Collectors.toList());
    }

    static List<String> extractTables(JsonObject json) {
        return Jsons.builder(json.getJsonArray(Schemas.Raw.Tables.collection())).toStream()
                .map(js -> js.getString(Schemas.Raw.Tables.table.name(), ""))
                .distinct()
                .collect(Collectors.toList());
    }

}
