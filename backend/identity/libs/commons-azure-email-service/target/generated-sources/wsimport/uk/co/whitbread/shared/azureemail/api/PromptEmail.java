
package uk.co.whitbread.shared.azureemail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PromptEmail complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="PromptEmail">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="bookingDetails" type="{https://dto.email.transact.comms.int.wtbapi.com}BookingDetails" minOccurs="0"/>
 *         <element name="login" type="{https://dto.email.transact.comms.int.wtbapi.com}Login" minOccurs="0"/>
 *         <element name="messageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="recipientDetails" type="{https://dto.email.transact.comms.int.wtbapi.com}RecipientDetails" minOccurs="0"/>
 *         <element name="template" type="{https://dto.email.transact.comms.int.wtbapi.com}Template" minOccurs="0"/>
 *         <element name="totalReservation" type="{https://dto.email.transact.comms.int.wtbapi.com}TotalReservation" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PromptEmail", propOrder = {
    "bookingDetails",
    "login",
    "messageType",
    "recipientDetails",
    "template",
    "totalReservation"
})
public class PromptEmail
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(nillable = true)
    protected BookingDetails bookingDetails;
    @XmlElement(nillable = true)
    protected Login login;
    @XmlElement(nillable = true)
    protected String messageType;
    @XmlElement(nillable = true)
    protected RecipientDetails recipientDetails;
    @XmlElement(nillable = true)
    protected Template template;
    @XmlElement(nillable = true)
    protected TotalReservation totalReservation;

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
     * Gets the value of the login property.
     * 
     * @return
     *     possible object is
     *     {@link Login }
     *     
     */
    public Login getLogin() {
        return login;
    }

    /**
     * Sets the value of the login property.
     * 
     * @param value
     *     allowed object is
     *     {@link Login }
     *     
     */
    public void setLogin(Login value) {
        this.login = value;
    }

    /**
     * Gets the value of the messageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the value of the messageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessageType(String value) {
        this.messageType = value;
    }

    /**
     * Gets the value of the recipientDetails property.
     * 
     * @return
     *     possible object is
     *     {@link RecipientDetails }
     *     
     */
    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }

    /**
     * Sets the value of the recipientDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link RecipientDetails }
     *     
     */
    public void setRecipientDetails(RecipientDetails value) {
        this.recipientDetails = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link Template }
     *     
     */
    public Template getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link Template }
     *     
     */
    public void setTemplate(Template value) {
        this.template = value;
    }

    /**
     * Gets the value of the totalReservation property.
     * 
     * @return
     *     possible object is
     *     {@link TotalReservation }
     *     
     */
    public TotalReservation getTotalReservation() {
        return totalReservation;
    }

    /**
     * Sets the value of the totalReservation property.
     * 
     * @param value
     *     allowed object is
     *     {@link TotalReservation }
     *     
     */
    public void setTotalReservation(TotalReservation value) {
        this.totalReservation = value;
    }

}
