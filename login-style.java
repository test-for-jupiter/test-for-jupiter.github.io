import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * StyleApplier matches the React/JS engine's appearance logic.
 * It takes style properties, automatically appends "px" to numeric sizes, 
 * respects unitless CSS properties (like opacity or zIndex), and outputs 
 * a clean inline CSS string.
 */
public class StyleApplier {

    // Set of CSS properties that do NOT require "px" units when defined as numbers
    private static final Set<String> UNITLESS_PROPERTIES = new HashSet<>();

    static {
        String[] rawUnitless = {
            "animationIterationCount", "aspectRatio", "borderImageOutset", "borderImageSlice", 
            "borderImageWidth", "boxFlex", "boxFlexGroup", "boxOrdinalGroup", "columnCount", 
            "columns", "flex", "flexGrow", "flexPositive", "flexShrink", "flexNegative", 
            "flexOrder", "gridArea", "gridRow", "gridRowEnd", "gridRowSpan", "gridRowStart", 
            "gridColumn", "gridColumnEnd", "gridColumnSpan", "gridColumnStart", "fontWeight", 
            "lineClamp", "lineHeight", "opacity", "order", "orphans", "tabSize", "widows", 
            "zIndex", "zoom", "fillOpacity", "floodOpacity", "stopOpacity", "strokeDasharray", 
            "strokeDashoffset", "strokeMiterlimit", "strokeOpacity", "strokeWidth"
        };
        
        for (String prop : rawUnitless) {
            UNITLESS_PROPERTIES.add(prop);
            // Auto-generate common vendor prefixes (e.g., WebkitOpacity, msFlexGrow)
            UNITLESS_PROPERTIES.add("Webkit" + capitalize(prop));
            UNITLESS_PROPERTIES.add("ms" + capitalize(prop));
            UNITLESS_PROPERTIES.add("Moz" + capitalize(prop));
            UNITLESS_PROPERTIES.add("O" + capitalize(prop));
        }
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Formats individual style values. 
     * Appends "px" to numbers unless they are unitless (like z-index) or CSS custom variables.
     */
    public static String formatStyleValue(String name, Object value) {
        if (value == null || value instanceof Boolean || value.toString().isEmpty()) {
            return "";
        }

        boolean isCustomProperty = name.startsWith("--");

        if (value instanceof Number) {
            double numValue = ((Number) value).doubleValue();
            if (numValue == 0 || isCustomProperty || UNITLESS_PROPERTIES.contains(name)) {
                // If it's a whole number, drop the decimal point (e.g., 10 instead of 10.0)
                if (numValue == (long) numValue) {
                    return String.valueOf((long) numValue);
                }
                return String.valueOf(numValue);
            }
            
            // Append "px" to non-unitless numbers
            if (numValue == (long) numValue) {
                return (long) numValue + "px";
            }
            return numValue + "px";
        }

        return value.toString().trim();
    }

    /**
     * Converts a Map of camelCase styles into a formatted inline CSS string.
     * Example: { backgroundColor: "red", width: 150 } -> "background-color: red; width: 150px;"
     */
    public static String buildInlineStyleString(Map<String, Object> styles) {
        StringBuilder styleBuilder = new StringBuilder();

        for (Map.Entry<String, Object> entry : styles.entrySet()) {
            String propName = entry.getKey();
            Object value = entry.getValue();

            String formattedValue = formatStyleValue(propName, value);
            if (!formattedValue.isEmpty()) {
                // Convert camelCase keys to CSS kebab-case (e.g., backgroundColor -> background-color)
                String cssName = propName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
                
                if ("float".equals(cssName)) {
                    cssName = "css-float";
                }

                styleBuilder.append(cssName).append(": ").append(formattedValue).append("; ");
            }
        }

        return styleBuilder.toString().trim();
    }
}
