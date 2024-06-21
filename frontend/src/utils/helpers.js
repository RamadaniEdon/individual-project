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
  return !property.objectProperty;
}

export function getReferenceClassProperty(database, className, property){
  let result;
  database.collections.forEach((collection) => {
    if(collection.nameInDb == className){
      collection.documents[0].forEach((field) => {
        if(field.dbField == property){
          result = collection.name + "." + field.field
        }
      })
    }
  })
  return result;
}

export function sortObjectsByAttribute(objects, attribute) {
  return objects.sort((a, b) => {
    const valueA = a[attribute];
    const valueB = b[attribute];

    if (typeof valueA === 'number' && typeof valueB === 'number') {
      return valueA - valueB;
    } else if (typeof valueA === 'string' && typeof valueB === 'string') {
      return valueA.localeCompare(valueB);
    }
    return 0;
  });
}