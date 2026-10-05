
package exacttarget.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for DataExtensionCreateResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="DataExtensionCreateResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://exacttarget.com/wsdl/partnerAPI}CreateResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="KeyErrors" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="KeyError" type="{http://exacttarget.com/wsdl/partnerAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="ValueErrors" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ValueError" type="{http://exacttarget.com/wsdl/partnerAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataExtensionCreateResult", propOrder = {
    "errorMessage",
    "keyErrors",
    "valueErrors"
})
public class DataExtensionCreateResult
    extends CreateResult
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;
    @XmlElement(name = "KeyErrors")
    protected DataExtensionCreateResult.KeyErrors keyErrors;
    @XmlElement(name = "ValueErrors")
    protected DataExtensionCreateResult.ValueErrors valueErrors;

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

    /**
     * Gets the value of the keyErrors property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtensionCreateResult.KeyErrors }
     *     
     */
    public DataExtensionCreateResult.KeyErrors getKeyErrors() {
        return keyErrors;
    }

    /**
     * Sets the value of the keyErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionCreateResult.KeyErrors }
     *     
     */
    public void setKeyErrors(DataExtensionCreateResult.KeyErrors value) {
        this.keyErrors = value;
    }

    /**
     * Gets the value of the valueErrors property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtensionCreateResult.ValueErrors }
     *     
     */
    public DataExtensionCreateResult.ValueErrors getValueErrors() {
        return valueErrors;
    }

    /**
     * Sets the value of the valueErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionCreateResult.ValueErrors }
     *     
     */
    public void setValueErrors(DataExtensionCreateResult.ValueErrors value) {
        this.valueErrors = value;
    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="KeyError" type="{http://exacttarget.com/wsdl/partnerAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "keyError"
    })
    public static class KeyErrors
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "KeyError")
        protected List<DataExtensionError> keyError;

        /**
         * Gets the value of the keyError property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the keyError property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getKeyError().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DataExtensionError }
         * </p>
         * 
         * 
         * @return
         *     The value of the keyError property.
         */
        public List<DataExtensionError> getKeyError() {
            if (keyError == null) {
                keyError = new ArrayList<>();
            }
            return this.keyError;
        }

    }


    /**
     * &lt;p&gt;Java class for anonymous complex type&lt;/p&gt;.
     * 
     * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
     * 
     * &lt;pre&gt;{&#064;code
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="ValueError" type="{http://exacttarget.com/wsdl/partnerAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * }&lt;/pre&gt;
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "valueError"
    })
    public static class ValueErrors
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ValueError")
        protected List<DataExtensionError> valueError;

        /**
         * Gets the value of the valueError property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the valueError property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getValueError().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DataExtensionError }
         * </p>
         * 
         * 
         * @return
         *     The value of the valueError property.
         */
        public List<DataExtensionError> getValueError() {
            if (valueError == null) {
                valueError = new ArrayList<>();
            }
            return this.valueError;
        }

    }

}
