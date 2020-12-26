package com.devblack.frontend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pessoa implements Serializable {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private String dtNascimento;

}