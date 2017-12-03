package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.command.*;
import net.christophe.genin.domain.server.db.InitializeDb;
import net.christophe.genin.domain.server.query.Configuration;
import net.christophe.genin.domain.server.query.Endpoints;
import net.christophe.genin.domain.server.query.Projects;
import net.christophe.genin.domain.server.query.Tables;

/**
 * Main Verticle
 */
public class Server extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    @Override
    public void start() throws Exception {
        logger.info("start ....");
        vertx.deployVerticle(new Http(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(new InitializeDb(), new DeploymentOptions().setConfig(config()).setWorker(true), as ->{
            if(as.failed()){
                throw new IllegalStateException("Error in creating DB", as.cause());
            }
            deployCommand();
            deployQuery();
        });
        logger.info("start : OK");
    }

    private void deployCommand() {
        vertx.deployVerticle(new Raw());
        vertx.deployVerticle(new ProjectBatch());
        vertx.deployVerticle(new TablesBatch());
        vertx.deployVerticle(new VersionBatch());
        vertx.deployVerticle(new Import());
        vertx.deployVerticle(new ConfigurationCommand());
        vertx.deployVerticle(new Reset());
        vertx.deployVerticle(new ApisBatch());
    }

    private void deployQuery() {
        vertx.deployVerticle(new Projects());
        vertx.deployVerticle(new Tables());
        vertx.deployVerticle(new Configuration());
        vertx.deployVerticle(new Endpoints());
    }
}
