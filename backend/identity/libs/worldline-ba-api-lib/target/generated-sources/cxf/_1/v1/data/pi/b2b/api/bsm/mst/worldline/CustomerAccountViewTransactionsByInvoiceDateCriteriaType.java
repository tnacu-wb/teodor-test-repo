
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.XMLGregorianCalendar;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountViewTransactionsByInvoiceDateCriteriaType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountViewTransactionsByInvoiceDateCriteriaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="InvoiceDate" type="{http://www.w3.org/2001/XMLSchema}date" maxOccurs="unbounded"/&gt;
 *         &lt;element name="PAN" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="19"/&gt;
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
@XmlType(name = "CustomerAccountViewTransactionsByInvoiceDateCriteriaType", propOrder = {
    "invoiceDate",
    "pan"
})
public class CustomerAccountViewTransactionsByInvoiceDateCriteriaType {

    /**
     * At least one invoice date must be provided
     * 
     */
    @XmlElement(name = "InvoiceDate", required = true)
    @XmlSchemaType(name = "date")
    protected List<XMLGregorianCalendar> invoiceDate;
    /**
     * Only include transactions for these (full) PAN numbers
     * 
     */
    @XmlElement(name = "PAN")
    protected List<String> pan;

    /**
     * At least one invoice date must be provided
     * 
     * Gets the value of the invoiceDate property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the invoiceDate property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getInvoiceDate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLGregorianCalendar }
     * </p>
     * 
     * 
     * @return
     *     The value of the invoiceDate property.
     */
    public List<XMLGregorianCalendar> getInvoiceDate() {
        if (invoiceDate == null) {
            invoiceDate = new ArrayList<>();
        }
        return this.invoiceDate;
    }

    /**
     * Only include transactions for these (full) PAN numbers
     * 
     * Gets the value of the pan property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the pan property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getPAN().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * </p>
     * 
     * 
     * @return
     *     The value of the pan property.
     */
    public List<String> getPAN() {
        if (pan == null) {
            pan = new ArrayList<>();
        }
        return this.pan;
    }

}
