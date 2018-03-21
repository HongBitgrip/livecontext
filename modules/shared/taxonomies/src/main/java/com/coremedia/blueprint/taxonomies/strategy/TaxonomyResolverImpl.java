package com.coremedia.blueprint.taxonomies.strategy;

import com.coremedia.blueprint.taxonomies.Taxonomy;
import com.coremedia.blueprint.taxonomies.TaxonomyResolver;
import com.coremedia.blueprint.taxonomies.TaxonomyUtil;
import com.coremedia.cap.common.CapObjectDestroyedException;
import com.coremedia.cap.common.CapSession;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentException;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteDestroyedException;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.rest.cap.content.search.solr.SolrSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Concrete implementation of the ITaxonomyResolver.
 */
public class TaxonomyResolverImpl implements TaxonomyResolver, InitializingBean {
  private static final Logger LOG = LoggerFactory.getLogger(TaxonomyResolverImpl.class);
  private static final String TAXONOMY_FOLDER_NAME = "Taxonomies";

  private ContentRepository contentRepository;
  private SolrSearchService solrSearchService;
  private Map<String, Taxonomy> strategies;
  private Map<String, String> aliasMapping;
  private SitesService sitesService;
  private String contentType;
  private String siteConfigPath;
  private String globalConfigPath;

  //--- Spring configuration --
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setSolrSearchService(SolrSearchService solrSearchService) {
    this.solrSearchService = solrSearchService;
  }

  @Required
  public void setAliasMapping(Map<String, String> aliasMapping) {
    this.aliasMapping = aliasMapping;
  }

  @Required
  public void setSiteConfigPath(String siteConfigPath) {
    this.siteConfigPath = siteConfigPath;
  }

  @Required
  public void setGlobalConfigPath(String globalConfigPath) {
    this.globalConfigPath = globalConfigPath;
  }

  // -- Impl ------

  @Override
  public Collection<Taxonomy> getTaxonomies() {
    return strategies.values();
  }

  @Override
  public boolean reload() {
    loadTaxonomies();
    return true;
  }

  @Required
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  @Override
  public Taxonomy getTaxonomy(String siteId, String taxonomyId) {
    Taxonomy taxonomy = findTaxonomy(siteId, taxonomyId);
    //run validity check.
    if (taxonomy != null && !taxonomy.isValid()) {
      String key = toKey(taxonomy.getTaxonomyId(), taxonomy.getSiteId());
      if (strategies.containsKey(key)) {
        strategies.remove(key);
      }
    }
    return taxonomy;
  }


  @Override
  public void afterPropertiesSet() {
    strategies = Collections.synchronizedMap(new HashMap<String, Taxonomy>());
    loadTaxonomies();
  }

  /**
   * Builds the key that is put into the strategies map.
   *
   * @param taxonomyId The id of the taxonomy
   * @param site       The site the taxonomy is used for or null.
   * @return The key of the taxonomy.
   */
  protected String toKey(String taxonomyId, String site) {
    return taxonomyId + "_" + site;
  }

  /**
   * Detects available taxonomy trees.
   */
  private void loadTaxonomies() {
    Map<String,Taxonomy> newTaxonomies = new HashMap<>();
    Set<Site> sites = sitesService.getSites();
    for (Site site : sites) {
      try {
        Content siteTaxonomyFolder = getSiteConfigFolder(site);
        addStrategies(newTaxonomies, siteTaxonomyFolder, site.getId());
      } catch (CapObjectDestroyedException | SiteDestroyedException e) {
        LOG.debug("ignoring destroyed site '{}'", site.getId(), e);
      }
    }

    Content globalConfigFolder = getGlobalConfigFolder();
    addStrategies(newTaxonomies, globalConfigFolder, null);

    strategies.clear();
    strategies.putAll(newTaxonomies);
  }

  private void addStrategies(Map<String, Taxonomy> newTaxonomies, Content taxonomyRootFolder, String id) {
    if (taxonomyRootFolder != null) {
      Content taxonomyFolder = taxonomyRootFolder.getChild(TAXONOMY_FOLDER_NAME);
      if (taxonomyFolder != null) {
        Map<String, Taxonomy> taxonomies = createStrategies(taxonomyFolder, id);
        newTaxonomies.putAll(taxonomies);
      }
    }
  }

  /**
   * Returns the site configuration folder or
   * null if that one is not readable.
   */
  private Content getSiteConfigFolder(Site site) {
    CapSession originalSession = contentRepository.getConnection().getConnectionSession().activate();
    try {
      return site.getSiteRootFolder().getChild(siteConfigPath);
    } catch (ContentException e) {
      LOG.error("Failed to read site config folder " + siteConfigPath+ " for site " + site.getSiteRootFolder().getPath() + ": " + e.getMessage());
    }
    finally {
      originalSession.activate();
    }
    return null;
  }

  /**
   * Returns the global configuration folder or
   * null if that one is not readable.
   */
  private Content getGlobalConfigFolder() {
    CapSession originalSession = contentRepository.getConnection().getConnectionSession().activate();
    try {
      return contentRepository.getChild(globalConfigPath);
    } catch (ContentException e) {
      LOG.error("Failed to read " + globalConfigPath + ": " + e.getMessage());
    }
    finally {
      originalSession.activate();
    }
    return null;
  }


  /**
   * Creates a taxonomy instance for the given (maybe side-depending) folder.
   *
   * @param taxFolderContent The folder to lookup keywords in.
   */
  private Map<String,Taxonomy> createStrategies(Content taxFolderContent, String siteId) {
    Map<String,Taxonomy> taxonomies = new HashMap<>();
    LOG.debug("Creating taxonomy strategy for folder '{}', site '{}'", (taxFolderContent == null ? null : taxFolderContent.getPath()), siteId);
    //lookup the root folder
    try {
      if (taxFolderContent != null) {
        Set<Content> taxonomyFolderChildren = taxFolderContent.getSubfolders();
        //check each subfolder that is a separate taxonomy tree
        if (!taxonomyFolderChildren.isEmpty()) {
          for (Content taxonomyFolder : taxonomyFolderChildren) {
            //find first taxonomy child to determine the type of taxonomy
            Content indexingTaxonomy = TaxonomyUtil.findFirstTaxonomy(taxonomyFolder, contentType);
            if (indexingTaxonomy != null) {
              ContentType type = indexingTaxonomy.getType();

              //we only have one strategy here, maybe some customers need more logic here and different strategies...
              long start = System.currentTimeMillis();
              DefaultTaxonomy strategy = new DefaultTaxonomy(taxonomyFolder, siteId, type, contentRepository, solrSearchService);
              taxonomies.put(toKey(strategy.getTaxonomyId(), siteId), strategy);

              if (LOG.isDebugEnabled()) {
                LOG.debug("Taxonomy strategy for folder '" + taxonomyFolder + "' took " + (System.currentTimeMillis() - start) + " ms");
              }
            }
          }
        }
      } else {
        LOG.warn("Invalid taxonomy root folder [null]");
      }
    }
    catch (Exception e) {
      LOG.error("Error resolving taxonomy strategey for '{}' and site id '{}'", taxFolderContent != null ? taxFolderContent.getPath() : "[null]", siteId, e);
    }

    return taxonomies;
  }


  /**
   * Recursive search for the taxonomy strategy matching the given id and site.
   * Lookup:
   * <ol>
   *   <li>Lookup taxonomy for site and (taxonomy) id</li>
   *   <li>Lookup common taxonomy, ignoring site value</li>
   *   <li>Lookup alias mapping</li>
   * </ol>
   *
   * @param siteId       The site id the taxonomy is working on or null if it is a global tree.
   * @param taxonomyId The id of the tree
   * @return The administrating object for the taxonomy tree.
   */
  private Taxonomy findTaxonomy(String siteId, String taxonomyId) {
    String key = toKey(taxonomyId, siteId);
    Taxonomy strategy = strategies.get(key);
    //it's most probably that the first lookup fails, means that a taxonomy belongs to a site but
    //the site does not define a taxonomy of type XY of its own, so search for the common one, using site=null value
    if (strategy == null) {
      key = toKey(taxonomyId, null);
      strategy = strategies.get(key);
    }
    if (strategy == null) {
      //still not found? ok then try an alias next...
      String mappedTaxonomy = aliasMapping.get(taxonomyId);
      strategy = strategies.get(toKey(mappedTaxonomy, siteId));

      //if site (e.g. querySubject+media) is set, try to find root taxonomy and ignore the site name
      if (strategy == null && siteId != null) {
        return getTaxonomy(null, taxonomyId);
      }
    }
    return strategy;
  }

  /**
   * Checks if the given Taxonomy entry is still valid.
   * @param entry The strategy entry to check.
   * @return True, if the entry is still valid.
   */
  private boolean isValid(Map.Entry<String, Taxonomy> entry) {
    return entry.getValue().isValid();
  }

}
