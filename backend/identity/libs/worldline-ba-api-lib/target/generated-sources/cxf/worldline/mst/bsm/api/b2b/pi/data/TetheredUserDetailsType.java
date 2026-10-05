
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for TetheredUserDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetheredUserDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TetheredUserOverview" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TetheredUserOverviewType"/&gt;
 *         &lt;element name="CustomerAccountOverview" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountOverviewType"/&gt;
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
@XmlType(name = "TetheredUserDetailsType", propOrder = {
    "tetheredUserOverview",
    "customerAccountOverview",
    "customAttributes"
})
public class TetheredUserDetailsType {

    @XmlElement(name = "TetheredUserOverview", required = true)
    protected TetheredUserOverviewType tetheredUserOverview;
    @XmlElement(name = "CustomerAccountOverview", required = true)
    protected CustomerAccountOverviewType customerAccountOverview;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the tetheredUserOverview property.
     * 
     * @return
     *     possible object is
     *     {@link TetheredUserOverviewType }
     *     
     */
    public TetheredUserOverviewType getTetheredUserOverview() {
        return tetheredUserOverview;
    }

    /**
     * Sets the value of the tetheredUserOverview property.
     * 
     * @param value
     *     allowed object is
     *     {@link TetheredUserOverviewType }
     *     
     */
    public void setTetheredUserOverview(TetheredUserOverviewType value) {
        this.tetheredUserOverview = value;
    }

    /**
     * Gets the value of the customerAccountOverview property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountOverviewType }
     *     
     */
    public CustomerAccountOverviewType getCustomerAccountOverview() {
        return customerAccountOverview;
    }

    /**
     * Sets the value of the customerAccountOverview property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountOverviewType }
     *     
     */
    public void setCustomerAccountOverview(CustomerAccountOverviewType value) {
        this.customerAccountOverview = value;
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
