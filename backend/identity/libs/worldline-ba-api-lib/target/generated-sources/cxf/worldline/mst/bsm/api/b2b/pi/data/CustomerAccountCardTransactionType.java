
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardTransactionType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardTransactionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InvoiceDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/&gt;
 *         &lt;element name="InvoiceNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="TransactionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/&gt;
 *         &lt;element name="NetAmount" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType" minOccurs="0"/&gt;
 *         &lt;element name="TaxAmount" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType" minOccurs="0"/&gt;
 *         &lt;element name="GrossAmount" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="Location"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PAN"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CardNumberMaskedType"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="19"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CardName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PurchaseOrderReference" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CustomerOwnRef" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="SalesOrderNumber" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="LineItems" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardTransactionDetailType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardTransactionType", propOrder = {
    "invoiceDate",
    "invoiceNo",
    "transactionDate",
    "netAmount",
    "taxAmount",
    "grossAmount",
    "location",
    "pan",
    "cardName",
    "purchaseOrderReference",
    "customerOwnRef",
    "salesOrderNumber",
    "lineItems",
    "customAttributes"
})
public class CustomerAccountCardTransactionType {

    @XmlElement(name = "InvoiceDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar invoiceDate;
    @XmlElement(name = "InvoiceNo")
    protected String invoiceNo;
    /**
     * Includes time element
     * 
     */
    @XmlElement(name = "TransactionDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transactionDate;
    @XmlElement(name = "NetAmount")
    protected CurrencyType netAmount;
    @XmlElement(name = "TaxAmount")
    protected CurrencyType taxAmount;
    @XmlElement(name = "GrossAmount", required = true)
    protected CurrencyType grossAmount;
    /**
     * Merchant location (derived from Merchant Id)
     * 
     */
    @XmlElement(name = "Location", required = true)
    protected String location;
    /**
     * Masked
     * 
     */
    @XmlElement(name = "PAN", required = true)
    protected String pan;
    /**
     * Derived from card number
     * 
     */
    @XmlElement(name = "CardName", required = true)
    protected String cardName;
    /**
     * AKA PurchaseRef
     * 
     */
    @XmlElement(name = "PurchaseOrderReference")
    protected String purchaseOrderReference;
    @XmlElement(name = "CustomerOwnRef")
    protected String customerOwnRef;
    /**
     * AKA ReservationNumber
     * 
     */
    @XmlElement(name = "SalesOrderNumber")
    protected String salesOrderNumber;
    @XmlElement(name = "LineItems")
    protected List<CustomerAccountCardTransactionDetailType> lineItems;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the invoiceDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the value of the invoiceDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setInvoiceDate(XMLGregorianCalendar value) {
        this.invoiceDate = value;
    }

    /**
     * Gets the value of the invoiceNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvoiceNo() {
        return invoiceNo;
    }

    /**
     * Sets the value of the invoiceNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvoiceNo(String value) {
        this.invoiceNo = value;
    }

    /**
     * Includes time element
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the value of the transactionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     * @see #getTransactionDate()
     */
    public void setTransactionDate(XMLGregorianCalendar value) {
        this.transactionDate = value;
    }

    /**
     * Gets the value of the netAmount property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getNetAmount() {
        return netAmount;
    }

    /**
     * Sets the value of the netAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setNetAmount(CurrencyType value) {
        this.netAmount = value;
    }

    /**
     * Gets the value of the taxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getTaxAmount() {
        return taxAmount;
    }

    /**
     * Sets the value of the taxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setTaxAmount(CurrencyType value) {
        this.taxAmount = value;
    }

    /**
     * Gets the value of the grossAmount property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getGrossAmount() {
        return grossAmount;
    }

    /**
     * Sets the value of the grossAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setGrossAmount(CurrencyType value) {
        this.grossAmount = value;
    }

    /**
     * Merchant location (derived from Merchant Id)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getLocation()
     */
    public void setLocation(String value) {
        this.location = value;
    }

    /**
     * Masked
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAN() {
        return pan;
    }

    /**
     * Sets the value of the pan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getPAN()
     */
    public void setPAN(String value) {
        this.pan = value;
    }

    /**
     * Derived from card number
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Sets the value of the cardName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCardName()
     */
    public void setCardName(String value) {
        this.cardName = value;
    }

    /**
     * AKA PurchaseRef
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPurchaseOrderReference() {
        return purchaseOrderReference;
    }

    /**
     * Sets the value of the purchaseOrderReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getPurchaseOrderReference()
     */
    public void setPurchaseOrderReference(String value) {
        this.purchaseOrderReference = value;
    }

    /**
     * Gets the value of the customerOwnRef property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerOwnRef() {
        return customerOwnRef;
    }

    /**
     * Sets the value of the customerOwnRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerOwnRef(String value) {
        this.customerOwnRef = value;
    }

    /**
     * AKA ReservationNumber
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesOrderNumber() {
        return salesOrderNumber;
    }

    /**
     * Sets the value of the salesOrderNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getSalesOrderNumber()
     */
    public void setSalesOrderNumber(String value) {
        this.salesOrderNumber = value;
    }

    /**
     * Gets the value of the lineItems property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the lineItems property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getLineItems().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerAccountCardTransactionDetailType }
     * </p>
     * 
     * 
     * @return
     *     The value of the lineItems property.
     */
    public List<CustomerAccountCardTransactionDetailType> getLineItems() {
        if (lineItems == null) {
            lineItems = new ArrayList<>();
        }
        return this.lineItems;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customAttributes property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomAttributeType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<CustomAttributeType> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
