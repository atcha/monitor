package net.christophe.genin.domain.server.db;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.es.ElasticSearchDs;
import net.christophe.genin.domain.server.db.es.InitializeCollections;
import net.christophe.genin.domain.server.db.nitrite.ConfigurationDto;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.query.Configuration;
import org.dizitart.no2.Document;
import org.dizitart.no2.NitriteCollection;

import java.util.Date;
import java.util.Optional;

public class InitializeDb extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(InitializeDb.class);

    public static final String HEALTH = InitializeDb.class.getName() + ".health";
    public static final String ELASTIC_INITIZALIZE = InitializeDb.class.getName() + ".health";


    @Override
    public void start() throws Exception {
        JsonObject dbConfig = config().getJsonObject("db", new JsonObject());
        String dbPath = dbConfig.getString("path", "test.db");
        String user = dbConfig.getString("user", "user");
        String pwd = dbConfig.getString("pwd", "password");


        NitriteCollection testCollection = Dbs.instance.build(dbPath, user, pwd).getCollection("health");

        Document init = Optional.ofNullable(testCollection.find().firstOrDefault())
                .orElseGet(() -> Document.createDocument("db", true))
                .put("time", new Date().getTime());

        logger.info("Updated : " + testCollection.update(init, true).getAffectedCount());
        ConfigurationDto configurationDto = Configuration.get();
        ElasticSearchDs.set(configurationDto);
        new InitializeCollections(ElasticSearchDs.get()).createIfNotExists()
                .subscribe(b -> logger.info("Elastic Search manage -> " + b),
                        err -> logger.error("Error in indices management", err)
                );

        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = Dbs.toArray(
                    Dbs.instance.getCollection("health")
                            .find()
                            .toList()
            );
            msg.reply(health);

        });

        vertx.eventBus().consumer(HEALTH, msg -> {
            JsonArray health = Dbs.toArray(
                    Dbs.instance.getCollection("health")
                            .find()
                            .toList()
            );
            msg.reply(health);

        });

    }

}
