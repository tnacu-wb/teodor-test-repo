package uk.co.whitbread.payments.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

    @Schema(description = "Full name of agent.", example = "Mr Sean Connery")
    private String name;
    @Schema(description = "E-mail address of agent.", example = "sean.connery@whitbread.com")
    private String email;
}
