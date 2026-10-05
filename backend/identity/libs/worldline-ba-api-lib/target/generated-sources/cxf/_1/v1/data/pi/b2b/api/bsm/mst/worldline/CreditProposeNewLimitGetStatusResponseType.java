
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * &lt;p&gt;Java class for CreditProposeNewLimitGetStatusResponseType complex type&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * 
 * &lt;pre&gt;{&#064;code
 * &lt;complexType name="CreditProposeNewLimitGetStatusResponseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{worldline.mst.bsm.api.b2b.pi.data.v1.1}ResponseType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Completed" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="OutcomeId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *         &lt;element name="AgreedNewCreditLimit" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * }&lt;/pre&gt;
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreditProposeNewLimitGetStatusResponseType", propOrder = {
    "completed",
    "outcomeId",
    "agreedNewCreditLimit"
})
public class CreditProposeNewLimitGetStatusResponseType
    extends ResponseType
{

    @XmlElement(name = "Completed")
    protected boolean completed;
    /**
     *  1=Approved, 2=Referred, 3=Rejected
     * 
     */
    @XmlElement(name = "OutcomeId")
    protected Integer outcomeId;
    @XmlElement(name = "AgreedNewCreditLimit")
    protected Integer agreedNewCreditLimit;

    /**
     * Gets the value of the completed property.
     * 
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * Sets the value of the completed property.
     * 
     */
    public void setCompleted(boolean value) {
        this.completed = value;
    }

    /**
     *  1=Approved, 2=Referred, 3=Rejected
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getOutcomeId() {
        return outcomeId;
    }

    /**
     * Sets the value of the outcomeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     * @see #getOutcomeId()
     */
    public void setOutcomeId(Integer value) {
        this.outcomeId = value;
    }

    /**
     * Gets the value of the agreedNewCreditLimit property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAgreedNewCreditLimit() {
        return agreedNewCreditLimit;
    }

    /**
     * Sets the value of the agreedNewCreditLimit property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAgreedNewCreditLimit(Integer value) {
        this.agreedNewCreditLimit = value;
    }

}
