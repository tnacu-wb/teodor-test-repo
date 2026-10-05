package com.whitbread.premierinn.api.response;

public class AcceptedCreditCardTest {

    public static com.whitbread.premierinn.api.response.AcceptedCreditCard create() {
        return new AutoValue_AcceptedCreditCard("AT", "1", "£", 1, false, "", "");
    }

}
