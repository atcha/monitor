package net.christophe.genin.domain.server.db.nitrite;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import net.christophe.genin.domain.server.db.Queries;
import net.christophe.genin.domain.server.db.Schemas;
import net.christophe.genin.domain.server.json.Jsons;
import rx.Single;

import java.util.List;
import java.util.Optional;

public class NitriteQuery implements Queries {


    @Override
    public Single<JsonArray> projects() {
        final JsonArray arr = Dbs.instance.getCollection(Schemas.Projects.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> {
                    final JsonObject obj = new JsonObject();
                    Optional.ofNullable(doc.get(Schemas.Projects.release.name()))
                            .ifPresent((s) -> obj.put(Schemas.Projects.release.name(), s));
                    Optional.ofNullable(doc.get(Schemas.Projects.snapshot.name()))
                            .ifPresent((s) -> obj.put(Schemas.Projects.snapshot.name(), s));
                    final Dbs.Attributes attributes = new Dbs.Attributes(doc);
                    return obj
                            .put(Schemas.Projects.id.name(), doc.get(Schemas.Projects.id.name()))
                            .put(Schemas.Projects.name.name(), doc.get(Schemas.Projects.name.name()))
                            .put(Schemas.Projects.latestUpdate.name(), doc.get(Schemas.Projects.latestUpdate.name()))
                            .put(Schemas.Projects.tables.name(), attributes.toJsonArray(Schemas.Projects.tables.name()))
                            .put(Schemas.Projects.apis.name(), attributes.toJsonArray(Schemas.Projects.apis.name()))
                            .put(Schemas.Projects.changelog.name(), doc.get(Schemas.Projects.changelog.name()))
                            .put(Schemas.Projects.javaDeps.name(), attributes.toJsonArray(Schemas.Projects.javaDeps.name()));
                }).collect(Jsons.Collectors.toJsonArray());
        return Single.just(arr);
    }

    @Override
    public Single<JsonArray> tables() {
        final JsonArray l = Dbs.instance.getCollection(Schemas.Tables.collection())
                .find().toList()
                .parallelStream()
                .map(doc -> {
                    JsonArray services = Optional.ofNullable(doc.get(Schemas.Tables.services.name(), List.class))
                            .map(JsonArray::new).orElse(new JsonArray());

                    return new JsonObject()
                            .put(Schemas.Tables.id.name(), doc.getId().getIdValue())
                            .put(Schemas.Tables.name.name(), doc.get(Schemas.Tables.name.name()))
                            .put(Schemas.Tables.latestUpdate.name(), doc.get(Schemas.Tables.latestUpdate.name()))
                            .put(Schemas.Tables.services.name(), services);
                })
                .collect(Jsons.Collectors.toJsonArray());
        return Single.just(l);
    }
}
