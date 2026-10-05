
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Card details required for a new card
 * 
 * &lt;p&gt;Java class for CustomerAccountCardBaseDetailsType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardBaseDetailsType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IsUserConsent" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="DisplayName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;minLength value="1"/&gt;
 *               &lt;maxLength value="27"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CardLimit" minOccurs="0"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="1000000"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CardUsageRestrictions" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardUsageRestrictionType" minOccurs="0"/&gt;
 *         &lt;element name="ReportingGroups" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomerAccountCardReportingGroupType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountCardBaseDetailsType", propOrder = {
    "isUserConsent",
    "displayName",
    "cardLimit",
    "cardUsageRestrictions",
    "reportingGroups"
})
@XmlSeeAlso({
    CustomerAccountCardAddType.class,
    CustomerAccountCardAllDetailsType.class,
    CustomerAccountCardUpdateType.class
})
public class CustomerAccountCardBaseDetailsType {

    @XmlElement(name = "IsUserConsent")
    protected int isUserConsent;
    /**
     * This is the name to be printed on the card changing this value will cause a new card with the same number to be issued
     * 
     */
    @XmlElement(name = "DisplayName", required = true)
    protected String displayName;
    /**
     * This is an optional limit to restrict an individual card spend in addition to the         account limit. Whichever limit is reached first will stop this card from transacting.
     * 
     */
    @XmlElementRef(name = "CardLimit", namespace = "worldline.mst.bsm.api.b2b.pi.data.v1.1", type = JAXBElement.class, required = false)
    protected JAXBElement<Integer> cardLimit;
    @XmlElement(name = "CardUsageRestrictions")
    protected CustomerAccountCardUsageRestrictionType cardUsageRestrictions;
    /**
     * not for UK scheme
     * 
     */
    @XmlElement(name = "ReportingGroups")
    protected List<CustomerAccountCardReportingGroupType> reportingGroups;

    /**
     * Gets the value of the isUserConsent property.
     * 
     */
    public int getIsUserConsent() {
        return isUserConsent;
    }

    /**
     * Sets the value of the isUserConsent property.
     * 
     */
    public void setIsUserConsent(int value) {
        this.isUserConsent = value;
    }

    /**
     * This is the name to be printed on the card changing this value will cause a new card with the same number to be issued
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getDisplayName()
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * This is an optional limit to restrict an individual card spend in addition to the         account limit. Whichever limit is reached first will stop this card from transacting.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getCardLimit() {
        return cardLimit;
    }

    /**
     * Sets the value of the cardLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     * @see #getCardLimit()
     */
    public void setCardLimit(JAXBElement<Integer> value) {
        this.cardLimit = value;
    }

    /**
     * Gets the value of the cardUsageRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAccountCardUsageRestrictionType }
     *     
     */
    public CustomerAccountCardUsageRestrictionType getCardUsageRestrictions() {
        return cardUsageRestrictions;
    }

    /**
     * Sets the value of the cardUsageRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAccountCardUsageRestrictionType }
     *     
     */
    public void setCardUsageRestrictions(CustomerAccountCardUsageRestrictionType value) {
        this.cardUsageRestrictions = value;
    }

    /**
     * not for UK scheme
     * 
     * Gets the value of the reportingGroups property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the reportingGroups property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getReportingGroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CustomerAccountCardReportingGroupType }
     * </p>
     * 
     * 
     * @return
     *     The value of the reportingGroups property.
     */
    public List<CustomerAccountCardReportingGroupType> getReportingGroups() {
        if (reportingGroups == null) {
            reportingGroups = new ArrayList<>();
        }
        return this.reportingGroups;
    }

}
