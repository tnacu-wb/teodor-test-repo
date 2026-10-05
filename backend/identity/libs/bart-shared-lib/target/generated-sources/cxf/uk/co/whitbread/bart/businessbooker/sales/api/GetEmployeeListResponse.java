
package uk.co.whitbread.bart.businessbooker.sales.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetEmployeeListResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="GetEmployeeListResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="userDefinedFields" type="{http://corporate.micros.com/1.0}CompanyMIDetails" minOccurs="0"/&gt;
 *         &lt;element name="header" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="data" type="{http://corporate.micros.com/1.0}ExportData"/&gt;
 *         &lt;element name="numberOfRecords" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="endOfFile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
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
@XmlType(name = "GetEmployeeListResponse", propOrder = {
    "userDefinedFields",
    "header",
    "data",
    "numberOfRecords",
    "endOfFile",
    "success",
    "errors"
})
public class GetEmployeeListResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected CompanyMIDetails userDefinedFields;
    @XmlElement(required = true)
    protected String header;
    @XmlElement(required = true)
    protected ExportData data;
    @XmlElement(required = true)
    protected String numberOfRecords;
    protected boolean endOfFile;
    protected boolean success;
    protected ArrayOferrorError errors;

    /**
     * Gets the value of the userDefinedFields property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyMIDetails }
     *     
     */
    public CompanyMIDetails getUserDefinedFields() {
        return userDefinedFields;
    }

    /**
     * Sets the value of the userDefinedFields property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyMIDetails }
     *     
     */
    public void setUserDefinedFields(CompanyMIDetails value) {
        this.userDefinedFields = value;
    }

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeader(String value) {
        this.header = value;
    }

    /**
     * Gets the value of the data property.
     * 
     * @return
     *     possible object is
     *     {@link ExportData }
     *     
     */
    public ExportData getData() {
        return data;
    }

    /**
     * Sets the value of the data property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportData }
     *     
     */
    public void setData(ExportData value) {
        this.data = value;
    }

    /**
     * Gets the value of the numberOfRecords property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumberOfRecords() {
        return numberOfRecords;
    }

    /**
     * Sets the value of the numberOfRecords property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumberOfRecords(String value) {
        this.numberOfRecords = value;
    }

    /**
     * Gets the value of the endOfFile property.
     * 
     */
    public boolean isEndOfFile() {
        return endOfFile;
    }

    /**
     * Sets the value of the endOfFile property.
     * 
     */
    public void setEndOfFile(boolean value) {
        this.endOfFile = value;
    }

    /**
     * Gets the value of the success property.
     * 
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Sets the value of the success property.
     * 
     */
    public void setSuccess(boolean value) {
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
