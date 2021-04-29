# How to use Azure Identity and Access Management to secure your Spring Boot Application with Azure Active Directory B2C Spring Boot Starter

Often times, web applications (or parts of web applications) will need to be protected and will require authentication for access. There are many ways to implement identity management, here we will be showing how to achieve this by using Azure Identity and Access management to protect resources in your Spring Boot Application.  OpenID Connect is an authentication protocol, built on top of OAuth 2.0, that can be used to securely sign users in to web applications. By using the Azure Active Directory B2C (Azure AD B2C) implementation of OpenID Connect, you can **outsource** sign-up, sign-in, and other identity management experiences in your web applications to Azure Active Directory (Azure AD). In this tutorial we will generate a new Spring Boot Application (we will be building a Pet Store Application, called Rhody Pet Store) and configure it to use the Azure Active Directory B2C Spring Boot Starter to receive and process the OAuth 2.0 tokens allowing us to easily construct Spring Authenticated Security Principals that are managed in Azure Active Directory B2C.

Live application can be found here [http://rhodypetstore.azurewebsites.net/](http://rhodypetstore.azurewebsites.net/)

## Objectives

**1. Generate and configure a new Spring Boot Application**

**2. Create an Azure Active Directory B2C Tenant and configure an App Registration**

**3. Configure the Spring Boot Application to properly authenticate sign-up, sign-in and edit users**

**4. Demo**  

## 1. Generate and configure a new Spring Boot Application
Head over to [https://start.spring.io/](https://start.spring.io/) and generate a new Spring Boot Application as seen below. (You may also choose to use STS or other tooling). ***You can also pull the completed project from [https://github.com/chtrembl/petstoreapp](https://github.com/chtrembl/petstoreapp)*** as your starting point. For this tutorial, there isn't a hard dependency on the Spring Boot version or Java Runtime. Any Spring Boot 2+ and Java 8+ will suffice.  We will also pull in Spring Web, Spring Security, Azure Active Directory, Cloud Oauth2 and Thymeleaf.

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/initializeproject.png?raw=true)

You can now build and test the newly generated Rhody Pet Store Application

    mvn clean package
    mvn spring-boot:run
    
That's it for now, we will head to Azure to create and configure the new tenant.

## 2.  Create an Azure Active Directory B2C Tenant and configure an App Registration

 1. Head to Azure Portal https://ms.portal.azure.com and search for Azure Active Directory B2C and select Create. This will allow us to create a new Directory/Tenant which we will use to manage our user's of the Rhody Pet Store Spring Boot Application.  ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap1.png?raw=true)
 
 2. Select the first option Create a new Azure AD B2C Tenant. (In this tutorial, we will want to manage our users in a separate isolated tenant)
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap2.png?raw=true)

 3. Select Create a directory. You can give your tenant an Organization Name and a Domain, that will be used in the user flows that we mentioned above (Sign In, Sign out etc...)   ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap3.png?raw=true)
 
 4. Confirm your settings and select Create, this will take a few minutes to provision ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap4.png?raw=true)
 
 5. In the top rigght, toggle directories and select your newly created Directory/Tenant, ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap5_1.png?raw=true)
  
 6. Once you are in the new directory, search for Azure Active Directory ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap6.png?raw=true)
 
 7. This will show you the details of your newly created Directory/Tenant![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap7.png?raw=true)
  
 8. Select App Registrations ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap8.png?raw=true)
  
 9. Register the Rhody Pet Store Application that we are building. Notice the redirect URI, this is Spring Boot Application that Azure will send request(s) to. You may want to use http://localhost:8080 if you are testing locally. I have the Rhody Pet Store Application running in an Azure Container already, hence the reason for not using localhost.![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap9.png?raw=true)
  
 10. ***After registering, take note of the meta data, you will need this when configuring the Spring Boot Application*** ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap10.png?raw=true)
  
 11. To further configure and view users etc... search for Azure AD B2C (Note, you could of also done the previous registration through this flow as well)![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap11.png?raw=true)
  
 12. Select Applications (Legacy)![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap12.png?raw=true)
 13. You will notice the application "rhodypetstoreapp" from the registration previously created above. Select it.![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap13.png?raw=true)
 14. Select Yes for Web App (We are configuring for an externally facing Spring Boot Application) and Yes for implicit flow to ensure our Spring Boot Application can use Open Id Connect to Sign In.
  ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap14.png?raw=true)
  
 15. Select Keys and Generate Key
***(Keep your newly generated key handy, you will not see it again, and will need it when configuring the Rhody Pet Store Spring Boot Application)***
     ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap15.png?raw=true)
     
 16. Select User Flows, here you will create 3 flows (Sign Up/Sign In, Profile Editing and Password Reset) These are the flows that are being offloaded to Azure Identity Management. ![sdsd](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap16.png?raw=true)
 17. Go through all three flows below, and select the User Claims necessary. This is the meta data associated with each user. I have selected all, for tutorial purposes, and display all claims within the Rhody Pet Store Spring Boot Application. 
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap17.png?raw=true)

 19. Enter a name for each flow and create, here we are using "signupsignin", this will get referenced in the Rhody Pet Store Application ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap18.png?raw=true)
 
 20. Enter a name for each flow and create, here we are using "profileediting", this will get referenced in the Rhody Pet Store Application ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap19.png?raw=true)
 21. Enter a name for each flow and create, here we are using "passwordreset", this will get referenced in the Rhody Pet Store Application   ![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap20.png?raw=true)
 
## 3. Configure the Spring Boot Application to properly authenticate sign-up, sign-in and edit users 

Since we are not managing identity ourselves, we can avoid configuring Spring Security to use Pre Authentication and configuring an Authentication Manager ourselves etc... Instead we can just @EnableSpringSecurity and wire up the and AADB2COidcLoginConfigurer
inside our WebSecurityConfigurerAdapter. By doing so, on successful Sign Ins, Azure will send along grant type and code to our Rhody Pet Store Application and the Spring Security/Filter Chain Flow will grab these values and request/construct tokens for us. With the JWT token, we have access to the claims managed by Azure Active Directory.

In no particular order, you will want to add the following to your newly generated Rhody Pet Store Spring Boot Application. ***You can also pull the completed project from [https://github.com/chtrembl/petstoreapp](https://github.com/chtrembl/petstoreapp)*** 

Create WebSecurityConfiguration.java as seen below. This will wire up the Microsoft AADB2COidcLoginConfigurer as our securtity configurer and resolve our Azure flows for us.  The other unique thing to notice is the configure methods. Our Rhody Pet Store Application will have 1 publicly accessible page (login landing page configured in our HttpSecurity) and all others will require authentication. We also need to permit all access to our static resources (configured in our WebSecurity) as they are used by the publicly accessible login landing page.  The Rhody Pet Store Presentation was built with Bootstrap, hence the need to permit that. CSRF is disabled for this tutorial, however it is not advised to disable CSRF in a real live application (internal or external). 

````java
package com.chtrembl.petstoreapp.security;

import com.microsoft.azure.spring.autoconfigure.b2c.AADB2COidcLoginConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AADB2COidcLoginConfigurer configurer;

    public WebSecurityConfiguration(AADB2COidcLoginConfigurer configurer) {
        this.configurer = configurer;
    }

    
    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers( "/*.css", "/*.js", "/*.png", "/bootstrap-4.5.0-dist/**");
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .apply(configurer)
                .and()
                .oauth2Login()
				.loginPage("/login")
				.and()
				.csrf().disable()
        ;
    }
}


````   

Create WebController.java as seen below. There are 2 GET request mapping handlers, login (public landing) and everything else, that in this case, will just route to the home Thymeleaf view. We also use @ModelAttribute to initializeModel on each Threads incoming GET Request. This will ensure that each Thymeleaf view has User Claims, if they exist.

````java
package com.chtrembl.petstoreapp.controller;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class WebController {

	@ModelAttribute
	public void initializeModel(Model model, OAuth2AuthenticationToken token) {
		if (token != null) {
			final OAuth2User user = token.getPrincipal();

			model.addAttribute("grant_type", user.getAuthorities());
			model.addAllAttributes(user.getAttributes());
		}
	}

	@GetMapping(value = "/login")
	public String login() {
		return "login";
	}

	@GetMapping(value = "/*")
	public String home(Model model, OAuth2AuthenticationToken token) {
		return "home";
	}
}


````

You will also notice login.html that presents a Welcome Guest, and prompts visitors to sign/up sign in, along with any other public accessible content. There is a home.html that is used for all authenticated requests, which presents user claims for this tutorial, among other links to Update Profile, Reset Password and Logout. Since we know about authenticated users, we can display a friendly message.

      Welcome <span th:text="${name}">

Lastly, you will need to update your application.properties or application.yml. I have left mine out of the repository as part of my .gitignore because it contains sensitive information to my tenant, but this is the place where your configurations lives (ideally we would externalize this in a real application such as Spring Cloud). You can use the snippet below filling in the blanks. Use tenant. client-id from the screenshots above. client-secret will be the generated key that you saved earlier. The url's will be localhost for local development or fully qualified for Cloud deployments.

````yaml
#logging:
#  level:
#    org.springframework.security: DEBUG
azure:
  activedirectory:
    b2c:
      tenant: realctremblayb2c
      oidc-enabled: true
      client-id: 
      client-secret: 
      reply-url: http://localhost:8080/home
      logout-success-url: http://localhost:8080/home
      user-flows:
        sign-up-or-sign-in: B2C_1_signupsignin
        profile-edit: B2C_1_profileediting
        password-reset: B2C_1_passwordreset

````

## 4. Demo
````
mvn clean package
mvn spring-boot:run
````

visit http://localhost:8080 (or if your deploying to cloud go there)

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/app1.png?raw=true)

Sign in flow ***(Note you can customize this in Azure to use your own .css etc...)***
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/app2.png?raw=true)

Fill out and submit...

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/app3.png?raw=true)

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/app4.png?raw=true)

Sign Out...
![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/app5.png?raw=true)

Head back to Azure Portal > Azure AD B2C and view/administer user's that have been created via the Rhody Pet Store Application

![enter image description here](https://github.com/chtrembl/staticcontent/blob/master/petstoreapp/ap22.png?raw=true)
