
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for DataExtension complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DataExtension"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="IsSendable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IsTestable" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="SendableDataExtensionField" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionField" minOccurs="0"/&gt;
 *         &lt;element name="SendableSubscriberField" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Attribute" minOccurs="0"/&gt;
 *         &lt;element name="Template" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionTemplate" minOccurs="0"/&gt;
 *         &lt;element name="DataRetentionPeriodLength" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="DataRetentionPeriodUnitOfMeasure" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="RowBasedRetention" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="ResetRetentionPeriodOnImport" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="DeleteAtEndOfRetentionPeriod" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="RetainUntil" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="Fields" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Field" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionField" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="DataRetentionPeriod" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DateTimeUnitOfMeasure" minOccurs="0"/&gt;
 *         &lt;element name="CategoryID" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataExtension", propOrder = {
    "name",
    "description",
    "isSendable",
    "isTestable",
    "sendableDataExtensionField",
    "sendableSubscriberField",
    "template",
    "dataRetentionPeriodLength",
    "dataRetentionPeriodUnitOfMeasure",
    "rowBasedRetention",
    "resetRetentionPeriodOnImport",
    "deleteAtEndOfRetentionPeriod",
    "retainUntil",
    "fields",
    "dataRetentionPeriod",
    "categoryID",
    "status"
})
public class DataExtension
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "Description")
    protected String description;
    @XmlElement(name = "IsSendable")
    protected Boolean isSendable;
    @XmlElement(name = "IsTestable")
    protected Boolean isTestable;
    @XmlElement(name = "SendableDataExtensionField")
    protected DataExtensionField sendableDataExtensionField;
    @XmlElement(name = "SendableSubscriberField")
    protected Attribute sendableSubscriberField;
    @XmlElement(name = "Template")
    protected DataExtensionTemplate template;
    @XmlElement(name = "DataRetentionPeriodLength")
    protected Integer dataRetentionPeriodLength;
    /**
     * Deprecated. Use DataRetentionPeriod instead.
     * 
     */
    @XmlElement(name = "DataRetentionPeriodUnitOfMeasure")
    protected Integer dataRetentionPeriodUnitOfMeasure;
    @XmlElement(name = "RowBasedRetention")
    protected Boolean rowBasedRetention;
    @XmlElement(name = "ResetRetentionPeriodOnImport")
    protected Boolean resetRetentionPeriodOnImport;
    @XmlElement(name = "DeleteAtEndOfRetentionPeriod")
    protected Boolean deleteAtEndOfRetentionPeriod;
    @XmlElement(name = "RetainUntil")
    protected String retainUntil;
    @XmlElement(name = "Fields")
    protected DataExtension.Fields fields;
    @XmlElement(name = "DataRetentionPeriod")
    @XmlSchemaType(name = "string")
    protected DateTimeUnitOfMeasure dataRetentionPeriod;
    @XmlElement(name = "CategoryID")
    protected Long categoryID;
    @XmlElement(name = "Status")
    protected String status;

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
     * Gets the value of the isSendable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSendable() {
        return isSendable;
    }

    /**
     * Sets the value of the isSendable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSendable(Boolean value) {
        this.isSendable = value;
    }

    /**
     * Gets the value of the isTestable property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsTestable() {
        return isTestable;
    }

    /**
     * Sets the value of the isTestable property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsTestable(Boolean value) {
        this.isTestable = value;
    }

    /**
     * Gets the value of the sendableDataExtensionField property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtensionField }
     *     
     */
    public DataExtensionField getSendableDataExtensionField() {
        return sendableDataExtensionField;
    }

    /**
     * Sets the value of the sendableDataExtensionField property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionField }
     *     
     */
    public void setSendableDataExtensionField(DataExtensionField value) {
        this.sendableDataExtensionField = value;
    }

    /**
     * Gets the value of the sendableSubscriberField property.
     * 
     * @return
     *     possible object is
     *     {@link Attribute }
     *     
     */
    public Attribute getSendableSubscriberField() {
        return sendableSubscriberField;
    }

    /**
     * Sets the value of the sendableSubscriberField property.
     * 
     * @param value
     *     allowed object is
     *     {@link Attribute }
     *     
     */
    public void setSendableSubscriberField(Attribute value) {
        this.sendableSubscriberField = value;
    }

    /**
     * Gets the value of the template property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtensionTemplate }
     *     
     */
    public DataExtensionTemplate getTemplate() {
        return template;
    }

    /**
     * Sets the value of the template property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionTemplate }
     *     
     */
    public void setTemplate(DataExtensionTemplate value) {
        this.template = value;
    }

    /**
     * Gets the value of the dataRetentionPeriodLength property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDataRetentionPeriodLength() {
        return dataRetentionPeriodLength;
    }

    /**
     * Sets the value of the dataRetentionPeriodLength property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setDataRetentionPeriodLength(Integer value) {
        this.dataRetentionPeriodLength = value;
    }

    /**
     * Deprecated. Use DataRetentionPeriod instead.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getDataRetentionPeriodUnitOfMeasure() {
        return dataRetentionPeriodUnitOfMeasure;
    }

    /**
     * Sets the value of the dataRetentionPeriodUnitOfMeasure property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     * @see #getDataRetentionPeriodUnitOfMeasure()
     */
    public void setDataRetentionPeriodUnitOfMeasure(Integer value) {
        this.dataRetentionPeriodUnitOfMeasure = value;
    }

    /**
     * Gets the value of the rowBasedRetention property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isRowBasedRetention() {
        return rowBasedRetention;
    }

    /**
     * Sets the value of the rowBasedRetention property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setRowBasedRetention(Boolean value) {
        this.rowBasedRetention = value;
    }

    /**
     * Gets the value of the resetRetentionPeriodOnImport property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isResetRetentionPeriodOnImport() {
        return resetRetentionPeriodOnImport;
    }

    /**
     * Sets the value of the resetRetentionPeriodOnImport property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setResetRetentionPeriodOnImport(Boolean value) {
        this.resetRetentionPeriodOnImport = value;
    }

    /**
     * Gets the value of the deleteAtEndOfRetentionPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDeleteAtEndOfRetentionPeriod() {
        return deleteAtEndOfRetentionPeriod;
    }

    /**
     * Sets the value of the deleteAtEndOfRetentionPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDeleteAtEndOfRetentionPeriod(Boolean value) {
        this.deleteAtEndOfRetentionPeriod = value;
    }

    /**
     * Gets the value of the retainUntil property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetainUntil() {
        return retainUntil;
    }

    /**
     * Sets the value of the retainUntil property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetainUntil(String value) {
        this.retainUntil = value;
    }

    /**
     * Gets the value of the fields property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtension.Fields }
     *     
     */
    public DataExtension.Fields getFields() {
        return fields;
    }

    /**
     * Sets the value of the fields property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtension.Fields }
     *     
     */
    public void setFields(DataExtension.Fields value) {
        this.fields = value;
    }

    /**
     * Gets the value of the dataRetentionPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimeUnitOfMeasure }
     *     
     */
    public DateTimeUnitOfMeasure getDataRetentionPeriod() {
        return dataRetentionPeriod;
    }

    /**
     * Sets the value of the dataRetentionPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimeUnitOfMeasure }
     *     
     */
    public void setDataRetentionPeriod(DateTimeUnitOfMeasure value) {
        this.dataRetentionPeriod = value;
    }

    /**
     * Gets the value of the categoryID property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCategoryID() {
        return categoryID;
    }

    /**
     * Sets the value of the categoryID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCategoryID(Long value) {
        this.categoryID = value;
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
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Field" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionField" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "field"
    })
    public static class Fields
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Field")
        protected List<DataExtensionField> field;

        /**
         * Gets the value of the field property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the field property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getField().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DataExtensionField }
         * </p>
         * 
         * 
         * @return
         *     The value of the field property.
         */
        public List<DataExtensionField> getField() {
            if (field == null) {
                field = new ArrayList<>();
            }
            return this.field;
        }

    }

}
