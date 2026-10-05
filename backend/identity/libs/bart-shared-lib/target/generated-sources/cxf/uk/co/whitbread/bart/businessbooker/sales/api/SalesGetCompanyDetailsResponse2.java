
package uk.co.whitbread.bart.businessbooker.sales.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for SalesGetCompanyDetailsResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="SalesGetCompanyDetailsResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="marketingOptOut" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="cellCodes" type="{http://corporate.micros.com/1.0}ArrayOfListItemListItem"/&gt;
 *         &lt;element name="prohibitedRates" type="{http://corporate.micros.com/1.0}ArrayOfprohibitedRatesItemString"/&gt;
 *         &lt;element name="prohibitedHotels" type="{http://corporate.micros.com/1.0}ArrayOfprohibitedHotelsItemString"/&gt;
 *         &lt;element name="companyStatus" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="allowCentralCC" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="maxNumberOfNights" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="locked" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SalesGetCompanyDetailsResponse", propOrder = {
    "marketingOptOut",
    "cellCodes",
    "prohibitedRates",
    "prohibitedHotels",
    "companyStatus",
    "allowCentralCC",
    "maxNumberOfNights",
    "locked",
    "success",
    "errors"
})
public class SalesGetCompanyDetailsResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected boolean marketingOptOut;
    @XmlElement(required = true)
    protected ArrayOfListItemListItem cellCodes;
    @XmlElement(required = true)
    protected ArrayOfprohibitedRatesItemString prohibitedRates;
    @XmlElement(required = true)
    protected ArrayOfprohibitedHotelsItemString prohibitedHotels;
    @XmlElement(required = true)
    protected String companyStatus;
    protected boolean allowCentralCC;
    protected Long maxNumberOfNights;
    protected boolean locked;
    protected Boolean success;
    protected ArrayOferrorError errors;

    /**
     * Gets the value of the marketingOptOut property.
     * 
     */
    public boolean isMarketingOptOut() {
        return marketingOptOut;
    }

    /**
     * Sets the value of the marketingOptOut property.
     * 
     */
    public void setMarketingOptOut(boolean value) {
        this.marketingOptOut = value;
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
     * Gets the value of the prohibitedRates property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfprohibitedRatesItemString }
     *     
     */
    public ArrayOfprohibitedRatesItemString getProhibitedRates() {
        return prohibitedRates;
    }

    /**
     * Sets the value of the prohibitedRates property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfprohibitedRatesItemString }
     *     
     */
    public void setProhibitedRates(ArrayOfprohibitedRatesItemString value) {
        this.prohibitedRates = value;
    }

    /**
     * Gets the value of the prohibitedHotels property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfprohibitedHotelsItemString }
     *     
     */
    public ArrayOfprohibitedHotelsItemString getProhibitedHotels() {
        return prohibitedHotels;
    }

    /**
     * Sets the value of the prohibitedHotels property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfprohibitedHotelsItemString }
     *     
     */
    public void setProhibitedHotels(ArrayOfprohibitedHotelsItemString value) {
        this.prohibitedHotels = value;
    }

    /**
     * Gets the value of the companyStatus property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyStatus() {
        return companyStatus;
    }

    /**
     * Sets the value of the companyStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyStatus(String value) {
        this.companyStatus = value;
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

    /**
     * Gets the value of the locked property.
     * 
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Sets the value of the locked property.
     * 
     */
    public void setLocked(boolean value) {
        this.locked = value;
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

}
