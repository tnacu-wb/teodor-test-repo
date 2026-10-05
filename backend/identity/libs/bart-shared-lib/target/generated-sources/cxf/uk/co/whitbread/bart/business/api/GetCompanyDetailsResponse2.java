
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetCompanyDetailsResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="GetCompanyDetailsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="companyDetails" type="{http://corporate.micros.com/1.0}CompanyDetails"/&gt;
 *         &lt;element name="cellCodes" type="{http://corporate.micros.com/1.0}ArrayOfListItemListItem"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *         &lt;element name="locked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="allowCentralCC" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="allowMarketing" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="maxNumberOfNights" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCompanyDetailsResponse", propOrder = {
    "companyDetails",
    "cellCodes",
    "success",
    "errors",
    "locked",
    "allowCentralCC",
    "allowMarketing",
    "maxNumberOfNights"
})
public class GetCompanyDetailsResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected CompanyDetails companyDetails;
    @XmlElement(required = true)
    protected ArrayOfListItemListItem cellCodes;
    protected Boolean success;
    protected ArrayOferrorError errors;
    protected Boolean locked;
    protected boolean allowCentralCC;
    protected boolean allowMarketing;
    protected Long maxNumberOfNights;

    /**
     * Gets the value of the companyDetails property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyDetails }
     *     
     */
    public CompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    /**
     * Sets the value of the companyDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyDetails }
     *     
     */
    public void setCompanyDetails(CompanyDetails value) {
        this.companyDetails = value;
    }

    /**
     * Gets the value of the cellCodes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public ArrayOfListItemListItem getCellCodes() {
        return cellCodes;
    }

    /**
     * Sets the value of the cellCodes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfListItemListItem }
     *     
     */
    public void setCellCodes(ArrayOfListItemListItem value) {
        this.cellCodes = value;
    }

    /**
     * Gets the value of the success property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setSuccess(Boolean value) {
        this.success = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOferrorError }
     *     
     */
    public ArrayOferrorError getErrors() {
        return errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOferrorError }
     *     
     */
    public void setErrors(ArrayOferrorError value) {
        this.errors = value;
    }

    /**
     * Gets the value of the locked property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLocked(Boolean value) {
        this.locked = value;
    }

    /**
     * Gets the value of the allowCentralCC property.
     * 
     */
    public boolean isAllowCentralCC() {
        return allowCentralCC;
    }

    /**
     * Sets the value of the allowCentralCC property.
     * 
     */
    public void setAllowCentralCC(boolean value) {
        this.allowCentralCC = value;
    }

    /**
     * Gets the value of the allowMarketing property.
     * 
     */
    public boolean isAllowMarketing() {
        return allowMarketing;
    }

    /**
     * Sets the value of the allowMarketing property.
     * 
     */
    public void setAllowMarketing(boolean value) {
        this.allowMarketing = value;
    }

    /**
     * Gets the value of the maxNumberOfNights property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMaxNumberOfNights() {
        return maxNumberOfNights;
    }

    /**
     * Sets the value of the maxNumberOfNights property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMaxNumberOfNights(Long value) {
        this.maxNumberOfNights = value;
    }

}
