
package uk.co.whitbread.bart.registeredguest.api;

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
 *         &lt;element name="FutureStaysRequestResult" type="{http://bartws.micros.com/1.13}FutureStaysResponse2"/&gt;
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
    "futureStaysRequestResult"
})
@XmlRootElement(name = "FutureStaysRequestResponse")
public class FutureStaysRequestResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "FutureStaysRequestResult", required = true)
    protected FutureStaysResponse2 futureStaysRequestResult;

    /**
     * Gets the value of the futureStaysRequestResult property.
     * 
     * @return
     *     possible object is
     *     {@link FutureStaysResponse2 }
     *     
     */
    public FutureStaysResponse2 getFutureStaysRequestResult() {
        return futureStaysRequestResult;
    }

    /**
     * Sets the value of the futureStaysRequestResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link FutureStaysResponse2 }
     *     
     */
    public void setFutureStaysRequestResult(FutureStaysResponse2 value) {
        this.futureStaysRequestResult = value;
    }

}
