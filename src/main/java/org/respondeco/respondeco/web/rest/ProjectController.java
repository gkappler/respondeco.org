package org.respondeco.respondeco.web.rest;

import com.codahale.metrics.annotation.Timed;
import org.respondeco.respondeco.domain.Project;
import org.respondeco.respondeco.repository.ProjectRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.ProjectService;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Project.
 */
@RestController
@RequestMapping("/app")
public class ProjectController {

    private final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private ProjectService projectService;
    private ProjectRepository projectRepository;

    @Inject
    public ProjectController(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    /**
     * POST  /rest/project -> Create a new project.
     */
    @RequestMapping(value = "/rest/project",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> create(@RequestBody ProjectDTO project) {
        log.debug("REST request to save Project : {}", project);
        ResponseEntity<?> responseEntity;
        try {
            projectService.save(
                    project.getId(),
                    project.getName(),
                    project.getPurpose(),
                    project.getConcrete(),
                    project.getStartDate(),
                    project.getEndDate(),
                    project.getProjectLogo());
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalArgumentException e) {
            log.error("Could not save Project : {}", project, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * POST  /rest/project/manager -> Change project manager of a project
     */
    @RequestMapping(value = "/rest/project/manager/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.USER)
    public ResponseEntity<?> changeManager(@PathVariable Long id, @RequestBody String newManager) {
        log.debug("REST request to change project manager of project {} to {}", id, newManager);
        ResponseEntity<?> responseEntity;
        try {
            projectService.setManager(id, newManager);
            responseEntity = new ResponseEntity<>(HttpStatus.OK);
        } catch(IllegalArgumentException | NoSuchUserException e) {
            log.error("Could not manager of project {} to {}", id, newManager, e);
            responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return responseEntity;
    }

    /**
     * GET  /rest/project -> get all the projects.
     */
    @RequestMapping(value = "/rest/project/all",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Project> getAll() {
        log.debug("REST request to get all ProjectIdeas");
        return projectRepository.findByActiveIsTrue();
    }

    /**
     * GET  /rest/project/:id -> get the "id" project.
     */
    @RequestMapping(value = "/rest/project/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Project> get(@PathVariable Long id) {
        log.debug("REST request to get ProjectIdea : {}", id);
        return Optional.ofNullable(projectRepository.findByIdAndActiveIsTrue(id))
            .map(project -> new ResponseEntity<>(
                project,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/project/:id -> delete the "id" project.
     */
    @RequestMapping(value = "/rest/project/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete ProjectIdea : {}", id);
        projectService.delete(id);
    }
}
