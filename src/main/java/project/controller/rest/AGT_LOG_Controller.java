package project.controller.rest;

import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.web.controller.Controllers;

import project.controller.rest.layout.LayoutFilter;
import project.service.CMN_CMN_Service;

@RestController
public class AGT_LOG_Controller extends Controllers{
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@SuppressWarnings("unchecked")
	@PostMapping(value="/rest/log", produces="application/json; charset=UTF-8")
	public void rest_log(@RequestBody String requestContent, @Context HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedWriter writer	= null;
		Map<String,Object> requestMap	= null;
		Map<String,Object> responseMap	= new HashMap<String,Object>();
		try {
			//인증
			validateToken(request);
			try {
				requestMap	= new ObjectMapper().readValue(requestContent, HashMap.class);
			} catch (Exception e) {
				throw new DefinedException("요청 데이터 Parsing 중 오류가 발생 하였습니다. 요청 데이터를 확인 해 주시기 바랍니다.");
			}
			//1.validate
			LayoutFilter.getInstance().validate("agt-log-i1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			//2.file write
			String json	= new ObjectMapper().writeValueAsString(requestMap);
			
			logger.filebeatDebug(json);
			
			responseMap.put(RT_CD, 	ErrorCode._000);
			responseMap.put(RT_MSG, "");
		} catch (Exception e) {
			logger.error(e);
			if (e instanceof DefinedException) {
				responseMap.put(RT_CD, ((DefinedException) e).getStatusCode() == null ? ErrorCode._999 : ((DefinedException) e).getStatusCode());
				responseMap.put(RT_MSG, e.getMessage());
			} else {
				responseMap.put(RT_CD,	ErrorCode._999);
				responseMap.put(RT_MSG,	"오류가 발생하였습니다.");
			}
		} finally {
			if(writer!=null) {writer.close();}
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
