package uk.co.whitbread.hotel.account.model;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDetails {
    @NotNull
    private String title;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
}
