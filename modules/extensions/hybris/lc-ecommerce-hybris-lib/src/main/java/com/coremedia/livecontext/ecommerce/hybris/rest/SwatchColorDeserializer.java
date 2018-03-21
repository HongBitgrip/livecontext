package com.coremedia.livecontext.ecommerce.hybris.rest;

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

public class SwatchColorDeserializer extends JsonDeserializer<List<String>> {

  public static final String SWATCH_COLORS = "swatchColorEnum";

  @Override
  public List<String> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
          throws IOException {
    JsonNode treeNode = jsonParser.readValueAs(JsonNode.class).get(SWATCH_COLORS);

    if (treeNode instanceof ArrayNode) {
      // if attributes > 1 then JSON is of type array
      TypeReference<List<String>> type = new TypeReference<List<String>>() {
      };

      return new ObjectMapper().readerFor(type).readValue(treeNode);
    } else {
      // if attributes <=1 then JSON is of type object
      String attribute = new ObjectMapper().readerFor(String.class).readValue(treeNode);

      return newArrayList(attribute);
    }
  }
}
