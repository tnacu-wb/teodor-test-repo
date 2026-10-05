
package _1.v1.data.pi.b2b.api.bsm.mst.worldline;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * Card despatch choice
 * 
 * &lt;p&gt;Java class for eDespatchChoiceType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="eDespatchChoiceType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AccountCorrespondenceAddress"/&gt;
 *     &lt;enumeration value="RegisteredUserAddress"/&gt;
 *     &lt;enumeration value="AlternativeAddress"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "eDespatchChoiceType")
@XmlEnum
public enum EDespatchChoiceType {

    @XmlEnumValue("AccountCorrespondenceAddress")
    ACCOUNT_CORRESPONDENCE_ADDRESS("AccountCorrespondenceAddress"),
    @XmlEnumValue("RegisteredUserAddress")
    REGISTERED_USER_ADDRESS("RegisteredUserAddress"),
    @XmlEnumValue("AlternativeAddress")
    ALTERNATIVE_ADDRESS("AlternativeAddress");
    private final String value;

    EDespatchChoiceType(String v) {
        value = v;
    }

    /**
     * Gets the value associated to the enum constant.
     * 
     * @return
     *     The value linked to the enum.
     */
    public String value() {
        return value;
    }

    /**
     * Gets the enum associated to the value passed as parameter.
     * 
     * @param v
     *     The value to get the enum from.
     * @return
     *     The enum which corresponds to the value, if it exists.
     * @throws IllegalArgumentException
     *     If no value matches in the enum declaration.
     */
    public static EDespatchChoiceType fromValue(String v) {
        for (EDespatchChoiceType c: EDespatchChoiceType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
