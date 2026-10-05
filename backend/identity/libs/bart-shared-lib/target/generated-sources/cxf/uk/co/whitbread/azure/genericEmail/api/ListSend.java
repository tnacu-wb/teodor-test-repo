
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ListSend complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ListSend"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SendID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="List" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}List" minOccurs="0"/&gt;
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
 *         &lt;element name="PreviewURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Links" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Link" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="Events" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TrackingEvent" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListSend", propOrder = {
    "sendID",
    "list",
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
    "previewURL",
    "links",
    "events"
})
public class ListSend
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SendID")
    protected Integer sendID;
    @XmlElement(name = "List")
    protected uk.co.whitbread.azure.genericEmail.api.List list;
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
    @XmlElement(name = "PreviewURL")
    protected String previewURL;
    @XmlElement(name = "Links")
    protected java.util.List<Link> links;
    @XmlElement(name = "Events")
    protected java.util.List<TrackingEvent> events;

    /**
     * Gets the value of the sendID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSendID() {
        return sendID;
    }

    /**
     * Sets the value of the sendID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSendID(Integer value) {
        this.sendID = value;
    }

    /**
     * Gets the value of the list property.
     * 
     * @return
     *     possible object is
     *     {@link uk.co.whitbread.azure.genericEmail.api.List }
     *     
     */
    public uk.co.whitbread.azure.genericEmail.api.List getList() {
        return list;
    }

    /**
     * Sets the value of the list property.
     * 
     * @param value
     *     allowed object is
     *     {@link uk.co.whitbread.azure.genericEmail.api.List }
     *     
     */
    public void setList(uk.co.whitbread.azure.genericEmail.api.List value) {
        this.list = value;
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

}
