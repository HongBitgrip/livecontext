package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.hybris.rest.documents.PriceRefDocument;
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
 *  If NO prices specified then JSON is:
 *  "europe1Prices": null
 *  If 1 price specified, then JSON "priceRow" is an object
 *  "europe1Prices": {
 *  "priceRow": {
 *  "@pk": "8796355200031",
 *  "@uri": "http://localhost:9001/ws410/rest/pricerows/8796355200031"
 *  }
 *
 *  If n(n>1) prices specified then JSON "priceRow" is an array object:
 *  "europe1Prices": {
 *  "priceRow": [
 *   {
 *    "@pk": "8796355200031",
 *    "@uri": "http://localhost:9001/ws410/rest/pricerows/8796355200031"
 *   },
 *  {
 *    "@pk": "8796355232799",
 *    "@uri": "http://localhost:9001/ws410/rest/pricerows/8796355232799"
 *  }
 *  ]
 */
public class PriceDeserializer extends JsonDeserializer<List<PriceRefDocument>> {

  public static final String EUROPE_1_PRICES = "priceRow";

  @Override
  public List<PriceRefDocument> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
          throws IOException {
    JsonNode treeNode = jsonParser.readValueAs(JsonNode.class).get(EUROPE_1_PRICES);

    if (treeNode instanceof ArrayNode) {
      // if products > 1 then JSON is of type array
      TypeReference<List<PriceRefDocument>> type = new TypeReference<List<PriceRefDocument>>() {
      };

      return new ObjectMapper().readerFor(type).readValue(treeNode);
    } else {
      // if products <=1 then JSON is of type object
      PriceRefDocument productRefDocument = new ObjectMapper().readerFor(PriceRefDocument.class).readValue(treeNode);

      return newArrayList(productRefDocument);
    }
  }
}


