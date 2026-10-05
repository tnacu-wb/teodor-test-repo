
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Successful tether result information
 * 
 * &lt;p&gt;Java class for TetherDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="TetherDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TetheredUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
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
@XmlType(name = "TetherDetailsType", propOrder = {
    "tetheredUserGuid",
    "customAttributes"
})
public class TetherDetailsType {

    /**
     * A random Guid that represents the newly tethered user.  This is the value that will be used in future to create a new session (loginTetheredUser)
     * 
     */
    @XmlElement(name = "TetheredUserGuid", required = true)
    protected String tetheredUserGuid;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * A random Guid that represents the newly tethered user.  This is the value that will be used in future to create a new session (loginTetheredUser)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTetheredUserGuid() {
        return tetheredUserGuid;
    }

    /**
     * Sets the value of the tetheredUserGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getTetheredUserGuid()
     */
    public void setTetheredUserGuid(String value) {
        this.tetheredUserGuid = value;
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
