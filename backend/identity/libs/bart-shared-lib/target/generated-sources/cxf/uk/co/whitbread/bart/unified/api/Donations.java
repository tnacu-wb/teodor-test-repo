
package uk.co.whitbread.bart.unified.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Donations complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Donations"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="donationAllowed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="charityName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="donationAmounts" type="{http://bartws.micros.com/1.17}ArrayOfPricePrice" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Donations", propOrder = {
    "donationAllowed",
    "code",
    "charityName",
    "donationAmounts"
})
public class Donations
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean donationAllowed;
    protected Long code;
    protected String charityName;
    protected ArrayOfPricePrice donationAmounts;

    /**
     * Gets the value of the donationAllowed property.
     * 
     */
    public boolean isDonationAllowed() {
        return donationAllowed;
    }

    /**
     * Sets the value of the donationAllowed property.
     * 
     */
    public void setDonationAllowed(boolean value) {
        this.donationAllowed = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCode(Long value) {
        this.code = value;
    }

    /**
     * Gets the value of the charityName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCharityName() {
        return charityName;
    }

    /**
     * Sets the value of the charityName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCharityName(String value) {
        this.charityName = value;
    }

    /**
     * Gets the value of the donationAmounts property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPricePrice }
     *     
     */
    public ArrayOfPricePrice getDonationAmounts() {
        return donationAmounts;
    }

    /**
     * Sets the value of the donationAmounts property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPricePrice }
     *     
     */
    public void setDonationAmounts(ArrayOfPricePrice value) {
        this.donationAmounts = value;
    }

}
