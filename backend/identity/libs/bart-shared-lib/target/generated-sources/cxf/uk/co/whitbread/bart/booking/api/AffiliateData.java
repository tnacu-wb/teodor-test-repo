
package uk.co.whitbread.bart.booking.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AffiliateData complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AffiliateData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="P36"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PURL"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AffiliateData", propOrder = {
    "p36",
    "purl"
})
public class AffiliateData
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "P36", required = true)
    protected String p36;
    @XmlElement(name = "PURL", required = true)
    protected String purl;

    /**
     * Gets the value of the p36 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getP36() {
        return p36;
    }

    /**
     * Sets the value of the p36 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setP36(String value) {
        this.p36 = value;
    }

    /**
     * Gets the value of the purl property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPURL() {
        return purl;
    }

    /**
     * Sets the value of the purl property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPURL(String value) {
        this.purl = value;
    }

}
