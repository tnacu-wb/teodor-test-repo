
package uk.co.whitbread.bart.business.corporate.api;

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
 *         &lt;element name="tetherByCardResult" type="{http://tempuri.org}TetherByCardResponse"/&gt;
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
    "tetherByCardResult"
})
@XmlRootElement(name = "tetherByCardResponse")
public class TetherByCardResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected TetherByCardResponse2 tetherByCardResult;

    /**
     * Gets the value of the tetherByCardResult property.
     * 
     * @return
     *     possible object is
     *     {@link TetherByCardResponse2 }
     *     
     */
    public TetherByCardResponse2 getTetherByCardResult() {
        return tetherByCardResult;
    }

    /**
     * Sets the value of the tetherByCardResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link TetherByCardResponse2 }
     *     
     */
    public void setTetherByCardResult(TetherByCardResponse2 value) {
        this.tetherByCardResult = value;
    }

}
