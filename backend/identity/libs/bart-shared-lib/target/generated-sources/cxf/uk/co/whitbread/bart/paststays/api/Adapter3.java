
package uk.co.whitbread.bart.paststays.api;

import java.time.LocalDate;
import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter3
    extends XmlAdapter<String, LocalDate>
{


    public LocalDate unmarshal(String value) {
        return (uk.co.whitbread.bart.util.adapter.DateBinder.parseDate(value));
    }

    public String marshal(LocalDate value) {
        return (uk.co.whitbread.bart.util.adapter.DateBinder.printDate(value));
    }

}
