package com.coremedia.blueprint.userproviders.crowd;

import com.atlassian.crowd.embedded.api.SearchRestriction;
import com.atlassian.crowd.exception.*;
import com.atlassian.crowd.integration.rest.service.factory.RestCrowdClientFactory;
import com.atlassian.crowd.model.group.Group;
import com.atlassian.crowd.model.user.User;
import com.atlassian.crowd.search.builder.Restriction;
import com.atlassian.crowd.search.query.entity.restriction.NullRestriction;
import com.atlassian.crowd.search.query.entity.restriction.Property;
import com.atlassian.crowd.search.query.entity.restriction.constants.GroupTermKeys;
import com.atlassian.crowd.search.query.entity.restriction.constants.UserTermKeys;
import com.atlassian.crowd.service.client.ClientProperties;
import com.atlassian.crowd.service.client.ClientPropertiesImpl;
import com.atlassian.crowd.service.client.CrowdClient;
import com.coremedia.ldap.*;
import hox.corem.Corem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class CrowdUserProvider implements MemberHelper, UserProvider2 {

  private static final Logger LOG = LoggerFactory.getLogger(CrowdUserProvider.class);

  /**
   * Constant that holds the name of the property which specifies the domain this UserProvider is responsible for
   * <p/>
   * The value of this constant is "crowd.domain"
   * <p/>
   */
  public static final String CROWD_MEMBER_DOMAIN = "crowd.domain";

  /**
   * Constant that holds the name of the property for specifying the expiration (in seconds) of crowd search results
   * <p/>
   * The value of this constant is "crowd.expiration"
   * <p/>
   * The default value of this property is 3600 (i.e. an hour)
   */
  public static final String CROWD_EXPIRATION = "crowd.expiration";

  /**
   * Constant that holds the property which specifies if the groups are content groups.
   * <p/>
   * The value of this constant is "crowd.contentgroups"
   * <p/>
   * The default value for this constant is true
   */
  public static final String CROWD_CONTENTGROUPS = "crowd.contentgroups";
  /**
   * Constant that holds the property which specifies if the groups are live groups.
   * <p/>
   * The value of this constant is "crowd.livegroups"
   * <p/>
   * The default value for this constant is false
   */
  public static final String CROWD_LIVEGROUPS = "crowd.livegroups";

  /**
   * Constant that holds the property which specifies if the groups are admin groups.
   * <p/>
   * The value of this constant is "crowd.admingroups"
   * <p/>
   * The default value for this constant is false
   */
  public static final String CROWD_ADMINGROUPS = "crowd.admingroups";

  private static final NullRestriction NULL_RESTRICTION = new NullRestriction() {};
  private static final String TRUE_STR = "true";
  private static final String FALSE_STR = "false";

  private Properties crowdProperties;
  private CrowdClient crowdClient;
  private int timeOutSeconds;
  private MemberHelperMemberFactory memberFactory;


  @Override
  public LdapGroup[] memberOf(String dn) throws Exception {
    List<Group> groups = crowdClient.getParentGroupsForGroup(dn, 0, -1);
    groups.addAll(crowdClient.getGroupsForUser(dn, 0, -1));
    return makeGroups(groups, getDomain());
  }

  @Override
  public int countMembers(String dn) throws Exception {
    return members(dn).length;
  }

  @Override
  public LdapMember[] members(String dn) throws Exception {
    List<User> users = crowdClient.getUsersOfGroup(dn, 0, -1);
    LdapUser[] ldapUsers = makeUsers(users, getDomain());

    List<Group> groups = crowdClient.getChildGroupsOfGroup(dn, 0, -1);
    LdapGroup[] ldapGroups = makeGroups(groups, getDomain());

    LdapMember[] members = new LdapMember[ldapUsers.length + ldapGroups.length];
    int i = 0;
    for (LdapUser ldapUser : ldapUsers) {
      members[i++] = ldapUser;
    }
    for (LdapGroup ldapGroup : ldapGroups) {
      members[i++] = ldapGroup;
    }

    return members;
  }

  @Override
  public LdapGroup[] subgroups(String dn) throws Exception {
    List<Group> groups = crowdClient.getChildGroupsOfGroup(dn, 0, -1);
    return makeGroups(groups, getDomain());
  }

  @Override
  public LdapUser[] users(String dn) throws Exception {
    List<User> users = crowdClient.getUsersOfGroup(dn, 0, -1);
    return makeUsers(users, getDomain());
  }

  @Override
  public boolean isMemberOf(String memberDn, String groupDn) throws Exception {
    List<User> users = crowdClient.getUsersOfGroup(groupDn, 0, -1);
    for (User user : users) {
      if (user.getName().equals(memberDn)) {
        return true;
      }
    }
    List<Group> groups = crowdClient.getChildGroupsOfGroup(groupDn, 0, -1);
    for (Group group : groups) {
      if (group.getName().equals(memberDn)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void initialize(String propfile) throws Exception {
    crowdProperties = new Properties();
    FileInputStream stream = new FileInputStream(coremHomeFile(propfile));
    try {
      crowdProperties.load(stream);
    } finally {
      stream.close();
    }

    String expiration = crowdProperties.getProperty(CROWD_EXPIRATION, "3600").trim();
    timeOutSeconds = Integer.parseInt(expiration.trim());
    memberFactory = new MemberHelperMemberFactory(this, timeOutSeconds);

    final ClientProperties clientProperties = ClientPropertiesImpl.newInstanceFromProperties(crowdProperties);
    crowdClient = new RestCrowdClientFactory().newInstance(clientProperties);
  }

  @Override
  public void close() {
    if (crowdClient != null) {
      crowdClient.shutdown();
    }
  }

  /**
   * If the file is relative, resolve it against COREM_HOME.
   *
   * @param propfile a path
   * @return an absolute path
   */
  private String coremHomeFile(String propfile) {
    if (new File(propfile).isAbsolute()) {
      return propfile;
    }
    String coremHome = Corem.getHome().getAbsolutePath();
    if (!coremHome.endsWith(File.separator)) {
      coremHome += File.separator;
    }
    return coremHome + propfile;
  }

  /**
   * Returns the domain of which the external source contains users and groups.
   */
  @Override
  public String[] getDomains() {
    return new String[]{crowdProperties.getProperty(CROWD_MEMBER_DOMAIN)};
  }

  private String getDomain() {
    return getDomains()[0];
  }

  @Override
  public void invalidate() {

  }

  @Override
  public LdapUser[] getUsers(String matchName, String domain, int mode, int maxUsers, boolean invalidate) throws Exception {
    SearchRestriction searchRestriction = createSearchRestriction(UserTermKeys.USERNAME, mode, matchName);
    // run the search
    List<User> users = crowdClient.searchUsers(searchRestriction, 0, maxUsers + 1);
    return makeUsers(users, domain);
  }

  @Override
  public LdapGroup[] getGroups(String matchName, String domain, int mode, int maxGroups, boolean invalidate) throws Exception {
    SearchRestriction searchRestriction = createSearchRestriction(GroupTermKeys.NAME, mode, matchName);
    // run the search
    List<Group> groups = crowdClient.searchGroups(searchRestriction, 0, maxGroups + 1);
    return makeGroups(groups, domain);
  }


  private SearchRestriction createSearchRestriction(Property<String> property, int mode, String matchName) {
    SearchRestriction searchRestriction = NULL_RESTRICTION;

    if (mode == UserProvider.NAME_EXACT) {
      searchRestriction = Restriction.on(property).exactlyMatching(matchName);
    } else if (mode == UserProvider.NAME_CONTAINS) {
      searchRestriction = Restriction.on(property).containing(matchName);
    } else if (mode == UserProvider.NAME_STARTS_WITH) {
      searchRestriction = Restriction.on(property).startingWith(matchName);
    }
    return searchRestriction;
  }

  @Override
  public LdapUser getUser(String jndiId, boolean invalidate) throws Exception {
    try {
      User user = crowdClient.getUser(jndiId);
      return makeUser(user, getDomain());
    } catch (UserNotFoundException e) {
      LOG.debug("Cannot find user '" + jndiId + "'", e);
    }
    // null signals that crowd does not know the user
    return null;
  }

  @Override
  public LdapGroup getGroup(String jndiId, boolean invalidate) throws Exception {
    try {
      Group group = crowdClient.getGroup(jndiId);
      return makeGroup(group, getDomain());
    } catch (GroupNotFoundException e) {
      LOG.debug("Cannot find group '" + jndiId + "'", e);
    }
    // null signals that crowd does not know the group
    return null;
  }

  /**
   * Returns the recommended update interval in seconds.
   * <p/>
   * 0 means update always. You should not configure this value too small, it should be at least some minutes. The
   * check for changes concerning memberships is considerably expensive, even if nothing actually changed. If you
   * don't thwart it this way, it is performed recursively up the group hierarchy on each permission check (i.e.
   * extremely frequent!).
   */
  @Override
  public int getExpirationSeconds() {
    return timeOutSeconds + 1;
  }

  private LdapUser[] makeUsers(List<User> users, String domain) {
    List<LdapUser> result = new ArrayList<>();
    for (User user : users) {
      result.add(makeUser(user, domain));
    }
    return result.toArray(new LdapUser[result.size()]);
  }

  private LdapUser makeUser(User crowduser, String domain) {
    String name = crowduser.getName();
    return memberFactory.createUser(name, name, domain, getCustomAttributes(crowduser), getHomeFolder(name, domain));
  }

  private Map<String, String> getCustomAttributes(User user) {
    Map<String, String> attributes = new HashMap<>();
    if (StringUtils.isNotBlank(user.getFirstName())) {
      attributes.put("firstname", user.getFirstName());
    }
    if (StringUtils.isNotBlank(user.getLastName())) {
      attributes.put("lastname", user.getLastName());
    }
    if (StringUtils.isNotBlank(user.getDisplayName())) {
      attributes.put("displayName", user.getDisplayName());
    }
    if (StringUtils.isNotBlank(user.getEmailAddress())) {
      attributes.put("emailAddress", user.getEmailAddress());
    }
    return attributes;
  }

  private LdapGroup[] makeGroups(List<Group> groups, String domain) {
    List<LdapGroup> result = new ArrayList<>();
    for (Group group : groups) {
      result.add(makeGroup(group, domain));
    }
    return result.toArray(new LdapGroup[result.size()]);
  }

  private LdapGroup makeGroup(Group crowdgroup, String domain) {
    String name = crowdgroup.getName();
    return memberFactory.createGroup(name, name, domain, null, isAdminGroup(), isContentGroup(), isLiveGroup());
  }

  /**
   * Compute the user's home folder in the CAP repository.
   * This default implementation returns /Home/name@domain or
   * /Home/name for the empty domain.
   * <p/>
   * If you override this method, make sure that you thoroughly
   * distinguish between the general Home Folder which you configure in
   * capclient.properties (default: /Home) and a user's personal Home
   * Folder. The general Home Folder must not be used as personal Home
   * Folder of a user. The personal Home Folders should be descendants
   * of the general Home Folder.
   * <p/>
   * The root folder also must not be used as a Home Folder.
   *
   * @param name   name of the user
   * @param domain domain of the user
   * @return returns the users home folder
   */
  protected String getHomeFolder(String name, String domain) {
    if (domain == null || domain.length() == 0) {
      return "/Home/" + name;
    } else {
      return "/Home/" + name + "@" + domain;
    }
  }


  /**
   * Returns true if the group denoted by name@domain is a Content Server group. This default implementation returns
   * the value of the property {@link #CROWD_CONTENTGROUPS} or true if not specified.
   *
   * @return true if the group denoted by name@domain is a Content Server group
   */
  protected boolean isContentGroup() {
    String contentgroups = crowdProperties.getProperty(CROWD_CONTENTGROUPS, TRUE_STR);
    return TRUE_STR.equals(contentgroups.trim());
  }

  /**
   * Returns true if the group denoted by name@domain is an Admin group. This default implementation returns the value
   * of the property {@link #CROWD_ADMINGROUPS} or false if not specified.
   *
   * @return true if the group denoted by name@domain is an Admin group
   */
  protected boolean isAdminGroup() {
    String admingroups = crowdProperties.getProperty(CROWD_ADMINGROUPS, FALSE_STR);
    return TRUE_STR.equals(admingroups.trim());
  }

  /**
   * Returns true if the group denoted by name@domain is a Live Server group. This default implementation returns the
   * value of the property {@link #CROWD_LIVEGROUPS} or false if not specified.
   *
   * @return true if the group denoted by name@domain is a Live Server group
   */
  protected boolean isLiveGroup() {
    String livegroups = crowdProperties.getProperty(CROWD_LIVEGROUPS, FALSE_STR);
    return TRUE_STR.equals(livegroups.trim());
  }
}
