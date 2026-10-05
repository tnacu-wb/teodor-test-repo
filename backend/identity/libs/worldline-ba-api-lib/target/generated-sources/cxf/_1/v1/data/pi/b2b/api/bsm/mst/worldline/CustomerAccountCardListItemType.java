
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountCardListItemType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountCardListItemType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PrimarySchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="CardId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="PAN" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CardNumberMaskedType"/&gt;
 *         &lt;element name="CardName"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="IsActivated" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="IsMyCard" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="CountRegistrations" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="ContextCan"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
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
@XmlType(name = "CustomerAccountCardListItemType", propOrder = {
    "primarySchemeCustomerId",
    "schemeCustomerId",
    "cardId",
    "pan",
    "cardName",
    "status",
    "isActivated",
    "isMyCard",
    "countRegistrations",
    "contextCan"
})
public class CustomerAccountCardListItemType {

    @XmlElement(name = "PrimarySchemeCustomerId")
    protected int primarySchemeCustomerId;
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    /**
     * This value should be used when performing any action on the card
     * 
     */
    @XmlElement(name = "CardId")
    protected int cardId;
    /**
     * This will be the PAN masked
     * 
     */
    @XmlElement(name = "PAN", required = true)
    protected String pan;
    @XmlElement(name = "CardName", required = true)
    protected String cardName;
    /**
     * Note: If status pending then card despatched is false
     * 
     */
    @XmlElement(name = "Status", required = true)
    protected String status;
    /**
     * Whether the card is activated or not
     * 
     */
    @XmlElement(name = "IsActivated")
    protected boolean isActivated;
    /**
     * Whether the is registered to the caller of the method
     * 
     */
    @XmlElement(name = "IsMyCard")
    protected boolean isMyCard;
    /**
     *  0 Card is not registered 1 Card is registered &gt;1 card is registered to n users
     * 
     */
    @XmlElement(name = "CountRegistrations")
    protected int countRegistrations;
    /**
     * Comma seperated list of actions that can be performed in the combined context of the state of account or cost centre. card and user
     *             e.g.
     *             CardActivate
     *             CardCancel
     *             CardView
     * 
     */
    @XmlElement(name = "ContextCan", required = true)
    protected String contextCan;

    /**
     * Gets the value of the primarySchemeCustomerId property.
     * 
     */
    public int getPrimarySchemeCustomerId() {
        return primarySchemeCustomerId;
    }

    /**
     * Sets the value of the primarySchemeCustomerId property.
     * 
     */
    public void setPrimarySchemeCustomerId(int value) {
        this.primarySchemeCustomerId = value;
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
     * This value should be used when performing any action on the card
     * 
     */
    public int getCardId() {
        return cardId;
    }

    /**
     * Sets the value of the cardId property.
     * 
     */
    public void setCardId(int value) {
        this.cardId = value;
    }

    /**
     * This will be the PAN masked
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAN() {
        return pan;
    }

    /**
     * Sets the value of the pan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getPAN()
     */
    public void setPAN(String value) {
        this.pan = value;
    }

    /**
     * Gets the value of the cardName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Sets the value of the cardName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardName(String value) {
        this.cardName = value;
    }

    /**
     * Note: If status pending then card despatched is false
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getStatus()
     */
    public void setStatus(String value) {
        this.status = value;
    }

    /**
     * Whether the card is activated or not
     * 
     */
    public boolean isIsActivated() {
        return isActivated;
    }

    /**
     * Sets the value of the isActivated property.
     * 
     */
    public void setIsActivated(boolean value) {
        this.isActivated = value;
    }

    /**
     * Whether the is registered to the caller of the method
     * 
     */
    public boolean isIsMyCard() {
        return isMyCard;
    }

    /**
     * Sets the value of the isMyCard property.
     * 
     */
    public void setIsMyCard(boolean value) {
        this.isMyCard = value;
    }

    /**
     *  0 Card is not registered 1 Card is registered &gt;1 card is registered to n users
     * 
     */
    public int getCountRegistrations() {
        return countRegistrations;
    }

    /**
     * Sets the value of the countRegistrations property.
     * 
     */
    public void setCountRegistrations(int value) {
        this.countRegistrations = value;
    }

    /**
     * Comma seperated list of actions that can be performed in the combined context of the state of account or cost centre. card and user
     *             e.g.
     *             CardActivate
     *             CardCancel
     *             CardView
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContextCan() {
        return contextCan;
    }

    /**
     * Sets the value of the contextCan property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getContextCan()
     */
    public void setContextCan(String value) {
        this.contextCan = value;
    }

}
