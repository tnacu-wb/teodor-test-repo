
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingSummary complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingSummary"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="upcomingBooking" type="{http://corporate.micros.com/1.0}UpcomingBooking" minOccurs="0"/&gt;
 *         &lt;element name="frequentBooking" type="{http://corporate.micros.com/1.0}FrequentBooking" minOccurs="0"/&gt;
 *         &lt;element name="pastBooking" type="{http://corporate.micros.com/1.0}PastBooking" minOccurs="0"/&gt;
 *         &lt;element name="cancelledBooking" type="{http://corporate.micros.com/1.0}CancelledBooking" minOccurs="0"/&gt;
 *         &lt;element name="bookerStayer"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="STAYER"/&gt;
 *               &lt;enumeration value="BOOKER"/&gt;
 *               &lt;enumeration value="SUPER"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingSummary", propOrder = {
    "upcomingBooking",
    "frequentBooking",
    "pastBooking",
    "cancelledBooking",
    "bookerStayer"
})
public class BookingSummary2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected UpcomingBooking upcomingBooking;
    protected FrequentBooking frequentBooking;
    protected PastBooking pastBooking;
    protected CancelledBooking cancelledBooking;
    @XmlElement(required = true)
    protected String bookerStayer;

    /**
     * Gets the value of the upcomingBooking property.
     * 
     * @return
     *     possible object is
     *     {@link UpcomingBooking }
     *     
     */
    public UpcomingBooking getUpcomingBooking() {
        return upcomingBooking;
    }

    /**
     * Sets the value of the upcomingBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link UpcomingBooking }
     *     
     */
    public void setUpcomingBooking(UpcomingBooking value) {
        this.upcomingBooking = value;
    }

    /**
     * Gets the value of the frequentBooking property.
     * 
     * @return
     *     possible object is
     *     {@link FrequentBooking }
     *     
     */
    public FrequentBooking getFrequentBooking() {
        return frequentBooking;
    }

    /**
     * Sets the value of the frequentBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link FrequentBooking }
     *     
     */
    public void setFrequentBooking(FrequentBooking value) {
        this.frequentBooking = value;
    }

    /**
     * Gets the value of the pastBooking property.
     * 
     * @return
     *     possible object is
     *     {@link PastBooking }
     *     
     */
    public PastBooking getPastBooking() {
        return pastBooking;
    }

    /**
     * Sets the value of the pastBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link PastBooking }
     *     
     */
    public void setPastBooking(PastBooking value) {
        this.pastBooking = value;
    }

    /**
     * Gets the value of the cancelledBooking property.
     * 
     * @return
     *     possible object is
     *     {@link CancelledBooking }
     *     
     */
    public CancelledBooking getCancelledBooking() {
        return cancelledBooking;
    }

    /**
     * Sets the value of the cancelledBooking property.
     * 
     * @param value
     *     allowed object is
     *     {@link CancelledBooking }
     *     
     */
    public void setCancelledBooking(CancelledBooking value) {
        this.cancelledBooking = value;
    }

    /**
     * Gets the value of the bookerStayer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBookerStayer() {
        return bookerStayer;
    }

    /**
     * Sets the value of the bookerStayer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBookerStayer(String value) {
        this.bookerStayer = value;
    }

}
