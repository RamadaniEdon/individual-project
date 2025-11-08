const BACKEND_URL = "http://localhost:8081";

// returns a Promise
function transformToJsonOrTextPromise(response) {
  const contentLength = response.headers.get("Content-Length");
  const contentType = response.headers.get("Content-Type");
  if (
    contentLength !== "0" &&
    contentType &&
    contentType.includes("application/json")
  ) {
    return response.json();
  } else {
    return response.text();
  }
}

async function sendRequest(url, { method = "GET", body, headers = {} }) {
  const options = {
    method,
    headers: new Headers({ "content-type": "application/json", ...headers }),
    body: body ? JSON.stringify(body) : null,
  };


  return fetch(url, options).then((res) => {
    const jsonOrTextPromise = transformToJsonOrTextPromise(res);

    if (res.ok) {
      return jsonOrTextPromise;
    } else {
      return jsonOrTextPromise.then(function (response) {
        const responseObject = {
          status: res.status,
          ok: false,
          message: typeof response === "string" ? response : response.message,
        };

        if (res.status === 401) {
          window.location.href = '/';
        }

        return Promise.reject(responseObject);
      });
    }
  });
}

export async function testBackend() {
  return "Hello from the backend";
  // return sendRequest(BACKEND_URL + `/ontology/helloworld`, {})
  // .then((response) => response.data);
}

export async function loginReq({ name, surname, password }) {
  return sendRequest(BACKEND_URL + `/generateToken`, {
    method: "POST",
    body: { name, surname, password },
  })
}

export async function getSchemaTypes() {
  return [
    "Person",
    "Organization",
    "Place",
    "Event",
    "Product",
  ]
}

export async function getSchemaTypeProperties(schemaType) {
  return [
    {
      property: "name",
      range: [
        {
          type: "Text",
          isDataType: true,
        },
        {
          type: "Person",
          isDataType: false,
        },
      ]
    },
  ]
}

export async function getUserDataFromDb(token) {
  return sendRequest(BACKEND_URL + `/userData`, {
    headers: { Authorization: `Bearer ${token}` },
  }).then((response) => {
    return response
  });

}

export async function getUserCategories(token) {
  return sendRequest(BACKEND_URL + `/ontologies/categories/user`, {
    headers: { Authorization: `Bearer ${token}` },
  });
}

export async function changeCategoryForData(token, collection, identifier, property, categoryName, databaseId) {
  let propertyArray = property.split(".");

  propertyArray = propertyArray.slice(1);

  property = propertyArray.join(".");
  return sendRequest(BACKEND_URL + `/userData/database/${databaseId}/accessControl`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    body: { collection, identifier, property, categoryName },
  });
}

export async function createCategory(token, newCategory) {
  console.log(newCategory)
  return sendRequest(BACKEND_URL + `/ontologies/categories`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    body: newCategory,
  });
}

export async function getClassesAndProperties() {
  return sendRequest(BACKEND_URL + `/ontologies/classesAndProperties`, {
  });
}

export async function getDatabases(token) {
  return sendRequest(BACKEND_URL + `/databases`, {
    headers: { Authorization: `Bearer ${token}` },
  });
}

export async function filterUserData(token, dbId, filters) {
  console.log(dbId, filters)
  // private String name;
    // private String property;
    // private String value;
    // private boolean bigger;
    // private boolean smaller;
    // private boolean date;

  return sendRequest(BACKEND_URL + `/userData/filter`, {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    body: {
      databaseId: dbId,
      entities: filters.map((filter) => {
        const newFilter = {
          name: filter.entityName,
          property: filter.propertyName,
          value: filter.value ? filter.value : undefined,
          bigger: filter.comparisonType === "bigger",
          smaller: filter.comparisonType === "smaller",
          date: filter.isDate,
        }
        console.log(newFilter)
        return newFilter
      }),
    },
  });
}

export async function incorporateDatabase(formData) {
  return sendRequest(BACKEND_URL + `/databases`, {
    method: "POST",
    body: formData,
  });

}

export async function getUserDataFromDb1(db) {
  return [
    {
      companyName: "Credit Card Company",
      collections: [
        {
          name: "Person",
          userData: true,
          documents: [
            [
              {
                field: "identifier",
                value: "1",
              },
              {
                field: "address",
                objectProperty: true,
                range: "PostalAddress",
                fields: [
                  {
                    field: "addressCountry",
                    value: "Kosovo"
                  },
                  {
                    field: "postalCode",
                    value: "60000"
                  }
                ]
              },
            ]
          ],
        },
        {
          name: "Order",
          userData: true,
          documents: [
            [
              {
                field: "identifier",
                value: "1",
              },
              {
                field: "customer",
                reference: true,
                referenceProperty: "Person.identifier",
                value: "1",
              },
              {
                field: "partOfInvoice",
                objectProperty: true,
                range: "Invoice",
                fields: [
                  {
                    field: "totalPaymentDue",
                    value: "100"
                  }
                ]
              }
            ]
          ],
        },
        {
          name: "OrderItem",
          userData: true,
          documents: [
            [
              {
                field: "identifier",
                value: "1",
              },
              {
                field: "order",
                reference: true,
                referenceProperty: "Order.identifier",
                value: "1",
              },
              {
                field: "orderedItem",
                reference: true,
                referenceProperty: "Product.identifier",
                value: "1",
              },
              {
                field: "orderQuantity",
                value: "4",
              },
            ],
            [
              {
                field: "identifier",
                value: "2",
              },
              {
                field: "order",
                reference: true,
                referenceProperty: "Order.identifier",
                value: "1",
              },
              {
                field: "orderedItem",
                reference: true,
                referenceProperty: "Product.identifier",
                value: "2",
              },
              {
                field: "orderQuantity",
                value: "3",
              },
            ]
          ],
        },
        {
          name: "Product",
          userData: false,
          documents: [
            [
              {
                field: "identifier",
                value: "1",
              },
              {
                field: "name",
                value: "item1",
              },
              {
                field: "description",
                value: "item1 description",
              },
              {
                field: "offers",
                objectProperty: true,
                range: "Offer",
                fields: [
                  {
                    field: "price",
                    value: "100"
                  }
                ]
              }
            ],
            [
              {
                field: "identifier",
                value: "2",
              },
              {
                field: "name",
                value: "item2",
              },
              {
                field: "description",
                value: "item2 description",
              },
              {
                field: "offers",
                objectProperty: true,
                range: "Offer",
                fields: [
                  {
                    field: "price",
                    value: "250"
                  }
                ]
              }
            ]
          ],
        }
      ]
    },
  ]
}