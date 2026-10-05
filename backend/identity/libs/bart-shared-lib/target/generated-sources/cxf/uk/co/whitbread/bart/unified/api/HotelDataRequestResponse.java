
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
 *         &lt;element name="HotelDataRequestResult" type="{http://bartws.micros.com/1.17}HotelDataResponse"/&gt;
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
    "hotelDataRequestResult"
})
@XmlRootElement(name = "HotelDataRequestResponse")
public class HotelDataRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "HotelDataRequestResult", required = true)
    protected HotelDataResponse hotelDataRequestResult;

    /**
     * Gets the value of the hotelDataRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link HotelDataResponse }
     *     
     */
    public HotelDataResponse getHotelDataRequestResult() {
        return hotelDataRequestResult;
    }

    /**
     * Sets the value of the hotelDataRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link HotelDataResponse }
     *     
     */
    public void setHotelDataRequestResult(HotelDataResponse value) {
        this.hotelDataRequestResult = value;
    }

}
