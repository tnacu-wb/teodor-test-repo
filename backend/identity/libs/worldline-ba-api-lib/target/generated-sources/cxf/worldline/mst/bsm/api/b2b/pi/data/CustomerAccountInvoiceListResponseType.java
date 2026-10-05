
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountInvoiceListResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountInvoiceListResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerAccountInvoiceItem" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountInvoiceItemType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="PagingResult" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}PagingResultType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountInvoiceListResponseType", propOrder = {
    "customerAccountInvoiceItem",
    "pagingResult"
})
public class CustomerAccountInvoiceListResponseType
    extends ResponseType
{

    @XmlElement(name = "CustomerAccountInvoiceItem")
    protected List<CustomerAccountInvoiceItemType> customerAccountInvoiceItem;
    @XmlElement(name = "PagingResult")
    protected PagingResultType pagingResult;

    /**
     * Gets the value of the customerAccountInvoiceItem property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customerAccountInvoiceItem property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomerAccountInvoiceItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerAccountInvoiceItemType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customerAccountInvoiceItem property.
     */
    public List<CustomerAccountInvoiceItemType> getCustomerAccountInvoiceItem() {
        if (customerAccountInvoiceItem == null) {
            customerAccountInvoiceItem = new ArrayList<>();
        }
        return this.customerAccountInvoiceItem;
    }

    /**
     * Gets the value of the pagingResult property.
     * 
     * @return
     *     possible object is
     *     {@link PagingResultType }
     *     
     */
    public PagingResultType getPagingResult() {
        return pagingResult;
    }

    /**
     * Sets the value of the pagingResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link PagingResultType }
     *     
     */
    public void setPagingResult(PagingResultType value) {
        this.pagingResult = value;
    }

}
