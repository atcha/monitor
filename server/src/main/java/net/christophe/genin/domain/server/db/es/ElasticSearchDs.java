package net.christophe.genin.domain.server.db.es;

import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.christophe.genin.domain.server.db.nitrite.ConfigurationDto;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import rx.Single;

import java.net.InetAddress;

public interface ElasticSearchDs {

    Logger logger = LoggerFactory.getLogger(ElasticSearchDs.class);

    static void set(ConfigurationDto dto) {
        logger.info("state of Elastic Search : " + Instance.setSingleton(dto));
    }

    static ElasticSearchDs get() {
        return Instance.getSingleton();
    }

    default boolean active() {
        return false;
    }

    default Single<TransportClient> client() {
        return Single.error(new IllegalAccessException());
    }

    class Instance {
        static ElasticSearchDs singleton = new Null();

        private static ElasticSearchDs getSingleton() {
            return singleton;
        }

        private static synchronized boolean setSingleton(ConfigurationDto dto) {
            if (dto.getActivateElasticSearch()) {
                singleton = new ElasticSearchDsImpl(dto.getEsHost(), dto.getEsPort());
                return true;
            }
            singleton = new Null();
            return false;
        }
    }

    class Null implements ElasticSearchDs {

    }

    class ElasticSearchDsImpl implements ElasticSearchDs {
        private final String host;
        private final Integer port;

        private ElasticSearchDsImpl(String host, Integer port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public boolean active() {
            return true;
        }

        @Override
        public Single<TransportClient> client() {
            return Single.fromCallable(() -> new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port))
            );

        }
    }


}
