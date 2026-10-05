package uk.co.whitbread.payments.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;


@Data
@Builder(toBuilder=true)
@With
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class Billing {

    @Schema(description = "name title.",
            example = "Mr")
    private String title;

    @Schema(description = "Card holder first name.",
            example = "James")
    private String firstName;

    @Schema(description = "Card holder last name.",
            example = "Bond")
    private String lastName;

    @Schema(description = "Booker email address.",
            example = "example@email.com")
    private String email;

    @Schema(description = "Booker telephone number.",
            example = "0789110237")
    private String telephone;

    @Schema(description = "Billing address for customer making payment")
    private Address address;

    @DynamoDbIgnore
    public Address getAddress() {
        return address;
    }

    @DynamoDbIgnore
    public String getTelephone() {
        return telephone;
    }

    @DynamoDbIgnore
    public String getEmail() {
        return email;
    }
}
