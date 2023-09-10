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

    @Test
    public void buscarPerfil_comPerfilAdmin_RetornarDados200(){
        testClient.get().uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-101300")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("clienteCpf").isEqualTo("776.809.540-54")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void buscarPerfil_comPerfilCliente_RetornarDados200(){
        testClient.get().uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-101300")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bob@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("clienteCpf").isEqualTo("776.809.540-54")
                .jsonPath("recibo").isEqualTo("20230313-101300")
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();
    }

    @Test
    public void buscarCheckin_ComReciboInexistente_RetornarErro404(){
        testClient.get().uri("/api/v1/estacionamentos/checkin/{recibo}", "20230313-000000")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("GET");
    }

    @Test
    public void criarCheckout_ComReciboExistente_RetornarSucesso200(){
        testClient.put().uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-101300")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("dataEntrada").isEqualTo("2023-03-13 10:15:00")
                .jsonPath("dataSaida").exists()
                .jsonPath("valor").exists()
                .jsonPath("desconto").exists()
                .jsonPath("clienteCpf").isEqualTo("776.809.540-54")
                .jsonPath("vagaCodigo").isEqualTo("A-01")
                .jsonPath("recibo").isEqualTo("20230313-101300");

    }

    @Test
    public void criarCheckout_ComReciboInexistente_RetornarError404(){
        testClient.put().uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-000000")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "ana@email.com.br", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo(404)
                .jsonPath("method").isEqualTo("PUT");

    }

    @Test
    public void criarCheckout_ComRoleCliebte_RetornarError403(){
        testClient.put().uri("/api/v1/estacionamentos/checkout/{recibo}", "20230313-101300")
                .headers(JWTAuthentication.getHeaderAuthorization(testClient, "bia@email.com.br", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo(403)
                .jsonPath("method").isEqualTo("PUT");

    }
}
