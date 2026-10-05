
package uk.co.whitbread.shared.azureemail.genericEmail.api;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for SMSMOEvent complex type</p>.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 * 
 * <pre>{@code
 * <complexType name="SMSMOEvent">
 *   <complexContent>
 *     <extension base="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}APIObject">
 *       <sequence>
 *         <element name="Keyword" type="{https://transact.comms.int.wtbapi.com/wsdl/emailAPI}BaseMOKeyword" minOccurs="0"/>
 *         <element name="MobileTelephoneNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="MOCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="EventDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         <element name="MOMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="MTMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="Carrier" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SMSMOEvent", propOrder = {
    "keyword",
    "mobileTelephoneNumber",
    "moCode",
    "eventDate",
    "moMessage",
    "mtMessage",
    "carrier"
})
public class SMSMOEvent
    extends APIObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "Keyword")
    protected BaseMOKeyword keyword;
    @XmlElement(name = "MobileTelephoneNumber")
    protected String mobileTelephoneNumber;
    @XmlElement(name = "MOCode")
    protected String moCode;
    @XmlElement(name = "EventDate", type = String.class)
    @XmlJavaTypeAdapter(Adapter1 .class)
    @XmlSchemaType(name = "dateTime")
    protected LocalDateTime eventDate;
    @XmlElement(name = "MOMessage")
    protected String moMessage;
    @XmlElement(name = "MTMessage")
    protected String mtMessage;
    @XmlElement(name = "Carrier")
    protected String carrier;

    /**
     * Gets the value of the keyword property.
     * 
     * @return
     *     possible object is
     *     {@link BaseMOKeyword }
     *     
     */
    public BaseMOKeyword getKeyword() {
        return keyword;
    }

    /**
     * Sets the value of the keyword property.
     * 
     * @param value
     *     allowed object is
     *     {@link BaseMOKeyword }
     *     
     */
    public void setKeyword(BaseMOKeyword value) {
        this.keyword = value;
    }

    /**
     * Gets the value of the mobileTelephoneNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMobileTelephoneNumber() {
        return mobileTelephoneNumber;
    }

    /**
     * Sets the value of the mobileTelephoneNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMobileTelephoneNumber(String value) {
        this.mobileTelephoneNumber = value;
    }

    /**
     * Gets the value of the moCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOCode() {
        return moCode;
    }

    /**
     * Sets the value of the moCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOCode(String value) {
        this.moCode = value;
    }

    /**
     * Gets the value of the eventDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public LocalDateTime getEventDate() {
        return eventDate;
    }

    /**
     * Sets the value of the eventDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEventDate(LocalDateTime value) {
        this.eventDate = value;
    }

    /**
     * Gets the value of the moMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOMessage() {
        return moMessage;
    }

    /**
     * Sets the value of the moMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOMessage(String value) {
        this.moMessage = value;
    }

    /**
     * Gets the value of the mtMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMTMessage() {
        return mtMessage;
    }

    /**
     * Sets the value of the mtMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMTMessage(String value) {
        this.mtMessage = value;
    }

    /**
     * Gets the value of the carrier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * Sets the value of the carrier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCarrier(String value) {
        this.carrier = value;
    }

}
