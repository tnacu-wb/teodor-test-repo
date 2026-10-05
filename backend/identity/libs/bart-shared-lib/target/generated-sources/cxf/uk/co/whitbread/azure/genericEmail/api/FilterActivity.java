
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for FilterActivity complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="FilterActivity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}InteractionDefinition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="FilterActivityID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="FilterDefinitionID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DestinationObjectID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="DestinationTypeID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="SourceObjectID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="SourceTypeID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="OwnerID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="StatusID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="CreatedBy" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ClientID" minOccurs="0"/&gt;
 *         &lt;element name="ModifiedBy" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ClientID" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilterActivity", propOrder = {
    "filterActivityID",
    "filterDefinitionID",
    "destinationObjectID",
    "destinationTypeID",
    "sourceObjectID",
    "sourceTypeID",
    "ownerID",
    "statusID",
    "createdBy",
    "modifiedBy"
})
public class FilterActivity
    extends InteractionDefinition
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "FilterActivityID")
    protected String filterActivityID;
    @XmlElement(name = "FilterDefinitionID")
    protected String filterDefinitionID;
    @XmlElement(name = "DestinationObjectID")
    protected String destinationObjectID;
    @XmlElement(name = "DestinationTypeID")
    protected Integer destinationTypeID;
    @XmlElement(name = "SourceObjectID")
    protected String sourceObjectID;
    @XmlElement(name = "SourceTypeID")
    protected Integer sourceTypeID;
    @XmlElement(name = "OwnerID")
    protected String ownerID;
    @XmlElement(name = "StatusID")
    protected String statusID;
    @XmlElement(name = "CreatedBy")
    protected ClientID createdBy;
    @XmlElement(name = "ModifiedBy")
    protected ClientID modifiedBy;

    /**
     * Gets the value of the filterActivityID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterActivityID() {
        return filterActivityID;
    }

    /**
     * Sets the value of the filterActivityID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterActivityID(String value) {
        this.filterActivityID = value;
    }

    /**
     * Gets the value of the filterDefinitionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFilterDefinitionID() {
        return filterDefinitionID;
    }

    /**
     * Sets the value of the filterDefinitionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFilterDefinitionID(String value) {
        this.filterDefinitionID = value;
    }

    /**
     * Gets the value of the destinationObjectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestinationObjectID() {
        return destinationObjectID;
    }

    /**
     * Sets the value of the destinationObjectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestinationObjectID(String value) {
        this.destinationObjectID = value;
    }

    /**
     * Gets the value of the destinationTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDestinationTypeID() {
        return destinationTypeID;
    }

    /**
     * Sets the value of the destinationTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDestinationTypeID(Integer value) {
        this.destinationTypeID = value;
    }

    /**
     * Gets the value of the sourceObjectID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceObjectID() {
        return sourceObjectID;
    }

    /**
     * Sets the value of the sourceObjectID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceObjectID(String value) {
        this.sourceObjectID = value;
    }

    /**
     * Gets the value of the sourceTypeID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSourceTypeID() {
        return sourceTypeID;
    }

    /**
     * Sets the value of the sourceTypeID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSourceTypeID(Integer value) {
        this.sourceTypeID = value;
    }

    /**
     * Gets the value of the ownerID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerID() {
        return ownerID;
    }

    /**
     * Sets the value of the ownerID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerID(String value) {
        this.ownerID = value;
    }

    /**
     * Gets the value of the statusID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatusID() {
        return statusID;
    }

    /**
     * Sets the value of the statusID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatusID(String value) {
        this.statusID = value;
    }

    /**
     * Gets the value of the createdBy property.
     * 
     * @return
     *     possible object is
     *     {@link ClientID }
     *     
     */
    public ClientID getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the value of the createdBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientID }
     *     
     */
    public void setCreatedBy(ClientID value) {
        this.createdBy = value;
    }

    /**
     * Gets the value of the modifiedBy property.
     * 
     * @return
     *     possible object is
     *     {@link ClientID }
     *     
     */
    public ClientID getModifiedBy() {
        return modifiedBy;
    }

    /**
     * Sets the value of the modifiedBy property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientID }
     *     
     */
    public void setModifiedBy(ClientID value) {
        this.modifiedBy = value;
    }

}
