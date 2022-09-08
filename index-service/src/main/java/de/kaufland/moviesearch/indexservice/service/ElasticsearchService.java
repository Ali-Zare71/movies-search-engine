package de.kaufland.moviesearch.indexservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kaufland.moviesearch.indexservice.model.index.IndexMovieModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ElasticsearchService {
    private static final Logger infoLog = LogManager.getLogger("infoLog");
    private static final Logger LOGGER = LogManager.getLogger(RabbitMqConsumerService.class.getName());
    private final ObjectMapper objectMapper;

    @Value("${index.name}")
    private String indexName;
    private RestHighLevelClient client;
    private BulkProcessor bulkProcessor;
    @Value("${elasticsearch.timeOut.socket}")
    private int socketTimeOut;
    @Value("${elasticsearch.timeOut.connection}")
    private int connectionTimeOut;
    @Value("${elasticsearch.username}")
    private String username;
    @Value("${elasticsearch.password}")
    private String password;
    private String hostName;
    private String schema;
    private int port;
    @Value("${elasticsearch.address}")
    private String elasticsearchFullAddress;

    public ElasticsearchService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private void parsElasticsearchFullAddress() {
        infoLog.info("elasticsearchFullAddress: " + elasticsearchFullAddress);
        try {
            if (!StringUtils.isEmpty(elasticsearchFullAddress)) {
                elasticsearchFullAddress = StringUtils.removeEnd(elasticsearchFullAddress, "/");
                String[] split = elasticsearchFullAddress.split("://");
                schema = split[0];
                String[] address = split[1].split(":");
                hostName = address[0];
                port = Integer.valueOf(address[1]);
            }
        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

    /**
     * creates elasticsearch rest high level client and bulk processor.
     */
    @PostConstruct()
    private void init() {
        parsElasticsearchFullAddress();
        infoLog.info("Building elasticsearch client...");
        try {
            buildClient();
            buildBulkP();
        } catch (Exception e) {
            LOGGER.catching(e);
            infoLog.info("Building elasticsearch client failed!");
        }
        infoLog.info("Elasticsearch client built.");
        infoLog.info("Arbiter started.");
    }

    private void buildClient() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("SSL");

        // set up a TrustManager that trusts everything
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs,
                                           String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] certs,
                                           String authType) {
            }
        }}, new SecureRandom());

        final CredentialsProvider credentialsProvider =
                new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));

        RestClientBuilder builder = RestClient.builder(
                new HttpHost(hostName, port, schema))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(
                            HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder
                                .setSSLContext(sslContext)
                                .setDefaultCredentialsProvider(credentialsProvider);
                    }
                }).setRequestConfigCallback(
                        new RestClientBuilder.RequestConfigCallback() {
                            @Override
                            public RequestConfig.Builder customizeRequestConfig(
                                    RequestConfig.Builder requestConfigBuilder) {
                                return requestConfigBuilder
                                        .setConnectTimeout(connectionTimeOut)
                                        .setSocketTimeout(socketTimeOut);
                            }
                        }
                );

        client = new RestHighLevelClient(builder);
    }

    private void buildBulkP() {
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
//                System.out.println("before bulk - executionId: " + executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request,
                                  BulkResponse response) {
//                System.out.println("after bulk - executionId: " + executionId);
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request,
                                  Throwable failure) {
                LOGGER.catching(failure);
//                System.out.println("after bulk - failed - executionId: " + executionId);
            }
        };

        BulkProcessor.Builder builder = BulkProcessor.builder(
                (request, bulkListener) ->
                        client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener),
                listener);
        builder.setBulkActions(5000);
        builder.setBulkSize(new ByteSizeValue(5L, ByteSizeUnit.MB));
        builder.setConcurrentRequests(10);
        builder.setFlushInterval(TimeValue.timeValueSeconds(10L));
        builder.setBackoffPolicy(BackoffPolicy
                .constantBackoff(TimeValue.timeValueSeconds(1L), 3));
        bulkProcessor = builder.build();
    }

    @PreDestroy
    private void close() {
        infoLog.info("Closing elasticsearch client...");
        try {
            closeBulkP();
            closeClient();
        } catch (Exception e) {
            infoLog.info("Closing elasticsearch client failed!");
            LOGGER.catching(e);
        }
        infoLog.info("Elasticsearch client closed.");
        infoLog.info("Arbiter stopped.");
    }

    private void closeBulkP() throws InterruptedException {
        if (bulkProcessor != null) {
            bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);
        }
    }

    public void closeClient() throws IOException {
        client.close();
    }

    /**
     * stores the given model to elasticsearch.
     *
     * @param doc
     * @param indexName
     */
    public void storeDocument(IndexMovieModel doc, String indexName) {
        try {
            String jsonInString = objectMapper.writeValueAsString(doc);
            IndexRequest indexRequest = new IndexRequest(indexName)
                    .source(jsonInString, XContentType.JSON);
            indexRequest.id(String.valueOf(doc.getId()));
            bulkProcessor.add(indexRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * creates index mapping in first startup
     */
    public void checkAndCreateIndex() {
        try {
            GetIndexRequest request = new GetIndexRequest(indexName);
            boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
            if (!exists) {
                infoLog.info("index does not exists.");
                infoLog.info("creating index...");
                createIndex();
            }

        } catch (Exception e) {
            LOGGER.catching(e);
        }
    }

    private void createIndex() {
        try {
            InputStream resource = new ClassPathResource(
                    "index-mapping.json").getInputStream();
            String mappingJson = "";
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource))) {
                mappingJson = reader.lines()
                        .collect(Collectors.joining());
            }

            if (!StringUtils.isEmpty(mappingJson)) {
                CreateIndexRequest request = new CreateIndexRequest(indexName);
                request.source(mappingJson, XContentType.JSON);
                CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
            }
            infoLog.info("index created.");

        } catch (Exception e) {
            infoLog.info("failed to create index.");
            LOGGER.catching(e);
        }
    }

    @EventListener
    private void listener(ContextRefreshedEvent event) {
        try {
            checkAndCreateIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
