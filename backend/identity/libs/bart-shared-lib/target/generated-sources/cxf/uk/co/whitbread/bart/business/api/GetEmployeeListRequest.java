
package uk.co.whitbread.bart.business.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for GetEmployeeListRequest complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="GetEmployeeListRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="sessionID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="companyID" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="centralCardID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="fullListMarker" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="recordsPerPage" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *         &lt;element name="pageRequired" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetEmployeeListRequest", propOrder = {
    "sessionID",
    "companyID",
    "centralCardID",
    "fullListMarker",
    "recordsPerPage",
    "pageRequired"
})
public class GetEmployeeListRequest
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(required = true)
    protected String sessionID;
    @XmlElement(required = true)
    protected String companyID;
    protected String centralCardID;
    protected boolean fullListMarker;
    protected Long recordsPerPage;
    protected Long pageRequired;

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
     * Gets the value of the companyID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyID() {
        return companyID;
    }

    /**
     * Sets the value of the companyID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyID(String value) {
        this.companyID = value;
    }

    /**
     * Gets the value of the centralCardID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCentralCardID() {
        return centralCardID;
    }

    /**
     * Sets the value of the centralCardID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCentralCardID(String value) {
        this.centralCardID = value;
    }

    /**
     * Gets the value of the fullListMarker property.
     * 
     */
    public boolean isFullListMarker() {
        return fullListMarker;
    }

    /**
     * Sets the value of the fullListMarker property.
     * 
     */
    public void setFullListMarker(boolean value) {
        this.fullListMarker = value;
    }

    /**
     * Gets the value of the recordsPerPage property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getRecordsPerPage() {
        return recordsPerPage;
    }

    /**
     * Sets the value of the recordsPerPage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setRecordsPerPage(Long value) {
        this.recordsPerPage = value;
    }

    /**
     * Gets the value of the pageRequired property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPageRequired() {
        return pageRequired;
    }

    /**
     * Sets the value of the pageRequired property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPageRequired(Long value) {
        this.pageRequired = value;
    }

}
