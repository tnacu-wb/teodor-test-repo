
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DoubleOptInMOKeyword complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="DoubleOptInMOKeyword">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}BaseMOKeyword">
 *       <sequence>
 *         <element name="DefaultPublication" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}List"/>
 *         <element name="InvalidPublicationMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="InvalidResponseMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="MissingPublicationMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="NeedPublicationMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="PromptMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="SuccessMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="UnexpectedErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         <element name="ValidPublications">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="ValidPublication" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}List" maxOccurs="unbounded"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="ValidResponses">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="ValidResponse" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoubleOptInMOKeyword", propOrder = {
    "defaultPublication",
    "invalidPublicationMessage",
    "invalidResponseMessage",
    "missingPublicationMessage",
    "needPublicationMessage",
    "promptMessage",
    "successMessage",
    "unexpectedErrorMessage",
    "validPublications",
    "validResponses"
})
public class DoubleOptInMOKeyword
    extends BaseMOKeyword
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "DefaultPublication", required = true)
    protected uk.co.whitbread.shared.azureemail.genericEmail.api.List defaultPublication;
    @XmlElement(name = "InvalidPublicationMessage", required = true)
    protected String invalidPublicationMessage;
    @XmlElement(name = "InvalidResponseMessage", required = true)
    protected String invalidResponseMessage;
    @XmlElement(name = "MissingPublicationMessage", required = true)
    protected String missingPublicationMessage;
    @XmlElement(name = "NeedPublicationMessage", required = true)
    protected String needPublicationMessage;
    @XmlElement(name = "PromptMessage", required = true)
    protected String promptMessage;
    @XmlElement(name = "SuccessMessage", required = true)
    protected String successMessage;
    @XmlElement(name = "UnexpectedErrorMessage", required = true)
    protected String unexpectedErrorMessage;
    @XmlElement(name = "ValidPublications", required = true)
    protected DoubleOptInMOKeyword.ValidPublications validPublications;
    @XmlElement(name = "ValidResponses", required = true)
    protected DoubleOptInMOKeyword.ValidResponses validResponses;

    /**
     * Gets the value of the defaultPublication property.
     * 
     * @return
     *     possible object is
     *     {@link uk.co.whitbread.shared.azureemail.genericEmail.api.List }
     *     
     */
    public uk.co.whitbread.shared.azureemail.genericEmail.api.List getDefaultPublication() {
        return defaultPublication;
    }

    /**
     * Sets the value of the defaultPublication property.
     * 
     * @param value
     *     allowed object is
     *     {@link uk.co.whitbread.shared.azureemail.genericEmail.api.List }
     *     
     */
    public void setDefaultPublication(uk.co.whitbread.shared.azureemail.genericEmail.api.List value) {
        this.defaultPublication = value;
    }

    /**
     * Gets the value of the invalidPublicationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalidPublicationMessage() {
        return invalidPublicationMessage;
    }

    /**
     * Sets the value of the invalidPublicationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalidPublicationMessage(String value) {
        this.invalidPublicationMessage = value;
    }

    /**
     * Gets the value of the invalidResponseMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInvalidResponseMessage() {
        return invalidResponseMessage;
    }

    /**
     * Sets the value of the invalidResponseMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInvalidResponseMessage(String value) {
        this.invalidResponseMessage = value;
    }

    /**
     * Gets the value of the missingPublicationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMissingPublicationMessage() {
        return missingPublicationMessage;
    }

    /**
     * Sets the value of the missingPublicationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMissingPublicationMessage(String value) {
        this.missingPublicationMessage = value;
    }

    /**
     * Gets the value of the needPublicationMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNeedPublicationMessage() {
        return needPublicationMessage;
    }

    /**
     * Sets the value of the needPublicationMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNeedPublicationMessage(String value) {
        this.needPublicationMessage = value;
    }

    /**
     * Gets the value of the promptMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPromptMessage() {
        return promptMessage;
    }

    /**
     * Sets the value of the promptMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPromptMessage(String value) {
        this.promptMessage = value;
    }

    /**
     * Gets the value of the successMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuccessMessage() {
        return successMessage;
    }

    /**
     * Sets the value of the successMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuccessMessage(String value) {
        this.successMessage = value;
    }

    /**
     * Gets the value of the unexpectedErrorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnexpectedErrorMessage() {
        return unexpectedErrorMessage;
    }

    /**
     * Sets the value of the unexpectedErrorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnexpectedErrorMessage(String value) {
        this.unexpectedErrorMessage = value;
    }

    /**
     * Gets the value of the validPublications property.
     * 
     * @return
     *     possible object is
     *     {@link DoubleOptInMOKeyword.ValidPublications }
     *     
     */
    public DoubleOptInMOKeyword.ValidPublications getValidPublications() {
        return validPublications;
    }

    /**
     * Sets the value of the validPublications property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoubleOptInMOKeyword.ValidPublications }
     *     
     */
    public void setValidPublications(DoubleOptInMOKeyword.ValidPublications value) {
        this.validPublications = value;
    }

    /**
     * Gets the value of the validResponses property.
     * 
     * @return
     *     possible object is
     *     {@link DoubleOptInMOKeyword.ValidResponses }
     *     
     */
    public DoubleOptInMOKeyword.ValidResponses getValidResponses() {
        return validResponses;
    }

    /**
     * Sets the value of the validResponses property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoubleOptInMOKeyword.ValidResponses }
     *     
     */
    public void setValidResponses(DoubleOptInMOKeyword.ValidResponses value) {
        this.validResponses = value;
    }


    /**
     * <p>Java class for anonymous complex type</p>.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="ValidPublication" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}List" maxOccurs="unbounded"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "validPublication"
    })
    public static class ValidPublications
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ValidPublication", required = true)
        protected java.util.List<uk.co.whitbread.shared.azureemail.genericEmail.api.List> validPublication;

        /**
         * Gets the value of the validPublication property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the validPublication property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getValidPublication().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link uk.co.whitbread.shared.azureemail.genericEmail.api.List }
         * </p>
         * 
         * 
         * @return
         *     The value of the validPublication property.
         */
        public java.util.List<uk.co.whitbread.shared.azureemail.genericEmail.api.List> getValidPublication() {
            if (validPublication == null) {
                validPublication = new ArrayList<>();
            }
            return this.validPublication;
        }

    }


    /**
     * <p>Java class for anonymous complex type</p>.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="ValidResponse" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
     *       </sequence>
     *     </restriction>
     *   </complexContent>
     * </complexType>
     * }</pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "validResponse"
    })
    public static class ValidResponses
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "ValidResponse", required = true)
        protected java.util.List<String> validResponse;

        /**
         * Gets the value of the validResponse property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the validResponse property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getValidResponse().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * </p>
         * 
         * 
         * @return
         *     The value of the validResponse property.
         */
        public java.util.List<String> getValidResponse() {
            if (validResponse == null) {
                validResponse = new ArrayList<>();
            }
            return this.validResponse;
        }

    }

}
