package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryRefDocument;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.List;

public class CategoryIdDeserializer extends JsonDeserializer<String> {

  public static final String CATEGORIES_WRAPPER_PROPERTY_NAME = "category";

  @Override
  public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
    JsonNode treeNode = jsonParser.readValueAs(JsonNode.class).get(CATEGORIES_WRAPPER_PROPERTY_NAME);

    if (treeNode instanceof ArrayNode) {
      // if categories > 1 then JSON is of type array
      TypeReference<List<CategoryRefDocument>> type = new TypeReference<List<CategoryRefDocument>>() {
      };

      List<CategoryRefDocument> categoryRefDocuments = new ObjectMapper().readerFor(type).readValue(treeNode);

      CategoryRefDocument firstCategoryRefDocument = categoryRefDocuments.get(0);
      return firstCategoryRefDocument.getCode();
    } else {
      // if categories <=1 then JSON is of type object
      CategoryRefDocument categoryRefDocument = new ObjectMapper()
              .readerFor(CategoryRefDocument.class)
              .readValue(treeNode);

      return categoryRefDocument.getCode();
    }
  }
}


