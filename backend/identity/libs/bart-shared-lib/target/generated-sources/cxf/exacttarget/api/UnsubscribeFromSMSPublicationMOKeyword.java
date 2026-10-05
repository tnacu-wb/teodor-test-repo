
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UnsubscribeFromSMSPublicationMOKeyword complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UnsubscribeFromSMSPublicationMOKeyword"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}BaseMOKeyword"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NextMOKeyword" type="{http://exacttarget.com/wsdl/partnerAPI}BaseMOKeyword" minOccurs="0"/&gt;
 *         &lt;element name="AllUnsubSuccessMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="InvalidPublicationMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="SingleUnsubSuccessMessage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UnsubscribeFromSMSPublicationMOKeyword", propOrder = {
    "nextMOKeyword",
    "allUnsubSuccessMessage",
    "invalidPublicationMessage",
    "singleUnsubSuccessMessage"
})
public class UnsubscribeFromSMSPublicationMOKeyword
    extends BaseMOKeyword
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "NextMOKeyword")
    protected BaseMOKeyword nextMOKeyword;
    @XmlElement(name = "AllUnsubSuccessMessage", required = true)
    protected String allUnsubSuccessMessage;
    @XmlElement(name = "InvalidPublicationMessage", required = true)
    protected String invalidPublicationMessage;
    @XmlElement(name = "SingleUnsubSuccessMessage", required = true)
    protected String singleUnsubSuccessMessage;

    /**
     * Gets the value of the nextMOKeyword property.
     * 
     * @return
     *     possible object is
     *     {@link BaseMOKeyword }
     *     
     */
    public BaseMOKeyword getNextMOKeyword() {
        return nextMOKeyword;
    }

    /**
     * Sets the value of the nextMOKeyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseMOKeyword }
     *     
     */
    public void setNextMOKeyword(BaseMOKeyword value) {
        this.nextMOKeyword = value;
    }

    /**
     * Gets the value of the allUnsubSuccessMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAllUnsubSuccessMessage() {
        return allUnsubSuccessMessage;
    }

    /**
     * Sets the value of the allUnsubSuccessMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAllUnsubSuccessMessage(String value) {
        this.allUnsubSuccessMessage = value;
    }

    /**
     * Gets the value of the invalidPublicationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalidPublicationMessage() {
        return invalidPublicationMessage;
    }

    /**
     * Sets the value of the invalidPublicationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalidPublicationMessage(String value) {
        this.invalidPublicationMessage = value;
    }

    /**
     * Gets the value of the singleUnsubSuccessMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSingleUnsubSuccessMessage() {
        return singleUnsubSuccessMessage;
    }

    /**
     * Sets the value of the singleUnsubSuccessMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSingleUnsubSuccessMessage(String value) {
        this.singleUnsubSuccessMessage = value;
    }

}
