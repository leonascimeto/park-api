package tech.leondev.demoparkapi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import tech.leondev.demoparkapi.web.dto.EstacionamentoCreateDTO;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/estacionamentos/estacionamento-delete.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/estacionamentos/estacionamento-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/estacionamentos/estacionamento-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void criarCheckin_comDadosValidos_RetornarCreatedAndLocation201(){
        EstacionamentoCreateDTO crateDTO = EstacionamentoCreateDTO.builder()
                .placa("MER-1111")
                .cor("cinza")
                .marca("fiat")
                .modelo("palio")
                .clienteCpf("776.809.540-54")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(crateDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("MER-1111")
                .jsonPath("cor").isEqualTo("cinza")
                .jsonPath("modelo").isEqualTo("palio")
                .jsonPath("clienteCpf").isEqualTo("776.809.540-54")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void criarCheckin_comDadosInvalido_RetornarErro422(){
        EstacionamentoCreateDTO crateDTO = EstacionamentoCreateDTO.builder()
                .placa("ME-1111")
                .cor("")
                .marca("fiat")
                .modelo("palio")
                .clienteCpf("809.540-54")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(crateDTO)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo(422)
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckin_comUsuarioComPerfilInvalido_RetornarErro403(){
        EstacionamentoCreateDTO crateDTO = EstacionamentoCreateDTO.builder()
                .placa("MER-1111")
                .cor("cinza")
                .marca("fiat")
                .modelo("palio")
                .clienteCpf("776.809.540-54")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .bodyValue(crateDTO)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    public void criarCheckin_comCpfInexistente_RetornarErro404(){
        EstacionamentoCreateDTO crateDTO = EstacionamentoCreateDTO.builder()
                .placa("MER-1111")
                .cor("cinza")
                .marca("fiat")
                .modelo("palio")
                .clienteCpf("803.749.730-59")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(crateDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("POST");
    }

    @Test
    @Sql(scripts = "/sql/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/estacionamentos/estacionamento-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void criarCheckin_comVagasLivresInexistentes_RetornarErro404(){
        EstacionamentoCreateDTO crateDTO = EstacionamentoCreateDTO.builder()
                .placa("MER-1111")
                .cor("cinza")
                .marca("fiat")
                .modelo("palio")
                .clienteCpf("221.660.140-38")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/checkin")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .bodyValue(crateDTO)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("POST");
    }
}
