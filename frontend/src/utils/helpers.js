export function generateLayerColor(existingColor, hueOffset = 0, saturationOffset = 0, lightnessOffset = -10) {
  // Extract HSL components from existing color
  const existingHSL = existingColor.match(/\d+/g).map(Number);
  let [hue, saturation, lightness] = existingHSL;

  // Adjust HSL components
  hue = (hue + hueOffset) % 360;
  saturation = Math.min(100, Math.max(0, saturation + saturationOffset));
  lightness = Math.min(90, Math.max(10, lightness + lightnessOffset)); // Restrict lightness between 10% and 90%

  // Construct and return the new color
  return `hsl(${hue}, ${saturation}%, ${lightness}%)`;
}


export function isDataTypeProperty(property){
  return property.objectProperty === undefined;
}