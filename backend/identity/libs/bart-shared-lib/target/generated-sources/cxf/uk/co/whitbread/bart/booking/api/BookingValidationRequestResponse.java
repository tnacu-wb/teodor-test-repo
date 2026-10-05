
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="BookingValidationRequestResult" type="{http://bartws.micros.com/1.31}BookingValidationResponse3"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "bookingValidationRequestResult"
})
@XmlRootElement(name = "BookingValidationRequestResponse")
public class BookingValidationRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "BookingValidationRequestResult", required = true)
    protected BookingValidationResponse3 bookingValidationRequestResult;

    /**
     * Gets the value of the bookingValidationRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link BookingValidationResponse3 }
     *     
     */
    public BookingValidationResponse3 getBookingValidationRequestResult() {
        return bookingValidationRequestResult;
    }

    /**
     * Sets the value of the bookingValidationRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingValidationResponse3 }
     *     
     */
    public void setBookingValidationRequestResult(BookingValidationResponse3 value) {
        this.bookingValidationRequestResult = value;
    }

}
