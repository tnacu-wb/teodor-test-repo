
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCostCentreListResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCostCentreListResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerAccountCostCentreListItem" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}DropDownListItemType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCostCentreListResponseType", propOrder = {
    "customerAccountCostCentreListItem"
})
public class CustomerAccountCostCentreListResponseType
    extends ResponseType
{

    @XmlElement(name = "CustomerAccountCostCentreListItem")
    protected List<DropDownListItemType> customerAccountCostCentreListItem;

    /**
     * Gets the value of the customerAccountCostCentreListItem property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customerAccountCostCentreListItem property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomerAccountCostCentreListItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DropDownListItemType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customerAccountCostCentreListItem property.
     */
    public List<DropDownListItemType> getCustomerAccountCostCentreListItem() {
        if (customerAccountCostCentreListItem == null) {
            customerAccountCostCentreListItem = new ArrayList<>();
        }
        return this.customerAccountCostCentreListItem;
    }

}
