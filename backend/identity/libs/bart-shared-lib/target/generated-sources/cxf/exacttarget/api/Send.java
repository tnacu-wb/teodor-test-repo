
package exacttarget.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * &lt;p&gt;Java class for Send complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Send"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Email" type="{http://exacttarget.com/wsdl/partnerAPI}Email" minOccurs="0"/&gt;
 *         &lt;element name="List" type="{http://exacttarget.com/wsdl/partnerAPI}List" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SendDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="FromAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FromName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Duplicates" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="InvalidAddresses" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ExistingUndeliverables" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ExistingUnsubscribes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="HardBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SoftBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OtherBounces" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="ForwardedEmails" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="UniqueClicks" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="UniqueOpens" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberSent" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberDelivered" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Unsubscribes" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="MissingAddresses" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Subject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="PreviewURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Links" type="{http://exacttarget.com/wsdl/partnerAPI}Link" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Events" type="{http://exacttarget.com/wsdl/partnerAPI}TrackingEvent" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="SentDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/&gt;
 *         &lt;element name="EmailName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsMultipart" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SendLimit" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SendWindowOpen" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="SendWindowClose" type="{http://www.w3.org/2001/XMLSchema}time" minOccurs="0"/&gt;
 *         &lt;element name="IsAlwaysOn" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Sources" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Source" type="{http://exacttarget.com/wsdl/partnerAPI}APIObject" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumberTargeted" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberErrored" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="NumberExcluded" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="Additional" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="BccEmail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="EmailSendDefinition" type="{http://exacttarget.com/wsdl/partnerAPI}EmailSendDefinition" minOccurs="0"/&gt;
 *         &lt;element name="SuppressionLists" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="SuppressionList" type="{http://exacttarget.com/wsdl/partnerAPI}AudienceItem" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Send", propOrder = {
    "email",
    "list",
    "sendDate",
    "fromAddress",
    "fromName",
    "duplicates",
    "invalidAddresses",
    "existingUndeliverables",
    "existingUnsubscribes",
    "hardBounces",
    "softBounces",
    "otherBounces",
    "forwardedEmails",
    "uniqueClicks",
    "uniqueOpens",
    "numberSent",
    "numberDelivered",
    "unsubscribes",
    "missingAddresses",
    "subject",
    "previewURL",
    "links",
    "events",
    "sentDate",
    "emailName",
    "status",
    "isMultipart",
    "sendLimit",
    "sendWindowOpen",
    "sendWindowClose",
    "isAlwaysOn",
    "sources",
    "numberTargeted",
    "numberErrored",
    "numberExcluded",
    "additional",
    "bccEmail",
    "emailSendDefinition",
    "suppressionLists"
})
public class Send
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Email")
    protected Email email;
    @XmlElement(name = "List")
    protected java.util.List<exacttarget.api.List> list;
    @XmlElement(name = "SendDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime sendDate;
    @XmlElement(name = "FromAddress")
    protected String fromAddress;
    @XmlElement(name = "FromName")
    protected String fromName;
    @XmlElement(name = "Duplicates")
    protected Integer duplicates;
    @XmlElement(name = "InvalidAddresses")
    protected Integer invalidAddresses;
    @XmlElement(name = "ExistingUndeliverables")
    protected Integer existingUndeliverables;
    @XmlElement(name = "ExistingUnsubscribes")
    protected Integer existingUnsubscribes;
    @XmlElement(name = "HardBounces")
    protected Integer hardBounces;
    @XmlElement(name = "SoftBounces")
    protected Integer softBounces;
    @XmlElement(name = "OtherBounces")
    protected Integer otherBounces;
    @XmlElement(name = "ForwardedEmails")
    protected Integer forwardedEmails;
    @XmlElement(name = "UniqueClicks")
    protected Integer uniqueClicks;
    @XmlElement(name = "UniqueOpens")
    protected Integer uniqueOpens;
    @XmlElement(name = "NumberSent", nillable = true)
    protected Integer numberSent;
    @XmlElement(name = "NumberDelivered", nillable = true)
    protected Integer numberDelivered;
    @XmlElement(name = "Unsubscribes")
    protected Integer unsubscribes;
    @XmlElement(name = "MissingAddresses")
    protected Integer missingAddresses;
    @XmlElement(name = "Subject")
    protected String subject;
    @XmlElement(name = "PreviewURL")
    protected String previewURL;
    @XmlElement(name = "Links")
    protected java.util.List<Link> links;
    @XmlElement(name = "Events")
    protected java.util.List<TrackingEvent> events;
    @XmlElement(name = "SentDate", type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime sentDate;
    @XmlElement(name = "EmailName")
    protected String emailName;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "IsMultipart")
    protected Boolean isMultipart;
    @XmlElement(name = "SendLimit")
    protected Integer sendLimit;
    @XmlElement(name = "SendWindowOpen", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime sendWindowOpen;
    @XmlElement(name = "SendWindowClose", type = String.class)
    @XmlJavaTypeAdapter(Adapter2 .class)
    @XmlSchemaType(name = "time")
    protected LocalTime sendWindowClose;
    @XmlElement(name = "IsAlwaysOn")
    protected Boolean isAlwaysOn;
    @XmlElement(name = "Sources")
    protected Send.Sources sources;
    @XmlElement(name = "NumberTargeted")
    protected Integer numberTargeted;
    @XmlElement(name = "NumberErrored")
    protected Integer numberErrored;
    @XmlElement(name = "NumberExcluded")
    protected Integer numberExcluded;
    @XmlElement(name = "Additional")
    protected String additional;
    @XmlElement(name = "BccEmail")
    protected String bccEmail;
    @XmlElement(name = "EmailSendDefinition")
    protected EmailSendDefinition emailSendDefinition;
    @XmlElement(name = "SuppressionLists")
    protected Send.SuppressionLists suppressionLists;

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
     * Gets the value of the list property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the list property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link exacttarget.api.List }
     * </p>
     * 
     * 
     * @return
     *     The value of the list property.
     */
    public java.util.List<exacttarget.api.List> getList() {
        if (list == null) {
            list = new ArrayList<>();
        }
        return this.list;
    }

    /**
     * Gets the value of the sendDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getSendDate() {
        return sendDate;
    }

    /**
     * Sets the value of the sendDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendDate(LocalDateTime value) {
        this.sendDate = value;
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
     * Gets the value of the duplicates property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDuplicates() {
        return duplicates;
    }

    /**
     * Sets the value of the duplicates property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDuplicates(Integer value) {
        this.duplicates = value;
    }

    /**
     * Gets the value of the invalidAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getInvalidAddresses() {
        return invalidAddresses;
    }

    /**
     * Sets the value of the invalidAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setInvalidAddresses(Integer value) {
        this.invalidAddresses = value;
    }

    /**
     * Gets the value of the existingUndeliverables property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExistingUndeliverables() {
        return existingUndeliverables;
    }

    /**
     * Sets the value of the existingUndeliverables property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExistingUndeliverables(Integer value) {
        this.existingUndeliverables = value;
    }

    /**
     * Gets the value of the existingUnsubscribes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getExistingUnsubscribes() {
        return existingUnsubscribes;
    }

    /**
     * Sets the value of the existingUnsubscribes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setExistingUnsubscribes(Integer value) {
        this.existingUnsubscribes = value;
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

    /**
     * Gets the value of the forwardedEmails property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getForwardedEmails() {
        return forwardedEmails;
    }

    /**
     * Sets the value of the forwardedEmails property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setForwardedEmails(Integer value) {
        this.forwardedEmails = value;
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
     * Gets the value of the uniqueOpens property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUniqueOpens() {
        return uniqueOpens;
    }

    /**
     * Sets the value of the uniqueOpens property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUniqueOpens(Integer value) {
        this.uniqueOpens = value;
    }

    /**
     * Gets the value of the numberSent property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberSent() {
        return numberSent;
    }

    /**
     * Sets the value of the numberSent property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberSent(Integer value) {
        this.numberSent = value;
    }

    /**
     * Gets the value of the numberDelivered property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberDelivered() {
        return numberDelivered;
    }

    /**
     * Sets the value of the numberDelivered property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberDelivered(Integer value) {
        this.numberDelivered = value;
    }

    /**
     * Gets the value of the unsubscribes property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getUnsubscribes() {
        return unsubscribes;
    }

    /**
     * Sets the value of the unsubscribes property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setUnsubscribes(Integer value) {
        this.unsubscribes = value;
    }

    /**
     * Gets the value of the missingAddresses property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMissingAddresses() {
        return missingAddresses;
    }

    /**
     * Sets the value of the missingAddresses property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMissingAddresses(Integer value) {
        this.missingAddresses = value;
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
     * Gets the value of the previewURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreviewURL() {
        return previewURL;
    }

    /**
     * Sets the value of the previewURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreviewURL(String value) {
        this.previewURL = value;
    }

    /**
     * Gets the value of the links property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the links property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getLinks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Link }
     * </p>
     * 
     * 
     * @return
     *     The value of the links property.
     */
    public java.util.List<Link> getLinks() {
        if (links == null) {
            links = new ArrayList<>();
        }
        return this.links;
    }

    /**
     * Gets the value of the events property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the events property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getEvents().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TrackingEvent }
     * </p>
     * 
     * 
     * @return
     *     The value of the events property.
     */
    public java.util.List<TrackingEvent> getEvents() {
        if (events == null) {
            events = new ArrayList<>();
        }
        return this.events;
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
     * Gets the value of the emailName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailName() {
        return emailName;
    }

    /**
     * Sets the value of the emailName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailName(String value) {
        this.emailName = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Gets the value of the isMultipart property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsMultipart() {
        return isMultipart;
    }

    /**
     * Sets the value of the isMultipart property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsMultipart(Boolean value) {
        this.isMultipart = value;
    }

    /**
     * Gets the value of the sendLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSendLimit() {
        return sendLimit;
    }

    /**
     * Sets the value of the sendLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSendLimit(Integer value) {
        this.sendLimit = value;
    }

    /**
     * Gets the value of the sendWindowOpen property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getSendWindowOpen() {
        return sendWindowOpen;
    }

    /**
     * Sets the value of the sendWindowOpen property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendWindowOpen(LocalTime value) {
        this.sendWindowOpen = value;
    }

    /**
     * Gets the value of the sendWindowClose property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalTime getSendWindowClose() {
        return sendWindowClose;
    }

    /**
     * Sets the value of the sendWindowClose property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSendWindowClose(LocalTime value) {
        this.sendWindowClose = value;
    }

    /**
     * Gets the value of the isAlwaysOn property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAlwaysOn() {
        return isAlwaysOn;
    }

    /**
     * Sets the value of the isAlwaysOn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAlwaysOn(Boolean value) {
        this.isAlwaysOn = value;
    }

    /**
     * Gets the value of the sources property.
     * 
     * @return
     *     possible object is
     *     {@link Send.Sources }
     *     
     */
    public Send.Sources getSources() {
        return sources;
    }

    /**
     * Sets the value of the sources property.
     * 
     * @param value
     *     allowed object is
     *     {@link Send.Sources }
     *     
     */
    public void setSources(Send.Sources value) {
        this.sources = value;
    }

    /**
     * Gets the value of the numberTargeted property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberTargeted() {
        return numberTargeted;
    }

    /**
     * Sets the value of the numberTargeted property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberTargeted(Integer value) {
        this.numberTargeted = value;
    }

    /**
     * Gets the value of the numberErrored property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberErrored() {
        return numberErrored;
    }

    /**
     * Sets the value of the numberErrored property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberErrored(Integer value) {
        this.numberErrored = value;
    }

    /**
     * Gets the value of the numberExcluded property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getNumberExcluded() {
        return numberExcluded;
    }

    /**
     * Sets the value of the numberExcluded property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setNumberExcluded(Integer value) {
        this.numberExcluded = value;
    }

    /**
     * Gets the value of the additional property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdditional() {
        return additional;
    }

    /**
     * Sets the value of the additional property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdditional(String value) {
        this.additional = value;
    }

    /**
     * Gets the value of the bccEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBccEmail() {
        return bccEmail;
    }

    /**
     * Sets the value of the bccEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBccEmail(String value) {
        this.bccEmail = value;
    }

    /**
     * Gets the value of the emailSendDefinition property.
     * 
     * @return
     *     possible object is
     *     {@link EmailSendDefinition }
     *     
     */
    public EmailSendDefinition getEmailSendDefinition() {
        return emailSendDefinition;
    }

    /**
     * Sets the value of the emailSendDefinition property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailSendDefinition }
     *     
     */
    public void setEmailSendDefinition(EmailSendDefinition value) {
        this.emailSendDefinition = value;
    }

    /**
     * Gets the value of the suppressionLists property.
     * 
     * @return
     *     possible object is
     *     {@link Send.SuppressionLists }
     *     
     */
    public Send.SuppressionLists getSuppressionLists() {
        return suppressionLists;
    }

    /**
     * Sets the value of the suppressionLists property.
     * 
     * @param value
     *     allowed object is
     *     {@link Send.SuppressionLists }
     *     
     */
    public void setSuppressionLists(Send.SuppressionLists value) {
        this.suppressionLists = value;
    }


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
     *         &lt;element name="Source" type="{http://exacttarget.com/wsdl/partnerAPI}APIObject" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "source"
    })
    public static class Sources
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Source")
        protected java.util.List<APIObject> source;

        /**
         * Gets the value of the source property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the source property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getSource().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link APIObject }
         * </p>
         * 
         * 
         * @return
         *     The value of the source property.
         */
        public java.util.List<APIObject> getSource() {
            if (source == null) {
                source = new ArrayList<>();
            }
            return this.source;
        }

    }


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
     *         &lt;element name="SuppressionList" type="{http://exacttarget.com/wsdl/partnerAPI}AudienceItem" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "suppressionList"
    })
    public static class SuppressionLists
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "SuppressionList")
        protected java.util.List<AudienceItem> suppressionList;

        /**
         * Gets the value of the suppressionList property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the suppressionList property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getSuppressionList().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AudienceItem }
         * </p>
         * 
         * 
         * @return
         *     The value of the suppressionList property.
         */
        public java.util.List<AudienceItem> getSuppressionList() {
            if (suppressionList == null) {
                suppressionList = new ArrayList<>();
            }
            return this.suppressionList;
        }

    }

}
