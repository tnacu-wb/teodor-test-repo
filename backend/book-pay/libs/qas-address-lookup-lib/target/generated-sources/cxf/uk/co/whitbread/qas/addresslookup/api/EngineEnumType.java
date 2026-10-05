
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The available engines
 * 
 * &lt;p&gt;Java class for EngineEnumType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="EngineEnumType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="Singleline"/&gt;
 *     &lt;enumeration value="Typedown"/&gt;
 *     &lt;enumeration value="Verification"/&gt;
 *     &lt;enumeration value="Keyfinder"/&gt;
 *     &lt;enumeration value="Intuitive"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "EngineEnumType")
@XmlEnum
public enum EngineEnumType {

    @XmlEnumValue("Singleline")
    SINGLELINE("Singleline"),
    @XmlEnumValue("Typedown")
    TYPEDOWN("Typedown"),
    @XmlEnumValue("Verification")
    VERIFICATION("Verification"),
    @XmlEnumValue("Keyfinder")
    KEYFINDER("Keyfinder"),
    @XmlEnumValue("Intuitive")
    INTUITIVE("Intuitive");
    private final String value;

    EngineEnumType(String v) {
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
    public static EngineEnumType fromValue(String v) {
        for (EngineEnumType c: EngineEnumType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
