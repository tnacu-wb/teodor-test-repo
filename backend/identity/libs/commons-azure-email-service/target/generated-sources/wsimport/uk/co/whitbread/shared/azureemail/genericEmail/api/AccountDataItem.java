
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AccountDataItem complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="AccountDataItem">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="ChildAccountID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="BrandID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="PrivateLabelID" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         <element name="AccountType" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccountDataItem", propOrder = {
    "childAccountID",
    "brandID",
    "privateLabelID",
    "accountType"
})
public class AccountDataItem
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ChildAccountID")
    protected Integer childAccountID;
    @XmlElement(name = "BrandID", nillable = true)
    protected Integer brandID;
    @XmlElement(name = "PrivateLabelID", nillable = true)
    protected Integer privateLabelID;
    @XmlElement(name = "AccountType", nillable = true)
    protected Integer accountType;

    /**
     * Gets the value of the childAccountID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getChildAccountID() {
        return childAccountID;
    }

    /**
     * Sets the value of the childAccountID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setChildAccountID(Integer value) {
        this.childAccountID = value;
    }

    /**
     * Gets the value of the brandID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getBrandID() {
        return brandID;
    }

    /**
     * Sets the value of the brandID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setBrandID(Integer value) {
        this.brandID = value;
    }

    /**
     * Gets the value of the privateLabelID property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getPrivateLabelID() {
        return privateLabelID;
    }

    /**
     * Sets the value of the privateLabelID property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setPrivateLabelID(Integer value) {
        this.privateLabelID = value;
    }

    /**
     * Gets the value of the accountType property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAccountType() {
        return accountType;
    }

    /**
     * Sets the value of the accountType property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAccountType(Integer value) {
        this.accountType = value;
    }

}
