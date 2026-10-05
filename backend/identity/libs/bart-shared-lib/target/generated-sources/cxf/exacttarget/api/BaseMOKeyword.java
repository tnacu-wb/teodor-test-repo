
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Maybe add verb here...
 * 
 * &lt;p&gt;Java class for BaseMOKeyword complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="BaseMOKeyword"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}APIObject"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IsDefaultKeyword" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseMOKeyword", propOrder = {
    "isDefaultKeyword"
})
@XmlSeeAlso({
    SendSMSMOKeyword.class,
    UnsubscribeFromSMSPublicationMOKeyword.class,
    DoubleOptInMOKeyword.class,
    HelpMOKeyword.class,
    SendEmailMOKeyword.class
})
public class BaseMOKeyword
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "IsDefaultKeyword")
    protected Boolean isDefaultKeyword;

    /**
     * Gets the value of the isDefaultKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsDefaultKeyword() {
        return isDefaultKeyword;
    }

    /**
     * Sets the value of the isDefaultKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsDefaultKeyword(Boolean value) {
        this.isDefaultKeyword = value;
    }

}
