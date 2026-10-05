
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountViewCurrentBalancesResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountViewCurrentBalancesResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CustomerAccountBalancesDashboardPattern1" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountBalancesDashboardPattern1Type" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountViewCurrentBalancesResponseType", propOrder = {
    "customerAccountBalancesDashboardPattern1"
})
public class CustomerAccountViewCurrentBalancesResponseType
    extends ResponseType
{

    @XmlElement(name = "CustomerAccountBalancesDashboardPattern1")
    protected CustomerAccountBalancesDashboardPattern1Type customerAccountBalancesDashboardPattern1;

    /**
     * Gets the value of the customerAccountBalancesDashboardPattern1 property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountBalancesDashboardPattern1Type }
     *     
     */
    public CustomerAccountBalancesDashboardPattern1Type getCustomerAccountBalancesDashboardPattern1() {
        return customerAccountBalancesDashboardPattern1;
    }

    /**
     * Sets the value of the customerAccountBalancesDashboardPattern1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountBalancesDashboardPattern1Type }
     *     
     */
    public void setCustomerAccountBalancesDashboardPattern1(CustomerAccountBalancesDashboardPattern1Type value) {
        this.customerAccountBalancesDashboardPattern1 = value;
    }

}
