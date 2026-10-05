
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AttributeMap complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="AttributeMap">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIProperty">
 *       <sequence>
 *         <element name="EntityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ColumnName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ColumnNameMappedTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="EntityNameMappedTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="AdditionalData" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIProperty" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeMap", propOrder = {
    "entityName",
    "columnName",
    "columnNameMappedTo",
    "entityNameMappedTo",
    "additionalData"
})
public class AttributeMap
    extends APIProperty
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "EntityName")
    protected String entityName;
    @XmlElement(name = "ColumnName")
    protected String columnName;
    @XmlElement(name = "ColumnNameMappedTo")
    protected String columnNameMappedTo;
    @XmlElement(name = "EntityNameMappedTo")
    protected String entityNameMappedTo;
    @XmlElement(name = "AdditionalData")
    protected List<APIProperty> additionalData;

    /**
     * Gets the value of the entityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Sets the value of the entityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityName(String value) {
        this.entityName = value;
    }

    /**
     * Gets the value of the columnName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Sets the value of the columnName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnName(String value) {
        this.columnName = value;
    }

    /**
     * Gets the value of the columnNameMappedTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getColumnNameMappedTo() {
        return columnNameMappedTo;
    }

    /**
     * Sets the value of the columnNameMappedTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setColumnNameMappedTo(String value) {
        this.columnNameMappedTo = value;
    }

    /**
     * Gets the value of the entityNameMappedTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntityNameMappedTo() {
        return entityNameMappedTo;
    }

    /**
     * Sets the value of the entityNameMappedTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntityNameMappedTo(String value) {
        this.entityNameMappedTo = value;
    }

    /**
     * Gets the value of the additionalData property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the additionalData property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAdditionalData().add(newItem);
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
     *     The value of the additionalData property.
     */
    public List<APIProperty> getAdditionalData() {
        if (additionalData == null) {
            additionalData = new ArrayList<>();
        }
        return this.additionalData;
    }

}
