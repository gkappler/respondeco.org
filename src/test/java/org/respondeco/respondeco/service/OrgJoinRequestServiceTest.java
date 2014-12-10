package org.respondeco.respondeco.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.OrgJoinRequest;
import org.respondeco.respondeco.domain.Organization;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.ImageRepository;
import org.respondeco.respondeco.repository.OrgJoinRequestRepository;
import org.respondeco.respondeco.repository.OrganizationRepository;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.service.exception.AlreadyInOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchOrgJoinRequestException;
import org.respondeco.respondeco.service.exception.NoSuchOrganizationException;
import org.respondeco.respondeco.service.exception.NoSuchUserException;
import org.respondeco.respondeco.web.rest.dto.OrgJoinRequestWithActiveFlagDTO;
import org.respondeco.respondeco.web.rest.dto.OrganizationResponseDTO;
import org.respondeco.respondeco.web.rest.dto.UserDTO;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Created by Christoph Schiessl on 16/11/14.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class OrgJoinRequestServiceTest {

    @Inject
    private OrgJoinRequestRepository orgJoinRequestRepository;

    @Mock
    private OrganizationRepository organizationRepositoryMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganizationService organizationService;

    @Mock
    private ImageRepository imageRepositoryMock;

    private OrgJoinRequestService orgJoinRequestService;

    private User orgOwner;
    private User defaultUser;
    private Organization defaultOrganization;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        orgJoinRequestService = new OrgJoinRequestService(orgJoinRequestRepository, userServiceMock, userRepositoryMock, organizationRepositoryMock);
        organizationService = new OrganizationService(organizationRepositoryMock,userServiceMock, userRepositoryMock, imageRepositoryMock);

        defaultUser = new User();
        defaultUser.setId(2L);
        defaultUser.setLogin("testUser");

        orgOwner = new User();
        orgOwner.setId(1L);
        orgOwner.setLogin("org Owner");
        //orgOwner.setOrgId(1L);

        defaultOrganization = new Organization();
        defaultOrganization.setName("test org");
        defaultOrganization.setId(1L);
        defaultOrganization.setOwner(orgOwner);

        orgJoinRequestRepository.deleteAll();
    }

    @Test
    public void testCreateOrgJoinRequest() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(orgOwner.getId())).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(orgOwner));
        assertNotNull(orgJoinRequest);
        assertEquals(orgJoinRequest.getOrganization(),defaultOrganization);
        assertEquals(orgJoinRequest.getUser(), orgOwner);
    }

    @Test(expected = NoSuchUserException.class)
    public void testCreateOrgJoinRequest_NotExistingUser() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(orgOwner.getId())).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO());
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testCreateOrgJoinRequest_NotExistingOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(orgOwner.getId())).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(new OrganizationResponseDTO(), new UserDTO(orgOwner));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateOrgJoinRequest_NotOwnerOfOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(defaultUser));
    }

    @Test
    public void testGetOrgJoinRequestByOrgName() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(orgOwner.getId())).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(orgOwner));

        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByOrganization(defaultOrganization.getId());
        OrgJoinRequest orgJoinRequest = orgJoinRequestList.get(0);
        assertNotNull(orgJoinRequest);

        assertEquals(orgJoinRequest.getOrganization(),defaultOrganization);
        assertEquals(orgJoinRequest.getUser(),orgOwner);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testGetOrgJoinRequestByOrgName_NoSuchOrganization() throws Exception {
        orgJoinRequestService.getOrgJoinRequestByOrganization(231l);
    }

    @Test
     public void testGetOrgJoinRequestByCurrentUser() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(orgOwner.getId())).thenReturn(orgOwner);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null), new UserDTO(orgOwner));

        List<OrgJoinRequest> orgJoinRequestList = orgJoinRequestService.getOrgJoinRequestByCurrentUser();
        OrgJoinRequest orgJoinRequest = orgJoinRequestList.get(0);
        assertNotNull(orgJoinRequest);

        assertEquals(orgJoinRequest.getOrganization(),defaultOrganization);
        assertEquals(orgJoinRequest.getUser(),orgOwner);
    }

    @Test
    public void testAcceptOrgJoinRequest() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());

        assertNull(orgJoinRequestRepository.findByIdAndActiveIsTrue(orgJoinRequest.getId()));
    }

    @Test(expected = NoSuchOrgJoinRequestException.class)
    public void testAcceptOrgJoinRequest_NotExistingOrgJoinRequest() throws Exception {
        orgJoinRequestService.acceptRequest(100L);
    }

    @Test(expected = NoSuchOrganizationException.class)
    public void testAcceptOrgJoinRequest_NotExistingOrganization() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAcceptOrgJoinRequest_UserNotOfOrgJoinRequest() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findByLogin(defaultUser.getLogin())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByName(defaultOrganization.getName())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
    }

//    @Test(expected = AlreadyInOrganizationException.class)
//    public void testAcceptOrgJoinRequest_AlreadyInOrganization() throws Exception {
//
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
//        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//
//        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
//        assertNotNull(orgJoinRequest);
//
//      //  defaultUser.setOrgId(1L);
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//        orgJoinRequestService.acceptRequest(orgJoinRequest.getId());
//    }

    @Test
    public void testDeclineOrgJoinRequest() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.declineRequest(orgJoinRequest.getId());

        assertNull(orgJoinRequestRepository.findByIdAndActiveIsTrue(orgJoinRequest.getId()));
    }
    @Test(expected = NoSuchOrgJoinRequestException.class)
    public void testDeclineOrgJoinRequest_NotExistingOrgJoinRequest() throws Exception {
        orgJoinRequestService.declineRequest(100L);
    }

//    @Test(expected = NoSuchOrganizationException.class)
//    public void testDeclineOrgJoinRequest_NotExistingOrganization() throws Exception {
//
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
//        when(userRepositoryMock.findOne(defaultUser.getId())).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
//
//        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
//        assertNotNull(orgJoinRequest);
//
//        when(userServiceMock.getUserWithAuthorities()).thenReturn(defaultUser);
//        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(null);
//        orgJoinRequestService.declineRequest(orgJoinRequest.getId());
//    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeclineOrgJoinRequest_UserNotOfOrgJoinRequest() throws Exception {

        when(userServiceMock.getUserWithAuthorities()).thenReturn(orgOwner);
        when(userRepositoryMock.findByLogin(defaultUser.getLogin())).thenReturn(defaultUser);
        when(organizationRepositoryMock.findByName(defaultOrganization.getName())).thenReturn(defaultOrganization);

        OrgJoinRequest orgJoinRequest = orgJoinRequestService.createOrgJoinRequest(OrganizationResponseDTO.fromEntity(defaultOrganization, null),new UserDTO(defaultUser));
        assertNotNull(orgJoinRequest);

        when(organizationRepositoryMock.findOne(defaultOrganization.getId())).thenReturn(defaultOrganization);
        orgJoinRequestService.declineRequest(orgJoinRequest.getId());
    }



}
