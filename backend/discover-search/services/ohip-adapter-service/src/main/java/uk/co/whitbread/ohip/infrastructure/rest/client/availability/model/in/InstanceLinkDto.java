package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@Validated
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstanceLinkDto {

  @JsonProperty("href")
  private String href;
  @JsonProperty("rel")
  private String rel;
  @JsonProperty("templated")
  private Boolean templated;
  @JsonProperty("method")
  private MethodEnumDto method;
  @JsonProperty("targetSchema")
  private String targetSchema;
  @JsonProperty("operationId")
  private String operationId;
  @JsonProperty("title")
  private String title;

  public enum MethodEnumDto {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    OPTIONS("OPTIONS"),
    HEAD("HEAD");

    private final String value;

    MethodEnumDto(String value) {
      this.value = value;
    }

    @JsonCreator
    public static MethodEnumDto fromValue(String text) {
      final MethodEnumDto[] var1 = values();
      int var2 = var1.length;

      for (int var3 = 0; var3 < var2; ++var3) {
        final MethodEnumDto b = var1[var3];
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }

    @JsonValue
    @Override
    public String toString() {
      return String.valueOf(this.value);
    }
  }
}
