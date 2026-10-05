
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="hotelDataRequest" type="{http://bartws.micros.com/1.17}HotelDataRequest" minOccurs="0"/&gt;
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
    "hotelDataRequest"
})
@XmlRootElement(name = "HotelDataRequest")
public class HotelDataRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected HotelDataRequest2 hotelDataRequest;

    /**
     * Gets the value of the hotelDataRequest property.
     * 
     * @return
     *     possible object is
     *     {@link HotelDataRequest2 }
     *     
     */
    public HotelDataRequest2 getHotelDataRequest() {
        return hotelDataRequest;
    }

    /**
     * Sets the value of the hotelDataRequest property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelDataRequest2 }
     *     
     */
    public void setHotelDataRequest(HotelDataRequest2 value) {
        this.hotelDataRequest = value;
    }

}
