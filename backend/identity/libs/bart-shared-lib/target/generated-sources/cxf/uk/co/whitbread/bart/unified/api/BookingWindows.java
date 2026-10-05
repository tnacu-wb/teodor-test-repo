
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingWindows complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingWindows"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="daysAhead" type="{http://www.w3.org/2001/XMLSchema}long"/&gt;
 *         &lt;element name="bookingChannel" type="{http://bartws.micros.com/1.17}BookingChannel"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingWindows", propOrder = {
    "daysAhead",
    "bookingChannel"
})
public class BookingWindows
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected long daysAhead;
    @XmlElement(required = true)
    protected BookingChannel bookingChannel;

    /**
     * Gets the value of the daysAhead property.
     * 
     */
    public long getDaysAhead() {
        return daysAhead;
    }

    /**
     * Sets the value of the daysAhead property.
     * 
     */
    public void setDaysAhead(long value) {
        this.daysAhead = value;
    }

    /**
     * Gets the value of the bookingChannel property.
     * 
     * @return
     *     possible object is
     *     {@link BookingChannel }
     *     
     */
    public BookingChannel getBookingChannel() {
        return bookingChannel;
    }

    /**
     * Sets the value of the bookingChannel property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingChannel }
     *     
     */
    public void setBookingChannel(BookingChannel value) {
        this.bookingChannel = value;
    }

}
