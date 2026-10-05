
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for FindEmployeeResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="FindEmployeeResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="employee" type="{http://corporate.micros.com/1.0}ArrayOfFindEmployeeFindEmployee"/&gt;
 *         &lt;element name="endOfFile" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="awaitingApproval" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="success" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="errors" type="{http://corporate.micros.com/1.0}ArrayOferrorError" minOccurs="0"/&gt;
 *         &lt;element name="lock" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FindEmployeeResponse", propOrder = {
    "employee",
    "endOfFile",
    "awaitingApproval",
    "success",
    "errors",
    "lock"
})
public class FindEmployeeResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected ArrayOfFindEmployeeFindEmployee employee;
    protected boolean endOfFile;
    protected Boolean awaitingApproval;
    protected Boolean success;
    protected ArrayOferrorError errors;
    protected Boolean lock;

    /**
     * Gets the value of the employee property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfFindEmployeeFindEmployee }
     *     
     */
    public ArrayOfFindEmployeeFindEmployee getEmployee() {
        return employee;
    }

    /**
     * Sets the value of the employee property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfFindEmployeeFindEmployee }
     *     
     */
    public void setEmployee(ArrayOfFindEmployeeFindEmployee value) {
        this.employee = value;
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
     * Gets the value of the awaitingApproval property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAwaitingApproval() {
        return awaitingApproval;
    }

    /**
     * Sets the value of the awaitingApproval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAwaitingApproval(Boolean value) {
        this.awaitingApproval = value;
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
     * Gets the value of the lock property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isLock() {
        return lock;
    }

    /**
     * Sets the value of the lock property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLock(Boolean value) {
        this.lock = value;
    }

}
