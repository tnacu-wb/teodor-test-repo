package uk.co.whitbread.wallet.domain.ports.secondary;

import java.io.InputStream;

public interface CertsRetrieverOutPort {

  InputStream getP12();

  InputStream getAppleWwdrca();
}
