package com.codingdojo.loginReg.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codingdojo.loginReg.models.User;
import com.codingdojo.loginReg.services.UserService;
import com.codingdojo.loginReg.validation.UserValidator;

@Controller
public class LoginRegController {
	
	private final UserService userService;
	private final UserValidator userValidator;
	public LoginRegController(UserService userService,UserValidator userValidator) {
		this.userService = userService;
		this.userValidator=userValidator;
	}
	
	
	
	
	@RequestMapping("/")
	public String homepage(@ModelAttribute("user")User user) {
		
		return "index.jsp";
	}
	
	@RequestMapping( value = "/registration", method=RequestMethod.POST)
	public String registration(@Valid @ModelAttribute("user")User user,BindingResult result,HttpSession session) {
		
		System.out.println("*******************");
		System.out.println(user.getEmail());
		System.out.println("*******************");

//		System.out.println(this.userService.findByEmail(user.getEmail()));
		userValidator.validate(user,result);
		if(result.hasErrors()) {
			return "index.jsp";
		}
		//TODO later after login reg works prevent dupe emails
		
		// create a user with this information
		
		User userObj = this.userService.registerUser(user);
		//get the user that just got created and store their id in session
		session.setAttribute("userid", userObj.getId());
		return "redirect:/dashboard";
		
	}
	
	@RequestMapping("/dashboard")
	public String dashboard(Model model,HttpSession session) {
		//retrieve the userobject from the db whos id matches the id stored in session
		Long id = (Long)session.getAttribute("userid");
		User loggedinuser = this.userService.findUserById(id);
		
		model.addAttribute("loggedinuser",loggedinuser);
		return "dashboard.jsp";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpSession session) {
		 //clear the session
		session.invalidate();
		return "redirect:/";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
		public String login(@RequestParam("email")String email,@RequestParam("password")String password,HttpSession session,RedirectAttributes redirectAttributes) {
			Boolean isConfirmed = this.userService.authenticateUser(email, password);
			
			if(isConfirmed) {
				//if the email password combo is correct,log them in using session and redirect them to the dashboard
				
				//get the user with that email
				User user= this.userService.findByEmail(email);
				
				//put that user id in session
				session.setAttribute("userid", user.getId());
				return "redirect:/dashboard";
			}
			//if login attempt was unsuccessful, flash an error message
			redirectAttributes.addFlashAttribute("error","Invalid login");
			return "redirect:/";			
		}

	

}
