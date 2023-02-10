package com.mate.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BaseApiResponse {
  private String status = "SUCCESS";
  private String message;
}
