
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BusinessUnit complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="BusinessUnit">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Account">
 *       <sequence>
 *         <element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="DefaultSendClassification" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendClassification" minOccurs="0"/>
 *         <element name="DefaultHomePage" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}LandingPage" minOccurs="0"/>
 *         <element name="SubscriberFilter" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}FilterPart" minOccurs="0"/>
 *         <element name="MasterUnsubscribeBehavior" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UnsubscribeBehaviorEnum" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BusinessUnit", propOrder = {
    "description",
    "defaultSendClassification",
    "defaultHomePage",
    "subscriberFilter",
    "masterUnsubscribeBehavior"
})
public class BusinessUnit
    extends Account
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "DefaultSendClassification")
    protected SendClassification defaultSendClassification;
    @XmlElement(name = "DefaultHomePage")
    protected LandingPage defaultHomePage;
    @XmlElement(name = "SubscriberFilter")
    protected FilterPart subscriberFilter;
    @XmlElement(name = "MasterUnsubscribeBehavior")
    @XmlSchemaType(name = "string")
    protected UnsubscribeBehaviorEnum masterUnsubscribeBehavior;

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
     * Gets the value of the defaultSendClassification property.
     * 
     * @return
     *     possible object is
     *     {@link SendClassification }
     *     
     */
    public SendClassification getDefaultSendClassification() {
        return defaultSendClassification;
    }

    /**
     * Sets the value of the defaultSendClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendClassification }
     *     
     */
    public void setDefaultSendClassification(SendClassification value) {
        this.defaultSendClassification = value;
    }

    /**
     * Gets the value of the defaultHomePage property.
     * 
     * @return
     *     possible object is
     *     {@link LandingPage }
     *     
     */
    public LandingPage getDefaultHomePage() {
        return defaultHomePage;
    }

    /**
     * Sets the value of the defaultHomePage property.
     * 
     * @param value
     *     allowed object is
     *     {@link LandingPage }
     *     
     */
    public void setDefaultHomePage(LandingPage value) {
        this.defaultHomePage = value;
    }

    /**
     * Gets the value of the subscriberFilter property.
     * 
     * @return
     *     possible object is
     *     {@link FilterPart }
     *     
     */
    public FilterPart getSubscriberFilter() {
        return subscriberFilter;
    }

    /**
     * Sets the value of the subscriberFilter property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterPart }
     *     
     */
    public void setSubscriberFilter(FilterPart value) {
        this.subscriberFilter = value;
    }

    /**
     * Gets the value of the masterUnsubscribeBehavior property.
     * 
     * @return
     *     possible object is
     *     {@link UnsubscribeBehaviorEnum }
     *     
     */
    public UnsubscribeBehaviorEnum getMasterUnsubscribeBehavior() {
        return masterUnsubscribeBehavior;
    }

    /**
     * Sets the value of the masterUnsubscribeBehavior property.
     * 
     * @param value
     *     allowed object is
     *     {@link UnsubscribeBehaviorEnum }
     *     
     */
    public void setMasterUnsubscribeBehavior(UnsubscribeBehaviorEnum value) {
        this.masterUnsubscribeBehavior = value;
    }

}
