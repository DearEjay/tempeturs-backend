package petfinder.elasticclient;

import java.io.IOException;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.lucene.queryparser.ext.Extensions.Pair;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

public class ElasticClient {
    private static ElasticClient instance = null;
    
    protected static RestHighLevelClient client;
        
    protected ElasticClient() {
        
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("ctqf200l", "cxi28osdsjapfob7"));

        RestClientBuilder builder = RestClient.builder(new HttpHost("spruce-444384.us-east-1.bonsaisearch.net", 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        httpClientBuilder.disableAuthCaching(); 
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });
        
        client = new RestHighLevelClient(builder.build()); 
    }
    
    public static ElasticClient getInstance() {
       if(instance == null) {
          instance = new ElasticClient();
       }
       return instance;
    }
    
    public GetResponse get(String index, String type, String id) throws IOException {
        GetRequest getRequest = new GetRequest(
                index, 
                type,  
                id);
        
        return ElasticClient.getInstance().getClient().get(getRequest);
        
    }
    
    public SearchResponse search(String index, String type, String key, String value) throws IOException {
        
        //Search for login document with Elastic
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
        searchSourceBuilder.query(QueryBuilders.matchPhraseQuery(key, value));
        searchRequest.source(searchSourceBuilder);
        
        return client.search(searchRequest);
    }
    
    public SearchResponse searchWildcard(String index, String type, String key, String value) throws IOException {
        
        //Search for login document with Elastic
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
        searchSourceBuilder.query(QueryBuilders.wildcardQuery(key, value));
        searchRequest.source(searchSourceBuilder);
        
        return client.search(searchRequest);
    }
    
    public SearchResponse searchMultifieldsWithRange(String index, String type, Map<String,String> fields, Map<String, Pair<String,String>> rangeFields) throws IOException {
        
        BoolQueryBuilder b = new BoolQueryBuilder();
        b.must(QueryBuilders.matchPhraseQuery("classification", "SITTER"));
        
        for (Map.Entry<String, String> entry : fields.entrySet()){
            b.must(QueryBuilders.matchQuery(entry.getKey(), entry.getValue()));
        }
        
        for (Map.Entry<String, Pair<String,String>> entry : rangeFields.entrySet()){
            System.out.println(entry.getValue().cur);
            System.out.println(entry.getValue().cud);

            b.must( QueryBuilders.rangeQuery(entry.getKey()).from( entry.getValue().cur, true).to( entry.getValue().cud, true));
        }
        
        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
        searchSourceBuilder.query(b);
        searchRequest.source(searchSourceBuilder);
        
        return client.search(searchRequest);
    }
    
    public IndexResponse create(String index, String type, String key, String value) throws IOException {
        
        IndexRequest request = new IndexRequest(
                index, 
                type,  
                key);
        
        request.source(value, XContentType.JSON);

        return client.index(request);

    }
    
    public UpdateResponse update(String index, String type, String id, String value) throws IOException {
        
        UpdateRequest request = new UpdateRequest(
                index, 
                type,  
                id);
        
        request.doc(value, XContentType.JSON);

        return client.update(request);

    }
    
    public DeleteResponse delete(String index, String type, String id) throws IOException {
        
        DeleteRequest request = new DeleteRequest(
                index, 
                type,  
                id);
        
        return client.delete(request);

    }
    
    public RestHighLevelClient getClient() {
        
        return client;
     }
 }
