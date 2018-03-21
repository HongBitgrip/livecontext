package com.coremedia.blueprint.elastic.social.demodata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.coremedia.elastic.core.api.tenant.TenantService;

import static com.coremedia.blueprint.elastic.social.demodata.DemoDataGenerator.STATE_RUNNING;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Named("demoDataGeneratorController")
public class DemoDataGeneratorController extends AbstractController {

  private static final Logger LOG = LoggerFactory.getLogger(DemoDataGeneratorController.class);

  static final String INTERVAL_PARAM = "interval";
  static final String TENANT_PARAM = "tenant";
  static final String BOOST_PARAM = "boost";
  static final String STOP_PARAM = "stop";
  static final String STATUS_PARAM = "status";
  static final String STATUS_KEY = "status";
  static final String INFO_KEY = "info";

  @Inject
  private DemoDataGenerator demoDataGenerator;

  @Inject
  private TenantService tenantService;

  @Override
  protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
    final ModelAndView mv = new ModelAndView();

    String tenant = request.getParameter(TENANT_PARAM);
    if (!isBlank(tenant)) {
      if (!tenantService.getRegistered().contains(tenant)) {
        LOG.info("Register previously unknown tenant '{}'", tenant);
        tenantService.register(tenant);
      }
      tenantService.setCurrent(tenant);
    }

    try {
     if (request.getParameter(INTERVAL_PARAM) != null) {
        try {
          demoDataGenerator.setInterval(Integer.valueOf(request.getParameter(INTERVAL_PARAM)));
        } catch (NumberFormatException e) {
          demoDataGenerator.setInterval(demoDataGenerator.getDefaultInterval());
        }
      }

      if (request.getParameter(STOP_PARAM) != null) {
        if (STATE_RUNNING.equals(demoDataGenerator.getStatus())) {
          demoDataGenerator.stop();
          mv.addObject(INFO_KEY, "Stopped the DemoDataGenerator");
        } else {
          mv.addObject(INFO_KEY, "DemoDataGenerator not running");
        }
      }
      else if (request.getParameter(STATUS_PARAM) == null) {
        // start the DemoDataGenerator if it is not a stop or status info request
        if (!demoDataGenerator.getStatus().equals(STATE_RUNNING)) {
          demoDataGenerator.start();
          mv.addObject(INFO_KEY, "Started the DemoDataGenerator");
        } else {
          mv.addObject(INFO_KEY, "DemoDataGenerator already running");
        }
      }

      mv.addObject(STATUS_KEY, demoDataGenerator.getStatus());

      addStatistics(mv);
    } finally {
      tenantService.clearCurrent();
    }

    return mv;
  }

  private void addStatistics(ModelAndView mv) {
    mv.addObject("tenant", tenantService.getCurrent());
    mv.addObject("creationInterval", demoDataGenerator.getInterval());
    mv.addObject("tenant", tenantService.getCurrent());
    mv.addObject("teasablesCommentingEnabled", demoDataGenerator.getTeasablesCommentingEnabled().size());
    mv.addObject("teasablesAnonymousCommentingEnabled", demoDataGenerator.getTeasablesAnonymousCommentingEnabled().size());
    mv.addObject("teasablesComplainingEnabled", demoDataGenerator.getTeasablesComplainingEnabled().size());
    mv.addObject("teasablesAnonymousComplainingEnabled", demoDataGenerator.getTeasablesAnonymousComplainingEnabled().size());
    mv.addObject("teasablesPostModerationEnabled", demoDataGenerator.getTeasablesCommentingEnabledPostModeration().size());
    mv.addObject("teasablesPreModerationEnabled", demoDataGenerator.getTeasablesCommentingEnabledPreModeration().size());
    mv.addObject("teasablesNoModerationEnabled", demoDataGenerator.getTeasablesCommentingEnabledNoModeration().size());
    mv.addObject("teasablesLikeEnabled", demoDataGenerator.getTeasablesLikeEnabled().size());
    mv.addObject("teasablesAnonymousLikeEnabled", demoDataGenerator.getTeasablesAnonymousLikeEnabled().size());
    mv.addObject("teasablesRatingEnabled", demoDataGenerator.getTeasablesRatingEnabled().size());
    mv.addObject("teasablesAnonymousRatingEnabled", demoDataGenerator.getTeasablesAnonymousRatingEnabled().size());

    mv.addObject("users", demoDataGenerator.getUserCount());
    mv.addObject("postModerationUsers", demoDataGenerator.getPostModerationUserCount());
    mv.addObject("preModerationUsers", demoDataGenerator.getPreModerationUserCount());
    mv.addObject("noModerationUsers", demoDataGenerator.getNoModerationUserCount());
    mv.addObject("userChangesPreModeration", demoDataGenerator.getUserChangesPreModerationCount());
    mv.addObject("userChangesPostModeration", demoDataGenerator.getUserChangesPostModerationCount());
    mv.addObject("postModerationComments", demoDataGenerator.getPostModerationCommentCount());
    mv.addObject("preModerationComments", demoDataGenerator.getPreModerationCommentCount());
    mv.addObject("noModerationComments", demoDataGenerator.getNoModerationCommentCount());
    mv.addObject("commentComplaints", demoDataGenerator.getCommentComplaintCount());
    mv.addObject("commentsWithAttachment", demoDataGenerator.getCommentWithAttachmentCount());
    mv.addObject("userComplaints", demoDataGenerator.getUserComplaintCount());
    mv.addObject("comments", demoDataGenerator.getCommentCount());
    mv.addObject("postModerationReviews", demoDataGenerator.getPostModerationReviewCount());
    mv.addObject("preModerationReviews", demoDataGenerator.getPreModerationReviewCount());
    mv.addObject("noModerationReviews", demoDataGenerator.getNoModerationReviewCount());
    mv.addObject("reviewComplaints", demoDataGenerator.getReviewComplaintCount());
    mv.addObject("reviewsWithAttachment", demoDataGenerator.getReviewWithAttachmentCount());
    mv.addObject("reviews", demoDataGenerator.getReviewCount());
    mv.addObject("likes", demoDataGenerator.getLikeCount());
    mv.addObject("ratings", demoDataGenerator.getRatingCount());
  }
}
