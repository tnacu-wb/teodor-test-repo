
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ObjectDefinition complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ObjectDefinition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ObjectType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsCreatable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsUpdatable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsRetrievable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsQueryable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsReference" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ReferencedType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsPropertyCollection" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsObjectCollection" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="Properties" type="{http://exacttarget.com/wsdl/partnerAPI}PropertyDefinition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ExtendedProperties" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ExtendedProperty" type="{http://exacttarget.com/wsdl/partnerAPI}PropertyDefinition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ChildObjects" type="{http://exacttarget.com/wsdl/partnerAPI}ObjectDefinition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectDefinition", propOrder = {
    "objectType",
    "name",
    "isCreatable",
    "isUpdatable",
    "isRetrievable",
    "isQueryable",
    "isReference",
    "referencedType",
    "isPropertyCollection",
    "isObjectCollection",
    "properties",
    "extendedProperties",
    "childObjects"
})
public class ObjectDefinition
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ObjectType")
    protected String objectType;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "IsCreatable")
    protected Boolean isCreatable;
    @XmlElement(name = "IsUpdatable")
    protected Boolean isUpdatable;
    @XmlElement(name = "IsRetrievable")
    protected Boolean isRetrievable;
    @XmlElement(name = "IsQueryable")
    protected Boolean isQueryable;
    @XmlElement(name = "IsReference")
    protected Boolean isReference;
    @XmlElement(name = "ReferencedType")
    protected String referencedType;
    @XmlElement(name = "IsPropertyCollection")
    protected String isPropertyCollection;
    @XmlElement(name = "IsObjectCollection")
    protected Boolean isObjectCollection;
    @XmlElement(name = "Properties")
    protected List<PropertyDefinition> properties;
    @XmlElement(name = "ExtendedProperties")
    protected ObjectDefinition.ExtendedProperties extendedProperties;
    @XmlElement(name = "ChildObjects")
    protected List<ObjectDefinition> childObjects;

    /**
     * Gets the value of the objectType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectType() {
        return objectType;
    }

    /**
     * Sets the value of the objectType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectType(String value) {
        this.objectType = value;
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
     * Gets the value of the isCreatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsCreatable() {
        return isCreatable;
    }

    /**
     * Sets the value of the isCreatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsCreatable(Boolean value) {
        this.isCreatable = value;
    }

    /**
     * Gets the value of the isUpdatable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsUpdatable() {
        return isUpdatable;
    }

    /**
     * Sets the value of the isUpdatable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsUpdatable(Boolean value) {
        this.isUpdatable = value;
    }

    /**
     * Gets the value of the isRetrievable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsRetrievable() {
        return isRetrievable;
    }

    /**
     * Sets the value of the isRetrievable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsRetrievable(Boolean value) {
        this.isRetrievable = value;
    }

    /**
     * Gets the value of the isQueryable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsQueryable() {
        return isQueryable;
    }

    /**
     * Sets the value of the isQueryable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsQueryable(Boolean value) {
        this.isQueryable = value;
    }

    /**
     * Gets the value of the isReference property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsReference() {
        return isReference;
    }

    /**
     * Sets the value of the isReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsReference(Boolean value) {
        this.isReference = value;
    }

    /**
     * Gets the value of the referencedType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReferencedType() {
        return referencedType;
    }

    /**
     * Sets the value of the referencedType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReferencedType(String value) {
        this.referencedType = value;
    }

    /**
     * Gets the value of the isPropertyCollection property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIsPropertyCollection() {
        return isPropertyCollection;
    }

    /**
     * Sets the value of the isPropertyCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIsPropertyCollection(String value) {
        this.isPropertyCollection = value;
    }

    /**
     * Gets the value of the isObjectCollection property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsObjectCollection() {
        return isObjectCollection;
    }

    /**
     * Sets the value of the isObjectCollection property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsObjectCollection(Boolean value) {
        this.isObjectCollection = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the properties property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getProperties().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PropertyDefinition }
     * </p>
     * 
     * 
     * @return
     *     The value of the properties property.
     */
    public List<PropertyDefinition> getProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        return this.properties;
    }

    /**
     * Gets the value of the extendedProperties property.
     * 
     * @return
     *     possible object is
     *     {@link ObjectDefinition.ExtendedProperties }
     *     
     */
    public ObjectDefinition.ExtendedProperties getExtendedProperties() {
        return extendedProperties;
    }

    /**
     * Sets the value of the extendedProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectDefinition.ExtendedProperties }
     *     
     */
    public void setExtendedProperties(ObjectDefinition.ExtendedProperties value) {
        this.extendedProperties = value;
    }

    /**
     * Gets the value of the childObjects property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the childObjects property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getChildObjects().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ObjectDefinition }
     * </p>
     * 
     * 
     * @return
     *     The value of the childObjects property.
     */
    public List<ObjectDefinition> getChildObjects() {
        if (childObjects == null) {
            childObjects = new ArrayList<>();
        }
        return this.childObjects;
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
     *         &lt;element name="ExtendedProperty" type="{http://exacttarget.com/wsdl/partnerAPI}PropertyDefinition" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "extendedProperty"
    })
    public static class ExtendedProperties
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ExtendedProperty")
        protected List<PropertyDefinition> extendedProperty;

        /**
         * Gets the value of the extendedProperty property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the extendedProperty property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getExtendedProperty().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PropertyDefinition }
         * </p>
         * 
         * 
         * @return
         *     The value of the extendedProperty property.
         */
        public List<PropertyDefinition> getExtendedProperty() {
            if (extendedProperty == null) {
                extendedProperty = new ArrayList<>();
            }
            return this.extendedProperty;
        }

    }

}
