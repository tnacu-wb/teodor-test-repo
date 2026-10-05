
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for HotelListResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="HotelListResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="hotelSummaries" type="{http://bartws.micros.com/1.17}ArrayOfHotelSummaryHotelSummaries"/&gt;
 *         &lt;element name="hotelListError" type="{http://bartws.micros.com/1.17}ErrorDetails" minOccurs="0"/&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.17}ErrorDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HotelListResponse", propOrder = {
    "hotelSummaries",
    "hotelListError",
    "errorDetail"
})
public class HotelListResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfHotelSummaryHotelSummaries hotelSummaries;
    protected ErrorDetails hotelListError;
    protected ErrorDetails errorDetail;

    /**
     * Gets the value of the hotelSummaries property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfHotelSummaryHotelSummaries }
     *     
     */
    public ArrayOfHotelSummaryHotelSummaries getHotelSummaries() {
        return hotelSummaries;
    }

    /**
     * Sets the value of the hotelSummaries property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfHotelSummaryHotelSummaries }
     *     
     */
    public void setHotelSummaries(ArrayOfHotelSummaryHotelSummaries value) {
        this.hotelSummaries = value;
    }

    /**
     * Gets the value of the hotelListError property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getHotelListError() {
        return hotelListError;
    }

    /**
     * Sets the value of the hotelListError property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setHotelListError(ErrorDetails value) {
        this.hotelListError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetails }
     *     
     */
    public ErrorDetails getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetails }
     *     
     */
    public void setErrorDetail(ErrorDetails value) {
        this.errorDetail = value;
    }

}
