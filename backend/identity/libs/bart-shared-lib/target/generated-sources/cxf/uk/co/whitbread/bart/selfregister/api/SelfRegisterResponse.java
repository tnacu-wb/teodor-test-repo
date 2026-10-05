
package uk.co.whitbread.bart.selfregister.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SelfRegisterResult" type="{http://corporate.micros.com}SelfRegisterResponse"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "selfRegisterResult"
})
@XmlRootElement(name = "SelfRegisterResponse")
public class SelfRegisterResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SelfRegisterResult", required = true)
    protected SelfRegisterResponse2 selfRegisterResult;

    /**
     * Gets the value of the selfRegisterResult property.
     * 
     * @return
     *     possible object is
     *     {@link SelfRegisterResponse2 }
     *     
     */
    public SelfRegisterResponse2 getSelfRegisterResult() {
        return selfRegisterResult;
    }

    /**
     * Sets the value of the selfRegisterResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelfRegisterResponse2 }
     *     
     */
    public void setSelfRegisterResult(SelfRegisterResponse2 value) {
        this.selfRegisterResult = value;
    }

}
