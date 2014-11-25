package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.OrgJoinRequestService;
import org.respondeco.respondeco.service.OrganizationService;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrgJoinRequestException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestDTO;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestWithActiveFlagDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing OrgJoinRequest.
 */
@RestController
@RequestMapping("/app")
public class OrgJoinRequestController {

    private final Logger log = LoggerFactory.getLogger(OrgJoinRequestController.class);

    private OrgJoinRequestRepository orgjoinrequestRepository;
    private OrgJoinRequestService orgJoinRequestService;

    @Inject
    public OrgJoinRequestController(OrgJoinRequestRepository orgJoinRequestRepository, OrgJoinRequestService orgJoinRequestService) {
        this.orgjoinrequestRepository = orgJoinRequestRepository;
        this.orgJoinRequestService = orgJoinRequestService;
    }

    /**
     * POST  /rest/orgjoinrequests -> Create a new orgjoinrequest.
     */
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody @Valid OrgJoinRequestDTO orgjoinrequest) {
        log.debug("REST request to save OrgJoinRequest : {}", orgjoinrequest);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.createOrgJoinRequest(orgjoinrequest.getOrgName(),
                    orgjoinrequest.getUserLogin());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchUserException e) {
            log.error("Could not save OrgJoinRequest : {}", orgjoinrequest, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/orgjoinrequests -> get all the orgjoinrequests.
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/orgjoinrequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getAll(@RequestParam String filter) {
        log.debug("REST request to get OrgJoinRequests");
        ResponseEntity<?> responseEntity;
        try {
            if(filter==null) {
                return Optional.ofNullable(orgjoinrequestRepository.findAll())
                        .map(orgjoinrequest -> new ResponseEntity<>(
                                orgjoinrequest,
                                HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            else if (filter.equals("ownerrequests")) {
                return Optional.ofNullable(orgJoinRequestService.getOrgJoinRequestsByOwner())
                        .map(orgjoinrequest -> new ResponseEntity<>(
                                orgjoinrequest,
                                HttpStatus.OK))
                        .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
            }
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not find OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
    /**
     * GET  /rest/orgjoinrequests -> get all the orgjoinrequests.
     */
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    @RequestMapping(value = "/rest/orgjoinrequests/{orgName}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> getOrgJoinRequestsByOrgName(@PathVariable String orgName) {
        log.debug("REST request to get OrgJoinRequests by orgName");
        ResponseEntity<?> responseEntity;
        try {
            return Optional.ofNullable(orgJoinRequestService.getOrgJoinRequestByOrgName(orgName))
                    .map(orgjoinrequest -> new ResponseEntity<>(
                            orgjoinrequest,
                            HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (NoSuchOrganizationException e) {
            log.error("Could not find OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
    /**
     * GET  /rest/orgjoinrequests/:orgName -> get the "orgName" orgjoinrequest.
     */
            @RolesAllowed(AuthoritiesConstants.USER)
            @RequestMapping(value = "/rest/orgjoinrequests/myOrgJoinRequests",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrgJoinRequest>> getByCurrentUser() {
        log.debug("REST request to get OrgJoinRequest : {}");
        return Optional.ofNullable(orgJoinRequestService.getOrgJoinRequestByCurrentUser())
                .map(orgjoinrequest -> new ResponseEntity<>(
                        orgjoinrequest,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> accept user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/accept/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> acceptRequest(@PathVariable Long id) {
        log.debug("REST request to accept user and delete OrgJoinRequest : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.acceptRequest(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrgJoinRequestException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (AlreadyInOrganizationException e) {
            log.error("Could not accept OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * DELETE  /rest/orgjoinrequests/:id -> decline user and delete the "id" orgjoinrequest.
     */
    @RolesAllowed(AuthoritiesConstants.USER)
    @RequestMapping(value = "/rest/orgjoinrequests/decline/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> declineRequest(@PathVariable Long id) {
        log.debug("REST request to decline user and delete OrgJoinRequest : {}", id);
        ResponseEntity<?> responseEntity;
        try {
            orgJoinRequestService.declineRequest(id);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchOrgJoinRequestException e) {
            log.error("Could not decline OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (NoSuchOrganizationException e) {
            log.error("Could not decline OrgJoinRequest : {}", e);
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return responseEntity;
    }
}
