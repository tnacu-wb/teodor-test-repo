
package worldline.mst.bsm.api.b2b.pi.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for RegistrationCodeInfoType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="RegistrationCodeInfoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistrationCode"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *               &lt;maxLength value="19"/&gt;
 *               &lt;minLength value="1"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="PrimarySchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="SchemeCustomerId" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="RegistrationRole"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *               &lt;whiteSpace value="collapse"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="AuthenticationQuestions" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}RegistrationAuthenticationQuestionType" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "RegistrationCodeInfoType", propOrder = {
    "registrationCode",
    "primarySchemeCustomerId",
    "schemeCustomerId",
    "registrationRole",
    "authenticationQuestions",
    "customAttributes"
})
public class RegistrationCodeInfoType {

    /**
     * The verified registration code
     * 
     */
    @XmlElement(name = "RegistrationCode", required = true)
    protected String registrationCode;
    /**
     * Worldline customerId of the overall account
     * 
     */
    @XmlElement(name = "PrimarySchemeCustomerId")
    protected int primarySchemeCustomerId;
    /**
     * When a cost centre this will be populated with a *different* value to PrimarySchemeCustomerId otherwise will be identical
     * 
     */
    @XmlElement(name = "SchemeCustomerId")
    protected int schemeCustomerId;
    /**
     * The role that the registration code relates to
     * 
     */
    @XmlElement(name = "RegistrationRole", required = true)
    protected String registrationRole;
    /**
     * Authentication questions.  There could be none, 1 or many of these
     * 
     */
    @XmlElement(name = "AuthenticationQuestions")
    protected List<RegistrationAuthenticationQuestionType> authenticationQuestions;
    @XmlElement(name = "CustomAttributes")
    protected List<CustomAttributeType> customAttributes;

    /**
     * The verified registration code
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the value of the registrationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getRegistrationCode()
     */
    public void setRegistrationCode(String value) {
        this.registrationCode = value;
    }

    /**
     * Worldline customerId of the overall account
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
     * When a cost centre this will be populated with a *different* value to PrimarySchemeCustomerId otherwise will be identical
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
     * The role that the registration code relates to
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistrationRole() {
        return registrationRole;
    }

    /**
     * Sets the value of the registrationRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getRegistrationRole()
     */
    public void setRegistrationRole(String value) {
        this.registrationRole = value;
    }

    /**
     * Authentication questions.  There could be none, 1 or many of these
     * 
     * Gets the value of the authenticationQuestions property.
     * 
     * <p>This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the Jakarta XML Binding object.
     * This is why there is not a {@code set} method for the authenticationQuestions property.</p>
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * </p>
     * <pre>
     * getAuthenticationQuestions().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistrationAuthenticationQuestionType }
     * </p>
     * 
     * 
     * @return
     *     The value of the authenticationQuestions property.
     */
    public List<RegistrationAuthenticationQuestionType> getAuthenticationQuestions() {
        if (authenticationQuestions == null) {
            authenticationQuestions = new ArrayList<>();
        }
        return this.authenticationQuestions;
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
