package tech.leondev.demoparkapi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VagaCreateDTO {
    @NotBlank
    @Size(min = 4, max = 4)
    private String codigo;
    @NotBlank
    @Pattern(regexp = "LIVRE|OCUPADA")
    private String status;
}
