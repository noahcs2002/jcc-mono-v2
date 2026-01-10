package com.software.teamfive.jcc_product_inventory_management.service;

import com.software.teamfive.jcc_product_inventory_management.model.biz.Company;
import com.software.teamfive.jcc_product_inventory_management.model.biz.Role;
import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.LoginRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.request.user.RegistrationRequest;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.role.RoleResponse;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.LoginResponse;
import com.software.teamfive.jcc_product_inventory_management.model.dto.response.user.RegistrationResponse;
import com.software.teamfive.jcc_product_inventory_management.model.join.CompanyMember;
import com.software.teamfive.jcc_product_inventory_management.model.join.UserRole;
import com.software.teamfive.jcc_product_inventory_management.repo.*;
import com.software.teamfive.jcc_product_inventory_management.security.Security;
import com.software.teamfive.jcc_product_inventory_management.utility.exception.user.UserAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class UserService implements UserDetailsService {

    final private UserRepository userRepository;
    final private CompanyRepository companyRepository;
    final private CompanyMemberRepository companyMemberRepository;
    final private RoleRepository roleRepository;
    final private UserRoleRepository userRoleRepository;
    final private JwtService jwtService;
    final private AuthenticationManager authenticationManager;

    @Autowired
    public UserService(UserRepository userRepository, CompanyRepository companyRepository, CompanyMemberRepository companyMemberRepository, RoleRepository roleRepository, UserRoleRepository userRoleRepository, JwtService jwtService, @Lazy AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyMemberRepository = companyMemberRepository;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }


    /**
     * Create a new user.
     * @param request firstName, lastName, email, password, companyName
     * @return New user.
     */
    @Transactional
    public RegistrationResponse register(RegistrationRequest request) {

        User userCheckSum = this.userRepository.findByEmail(request.getEmail()).orElse(null);

        if (userCheckSum != null) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        // Create User
        User user = new User();
        user.setPasswordHash(Security.encode(request.getPasswordPlainText()));
        user.setEmail(request.getEmail());
        user.setLastName(request.getLastName());
        user.setFirstName(request.getFirstName());
        this.userRepository.save(user);

        // Create a company
        Company company = new Company();
        company.setCreatedBy(user);
        company.setName(request.getCompanyName());
        this.companyRepository.save(company);

        // Associate user with Company
        CompanyMember companyMember = new CompanyMember();
        companyMember.setCompany(company);
        companyMember.setUser(user);
        this.companyMemberRepository.save(companyMember);

        // Create the default role
        Role defaultRole = new Role();
        defaultRole.setCompany(company);
        defaultRole.setName("Company Owner");
        defaultRole.setDescription("All Permissions Granted");
        this.roleRepository.save(defaultRole);

        // Associate that user with the role
        UserRole userRole = new UserRole();
        userRole.setRole(defaultRole);
        userRole.setMember(companyMember);
        this.userRoleRepository.save(userRole);

        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "companyId", company.getId(),
                "roles", List.of(defaultRole.getName())
        );
        String token = jwtService.generateToken(user.getEmail(), claims);


        // Return the user details
        return new RegistrationResponse(user.getEmail(), token, user.getId(), company.getId());
    }

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginRequest.getEmail()));

        CompanyMember companyMember = companyMemberRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("User is not associated with a company"));

        List<RoleResponse> roles = userRoleRepository.findByMemberId(companyMember.getId())
                .stream()
                .map(ur -> new RoleResponse(ur.getRole().getId(), ur.getRole().getName()))
                .toList();

        // 4. Generate JWT
        Map<String, Object> claims = Map.of(
                "userId", user.getId(),
                "companyId", companyMember.getCompany().getId(),
                "roles", roles
        );

        String token = jwtService.generateToken(user.getEmail(), claims);

        // 5. Return token + metadata
        LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                companyMember.getCompany().getId(),
                token
        );

        return response;
    }
}
