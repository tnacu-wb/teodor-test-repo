
package uk.co.whitbread.azure.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for ContentValidationTaskResult complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="ContentValidationTaskResult"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}TaskResult"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ValidationResults" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="ValidationResult" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ValidationResult" maxOccurs="unbounded" minOccurs="0"/&gt;
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
@XmlType(name = "ContentValidationTaskResult", propOrder = {
    "validationResults"
})
public class ContentValidationTaskResult
    extends TaskResult
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ValidationResults")
    protected ContentValidationTaskResult.ValidationResults validationResults;

    /**
     * Gets the value of the validationResults property.
     * 
     * @return
     *     possible object is
     *     {@link ContentValidationTaskResult.ValidationResults }
     *     
     */
    public ContentValidationTaskResult.ValidationResults getValidationResults() {
        return validationResults;
    }

    /**
     * Sets the value of the validationResults property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContentValidationTaskResult.ValidationResults }
     *     
     */
    public void setValidationResults(ContentValidationTaskResult.ValidationResults value) {
        this.validationResults = value;
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
     *         &lt;element name="ValidationResult" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}ValidationResult" maxOccurs="unbounded" minOccurs="0"/&gt;
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
        "validationResult"
    })
    public static class ValidationResults
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ValidationResult")
        protected List<ValidationResult> validationResult;

        /**
         * Gets the value of the validationResult property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the Jakarta XML Binding object.
         * This is why there is not a {@code set} method for the validationResult property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getValidationResult().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ValidationResult }
         * </p>
         * 
         * 
         * @return
         *     The value of the validationResult property.
         */
        public List<ValidationResult> getValidationResult() {
            if (validationResult == null) {
                validationResult = new ArrayList<>();
            }
            return this.validationResult;
        }

    }

}
