
package uk.co.whitbread.bart.business.api;

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
 *         &lt;element name="BookingPreferenceResult" type="{http://corporate.micros.com/1.0}BookingPreferenceResponse"/&gt;
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
    "bookingPreferenceResult"
})
@XmlRootElement(name = "BookingPreferenceResponse")
public class BookingPreferenceResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "BookingPreferenceResult", required = true)
    protected BookingPreferenceResponse2 bookingPreferenceResult;

    /**
     * Gets the value of the bookingPreferenceResult property.
     * 
     * @return
     *     possible object is
     *     {@link BookingPreferenceResponse2 }
     *     
     */
    public BookingPreferenceResponse2 getBookingPreferenceResult() {
        return bookingPreferenceResult;
    }

    /**
     * Sets the value of the bookingPreferenceResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingPreferenceResponse2 }
     *     
     */
    public void setBookingPreferenceResult(BookingPreferenceResponse2 value) {
        this.bookingPreferenceResult = value;
    }

}
