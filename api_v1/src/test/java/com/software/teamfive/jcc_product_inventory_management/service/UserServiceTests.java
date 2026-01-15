package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.Role;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.LoginRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.RegistrationRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.LoginResponse;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.RegistrationResponse;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.model.join.UserRole;
import com.software.teamfive.jcc_product_inventory_management.repo.*;
import com.software.teamfive.jcc_product_inventory_management.utility.config.PermissionKeys;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.permission.InsufficientPermissionsException;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserAlreadyExistsException;
import org.aspectj.lang.annotation.Before;
import org.json.JSONException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMemberRepository companyMemberRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PermissionValidatorService permissionValidatorService;

    @InjectMocks
    private UserService userService;

    User testUser = new User();

    @Before("")
    public void setUp() {
        UUID userId = UUID.randomUUID();
        this.testUser.setId(userId);
        testUser.setEmail("testUser123@gmail.com");
    }

    @Test
    void loadByUsername_IdealCheck() throws JSONException {
        when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testUser123");

        verify(this.userRepository, times(1)).findByEmail(any(String.class));
        assertEquals("Match", userDetails.getUsername(), testUser.getUsername());
    }

    @Test
    void loadByUsername_throwsOnNullUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.userService.loadUserByUsername(null);
        });
    }

    @Test
    void loadByUsername_throwsOnEmptyUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.userService.loadUserByUsername("");
        });
    }

    @Test
    void loadByUsername_throwsOnWhiteSpaceUsername() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            this.userService.loadUserByUsername("     ");
        });
    }

    @Test
    void register_IdealTest() throws JSONException {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setFirstName("FirstName");
        registrationRequest.setLastName("LastName");
        registrationRequest.setCompanyName("CompanyName");
        registrationRequest.setPasswordPlainText("PasswordPlainText");
        registrationRequest.setEmail("email");

        Company company = new Company();
        company.setName("CompanyName");
        company.setCreatedBy(testUser);

        CompanyMember companyMember = new CompanyMember();
        companyMember.setUser(testUser);
        companyMember.setCompany(company);

        Role role = new Role();
        role.setName("RoleName");

        UserRole userRole = new UserRole();
        userRole.setRole(role);
        userRole.setMember(companyMember);

        String token = UUID.randomUUID().toString();

        testUser.setId(UUID.randomUUID());
        company.setId(UUID.randomUUID());

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User u = invocation.getArgument(0);
                    u.setId(UUID.randomUUID());
                    return u;
                });

        when(companyRepository.save(any(Company.class)))
                .thenAnswer(invocation -> {
                    Company c = invocation.getArgument(0);
                    c.setId(UUID.randomUUID());
                    return c;
                });

        when(this.companyMemberRepository.save(any(CompanyMember.class))).thenReturn(companyMember);
        when(this.roleRepository.save(any(Role.class))).thenReturn(role);
        when(this.userRoleRepository.save(any(UserRole.class))).thenReturn(userRole);
        when(this.jwtService.generateToken(any(String.class), any())).thenReturn(token);

        RegistrationResponse response = this.userService.register(registrationRequest);

        assertEquals("Match", response.getEmail(), registrationRequest.getEmail());
        assertEquals("Match", response.getToken(), token);
    }

    @Test
    void register_ThrowsOnNullRequest() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.register(null);
        });
    }

    @Test
    void register_ThrowsOnNullEmail() {
        assertThrows(NullPointerException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail(null);
            registrationRequest.setPasswordPlainText("password_plain_text");
            registrationRequest.setFirstName("first name");
            registrationRequest.setLastName("last name");
            registrationRequest.setCompanyName("some company");

            this.userService.register(registrationRequest);
        });

    }

    @Test
    void register_ThrowsOnNullFirstName() {
        assertThrows(NullPointerException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail("email");
            registrationRequest.setPasswordPlainText("password_plain_text");
            registrationRequest.setFirstName(null);
            registrationRequest.setLastName("last name");
            registrationRequest.setCompanyName("some company");

            this.userService.register(registrationRequest);
        });
    }

    @Test
    void register_ThrowsOnNullLastName() {
        assertThrows(NullPointerException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail("email");
            registrationRequest.setPasswordPlainText("password_plain_text");
            registrationRequest.setFirstName("first name");
            registrationRequest.setLastName(null);
            registrationRequest.setCompanyName("some company");

            this.userService.register(registrationRequest);
        });
    }

    @Test
    void register_ThrowsOnNullCompanyName() {
        assertThrows(NullPointerException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail("email");
            registrationRequest.setPasswordPlainText("password_plain_text");
            registrationRequest.setFirstName("first name");
            registrationRequest.setLastName("last name");
            registrationRequest.setCompanyName(null);

            this.userService.register(registrationRequest);
        });
    }

    @Test
    void register_ThrowsOnNullPassword() {
        assertThrows(NullPointerException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail("email");
            registrationRequest.setPasswordPlainText(null);
            registrationRequest.setFirstName("first name");
            registrationRequest.setLastName("last name");
            registrationRequest.setCompanyName("some company");

            this.userService.register(registrationRequest);
        });
    }

    @Test
    void register_ThrowsOnExistingUser() {
        assertThrows(UserAlreadyExistsException.class, () -> {
            RegistrationRequest registrationRequest = new RegistrationRequest();
            registrationRequest.setEmail("email");
            registrationRequest.setPasswordPlainText("password_plain_text");
            registrationRequest.setFirstName("first name");
            registrationRequest.setLastName("last name");
            registrationRequest.setCompanyName("some company");

            when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(new User()));
            this.userService.register(registrationRequest);
        });
    }

    @Test
    void login_IdealCheck() {

       User user = new User();
       user.setId(UUID.randomUUID());
       user.setEmail("email");
       String token = UUID.randomUUID().toString();

       Company company = new Company();
       company.setId(UUID.randomUUID());

       CompanyMember companyMember = new CompanyMember();
       companyMember.setId(UUID.randomUUID());
       companyMember.setUser(user);
       companyMember.setCompany(company);

       Role role = new Role();
       role.setId(UUID.randomUUID());
       role.setName("Some Role Name");

       UserRole userRole = new UserRole();
       userRole.setId(UUID.randomUUID());
       userRole.setMember(companyMember);
       userRole.setRole(role);

       when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
       when(this.companyMemberRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(companyMember));
       when(this.userRoleRepository.findByMemberId(any(UUID.class))).thenReturn(Optional.of(userRole));
       when(this.jwtService.generateToken(any(String.class), any())).thenReturn(token);
       when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), eq(PermissionKeys.LOGON))).thenReturn(true);
       when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new Authentication() {
           @Override
           public Collection<? extends GrantedAuthority> getAuthorities() {
               return List.of();
           }

           @Override
           public @Nullable Object getCredentials() {
               return null;
           }

           @Override
           public @Nullable Object getDetails() {
               return null;
           }

           @Override
           public @Nullable Object getPrincipal() {
               return null;
           }

           @Override
           public boolean isAuthenticated() {
               return false;
           }

           @Override
           public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

           }

           @Override
           public String getName() {
               return "";
           }
       });

        LoginResponse response = this.userService.login(new LoginRequest("email", "password"));

        assertEquals("Match", token, response.getToken());
        assertEquals("Match", user.getId(), response.getUserId());
        assertEquals("Match", user.getEmail(), response.getEmail());
        assertEquals("Match", company.getId(), response.getCompanyId());
    }

    @Test
    void login_throwsWhenNoLoginPerm() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("email");
        String token = UUID.randomUUID().toString();

        Company company = new Company();
        company.setId(UUID.randomUUID());

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(UUID.randomUUID());
        companyMember.setUser(user);
        companyMember.setCompany(company);

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("Some Role Name");

        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID());
        userRole.setMember(companyMember);
        userRole.setRole(role);

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(this.companyMemberRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(companyMember));
        when(this.permissionValidatorService.doesUserHavePerm(any(CompanyMember.class), eq(PermissionKeys.LOGON))).thenReturn(false);
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public @Nullable Object getCredentials() {
                return null;
            }

            @Override
            public @Nullable Object getDetails() {
                return null;
            }

            @Override
            public @Nullable Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "";
            }
        });

        assertThrows(InsufficientPermissionsException.class, () -> {
            this.userService.login(new LoginRequest("email", "password"));
        });
    }

    @Test
    void login_throwsOnNullRequest() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.login(null);
        });
    }

    @Test
    void login_throwsOnNullEmail() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.login(new LoginRequest(null, "password"));
        });
    }

    @Test
    void login_throwsOnNullPassword() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.login(new LoginRequest("email", null));
        });
    }

    @Test
    void login_throwsOnEmptyEmail() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.login(new LoginRequest("", "password"));
        });
    }

    @Test
    void login_throwsOnEmptyPassword() {
        assertThrows(NullPointerException.class, () -> {
            this.userService.login(new LoginRequest("Email", ""));
        });
    }

    @Test
    void login_throwsOnEmailNotFound() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("email");
        String token = UUID.randomUUID().toString();

        Company company = new Company();
        company.setId(UUID.randomUUID());

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(UUID.randomUUID());
        companyMember.setUser(user);
        companyMember.setCompany(company);

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("Some Role Name");

        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID());
        userRole.setMember(companyMember);
        userRole.setRole(role);

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.empty());
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public @Nullable Object getCredentials() {
                return null;
            }

            @Override
            public @Nullable Object getDetails() {
                return null;
            }

            @Override
            public @Nullable Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "";
            }
        });

        assertThrows(UsernameNotFoundException.class, () -> {
            this.userService.login(new LoginRequest("email", "password"));
        });
    }

    @Test
    void login_throwsOnBadCompanyAssociation() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("email");
        String token = UUID.randomUUID().toString();

        Company company = new Company();
        company.setId(UUID.randomUUID());

        CompanyMember companyMember = new CompanyMember();
        companyMember.setId(UUID.randomUUID());
        companyMember.setUser(user);
        companyMember.setCompany(company);

        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setName("Some Role Name");

        UserRole userRole = new UserRole();
        userRole.setId(UUID.randomUUID());
        userRole.setMember(companyMember);
        userRole.setRole(role);

        when(this.userRepository.findByEmail(any(String.class))).thenReturn(Optional.of(user));
        when(this.companyMemberRepository.findByUserId(any(UUID.class))).thenReturn(Optional.empty());
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public @Nullable Object getCredentials() {
                return null;
            }

            @Override
            public @Nullable Object getDetails() {
                return null;
            }

            @Override
            public @Nullable Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return "";
            }
        });

        assertThrows(RuntimeException.class, () -> {
           this.userService.login(new LoginRequest("email", "password"));
        });
    }

    @Test
    void login_ThrowsOnNoAuth() {
        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        assertThrows(Exception.class, () -> {
            this.userService.login(new LoginRequest("email", "password"));
        });
    }
}
