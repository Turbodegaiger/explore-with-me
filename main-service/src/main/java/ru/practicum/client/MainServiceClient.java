package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.EndpointHit;

import java.util.List;

@Service
public class MainServiceClient extends BaseClient {

    @Autowired
    public MainServiceClient(@Value("${stats-client.url:http://localhost:9090}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public void saveHit(EndpointHit hit) {
        post("/hit", hit);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, Boolean unique) {
        StringBuilder sb = new StringBuilder();
        String urisString = uris.toString();
        urisString = sb.append(urisString, 1, urisString.length() - 1).toString();
        return get("/stats?start=" + start + "&end=" + end + "&uris=" + urisString + "&unique=" + unique);
    }
}
