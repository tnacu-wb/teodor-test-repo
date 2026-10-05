
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountViewTransactionsByDateCriteriaType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountViewTransactionsByDateCriteriaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DateFrom" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="DateTo" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="TransactionTypes"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;enumeration value="Invoiced"/&gt;
 *               &lt;enumeration value="Uninvoiced"/&gt;
 *               &lt;enumeration value="Both"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PAN" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="19"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountViewTransactionsByDateCriteriaType", propOrder = {
    "dateFrom",
    "dateTo",
    "transactionTypes",
    "pan"
})
public class CustomerAccountViewTransactionsByDateCriteriaType {

    /**
     * Transaction date
     * 
     */
    @XmlElement(name = "DateFrom", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateFrom;
    /**
     * Transaction date
     * 
     */
    @XmlElement(name = "DateTo", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar dateTo;
    /**
     * Invoiced, Uninvoiced or both
     * 
     */
    @XmlElement(name = "TransactionTypes", required = true)
    protected String transactionTypes;
    /**
     * Only include transactions for this PAN number
     * 
     */
    @XmlElement(name = "PAN")
    protected String pan;

    /**
     * Transaction date
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the value of the dateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     * @see #getDateFrom()
     */
    public void setDateFrom(XMLGregorianCalendar value) {
        this.dateFrom = value;
    }

    /**
     * Transaction date
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTo() {
        return dateTo;
    }

    /**
     * Sets the value of the dateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     * @see #getDateTo()
     */
    public void setDateTo(XMLGregorianCalendar value) {
        this.dateTo = value;
    }

    /**
     * Invoiced, Uninvoiced or both
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionTypes() {
        return transactionTypes;
    }

    /**
     * Sets the value of the transactionTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getTransactionTypes()
     */
    public void setTransactionTypes(String value) {
        this.transactionTypes = value;
    }

    /**
     * Only include transactions for this PAN number
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

}
