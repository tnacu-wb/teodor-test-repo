
package uk.co.whitbread.bart.business.auth0.api;

import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UDFAnswers2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UDFAnswers2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="miID" type="{http://www.w3.org/2001/XMLSchema}decimal"/&gt;
 *         &lt;element name="miAnswer" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UDFAnswers2", propOrder = {
    "miID",
    "miAnswer"
})
public class UDFAnswers2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected BigDecimal miID;
    @XmlElement(required = true)
    protected String miAnswer;

    /**
     * Gets the value of the miID property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getMiID() {
        return miID;
    }

    /**
     * Sets the value of the miID property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setMiID(BigDecimal value) {
        this.miID = value;
    }

    /**
     * Gets the value of the miAnswer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiAnswer() {
        return miAnswer;
    }

    /**
     * Sets the value of the miAnswer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiAnswer(String value) {
        this.miAnswer = value;
    }

}
