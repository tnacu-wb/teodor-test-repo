
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountInvoiceItemType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountInvoiceItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="StatementDate"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}date"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="InvoiceNo"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="BroughtForward" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="PaymentsReceived" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="OverdueBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="InvoiceValue" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="StatementBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="FileAutoID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountInvoiceItemType", propOrder = {
    "statementDate",
    "invoiceNo",
    "broughtForward",
    "paymentsReceived",
    "overdueBalance",
    "invoiceValue",
    "statementBalance",
    "fileAutoID"
})
public class CustomerAccountInvoiceItemType {

    @XmlElement(name = "StatementDate", required = true)
    protected XMLGregorianCalendar statementDate;
    @XmlElement(name = "InvoiceNo", required = true)
    protected String invoiceNo;
    @XmlElement(name = "BroughtForward", required = true)
    protected CurrencyType broughtForward;
    @XmlElement(name = "PaymentsReceived", required = true)
    protected CurrencyType paymentsReceived;
    @XmlElement(name = "OverdueBalance", required = true)
    protected CurrencyType overdueBalance;
    @XmlElement(name = "InvoiceValue", required = true)
    protected CurrencyType invoiceValue;
    @XmlElement(name = "StatementBalance", required = true)
    protected CurrencyType statementBalance;
    @XmlElement(name = "FileAutoID")
    protected Integer fileAutoID;

    /**
     * Gets the value of the statementDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getStatementDate() {
        return statementDate;
    }

    /**
     * Sets the value of the statementDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setStatementDate(XMLGregorianCalendar value) {
        this.statementDate = value;
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
     * Gets the value of the broughtForward property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getBroughtForward() {
        return broughtForward;
    }

    /**
     * Sets the value of the broughtForward property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setBroughtForward(CurrencyType value) {
        this.broughtForward = value;
    }

    /**
     * Gets the value of the paymentsReceived property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getPaymentsReceived() {
        return paymentsReceived;
    }

    /**
     * Sets the value of the paymentsReceived property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setPaymentsReceived(CurrencyType value) {
        this.paymentsReceived = value;
    }

    /**
     * Gets the value of the overdueBalance property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getOverdueBalance() {
        return overdueBalance;
    }

    /**
     * Sets the value of the overdueBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setOverdueBalance(CurrencyType value) {
        this.overdueBalance = value;
    }

    /**
     * Gets the value of the invoiceValue property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getInvoiceValue() {
        return invoiceValue;
    }

    /**
     * Sets the value of the invoiceValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setInvoiceValue(CurrencyType value) {
        this.invoiceValue = value;
    }

    /**
     * Gets the value of the statementBalance property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getStatementBalance() {
        return statementBalance;
    }

    /**
     * Sets the value of the statementBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setStatementBalance(CurrencyType value) {
        this.statementBalance = value;
    }

    /**
     * Gets the value of the fileAutoID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFileAutoID() {
        return fileAutoID;
    }

    /**
     * Sets the value of the fileAutoID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFileAutoID(Integer value) {
        this.fileAutoID = value;
    }

}
