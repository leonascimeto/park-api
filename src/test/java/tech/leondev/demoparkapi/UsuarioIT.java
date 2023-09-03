package tech.leondev.demoparkapi;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.leondev.demoparkapi.web.dto.UsuarioCreateDTO;
import tech.leondev.demoparkapi.web.dto.UsuarioResponseDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuario-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuario-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuario-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuaion_ComUsernameEPasswordValidos_RetornaUaurioCOm201(){
        UsuarioResponseDTO response = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("dexter@email.com", "976431"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isNotNull();
        Assertions.assertThat(response.getUsername()).isEqualTo("dexter@email.com");
        Assertions.assertThat(response.getRole()).isEqualTo("CLIENTE");
    }
}
