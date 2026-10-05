
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataExtensionUpdateResult complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="DataExtensionUpdateResult">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}UpdateResult">
 *       <sequence>
 *         <element name="ErrorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="KeyErrors" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="KeyError" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/>
 *                 </sequence>
 *               </restriction>
 *             </complexContent>
 *           </complexType>
 *         </element>
 *         <element name="ValueErrors" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="ValueError" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "DataExtensionUpdateResult", propOrder = {
    "errorMessage",
    "keyErrors",
    "valueErrors"
})
public class DataExtensionUpdateResult
    extends UpdateResult
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "ErrorMessage")
    protected String errorMessage;
    @XmlElement(name = "KeyErrors")
    protected DataExtensionUpdateResult.KeyErrors keyErrors;
    @XmlElement(name = "ValueErrors")
    protected DataExtensionUpdateResult.ValueErrors valueErrors;

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
     *     {@link DataExtensionUpdateResult.KeyErrors }
     *     
     */
    public DataExtensionUpdateResult.KeyErrors getKeyErrors() {
        return keyErrors;
    }

    /**
     * Sets the value of the keyErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionUpdateResult.KeyErrors }
     *     
     */
    public void setKeyErrors(DataExtensionUpdateResult.KeyErrors value) {
        this.keyErrors = value;
    }

    /**
     * Gets the value of the valueErrors property.
     * 
     * @return
     *     possible object is
     *     {@link DataExtensionUpdateResult.ValueErrors }
     *     
     */
    public DataExtensionUpdateResult.ValueErrors getValueErrors() {
        return valueErrors;
    }

    /**
     * Sets the value of the valueErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataExtensionUpdateResult.ValueErrors }
     *     
     */
    public void setValueErrors(DataExtensionUpdateResult.ValueErrors value) {
        this.valueErrors = value;
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
     *         <element name="KeyError" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/>
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
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the keyError property.</p>
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
     * <p>Java class for anonymous complex type</p>.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.</p>
     * 
     * <pre>{@code
     * <complexType>
     *   <complexContent>
     *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       <sequence>
     *         <element name="ValueError" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}DataExtensionError" maxOccurs="unbounded" minOccurs="0"/>
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
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the valueError property.</p>
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
