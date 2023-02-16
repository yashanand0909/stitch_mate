package com.mate.models.responses.auth;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtResponse implements Serializable {

  private final String jwttoken;
}
