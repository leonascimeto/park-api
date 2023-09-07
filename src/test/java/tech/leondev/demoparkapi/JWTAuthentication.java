package tech.leondev.demoparkapi;

import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.leondev.demoparkapi.jwt.JwtToken;
import tech.leondev.demoparkapi.web.dto.UsuarioLoginDTO;

import java.util.function.Consumer;

public class JWTAuthentication {

    public static Consumer<HttpHeaders> getHeaderAuthorization(WebTestClient client, String username, String password){
        String token = client
                .post()
                .uri("/api/v1/auth")
                .bodyValue(new UsuarioLoginDTO(username, password))
                .exchange()
                .expectStatus().isOk()
                .expectBody(JwtToken.class)
                .returnResult().getResponseBody().getToken();
        return header -> header.add(HttpHeaders.AUTHORIZATION, "Bearer "+ token);
    }
}
