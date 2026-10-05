package uk.co.whitbread.marketing.model.newsletter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ContactType {
    email("Email"),
    phone("Telephone"),
    email_contact_channel_id("EmailContactChannelId");
    private final String type;
}
