package net.christophe.genin.domain.server.command;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.es.ElasticSearchDs;
import net.christophe.genin.domain.server.db.es.InitializeCollections;
import net.christophe.genin.domain.server.db.nitrite.ConfigurationDto;
import net.christophe.genin.domain.server.db.nitrite.Dbs;
import net.christophe.genin.domain.server.db.Schemas;

public class ConfigurationCommand extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationCommand.class);

    public static final String SAVE = ConfigurationCommand.class.getName() + ".save";
    public static final String ELASTICSEARCH_RESET = ConfigurationCommand.class.getName() + ".elastic.search.reset";

    @Override
    public void start() {
        vertx.eventBus().consumer(SAVE, (Handler<Message<JsonObject>>) (msg) -> {
            JsonObject body = msg.body();
            ConfigurationDto dto = Schemas.Configuration.fromJson(body);
            if (Dbs.instance.repository(ConfigurationDto.class)
                    .update(dto, true).getAffectedCount() == 1) {
                ElasticSearchDs.set(dto);
                msg.reply(body);
            } else {
                msg.fail(500, "Error in saving configuration");
            }

        });
        vertx.eventBus().consumer(ELASTICSEARCH_RESET, (msg) -> {

            ElasticSearchDs ds = ElasticSearchDs.get();
            JsonObject reply = new JsonObject().put("active", ds.active());
            if (!ds.active()) {
                msg.reply(reply);
                return;
            }

            new InitializeCollections(ds).reset()
                    .collect(JsonArray::new, JsonArray::add)
                    .subscribe((arr) -> {
                        msg.reply(reply.put("created", arr));
                    }, err -> {
                        logger.error("Error in Elastic search rest", err);
                        msg.fail(500, err.getMessage());
                    });
        });
    }
}
