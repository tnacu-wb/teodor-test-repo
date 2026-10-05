
package worldline.mst.bsm.api.b2b.pi.data;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * 
 * 
 * &lt;p&gt;Java class for RegistrationRole&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="RegistrationRole"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="AccountHolder"/&gt;
 *     &lt;enumeration value="CardHolder"/&gt;
 *     &lt;enumeration value="CostCentre"/&gt;
 *     &lt;enumeration value="Reporting"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "RegistrationRole")
@XmlEnum
public enum RegistrationRole {

    @XmlEnumValue("AccountHolder")
    ACCOUNT_HOLDER("AccountHolder"),
    @XmlEnumValue("CardHolder")
    CARD_HOLDER("CardHolder"),
    @XmlEnumValue("CostCentre")
    COST_CENTRE("CostCentre"),
    @XmlEnumValue("Reporting")
    REPORTING("Reporting");
    private final String value;

    RegistrationRole(String v) {
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
    public static RegistrationRole fromValue(String v) {
        for (RegistrationRole c: RegistrationRole.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
