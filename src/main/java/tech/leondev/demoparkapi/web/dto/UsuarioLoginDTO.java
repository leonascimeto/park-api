package tech.leondev.demoparkapi.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UsuarioLoginDTO {
    @NotBlank(message = "campo obrigátorio")
    @Email(message = "Email inválido.")
    private String username;
    @NotBlank(message = "campo obrigátorio")
    @Size(min = 6, max = 6, message = "tamanho deve ser entre 6 e 6")
    private String password;
}
