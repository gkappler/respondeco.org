package org.respondeco.respondeco.web.rest;

import org.apache.tomcat.util.codec.binary.Base64;
import org.respondeco.respondeco.Application;
import org.respondeco.respondeco.domain.Authority;
import org.respondeco.respondeco.domain.Gender;
import org.respondeco.respondeco.domain.ProfilePicture;
import org.respondeco.respondeco.domain.User;
import org.respondeco.respondeco.repository.UserRepository;
import org.respondeco.respondeco.security.AuthoritiesConstants;
import org.respondeco.respondeco.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for the AccountResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)

public class AccountResourceTest {

    @Inject
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    private MockMvc restUserMockMvc;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AccountResource accountResource = new AccountResource();
        ReflectionTestUtils.setField(accountResource, "userRepository", userRepository);
        ReflectionTestUtils.setField(accountResource, "userService", userService);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(accountResource).build();
    }

    @Test
    public void testNonAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

    }

    @Test
    public void testAuthenticatedUser() throws Exception {
        restUserMockMvc.perform(get("/app/rest/authenticate")
                .with(request -> {
                    request.setRemoteUser("test");
                    return request;
                })
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("test"));
    }

    @Test
    public void testGetExistingAccount() throws Exception {
        Set<Authority> authorities = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(AuthoritiesConstants.ADMIN);
        authorities.add(authority);

        User user = new User();
        user.setLogin("test");
        user.setTitle("Dr.");
        user.setGender(Gender.MALE);
        user.setFirstName("john");
        user.setLastName("doe");
        user.setEmail("john.doe@jhipter.com");
        user.setDescription("just a regular everyday normal guy");
        user.setAuthorities(authorities);
        when(userService.getUserWithAuthorities()).thenReturn(user);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.login").value("test"))
                .andExpect(jsonPath("$.title").value("Dr."))
                .andExpect(jsonPath("$.gender").value(Gender.MALE.name()))
                .andExpect(jsonPath("$.firstName").value("john"))
                .andExpect(jsonPath("$.lastName").value("doe"))
                .andExpect(jsonPath("$.email").value("john.doe@jhipter.com"))
                .andExpect(jsonPath("$.description").value("just a regular everyday normal guy"))
                .andExpect(jsonPath("$.roles").value(AuthoritiesConstants.ADMIN));
    }

    @Test
    public void testGetUnknownAccount() throws Exception {
        when(userService.getUserWithAuthorities()).thenReturn(null);

        restUserMockMvc.perform(get("/app/rest/account")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

}
