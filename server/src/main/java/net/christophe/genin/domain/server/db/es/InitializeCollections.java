package net.christophe.genin.domain.server.db.es;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.Schemas;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequestBuilder;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.IndicesAdminClient;
import rx.Observable;
import rx.Single;
import rx.functions.Func1;

public class InitializeCollections {

    private static final Logger logger = LoggerFactory.getLogger(InitializeCollections.class);
    private static final String[] LIST = new String[]{
            Schemas.Projects.collection(), Schemas.Tables.collection()
    };

    private ElasticSearchDs ds;

    public InitializeCollections(ElasticSearchDs ds) {
        this.ds = ds;
    }

    private Observable<String> createIfNotExists(IndicesAdminClient indicesAdminClient, String collectionName) {
        return exist(indicesAdminClient, collectionName)
                .map(create(indicesAdminClient, collectionName));
    }

    private Func1<Boolean, String> create(IndicesAdminClient indicesAdminClient, String collectionName) {
        return exist -> {
            if (exist) {
                logger.info(collectionName + " already exists;");
                return collectionName;
            }

            CreateIndexResponse createIndexResponse = indicesAdminClient.prepareCreate(collectionName).get();
            logger.info("index : " + createIndexResponse.index());
            logger.info(collectionName + " sucessfully created;");
            return collectionName;
        };
    }

    private Observable<Boolean> exist(IndicesAdminClient indicesAdminClient, String collectionName) {
        return Observable.fromCallable(() -> indicesAdminClient.prepareExists(collectionName).get())
                .map(IndicesExistsResponse::isExists);
    }

    //curl -XHEAD 'localhost:9200/twitter?pretty'
    public Observable<String> createIfNotExists() {
        if (!ds.active())
            return Observable.just("None");

        return ds.client().map(transportClient -> transportClient.admin().indices())
                .toObservable()
                .flatMap(indicesAdminClient ->
                        Observable.from(LIST)
                                .flatMap(collectionName -> createIfNotExists(indicesAdminClient, collectionName))
                );
    }

    public Observable<String> reset() {
        if (!ds.active())
            return Observable.just("None");
        return ds.client().map(transportClient -> transportClient.admin().indices())
                .toObservable()
                .flatMap(indicesAdminClient -> Observable.from(LIST)
                        .flatMap(collectionName -> reset(indicesAdminClient, collectionName)));
    }

    private Observable<String> reset(IndicesAdminClient indicesAdminClient, String collectionName) {
        return exist(indicesAdminClient, collectionName)
                .map(exist -> {
                    if (exist) {
                        indicesAdminClient.prepareDelete(collectionName).get();
                    }
                    return false;
                })
                .map(create(indicesAdminClient, collectionName));
    }
}
