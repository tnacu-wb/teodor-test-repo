
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountViewTransactionsCriteriaType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountViewTransactionsCriteriaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DateSearch" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountViewTransactionsByDateCriteriaType" minOccurs="0"/&gt;
 *         &lt;element name="InvoiceNumberSearch" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountViewTransactionsByInvoiceNumberCriteriaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountViewTransactionsCriteriaType", propOrder = {
    "dateSearch",
    "invoiceNumberSearch"
})
public class CustomerAccountViewTransactionsCriteriaType {

    @XmlElement(name = "DateSearch")
    protected CustomerAccountViewTransactionsByDateCriteriaType dateSearch;
    @XmlElement(name = "InvoiceNumberSearch")
    protected CustomerAccountViewTransactionsByInvoiceNumberCriteriaType invoiceNumberSearch;

    /**
     * Gets the value of the dateSearch property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountViewTransactionsByDateCriteriaType }
     *     
     */
    public CustomerAccountViewTransactionsByDateCriteriaType getDateSearch() {
        return dateSearch;
    }

    /**
     * Sets the value of the dateSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountViewTransactionsByDateCriteriaType }
     *     
     */
    public void setDateSearch(CustomerAccountViewTransactionsByDateCriteriaType value) {
        this.dateSearch = value;
    }

    /**
     * Gets the value of the invoiceNumberSearch property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountViewTransactionsByInvoiceNumberCriteriaType }
     *     
     */
    public CustomerAccountViewTransactionsByInvoiceNumberCriteriaType getInvoiceNumberSearch() {
        return invoiceNumberSearch;
    }

    /**
     * Sets the value of the invoiceNumberSearch property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountViewTransactionsByInvoiceNumberCriteriaType }
     *     
     */
    public void setInvoiceNumberSearch(CustomerAccountViewTransactionsByInvoiceNumberCriteriaType value) {
        this.invoiceNumberSearch = value;
    }

}
