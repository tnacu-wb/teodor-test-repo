
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for List complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="List">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject">
 *       <sequence>
 *         <element name="ListName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Category" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="Type" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ListTypeEnum" minOccurs="0"/>
 *         <element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Subscribers" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Subscriber" maxOccurs="unbounded" minOccurs="0"/>
 *         <element name="ListClassification" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ListClassificationEnum" minOccurs="0"/>
 *         <element name="AutomatedEmail" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Email" minOccurs="0"/>
 *         <element name="SendClassification" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SendClassification" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "List", propOrder = {
    "listName",
    "category",
    "type",
    "description",
    "subscribers",
    "listClassification",
    "automatedEmail",
    "sendClassification"
})
public class List
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ListName")
    protected String listName;
    @XmlElement(name = "Category")
    protected Integer category;
    @XmlElement(name = "Type")
    @XmlSchemaType(name = "string")
    protected ListTypeEnum type;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "Subscribers")
    protected java.util.List<Subscriber> subscribers;
    @XmlElement(name = "ListClassification")
    @XmlSchemaType(name = "string")
    protected ListClassificationEnum listClassification;
    @XmlElement(name = "AutomatedEmail")
    protected Email automatedEmail;
    @XmlElement(name = "SendClassification")
    protected SendClassification sendClassification;

    /**
     * Gets the value of the listName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListName() {
        return listName;
    }

    /**
     * Sets the value of the listName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListName(String value) {
        this.listName = value;
    }

    /**
     * Gets the value of the category property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCategory() {
        return category;
    }

    /**
     * Sets the value of the category property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCategory(Integer value) {
        this.category = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ListTypeEnum }
     *     
     */
    public ListTypeEnum getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListTypeEnum }
     *     
     */
    public void setType(ListTypeEnum value) {
        this.type = value;
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
     * Gets the value of the subscribers property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the subscribers property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getSubscribers().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Subscriber }
     * </p>
     * 
     * 
     * @return
     *     The value of the subscribers property.
     */
    public java.util.List<Subscriber> getSubscribers() {
        if (subscribers == null) {
            subscribers = new ArrayList<>();
        }
        return this.subscribers;
    }

    /**
     * Gets the value of the listClassification property.
     * 
     * @return
     *     possible object is
     *     {@link ListClassificationEnum }
     *     
     */
    public ListClassificationEnum getListClassification() {
        return listClassification;
    }

    /**
     * Sets the value of the listClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link ListClassificationEnum }
     *     
     */
    public void setListClassification(ListClassificationEnum value) {
        this.listClassification = value;
    }

    /**
     * Gets the value of the automatedEmail property.
     * 
     * @return
     *     possible object is
     *     {@link Email }
     *     
     */
    public Email getAutomatedEmail() {
        return automatedEmail;
    }

    /**
     * Sets the value of the automatedEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link Email }
     *     
     */
    public void setAutomatedEmail(Email value) {
        this.automatedEmail = value;
    }

    /**
     * Gets the value of the sendClassification property.
     * 
     * @return
     *     possible object is
     *     {@link SendClassification }
     *     
     */
    public SendClassification getSendClassification() {
        return sendClassification;
    }

    /**
     * Sets the value of the sendClassification property.
     * 
     * @param value
     *     allowed object is
     *     {@link SendClassification }
     *     
     */
    public void setSendClassification(SendClassification value) {
        this.sendClassification = value;
    }

}
