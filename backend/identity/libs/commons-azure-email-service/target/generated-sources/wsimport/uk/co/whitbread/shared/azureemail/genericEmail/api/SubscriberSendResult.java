
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for SubscriberSendResult complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="SubscriberSendResult">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject">
 *       <sequence>
 *         <element name="Send" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Send" minOccurs="0"/>
 *         <element name="Email" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Email" minOccurs="0"/>
 *         <element name="Subscriber" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Subscriber" minOccurs="0"/>
 *         <element name="ClickDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="BounceDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="OpenDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="SentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="LastAction" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="UnsubscribeDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="FromAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="FromName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="TotalClicks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="UniqueClicks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ViewSentEmailURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="HardBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="SoftBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="OtherBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubscriberSendResult", propOrder = {
    "send",
    "email",
    "subscriber",
    "clickDate",
    "bounceDate",
    "openDate",
    "sentDate",
    "lastAction",
    "unsubscribeDate",
    "fromAddress",
    "fromName",
    "totalClicks",
    "uniqueClicks",
    "subject",
    "viewSentEmailURL",
    "hardBounces",
    "softBounces",
    "otherBounces"
})
public class SubscriberSendResult
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Send")
    protected Send send;
    @XmlElement(name = "Email")
    protected Email email;
    @XmlElement(name = "Subscriber")
    protected Subscriber subscriber;
    @XmlElement(name = "ClickDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime clickDate;
    @XmlElement(name = "BounceDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime bounceDate;
    @XmlElement(name = "OpenDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime openDate;
    @XmlElement(name = "SentDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime sentDate;
    @XmlElement(name = "LastAction")
    protected String lastAction;
    @XmlElement(name = "UnsubscribeDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime unsubscribeDate;
    @XmlElement(name = "FromAddress")
    protected String fromAddress;
    @XmlElement(name = "FromName")
    protected String fromName;
    @XmlElement(name = "TotalClicks")
    protected Integer totalClicks;
    @XmlElement(name = "UniqueClicks")
    protected Integer uniqueClicks;
    @XmlElement(name = "Subject")
    protected String subject;
    @XmlElement(name = "ViewSentEmailURL")
    protected String viewSentEmailURL;
    @XmlElement(name = "HardBounces")
    protected Integer hardBounces;
    @XmlElement(name = "SoftBounces")
    protected Integer softBounces;
    @XmlElement(name = "OtherBounces")
    protected Integer otherBounces;

    /**
     * Gets the value of the send property.
     * 
     * @return
     *     possible object is
     *     {@link Send }
     *     
     */
    public Send getSend() {
        return send;
    }

    /**
     * Sets the value of the send property.
     * 
     * @param value
     *     allowed object is
     *     {@link Send }
     *     
     */
    public void setSend(Send value) {
        this.send = value;
    }

    /**
     * Gets the value of the email property.
     * 
     * @return
     *     possible object is
     *     {@link Email }
     *     
     */
    public Email getEmail() {
        return email;
    }

    /**
     * Sets the value of the email property.
     * 
     * @param value
     *     allowed object is
     *     {@link Email }
     *     
     */
    public void setEmail(Email value) {
        this.email = value;
    }

    /**
     * Gets the value of the subscriber property.
     * 
     * @return
     *     possible object is
     *     {@link Subscriber }
     *     
     */
    public Subscriber getSubscriber() {
        return subscriber;
    }

    /**
     * Sets the value of the subscriber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Subscriber }
     *     
     */
    public void setSubscriber(Subscriber value) {
        this.subscriber = value;
    }

    /**
     * Gets the value of the clickDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getClickDate() {
        return clickDate;
    }

    /**
     * Sets the value of the clickDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClickDate(LocalDateTime value) {
        this.clickDate = value;
    }

    /**
     * Gets the value of the bounceDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getBounceDate() {
        return bounceDate;
    }

    /**
     * Sets the value of the bounceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBounceDate(LocalDateTime value) {
        this.bounceDate = value;
    }

    /**
     * Gets the value of the openDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getOpenDate() {
        return openDate;
    }

    /**
     * Sets the value of the openDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOpenDate(LocalDateTime value) {
        this.openDate = value;
    }

    /**
     * Gets the value of the sentDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getSentDate() {
        return sentDate;
    }

    /**
     * Sets the value of the sentDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSentDate(LocalDateTime value) {
        this.sentDate = value;
    }

    /**
     * Gets the value of the lastAction property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastAction() {
        return lastAction;
    }

    /**
     * Sets the value of the lastAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastAction(String value) {
        this.lastAction = value;
    }

    /**
     * Gets the value of the unsubscribeDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getUnsubscribeDate() {
        return unsubscribeDate;
    }

    /**
     * Sets the value of the unsubscribeDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnsubscribeDate(LocalDateTime value) {
        this.unsubscribeDate = value;
    }

    /**
     * Gets the value of the fromAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromAddress() {
        return fromAddress;
    }

    /**
     * Sets the value of the fromAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromAddress(String value) {
        this.fromAddress = value;
    }

    /**
     * Gets the value of the fromName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFromName() {
        return fromName;
    }

    /**
     * Sets the value of the fromName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFromName(String value) {
        this.fromName = value;
    }

    /**
     * Gets the value of the totalClicks property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getTotalClicks() {
        return totalClicks;
    }

    /**
     * Sets the value of the totalClicks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setTotalClicks(Integer value) {
        this.totalClicks = value;
    }

    /**
     * Gets the value of the uniqueClicks property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUniqueClicks() {
        return uniqueClicks;
    }

    /**
     * Sets the value of the uniqueClicks property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUniqueClicks(Integer value) {
        this.uniqueClicks = value;
    }

    /**
     * Gets the value of the subject property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the value of the subject property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubject(String value) {
        this.subject = value;
    }

    /**
     * Gets the value of the viewSentEmailURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViewSentEmailURL() {
        return viewSentEmailURL;
    }

    /**
     * Sets the value of the viewSentEmailURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViewSentEmailURL(String value) {
        this.viewSentEmailURL = value;
    }

    /**
     * Gets the value of the hardBounces property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getHardBounces() {
        return hardBounces;
    }

    /**
     * Sets the value of the hardBounces property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setHardBounces(Integer value) {
        this.hardBounces = value;
    }

    /**
     * Gets the value of the softBounces property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSoftBounces() {
        return softBounces;
    }

    /**
     * Sets the value of the softBounces property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSoftBounces(Integer value) {
        this.softBounces = value;
    }

    /**
     * Gets the value of the otherBounces property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOtherBounces() {
        return otherBounces;
    }

    /**
     * Sets the value of the otherBounces property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setOtherBounces(Integer value) {
        this.otherBounces = value;
    }

}
