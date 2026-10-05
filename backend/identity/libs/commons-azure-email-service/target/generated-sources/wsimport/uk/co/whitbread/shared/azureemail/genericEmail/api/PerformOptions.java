
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PerformOptions complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="PerformOptions">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}Options">
 *       <sequence>
 *         <element name="Explanation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ProgramActivityInstanceID" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}instanceid" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PerformOptions", propOrder = {
    "explanation",
    "programActivityInstanceID"
})
@XmlSeeAlso({
    CampaignPerformOptions.class
})
public class PerformOptions
    extends Options
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Explanation")
    protected String explanation;
    @XmlElement(name = "ProgramActivityInstanceID")
    protected String programActivityInstanceID;

    /**
     * Gets the value of the explanation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * Sets the value of the explanation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExplanation(String value) {
        this.explanation = value;
    }

    /**
     * Gets the value of the programActivityInstanceID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProgramActivityInstanceID() {
        return programActivityInstanceID;
    }

    /**
     * Sets the value of the programActivityInstanceID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProgramActivityInstanceID(String value) {
        this.programActivityInstanceID = value;
    }

}
