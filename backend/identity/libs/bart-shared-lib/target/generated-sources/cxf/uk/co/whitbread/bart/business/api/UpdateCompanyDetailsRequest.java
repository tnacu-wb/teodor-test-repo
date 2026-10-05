
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for UpdateCompanyDetailsRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="UpdateCompanyDetailsRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="companyID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="alternateCompanyName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="basicCompanyDetails" type="{http://corporate.micros.com/1.0}BasicCompanyDetails"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UpdateCompanyDetailsRequest", propOrder = {
    "sessionID",
    "companyID",
    "alternateCompanyName",
    "basicCompanyDetails"
})
public class UpdateCompanyDetailsRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String companyID;
    @XmlElement(required = true)
    protected String alternateCompanyName;
    @XmlElement(required = true)
    protected BasicCompanyDetails basicCompanyDetails;

    /**
     * Gets the value of the sessionID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSessionID() {
        return sessionID;
    }

    /**
     * Sets the value of the sessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSessionID(String value) {
        this.sessionID = value;
    }

    /**
     * Gets the value of the companyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyID(String value) {
        this.companyID = value;
    }

    /**
     * Gets the value of the alternateCompanyName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlternateCompanyName() {
        return alternateCompanyName;
    }

    /**
     * Sets the value of the alternateCompanyName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlternateCompanyName(String value) {
        this.alternateCompanyName = value;
    }

    /**
     * Gets the value of the basicCompanyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link BasicCompanyDetails }
     *     
     */
    public BasicCompanyDetails getBasicCompanyDetails() {
        return basicCompanyDetails;
    }

    /**
     * Sets the value of the basicCompanyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link BasicCompanyDetails }
     *     
     */
    public void setBasicCompanyDetails(BasicCompanyDetails value) {
        this.basicCompanyDetails = value;
    }

}
