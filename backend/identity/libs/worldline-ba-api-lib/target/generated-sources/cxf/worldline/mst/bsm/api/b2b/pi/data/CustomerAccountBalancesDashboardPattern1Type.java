
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountBalancesDashboardPattern1Type complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountBalancesDashboardPattern1Type"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="Outstanding" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="NewTransactions" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="Available" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CreditLimit" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="CurrentBalance" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
 *         &lt;element name="InterimPayments" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CurrencyType"/&gt;
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
@XmlType(name = "CustomerAccountBalancesDashboardPattern1Type", propOrder = {
    "schemeCustomerId",
    "outstanding",
    "newTransactions",
    "available",
    "creditLimit",
    "currentBalance",
    "interimPayments",
    "customAttributes"
})
public class CustomerAccountBalancesDashboardPattern1Type {

    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    @XmlElement(name = "Outstanding", required = true)
    protected CurrencyType outstanding;
    @XmlElement(name = "NewTransactions", required = true)
    protected CurrencyType newTransactions;
    @XmlElement(name = "Available", required = true)
    protected CurrencyType available;
    @XmlElement(name = "CreditLimit", required = true)
    protected CurrencyType creditLimit;
    @XmlElement(name = "CurrentBalance", required = true)
    protected CurrencyType currentBalance;
    @XmlElement(name = "InterimPayments", required = true)
    protected CurrencyType interimPayments;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

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
     * Gets the value of the outstanding property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getOutstanding() {
        return outstanding;
    }

    /**
     * Sets the value of the outstanding property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setOutstanding(CurrencyType value) {
        this.outstanding = value;
    }

    /**
     * Gets the value of the newTransactions property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getNewTransactions() {
        return newTransactions;
    }

    /**
     * Sets the value of the newTransactions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setNewTransactions(CurrencyType value) {
        this.newTransactions = value;
    }

    /**
     * Gets the value of the available property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getAvailable() {
        return available;
    }

    /**
     * Sets the value of the available property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setAvailable(CurrencyType value) {
        this.available = value;
    }

    /**
     * Gets the value of the creditLimit property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the value of the creditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCreditLimit(CurrencyType value) {
        this.creditLimit = value;
    }

    /**
     * Gets the value of the currentBalance property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getCurrentBalance() {
        return currentBalance;
    }

    /**
     * Sets the value of the currentBalance property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setCurrentBalance(CurrencyType value) {
        this.currentBalance = value;
    }

    /**
     * Gets the value of the interimPayments property.
     * 
     * @return
     *     possible object is
     *     {@link CurrencyType }
     *     
     */
    public CurrencyType getInterimPayments() {
        return interimPayments;
    }

    /**
     * Sets the value of the interimPayments property.
     * 
     * @param value
     *     allowed object is
     *     {@link CurrencyType }
     *     
     */
    public void setInterimPayments(CurrencyType value) {
        this.interimPayments = value;
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
