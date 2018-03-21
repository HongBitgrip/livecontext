package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.hybris.rest.documents.ProductVariantRefDocument;
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

public class ProductVariantDeserializer extends JsonDeserializer<List<ProductVariantRefDocument>> {

  public static final String PRODUCTVARIANT_WRAPPER_PROPERTY_NAME = "variantProduct";

  @Override
  public List<ProductVariantRefDocument> deserialize(JsonParser jsonParser,
                                                     DeserializationContext deserializationContext)
          throws IOException {
    JsonNode treeNode = jsonParser.readValueAs(JsonNode.class).get(PRODUCTVARIANT_WRAPPER_PROPERTY_NAME);

    if (treeNode instanceof ArrayNode) {
      // if products > 1 then JSON is of type array
      TypeReference<List<ProductVariantRefDocument>> type = new TypeReference<List<ProductVariantRefDocument>>() {
      };

      return new ObjectMapper().readerFor(type).readValue(treeNode);
    } else {
      // if products <=1 then JSON is of type object
      ProductVariantRefDocument ProductVariantRefDocument = new ObjectMapper()
              .readerFor(ProductVariantRefDocument.class)
              .readValue(treeNode);

      return newArrayList(ProductVariantRefDocument);
    }
  }
}


