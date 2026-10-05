
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingAlerts complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingAlerts"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="dayOfArrivalAlert" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="weekendArrivalAlert" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="passThroughWeekendAlert" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="rateCapAlertAmountUKWide" type="{http://corporate.micros.com/1.0}Price"/&gt;
 *         &lt;element name="rateCapAlertAmountGreaterLondon" type="{http://corporate.micros.com/1.0}Price"/&gt;
 *         &lt;element name="rateCapAlertAmountIreland" type="{http://corporate.micros.com/1.0}Price"/&gt;
 *         &lt;element name="bookingAlertHotels" type="{http://corporate.micros.com/1.0}ArrayOfbookingAlertHotelsItemString"/&gt;
 *         &lt;element name="recipientEmailAddresses" type="{http://corporate.micros.com/1.0}ArrayOfrecipientEmailAddressesItemString"/&gt;
 *         &lt;element name="frequency"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="A"/&gt;
 *               &lt;enumeration value="D"/&gt;
 *               &lt;enumeration value="W"/&gt;
 *               &lt;enumeration value="M"/&gt;
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
@XmlType(name = "BookingAlerts", propOrder = {
    "dayOfArrivalAlert",
    "weekendArrivalAlert",
    "passThroughWeekendAlert",
    "rateCapAlertAmountUKWide",
    "rateCapAlertAmountGreaterLondon",
    "rateCapAlertAmountIreland",
    "bookingAlertHotels",
    "recipientEmailAddresses",
    "frequency"
})
public class BookingAlerts
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean dayOfArrivalAlert;
    protected boolean weekendArrivalAlert;
    protected boolean passThroughWeekendAlert;
    @XmlElement(required = true)
    protected Price rateCapAlertAmountUKWide;
    @XmlElement(required = true)
    protected Price rateCapAlertAmountGreaterLondon;
    @XmlElement(required = true)
    protected Price rateCapAlertAmountIreland;
    @XmlElement(required = true)
    protected ArrayOfbookingAlertHotelsItemString bookingAlertHotels;
    @XmlElement(required = true)
    protected ArrayOfrecipientEmailAddressesItemString recipientEmailAddresses;
    @XmlElement(required = true)
    protected String frequency;

    /**
     * Gets the value of the dayOfArrivalAlert property.
     * 
     */
    public boolean isDayOfArrivalAlert() {
        return dayOfArrivalAlert;
    }

    /**
     * Sets the value of the dayOfArrivalAlert property.
     * 
     */
    public void setDayOfArrivalAlert(boolean value) {
        this.dayOfArrivalAlert = value;
    }

    /**
     * Gets the value of the weekendArrivalAlert property.
     * 
     */
    public boolean isWeekendArrivalAlert() {
        return weekendArrivalAlert;
    }

    /**
     * Sets the value of the weekendArrivalAlert property.
     * 
     */
    public void setWeekendArrivalAlert(boolean value) {
        this.weekendArrivalAlert = value;
    }

    /**
     * Gets the value of the passThroughWeekendAlert property.
     * 
     */
    public boolean isPassThroughWeekendAlert() {
        return passThroughWeekendAlert;
    }

    /**
     * Sets the value of the passThroughWeekendAlert property.
     * 
     */
    public void setPassThroughWeekendAlert(boolean value) {
        this.passThroughWeekendAlert = value;
    }

    /**
     * Gets the value of the rateCapAlertAmountUKWide property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getRateCapAlertAmountUKWide() {
        return rateCapAlertAmountUKWide;
    }

    /**
     * Sets the value of the rateCapAlertAmountUKWide property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setRateCapAlertAmountUKWide(Price value) {
        this.rateCapAlertAmountUKWide = value;
    }

    /**
     * Gets the value of the rateCapAlertAmountGreaterLondon property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getRateCapAlertAmountGreaterLondon() {
        return rateCapAlertAmountGreaterLondon;
    }

    /**
     * Sets the value of the rateCapAlertAmountGreaterLondon property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setRateCapAlertAmountGreaterLondon(Price value) {
        this.rateCapAlertAmountGreaterLondon = value;
    }

    /**
     * Gets the value of the rateCapAlertAmountIreland property.
     * 
     * @return
     *     possible object is
     *     {@link Price }
     *     
     */
    public Price getRateCapAlertAmountIreland() {
        return rateCapAlertAmountIreland;
    }

    /**
     * Sets the value of the rateCapAlertAmountIreland property.
     * 
     * @param value
     *     allowed object is
     *     {@link Price }
     *     
     */
    public void setRateCapAlertAmountIreland(Price value) {
        this.rateCapAlertAmountIreland = value;
    }

    /**
     * Gets the value of the bookingAlertHotels property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbookingAlertHotelsItemString }
     *     
     */
    public ArrayOfbookingAlertHotelsItemString getBookingAlertHotels() {
        return bookingAlertHotels;
    }

    /**
     * Sets the value of the bookingAlertHotels property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbookingAlertHotelsItemString }
     *     
     */
    public void setBookingAlertHotels(ArrayOfbookingAlertHotelsItemString value) {
        this.bookingAlertHotels = value;
    }

    /**
     * Gets the value of the recipientEmailAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfrecipientEmailAddressesItemString }
     *     
     */
    public ArrayOfrecipientEmailAddressesItemString getRecipientEmailAddresses() {
        return recipientEmailAddresses;
    }

    /**
     * Sets the value of the recipientEmailAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfrecipientEmailAddressesItemString }
     *     
     */
    public void setRecipientEmailAddresses(ArrayOfrecipientEmailAddressesItemString value) {
        this.recipientEmailAddresses = value;
    }

    /**
     * Gets the value of the frequency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFrequency() {
        return frequency;
    }

    /**
     * Sets the value of the frequency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFrequency(String value) {
        this.frequency = value;
    }

}
