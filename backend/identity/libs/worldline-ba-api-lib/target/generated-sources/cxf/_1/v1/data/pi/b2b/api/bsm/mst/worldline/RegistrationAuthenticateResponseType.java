
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegistrationAuthenticateResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegistrationAuthenticateResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistrationCodeInfo" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}RegistrationCodeInfoType" minOccurs="0"/&gt;
 *         &lt;element name="PrepopulatedItems" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}RegistrationPrepopulatedItemsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistrationAuthenticateResponseType", propOrder = {
    "registrationCodeInfo",
    "prepopulatedItems"
})
public class RegistrationAuthenticateResponseType
    extends ResponseType
{

    /**
     * Same summary information as provided before for reference
     * 
     */
    @XmlElement(name = "RegistrationCodeInfo")
    protected RegistrationCodeInfoType registrationCodeInfo;
    /**
     * Pre-populated items that should be used as defaults for the registrant (these can be changed)
     * 
     */
    @XmlElement(name = "PrepopulatedItems")
    protected RegistrationPrepopulatedItemsType prepopulatedItems;

    /**
     * Same summary information as provided before for reference
     * 
     * @return
     *     possible object is
     *     {@link RegistrationCodeInfoType }
     *     
     */
    public RegistrationCodeInfoType getRegistrationCodeInfo() {
        return registrationCodeInfo;
    }

    /**
     * Sets the value of the registrationCodeInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationCodeInfoType }
     *     
     * @see #getRegistrationCodeInfo()
     */
    public void setRegistrationCodeInfo(RegistrationCodeInfoType value) {
        this.registrationCodeInfo = value;
    }

    /**
     * Pre-populated items that should be used as defaults for the registrant (these can be changed)
     * 
     * @return
     *     possible object is
     *     {@link RegistrationPrepopulatedItemsType }
     *     
     */
    public RegistrationPrepopulatedItemsType getPrepopulatedItems() {
        return prepopulatedItems;
    }

    /**
     * Sets the value of the prepopulatedItems property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistrationPrepopulatedItemsType }
     *     
     * @see #getPrepopulatedItems()
     */
    public void setPrepopulatedItems(RegistrationPrepopulatedItemsType value) {
        this.prepopulatedItems = value;
    }

}
