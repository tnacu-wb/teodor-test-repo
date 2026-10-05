
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegistrationGetInfoResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegistrationGetInfoResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistrationCodeInfo" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}RegistrationCodeInfoType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistrationGetInfoResponseType", propOrder = {
    "registrationCodeInfo"
})
public class RegistrationGetInfoResponseType
    extends ResponseType
{

    /**
     * Summary information about the supplied registration code (including authentication questions)
     * 
     */
    @XmlElement(name = "RegistrationCodeInfo")
    protected RegistrationCodeInfoType registrationCodeInfo;

    /**
     * Summary information about the supplied registration code (including authentication questions)
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

}
