package uk.co.whitbread.shared.cdh.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Participants {

    private boolean initiator;
    private int participantId;
    private boolean delegated;
    private boolean terms;
    private boolean directdebit;
    private String email;
    private String shared;
    private String name;
}
