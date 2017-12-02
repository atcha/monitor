package net.christophe.genin.domain.server;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.StaticHandler;
import net.christophe.genin.domain.server.http.Index;
import net.christophe.genin.domain.server.http.Services;

public class Http extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(Http.class);

    @Override
    public void start() throws Exception {
        final int port = config().getInteger("server-port", 8280);

        final HttpServer httpServer = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));

        final Router router = Router.router(vertx);
        router.route().handler(FaviconHandler.create(600));
        router.route().handler(BodyHandler.create());
        router.mountSubRouter("/api", new Services(vertx).build());

        new Index().register(router);

        router.route("/*")
                .handler(StaticHandler.create("dist")
                                .setFilesReadOnly(true)
                );

        logger.info("port : " + port);
        httpServer.requestHandler(router::accept)
                .listen(port);
        logger.info("Http server launched !");
    }


}
