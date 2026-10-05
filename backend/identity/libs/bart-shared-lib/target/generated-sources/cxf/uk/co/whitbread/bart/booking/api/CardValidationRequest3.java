
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CardValidationRequest3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CardValidationRequest3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="guestDetails" type="{http://bartws.micros.com/1.31}ArrayOfguestGuest"/&gt;
 *         &lt;element name="bookerDetails" type="{http://bartws.micros.com/1.31}BookerDetails"/&gt;
 *         &lt;element name="paymentCard" type="{http://bartws.micros.com/1.31}PaymentCard3"/&gt;
 *         &lt;element name="donation" type="{http://bartws.micros.com/1.31}Donation" minOccurs="0"/&gt;
 *         &lt;element name="isBusinessTrip" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="arrivalTime" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="breakfastUpsell" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastBreakfastUpsell" minOccurs="0"/&gt;
 *         &lt;element name="upsellItems" type="{http://bartws.micros.com/1.31}ArrayOfUpsellItemsUpsellUnit" minOccurs="0"/&gt;
 *         &lt;element name="browserAccept"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="1000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="browserUserAgent"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="1000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="IPAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardValidationRequest3", propOrder = {
    "sessionID",
    "guestDetails",
    "bookerDetails",
    "paymentCard",
    "donation",
    "isBusinessTrip",
    "arrivalTime",
    "breakfastUpsell",
    "upsellItems",
    "browserAccept",
    "browserUserAgent",
    "ipAddress"
})
public class CardValidationRequest3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected ArrayOfguestGuest guestDetails;
    @XmlElement(required = true)
    protected BookerDetails bookerDetails;
    @XmlElement(required = true)
    protected PaymentCard3 paymentCard;
    protected Donation donation;
    protected boolean isBusinessTrip;
    @XmlElement(required = true)
    protected String arrivalTime;
    protected ArrayOfbreakfastBreakfastUpsell breakfastUpsell;
    protected ArrayOfUpsellItemsUpsellUnit upsellItems;
    @XmlElement(required = true)
    protected String browserAccept;
    @XmlElement(required = true)
    protected String browserUserAgent;
    @XmlElement(name = "IPAddress", required = true)
    protected String ipAddress;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the guestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfguestGuest }
     *     
     */
    public ArrayOfguestGuest getGuestDetails() {
        return guestDetails;
    }

    /**
     * Sets the value of the guestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfguestGuest }
     *     
     */
    public void setGuestDetails(ArrayOfguestGuest value) {
        this.guestDetails = value;
    }

    /**
     * Gets the value of the bookerDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookerDetails }
     *     
     */
    public BookerDetails getBookerDetails() {
        return bookerDetails;
    }

    /**
     * Sets the value of the bookerDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookerDetails }
     *     
     */
    public void setBookerDetails(BookerDetails value) {
        this.bookerDetails = value;
    }

    /**
     * Gets the value of the paymentCard property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard3 }
     *     
     */
    public PaymentCard3 getPaymentCard() {
        return paymentCard;
    }

    /**
     * Sets the value of the paymentCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard3 }
     *     
     */
    public void setPaymentCard(PaymentCard3 value) {
        this.paymentCard = value;
    }

    /**
     * Gets the value of the donation property.
     * 
     * @return
     *     possible object is
     *     {@link Donation }
     *     
     */
    public Donation getDonation() {
        return donation;
    }

    /**
     * Sets the value of the donation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Donation }
     *     
     */
    public void setDonation(Donation value) {
        this.donation = value;
    }

    /**
     * Gets the value of the isBusinessTrip property.
     * 
     */
    public boolean isIsBusinessTrip() {
        return isBusinessTrip;
    }

    /**
     * Sets the value of the isBusinessTrip property.
     * 
     */
    public void setIsBusinessTrip(boolean value) {
        this.isBusinessTrip = value;
    }

    /**
     * Gets the value of the arrivalTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Sets the value of the arrivalTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalTime(String value) {
        this.arrivalTime = value;
    }

    /**
     * Gets the value of the breakfastUpsell property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfbreakfastBreakfastUpsell }
     *     
     */
    public ArrayOfbreakfastBreakfastUpsell getBreakfastUpsell() {
        return breakfastUpsell;
    }

    /**
     * Sets the value of the breakfastUpsell property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfbreakfastBreakfastUpsell }
     *     
     */
    public void setBreakfastUpsell(ArrayOfbreakfastBreakfastUpsell value) {
        this.breakfastUpsell = value;
    }

    /**
     * Gets the value of the upsellItems property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfUpsellItemsUpsellUnit }
     *     
     */
    public ArrayOfUpsellItemsUpsellUnit getUpsellItems() {
        return upsellItems;
    }

    /**
     * Sets the value of the upsellItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfUpsellItemsUpsellUnit }
     *     
     */
    public void setUpsellItems(ArrayOfUpsellItemsUpsellUnit value) {
        this.upsellItems = value;
    }

    /**
     * Gets the value of the browserAccept property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowserAccept() {
        return browserAccept;
    }

    /**
     * Sets the value of the browserAccept property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowserAccept(String value) {
        this.browserAccept = value;
    }

    /**
     * Gets the value of the browserUserAgent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBrowserUserAgent() {
        return browserUserAgent;
    }

    /**
     * Sets the value of the browserUserAgent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBrowserUserAgent(String value) {
        this.browserUserAgent = value;
    }

    /**
     * Gets the value of the ipAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPAddress() {
        return ipAddress;
    }

    /**
     * Sets the value of the ipAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPAddress(String value) {
        this.ipAddress = value;
    }

}
