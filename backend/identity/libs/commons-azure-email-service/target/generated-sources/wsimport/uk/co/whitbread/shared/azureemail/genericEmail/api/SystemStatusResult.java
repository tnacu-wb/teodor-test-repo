
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SystemStatusResult complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="SystemStatusResult">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Result">
 *       <sequence>
 *         <element name="SystemStatus" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SystemStatusType"/>
 *         <element name="Outages" minOccurs="0">
 *           <complexType>
 *             <complexContent>
 *               <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 <sequence>
 *                   <element name="Outage" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SystemOutage" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "SystemStatusResult", propOrder = {
    "systemStatus",
    "outages"
})
public class SystemStatusResult
    extends Result
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "SystemStatus", required = true)
    @XmlSchemaType(name = "string")
    protected SystemStatusType systemStatus;
    @XmlElement(name = "Outages")
    protected SystemStatusResult.Outages outages;

    /**
     * Gets the value of the systemStatus property.
     * 
     * @return
     *     possible object is
     *     {@link SystemStatusType }
     *     
     */
    public SystemStatusType getSystemStatus() {
        return systemStatus;
    }

    /**
     * Sets the value of the systemStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemStatusType }
     *     
     */
    public void setSystemStatus(SystemStatusType value) {
        this.systemStatus = value;
    }

    /**
     * Gets the value of the outages property.
     * 
     * @return
     *     possible object is
     *     {@link SystemStatusResult.Outages }
     *     
     */
    public SystemStatusResult.Outages getOutages() {
        return outages;
    }

    /**
     * Sets the value of the outages property.
     * 
     * @param value
     *     allowed object is
     *     {@link SystemStatusResult.Outages }
     *     
     */
    public void setOutages(SystemStatusResult.Outages value) {
        this.outages = value;
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
     *         <element name="Outage" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}SystemOutage" maxOccurs="unbounded" minOccurs="0"/>
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
        "outage"
    })
    public static class Outages
        implements Serializable
    {

        private static final long serialVersionUID = 1L;
        @XmlElement(name = "Outage")
        protected List<SystemOutage> outage;

        /**
         * Gets the value of the outage property.
         * 
         * <p>This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the outage property.</p>
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * </p>
         * <pre>
         * getOutage().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link SystemOutage }
         * </p>
         * 
         * 
         * @return
         *     The value of the outage property.
         */
        public List<SystemOutage> getOutage() {
            if (outage == null) {
                outage = new ArrayList<>();
            }
            return this.outage;
        }

    }

}
