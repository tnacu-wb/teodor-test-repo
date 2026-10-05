
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for APIObject complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="APIObject">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="Client" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ClientID" minOccurs="0"/>
 *         <element name="PartnerKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="PartnerProperties" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIProperty" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="CreatedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="ModifiedDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="ID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="ObjectID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="CustomerKey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Owner" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Owner" minOccurs="0"/>
 *         <element name="CorrelationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ObjectState" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "APIObject", propOrder = {
    "client",
    "partnerKey",
    "partnerProperties",
    "createdDate",
    "modifiedDate",
    "id",
    "objectID",
    "customerKey",
    "owner",
    "correlationID",
    "objectState"
})
@XmlSeeAlso({
    ContentValidation.class,
    ScheduleDefinition.class,
    Subscriber.class,
    SubscriberList.class,
    uk.co.whitbread.shared.azureemail.genericEmail.api.List.class,
    Group.class,
    ListAttribute.class,
    ListAttributeRestrictedValue.class,
    Send.class,
    TriggeredSend.class,
    SenderProfile.class,
    DeliveryProfile.class,
    PrivateDomain.class,
    PrivateDomainSet.class,
    PrivateIP.class,
    AudienceItem.class,
    DataFolder.class,
    ResultMessage.class,
    ResultItem.class,
    ExtractTemplate.class,
    Locale.class,
    TimeZone.class,
    Account.class,
    LandingPage.class,
    AccountPrivateLabel.class,
    BusinessRule.class,
    AccountUser.class,
    SsoIdentity.class,
    UserAccess.class,
    Brand.class,
    BrandTag.class,
    Role.class,
    PermissionSet.class,
    Permission.class,
    Email.class,
    ContentArea.class,
    Message.class,
    TrackingEvent.class,
    ListSubscriber.class,
    GlobalUnsubscribeCategory.class,
    Link.class,
    SendSummary.class,
    SubscriberSendResult.class,
    TriggeredSendSummary.class,
    AsyncRequestResult.class,
    VoiceTriggeredSend.class,
    SMSTriggeredSend.class,
    SendClassification.class,
    TrackingUser.class,
    MessagingVendorKind.class,
    SMSMTEvent.class,
    SMSMOEvent.class,
    BaseMOKeyword.class,
    SMSSharedKeyword.class,
    FileTransferLocation.class,
    DataExtension.class,
    PropertyDefinition.class,
    DataExtensionTemplate.class,
    ImportDefinitionFieldMap.class,
    ImportResultsSummary.class,
    FilterDefinition.class,
    ListSend.class,
    LinkSend.class,
    ObjectExtension.class,
    PublicKeyManagement.class,
    SecurityObject.class,
    Authentication.class,
    ResourceSpecification.class,
    Portfolio.class,
    Template.class,
    Layout.class,
    IntegrationProfile.class,
    IntegrationProfileDefinition.class,
    ReplyMailManagementConfiguration.class,
    FileTrigger.class,
    FileTriggerTypeLastPull.class,
    ProgramManifestTemplate.class,
    Publication.class,
    PublicationSubscriber.class,
    AutomationInstances.class,
    AutomationNotification.class,
    AutomationTask.class,
    AutomationActivity.class,
    InteractionBaseObject.class,
    PlatformApplication.class,
    PlatformApplicationPackage.class,
    SuppressionListDefinition.class,
    SuppressionListContext.class,
    SuppressionListData.class,
    SendAdditionalAttribute.class,
    Asset.class,
    Category.class,
    ImportFileDestination.class,
    ContactEvent.class,
    AttributeSet.class
})
public class APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Client")
    protected ClientID client;
    @XmlElement(name = "PartnerKey", nillable = true)
    protected String partnerKey;
    @XmlElement(name = "PartnerProperties")
    protected java.util.List<APIProperty> partnerProperties;
    @XmlElement(name = "CreatedDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime createdDate;
    @XmlElement(name = "ModifiedDate", type = String.class, nillable = true)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime modifiedDate;
    @XmlElement(name = "ID")
    protected Integer id;
    @XmlElement(name = "ObjectID", nillable = true)
    protected String objectID;
    @XmlElement(name = "CustomerKey")
    protected String customerKey;
    @XmlElement(name = "Owner")
    protected Owner owner;
    @XmlElement(name = "CorrelationID")
    protected String correlationID;
    @XmlElement(name = "ObjectState")
    protected String objectState;

    /**
     * Gets the value of the client property.
     * 
     * @return
     *     possible object is
     *     {@link ClientID }
     *     
     */
    public ClientID getClient() {
        return client;
    }

    /**
     * Sets the value of the client property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientID }
     *     
     */
    public void setClient(ClientID value) {
        this.client = value;
    }

    /**
     * Gets the value of the partnerKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartnerKey() {
        return partnerKey;
    }

    /**
     * Sets the value of the partnerKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartnerKey(String value) {
        this.partnerKey = value;
    }

    /**
     * Gets the value of the partnerProperties property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the partnerProperties property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPartnerProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link APIProperty }
     * </p>
     * 
     * 
     * @return
     *     The value of the partnerProperties property.
     */
    public java.util.List<APIProperty> getPartnerProperties() {
        if (partnerProperties == null) {
            partnerProperties = new ArrayList<>();
        }
        return this.partnerProperties;
    }

    /**
     * Gets the value of the createdDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the value of the createdDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreatedDate(LocalDateTime value) {
        this.createdDate = value;
    }

    /**
     * Gets the value of the modifiedDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    /**
     * Sets the value of the modifiedDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModifiedDate(LocalDateTime value) {
        this.modifiedDate = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setID(Integer value) {
        this.id = value;
    }

    /**
     * Gets the value of the objectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * Sets the value of the objectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectID(String value) {
        this.objectID = value;
    }

    /**
     * Gets the value of the customerKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerKey() {
        return customerKey;
    }

    /**
     * Sets the value of the customerKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerKey(String value) {
        this.customerKey = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link Owner }
     *     
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link Owner }
     *     
     */
    public void setOwner(Owner value) {
        this.owner = value;
    }

    /**
     * Gets the value of the correlationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCorrelationID() {
        return correlationID;
    }

    /**
     * Sets the value of the correlationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCorrelationID(String value) {
        this.correlationID = value;
    }

    /**
     * Gets the value of the objectState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectState() {
        return objectState;
    }

    /**
     * Sets the value of the objectState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectState(String value) {
        this.objectState = value;
    }

}
