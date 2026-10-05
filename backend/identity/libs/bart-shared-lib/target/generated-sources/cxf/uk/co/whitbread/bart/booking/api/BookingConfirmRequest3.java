
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for BookingConfirmRequest3 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BookingConfirmRequest3"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="affiliateData" type="{http://bartws.micros.com/1.31}AffiliateData" minOccurs="0"/&gt;
 *         &lt;element name="guestDetails" type="{http://bartws.micros.com/1.31}ArrayOfguestGuest"/&gt;
 *         &lt;element name="bookerDetails" type="{http://bartws.micros.com/1.31}BookerDetails"/&gt;
 *         &lt;element name="guaranteeMethod" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="CA"/&gt;
 *               &lt;enumeration value="CC"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="paymentCard" type="{http://bartws.micros.com/1.31}PaymentCard3" minOccurs="0"/&gt;
 *         &lt;element name="donation" type="{http://bartws.micros.com/1.31}Donation" minOccurs="0"/&gt;
 *         &lt;element name="electronicInvoiceRequired" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="isBusinessTrip" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="referrer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="arrivalTime" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="specialRequirements" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="breakfastUpsell" type="{http://bartws.micros.com/1.31}ArrayOfbreakfastBreakfastUpsell" minOccurs="0"/&gt;
 *         &lt;element name="dinnerUpsell" type="{http://bartws.micros.com/1.31}ArrayOfdinnerDinnerUpsell" minOccurs="0"/&gt;
 *         &lt;element name="upsellItems" type="{http://bartws.micros.com/1.31}ArrayOfUpsellItemsUpsellUnit" minOccurs="0"/&gt;
 *         &lt;element name="smsConfirmation" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="IPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cbtDetails" type="{http://bartws.micros.com/1.31}CBTDetails" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BookingConfirmRequest3", propOrder = {
    "sessionID",
    "affiliateData",
    "guestDetails",
    "bookerDetails",
    "guaranteeMethod",
    "paymentCard",
    "donation",
    "electronicInvoiceRequired",
    "isBusinessTrip",
    "referrer",
    "arrivalTime",
    "specialRequirements",
    "breakfastUpsell",
    "dinnerUpsell",
    "upsellItems",
    "smsConfirmation",
    "ipAddress",
    "confirmationNumber",
    "cbtDetails"
})
public class BookingConfirmRequest3
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    protected AffiliateData affiliateData;
    @XmlElement(required = true)
    protected ArrayOfguestGuest guestDetails;
    @XmlElement(required = true)
    protected BookerDetails bookerDetails;
    protected String guaranteeMethod;
    protected PaymentCard3 paymentCard;
    protected Donation donation;
    protected Boolean electronicInvoiceRequired;
    protected boolean isBusinessTrip;
    protected String referrer;
    @XmlElement(required = true)
    protected String arrivalTime;
    protected String specialRequirements;
    protected ArrayOfbreakfastBreakfastUpsell breakfastUpsell;
    protected ArrayOfdinnerDinnerUpsell dinnerUpsell;
    protected ArrayOfUpsellItemsUpsellUnit upsellItems;
    protected boolean smsConfirmation;
    @XmlElement(name = "IPAddress")
    protected String ipAddress;
    protected String confirmationNumber;
    protected CBTDetails cbtDetails;

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
     * Gets the value of the affiliateData property.
     * 
     * @return
     *     possible object is
     *     {@link AffiliateData }
     *     
     */
    public AffiliateData getAffiliateData() {
        return affiliateData;
    }

    /**
     * Sets the value of the affiliateData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AffiliateData }
     *     
     */
    public void setAffiliateData(AffiliateData value) {
        this.affiliateData = value;
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
     * Gets the value of the guaranteeMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGuaranteeMethod() {
        return guaranteeMethod;
    }

    /**
     * Sets the value of the guaranteeMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGuaranteeMethod(String value) {
        this.guaranteeMethod = value;
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
     * Gets the value of the electronicInvoiceRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isElectronicInvoiceRequired() {
        return electronicInvoiceRequired;
    }

    /**
     * Sets the value of the electronicInvoiceRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setElectronicInvoiceRequired(Boolean value) {
        this.electronicInvoiceRequired = value;
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
     * Gets the value of the referrer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferrer() {
        return referrer;
    }

    /**
     * Sets the value of the referrer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferrer(String value) {
        this.referrer = value;
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
     * Gets the value of the specialRequirements property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialRequirements() {
        return specialRequirements;
    }

    /**
     * Sets the value of the specialRequirements property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialRequirements(String value) {
        this.specialRequirements = value;
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
     * Gets the value of the dinnerUpsell property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public ArrayOfdinnerDinnerUpsell getDinnerUpsell() {
        return dinnerUpsell;
    }

    /**
     * Sets the value of the dinnerUpsell property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfdinnerDinnerUpsell }
     *     
     */
    public void setDinnerUpsell(ArrayOfdinnerDinnerUpsell value) {
        this.dinnerUpsell = value;
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
     * Gets the value of the smsConfirmation property.
     * 
     */
    public boolean isSmsConfirmation() {
        return smsConfirmation;
    }

    /**
     * Sets the value of the smsConfirmation property.
     * 
     */
    public void setSmsConfirmation(boolean value) {
        this.smsConfirmation = value;
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

    /**
     * Gets the value of the confirmationNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getConfirmationNumber() {
        return confirmationNumber;
    }

    /**
     * Sets the value of the confirmationNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setConfirmationNumber(String value) {
        this.confirmationNumber = value;
    }

    /**
     * Gets the value of the cbtDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CBTDetails }
     *     
     */
    public CBTDetails getCbtDetails() {
        return cbtDetails;
    }

    /**
     * Sets the value of the cbtDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CBTDetails }
     *     
     */
    public void setCbtDetails(CBTDetails value) {
        this.cbtDetails = value;
    }

}
