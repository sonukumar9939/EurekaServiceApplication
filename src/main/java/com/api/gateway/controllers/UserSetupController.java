package com.api.gateway.controllers;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.gateway.Exception.FunctionalException;
import com.api.gateway.Response.SuccessRestResponse;
import com.api.gateway.models.User;
import com.api.gateway.service.UserSetupService;

@RestController
@RequestMapping(value = "/user")
public class UserSetupController {

	private static Logger logger = LoggerFactory.getLogger(UserSetupController.class);

	@Autowired
	private UserSetupService userSetupService;

	@RequestMapping(value = "getUser/{id}", method = RequestMethod.GET)
	public ResponseEntity<SuccessRestResponse> getUser(@PathVariable("id") int id) throws FunctionalException {
		SuccessRestResponse response = new SuccessRestResponse();
		logger.info("Get User Details Process Started");
		User user = userSetupService.findById(id);
		if (user == null) {
			response.setSuccess(false);
			return new ResponseEntity<SuccessRestResponse>(response, HttpStatus.NOT_FOUND);

		} else {
			response.setSuccess(true);
			response.setTimestamp(System.currentTimeMillis());
			response.setData(user);
			return new ResponseEntity<SuccessRestResponse>(response, HttpStatus.OK);
		}

	}
	@RequestMapping(method = RequestMethod.GET,value="getUser", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SuccessRestResponse> getUserByUserName(@RequestParam String userName) throws FunctionalException{
		SuccessRestResponse response = new SuccessRestResponse();
		List<User> users = userSetupService.findUsersByName(userName);
		if(CollectionUtils.isEmpty(users)) {
			response.setSuccess(false);
			response.setTimestamp(System.currentTimeMillis());
			return new ResponseEntity<SuccessRestResponse>(response,HttpStatus.NOT_FOUND);
		}else{
			response.setSuccess(true);
			response.setTimestamp(System.currentTimeMillis());
			response.setData(users);
			return new ResponseEntity<SuccessRestResponse>(response,HttpStatus.OK);
		}
		
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<SuccessRestResponse> saveUser(@Valid @RequestBody User user) throws FunctionalException {

		logger.info("Save User Process Started");
		user.getUserDetails().setUser(user);
		userSetupService.saveUserDetails(user);
		SuccessRestResponse response = new SuccessRestResponse();
		response.setSuccess(true);
		response.setTimestamp(System.currentTimeMillis());
		return new ResponseEntity<SuccessRestResponse>(response, HttpStatus.OK);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/update/{id}")
	public ResponseEntity<SuccessRestResponse> updateUserById(@PathVariable("id") int id, @Valid @RequestBody User user)
			throws FunctionalException {
		logger.info("Update Process Initiated");
		SuccessRestResponse response = new SuccessRestResponse();
		User userDetails = userSetupService.findById(id);
		if (userDetails == null) {
			logger.info("No such user exists with id :" + " " + id);
			response.setSuccess(false);
			response.setTimestamp(System.currentTimeMillis());
			return new ResponseEntity<SuccessRestResponse>(response, HttpStatus.NOT_FOUND);

		} else {
			userSetupService.saveUserDetails(userSetupService.saveEntity(userDetails,user));
			response.setSuccess(true);
			response.setTimestamp(System.currentTimeMillis());
			return new ResponseEntity<SuccessRestResponse>(response, HttpStatus.OK);
		}

	}	

}
