package fullhouse.controller;

import fullhouse.model.Player;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;


@Api(value = "Player", description="Services for accessing players")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@RestController
public class PlayerController {

	private static final Logger LOG = LoggerFactory.getLogger(PlayerController.class);

	@ApiOperation(value="init", notes="Returns a player for the given ID")
	@RequestMapping(value={"/player/{id}"}, method = RequestMethod.GET)
	public Player getPlayer(	@PathVariable @ApiParam(value="The ID for the player you wish to return", required=true) int id,
								HttpServletRequest request,
								@ApiParam(value="This is an internal parameter and should be left blank", required=false) Model model) throws Exception {
		Player p = new Player();
		return p;
	}
}
