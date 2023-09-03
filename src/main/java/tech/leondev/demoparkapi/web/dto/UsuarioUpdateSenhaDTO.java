package tech.leondev.demoparkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioUpdateSenhaDTO {
    @NotBlank(message = "campo obrigátorio")
    @Size(min = 6, max = 6, message = "tamanho deve ser entre 6 e 6")
    private String senhaAtual;
    @NotBlank(message = "campo obrigátorio")
    @Size(min = 6, max = 6, message = "tamanho deve ser entre 6 e 6")
    private String novaSenha;
    @NotBlank(message = "campo obrigátorio")
    @Size(min = 6, max = 6, message = "tamanho deve ser entre 6 e 6")
    private String confirmaSenha;
}
