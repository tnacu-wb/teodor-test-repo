
package uk.co.whitbread.bart.paststays.api;

import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, LocalDateTime>
{


    public LocalDateTime unmarshal(String value) {
        return (uk.co.whitbread.bart.util.adapter.DateTimeBinder.parseDateTime(value));
    }

    public String marshal(LocalDateTime value) {
        return (uk.co.whitbread.bart.util.adapter.DateTimeBinder.printDateTime(value));
    }

}
