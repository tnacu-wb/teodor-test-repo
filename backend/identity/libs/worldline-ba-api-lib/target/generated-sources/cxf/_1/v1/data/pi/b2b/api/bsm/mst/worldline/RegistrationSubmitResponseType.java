
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegistrationSubmitResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegistrationSubmitResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistrationCodeInfo" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}RegistrationCodeInfoType" minOccurs="0"/&gt;
 *         &lt;element name="TetherDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TetherDetailsType" minOccurs="0"/&gt;
 *         &lt;element name="ApplicationGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistrationSubmitResponseType", propOrder = {
    "registrationCodeInfo",
    "tetherDetails",
    "applicationGuid"
})
public class RegistrationSubmitResponseType
    extends ResponseType
{

    /**
     * Same summary information as provided before for reference
     * 
     */
    @XmlElement(name = "RegistrationCodeInfo")
    protected RegistrationCodeInfoType registrationCodeInfo;
    @XmlElement(name = "TetherDetails")
    protected TetherDetailsType tetherDetails;
    /**
     * A random Guid that represents the newly created application.
     * 
     */
    @XmlElement(name = "ApplicationGuid", required = true)
    protected String applicationGuid;

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
     * Gets the value of the tetherDetails property.
     * 
     * @return
     *     possible object is
     *     {@link TetherDetailsType }
     *     
     */
    public TetherDetailsType getTetherDetails() {
        return tetherDetails;
    }

    /**
     * Sets the value of the tetherDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link TetherDetailsType }
     *     
     */
    public void setTetherDetails(TetherDetailsType value) {
        this.tetherDetails = value;
    }

    /**
     * A random Guid that represents the newly created application.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApplicationGuid() {
        return applicationGuid;
    }

    /**
     * Sets the value of the applicationGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getApplicationGuid()
     */
    public void setApplicationGuid(String value) {
        this.applicationGuid = value;
    }

}
