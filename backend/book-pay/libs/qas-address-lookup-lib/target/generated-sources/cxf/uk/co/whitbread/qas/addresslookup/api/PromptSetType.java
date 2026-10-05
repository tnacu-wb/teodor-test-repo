
package uk.co.whitbread.qas.addresslookup.api;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * The available prompt sets
 * 
 * &lt;p&gt;Java class for PromptSetType&lt;/p&gt;.
 * 
 * &lt;p&gt;The following schema fragment specifies the expected content contained within this class.&lt;/p&gt;
 * &lt;pre&gt;{&#064;code
 * &lt;simpleType name="PromptSetType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OneLine"/&gt;
 *     &lt;enumeration value="Default"/&gt;
 *     &lt;enumeration value="Generic"/&gt;
 *     &lt;enumeration value="Optimal"/&gt;
 *     &lt;enumeration value="Alternate"/&gt;
 *     &lt;enumeration value="Alternate2"/&gt;
 *     &lt;enumeration value="Alternate3"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * }&lt;/pre&gt;
 * 
 */
@XmlType(name = "PromptSetType")
@XmlEnum
public enum PromptSetType {

    @XmlEnumValue("OneLine")
    ONE_LINE("OneLine"),
    @XmlEnumValue("Default")
    DEFAULT("Default"),
    @XmlEnumValue("Generic")
    GENERIC("Generic"),
    @XmlEnumValue("Optimal")
    OPTIMAL("Optimal"),
    @XmlEnumValue("Alternate")
    ALTERNATE("Alternate"),
    @XmlEnumValue("Alternate2")
    ALTERNATE_2("Alternate2"),
    @XmlEnumValue("Alternate3")
    ALTERNATE_3("Alternate3");
    private final String value;

    PromptSetType(String v) {
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
    public static PromptSetType fromValue(String v) {
        for (PromptSetType c: PromptSetType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
