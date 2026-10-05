
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PaymentRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PaymentRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmationNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="paymentCard" type="{http://bartws.micros.com/1.0}PaymentCard" minOccurs="0"/&gt;
 *         &lt;element name="donation" type="{http://bartws.micros.com/1.0}Donation" minOccurs="0"/&gt;
 *         &lt;element name="paymentConfirmationEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="ezineSubscribtion" type="{http://bartws.micros.com/1.0}SubscriptionRequest" minOccurs="0"/&gt;
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
@XmlType(name = "PaymentRequest", propOrder = {
    "sessionID",
    "confirmationNumber",
    "paymentCard",
    "donation",
    "paymentConfirmationEmail",
    "ezineSubscribtion",
    "browserAccept",
    "browserUserAgent",
    "ipAddress"
})
public class PaymentRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String confirmationNumber;
    protected PaymentCard paymentCard;
    protected Donation donation;
    protected String paymentConfirmationEmail;
    protected SubscriptionRequest ezineSubscribtion;
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
     * Gets the value of the paymentCard property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentCard }
     *     
     */
    public PaymentCard getPaymentCard() {
        return paymentCard;
    }

    /**
     * Sets the value of the paymentCard property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentCard }
     *     
     */
    public void setPaymentCard(PaymentCard value) {
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
     * Gets the value of the paymentConfirmationEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentConfirmationEmail() {
        return paymentConfirmationEmail;
    }

    /**
     * Sets the value of the paymentConfirmationEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentConfirmationEmail(String value) {
        this.paymentConfirmationEmail = value;
    }

    /**
     * Gets the value of the ezineSubscribtion property.
     * 
     * @return
     *     possible object is
     *     {@link SubscriptionRequest }
     *     
     */
    public SubscriptionRequest getEzineSubscribtion() {
        return ezineSubscribtion;
    }

    /**
     * Sets the value of the ezineSubscribtion property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubscriptionRequest }
     *     
     */
    public void setEzineSubscribtion(SubscriptionRequest value) {
        this.ezineSubscribtion = value;
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
