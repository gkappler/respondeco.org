package org.respondeco.respondeco.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.*;
import org.respondeco.respondeco.repository.*;
import org.respondeco.respondeco.service.exception.ResourceException;
import org.respondeco.respondeco.service.exception.ResourceJoinTagException;
import org.respondeco.respondeco.service.exception.ResourceTagException;
import org.respondeco.respondeco.web.rest.dto.ResourceOfferDTO;
import org.respondeco.respondeco.web.rest.dto.ResourceRequirementDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import scala.Console;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@Transactional
public class ResourceServiceTest {

    @Mock
    private ResourceOfferRepository resourceOfferRepositoryMock;
    @Mock
    private ResourceRequirementRepository resourceRequirementRepositoryMock;
    @Mock
    private ResourceTagRepository resourceTagRepositoryMock;
    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ImageRepository imageRepository;
    @Mock
    private UserService userService;

    private ResourceService resourceService;
    private ResourceOffer expOffer = null;
    private ResourceTag expResourceTag = null;
    private ResourceRequirement expectedReq = new ResourceRequirement();
    private Project expProject;
    private Organization expOrg;

    private User logedInUser;

    private Long newTagId = 1L;

    private static Class<Long> longCl = Long.class;
    private static Class<String> stringCl = String.class;
    private static Class<ResourceRequirement> reqCl = ResourceRequirement.class;
    private static Class<ResourceOffer> offerCl = ResourceOffer.class;
    private static Class<ResourceTag> tagCl = ResourceTag.class;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.resourceService = new ResourceService(
            resourceOfferRepositoryMock,
            resourceRequirementRepositoryMock,
            resourceTagRepositoryMock,
            organizationRepository,
            projectRepository,
            imageRepository,
            userService
        );
    }

    @After
    public void reset(){
        expOffer = null;
        expResourceTag = null;
        expectedReq = null;
        logedInUser = null;
        expProject = null;
        expOrg = null;
        newTagId = 1L;
    }

    private String[] prepareCreateOffer(){
        Organization organisation = new Organization();
        organisation.setId(1L);
        expOffer = new ResourceOffer();
        expOffer.setDescription(" Here is my test Requirement... bla bla. ");
        expOffer.setAmount(new BigDecimal(10));
        expOffer.setOrganisation(organisation);
        expOffer.setIsCommercial(true);
        expOffer.setIsRecurrent(false);
        expOffer.setStartDate(LocalDate.now());
        expOffer.setId(1L);
        expOffer.setName(" TEST ");
        expResourceTag = new ResourceTag(1L, "test ");

        expResourceTag.setId(1L);
        expOffer.addResourceTag(expResourceTag);
        String[] tags = new String[1];
        tags[0] = expResourceTag.getName();

        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        List<ResourceOffer> resourceOffers = new ArrayList<ResourceOffer>();

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceOfferRepositoryMock.findByNameAndOrganisationId(expOffer.getName(), expOffer.getOrganisation().getId())).thenReturn(resourceOffers);
        //Assign all variables to new Resource Requirement Objekt and execute Save
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceOffer result = (ResourceOffer) args[0];
            result.setId(1L);
            Console.out().print(args[0]);
            return result;
        }).when(resourceOfferRepositoryMock).save(isA(offerCl));
        this.prepareTagRepository(tags[0], resourceTags);

        return tags;
    }

    private void prepareUser(){
        logedInUser = new User();
        logedInUser.setId(1L);
        Set<Authority> authorities = new HashSet<>(1);
        Authority a1 = new Authority();
        a1.setName("ROLE_ADMIN");
        authorities.add(a1);
        Authority a2 = new Authority();
        a2.setName("ROLE_USER");
        authorities.add(a2);
        logedInUser.setAuthorities(authorities);
        expOrg = new Organization();
        expOrg.setId(1L);
        logedInUser.setOrganization(expOrg);
        when(this.userService.getUserWithAuthorities()).thenReturn(logedInUser);
        when(this.organizationRepository.findOne(isA(longCl))).thenReturn(expOrg);
    }

    private void prepareProject(Long projectId){
        expProject = new Project();
        expProject.setId(projectId);
        expectedReq.setProject(expProject);
        expProject.setOrganization(expOrg);
        when(this.projectRepository.findOne(expProject.getId())).thenReturn(expProject);
    }

    private String[] prepareCreateRequirement(){
        this.prepareProject(1L);
        //region Test data
        expectedReq = new ResourceRequirement();
        expectedReq.setDescription(" Here is my test Requirement... bla bla. ");
        expectedReq.setAmount(new BigDecimal(10));
        expectedReq.setProject(expProject);
        expectedReq.setIsEssential(true);
        expectedReq.setId(1L);
        expectedReq.setName(" TEST ");
        expResourceTag = new ResourceTag(1L, "test ");
        expResourceTag.setId(1L);
        expectedReq.addResourceTag(expResourceTag);
        String[] tags = new String[1];
        tags[0] = expResourceTag.getName();


        List<ResourceTag> resourceTags = new ArrayList<ResourceTag>();
        List<ResourceRequirement> resourceReqs = new ArrayList<ResourceRequirement>();

        //resourceTags.add(testResourceTag);
        //endregion

        //Step 1 Check if the same ResourceRequirement exists, by Project ID and Description
        when(resourceRequirementRepositoryMock.findByNameAndProjectId(expectedReq.getName(), expectedReq.getProject().getId())).thenReturn(resourceReqs);
        //Assign all variables to new Resource Requirement Objekt and execute Save
        doAnswer(invocation -> {
            Object[] args = invocation.getArguments();
            ResourceRequirement result = (ResourceRequirement) args[0];
            result.setId(1L);
            Console.out().print(args[0]);
            return result;
        }).when(resourceRequirementRepositoryMock).save(isA(reqCl));
        this.prepareTagRepository(tags[0], resourceTags);

        return tags;
    }

    private void prepareTagRepository(String tagName, List<ResourceTag> resourceTags){
        // -> saveResourceTag method
        // Step 3: Return Empty list for our search case
        when(resourceTagRepositoryMock.findByName(tagName)).thenReturn(resourceTags);
        // Step 4: Save Resource Tag
        if(newTagId != null) {
            doAnswer(invocation -> {
                Object[] args = invocation.getArguments();
                ResourceTag tag = (ResourceTag) args[0];
                tag.setId(newTagId);
                Console.out().print(args[0]);
                return tag;
            }).when(resourceTagRepositoryMock).save(isA(tagCl));
        } else {
            when(resourceTagRepositoryMock.save(isA(tagCl))).thenThrow(new IllegalArgumentException());
        }

    }

    @Test
    public void testCreateRequirement_OK() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();

        //save without any tags
        ResourceRequirement actual = this.resourceService.createRequirement(expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expProject.getId(), expectedReq.getIsEssential(), tags);

        assertEquals(expectedReq.getId(), actual.getId());
        assertEquals(expectedReq.getAmount(), actual.getAmount());
        assertEquals(expectedReq.getName(), actual.getName());
        assertEquals(expectedReq.getDescription(), actual.getDescription());
        assertEquals(expProject, actual.getProject());
        assertEquals(expProject, expectedReq.getProject());
        assertEquals(expectedReq.getProject(), actual.getProject());
        assertEquals(expectedReq.getProject().getOrganization(), actual.getProject().getOrganization());
        assertEquals(expectedReq.getIsEssential(), actual.getIsEssential());
        assertEquals(expectedReq.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepository, times(1)).findOne(isA(longCl));
        verify(organizationRepository, times(0)).findOne(isA(longCl));

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }

    @Test(expected = ResourceException.class)
    public void testUpdateRequirement_Fail_NoEntryFound() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(), expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testUpdateRequirement() throws Exception{
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();

        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        //save without any tags
        ResourceRequirement actual = this.resourceService.updateRequirement(expectedReq.getId(), expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getIsEssential(), tags);

        assertEquals(expectedReq.getId(), actual.getId());
        assertEquals(expectedReq.getAmount(), actual.getAmount());
        assertEquals(expectedReq.getName(), actual.getName());
        assertEquals(expectedReq.getDescription(), actual.getDescription());
        assertEquals(expectedReq.getProject(), actual.getProject());
        assertEquals(expectedReq.getIsEssential(), actual.getIsEssential());
        assertEquals(expectedReq.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expectedReq.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }
        //verifying user authorities has been called.
        verify(userService, times(1)).getUserWithAuthorities();
        verify(projectRepository, times(1)).findOne(isA(longCl));
        verify(organizationRepository, times(0)).findOne(isA(longCl));

        verify(resourceRequirementRepositoryMock, times(1)).save(isA(reqCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
        //No Tag joins added
    }

    @Test(expected = ResourceTagException.class)
    public void testCreateRequirement_TagFailed() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();
        tags[0] = null;

        //save without any tags
        ResourceRequirement actual = this.resourceService.createRequirement(expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject().getId(), expectedReq.getIsEssential(), tags);
    }

    @Test(expected = ResourceTagException.class)
    public void testCreateRequirement_TagSaveFailed() throws Exception {
        this.newTagId = null;
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();


        //save without any tags
        ResourceRequirement actual = this.resourceService.createRequirement(expectedReq.getName(), expectedReq.getAmount(), expectedReq.getDescription(), expectedReq.getProject().getId(), expectedReq.getIsEssential(), tags);
    }

    @Test
    public void testDeleteRequirement() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateRequirement();
        when(resourceRequirementRepositoryMock.findOne(expectedReq.getId())).thenReturn(expectedReq);
        this.resourceService.deleteRequirement(expectedReq.getId());

        verify(resourceRequirementRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceRequirementRepositoryMock, times(1)).delete(isA(longCl));
    }

    @Test
    public void testGetAllRequirements_2Entries() throws Exception {
        expProject = new Project();
        expProject.setId(1L);
        doAnswer(invo -> {
            List<ResourceRequirement> items = new ArrayList<ResourceRequirement>(2);
            ResourceRequirement item1 = new ResourceRequirement();
            item1.setId(1L);
            item1.setProject(expProject);
            items.add(item1);
            ResourceRequirement item2 = new ResourceRequirement();
            item2.setId(10L);
            item2.setProject(expProject);
            items.add(item2);
            return items;
        }).when(resourceRequirementRepositoryMock).findAll();
        Long expected = 1L;
        int listSize = 2;
        List<ResourceRequirementDTO> items = this.resourceService.getAllRequirements();
        assertEquals(items.size(), listSize);
        for(int i = 0; i < items.size(); i++){
            ResourceRequirementDTO current = items.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getProjectId(), expProject.getId());
            expected += 9L;
        }

        verify(this.resourceRequirementRepositoryMock, times(1)).findAll();
    }

    @Test
    public void testGetAllRequirements_1EntryForProject2() throws Exception {
        final Long projectID = 2L;
        final Long expectedRequirementID = 10L;
        final Project project1 = new Project();
        project1.setId(1L);
        final Project project2 = new Project();
        project2.setId(2L);

        final List<ResourceRequirement> items = new ArrayList<ResourceRequirement>();
        ResourceRequirement item1 = new ResourceRequirement();
        item1.setId(1L);
        item1.setProject(project1);
        items.add(item1);
        ResourceRequirement item2 = new ResourceRequirement();
        item2.setProject(project2);
        item2.setId(expectedRequirementID);
        items.add(item2);

        doAnswer(invo ->{
            List<ResourceRequirement> internalItems = new ArrayList<ResourceRequirement>();
            for(ResourceRequirement item: items){
                if(item.getProject() == project2){
                    internalItems.add(item);
                }
            }
            return internalItems;
        }).when(resourceRequirementRepositoryMock).findByProjectId(isA(longCl));
        int listSize = 1;
        List<ResourceRequirementDTO> expectedItem = this.resourceService.getAllRequirements(projectID);
        assertEquals(expectedItem.size(), listSize);
        assertEquals(expectedItem.get(0).getProjectId(), projectID);
        assertEquals(expectedItem.get(0).getId(), expectedRequirementID);
        verify(this.resourceRequirementRepositoryMock, times(1)).findByProjectId(isA(longCl));
    }

    @Test
    public void testCreateOffer_OK() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateOffer();

        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getOrganisation().getId(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganisation(), actual.getOrganisation());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getIsRecurrent(), actual.getIsRecurrent());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }

    @Test(expected = ResourceTagException.class)
    public void testCreateOffer_TagFailed() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateOffer();
        tags[0] = null;
        //save without any tags
        ResourceOffer actual = this.resourceService.createOffer(expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getOrganisation().getId(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);
    }

    @Test(expected = ResourceException.class)
    public void testUpdateOffer_Fail() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateOffer();
        ResourceOffer actual = this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganisation().getId(), expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

    }
    @Test
    public void testUpdateOffer() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findOne(expOffer.getId())).thenReturn(expOffer);
        ResourceOffer actual = this.resourceService.updateOffer(expOffer.getId(), expOffer.getOrganisation().getId(), expOffer.getName(), expOffer.getAmount(), expOffer.getDescription(), expOffer.getIsCommercial(), expOffer.getIsRecurrent(), expOffer.getStartDate(), expOffer.getEndDate(), tags);

        assertEquals(expOffer.getId(), actual.getId());
        assertEquals(expOffer.getAmount(), actual.getAmount());
        assertEquals(expOffer.getName(), actual.getName());
        assertEquals(expOffer.getDescription(), actual.getDescription());
        assertEquals(expOffer.getOrganisation(), actual.getOrganisation());
        assertEquals(expOffer.getIsCommercial(), actual.getIsCommercial());
        assertEquals(expOffer.getIsRecurrent(), actual.getIsRecurrent());
        assertEquals(expOffer.getStartDate(), actual.getStartDate());
        assertEquals(expOffer.getEndDate(), actual.getEndDate());
        assertEquals(expOffer.getResourceTags().size(), actual.getResourceTags().size());

        for(ResourceTag actTag: expOffer.getResourceTags()) {
            assertEquals(actTag.getId(), expResourceTag.getId());
            assertEquals(actTag.getName(), expResourceTag.getName());
        }

        verify(resourceOfferRepositoryMock, times(1)).save(isA(offerCl));
        //No tags has been added
        verify(resourceTagRepositoryMock, times(1)).findByName(isA(stringCl));
        verify(resourceTagRepositoryMock, times(1)).save(isA(tagCl));
    }
    @Test
    public void testDeleteOffer() throws Exception {
        this.prepareUser();
        String[] tags = this.prepareCreateOffer();
        when(resourceOfferRepositoryMock.findOne(expOffer.getId())).thenReturn(expOffer);
        this.resourceService.deleteOffer(expOffer.getId());

        verify(resourceOfferRepositoryMock, times(1)).findOne(isA(longCl));
        verify(resourceOfferRepositoryMock, times(1)).delete(isA(longCl));
    }

    @Test
    public void testGetAllOffers_2Entries() throws Exception {
        expOrg = new Organization();
        expOrg.setId(1L);
        doAnswer(invo -> {
            List<ResourceOffer> items = new ArrayList<ResourceOffer>(2);
            ResourceOffer item1 = new ResourceOffer();
            item1.setId(1L);
            item1.setOrganisation(expOrg);
            items.add(item1);
            ResourceOffer item2 = new ResourceOffer();
            item2.setId(10L);
            item2.setOrganisation(expOrg);
            items.add(item2);
            return items;
        }).when(resourceOfferRepositoryMock).findAll();
        Long expected = 1L;
        int listSize = 2;
        List<ResourceOfferDTO> items = this.resourceService.getAllOffers();
        assertEquals(items.size(), listSize);
        for(int i = 0; i < items.size(); i++){
            ResourceOfferDTO current = items.get(i);
            assertEquals(current.getId(), expected);
            assertEquals(current.getOrganizationId(), expOrg.getId());
            expected += 9L;
        }

        verify(this.resourceOfferRepositoryMock, times(1)).findAll();
    }

    /*
    @Test
    public void testGetAllOffers_1EntryForOrganisation2() throws Exception {
        final Long organisationID = 2L;
        final Long expectedOfferID = 10L;
        final Organization organisation1 = new Organization();
        organisation1.setId(1L);
        final Organization organisation2 = new Organization();
        organisation1.setId(2L);

        final List<ResourceOffer> items = new ArrayList<ResourceOffer>();
        ResourceOffer item1 = new ResourceOffer();
        item1.setId(1L);
        item1.setOrganisation(organisation1);
        items.add(item1);
        ResourceOffer item2 = new ResourceOffer();
        item2.setOrganisation(organisation2);
        item2.setId(expectedOfferID);
        items.add(item2);

        doAnswer(invo ->{
            List<ResourceOffer> internalItems = new ArrayList<ResourceOffer>();
            for(ResourceOffer item: items){
                if(item.getOrganisation() == organisation2){
                    internalItems.add(item);
                }
            }
            return internalItems;
        }).when(resourceOfferRepositoryMock).findByOrganisationId(isA(longCl));
        int listSize = 1;
        List<ResourceOfferDTO> expectedItem = this.resourceService.getAllOffers(organisationID);
        assertEquals(expectedItem.size(), listSize);
        assertEquals(expectedItem.get(0).getOrganizationId(), ogranisationID);
        assertEquals(expectedItem.get(0).getId(), expectedOfferID);
        verify(this.resourceOfferRepositoryMock, times(1)).findByOrganisationId(isA(longCl));

    }*/
}
