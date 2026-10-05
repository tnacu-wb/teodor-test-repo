
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CustomerAccountOverviewType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CustomerAccountOverviewType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PrimarySchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="AccountName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AccountNumber"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CustomAttributes" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}CustomAttributeType" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="CostCentreName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerAccountOverviewType", propOrder = {
    "primarySchemeCustomerId",
    "schemeCustomerId",
    "accountName",
    "accountNumber",
    "customAttributes",
    "costCentreName"
})
public class CustomerAccountOverviewType {

    @XmlElement(name = "PrimarySchemeCustomerId")
    protected int primarySchemeCustomerId;
    /**
     * for uk scheme priimaryschemecustomerid and scheme customer id will be the same for euro where cost centres are involvedthis would refer to the cost centre
     * 
     */
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    /**
     * The account name
     * 
     */
    @XmlElement(name = "AccountName", required = true)
    protected String accountName;
    /**
     * e.g. 3089503200066597
     * 
     */
    @XmlElement(name = "AccountNumber", required = true)
    protected String accountNumber;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;
    /**
     * The cost centre name (Eurozone only)
     * 
     */
    @XmlElement(name = "CostCentreName")
    protected String costCentreName;

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
     * for uk scheme priimaryschemecustomerid and scheme customer id will be the same for euro where cost centres are involvedthis would refer to the cost centre
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
     * The account name
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountName() {
        return accountName;
    }

    /**
     * Sets the value of the accountName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAccountName()
     */
    public void setAccountName(String value) {
        this.accountName = value;
    }

    /**
     * e.g. 3089503200066597
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the value of the accountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getAccountNumber()
     */
    public void setAccountNumber(String value) {
        this.accountNumber = value;
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

    /**
     * The cost centre name (Eurozone only)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCostCentreName() {
        return costCentreName;
    }

    /**
     * Sets the value of the costCentreName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCostCentreName()
     */
    public void setCostCentreName(String value) {
        this.costCentreName = value;
    }

}
