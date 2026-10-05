
package uk.co.whitbread.qas.addresslookup.api;

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
 *         &lt;element name="QAPicklist" type="{http://www.qas.com/OnDemand-2011-03}QAPicklistType"/&gt;
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
    "qaPicklist"
})
@XmlRootElement(name = "Picklist")
public class Picklist {

    @XmlElement(name = "QAPicklist", required = true)
    protected QAPicklistType qaPicklist;

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

}
