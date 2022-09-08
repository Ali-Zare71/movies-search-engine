package de.kaufland.moviesearch.indexservice.searchservice.service;

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
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

@Service
public class ElasticsearchService {
    private static final Logger infoLog = LogManager.getLogger("infoLog");
    private static final Logger LOGGER = LogManager.getLogger(ElasticsearchService.class.getName());

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

    private void parsElasticsearchFullAddress() {
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
     * creates elasticsearch rest high level client.
     */
    @PostConstruct()
    private void init() {
        parsElasticsearchFullAddress();
        infoLog.info("Building elasticsearch client...");
        try {
            buildClient();
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
     * executes given search request and return the elasticsearch response.
     * @param searchRequest
     * @return
     */
    public SearchResponse executeQuery(SearchRequest searchRequest) {
        try {
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.catching(e);
        }
        return null;
    }

}
