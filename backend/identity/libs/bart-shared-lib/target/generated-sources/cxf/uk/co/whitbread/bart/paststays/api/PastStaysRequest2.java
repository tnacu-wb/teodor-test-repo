
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
 *         &lt;element name="PastStaysRequest2" type="{http://bartws.micros.com/1.13}PastStaysRequest2" minOccurs="0"/&gt;
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
    "pastStaysRequest2"
})
@XmlRootElement(name = "PastStaysRequest2")
public class PastStaysRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "PastStaysRequest2")
    protected PastStaysRequest22 pastStaysRequest2;

    /**
     * Gets the value of the pastStaysRequest2 property.
     * 
     * @return
     *     possible object is
     *     {@link PastStaysRequest22 }
     *     
     */
    public PastStaysRequest22 getPastStaysRequest2() {
        return pastStaysRequest2;
    }

    /**
     * Sets the value of the pastStaysRequest2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link PastStaysRequest22 }
     *     
     */
    public void setPastStaysRequest2(PastStaysRequest22 value) {
        this.pastStaysRequest2 = value;
    }

}
