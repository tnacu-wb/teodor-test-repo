
package uk.co.whitbread.bart.unified.api;

import java.time.LocalTime;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter2
    extends XmlAdapter<String, LocalTime>
{


    public LocalTime unmarshal(String value) {
        return (uk.co.whitbread.bart.util.adapter.TimeBinder.parseTime(value));
    }

    public String marshal(LocalTime value) {
        return (uk.co.whitbread.bart.util.adapter.TimeBinder.printTime(value));
    }

}
