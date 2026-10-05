
package uk.co.whitbread.bart.checkin.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Readback complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Readback"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bookingDetails" type="{http://bartws.micros.com/1.0}BookingDetails" minOccurs="0"/&gt;
 *         &lt;element name="specialEvents" type="{http://bartws.micros.com/1.0}ArrayOfEventEvent" minOccurs="0"/&gt;
 *         &lt;element name="transaction" type="{http://bartws.micros.com/1.0}Transaction" minOccurs="0"/&gt;
 *         &lt;element name="paymentEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="confirmationEmails" type="{http://bartws.micros.com/1.0}ArrayOfconfirmationEmailsItemString" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Readback", propOrder = {
    "bookingDetails",
    "specialEvents",
    "transaction",
    "paymentEmail",
    "confirmationEmails"
})
public class Readback
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected BookingDetails bookingDetails;
    protected ArrayOfEventEvent specialEvents;
    protected Transaction transaction;
    protected String paymentEmail;
    protected ArrayOfconfirmationEmailsItemString confirmationEmails;

    /**
     * Gets the value of the bookingDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BookingDetails }
     *     
     */
    public BookingDetails getBookingDetails() {
        return bookingDetails;
    }

    /**
     * Sets the value of the bookingDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BookingDetails }
     *     
     */
    public void setBookingDetails(BookingDetails value) {
        this.bookingDetails = value;
    }

    /**
     * Gets the value of the specialEvents property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfEventEvent }
     *     
     */
    public ArrayOfEventEvent getSpecialEvents() {
        return specialEvents;
    }

    /**
     * Sets the value of the specialEvents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfEventEvent }
     *     
     */
    public void setSpecialEvents(ArrayOfEventEvent value) {
        this.specialEvents = value;
    }

    /**
     * Gets the value of the transaction property.
     * 
     * @return
     *     possible object is
     *     {@link Transaction }
     *     
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Sets the value of the transaction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transaction }
     *     
     */
    public void setTransaction(Transaction value) {
        this.transaction = value;
    }

    /**
     * Gets the value of the paymentEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentEmail() {
        return paymentEmail;
    }

    /**
     * Sets the value of the paymentEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentEmail(String value) {
        this.paymentEmail = value;
    }

    /**
     * Gets the value of the confirmationEmails property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfconfirmationEmailsItemString }
     *     
     */
    public ArrayOfconfirmationEmailsItemString getConfirmationEmails() {
        return confirmationEmails;
    }

    /**
     * Sets the value of the confirmationEmails property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfconfirmationEmailsItemString }
     *     
     */
    public void setConfirmationEmails(ArrayOfconfirmationEmailsItemString value) {
        this.confirmationEmails = value;
    }

}
