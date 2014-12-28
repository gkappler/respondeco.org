package org.respondeco.respondeco.service;

import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.*;
import org.respondeco.respondeco.web.rest.util.RestParameters;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clemens Puehringer on 02/12/14.
 */

@Service
public class PostingFeedService {

    private PostingFeedRepository postingFeedRepository;

    private ProjectRepository projectRepository;

    private OrganizationRepository organizationRepository;

    private UserService userService;

    private PostingRepository postingRepository;

    @Inject
    public PostingFeedService(PostingFeedRepository postingFeedRepository,
                              ProjectRepository projectRepository,
                              OrganizationRepository organizationRepository,
                              UserService userService,
                              PostingRepository postingRepository) {
        this.postingFeedRepository = postingFeedRepository;
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.userService = userService;
        this.postingRepository = postingRepository;

    }

    public Posting addPostingForProjects(Long projectId, String information) throws
            NoSuchProjectException,
            PostingFeedException {
        User currentUser = userService.getUserWithAuthorities();
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        if(currentUser.equals(project.getManager()) == false) {
            throw new PostingFeedException(".notmanagerofproject", String.format("User is not manager of project"));
        }
        if(information == "") {
            throw new PostingFeedException(".emptyinformation",String.format("Information must not be empty"));
        }
        PostingFeed postingFeed = project.getPostingFeed();
        if(postingFeed == null) {
            throw new PostingFeedException(".nopostingfeed",
                    String.format("Project %s does not have a postingfeed", projectId));
        }
        Posting posting = new Posting();
        posting.setInformation(information);
        posting.setAuthor(currentUser);
        posting.setPostingfeed(postingFeed);

        return postingRepository.save(posting);
    }

    public Posting addPostingForOrganization(Long organizationId, String information) throws
            NoSuchOrganizationException,
            PostingFeedException {
        User currentUser = userService.getUserWithAuthorities();
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        if(organization == null) {
            throw new NoSuchOrganizationException(organizationId);
        }
        if(currentUser.equals(organization.getOwner()) == false) {
            throw new PostingFeedException(".notowneroforganization", String.format("User is not owner of organization"));
        }
        if(information == "") {
            throw new PostingFeedException(".emptyinformation",String.format("Information must not be empty"));
        }
        PostingFeed postingFeed = organization.getPostingFeed();
        if(postingFeed == null) {
            throw new PostingFeedException(".nopostingfeed",
                    String.format("Organization %s does not have a postingfeed", organizationId));
        }
        Posting posting = new Posting();
        posting.setInformation(information);
        posting.setAuthor(currentUser);
        posting.setPostingfeed(postingFeed);

        return postingRepository.save(posting);
    }

    public Page<Posting> getPostingsForOrganization(Long organizationId, RestParameters restParameters) throws
            NoSuchOrganizationException {
        Organization organization = organizationRepository.findByIdAndActiveIsTrue(organizationId);
        if(organization == null) {
            throw new NoSuchOrganizationException(organizationId);
        }
        return postingFeedRepository.getPostingsForOrganization(organizationId, restParameters.buildPageRequest());
    }

    public Page<Posting> getPostingsForProject(Long projectId, RestParameters restParameters) throws
            NoSuchProjectException {
        Project project = projectRepository.findByIdAndActiveIsTrue(projectId);
        if(project == null) {
            throw new NoSuchProjectException(projectId);
        }
        return postingFeedRepository.getPostingsForProject(projectId, restParameters.buildPageRequest());
    }

    public void deletePosting(Long postingId) throws PostingException {
        Posting posting = postingRepository.findByIdAndActiveIsTrue(postingId);
        User currentUser = userService.getUserWithAuthorities();
        if(posting == null) {
            throw new PostingException(".noSuchPosting", String.format("There is no such posting"));
        }
        if(posting.getAuthor().equals(currentUser) == false) {
            throw new PostingException(".userIsNotAuthorOfPosting", String.format("You are not the author of this posting"));
        }
        posting.setActive(false);
        postingRepository.save(posting);
    }
}