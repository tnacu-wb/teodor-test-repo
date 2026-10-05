
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Generic response type used as a base type for all message responses
 * 
 * &lt;p&gt;Java class for ResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ResultCode"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Errors" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ErrorInfoType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="MetaInfo" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ProcessingMetaInfoType"/&gt;
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
@XmlType(name = "ResponseType", propOrder = {
    "resultCode",
    "errors",
    "metaInfo",
    "customAttributes"
})
@XmlSeeAlso({
    CNPHostedPageInitiateResponseType.class,
    CNPHostedPageResultResponseType.class,
    CNPGetDigitsForCheckTelephoneBookingResponseType.class,
    CNPCheckTelephoneBookingBySessionIDResponseType.class,
    CNPCheckMemorableWordResponseType.class,
    CNPCheckTelephoneBookingResponseType.class,
    CNPCheckTetheredUserResponseType.class,
    CreditProposeNewLimitGetStatusResponseType.class,
    CreditProposeNewLimitResponseType.class,
    CustomerAccountCardActivateResponseType.class,
    CustomerAccountCardCancelResponseType.class,
    CustomerAccountCardInviteCardholderResponseType.class,
    CustomerAccountCardListResponseType.class,
    CustomerAccountCardRenewResponseType.class,
    CustomerAccountCardUpdateResponseType.class,
    CustomerAccountCardViewResponseType.class,
    CustomerAccountCostCentreListResponseType.class,
    CustomerAccountInvoiceDownloadResponseType.class,
    CustomerAccountInvoiceListResponseType.class,
    CustomerAccountRegisteredUserListResponseType.class,
    CustomerAccountViewCurrentBalancesResponseType.class,
    CustomerAccountViewTransactionsResponseType.class,
    EndSessionResponseType.class,
    LoginTetheredUserResponseType.class,
    RefreshSessionResponseType.class,
    RegistrationAuthenticateResponseType.class,
    RegistrationGetInfoResponseType.class,
    RegistrationSubmitResponseType.class,
    TetherByAccountNumberResponseType.class,
    TetherByCardNumberResponseType.class,
    TetheredUserDetailsGetResponseType.class,
    UpdateMemorableWordResponseType.class,
    CustomerAccountCardAddResponseType.class
})
public class ResponseType {

    /**
     * Result code.  OK means success.
     * 
     */
    @XmlElement(name = "ResultCode", required = true)
    protected String resultCode;
    @XmlElement(name = "Errors")
    protected List<ErrorInfoType> errors;
    @XmlElement(name = "MetaInfo", required = true)
    protected ProcessingMetaInfoType metaInfo;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * Result code.  OK means success.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getResultCode()
     */
    public void setResultCode(String value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the errors property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ErrorInfoType }
     * </p>
     * 
     * 
     * @return
     *     The value of the errors property.
     */
    public List<ErrorInfoType> getErrors() {
        if (errors == null) {
            errors = new ArrayList<>();
        }
        return this.errors;
    }

    /**
     * Gets the value of the metaInfo property.
     * 
     * @return
     *     possible object is
     *     {@link ProcessingMetaInfoType }
     *     
     */
    public ProcessingMetaInfoType getMetaInfo() {
        return metaInfo;
    }

    /**
     * Sets the value of the metaInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProcessingMetaInfoType }
     *     
     */
    public void setMetaInfo(ProcessingMetaInfoType value) {
        this.metaInfo = value;
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
