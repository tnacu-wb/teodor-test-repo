package uk.co.whitbread.shared.azureemail.requestbuilder.utils;

import lombok.experimental.UtilityClass;
import uk.co.whitbread.shared.azureemail.genericEmail.api.ClientID;
import uk.co.whitbread.shared.azureemail.genericEmail.api.Owner;

@UtilityClass
public class RequestUtils {

  private static final int CLIENT_ID = 1;

  public static Owner buildOwner() {
    Owner owner = new Owner();
    ClientID clientID = new ClientID();
    clientID.setID(CLIENT_ID);
    owner.setClient(clientID);
    return owner;
  }
}
