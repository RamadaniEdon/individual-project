const BACKEND_URL = "http://localhost:8080";

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


export async function getUserDataFromDb(db) {
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