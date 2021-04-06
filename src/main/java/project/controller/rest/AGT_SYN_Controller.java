package project.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwic.code.ErrorCode;
import com.kwic.exception.DefinedException;
import com.kwic.web.controller.Controllers;

import egovframework.rte.fdl.property.EgovPropertyService;
import project.controller.rest.layout.LayoutFilter;
import project.service.AGT_SYN_Service;
import project.service.CMN_CMN_Service;

/**
 * Agent정보 동기화 Controller
 * 
 * @date 2020.07.24
 * @author 장수호
 *
 */
@RestController
public class AGT_SYN_Controller extends Controllers{
	@Resource(name="propertiesService")
	private EgovPropertyService properties;
	
	@Resource(name = "CMN_CMN_Service")
	private CMN_CMN_Service cmnService;
	
	@Resource(name = "AGT_SYN_Service")
	private AGT_SYN_Service service;
	
	/**
	 * Agent정보 동기화
	 * 
	 * 1. Agent정보
	 * 2. 인증서 정보
	 * 
	 * - 요청
	 * {
		 "timestamp" : "20200722092253",
		 "agent" : {
		  "version" : "1.0.1",
		  "os" : "windows 10",
		  "privateIP" : "192.168.0.1",
		  "publicIP" : "112.324.125.155"
		 }
		 "certs" : [{
		   "userId" : "userId1",
		   "certId" : "certid1",
		   "agentId" : "agentId",
		   "certnm" : "cn=장수호(JANG SU HO)008104420120816181000433,ou=HNB,ou=personal4IB,o=yessign,c=kr",
		   "expire" : "2021-07-21 15:40:22"
		  }]
		}
	 * 
	 * - 응답
	 * {
		 "resultCode" : "000",
		 "resultMsg" : ""
		}
	 * 
	 * @param requestContent
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@PutMapping(value="/rest/sync/{id}", produces="application/json; charset=UTF-8")
	public void agt_syn_u1000j(@RequestBody String requestContent, @Context HttpServletRequest request, @PathVariable String id, HttpServletResponse response) throws Exception {
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
			LayoutFilter.getInstance().validate("agt-syn-u1000j", LayoutFilter.DIRECTION_REQUEST, requestMap);
			
			requestMap.put("agentId", id);
			
			//2. DB수정
			List<Map<String,Object>> delCertList	= service.AGT_SYN_U1000J(requestMap);
			
			//3. 삭제 할 인증서 목록 셋팅
			if (delCertList != null && delCertList.size() > 0) {
				responseMap.put("certs", delCertList);
			}
			
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
			//로그 저장
			try {cmnService.log(getLogData(responseMap, requestContent, request));}catch (Exception e){logger.error(e);}
		}
		response(response, responseMap);
	}
}
