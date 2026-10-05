
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SendClassification complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SendClassification"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SendClassificationType" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendClassificationTypeEnum" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SenderProfile" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SenderProfile" minOccurs="0"/&gt;
 *         &lt;element name="DeliveryProfile" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DeliveryProfile" minOccurs="0"/&gt;
 *         &lt;element name="HonorPublicationListOptOutsForTransactionalSends" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SendPriority" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendPriorityEnum" minOccurs="0"/&gt;
 *         &lt;element name="ArchiveEmail" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SendClassification", propOrder = {
    "sendClassificationType",
    "name",
    "description",
    "senderProfile",
    "deliveryProfile",
    "honorPublicationListOptOutsForTransactionalSends",
    "sendPriority",
    "archiveEmail"
})
public class SendClassification
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SendClassificationType")
    @XmlSchemaType(name = "string")
    protected SendClassificationTypeEnum sendClassificationType;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "SenderProfile")
    protected SenderProfile senderProfile;
    @XmlElement(name = "DeliveryProfile")
    protected DeliveryProfile deliveryProfile;
    @XmlElement(name = "HonorPublicationListOptOutsForTransactionalSends")
    protected Boolean honorPublicationListOptOutsForTransactionalSends;
    @XmlElement(name = "SendPriority")
    @XmlSchemaType(name = "string")
    protected SendPriorityEnum sendPriority;
    @XmlElement(name = "ArchiveEmail")
    protected Boolean archiveEmail;

    /**
     * Gets the value of the sendClassificationType property.
     * 
     * @return
     *     possible object is
     *     {@link SendClassificationTypeEnum }
     *     
     */
    public SendClassificationTypeEnum getSendClassificationType() {
        return sendClassificationType;
    }

    /**
     * Sets the value of the sendClassificationType property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendClassificationTypeEnum }
     *     
     */
    public void setSendClassificationType(SendClassificationTypeEnum value) {
        this.sendClassificationType = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the senderProfile property.
     * 
     * @return
     *     possible object is
     *     {@link SenderProfile }
     *     
     */
    public SenderProfile getSenderProfile() {
        return senderProfile;
    }

    /**
     * Sets the value of the senderProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link SenderProfile }
     *     
     */
    public void setSenderProfile(SenderProfile value) {
        this.senderProfile = value;
    }

    /**
     * Gets the value of the deliveryProfile property.
     * 
     * @return
     *     possible object is
     *     {@link DeliveryProfile }
     *     
     */
    public DeliveryProfile getDeliveryProfile() {
        return deliveryProfile;
    }

    /**
     * Sets the value of the deliveryProfile property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeliveryProfile }
     *     
     */
    public void setDeliveryProfile(DeliveryProfile value) {
        this.deliveryProfile = value;
    }

    /**
     * Gets the value of the honorPublicationListOptOutsForTransactionalSends property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isHonorPublicationListOptOutsForTransactionalSends() {
        return honorPublicationListOptOutsForTransactionalSends;
    }

    /**
     * Sets the value of the honorPublicationListOptOutsForTransactionalSends property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setHonorPublicationListOptOutsForTransactionalSends(Boolean value) {
        this.honorPublicationListOptOutsForTransactionalSends = value;
    }

    /**
     * Gets the value of the sendPriority property.
     * 
     * @return
     *     possible object is
     *     {@link SendPriorityEnum }
     *     
     */
    public SendPriorityEnum getSendPriority() {
        return sendPriority;
    }

    /**
     * Sets the value of the sendPriority property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendPriorityEnum }
     *     
     */
    public void setSendPriority(SendPriorityEnum value) {
        this.sendPriority = value;
    }

    /**
     * Gets the value of the archiveEmail property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isArchiveEmail() {
        return archiveEmail;
    }

    /**
     * Sets the value of the archiveEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setArchiveEmail(Boolean value) {
        this.archiveEmail = value;
    }

}
