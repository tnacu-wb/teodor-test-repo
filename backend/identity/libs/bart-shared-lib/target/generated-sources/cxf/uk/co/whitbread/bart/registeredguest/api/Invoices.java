
package uk.co.whitbread.bart.registeredguest.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for Invoices complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="Invoices"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="historyRecordNumber" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="copyInvoiceSuccess" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="copyInvoiceError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="USER_NOT_LOGGED_IN"/&gt;
 *               &lt;enumeration value="NO_REGISTERED_EMAIL_ADDRESS"/&gt;
 *               &lt;enumeration value="INVALID_STAY_NUMBER"/&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
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
@XmlType(name = "Invoices", propOrder = {
    "historyRecordNumber",
    "copyInvoiceSuccess",
    "copyInvoiceError"
})
public class Invoices
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    protected Long historyRecordNumber;
    protected boolean copyInvoiceSuccess;
    protected String copyInvoiceError;

    /**
     * Gets the value of the historyRecordNumber property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getHistoryRecordNumber() {
        return historyRecordNumber;
    }

    /**
     * Sets the value of the historyRecordNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setHistoryRecordNumber(Long value) {
        this.historyRecordNumber = value;
    }

    /**
     * Gets the value of the copyInvoiceSuccess property.
     * 
     */
    public boolean isCopyInvoiceSuccess() {
        return copyInvoiceSuccess;
    }

    /**
     * Sets the value of the copyInvoiceSuccess property.
     * 
     */
    public void setCopyInvoiceSuccess(boolean value) {
        this.copyInvoiceSuccess = value;
    }

    /**
     * Gets the value of the copyInvoiceError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCopyInvoiceError() {
        return copyInvoiceError;
    }

    /**
     * Sets the value of the copyInvoiceError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCopyInvoiceError(String value) {
        this.copyInvoiceError = value;
    }

}
