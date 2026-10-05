package uk.co.whitbread.ohip.generated.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import uk.co.whitbread.ohip.generated.models.CharacterUDFsDto;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserDefinedFieldsDto
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-08-25T07:50:12.143180+03:00[Europe/Bucharest]", comments = "Generator version: 7.12.0")
public class UserDefinedFieldsDto {

  @Valid
  private List<@Valid CharacterUDFsDto> characterUDFs = new ArrayList<>();

  public UserDefinedFieldsDto characterUDFs(List<@Valid CharacterUDFsDto> characterUDFs) {
    this.characterUDFs = characterUDFs;
    return this;
  }

  public UserDefinedFieldsDto addCharacterUDFsItem(CharacterUDFsDto characterUDFsItem) {
    if (this.characterUDFs == null) {
      this.characterUDFs = new ArrayList<>();
    }
    this.characterUDFs.add(characterUDFsItem);
    return this;
  }

  /**
   * Get characterUDFs
   * @return characterUDFs
   */
  @Valid 
  @Schema(name = "characterUDFs", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("characterUDFs")
  public List<@Valid CharacterUDFsDto> getCharacterUDFs() {
    return characterUDFs;
  }

  public void setCharacterUDFs(List<@Valid CharacterUDFsDto> characterUDFs) {
    this.characterUDFs = characterUDFs;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDefinedFieldsDto userDefinedFieldsDto = (UserDefinedFieldsDto) o;
    return Objects.equals(this.characterUDFs, userDefinedFieldsDto.characterUDFs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(characterUDFs);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDefinedFieldsDto {\n");
    sb.append("    characterUDFs: ").append(toIndentedString(characterUDFs)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

