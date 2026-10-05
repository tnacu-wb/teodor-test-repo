
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="QAPicklist" type="{http://www.qas.com/OnDemand-2011-03}QAPicklistType" minOccurs="0"/&gt;
 *         &lt;element name="QAAddress" type="{http://www.qas.com/OnDemand-2011-03}QAAddressType" minOccurs="0"/&gt;
 *         &lt;element name="VerificationFlags" type="{http://www.qas.com/OnDemand-2011-03}VerificationFlagsType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="VerifyLevel" type="{http://www.qas.com/OnDemand-2011-03}VerifyLevelType" default="None" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "qaPicklist",
    "qaAddress",
    "verificationFlags"
})
@XmlRootElement(name = "QASearchResult")
public class QASearchResult {

    @XmlElement(name = "QAPicklist")
    protected QAPicklistType qaPicklist;
    @XmlElement(name = "QAAddress")
    protected QAAddressType qaAddress;
    @XmlElement(name = "VerificationFlags")
    protected VerificationFlagsType verificationFlags;
    @XmlAttribute(name = "VerifyLevel")
    protected VerifyLevelType verifyLevel;

    /**
     * Gets the value of the qaPicklist property.
     * 
     * @return
     *     possible object is
     *     {@link QAPicklistType }
     *     
     */
    public QAPicklistType getQAPicklist() {
        return qaPicklist;
    }

    /**
     * Sets the value of the qaPicklist property.
     * 
     * @param value
     *     allowed object is
     *     {@link QAPicklistType }
     *     
     */
    public void setQAPicklist(QAPicklistType value) {
        this.qaPicklist = value;
    }

    /**
     * Gets the value of the qaAddress property.
     * 
     * @return
     *     possible object is
     *     {@link QAAddressType }
     *     
     */
    public QAAddressType getQAAddress() {
        return qaAddress;
    }

    /**
     * Sets the value of the qaAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link QAAddressType }
     *     
     */
    public void setQAAddress(QAAddressType value) {
        this.qaAddress = value;
    }

    /**
     * Gets the value of the verificationFlags property.
     * 
     * @return
     *     possible object is
     *     {@link VerificationFlagsType }
     *     
     */
    public VerificationFlagsType getVerificationFlags() {
        return verificationFlags;
    }

    /**
     * Sets the value of the verificationFlags property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerificationFlagsType }
     *     
     */
    public void setVerificationFlags(VerificationFlagsType value) {
        this.verificationFlags = value;
    }

    /**
     * Gets the value of the verifyLevel property.
     * 
     * @return
     *     possible object is
     *     {@link VerifyLevelType }
     *     
     */
    public VerifyLevelType getVerifyLevel() {
        if (verifyLevel == null) {
            return VerifyLevelType.NONE;
        } else {
            return verifyLevel;
        }
    }

    /**
     * Sets the value of the verifyLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link VerifyLevelType }
     *     
     */
    public void setVerifyLevel(VerifyLevelType value) {
        this.verifyLevel = value;
    }

}
