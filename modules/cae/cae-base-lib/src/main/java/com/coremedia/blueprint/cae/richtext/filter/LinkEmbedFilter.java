package com.coremedia.blueprint.cae.richtext.filter;

import com.coremedia.id.IdProvider;
import com.coremedia.objectserver.dataviews.DataViewFactory;
import com.coremedia.objectserver.view.ViewUtils;
import com.coremedia.xml.Filter;
import com.coremedia.xml.Xlink;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Internal links that have the show attribute set to 'embed' will be rendered
 * through the ViewDispatcher, with the view 'asRichtextEmbed'.
 * <p>
 * If you use this class, make sure that the
 * {@link #hasBlockLevelView(org.xml.sax.Attributes)} method matches your
 * project templates.
 * <p>
 * The assertions in this class refer to internal logic and assume a correct
 * (i.e. wellformed) invocation of the filter methods.
 */
public class LinkEmbedFilter extends Filter implements FilterFactory {
  private static final Logger LOG = LoggerFactory.getLogger(LinkEmbedFilter.class);

  public static final String LINK_EMBED_ROLE = "linkEmbedRole";
  public static final String LINK_EMBED_CLASS_NAMES = "linkEmbedClassNames";

  private static final String EXTERNAL_START = "<![CDATA[external start]]>";
  private static final String EXTERNAL_END = "<![CDATA[external end]]>";

  private IdProvider idProvider;
  private DataViewFactory dataViewFactory;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String defaultViewName = "asRichtextEmbed";

  /**
   * Maps Xlink:show values to view names that are used for the embedded view
   */
  private Map<String, String> mappings;

  /**
   * Counter to memorize markup nesting in which link embedding is currently taking place
   */
  private int skipLevelsDuringEmbedding;
  private SaxElementStack elementStack = new SaxElementStack();

  /**
   * Buffer for attributes of the currently parsed withhold &lt;p&gt; tag.
   */
  private DelayedPData delayedP;


  @VisibleForTesting
  boolean strictNestedPCheck = false;


  // --- customize --------------------------------------------------

  /**
   * Returns a new LinkEmbedFilter.
   * <p>
   * If you override LinkEmbedFilter, you must also override this factory method
   * to return a new instance of your custom class.
   */
  protected LinkEmbedFilter createInstance() {
    return new LinkEmbedFilter();
  }

  /**
   * Returns true if the rendered result of the embedded link is a block level
   * snippet (e.g. div or p), false if it is a flow level snippet.
   * <p>
   * This implementation matches the Blueprint's default asRichtextEmbed
   * templates.  If you change the asRichtextEmbed template family, you must
   * adjust this method accordingly.
   */
  protected boolean hasBlockLevelView(Attributes atts) {
    return true;
  }


  // --- Factory ----------------------------------------------------

  /**
   * Factory Method
   *
   * @return initialized instance of {@link LinkEmbedFilter}
   */
  @Override
  public LinkEmbedFilter getInstance(HttpServletRequest request, HttpServletResponse response) {
    LinkEmbedFilter lef = createInstance();
    lef.setRequest(request);
    lef.setResponse(response);
    lef.setIdProvider(idProvider);
    lef.setDataViewFactory(dataViewFactory);

    //if no mappings are found, initialize with default.
    if (mappings == null) {
      mappings = new HashMap<>();
      mappings.put(Xlink.SHOW_EMBED, defaultViewName);
    }
    lef.setMappings(mappings);

    return lef;
  }


  // --- Filter -----------------------------------------------------

  /**
   * decide whether an &lt;a&gt; tag should be handled and if &lt;p&gt; tags need to be handled.
   *
   * @throws SAXException
   */
  @Override
  public void startElement(String namespaceUri, String localName, String qName, Attributes atts) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      if (isA(namespaceUri, localName, qName, "a") && mustEmbedLink(atts)) {
        embedLink(atts);
        ++skipLevelsDuringEmbedding;
      } else {
        startDelayed();
        startOrDelay(namespaceUri, localName, qName, atts);
      }
    } else {
      LOG.warn("Cannot handle unexpected tag " + tagDisplayName(localName, qName) + " in an <a> with mode embedded. Ignore.");
      ++skipLevelsDuringEmbedding;
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   *
   * @throws SAXException
   */
  @Override
  public void endElement(String namespaceUri, String localName, String qName) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      assert delayedP==null || isA(namespaceUri, localName, qName, "p") : "delayed P should have been executed at the opening of this " + tagDisplayName(localName, qName);
      if (delayedP==null || !delayedP.isTouched()) {
        // Regular closing of a tag
        startDelayed();
        super.endElement(namespaceUri, localName, qName);
        elementStack.pop();  // NOSONAR  Don't need the result here, but must sync the stack.
      } else {
        // Special case:
        // We have a delayed p which is to be closed now. I.e. it is empty.
        // It is also touched, i.e. it became empty by this filter.  Drop it.
        delayedP = null;
      }
    } else {
      assert skipLevelsDuringEmbedding>0 : "mismatching open/close counter while embedding a link";
      assert skipLevelsDuringEmbedding>1 || isA(namespaceUri, localName, qName, "a") : "Expected an <a> tag at parsingEmbed level 1.";
      --skipLevelsDuringEmbedding;
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   *
   * @throws SAXException
   */
  @Override
  public void characters(char[] text, int start, int length) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      startDelayed();
      super.characters(text, start, length);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   *
   * @throws SAXException
   */
  @Override
  public void ignorableWhitespace(char[] text, int start, int length) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      startDelayed();
      super.ignorableWhitespace(text, start, length);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   *
   * @throws SAXException
   */
  @Override
  public void processingInstruction(String target, String data) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      startDelayed();
      super.processingInstruction(target, data);
    }
  }

  /**
   * In case this method is called, and a &lt;p&gt; tag has been withheld, render the &lt;p&gt; tag with it's
   * original attributes. In either case, call the super implementation.
   *
   * @throws SAXException
   */
  @Override
  public void skippedEntity(String name) throws SAXException {
    if (skipLevelsDuringEmbedding==0) {
      startDelayed();
      super.skippedEntity(name);
    }
  }

  /**
   * Initialize this instance with default values when a document starts
   *
   * @throws SAXException
   */
  @Override
  public void startDocument() throws SAXException {
    skipLevelsDuringEmbedding = 0;
    delayedP = null;
    elementStack.clear();
    super.startDocument();
  }

  @Override
  public void endDocument() throws SAXException {
    assert elementStack.isEmpty() : "Stack not empty at endDocument().  This indicates a bug in the LinkEmbedFilter.";
    super.endDocument();
  }


  // --- Configuration ----------------------------------------------

  @Required
  public void setIdProvider(IdProvider idProvider) {
    this.idProvider = idProvider;
  }

  @Required
  public void setDataViewFactory(DataViewFactory dataViewFactory) {
    this.dataViewFactory = dataViewFactory;
  }

  public void setRequest(HttpServletRequest request) {
    this.request = request;
  }

  public void setResponse(HttpServletResponse response) {
    this.response = response;
  }

  public void setDefaultViewName(String defaultViewName) {
    this.defaultViewName = defaultViewName;
  }

  public void setMappings(Map<String, String> mappings) {
    this.mappings = mappings;
  }


  // --- internal ---------------------------------------------------

  /**
   * Retrieve the view to render the bean based on the show option selected.
   *
   * @param bean       - the bean that is being internally linked
   * @param showOption - show option selected e.g. 'New', 'Replace' etc.
   * @param roleOption - role option will be passed to delegated JSP, if applicable
   * @param actuate    - actuate option will be passed to delegated JSP, if applicable
   * @return the view to render the bean based on the show option selected
   */
  protected String getRenderingView(Object bean, String showOption, String roleOption, String actuate, String classNames) {
    Writer out = new StringWriter();
    String view = mappings.get(showOption);
    if (!StringUtils.isBlank(roleOption)) {
      request.setAttribute(LINK_EMBED_ROLE, roleOption);
    }
    if (StringUtils.isNotBlank(classNames)) {
      request.setAttribute(LINK_EMBED_CLASS_NAMES, classNames);
    }
    ViewUtils.render(bean, view, out, request, response);
    if (!StringUtils.isBlank(roleOption)) {
      request.removeAttribute(LINK_EMBED_ROLE);
    }
    if (StringUtils.isNotBlank(classNames)) {
      request.removeAttribute(LINK_EMBED_CLASS_NAMES);
    }
    return out.toString().trim();
  }

  /**
   * Retrieve the view to render the bean based on the show option selected.
   *
   * @param bean       - the bean that is being internally linked
   * @param showOption - show option selected e.g. 'New', 'Replace' etc.
   * @return the view to render the bean based on the show option selected
   */
  protected String getRenderingView(Object bean, String showOption) {
    return getRenderingView(bean, showOption, null, null, null);
  }

  /**
   * Retrieves the bean from the given id.
   *
   * @param id - the bean's identifier
   * @return the bean or <code>null</code> if no bean with the given id could be found
   */
  protected Object getBean(String id) {
    Object bean = idProvider.parseId(id);
    if (bean instanceof IdProvider.UnknownId) {
      // Regular case for external links, id is the foreign URL.
      // Should not happen for internal links,
      // indicates invalid content, misconfigured id schemes, ... .
      LOG.debug("There is no bean with the id {}", id);
      return null;
    }
    return dataViewFactory.loadCached(bean, null);
  }

  /**
   * If the element is a &lt;p&gt;, keep it in mind, else start it immediately.
   */
  private void startOrDelay(String uri, String localName, String qName, Attributes atts) throws SAXException {
    if (isA(uri, localName, qName, "p")) {
      assert delayedP==null : "Cannot delay nested elements";
      delayedP = new DelayedPData(new AttributesImpl(atts));
    } else {
      super.startElement(uri, localName, qName, atts);
      elementStack.push(new SaxElementData(uri, localName, qName, atts));
    }
  }

  /**
   * If there is a delayed &lt;p&gt;, start it now.
   */
  private void startDelayed() throws SAXException {
    if (delayedP!=null) {
      super.startElement("", "", "p", delayedP.getAttributes());
      elementStack.push(new SaxElementData("", "", "p", delayedP.getAttributes()));
      delayedP = null;
    }
  }

  private void embedLink(Attributes atts) throws SAXException {
    // Do we need a block context?
    boolean mustCloseP = hasBlockLevelView(atts);
    // Are we inside a p, i.e. in a flow context?
    List<SaxElementData> saxPContext = elementStack.subStack("p");
    if (saxPContext!=null && delayedP!=null) {
      logNestedP();
      mustCloseP = false;
    }

    // Mediate mismatching block/flow context for embedding
    if (mustCloseP) {
      // If there is an open p, close it temporarily.
      if (saxPContext!=null) {
        for (int i=saxPContext.size()-1; i>=0; --i) {
          SaxElementData sed = saxPContext.get(i);
          super.endElement(sed.getNamespaceUri(), sed.getLocalName(), sed.getqName());
        }
      }
      // If we are inside a delayed p, mark it as affected by link embedding.
      // Do not start it, but keep it as the state to be recovered after embedding.
      if (delayedP!=null) {
        delayedP.touch();
      }
    } else {
      // If the embedding is an inline snippet, start a delayed p.
      // If there is no delayed p, there is nothing to do, because
      // an open p or an open div are both ok for an inline snippet.
      startDelayed();
    }

    // Now embed the link result
    doEmbedLink(atts);

    // Recover the temporarily closed p context.
    // If there is a delayed p, the state is already correct.
    // If there is a saxPContext, it has to be recovered.
    if (mustCloseP && saxPContext!=null) {
      int saxPContextSize = saxPContext.size();
      if (saxPContextSize>1) {
        // reopen the temporarily closed elements (p and possible children)
        for (SaxElementData sed : saxPContext) {
          super.startElement(sed.getNamespaceUri(), sed.getLocalName(), sed.getqName(), sed.getAtts());
        }
      } else {
        // There was an open p with no children yet.
        // Recover it as delayed and touched.  If it remains empty,
        // the embed link was the last data in the original p, and this
        // second part of the splitted p is to be omitted.
        assert saxPContextSize==1 : "unexpected saxPContent size: " + saxPContextSize;
        SaxElementData sed = saxPContext.get(0);
        startOrDelay(sed.getNamespaceUri(), sed.getLocalName(), sed.getqName(), sed.getAtts());
        assert delayedP!=null : "There must be a delayedP now.";
        delayedP.touch();
        elementStack.pop();
      }
    }
  }

  private void doEmbedLink(Attributes atts) throws SAXException {
    String show = atts.getValue(Xlink.NAMESPACE_URI, Xlink.SHOW);
    String role = atts.getValue(Xlink.NAMESPACE_URI, Xlink.ROLE);
    String actuate = atts.getValue(Xlink.NAMESPACE_URI, Xlink.ACTUATE);
    String classNames = atts.getValue("class");
    Object bean = getBean(atts.getValue(Xlink.NAMESPACE_URI, Xlink.HREF));

    String outString = getRenderingView(bean, show, role, actuate, classNames);

    //if output starts with #EXTERNAL_START and ends with #EXTERNAL_END (this is contained in the view used to render the bean,
    //then this indicates that the output string should not be parsed further by the SAX Parser chain. It is therefore
    //just written by calling #characters.
    if (outString.startsWith(EXTERNAL_START) && outString.endsWith(EXTERNAL_END)) {
      String toWrite = StringUtils.substring(outString, EXTERNAL_START.length(), outString.length() - EXTERNAL_END.length());
      characters(toWrite.toCharArray(), 0, toWrite.toCharArray().length);
    } else {
      //return outString to SAX Parser chain for further processing
      raw(outString.toCharArray(), 0, outString.toCharArray().length);
    }
  }

  private boolean mustEmbedLink(Attributes atts) {
    String show = atts.getValue(Xlink.NAMESPACE_URI, Xlink.SHOW);
    if (!mappings.containsKey(show)) {
      return false;
    }
    String href = atts.getValue(Xlink.NAMESPACE_URI, Xlink.HREF);
    Object bean = getBean(href);
    if (bean!=null) {
      return true;
    } else {
      LOG.warn("{} does not denote a CMS bean, so it cannot be rendered embedded.", href);
      return false;
    }
  }

  private void logNestedP() {
    if (strictNestedPCheck) {
      // If you are definitely sure that your richtext has no nested <p>s this
      // state indicates a bug in the filter chain, possibly in this LEF itself.
      // We use this mode for unit tests.
      assert false : "Our unit test content has no nested <p>s.";
    } else {
      // Generally, an invocation of the LEF with nested <p>s is possible,
      // e.g. by errors in preceding filters or invalid richtext values
      // in the content repository.  Warn and continue.
      LOG.warn("Encountered nested paragraphs, which is invalid.");
    }
  }

  /**
   * Convenient check whether the tag matches the localName or qName of a Sax event.
   */
  private static boolean isA(String uri, String localName, String qName, String tag) {
    return "".equals(uri) ? tag.equalsIgnoreCase(qName) : tag.equalsIgnoreCase(localName);
  }

  private static String tagDisplayName(String localName, String qName) {
    return "".equals(localName) ? qName : localName;
  }

  private static class DelayedPData {
    private Attributes attributes;
    private boolean touched = false;

    public DelayedPData(Attributes attributes) {
      this.attributes = attributes;
    }

    public Attributes getAttributes() {
      return attributes;
    }

    public boolean isTouched() {
      return touched;
    }

    public void touch() {
      touched = true;
    }
  }
}

