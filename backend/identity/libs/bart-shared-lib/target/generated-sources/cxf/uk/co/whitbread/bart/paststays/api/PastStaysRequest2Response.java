
package uk.co.whitbread.bart.paststays.api;

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
 *         &lt;element name="PastStaysRequest2Result" type="{http://bartws.micros.com/1.13}PastStaysResponse2"/&gt;
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
    "pastStaysRequest2Result"
})
@XmlRootElement(name = "PastStaysRequest2Response")
public class PastStaysRequest2Response
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "PastStaysRequest2Result", required = true)
    protected PastStaysResponse2 pastStaysRequest2Result;

    /**
     * Gets the value of the pastStaysRequest2Result property.
     * 
     * @return
     *     possible object is
     *     {@link PastStaysResponse2 }
     *     
     */
    public PastStaysResponse2 getPastStaysRequest2Result() {
        return pastStaysRequest2Result;
    }

    /**
     * Sets the value of the pastStaysRequest2Result property.
     * 
     * @param value
     *     allowed object is
     *     {@link PastStaysResponse2 }
     *     
     */
    public void setPastStaysRequest2Result(PastStaysResponse2 value) {
        this.pastStaysRequest2Result = value;
    }

}
