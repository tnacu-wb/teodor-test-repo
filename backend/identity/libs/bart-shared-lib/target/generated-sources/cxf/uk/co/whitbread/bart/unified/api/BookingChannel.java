
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingChannel complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingChannel"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bookingChannelCode" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="bookingChannelDescription" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingChannel", propOrder = {
    "bookingChannelCode",
    "bookingChannelDescription"
})
public class BookingChannel
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String bookingChannelCode;
    @XmlElement(required = true)
    protected String bookingChannelDescription;

    /**
     * Gets the value of the bookingChannelCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingChannelCode() {
        return bookingChannelCode;
    }

    /**
     * Sets the value of the bookingChannelCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingChannelCode(String value) {
        this.bookingChannelCode = value;
    }

    /**
     * Gets the value of the bookingChannelDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookingChannelDescription() {
        return bookingChannelDescription;
    }

    /**
     * Sets the value of the bookingChannelDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookingChannelDescription(String value) {
        this.bookingChannelDescription = value;
    }

}
