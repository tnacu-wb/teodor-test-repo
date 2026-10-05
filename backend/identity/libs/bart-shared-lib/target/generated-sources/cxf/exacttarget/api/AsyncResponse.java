
package exacttarget.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for AsyncResponse complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="AsyncResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResponseType" type="{http://exacttarget.com/wsdl/partnerAPI}AsyncResponseType"/&gt;
 *         &lt;element name="ResponseAddress" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RespondWhen" type="{http://exacttarget.com/wsdl/partnerAPI}RespondWhen" minOccurs="0"/&gt;
 *         &lt;element name="IncludeResults" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="IncludeObjects" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="OnlyIncludeBase" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AsyncResponse", propOrder = {
    "responseType",
    "responseAddress",
    "respondWhen",
    "includeResults",
    "includeObjects",
    "onlyIncludeBase"
})
public class AsyncResponse
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ResponseType", required = true)
    @XmlSchemaType(name = "string")
    protected AsyncResponseType responseType;
    @XmlElement(name = "ResponseAddress", required = true)
    protected String responseAddress;
    @XmlElement(name = "RespondWhen")
    @XmlSchemaType(name = "string")
    protected RespondWhen respondWhen;
    @XmlElement(name = "IncludeResults")
    protected Boolean includeResults;
    @XmlElement(name = "IncludeObjects")
    protected Boolean includeObjects;
    @XmlElement(name = "OnlyIncludeBase")
    protected Boolean onlyIncludeBase;

    /**
     * Gets the value of the responseType property.
     * 
     * @return
     *     possible object is
     *     {@link AsyncResponseType }
     *     
     */
    public AsyncResponseType getResponseType() {
        return responseType;
    }

    /**
     * Sets the value of the responseType property.
     * 
     * @param value
     *     allowed object is
     *     {@link AsyncResponseType }
     *     
     */
    public void setResponseType(AsyncResponseType value) {
        this.responseType = value;
    }

    /**
     * Gets the value of the responseAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseAddress() {
        return responseAddress;
    }

    /**
     * Sets the value of the responseAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseAddress(String value) {
        this.responseAddress = value;
    }

    /**
     * Gets the value of the respondWhen property.
     * 
     * @return
     *     possible object is
     *     {@link RespondWhen }
     *     
     */
    public RespondWhen getRespondWhen() {
        return respondWhen;
    }

    /**
     * Sets the value of the respondWhen property.
     * 
     * @param value
     *     allowed object is
     *     {@link RespondWhen }
     *     
     */
    public void setRespondWhen(RespondWhen value) {
        this.respondWhen = value;
    }

    /**
     * Gets the value of the includeResults property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeResults() {
        return includeResults;
    }

    /**
     * Sets the value of the includeResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeResults(Boolean value) {
        this.includeResults = value;
    }

    /**
     * Gets the value of the includeObjects property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIncludeObjects() {
        return includeObjects;
    }

    /**
     * Sets the value of the includeObjects property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIncludeObjects(Boolean value) {
        this.includeObjects = value;
    }

    /**
     * Gets the value of the onlyIncludeBase property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isOnlyIncludeBase() {
        return onlyIncludeBase;
    }

    /**
     * Sets the value of the onlyIncludeBase property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOnlyIncludeBase(Boolean value) {
        this.onlyIncludeBase = value;
    }

}
