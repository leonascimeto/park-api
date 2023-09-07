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
import tech.leondev.demoparkapi.web.dto.UsuarioUpdateSenhaDTO;
import tech.leondev.demoparkapi.web.exception.ErrorMessage;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuario-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuario-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/usuarios/usuario-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuario_ComUsernameEPasswordValidos_RetornaUaurioCOm201(){
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

    @Test
    public void createUsuario_ComUsernameInvalido_RetornaErrorMessage422(){
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("", "976431"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComPasswordInvalido_RetornaErrorMessage422(){
        ErrorMessage response;
        response  = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("dexter@email.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);

        response = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("dexter@email.com", "1423"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);

        response = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("dexter@email.com", "1423148"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComUsernameRepetido_RetornaErrorMessage409(){
        ErrorMessage response = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDTO("ana@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(409);
    }

    @Test
    public void buscarUsuario_ComIdExistente_RetornaUsuaurioComStatus200(){
        UsuarioResponseDTO response;
        response = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(100);
        Assertions.assertThat(response.getUsername()).isEqualTo("ana@email.com");
        Assertions.assertThat(response.getRole()).isEqualTo("ADMIN");

        response = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(101);
        Assertions.assertThat(response.getUsername()).isEqualTo("bia@email.com");
        Assertions.assertThat(response.getRole()).isEqualTo("CLIENTE");

        response = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getId()).isEqualTo(101);
        Assertions.assertThat(response.getUsername()).isEqualTo("bia@email.com");
        Assertions.assertThat(response.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornaErrorComStatus404(){
        ErrorMessage response = testClient
                .get()
                .uri("/api/v1/usuarios/0")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(404);
    }

    @Test
    public void buscarUsuario_ComIdInexistente_RetornaErrorComStatus403(){
        ErrorMessage response = testClient
                .get()
                .uri("/api/v1/usuarios/0")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    public void editaSenha_ComDadosValidos_RetornaStatus204(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("123456", "321654", "321654"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("123456", "321654", "321654"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void editarSenha_ComUsuariosDiferentes_RetornaErrorComStatus403(){
        ErrorMessage response;
        response = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("123456", "321654", "321654"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(403);

        response = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("123456", "321654", "321654"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    public void editarSenha_ComCamposInvalidos_RetornaErrorComStatus422(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("1234561", "3216", ""))
                .exchange()
                .expectStatus().isEqualTo(422);
    }

    @Test
    public void editarSenha_ComSenhasInvalidas_RetornaErrorComStatus400(){
        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("123456", "321654", "147258"))
                .exchange()
                .expectStatus().isEqualTo(400);

        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioUpdateSenhaDTO("885522", "321654", "321654"))
                .exchange()
                .expectStatus().isEqualTo(400);
    }

    @Test
    public void buscarTodosUsuarios_RetornaUsuauriosComStatus200(){
        List<UsuarioResponseDTO> response = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDTO.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.size()).isEqualTo(3);
    }

    @Test
    public void buscarTodosUsuarios_ComUserDiferenteDeAdmin_RetornaStatus403(){
        ErrorMessage response = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatus()).isEqualTo(403);
    }
}
