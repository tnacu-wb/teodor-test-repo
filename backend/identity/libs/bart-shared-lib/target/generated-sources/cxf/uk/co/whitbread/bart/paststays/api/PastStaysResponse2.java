
package uk.co.whitbread.bart.paststays.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for PastStaysResponse2 complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="PastStaysResponse2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="pastStays" type="{http://bartws.micros.com/1.13}ArrayOfPastStayPastStays2"/&gt;
 *         &lt;element name="pastStaysError" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;enumeration value="INVALID_DATA_IN_FIELD"/&gt;
 *               &lt;enumeration value="MISSING_MANDATORY_FIELD"/&gt;
 *               &lt;enumeration value="USER_NOT_LOGGED_IN"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="errorDetail" type="{http://bartws.micros.com/1.13}ErrorDetailsPastStays" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PastStaysResponse2", propOrder = {
    "sessionID",
    "pastStays",
    "pastStaysError",
    "errorDetail"
})
public class PastStaysResponse2
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected ArrayOfPastStayPastStays2 pastStays;
    protected String pastStaysError;
    protected ErrorDetailsPastStays errorDetail;

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
     * Gets the value of the pastStays property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfPastStayPastStays2 }
     *     
     */
    public ArrayOfPastStayPastStays2 getPastStays() {
        return pastStays;
    }

    /**
     * Sets the value of the pastStays property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfPastStayPastStays2 }
     *     
     */
    public void setPastStays(ArrayOfPastStayPastStays2 value) {
        this.pastStays = value;
    }

    /**
     * Gets the value of the pastStaysError property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPastStaysError() {
        return pastStaysError;
    }

    /**
     * Sets the value of the pastStaysError property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPastStaysError(String value) {
        this.pastStaysError = value;
    }

    /**
     * Gets the value of the errorDetail property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorDetailsPastStays }
     *     
     */
    public ErrorDetailsPastStays getErrorDetail() {
        return errorDetail;
    }

    /**
     * Sets the value of the errorDetail property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorDetailsPastStays }
     *     
     */
    public void setErrorDetail(ErrorDetailsPastStays value) {
        this.errorDetail = value;
    }

}
