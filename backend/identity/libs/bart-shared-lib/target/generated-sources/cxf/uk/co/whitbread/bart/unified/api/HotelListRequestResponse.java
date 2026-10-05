
package uk.co.whitbread.bart.unified.api;

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
 *         &lt;element name="HotelListRequestResult" type="{http://bartws.micros.com/1.17}HotelListResponse"/&gt;
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
    "hotelListRequestResult"
})
@XmlRootElement(name = "HotelListRequestResponse")
public class HotelListRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "HotelListRequestResult", required = true)
    protected HotelListResponse hotelListRequestResult;

    /**
     * Gets the value of the hotelListRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link HotelListResponse }
     *     
     */
    public HotelListResponse getHotelListRequestResult() {
        return hotelListRequestResult;
    }

    /**
     * Sets the value of the hotelListRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelListResponse }
     *     
     */
    public void setHotelListRequestResult(HotelListResponse value) {
        this.hotelListRequestResult = value;
    }

}
