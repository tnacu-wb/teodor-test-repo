
package uk.co.whitbread.bart.cancellation.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CancellationRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CancellationRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="confirmation" type="{http://bartws.micros.com/1.2}ArrayOfConfirmationConfirmation"/&gt;
 *         &lt;element name="cbtCompanyId" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cbtEmployeeId" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="10"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="cbtSalesManager" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;maxLength value="30"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CancellationRequest", propOrder = {
    "sessionID",
    "confirmation",
    "cbtCompanyId",
    "cbtEmployeeId",
    "cbtSalesManager"
})
public class CancellationRequest2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected ArrayOfConfirmationConfirmation confirmation;
    protected String cbtCompanyId;
    protected String cbtEmployeeId;
    protected String cbtSalesManager;

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
     * Gets the value of the confirmation property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfConfirmationConfirmation }
     *     
     */
    public ArrayOfConfirmationConfirmation getConfirmation() {
        return confirmation;
    }

    /**
     * Sets the value of the confirmation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfConfirmationConfirmation }
     *     
     */
    public void setConfirmation(ArrayOfConfirmationConfirmation value) {
        this.confirmation = value;
    }

    /**
     * Gets the value of the cbtCompanyId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtCompanyId() {
        return cbtCompanyId;
    }

    /**
     * Sets the value of the cbtCompanyId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtCompanyId(String value) {
        this.cbtCompanyId = value;
    }

    /**
     * Gets the value of the cbtEmployeeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtEmployeeId() {
        return cbtEmployeeId;
    }

    /**
     * Sets the value of the cbtEmployeeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtEmployeeId(String value) {
        this.cbtEmployeeId = value;
    }

    /**
     * Gets the value of the cbtSalesManager property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCbtSalesManager() {
        return cbtSalesManager;
    }

    /**
     * Sets the value of the cbtSalesManager property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCbtSalesManager(String value) {
        this.cbtSalesManager = value;
    }

}
