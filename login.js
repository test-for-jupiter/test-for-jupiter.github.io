// 1. Properties that do NOT require "px" when defined as numbers
const UNITLESS_CSS_PROPERTIES = {
  animationIterationCount: true,
  aspectRatio: true,
  borderImageOutset: true,
  borderImageSlice: true,
  borderImageWidth: true,
  boxFlex: true,
  boxFlexGroup: true,
  boxOrdinalGroup: true,
  columnCount: true,
  columns: true,
  flex: true,
  flexGrow: true,
  flexPositive: true,
  flexShrink: true,
  flexNegative: true,
  flexOrder: true,
  gridArea: true,
  gridRow: true,
  gridRowEnd: true,
  gridRowSpan: true,
  gridRowStart: true,
  gridColumn: true,
  gridColumnEnd: true,
  gridColumnSpan: true,
  gridColumnStart: true,
  fontWeight: true,
  lineClamp: true,
  lineHeight: true,
  opacity: true,
  order: true,
  orphans: true,
  tabSize: true,
  widows: true,
  zIndex: true,
  zoom: true,
  fillOpacity: true,
  floodOpacity: true,
  stopOpacity: true,
  strokeDasharray: true,
  strokeDashoffset: true,
  strokeMiterlimit: true,
  strokeOpacity: true,
  strokeWidth: true
};

// Add vendor prefixes for compatibility (e.g., WebkitOpacity, msFlexGrow)
const VENDOR_PREFIXES = ["Webkit", "ms", "Moz", "O"];
Object.keys(UNITLESS_CSS_PROPERTIES).forEach((prop) => {
  VENDOR_PREFIXES.forEach((prefix) => {
    const prefixedProp = prefix + prop.charAt(0).toUpperCase() + prop.substring(1);
    UNITLESS_CSS_PROPERTIES[prefixedProp] = UNITLESS_CSS_PROPERTIES[prop];
  });
});

/**
 * 2. Formats the style value, appending "px" if it is a unitless-requiring number.
 */
function formatStyleValue(name, value, isCustomProperty) {
  const isEmpty = value == null || typeof value === "boolean" || value === "";
  if (isEmpty) {
    return "";
  }

  // If it's a CSS custom property (e.g., --my-color), or not a number, or 0, 
  // or on the unitless whitelist, return it as-is.
  if (isCustomProperty || typeof value !== "number" || value === 0 || UNITLESS_CSS_PROPERTIES[name]) {
    return ("" + value).trim();
  }

  // Otherwise, append "px"
  return value + "px";
}

/**
 * 3. Applies a style object to a native DOM element.
 * @param {HTMLElement} domElement - The target HTML element.
 * @param {Object} styles - An object containing camelCase CSS properties (e.g., { fontSize: 16, color: 'red' }).
 */
window.applyStyles = function(domElement, styles) {
  if (!domElement || !styles) return;
  const elementStyle = domElement.style;

  for (const propName in styles) {
    if (styles.hasOwnProperty(propName)) {
      const isCustomProperty = propName.indexOf("--") === 0;
      const value = formatStyleValue(propName, styles[propName], isCustomProperty);

      let targetProp = propName;

      if (isCustomProperty) {
        elementStyle.setProperty(targetProp, value);
      } else {
        if (propName === "float") {
          targetProp = "cssFloat";
        }
        // Safely set standard property
        elementStyle[targetProp] = value;
      }
    }
  }
}
