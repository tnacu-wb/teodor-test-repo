
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CNPGetDigitsForCheckTelephoneBookingResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CNPGetDigitsForCheckTelephoneBookingResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CNPSessionID" type="{worldline.mst.bsm.api.b2b.pi.data.v1.1}GuidType"/&gt;
 *         &lt;element name="Char1Position"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Char2Position"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="1"/&gt;
 *               &lt;maxInclusive value="50"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
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
@XmlType(name = "CNPGetDigitsForCheckTelephoneBookingResponseType", propOrder = {
    "cnpSessionID",
    "char1Position",
    "char2Position"
})
public class CNPGetDigitsForCheckTelephoneBookingResponseType
    extends ResponseType
{

    /**
     * New SessionID created by calling this method
     * 
     */
    @XmlElement(name = "CNPSessionID", required = true)
    protected String cnpSessionID;
    /**
     * Position of first character to ask for (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char1Position")
    protected int char1Position;
    /**
     * Position of second character to ask for (of memorable word OR password)
     * 
     */
    @XmlElement(name = "Char2Position")
    protected int char2Position;

    /**
     * New SessionID created by calling this method
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCNPSessionID() {
        return cnpSessionID;
    }

    /**
     * Sets the value of the cnpSessionID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     * @see #getCNPSessionID()
     */
    public void setCNPSessionID(String value) {
        this.cnpSessionID = value;
    }

    /**
     * Position of first character to ask for (of memorable word OR password)
     * 
     */
    public int getChar1Position() {
        return char1Position;
    }

    /**
     * Sets the value of the char1Position property.
     * 
     */
    public void setChar1Position(int value) {
        this.char1Position = value;
    }

    /**
     * Position of second character to ask for (of memorable word OR password)
     * 
     */
    public int getChar2Position() {
        return char2Position;
    }

    /**
     * Sets the value of the char2Position property.
     * 
     */
    public void setChar2Position(int value) {
        this.char2Position = value;
    }

}
