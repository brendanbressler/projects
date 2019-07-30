package com.techelevator.npgeek;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.SessionAttributes;

import com.techelevator.npgeek.model.User;
import com.techelevator.npgeek.authentication.AuthProvider;
import com.techelevator.npgeek.dao.ParkDao;
import com.techelevator.npgeek.dao.UserDao;
import com.techelevator.npgeek.dao.WeatherDao;
import com.techelevator.npgeek.model.Weather;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.techelevator.npgeek.dao.ParkDao;
import com.techelevator.npgeek.dao.WeatherDao;
import com.techelevator.npgeek.model.survey.Survey;
import com.techelevator.npgeek.model.survey.SurveyDao;
import com.techelevator.npgeek.model.survey.SurveyResults;


@Controller
@SessionAttributes({"temperature", "parkCode"})
public class HomeController {
	
 @Autowired
 private ParkDao parkDao;
 
 @Autowired
 private WeatherDao weatherDao;
 
 @Autowired
 private SurveyDao surveyDao;
 
 
 
 @Autowired 
 private AuthProvider auth;
 
 private String fahrenheit = "fahrenheit";
 private String celcius = "celcius";


@RequestMapping(path= {"/", "/login"}, method=RequestMethod.GET)
	public String getLoginPage(ModelMap modelMap) {
	modelMap.addAttribute("parks",parkDao.getAllParks());
	return "login";
}

@RequestMapping(path="/login", method=RequestMethod.POST)
public String login(
    @RequestParam String username,
    @RequestParam String password,
    RedirectAttributes flash
) {
    if(auth.signIn(username, password)) {
        return "redirect:/homePage";
    } else {
        flash.addFlashAttribute("message", "Login Invalid");
        return "redirect:/login";
    }
}

@RequestMapping(path="/registration", method=RequestMethod.GET)
public String register(ModelMap modelHolder) {
    if( ! modelHolder.containsAttribute("user")){
        modelHolder.put("user", new User());
    }
    return "registration";
}

@RequestMapping(path="/registration", method=RequestMethod.POST)
public String register(@Valid @ModelAttribute("user") User user, BindingResult result, RedirectAttributes flash) {
    
	
	try {
	if(result.hasErrors()) {
        flash.addFlashAttribute("user", user);
        flash.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "user", result);
        flash.addFlashAttribute("message", "Please fix the following errors:");
         return "redirect:/registration";
    }
    auth.register(user.getUsername(), user.getPassword());
    return "redirect:/homePage";
	}
	catch(DuplicateKeyException e) {
		return "redirect:/registraion";
	}
}


@RequestMapping(path="/logoff", method=RequestMethod.POST)
	public String logOff() {
    auth.logOff();
    return "redirect:/";
}


@RequestMapping(path="/homePage", method=RequestMethod.GET)
public String getHomePage(ModelMap modelMap) {
modelMap.addAttribute("parks",parkDao.getAllParks());
return "homePage";
}



@RequestMapping(path="/parkDetail", method=RequestMethod.GET)
	public String getParkDetailPage(@RequestParam String parkCode, ModelMap modelMap, @RequestParam(value="temperature", required=false) String temperature) {
	modelMap.addAttribute("park", parkDao.getParkByParkCode(parkCode));
	
	List<Weather> advisoryList = new ArrayList<Weather>();
	advisoryList = weatherDao.getTheWeather(parkCode);
	modelMap.addAttribute("advisoryList",advisoryList);
	
	if(temperature != null) {
		modelMap.addAttribute("temperature", temperature);
	}
	modelMap.addAttribute("parkCode", parkCode);
	String userchoice = (String)modelMap.get("temperature");
	
	this.setTemperatureByUserChoice(userchoice, parkCode, modelMap);
	
	return "parkDetail";
	}
	

	public void setTemperatureByUserChoice (String userchoice, String parkCode, ModelMap modelMap) {
	
		List<Weather> weatherList = new ArrayList<Weather>();
		weatherList = weatherDao.getTheWeather(parkCode);
	
	//check session attribute to see if the user has selected celcius or fahrenheit
		if  (userchoice == null) {
		modelMap.addAttribute("temperature", fahrenheit);
		modelMap.addAttribute("weatherList", weatherList);
		modelMap.addAttribute("temp", "°F");
		} else if (userchoice.equals(celcius)) {
		for (Weather weather : weatherList) {
			
			int fahrenheit = weather.getHigh();
			int fahrenheitlow = weather.getLow();
			weather.setHigh(convertToCelcius(fahrenheit));
			weather.setLow(convertToCelcius(fahrenheitlow));
		}
		modelMap.addAttribute("weatherList", weatherList);
		modelMap.addAttribute("temp", "°C");
		} else {
		modelMap.addAttribute("weatherList", weatherList);
		modelMap.addAttribute("temp", "°F");
		}		
		
	
}
	public int convertToCelcius(int fahrenheit) {
		int celcius = (fahrenheit - 32) * 5 / 9;
		return celcius;
	}




@RequestMapping(path="/SurveyInput", method=RequestMethod.GET)
public String showSurvey(ModelMap modelMap) {
	
	if (modelMap.containsAttribute("SurveyInput") == false) {
		Survey empty = new Survey();
		modelMap.put("SurveyInput", empty);
	}
	
	return "SurveyInput";
}

@RequestMapping(path= {"/SurveyInput"}, method=RequestMethod.POST)
public String getSurvey(@Valid @ModelAttribute Survey survey, BindingResult result, RedirectAttributes redirectAttributes) {


if (result.hasErrors()) {
	
	for (ObjectError error : result.getAllErrors()) {
		System.out.println(error.getDefaultMessage());
	}
				
	redirectAttributes.addFlashAttribute("SurveyInput", survey);
	redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "SurveyInput", result);
	return "redirect:/SurveyInput";
}
	surveyDao.saveSurvey(survey);
	return "redirect:/favoriteParks";
}


@RequestMapping(path="/favoriteParks", method=RequestMethod.GET)
public String displaySurveyOutput(ModelMap map) {
	List <SurveyResults> surveyResult = new ArrayList<SurveyResults>();
	surveyResult = surveyDao.getAllSurveys();
	
	map.addAttribute("surveyResultList", surveyResult);
	return "/favoriteParks";
}

}
