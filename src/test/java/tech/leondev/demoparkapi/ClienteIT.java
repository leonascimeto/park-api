package tech.leondev.demoparkapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.leondev.demoparkapi.web.dto.ClienteCreateDTO;
import tech.leondev.demoparkapi.web.dto.ClienteResponseDTO;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/clientes/cliente-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/cliente-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/clientes/cliente-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ClienteIT {

    @Autowired
    WebTestClient testClient;
    @Test
    public void createCliente_ComDadosValidos_RetornaClienteComStatus201(){
        ClienteResponseDTO response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("Tobias Ferreira", "868.414.600-06"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClienteResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getNome()).isEqualTo("Tobias Ferreira");
        Assertions.assertThat(response.getCpf()).isEqualTo("868.414.600-06");
    }

    @Test
    public void createCliente_ComCpfJaCadastrado_RetornaErrorMessage409(){
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("Tobias Ferreira", "651.793.630-04"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(409);
    }

    @Test
    public void createCliente_ComDadosInvalidos_RetornaErrorMessage422(){
        ErrorMessage response;
        response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);

        response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("bob maguirre", "000.000.000-00"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);

        response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "toby@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("bob", "888.136.390-90"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    public void createCliente_ComUsuarioNaoPermitido_RetornaErrorMessage403(){
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .bodyValue(new ClienteCreateDTO("Tobias Ferreira", "651.793.630-04"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(403);
    }
}
