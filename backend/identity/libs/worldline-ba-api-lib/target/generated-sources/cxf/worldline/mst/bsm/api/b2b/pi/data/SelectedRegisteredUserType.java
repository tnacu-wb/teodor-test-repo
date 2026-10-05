
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SelectedRegisteredUserType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SelectedRegisteredUserType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="APIUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType" minOccurs="0"/&gt;
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
@XmlType(name = "SelectedRegisteredUserType", propOrder = {
    "apiUserGuid",
    "customAttributes"
})
public class SelectedRegisteredUserType {

    /**
     * If appropriate as selected from a drop down list this will be the APIUserGUID for the selected users rather than the TetheredUserGuid
     * 
     */
    @XmlElement(name = "APIUserGuid")
    protected String apiUserGuid;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * If appropriate as selected from a drop down list this will be the APIUserGUID for the selected users rather than the TetheredUserGuid
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAPIUserGuid() {
        return apiUserGuid;
    }

    /**
     * Sets the value of the apiUserGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAPIUserGuid()
     */
    public void setAPIUserGuid(String value) {
        this.apiUserGuid = value;
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
