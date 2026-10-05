package uk.co.whitbread.payments.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EMerchantDetails {

    private String username;
    private String password;
}
