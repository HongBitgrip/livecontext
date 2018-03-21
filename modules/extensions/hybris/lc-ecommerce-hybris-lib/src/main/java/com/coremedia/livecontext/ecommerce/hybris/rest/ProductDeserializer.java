package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductRefDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Example of JSON object:
 * {
 *  "@code": "478828",
 *  "@pk": "8796094693377",
 *  "@uri": "http://localhost:9001/ws410/rest/catalogs/electronicsProductCatalog/catalogversions/Staged/products/478828"
 *  ...
 *  "supercategories": {
 *      "category": [
 *        {
 *           "@code": "578",
 *           "@pk": "8796093317262",
 *           "@uri": "http://localhost:9001/ws410/rest/catalogs/electronicsProductCatalog/catalogversions/Staged/categories/578"
 *         },
 *         {
 *           "@code": "brand_5",
 *           "@pk": "8796097806478",
 *           "@uri": "http://localhost:9001/ws410/rest/catalogs/electronicsProductCatalog/catalogversions/Staged/categories/brand_5"
 *         }
 *     ]
 *   },
 *   ...only one category....
 *   "supercategories": {
 *      "category":  {
 *           "@code": "578",
 *           "@pk": "8796093317262",
 *           "@uri": "http://localhost:9001/ws410/rest/catalogs/electronicsProductCatalog/catalogversions/Staged/categories/578"
 *         }
 *   },
 *
 *  }
 */
public class ProductDeserializer extends JsonDeserializer<List<ProductRefDocument>> {

  public static final String PRODUCTS_WRAPPER_PROPERTY_NAME = "product";

  @Override
  public List<ProductRefDocument> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
          throws IOException {
    JsonNode treeNode = jsonParser.readValueAs(JsonNode.class).get(PRODUCTS_WRAPPER_PROPERTY_NAME);

    if (treeNode instanceof ArrayNode) {
      // if products > 1 then JSON is of type array
      TypeReference<List<ProductRefDocument>> type = new TypeReference<List<ProductRefDocument>>() {
      };

      return new ObjectMapper().readerFor(type).readValue(treeNode);
    } else {
      // if products <=1 then JSON is of type object
      ProductRefDocument productRefDocument = new ObjectMapper()
              .readerFor(ProductRefDocument.class)
              .readValue(treeNode);

      return newArrayList(productRefDocument);
    }
  }
}


