
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for QAQueryHeader complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="QAQueryHeader"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="QAAuthentication" type="{http://www.qas.com/OnDemand-2011-03}QAAuthentication"/&gt;
 *         &lt;element name="Security" type="{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd}SecurityHeaderType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QAQueryHeader", propOrder = {
    "qaAuthentication",
    "security"
})
public class QAQueryHeader {

    @XmlElement(name = "QAAuthentication", required = true)
    protected QAAuthentication qaAuthentication;
    @XmlElement(name = "Security", required = true)
    protected SecurityHeaderType security;

    /**
     * Gets the value of the qaAuthentication property.
     * 
     * @return
     *     possible object is
     *     {@link QAAuthentication }
     *     
     */
    public QAAuthentication getQAAuthentication() {
        return qaAuthentication;
    }

    /**
     * Sets the value of the qaAuthentication property.
     * 
     * @param value
     *     allowed object is
     *     {@link QAAuthentication }
     *     
     */
    public void setQAAuthentication(QAAuthentication value) {
        this.qaAuthentication = value;
    }

    /**
     * Gets the value of the security property.
     * 
     * @return
     *     possible object is
     *     {@link SecurityHeaderType }
     *     
     */
    public SecurityHeaderType getSecurity() {
        return security;
    }

    /**
     * Sets the value of the security property.
     * 
     * @param value
     *     allowed object is
     *     {@link SecurityHeaderType }
     *     
     */
    public void setSecurity(SecurityHeaderType value) {
        this.security = value;
    }

}
