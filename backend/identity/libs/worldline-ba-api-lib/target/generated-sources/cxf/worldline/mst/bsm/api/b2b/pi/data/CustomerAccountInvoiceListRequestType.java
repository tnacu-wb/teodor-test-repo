
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountInvoiceListRequestType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountInvoiceListRequestType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Header" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}HeaderType"/&gt;
 *         &lt;element name="TrustedPartnerCredentials" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}TrustedPartnerCredentialsType"/&gt;
 *         &lt;element name="TetheredUserGuid" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SearchCriteria" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountInvoiceListSearchCriteriaType" minOccurs="0"/&gt;
 *         &lt;element name="PagingRequestDetails" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}PagingRequestWithoutSortType"/&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountInvoiceListRequestType", propOrder = {
    "header",
    "trustedPartnerCredentials",
    "tetheredUserGuid",
    "schemeCustomerId",
    "searchCriteria",
    "pagingRequestDetails",
    "customAttributes"
})
public class CustomerAccountInvoiceListRequestType {

    @XmlElement(name = "Header", required = true)
    protected HeaderType header;
    @XmlElement(name = "TrustedPartnerCredentials", required = true)
    protected TrustedPartnerCredentialsType trustedPartnerCredentials;
    /**
     * The Guid associated with the tethered user.  Reminder: the Guid is associated with a user and NOT a card
     * 
     */
    @XmlElement(name = "TetheredUserGuid", required = true)
    protected String tetheredUserGuid;
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    @XmlElement(name = "SearchCriteria")
    protected CustomerAccountInvoiceListSearchCriteriaType searchCriteria;
    @XmlElement(name = "PagingRequestDetails", required = true)
    protected PagingRequestWithoutSortType pagingRequestDetails;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Gets the value of the header property.
     * 
     * @return
     *     possible object is
     *     {@link HeaderType }
     *     
     */
    public HeaderType getHeader() {
        return header;
    }

    /**
     * Sets the value of the header property.
     * 
     * @param value
     *     allowed object is
     *     {@link HeaderType }
     *     
     */
    public void setHeader(HeaderType value) {
        this.header = value;
    }

    /**
     * Gets the value of the trustedPartnerCredentials property.
     * 
     * @return
     *     possible object is
     *     {@link TrustedPartnerCredentialsType }
     *     
     */
    public TrustedPartnerCredentialsType getTrustedPartnerCredentials() {
        return trustedPartnerCredentials;
    }

    /**
     * Sets the value of the trustedPartnerCredentials property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrustedPartnerCredentialsType }
     *     
     */
    public void setTrustedPartnerCredentials(TrustedPartnerCredentialsType value) {
        this.trustedPartnerCredentials = value;
    }

    /**
     * The Guid associated with the tethered user.  Reminder: the Guid is associated with a user and NOT a card
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTetheredUserGuid() {
        return tetheredUserGuid;
    }

    /**
     * Sets the value of the tetheredUserGuid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getTetheredUserGuid()
     */
    public void setTetheredUserGuid(String value) {
        this.tetheredUserGuid = value;
    }

    /**
     * Gets the value of the schemeCustomerId property.
     * 
     */
    public int getSchemeCustomerId() {
        return schemeCustomerId;
    }

    /**
     * Sets the value of the schemeCustomerId property.
     * 
     */
    public void setSchemeCustomerId(int value) {
        this.schemeCustomerId = value;
    }

    /**
     * Gets the value of the searchCriteria property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountInvoiceListSearchCriteriaType }
     *     
     */
    public CustomerAccountInvoiceListSearchCriteriaType getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * Sets the value of the searchCriteria property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountInvoiceListSearchCriteriaType }
     *     
     */
    public void setSearchCriteria(CustomerAccountInvoiceListSearchCriteriaType value) {
        this.searchCriteria = value;
    }

    /**
     * Gets the value of the pagingRequestDetails property.
     * 
     * @return
     *     possible object is
     *     {@link PagingRequestWithoutSortType }
     *     
     */
    public PagingRequestWithoutSortType getPagingRequestDetails() {
        return pagingRequestDetails;
    }

    /**
     * Sets the value of the pagingRequestDetails property.
     * 
     * @param value
     *     allowed object is
     *     {@link PagingRequestWithoutSortType }
     *     
     */
    public void setPagingRequestDetails(PagingRequestWithoutSortType value) {
        this.pagingRequestDetails = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the customAttributes property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getCustomAttributes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomAttributeType }
     * </p>
     * 
     * 
     * @return
     *     The value of the customAttributes property.
     */
    public List<CustomAttributeType> getCustomAttributes() {
        if (customAttributes == null) {
            customAttributes = new ArrayList<>();
        }
        return this.customAttributes;
    }

}
